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
              "following BLOB)")
    c.execute("CREATE TABLE ideas "
              "(idea_id INTEGER PRIMARY KEY AUTOINCREMENT, idea_text TEXT, poster TEXT, approval_num INTEGER)")
    c.execute("CREATE TABLE messages (message_text TEXT, sender TEXT, receiver TEXT)")
    c.commit()


def add_new_user(user_name, password, e_mail, country, city):
    c = get_db()
    c.execute("INSERT INTO users (user_name, password, e_mail, country, city, followers, following) "
              "VALUES (?,?,?,?,?,?,?)",
              (user_name, password, e_mail, country, city, 0, marshal.dumps([])))
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
    cur = c.execute("SELECT * FROM users")
    for row in cur:
        if identifier in row and password == row[1]:
            return True
    return False


def add_new_idea(idea_text, poster):
    c = get_db()
    c.execute("INSERT INTO ideas (idea_text, poster, approval_num) VALUES (?,?,?)", (idea_text, poster, 0))
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


def get_most_recent_idea(poster):
    c = get_db()
    cur = c.execute("SELECT * FROM ideas ORDER BY idea_id DESC")
    for row in cur:
        if poster in row:
            return row
    return "Invalid"


def get_recent_ideas(poster):
    c = get_db()
    cur = c.execute("SELECT * FROM ideas ORDER BY idea_id DESC")
    ideas = []
    for row in cur:
        if poster in row:
            temp = [row[0], row[1], row[2], row[3]]
            ideas.append(temp)
    return ideas


def get_following(identifier):
    c = get_db()
    cur = c.execute("SELECT user_name, e_mail, following FROM users")
    for row in cur:
        if identifier in row:
            return marshal.loads(row[2])
    return "Invalid"


def get_user_data(identifier):
    c = get_db()
    cur = c.execute("SELECT * FROM users")
    user_data = []
    for row in cur:
        if identifier in row:
            for item in row:
                user_data += [item]
            user_data[6] = marshal.loads(user_data[6])
            return user_data
    return "Invalid"