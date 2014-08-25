import unittest
import database as db
import main_test as main
from main import app

# This test module uses the main_test module in order
# to test that the responses sent by its methods are correct

TEST_USERS = [["TestUser1", "password1", "email.1@email.com", "Test-Land", "Test-City", "image"],
              ["John", "guest123", "john.peterson@hotmail.com", "England", "London", "image"],
              ["THE MIGHTY MONARCH", "monarch", "the.mighty@monar.ch", "The Grand Canyon", "The Cocoon", "image"],
              ["Sterling Archer", "GUEST", "chet.manly@isismail.com", "United States", "New York", "image"],
              ["Bender", "Bender Is Great", "bender@benderisgreat.com", "United States", "New New York", "image"],
              ["Fry", "password", "philipfry@hotmail.com", "United States", "New New York", "image"],
              ["TestUser2", "password2", "email.2@email.com", "Not-Test-Land", "Test-City", "image"]]

TEST_IDEAS = [["This is an idea. Idea number one.", "TestUser1", "&nbhtidea&nbhttest"],
              ["Wall of text. Wall of text. Wall of text.", "John", "&nbhtidea"],
              ["An incredibly long idea posted by a user.", "THE MIGHTY MONARCH", "&nbhttest&nbhtlong_idea"]]

TEST_MESSAGES = [["John", "Sterling Archer", "Message from John to Sterling Archer"],
                 ["THE MIGHTY MONARCH", "Bender", "Hey man, this is THE MIGHTY MONARCH..."],
                 ["Bender", "Fry", "Oh wait you're serious, let me laugh even harder."],
                 ["John", "Bender", "Another message"],
                 ["Sterling Archer", "John", "Hey! Phrasing!"],
                 ["TestUser1", "TestUser2", "Test Message"]]

TEST_COMMENTS = [["John", 1, "This is a comment on idea nr 1"],
                 ["Bender", 1, "This is another comment on idea nr 1"],
                 ["TestUser1", 2, "This is a comment on idea nr 2"]]

# This shows the data of the ideas above when added, in order, to the database
STORED_TEST_IDEAS = [[1, "This is an idea. Idea number one.", "TestUser1", 0, ["idea", "test"]],
                     [2, "Wall of text. Wall of text. Wall of text.", "John", 0, ["idea"]],
                     [3, "An incredibly long idea posted by a user.", "THE MIGHTY MONARCH", 0, ["test", "long_idea"]]]

# This shows the data of the comments which is returned
RETURNED_TEST_COMMENTS = [["John", "This is a comment on idea nr 1"],
                          ["Bender", "This is another comment on idea nr 1"],
                          ["TestUser1", "This is a comment on idea nr 2"]]


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
            response = main.register_user(existing_user_name, "foo", "bar", "foo", "bar", "img")

            def user_name_exists():
                return response["response"] == "User Name Taken"

            self.failIf(not user_name_exists())

    # Tests if the correct response is received when registering
    # a user with an already existing e-mail address
    def test_existing_e_mail(self):
        with app.test_request_context():
            register_user(TEST_USERS[0])
            existing_e_mail = TEST_USERS[0][2]
            response = main.register_user("foo", "bar", existing_e_mail, "foo", "bar", "img")

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

    # Test if the correct image is returned
    # from the get_avatar_image function
    def test_get_avatar_image(self):
        with app.test_request_context():
            user = TEST_USERS[0]
            register_user(user)
            response = main.get_avatar_image(user[0])

            def correct_img_string():
                return response["avatar_image"] == "image"

            self.failIf(not correct_img_string())

    def tearDown(self):
        with app.test_request_context():
            # Destroys and closes the database
            db.destroy()
            db.close()


