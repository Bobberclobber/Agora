__author__ = 'Josef'

from flask import g

import sqlite3
import marshal


def connect_db():
    conn = sqlite3.connect("database.db")
    return conn


def get_db():
    db = getattr(g, 'db', None)
    if db is None:
        db = g.db = connect_db()
    return db


def init():
    c = get_db()
    c.execute("DROP TABLE IF EXISTS users")
    c.execute("DROP TABLE IF EXISTS ideas")
    c.execute("DROP TABLE IF EXISTS messages")

    c.execute("CREATE TABLE users "
              "(user_name TEXT, "
              "password TEXT, "
              "e_mail TEXT, "
              "country TEXT, "
              "city TEXT, "
              "followers INTEGER, "
              "following BLOB, "
              "approving BLOB)")

    c.execute("CREATE TABLE ideas "
              "(idea_id INTEGER PRIMARY KEY AUTOINCREMENT, "
              "idea_text TEXT, "
              "poster TEXT, "
              "approval_num INTEGER, "
              "tags BLOB)")

    c.execute("CREATE TABLE messages "
              "(sender TEXT, "
              "receiver TEXT,"
              "message_text TEXT)")

    c.execute("CREATE TABLE comments"
              "(user TEXT,"
              "idea_id INTEGER,"
              "comment_text TEXT)")
    c.commit()


def add_new_user(user_name, password, e_mail, country, city):
    c = get_db()
    c.execute("INSERT INTO users (user_name, password, e_mail, country, city, followers, following, approving) "
              "VALUES (?,?,?,?,?,?,?,?)",
              (user_name, password, e_mail, country, city, 0, marshal.dumps([]), marshal.dumps([])))
    c.commit()


def email_exists(e_mail):
    c = get_db()
    cur = c.execute("SELECT e_mail FROM users")
    for row in cur:
        if e_mail in row:
            return True
    return False


def user_name_exists(user_name):
    c = get_db()
    cur = c.execute("SELECT user_name FROM users")
    for row in cur:
        if user_name in row:
            return True
    return False


def user_exists(identifier):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail FROM users")
    for row in cur:
        if identifier in row:
            return True
    return False


def correct_password(identifier, password):
    c = get_db()
    cur = c.execute("SELECT user_name, password, e_mail FROM users")
    for row in cur:
        if (identifier == row[0] or identifier == row[2]) and password == row[1]:
            return True
    return False


def add_new_idea(idea_text, poster, tags):
    c = get_db()
    c.execute("INSERT INTO ideas (idea_text, poster, approval_num, tags) VALUES (?,?,?,?)",
              (idea_text, poster, 0, marshal.dumps(tags)))
    c.commit()


def get_email(user_name):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail FROM users")
    for row in cur:
        if user_name in row:
            return row[1]
    return "Invalid"


def get_user_name(e_mail):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail FROM users")
    for row in cur:
        if e_mail in row:
            return row[0]
    return "Invalid"


# Returns the most recently posted
# idea by the given user as a list
def get_most_recent_idea(poster):
    c = get_db()
    cur = c.execute("SELECT * FROM ideas ORDER BY idea_id DESC")
    for row in cur:
        if poster in row:
            temp = [row[0], row[1], row[2], row[3], marshal.loads(row[4])]
            return temp
    return []


def get_recent_messages(user_name, original_user_name):
    c = get_db()
    cur = c.execute("SELECT * FROM messages")
    messages = []
    for row in cur:
        print(row)
        scen_1 = user_name == row[0] and original_user_name == row[1]
        scen_2 = user_name == row[1] and original_user_name == row[0]
        if scen_1 or scen_2:
            temp = [row[0], row[1], row[2]]
            messages.append(temp)
    return messages


def add_message(sender, receiver, message_text):
    c = get_db()
    c.execute("INSERT INTO messages (sender, receiver, message_text) VALUES (?,?,?)", (sender, receiver, message_text))
    c.commit()


