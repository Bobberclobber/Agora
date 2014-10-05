package se.liu.ida.josfa969.tddd80.help_classes;

import android.os.NetworkOnMainThreadException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.item_records.CommentRecord;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.item_records.MessageRecord;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;

/**
 * Created by Josef on 18/07/14.
 * <p/>
 * A class containing methods used to get JSON-responses from URLs
 */
public class JsonMethods {
    // private static String BASE_URL = "http://agoraserver-josfa969.rhcloud.com/";
    // Base url for emulator
    private static String BASE_URL = "http://10.0.3.2:5000/";
    // Base url for real device
    // private static String BASE_URL = "http://localhost:5000/";
    private static final String SPACE = " ";
    private static final String SPACE_REPLACE = "&nbsp";
    private static final String HASH_TAG = "#";
    private static final String HASH_TAG_REPLACE = "&nbht";
    private static final String ENTER = "\n";
    private static final String ENTER_REPLACE = "&nbnl";

    // Sends the new user's information as a URL to a python
    // module which stores it in a sqlite3 database. A JSON
    // response is then received telling if the transaction
    // to the database succeeded or failed.
    public static String registerNewUser(String userName, String password, String eMail, String country, String city) {
        String registerURL = BASE_URL + "_register_user_/" + userName + "/" + password + "/" + eMail + "/" + country + "/" + city;
        registerURL = registerURL.replace(SPACE, SPACE_REPLACE);
        registerURL = registerURL.replace(ENTER, ENTER_REPLACE);
        return getJsonResponse(registerURL);
    }

    public static String loginUser(String identifier, String password) {
        String loginURL = BASE_URL + "_login_user_/" + identifier + "/" + password;
        loginURL = loginURL.replace(SPACE, SPACE_REPLACE);
        return getJsonResponse(loginURL);
    }

    public static String postIdea(String ideaText, String poster, String tags) {
        tags = tags.replace(SPACE, "");
        String postURL = BASE_URL + "_post_idea_/" + ideaText + "/" + poster + "/" + tags;
        postURL = postURL.replace(SPACE, SPACE_REPLACE);
        postURL = postURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        postURL = postURL.replace(ENTER, ENTER_REPLACE);
        return getJsonResponse(postURL);
    }