# Tests that updating user data works correctly
class UpdateUserDataTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            # Initializes the database
            # and adds all users
            db.init()
            register_all_users()
            post_ideas()
            send_messages()
            post_comments()

    # Tests if updating with an existing
    # user name returns the correct response
    def test_existing_user_name_update(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
            response = main.update_user_data(user1[0], user1[2],  # Original user name and e-mail
                                             user2[0], user1[2],  # New user name and email
                                             user1[1], user1[3], user1[4], "Not set", user1[5])

            def correct_response():
                return response["response"] == "User Name Exists"

            self.failIf(not correct_response())

    # Tests if updating with an existing
    # e-mail returns the correct response
    def test_existing_e_mail_update(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
            response = main.update_user_data(user1[0], user1[2],  # Original user name and e-mail
                                             user1[0], user2[2],  # New user name and email
                                             user1[1], user1[3], user1[4], "Not set", user1[5])

            def correct_response():
                return response["response"] == "E-Mail Exists"

            self.failIf(not correct_response())

    # Tests if updating with viable data works correctly
    def test_update_user_data(self):
        with app.test_request_context():
            user = TEST_USERS[0]
            response = main.update_user_data(user[0], user[2],  # Original user name and e-mail
                                             "new_user_name", "new_e_mail",
                                             "new_password", "new_country", "new_city", "new_location", "new_image")

            def correct_response():
                # Checks that the data has been updated in the users table
                user_data1 = main.get_user_data("new_user_name")
                user_data2 = main.get_user_data("new_e_mail")
                scen_1 = user_data1["user_name"] == user_data2["user_name"] == "new_user_name"
                scen_2 = user_data1["e_mail"] == user_data2["e_mail"] == "new_e_mail"
                scen_3 = user_data1["country"] == user_data2["country"] == "new_country"
                scen_4 = user_data1["city"] == user_data2["city"] == "new_city"
                scen_5 = user_data1["location"] == user_data2["location"] == "new_location"
                scen_6 = user_data1["avatar_image"] == user_data2["avatar_image"] == "new_image"
                scen_7 = response["response"] == "Success"
                users_updated = scen_1 and scen_2 and scen_3 and scen_4 and scen_5 and scen_6 and scen_7

                # Checks that the data has been updated in the ideas table
                # Since an idea by the user who's user name has been updated
                # had posted an idea, the function get_most_recent_idea returns
                # a non-empty list when using the new user name as parameter
                ideas_updated = db.get_most_recent_idea("new_user_name") != []

                # Checks that the data has been updated in the messages table
                # Since the original user has sent a message, the return value
                # from get conversation using the new user name and the other
                # user's name has to return a non-empty list
                messages_updated = db.get_conversation("new_user_name", "TestUser2") != []

                # Checks that the data has been updated in the comments table
                # The user who's name was changed posted a comment on the idea with id 2
                comments_updated = False
                for comment in db.get_comments(2):
                    if "new_user_name" in comment:
                        comments_updated = True

                return users_updated and ideas_updated and messages_updated and comments_updated

            self.failIf(not correct_response())

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
            register_all_users()

    # Tests if the idea feed is returned correctly
    # This test assumes that the function add_follower
    # in main functions correctly since fetching
    # ideas of users which the original user is
    # following is included in the test
    def test_idea_feed_fetch(self):
        with app.test_request_context():
            user1 = TEST_USERS[0]
            user2 = TEST_USERS[1]
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
            register_all_users()

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

            def correct_following():
                # Creates lists representing the data
                # of users retrieved from the database
                # when fetching a user as a follower
                stored_user1 = user1.copy()
                stored_user2 = user2.copy()
                # Removes the password from the list
                stored_user1.remove(user1[1])
                stored_user2.remove(user2[1])
                # Adds the number of followers, which in
                # this case is 1 since user3 is following
                # both user1 and user2
                stored_user1 += [1]
                stored_user1.remove("image")
                stored_user2 += [1]
                stored_user2.remove("image")
                return following_resp["following"] == [stored_user1, stored_user2]

            self.failIf(not correct_following())

    def tearDown(self):
        with app.test_request_context():
            # Destroys and closes the database
            db.destroy()
            db.close()


class CommentsAndApprovingTest(unittest.TestCase):
    def setUp(self):
        with app.test_request_context():
            # Initializes the database and
            # adds all ideas and users
            db.init()
            register_all_users()
            post_ideas()

    # Tests that approving an idea works correctly
    def test_add_approving(self):
        with app.test_request_context():
            user1 = TEST_USERS[0][0]
            user2 = TEST_USERS[1][0]
            main.add_approving(user1, 1)
            main.add_approving(user1, 2)
            main.add_approving(user2, 1)

            def approving_added_correctly():
                scen_1 = main.user_is_approving(user1, 1)["approving"]
                scen_2 = main.user_is_approving(user1, 2)["approving"]
                scen_3 = main.user_is_approving(user2, 1)["approving"]
                scen_4 = main.get_approving(user1)["approving"] == [STORED_TEST_IDEAS[0], STORED_TEST_IDEAS[1]]
                scen_5 = main.get_approving(user2)["approving"] == [STORED_TEST_IDEAS[0]]
                return scen_1 and scen_2 and scen_3 and scen_4 and scen_5

            self.failIf(not approving_added_correctly())

    # Tests that un-approving an idea works correctly
    def test_remove_approving(self):
        with app.test_request_context():
            user1 = TEST_USERS[0][0]
            user2 = TEST_USERS[1][0]
            main.add_approving(user1, 1)
            main.add_approving(user1, 2)
            main.add_approving(user2, 1)
            main.remove_approving(user1, 1)
            main.remove_approving(user2, 1)

            def approving_removed_correctly():
                scen_1 = not main.user_is_approving(user1, 1)["approving"]
                scen_2 = not main.user_is_approving(user2, 1)["approving"]
                scen_3 = main.get_approving(user1)["approving"] == [STORED_TEST_IDEAS[1]]
                scen_4 = main.get_approving(user2)["approving"] == []
                return scen_1 and scen_2 and scen_3 and scen_4

            self.failIf(not approving_removed_correctly())

    # Tests that commenting on an idea works correctly
    def test_add_comment(self):
        with app.test_request_context():
            post_comment(TEST_COMMENTS[0])
            post_comment(TEST_COMMENTS[1])
            post_comment(TEST_COMMENTS[2])

            def comments_posted_correctly():
                scen_1 = main.get_comments(1)["comments"] == [RETURNED_TEST_COMMENTS[0], RETURNED_TEST_COMMENTS[1]]
                scen_2 = main.get_comments(2)["comments"] == [RETURNED_TEST_COMMENTS[2]]
                return scen_1 and scen_2

            self.failIf(not comments_posted_correctly())


# Registers a user and returns the response
def register_user(user):
    if len(user) == 5:
        print(user)
    response = main.register_user(user[0], user[1], user[2], user[3], user[4], user[5])
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


# Posts a comment
def post_comment(comment):
    main.add_comment(comment[0], comment[1], comment[2])


# Posts all comments
def post_comments():
    for comment in TEST_COMMENTS:
        post_comment(comment)