# Returns the 10 most recent ideas by
# the given user as a list of lists
def get_recent_ideas(poster):
    c = get_db()
    cur = c.execute("SELECT * FROM ideas ORDER BY idea_id DESC")
    ideas = []
    count = 0
    for row in cur:
        if poster in row:
            temp = [row[0], row[1], row[2], row[3], marshal.loads(row[4])]
            ideas.append(temp)
            count += 1
            if count == 10:
                return ideas
    return ideas


# Returns a list of all the users
# the given user is following
def get_following(user_name):
    c = get_db()
    cur = c.execute("SELECT user_name, following FROM users")
    for row in cur:
        if user_name == row[0]:
            return marshal.loads(row[1])
    return []


def get_user_data(identifier):
    c = get_db()
    cur = c.execute("SELECT * FROM users")
    user_data = []
    for row in cur:
        if identifier == row[0] or identifier == row[2]:
            for item in row:
                user_data += [item]
            user_data[6] = marshal.loads(user_data[6])
            return user_data
    return "Invalid"


def add_follower(user, follower):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail, followers, following FROM users")
    follower_num = 0
    following = []
    for row in cur:
        if user in row:
            follower_num = row[2]
        elif follower in row:
            following = marshal.loads(row[3])
    following.append(user)
    following = marshal.dumps(following)
    follower_num += 1
    c.execute("UPDATE users SET followers=? WHERE user_name=? OR e_mail=?", (follower_num, user, user))
    c.execute("UPDATE users SET following=? WHERE user_name=? OR e_mail=?", (following, follower, follower))
    c.commit()


def remove_follower(user, follower):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail, followers, following FROM users")
    follower_num = 0
    following = []
    for row in cur:
        if user in row:
            follower_num = row[2]
        elif follower in row:
            following = marshal.loads(row[3])
    if user in following:
        following.remove(user)
        following = marshal.dumps(following)
        follower_num -= 1
        c.execute("UPDATE users SET followers=? WHERE user_name=? OR e_mail=?", (follower_num, user, user))
        c.execute("UPDATE users SET following=? WHERE user_name=? OR e_mail=?", (following, follower, follower))
        c.commit()


def get_ideas(tag):
    c = get_db()
    cur = c.execute("SELECT * FROM ideas")
    idea_list = []
    for row in cur:
        idea_tags = marshal.loads(row[4])
        if tag in idea_tags:
            temp = [row[0], row[1], row[2], row[3], idea_tags]
            idea_list.append(temp)
    return idea_list


def get_people(identifier):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail, country, city, followers FROM users")
    people_list = []
    for row in cur:
        if identifier in row:
            temp = [row[0], row[1], row[2], row[3], row[4]]
            people_list.append(temp)
    return people_list


def user_is_following(user, other_user):
    c = get_db()
    cur = c.execute("SELECT user_name, following FROM users")
    for row in cur:
        if user in row:
            following = marshal.loads(row[1])
            if other_user in following:
                return True
    return False


def update_user_data(original_user_name, new_user_name, new_e_mail, new_password, new_country, new_city):

    c = get_db()
    c.execute("UPDATE users SET user_name=? WHERE user_name=?", (new_user_name, original_user_name))
    c.execute("UPDATE users SET e_mail=? WHERE user_name=?", (new_e_mail, original_user_name))
    c.execute("UPDATE users SET password=? WHERE user_name=?", (new_password, original_user_name))
    c.execute("UPDATE users SET country=? WHERE user_name=?", (new_country, original_user_name))
    c.execute("UPDATE users SET city=? WHERE user_name=?", (new_city, original_user_name))

    # Changes the user name in all required
    # places if it has been changed
    if new_user_name != original_user_name:
        # Changes the user name in all following lists
        cur = c.execute("SELECT user_name, following FROM users")
        for row in cur:
            following = marshal.loads(row[1])
            if original_user_name in following:
                other_user = row[0]
                following.remove(original_user_name)
                following.append(new_user_name)
                c.execute("UPDATE users SET following=? WHERE user_name=?", (marshal.dumps(following), other_user))

        # Changes the user name in all idea posts
        c.execute("UPDATE ideas SET poster=? WHERE poster=?", (new_user_name, original_user_name))

        # Changes the user name in all messages
        c.execute("UPDATE messages SET sender=? WHERE sender=?", (new_user_name, original_user_name))
        c.execute("UPDATE messages SET receiver=? WHERE receiver=?", (new_user_name, original_user_name))
    c.commit()


