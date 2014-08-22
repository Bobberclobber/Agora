import unittest
import database as db
import main_test as main
from main import app

# This test module uses the main_test module in order
# to test that the responses sent by its methods are correct

TEST_USERS = [["TestUser1", "password1", "email.1@email.com", "Test-Land", "Test-City"],
              ["John", "guest123", "john.peterson@hotmail.com", "England", "London"],
              ["THE MIGHTY MONARCH", "monarch", "the.mighty@monar.ch", "The Grand Canyon", "The Cocoon"],
              ["Sterling Archer", "GUEST", "chet.manly@isismail.com", "United States", "New York"],
              ["Bender", "Bender Is Great", "bender@benderisgreat.com", "United States", "New New York"],
              ["Fry", "password", "philipfry@hotmail.com", "United States", "New New York"],
              ["TestUser2", "password2", "email.2@email.com", "Not-Test-Land", "Test-City"]]

TEST_IDEAS = [["This is an idea. Idea number one.", "TestUser1", "&nbhtidea&nbhttest"],
              ["Wall of text. Wall of text. Wall of text.", "John", "&nbhtidea"],
              ["An incredibly long idea posted by a user.", "THE MIGHTY MONARCH", "&nbhttest&nbhtlong_idea"]]

TEST_MESSAGES = [["John", "Sterling Archer", "Message from John to Sterling Archer"],
                 ["THE MIGHTY MONARCH", "Bender", "Hey man, this is THE MIGHTY MONARCH..."],
                 ["Bender", "Fry", "Oh wait you're serious, let me laugh even harder."],
                 ["John", "Bender", "Another message"],
                 ["Sterling Archer", "John", "Hey! Phrasing!"]]

TEST_COMMENTS = [["John", 1, "This is a comment on idea nr 1"],
                 ["Bender", 1, "This is another comment on idea nr 1"],
                 ["This is a comment on idea nr 2"]]

# This shows the data of the ideas above when added, in order, to the database
STORED_TEST_IDEAS = [[1, "This is an idea. Idea number one.", "TestUser1", 0, ["idea", "test"]],
                     [2, "Wall of text. Wall of text. Wall of text.", "John", 0, ["idea"]],
                     [3, "An incredibly long idea posted by a user.", "THE MIGHTY MONARCH", 0, ["test", "long_idea"]]]


