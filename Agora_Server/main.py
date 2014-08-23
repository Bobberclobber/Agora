__author__ = 'Josef'

from flask import Flask, jsonify

import database as db

app = Flask(__name__)
app.debug = True


@app.route('/')
def index():
    return "Index Page"


@app.route('/_init_database_')
def init_database():
    db.init()
    return "Database Initialized"


@app.route('/_register_user_/<user_name>/<password>/<e_mail>/<country>/<city>/<avatar_image>')
def register_user(user_name, password, e_mail, country, city, avatar_image):
    if db.email_exists(e_mail):
        return jsonify({"response": ["E-Mail Already Registered"]})
    elif db.user_name_exists(user_name):
        return jsonify({"response": ["User Name Taken"]})
    else:
        db.add_new_user(user_name, password, e_mail, country, city, avatar_image)
        return jsonify({"response": ["Success"]})


# Checks if the user exists and the password
# is correct and returns the response "Success"
# if that is the case
@app.route('/_login_user_/<identifier>/<password>')
def login_user(identifier, password):
    if not db.user_exists(identifier):
        return jsonify({"response": ["User doesn't exist"]})
    elif not db.correct_password(identifier, password):
        return jsonify({"response": ["Incorrect Password"]})
    else:
        return jsonify({"response": ["Success"]})


# Takes a string of the idea and a user
# name and adds the idea to the ideas table
@app.route('/_post_idea_/<idea_text>/<poster>/<tags>')
def post_idea(idea_text, poster, tags):
    c = "&nbht"
    n = tags.count(c)
    tag_list = tags.split(c, n)
    tag_list.remove('')
    db.add_new_idea(idea_text, poster, tag_list)
    return jsonify({"response": ["Success"]})


# Takes a user name or an e-mail and returns
# a JSON response containing the information
# about the most recent idea posted by that
# user and the users which that user is following
@app.route('/_get_idea_feed_/<user_name>')
def get_idea_feed(user_name):
    return jsonify({"response": ["Success"], "ideas": get_ideas(user_name)})


def get_ideas(user_name):
    user_list = [user_name]
    user_list += db.get_following(user_name)
    idea_list = []
    for user in user_list:
        idea_list.append(db.get_most_recent_idea(user))
    return idea_list


@app.route('/_get_message_feed_/<user_name>')
def get_message_feed(user_name):
    return jsonify({"response": ["Success"], "messages": db.get_message_feed(user_name)})


@app.route('/_get_conversation_/<user_name>/<original_user_name>')
def get_conversation(user_name, original_user_name):
    recent_messages = db.get_conversation(user_name, original_user_name)
    return jsonify({"response": ["Success"], "messages": recent_messages})


@app.route('/_send_message_/<sender>/<receiver>/<message_text>')
def send_message(sender, receiver, message_text):
    db.add_message(sender, receiver, message_text)
    return jsonify({"response": ["Success"]})


# Takes a user name or e-mail and returns
# all data concerning that user if it exists
@app.route('/_get_user_data_/<identifier>')
def get_user_data(identifier):
    user_data = db.get_user_data(identifier)
    if user_data != "Invalid":
        user_name = user_data[0]
        e_mail = user_data[1]
        country = user_data[2]
        city = user_data[3]
        followers = user_data[4]
        location = user_data[5]
        avatar_image = user_data[6]
        return jsonify({"response": ["Success"],
                        "user_name": [user_name],
                        "e_mail": [e_mail],
                        "country": [country],
                        "city": [city],
                        "followers": [followers],
                        "location": [location],
                        "avatar_image": [avatar_image]})
    else:
        return jsonify({"response": ["Failure"]})


@app.route('/_get_user_name_/<e_mail>')
def get_user_name(e_mail):
    user_name = db.get_user_name(e_mail)
    if user_name != "Invalid":
        return jsonify({"response": ["Success"], "user_name": [user_name]})
    else:
        return jsonify({"response": ["Failure"]})


@app.route('/_get_e_mail_/<user_name>')
def get_e_mail(user_name):
    e_mail = db.get_email(user_name)
    if e_mail != "Invalid":
        return jsonify({"response": ["Success"], "e_mail": [e_mail]})
    else:
        return jsonify({"response": ["Failure"]})