def add_approving(user_name, idea_id):
    c = get_db()
    cur = c.execute("SELECT user_name, approving FROM users")

    # Adds the idea id to the user's approving list
    for row in cur:
        if user_name == row[0]:
            approving = marshal.loads(row[1])
            if not idea_id in approving:
                approving.append(idea_id)
                approving = marshal.dumps(approving)
                c.execute("UPDATE users SET approving=? WHERE user_name=?", (approving, user_name))
            break

    # Increases the idea's approval num
    cur = c.execute("SELECT idea_id, approval_num FROM ideas")
    for row in cur:
        if idea_id == str(row[0]):
            approval_num = row[1]
            approval_num += 1
            c.execute("UPDATE ideas SET approval_num=? WHERE idea_id=?", (approval_num, idea_id))
            break
    c.commit()


def remove_approving(user_name, idea_id):
    c = get_db()
    cur = c.execute("SELECT user_name, approving FROM users")

    # Removes the idea id from the user's approving list
    for row in cur:
        if user_name == row[0]:
            approving = marshal.loads(row[1])
            if idea_id in approving:
                approving.remove(idea_id)
                approving = marshal.dumps(approving)
                c.execute("UPDATE users SET approving=? WHERE user_name=?", (approving, user_name))
            break

    # Decreases the idea's approval num
    cur = c.execute("SELECT idea_id, approval_num FROM ideas")
    for row in cur:
        if idea_id == str(row[0]):
            approval_num = row[1]
            approval_num -= 1
            c.execute("UPDATE ideas SET approval_num=? WHERE idea_id=?", (approval_num, idea_id))
            break
    c.commit()


def user_is_approving(user_name, idea_id):
    c = get_db()
    cur = c.execute("SELECT user_name, approving FROM users")
    for row in cur:
        if user_name == row[0]:
            approving = marshal.loads(row[1])
            if idea_id in approving:
                return True
    return False


def get_following_data(user_name):
    c = get_db()
    cur = c.execute("SELECT user_name, following FROM users")

    # Gets the names of all users the given user is following
    following_names = []
    for row in cur:
        if user_name == row[0]:
            following_names = marshal.loads(row[1])
            break

    # Builds a list of all the user data of
    # users the given user is following
    following = []
    for user in following_names:
        cur = c.execute("SELECT user_name, e_mail, country, city, followers FROM users")
        for row in cur:
            if user == row[0]:
                following.append([row[0], row[1], row[2], row[3], row[4]])
    return following


def get_approving_data(user_name):
    c = get_db()
    cur = c.execute("SELECT user_name, approving FROM users")

    # Gets the ids of all the ideas the given user is approving
    approving_ids = []
    for row in cur:
        if user_name == row[0]:
            approving_ids = marshal.loads(row[1])
            break

    # Builds a list of all the idea data of
    # the idea the given user is approving
    approving = []
    for idea in approving_ids:
        cur = c.execute("SELECT idea_id, idea_text, poster, approval_num, tags FROM ideas")
        for row in cur:
            if idea == str(row[0]):
                approving.append([row[0], row[1], row[2], row[3], marshal.loads(row[4])])
    return approving


def add_comment(user, idea_id, comment_text):
    c = get_db()
    c.execute("INSERT INTO comments (user, idea_id, comment_text) VALUES (?,?,?)", (user, int(idea_id), comment_text))
    c.commit()


def get_comments(idea_id):
    c = get_db()
    cur = c.execute("SELECT * FROM comments")
    comments = []
    for row in cur:
        if idea_id == str(row[1]):
            temp = [row[0], row[2]]
            comments.append(temp)
    return comments