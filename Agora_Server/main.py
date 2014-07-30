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


@app.route('/_register_user_/<user_name>/<password>/<e_mail>/<country>/<city>')
def register_user(user_name, password, e_mail, country, city):
    if db.email_exists(e_mail):
        return jsonify({"response": ["E-Mail Already Registered"]})
    elif db.user_name_exists(user_name):
        return jsonify({"response": ["User Name Taken"]})
    else:
        db.add_new_user(user_name, password, e_mail, country, city)
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
@app.route('/_post_idea_/<idea_text>/<poster>')
def post_idea(idea_text, poster):
    db.add_new_idea(idea_text, poster)
    return jsonify({"response": ["Success"]})


# Takes a user name or an e-mail and returns
# a JSON response containing the information
# about the most recent idea posted by that
# user and the users which that user is following
@app.route('/_get_recent_activities_/<identifier>')
def get_recent_activities(identifier):
    if '@' in identifier:
        user_name = db.get_user_name(identifier)
        if user_name == "Invalid":
            return jsonify({"response": ["Failure"]})
        else:
            return jsonify(get_most_recent_ideas(user_name))
    else:
        return jsonify(get_most_recent_ideas(identifier))


# Takes a user name and returns a dict where
# 'response' is 'Success' and 'ideas' links
# to a list containing the latest idea made
# by the given user and the user which the
# given user is following
def get_most_recent_ideas(user_name):
    users_to_check = [user_name, "Test_User1"]
    users_to_check += db.get_following(user_name)
    recent_ideas = {"response": ["Success"], "ideas": []}
    for user in users_to_check:
        recent_ideas["ideas"] += get_most_recent_idea(user)
    return recent_ideas


# Takes a user name and creates a dict linking
# the user name to the information concerning
# that user's latest idea
def get_most_recent_idea(user_name):
    recent_idea = db.get_most_recent_idea(user_name)
    if recent_idea != "Invalid":
        return [recent_idea[0], recent_idea[1], recent_idea[2], recent_idea[3]]
    else:
        return []


# Takes a user name or e-mail and returns
# all data concerning that user if it exists
@app.route('/_get_user_data_/<identifier>')
def get_user_data(identifier):
    user_data = db.get_user_data(identifier)
    if user_data != "Invalid":
        user_name = user_data[0]
        e_mail = user_data[2]
        country = user_data[3]
        city = user_data[4]
        followers = user_data[5]
        return jsonify({"response": ["Success"],
                        "user_name": [user_name],
                        "e_mail": [e_mail],
                        "country": [country],
                        "city": [city],
                        "followers": [followers]})
    else:
        return jsonify({"response": ["Failure"]})


@app.route('/_get_following_/<identifier>')
def get_following(identifier):
    return jsonify({"response": "Success", "following": db.get_following(identifier)})


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


@app.route('/_get_other_user_recent_ideas_/<identifier>')
def get_other_user_recent_ideas(identifier):
    return jsonify({"response": ["Success"], "ideas": db.get_recent_ideas(identifier)})


if __name__ == "__main__":
    app.run(host='127.0.0.1')