# Tests that user registration works properly
class UserRegistrationTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            # Sets up the database
            db.init()

    # Tests if the correct response is received when
    # registering a new user
    def test_user_registration(self):
        with app.test_request_context():
            response = register_user(TEST_USERS[0])

            def user_registered():
                return response["response"] == "Success"

            self.failIf(not user_registered())

    # Tests if the correct response is received when registering
    # a user with an already existing user name
    def test_existing_user_name(self):
        with app.test_request_context():
            register_user(TEST_USERS[0])
            existing_user_name = TEST_USERS[0][0]
            response = main.register_user(existing_user_name, "foo", "bar", "foo", "bar")

            def user_name_exists():
                return response["response"] == "User Name Taken"

            self.failIf(not user_name_exists())

    # Tests if the correct response is received when registering
    # a user with an already existing e-mail address
    def test_existing_e_mail(self):
        with app.test_request_context():
            register_user(TEST_USERS[0])
            existing_e_mail = TEST_USERS[0][2]
            response = main.register_user("foo", "bar", existing_e_mail, "foo", "bar")

            def e_mail_exists():
                return response["response"] == "E-Mail Already Registered"

            self.failIf(not e_mail_exists())

    # Tests if an existing user can log in with either e-mail or user name
    def test_login_user(self):
        with app.test_request_context():
            user = TEST_USERS[0]
            register_user(user)
            user_name_response = main.login_user(user[0], user[1])
            e_mail_response = main.login_user(user[2], user[1])

            def login_succeeded():
                scen_1 = user_name_response["response"] == "Success"
                scen_2 = e_mail_response["response"] == "Success"
                return scen_1 and scen_2

            self.failIf(not login_succeeded())

    # Test if trying to perform a login with a non-existent
    # user name or e-mail returns the correct response
    def test_login_nonexistent_user(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
            register_user(user1)
            user_name_response = main.login_user(user2[0], user2[1])
            e_mail_response = main.login_user(user2[2], user2[1])

            def login_failed():
                scen_1 = user_name_response["response"] == "User doesn't exist"
                scen_2 = e_mail_response["response"] == "User doesn't exist"
                return scen_1 and scen_2

            self.failIf(not login_failed())

    # Test if trying to log in with an incorrect
    # password gets the correct response
    def test_login_incorrect_password(self):
        with app.test_request_context():
            user = TEST_USERS[0]
            register_user(user)
            response = main.login_user(user[0], "foo")

            def login_failed():
                return response["response"] == "Incorrect Password"

            self.failIf(not login_failed())

    # Tests if the correct user data is
    # fetched when using either e-mail
    # or username as identifiers
    def test_get_user_data(self):
        with app.test_request_context():
            user = TEST_USERS[0]
            register_user(user)
            user_data1 = main.get_user_data(user[0])
            user_data2 = main.get_user_data(user[2])

            def correct_data():
                corr_user_name = user_data1["user_name"] == user_data2["user_name"] == user[0]
                corr_e_mail = user_data1["e_mail"] == user_data2["e_mail"] == user[2]
                corr_country = user_data1["country"] == user_data2["country"] == user[3]
                corr_city = user_data1["city"] == user_data2["city"] == user[4]
                corr_followers = user_data1["followers"] == user_data2["followers"] == 0
                corr_location = user_data1["location"] == user_data2["location"] == "Not set"
                return corr_user_name and corr_e_mail and corr_country and corr_city and corr_followers and corr_location

            self.failIf(not correct_data())

    def tearDown(self):
        with app.test_request_context():
            # Destroys and closes the database
            db.destroy()
            db.close()


# Tests if ideas can be posted and retrieved correctly
class IdeaPostingAndFetchingTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            db.init()

    # Tests if the idea feed is returned correctly
    # This test assumes that the function add_follower
    # in main functions correctly since fetching
    # ideas of users which the original user is
    # following is included in the test
    def test_idea_feed_fetch(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
            register_user(user1)
            register_user(user2)
            post_ideas()
            # Make user1 follow user2
            main.add_follower(user2[0], user1[0])
            response = main.get_idea_feed(user1[0])

            def correct_idea_feed():
                return response["ideas"] == [STORED_TEST_IDEAS[0], STORED_TEST_IDEAS[1]]

            self.failIf(not correct_idea_feed())

    # Makes the same assumptions as the test before this
    # Tests that the function get_other_user_recent_ideas
    # returns only the ideas posted by that user
    def test_other_user_idea_fetch(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
            register_user(user1)
            register_user(user2)
            post_ideas()
            # Make user1 follow user2
            main.add_follower(user2[0], user1[0])
            # Fetches the ideas posted by both users
            user1_response = main.get_other_user_recent_ideas(user1[0])
            user2_response = main.get_other_user_recent_ideas(user2[0])

            def correct_ideas_fetched():
                scen_1 = user1_response["ideas"] == [STORED_TEST_IDEAS[0]]
                scen_2 = user2_response["ideas"] == [STORED_TEST_IDEAS[1]]
                return scen_1 and scen_2

            self.failIf(not correct_ideas_fetched())

    def tearDown(self):
        with app.test_request_context():
            # Destroys and closes the database
            db.destroy()
            db.close()


# Tests if messages can be sent and fetched correctly
class MessageSendingAndFetchingTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            # Initializes the database
            db.init()
            register_all_users()
            send_messages()

    # Tests if the correct message feed is
    # returned from the get_message_feed function
    def test_get_message_feed(self):
        with app.test_request_context():
            # Gets the the most recent message
            # sent by any user to the given user
            # ordered by most recent message sent
            message_feed = main.get_message_feed("Bender")

            def correct_message_feed():
                return message_feed["messages"] == [TEST_MESSAGES[3], TEST_MESSAGES[1]]

        self.failIf(not correct_message_feed())

    # Tests if the correct messages are returned
    # from the get_conversation function
    def test_get_conversation(self):
        with app.test_request_context():
            # Gets the messages sent between
            # the two given people, ordered by
            # the most recent message sent
            conversation1 = main.get_conversation("John", "Sterling Archer")
            conversation2 = main.get_conversation("Sterling Archer", "John")

            def correct_conversation():
                cond_1 = conversation1["messages"] == [TEST_MESSAGES[0], TEST_MESSAGES[4]]
                cond_2 = conversation1["messages"] == conversation2["messages"]
                return cond_1 and cond_2

            self.failIf(not correct_conversation())


# Tests the two search functions
class SearchFunctionsTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            # Initializes the database and
            # adds all users and ideas
            db.init()
            register_all_users()
            post_ideas()

    def test_search_ideas(self):
        with app.test_request_context():
            # Searches for ideas with the tag idea
            search1 = main.search_ideas("&nbhtidea")
            # Searches for ideas with the tag long_idea
            search2 = main.search_ideas("&nbhtlong_idea")
            # Searches for ideas with the tags idea or test
            search3 = main.search_ideas("&nbhtidea&nbhttest")
            # Searches for ideas with the tags idea, test or long_idea
            search4 = main.search_ideas("&nbhtidea&nbhtlong_idea&nbhttest")
            # Searches for a non-existent tag
            search5 = main.search_ideas("&nbhtfoo")

            def correct_search_results():
                scen_1 = search1["ideas"] == [STORED_TEST_IDEAS[0], STORED_TEST_IDEAS[1]]
                scen_2 = search2["ideas"] == [STORED_TEST_IDEAS[2]]
                scen_3 = search3["ideas"] == STORED_TEST_IDEAS
                scen_4 = search4["ideas"] == STORED_TEST_IDEAS
                scen_5 = search5["ideas"] == []
                return scen_1 and scen_2 and scen_3 and scen_4 and scen_5

            self.failIf(not correct_search_results())

    def test_search_people(self):
        with app.test_request_context():
            # Search on user name
            search1 = main.search_people("John")
            # Search on e-mail
            search2 = main.search_people("the.mighty@monar.ch")
            # Search on country
            search3 = main.search_people("United States")
            # Search on city
            search4 = main.search_people("Test-City")
            # Search on two identifiers
            search5 = main.search_people("TestUser1&nbhtUnited States")
            # Search on three identifiers
            search6 = main.search_people("Sterling Archer&nbhtphilipfry@hotmail.com&nbhtNew New York")

            def correct_search_results():
                user1 = TEST_USERS[0]
                user2 = TEST_USERS[1]
                user3 = TEST_USERS[2]
                user4 = TEST_USERS[3]
                user5 = TEST_USERS[4]
                user6 = TEST_USERS[5]
                user7 = TEST_USERS[6]
                scen_1 = search1["people"] == [[user2[0], user2[2], user2[3], user2[4], 0]]
                scen_2 = search2["people"] == [[user3[0], user3[2], user3[3], user3[4], 0]]
                scen_3 = search3["people"] == [[user4[0], user4[2], user4[3], user4[4], 0],
                                               [user5[0], user5[2], user5[3], user5[4], 0],
                                               [user6[0], user6[2], user6[3], user6[4], 0]]
                scen_4 = search4["people"] == [[user1[0], user1[2], user1[3], user1[4], 0],
                                               [user7[0], user7[2], user7[3], user7[4], 0]]
                scen_5 = search5["people"] == [[user1[0], user1[2], user1[3], user1[4], 0],
                                               [user4[0], user4[2], user4[3], user4[4], 0],
                                               [user5[0], user5[2], user5[3], user5[4], 0],
                                               [user6[0], user6[2], user6[3], user6[4], 0]]
                scen_6 = search6["people"] == [[user4[0], user4[2], user4[3], user4[4], 0],
                                               [user6[0], user6[2], user6[3], user6[4], 0],
                                               [user5[0], user5[2], user5[3], user5[4], 0]]
                return scen_1 and scen_2 and scen_3 and scen_4 and scen_5 and scen_6

            self.failIf(not correct_search_results())

    def tearDown(self):
        with app.test_request_context():
            # Destroys and closes the database
            db.destroy()
            db.close()


class FollowersTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            db.init()
            register_user(TEST_USERS[0])
            register_user(TEST_USERS[1])
            register_user(TEST_USERS[2])

    # Test if adding follower works correctly
    def test_add_follower(self):
        with app.test_request_context():
            user1 = TEST_USERS[0][0]
            user2 = TEST_USERS[1][0]
            main.add_follower(user1, user2)
            user_data = main.get_user_data(user1)

            def follower_added():
                return user_data["followers"] == 1

        self.failIf(not follower_added())

    # Test if removing follower works correctly
    def test_remove_follower(self):
        with app.test_request_context():
            user1 = TEST_USERS[0][0]
            user2 = TEST_USERS[1][0]
            main.add_follower(user1, user2)
            main.remove_follower(user1, user2)
            user_data = main.get_user_data(user1)

            def follower_removed():
                return user_data["followers"] == 0

            self.failIf(not follower_removed())

    # Test if checking if a user is following another works correctly
    def test_is_following(self):
        with app.test_request_context():
            user1 = TEST_USERS[0][0]
            user2 = TEST_USERS[1][0]
            main.add_follower(user1, user2)
            is_following_resp = main.user_is_following(user2, user1)

            def user_is_following():
                return is_following_resp["is_following"]

            self.failIf(not user_is_following())

    # Test if getting the users which the given
    # user is following works correctly
    def test_get_following(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
            user3 = TEST_USERS[2]
            user_name1 = user1[0]
            user_name2 = user2[0]
            user_name3 = user3[0]
            main.add_follower(user_name1, user_name3)
            main.add_follower(user_name2, user_name3)
            following_resp = main.get_following(user_name3)
            print(following_resp)

            def correct_following():
                # Creates lists representing the data
                # of users retrieved from the database
                # when fetching a user as a follower
                stored_user1 = user1
                stored_user2 = user2
                # Removes the password from the list
                stored_user1.remove(user1[1])
                stored_user2.remove(user2[1])
                # Adds the number of followers, which in
                # this case is 1 since user3 is following
                # both user1 and user2
                stored_user1 += [1]
                stored_user2 += [1]
                return following_resp["following"] == [stored_user1, stored_user2]

            self.failIf(not correct_following())

    def tearDown(self):
        with app.test_request_context():
            # Destroys and closes the database
            db.destroy()
            db.close()


class CommentsAndApprovingTest(unittest.TestCase):
    def setUp(self):
        db.init()
        post_ideas()


# Registers a user and returns the response
def register_user(user):
    response = main.register_user(user[0], user[1], user[2], user[3], user[4])
    return response


# Registers all test users
def register_all_users():
    for user in TEST_USERS:
        register_user(user)


# Posts all test ideas
def post_ideas():
    for idea in TEST_IDEAS:
        main.post_idea(idea[0], idea[1], idea[2])


# Sends all test messages
def send_messages():
    for message in TEST_MESSAGES:
        main.send_message(message[0], message[1], message[2])