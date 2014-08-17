__author__ = 'Josef'

# A module used in testing which is an exact copy of
# main.py, except that it's methods aren't tied to a
# URL using Flask and its responses aren't jsonified
# so as to make testing easier

import database as db


def index():
    return "Index Page"


def init_database():
    db.init()
    return "Database Initialized"


# Registers a new user
def register_user(user_name, password, e_mail, country, city):
    if db.email_exists(e_mail):
        return {"response": "E-Mail Already Registered"}
    elif db.user_name_exists(user_name):
        return {"response": "User Name Taken"}
    else:
        db.add_new_user(user_name, password, e_mail, country, city)
        return {"response": "Success"}


# Checks if the user exists and the password
# is correct and returns the response "Success"
# if that is the case
def login_user(identifier, password):
    if not db.user_exists(identifier):
        return {"response": "User doesn't exist"}
    elif not db.correct_password(identifier, password):
        return {"response": "Incorrect Password"}
    else:
        return {"response": "Success"}


# Takes a string of the idea and a user
# name and adds the idea to the ideas table
def post_idea(idea_text, poster, tags):
    c = "&nbht"
    n = tags.count(c)
    tag_list = tags.split(c, n)
    tag_list.remove('')
    db.add_new_idea(idea_text, poster, tag_list)
    return {"response": "Success"}


# Takes a user name or an e-mail and returns
# a JSON response containing the information
# about the most recent idea posted by that
# user and the users which that user is following
def get_idea_feed(user_name):
    return {"response": "Success", "ideas": get_ideas(user_name)}


def get_ideas(user_name):
    user_list = [user_name]
    user_list += db.get_following(user_name)
    idea_list = []
    for user in user_list:
        idea_list.append(db.get_most_recent_idea(user))
    return idea_list


def get_message_feed(user_name):
    return {"response": "Success", "messages": db.get_message_feed(user_name)}


def get_recent_messages(user_name, original_user_name):
    recent_messages = db.get_recent_messages(user_name, original_user_name)
    return {"response": "Success", "messages": recent_messages}


def send_message(sender, receiver, message_text):
    db.add_message(sender, receiver, message_text)
    return {"response": "Success"}


# Takes a user name or e-mail and returns
# all data concerning that user if it exists
def get_user_data(identifier):
    user_data = db.get_user_data(identifier)
    if user_data != "Invalid":
        user_name = user_data[0]
        e_mail = user_data[2]
        country = user_data[3]
        city = user_data[4]
        followers = user_data[5]
        return {"response": "Success",
                "user_name": user_name,
                "e_mail": e_mail,
                "country": country,
                "city": city,
                "followers": followers}
    else:
        return {"response": "Failure"}


def get_user_name(e_mail):
    user_name = db.get_user_name(e_mail)
    if user_name != "Invalid":
        return {"response": "Success", "user_name": user_name}
    else:
        return {"response": "Failure"}


def get_e_mail(user_name):
    e_mail = db.get_email(user_name)
    if e_mail != "Invalid":
        return {"response": "Success", "e_mail": e_mail}
    else:
        return {"response": "Failure"}


def get_other_user_recent_ideas(identifier):
    return {"response": "Success", "ideas": db.get_recent_ideas(identifier)}


def add_follower(user, follower):
    db.add_follower(user, follower)
    return {"response": "Success"}


def remove_follower(user, follower):
    db.remove_follower(user, follower)
    return {"response": "Success"}


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
    return {"response": "Success", "ideas": idea_list}


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
    return {"response": "Success", "people": people_list}


def user_is_following(user, other_user):
    is_following = db.user_is_following(user, other_user)
    return {"response": "Success", "is_following": is_following}


def update_user_data(original_user_name, original_e_mail, new_user_name, new_e_mail,
                     new_password, new_country, new_city):
    response = "Success"
    if original_user_name != new_user_name and db.user_name_exists(new_user_name):
        response = "User Name Exists"
    elif original_e_mail != new_e_mail and db.email_exists(new_e_mail):
        response = "E-Mail Exists"
    else:
        db.update_user_data(original_user_name, new_user_name, new_e_mail, new_password, new_country, new_city)
    return {"response": response}


def add_approving(user_name, idea_id):
    db.add_approving(user_name, idea_id)
    return {"response": "Success"}


def remove_approving(user_name, idea_id):
    db.remove_approving(user_name, idea_id)
    return {"response": "Success"}


def user_is_approving(user_name, idea_id):
    return {"response": "Success", "approving": db.user_is_approving(user_name, idea_id)}


def get_following(user_name):
    return {"response": "Success", "following": db.get_following_data(user_name)}


def get_approving(user_name):
    return {"response": "Success", "approving": db.get_approving_data(user_name)}


def add_comment(user, idea_id, comment_text):
    db.add_comment(user, idea_id, comment_text)
    return {"response": "Success"}


def get_comments(idea_id):
    comments = db.get_comments(idea_id)
    return {"response": "Success", "comments": comments}