    public static ArrayList<IdeaRecord> getIdeaFeed(String identifier) {
        String recentEventsURL = BASE_URL + "_get_idea_feed_/" + identifier;
        recentEventsURL = recentEventsURL.replace(SPACE, SPACE_REPLACE);
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(recentEventsURL));
            JSONArray ideaArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideaArray.length(); i++) {
                JSONArray temp = ideaArray.getJSONArray(i);
                if (temp.length() >= 5) {
                    String ideaId = temp.getString(0);
                    String ideaText = temp.getString(1).replace(SPACE_REPLACE, SPACE);
                    ideaText = ideaText.replace(HASH_TAG_REPLACE, HASH_TAG);
                    ideaText = ideaText.replace(ENTER_REPLACE, ENTER);
                    String poster = temp.getString(2);
                    poster = poster.replace(SPACE_REPLACE, SPACE);
                    poster = poster.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String approvalNum = temp.getString(3);
                    boolean isApproving = userIsApproving(identifier, ideaId);
                    JSONArray tagJsonArray = temp.getJSONArray(4);
                    ArrayList<String> tags = new ArrayList<String>();
                    for (int n = 0; n < tagJsonArray.length(); n++) {
                        tags.add(tagJsonArray.getString(n));
                    }
                    ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum, isApproving, tags));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

    // Takes a user name or e-mail and returns
    // a list of that users most recent ideas
    public static ArrayList<IdeaRecord> getOtherUserRecentIdeas(String identifier, String originalUser) {
        String recentIdeasURL = BASE_URL + "_get_other_user_recent_ideas_/" + identifier;
        recentIdeasURL = recentIdeasURL.replace(SPACE, SPACE_REPLACE);
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(recentIdeasURL));
            JSONArray ideaArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideaArray.length(); i++) {
                JSONArray temp = ideaArray.getJSONArray(i);
                if (temp.length() >= 5) {
                    String ideaId = temp.getString(0);
                    String ideaText = temp.getString(1).replace(SPACE_REPLACE, SPACE);
                    ideaText = ideaText.replace(HASH_TAG_REPLACE, HASH_TAG);
                    ideaText = ideaText.replace(ENTER_REPLACE, ENTER);
                    String poster = temp.getString(2);
                    String approvalNum = temp.getString(3);
                    boolean isApproving = userIsApproving(originalUser, ideaId);
                    JSONArray tagJsonArray = temp.getJSONArray(4);
                    ArrayList<String> tags = new ArrayList<String>();
                    for (int n = 0; n < tagJsonArray.length(); n++) {
                        tags.add(tagJsonArray.getString(n));
                    }
                    ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum, isApproving, tags));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

    public static ArrayList<MessageRecord> getMessageFeed(String userName) {
        String getMessageFeedURL = BASE_URL + "_get_message_feed_/" + userName;
        getMessageFeedURL = getMessageFeedURL.replace(SPACE, SPACE_REPLACE);
        getMessageFeedURL = getMessageFeedURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        ArrayList<MessageRecord> messages = new ArrayList<MessageRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getMessageFeedURL));
            JSONArray messagesArray = jsonResponseObject.getJSONArray("messages");
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONArray temp = messagesArray.getJSONArray(i);
                if (temp.length() >= 3) {
                    String sender = temp.getString(0);
                    sender = sender.replace(SPACE_REPLACE, SPACE);
                    sender = sender.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String receiver = temp.getString(1);
                    receiver = receiver.replace(SPACE_REPLACE, SPACE);
                    receiver = receiver.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String messageText = temp.getString(2);
                    messageText = messageText.replace(SPACE_REPLACE, SPACE);
                    messageText = messageText.replace(HASH_TAG_REPLACE, HASH_TAG);
                    messageText = messageText.replace(ENTER_REPLACE, ENTER);
                    messages.add(new MessageRecord(sender, receiver, messageText));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return messages;
    }

    public static ArrayList<MessageRecord> getConversation(String userName, String originalUserName) {
        String getConversationURL = BASE_URL + "_get_conversation_/" + userName + "/" + originalUserName;
        getConversationURL = getConversationURL.replace(SPACE, SPACE_REPLACE);
        getConversationURL = getConversationURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        ArrayList<MessageRecord> recentMessages = new ArrayList<MessageRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getConversationURL));
            JSONArray messagesArray = jsonResponseObject.getJSONArray("messages");
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONArray temp = messagesArray.getJSONArray(i);
                if (temp.length() >= 3) {
                    String sender = temp.getString(0);
                    sender = sender.replace(SPACE_REPLACE, SPACE);
                    sender = sender.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String receiver = temp.getString(1);
                    receiver = receiver.replace(SPACE_REPLACE, SPACE);
                    receiver = receiver.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String messageText = temp.getString(2);
                    messageText = messageText.replace(SPACE_REPLACE, SPACE);
                    messageText = messageText.replace(HASH_TAG_REPLACE, HASH_TAG);
                    messageText = messageText.replace(ENTER_REPLACE, ENTER);
                    recentMessages.add(new MessageRecord(sender, receiver, messageText));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return recentMessages;
    }

    public static String sendMessage(String sender, String receiver, String messageText) {
        String sendMessageURL = BASE_URL + "_send_message_/" + sender + "/" + receiver + "/" + messageText;
        sendMessageURL = sendMessageURL.replace(SPACE, SPACE_REPLACE);
        sendMessageURL = sendMessageURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        sendMessageURL = sendMessageURL.replace(ENTER, ENTER_REPLACE);
        return getJsonResponse(sendMessageURL);
    }

    // Gets the user data of the given identifier
    // All user data is represented as strings which
    // have been returned to a usable state by reversing
    // the URL-friendly versions created using replace
    public static ArrayList<String> getUserData(String identifier) {
        String getUserDataURL = BASE_URL + "_get_user_data_/" + identifier;
        getUserDataURL = getUserDataURL.replace(SPACE, SPACE_REPLACE);
        ArrayList<String> userData = new ArrayList<String>();

        if (getJsonResponse(getUserDataURL).equals("Success")) {
            try {
                JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getUserDataURL));

                // Gets JSON Arrays containing all the useful information
                JSONArray userNameArray = jsonResponseObject.getJSONArray("user_name");
                JSONArray emailArray = jsonResponseObject.getJSONArray("e_mail");
                JSONArray countryArray = jsonResponseObject.getJSONArray("country");
                JSONArray cityArray = jsonResponseObject.getJSONArray("city");
                JSONArray followersArray = jsonResponseObject.getJSONArray("followers");
                JSONArray locationArray = jsonResponseObject.getJSONArray("location");

                // Converts the JSON Arrays to strings
                String userName = userNameArray.getString(0);
                userName = userName.replace(SPACE_REPLACE, SPACE);
                userName = userName.replace(HASH_TAG_REPLACE, HASH_TAG);
                String eMail = emailArray.getString(0);
                String country = countryArray.getString(0);
                country = country.replace(SPACE_REPLACE, SPACE);
                String city = cityArray.getString(0);
                city = city.replace(SPACE_REPLACE, SPACE);
                String followers = followersArray.getString(0);
                String location = locationArray.getString(0);
                location = location.replace(SPACE_REPLACE, SPACE);

                // Adds the strings to the userData array list
                userData.add(userName);
                userData.add(eMail);
                userData.add(country);
                userData.add(city);
                userData.add(followers);
                userData.add(location);
            } catch (JSONException e) {
                System.out.println("JSON Exception");
                e.printStackTrace();
            }
            return userData;
        } else {
            return null;
        }
    }

    public static String getUserName(String eMail) {
        String getUserNameURL = BASE_URL + "_get_user_name_/" + eMail;
        getUserNameURL = getUserNameURL.replace(SPACE, SPACE_REPLACE);
        String userName = "";

        if (getJsonResponse(getUserNameURL).equals("Success")) {
            try {
                JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getUserNameURL));
                JSONArray userNameArray = jsonResponseObject.getJSONArray("user_name");
                userName = userNameArray.getString(0);
                userName = userName.replace(SPACE_REPLACE, SPACE);
                userName = userName.replace(HASH_TAG_REPLACE, HASH_TAG);
            } catch (JSONException e) {
                System.out.println("JSON Exception");
                e.printStackTrace();
            }
            return userName;
        } else {
            return "ERROR!!!";
        }
    }

    public static boolean userIsFollowing(String user, String otherUser) {
        String userIsFollowingURL = BASE_URL + "_user_is_following_/" + user + "/" + otherUser;
        userIsFollowingURL = userIsFollowingURL.replace(SPACE, SPACE_REPLACE);
        boolean isFollowing = false;

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(userIsFollowingURL));
            JSONArray isFollowingArray = jsonResponseObject.getJSONArray("is_following");
            isFollowing = isFollowingArray.getBoolean(0);
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return isFollowing;
    }

    public static String addFollower(String user, String follower) {
        String addFollowerURL = BASE_URL + "_add_follower_/" + user + "/" + follower;
        addFollowerURL = addFollowerURL.replace(SPACE, SPACE_REPLACE);
        return getUrlResponseString(addFollowerURL);
    }

    public static String removeFollower(String user, String follower) {
        String removeFollowerURL = BASE_URL + "_remove_follower_/" + user + "/" + follower;
        removeFollowerURL = removeFollowerURL.replace(SPACE, SPACE_REPLACE);
        return getUrlResponseString(removeFollowerURL);
    }

    public static ArrayList<IdeaRecord> searchIdeas(String tags, String originalUser) {
        String searchIdeasURL = BASE_URL + "_search_ideas_/" + tags;
        searchIdeasURL = searchIdeasURL.replace(SPACE, "");
        searchIdeasURL = searchIdeasURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        searchIdeasURL = searchIdeasURL.replace(ENTER, "");
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(searchIdeasURL));
            JSONArray ideaJsonArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideaJsonArray.length(); i++) {
                JSONArray tempArray = ideaJsonArray.getJSONArray(i);
                if (tempArray.length() >= 5) {
                    String ideaId = tempArray.getString(0);
                    String ideaText = tempArray.getString(1);
                    ideaText = ideaText.replace(SPACE_REPLACE, SPACE);
                    ideaText = ideaText.replace(HASH_TAG_REPLACE, HASH_TAG);
                    ideaText = ideaText.replace(ENTER_REPLACE, ENTER);
                    String poster = tempArray.getString(2);
                    poster = poster.replace(SPACE_REPLACE, SPACE);
                    poster = poster.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String approvalNum = tempArray.getString(3);
                    boolean isApproving = userIsApproving(originalUser, ideaId);
                    JSONArray tempTags = tempArray.getJSONArray(4);
                    ArrayList<String> tagList = new ArrayList<String>();
                    for (int n = 0; n < tempTags.length(); n++) {
                        tagList.add(tempTags.getString(n));
                    }
                    ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum, isApproving, tagList));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

    public static ArrayList<UserRecord> searchPeople(String identifiers) {
        String searchPeopleURL = BASE_URL + "_search_people_/" + identifiers;
        searchPeopleURL = searchPeopleURL.replace("," + SPACE, HASH_TAG_REPLACE);
        searchPeopleURL = searchPeopleURL.replace(",", HASH_TAG_REPLACE);
        searchPeopleURL = searchPeopleURL.replace(SPACE, SPACE_REPLACE);
        searchPeopleURL = searchPeopleURL.replace(ENTER, SPACE_REPLACE);
        ArrayList<UserRecord> people = new ArrayList<UserRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(searchPeopleURL));
            JSONArray peopleJsonArray = jsonResponseObject.getJSONArray("people");
            for (int i = 0; i < peopleJsonArray.length(); i++) {
                JSONArray tempArray = peopleJsonArray.getJSONArray(i);
                if (tempArray.length() >= 5) {
                    String userName = tempArray.getString(0);
                    userName = userName.replace(SPACE_REPLACE, SPACE);
                    userName = userName.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String eMail = tempArray.getString(1);
                    String country = tempArray.getString(2);
                    country = country.replace(SPACE_REPLACE, SPACE);
                    String city = tempArray.getString(3);
                    city = city.replace(SPACE_REPLACE, SPACE);
                    String followers = tempArray.getString(4);
                    people.add(new UserRecord(userName, eMail, country, city, followers));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return people;
    }

    public static String updateUserData(String originalUserName, String originalEMail,
                                        String newUserName, String newEMail, String newPassword,
                                        String newCountry, String newCity, String newLocation) {
        String updateUserDataURL = BASE_URL + "_update_user_data_/" + originalUserName + "/" +
                originalEMail + "/" + newUserName + "/" + newEMail + "/" + newPassword + "/"
                + newCountry + "/" + newCity + "/" + newLocation;
        updateUserDataURL = updateUserDataURL.replace(SPACE, SPACE_REPLACE);
        updateUserDataURL = updateUserDataURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        updateUserDataURL = updateUserDataURL.replace(ENTER, ENTER_REPLACE);
        return getJsonResponse(updateUserDataURL);
    }

    public static String addApproving(String userName, String ideaId) {
        String addApprovingURL = BASE_URL + "_add_approving_/" + userName + "/" + ideaId;
        addApprovingURL = addApprovingURL.replace(SPACE, SPACE_REPLACE);
        return getJsonResponse(addApprovingURL);
    }

    public static String removeApproving(String userName, String ideaId) {
        String removeApprovingURL = BASE_URL + "_remove_approving_/" + userName + "/" + ideaId;
        removeApprovingURL = removeApprovingURL.replace(SPACE, SPACE_REPLACE);
        return getJsonResponse(removeApprovingURL);
    }

    public static boolean userIsApproving(String userName, String ideaId) {
        String userIsApprovingURL = BASE_URL + "_user_is_approving_/" + userName + "/" + ideaId;
        userIsApprovingURL = userIsApprovingURL.replace(SPACE, SPACE_REPLACE);
        boolean isApproving = false;

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(userIsApprovingURL));
            JSONArray userIsApprovingArray = jsonResponseObject.getJSONArray("approving");
            isApproving = userIsApprovingArray.getBoolean(0);
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return isApproving;
    }

    public static ArrayList<UserRecord> getFollowing(String userName) {
        String getFollowingURL = BASE_URL + "_get_following_/" + userName;
        getFollowingURL = getFollowingURL.replace(SPACE, SPACE_REPLACE);
        getFollowingURL = getFollowingURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        ArrayList<UserRecord> following = new ArrayList<UserRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getFollowingURL));
            JSONArray followingArray = jsonResponseObject.getJSONArray("following");
            for (int i = 0; i < followingArray.length(); i++) {
                JSONArray temp = followingArray.getJSONArray(i);
                String fUserName = temp.getString(0);
                fUserName = fUserName.replace(SPACE_REPLACE, SPACE);
                fUserName = fUserName.replace(HASH_TAG_REPLACE, HASH_TAG);
                String fEMail = temp.getString(1);
                String fCountry = temp.getString(2);
                fCountry = fCountry.replace(SPACE_REPLACE, SPACE);
                String fCity = temp.getString(3);
                fCity = fCity.replace(SPACE_REPLACE, SPACE);
                String fFollowers = temp.getString(4);
                following.add(new UserRecord(fUserName, fEMail, fCountry, fCity, fFollowers));
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return following;
    }

    public static ArrayList<IdeaRecord> getApproving(String userName, String originalUserName) {
        String getApprovingURL = BASE_URL + "_get_approving_/" + userName;
        getApprovingURL = getApprovingURL.replace(SPACE, SPACE_REPLACE);
        getApprovingURL = getApprovingURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        ArrayList<IdeaRecord> approving = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getApprovingURL));
            JSONArray followingArray = jsonResponseObject.getJSONArray("approving");
            for (int i = 0; i < followingArray.length(); i++) {
                JSONArray temp = followingArray.getJSONArray(i);
                String fIdeaId = temp.getString(0);
                String fIdeaText = temp.getString(1).replace(SPACE_REPLACE, SPACE);
                fIdeaText = fIdeaText.replace(HASH_TAG_REPLACE, HASH_TAG);
                String fPoster = temp.getString(2);
                fPoster = fPoster.replace(SPACE_REPLACE, SPACE);
                fPoster = fPoster.replace(HASH_TAG_REPLACE, HASH_TAG);
                String fApprovalNum = temp.getString(3);
                boolean isApproving = userIsApproving(originalUserName, fIdeaId);
                JSONArray tagsJsonArray = temp.getJSONArray(4);
                ArrayList<String> fTags = new ArrayList<String>();
                for (int n = 0; n < tagsJsonArray.length(); n++) {
                    String tempTag = tagsJsonArray.getString(n);
                    tempTag = tempTag.replace(HASH_TAG_REPLACE, HASH_TAG);
                    fTags.add(tempTag);
                }
                approving.add(new IdeaRecord(fIdeaId, fIdeaText, fPoster, fApprovalNum, isApproving, fTags));
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return approving;
    }

    public static String postComment(String user, String ideaId, String commentText) {
        String postCommentURL = BASE_URL + "_add_comment_/" + user + "/" + ideaId + "/" + commentText;
        postCommentURL = postCommentURL.replace(SPACE, SPACE_REPLACE);
        postCommentURL = postCommentURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        postCommentURL = postCommentURL.replace(ENTER, ENTER_REPLACE);
        return getJsonResponse(postCommentURL);
    }

    public static ArrayList<CommentRecord> getComments(String ideaId) {
        String getCommentsUrl = BASE_URL + "_get_comments_/" + ideaId;
        ArrayList<CommentRecord> comments = new ArrayList<CommentRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getCommentsUrl));
            JSONArray commentsArray = jsonResponseObject.getJSONArray("comments");
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONArray tempArray = commentsArray.getJSONArray(i);
                if (tempArray.length() >= 2) {
                    String user = tempArray.getString(0);
                    user = user.replace(SPACE_REPLACE, SPACE);
                    user = user.replace(HASH_TAG_REPLACE, HASH_TAG);
                    String commentText = tempArray.getString(1);
                    commentText = commentText.replace(SPACE_REPLACE, SPACE);
                    commentText = commentText.replace(HASH_TAG_REPLACE, HASH_TAG);
                    commentText = commentText.replace(ENTER_REPLACE, ENTER);
                    comments.add(new CommentRecord(user, commentText));
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return comments;
    }

    private static String getUrlResponseString(String url) {
        // Reading URL response
        StringBuilder builder = new StringBuilder();
        HttpClient client = getNewHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                System.out.println("Failed to download file");
            }
        } catch (ClientProtocolException e) {
            System.out.println("Client Protocol Exception");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            System.out.println("Network On Main Thread Exception");
            e.printStackTrace();
        }
        // End reading of URL
        return builder.toString();
    }

    // Gets a Http Client which will accept all certificates
    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static String getJsonResponse(String URL) {
        String response = "Failed";

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(URL));
            JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("response");
            response = jsonResponseArray.getString(0);
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return response;
    }
}