@app.route('/_get_avatar_image_/<user_name>')
def get_avatar_image(user_name):
    return jsonify({"response": ["Success"], "avatar_image": [db.get_avatar_image(user_name)]})


@app.route('/_get_other_user_recent_ideas_/<identifier>')
def get_other_user_recent_ideas(identifier):
    return jsonify({"response": ["Success"], "ideas": db.get_recent_ideas(identifier)})


@app.route('/_add_follower_/<user>/<follower>')
def add_follower(user, follower):
    db.add_follower(user, follower)
    return jsonify({"response": ["success"]})


@app.route('/_remove_follower_/<user>/<follower>')
def remove_follower(user, follower):
    db.remove_follower(user, follower)
    return jsonify({"response": ["success"]})


@app.route('/_search_ideas_/<tags>')
def search_ideas(tags):
    c = "&nbht"
    n = tags.count(c)
    tag_list = tags.split(c, n)
    if '' in tag_list:
        tag_list.remove('')
    idea_list = []
    for tag in tag_list:
        temp = db.get_ideas(tag)
        for item in temp:
            if not item in idea_list:
                idea_list.append(item)
    return jsonify({"response": ["Success"], "ideas": idea_list})


@app.route('/_search_people_/<identifiers>')
def search_people(identifiers):
    c = "&nbht"
    n = identifiers.count(c)
    identifier_list = identifiers.split(c, n)
    if '' in identifier_list:
        identifier_list.remove('')
    people_list = []
    for identifier in identifier_list:
        temp = db.get_people(identifier)
        for item in temp:
            if not item in people_list:
                people_list.append(item)
    return jsonify({"response": ["Success"], "people": people_list})


@app.route('/_user_is_following_/<user>/<other_user>')
def user_is_following(user, other_user):
    is_following = db.user_is_following(user, other_user)
    return jsonify({"response": ["Success"], "is_following": [is_following]})


@app.route('/_update_user_data_/<original_user_name>/<original_e_mail>/<new_user_name>/<new_e_mail>/'
           '<new_password>/<new_country>/<new_city>/<new_location>/<new_image>')
def update_user_data(original_user_name, original_e_mail, new_user_name, new_e_mail,
                     new_password, new_country, new_city, new_location, new_image):
    response = "Success"
    if original_user_name != new_user_name and db.user_name_exists(new_user_name):
        response = "User Name Exists"
    elif original_e_mail != new_e_mail and db.email_exists(new_e_mail):
        response = "E-Mail Exists"
    else:
        db.update_user_data(original_user_name, new_user_name, new_e_mail, new_password, new_country, new_city,
                            new_location, new_image)
    return jsonify({"response": [response]})


@app.route('/_add_approving_/<user_name>/<idea_id>')
def add_approving(user_name, idea_id):
    db.add_approving(user_name, idea_id)
    return jsonify({"response": ["Success"]})


@app.route('/_remove_approving_/<user_name>/<idea_id>')
def remove_approving(user_name, idea_id):
    db.remove_approving(user_name, idea_id)
    return jsonify({"response": ["Success"]})


@app.route('/_user_is_approving_/<user_name>/<idea_id>')
def user_is_approving(user_name, idea_id):
    return jsonify({"response": ["Success"], "approving": [db.user_is_approving(user_name, idea_id)]})


@app.route('/_get_following_/<user_name>')
def get_following(user_name):
    return jsonify({"response": ["Success"], "following": db.get_following_data(user_name)})


@app.route('/_get_approving_/<user_name>')
def get_approving(user_name):
    return jsonify({"response": ["Success"], "approving": db.get_approving_data(user_name)})


@app.route('/_add_comment_/<user>/<idea_id>/<comment_text>')
def add_comment(user, idea_id, comment_text):
    db.add_comment(user, idea_id, comment_text)
    return jsonify({"response": ["Success"]})


@app.route('/_get_comments_/<idea_id>')
def get_comments(idea_id):
    comments = db.get_comments(idea_id)
    return jsonify({"response": ["Success"], "comments": comments})


@app.route('/_test_/<blob>')
def test(blob):
    print(blob)
    return jsonify({"response": ["Success"]})


if __name__ == "__main__":
    app.run(host='127.0.0.1')