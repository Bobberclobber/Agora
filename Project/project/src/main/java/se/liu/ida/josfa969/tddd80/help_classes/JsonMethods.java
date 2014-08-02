package se.liu.ida.josfa969.tddd80.help_classes;

import android.os.NetworkOnMainThreadException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Josef on 18/07/14.
 * <p/>
 * A class containing methods used to get JSON-responses from URLs
 */
public class JsonMethods {
    private static String BASE_URL = "http://10.0.3.2:5000/";
    private static final String SPACE = " ";
    private static final String SPACE_REPLACE = "&nbsp";
    private static final String HASH_TAG = "#";
    private static final String HASH_TAG_REPLACE = "&nbht";

    // Sends the new user's information as a URL to a python
    // module which stores it in a sqlite3 database. A JSON
    // response is then received telling if the transaction
    // to the database succeeded or failed.
    public static String registerNewUser(String userName, String password, String eMail, String country, String city) {
        String registerURL = BASE_URL + "_register_user_/" + userName + "/" + password + "/" + eMail + "/" + country + "/" + city;
        registerURL = registerURL.replace(SPACE, SPACE_REPLACE);
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
                JSONArray temp = (JSONArray) ideaArray.get(i);
                String ideaId = temp.getString(0);
                String ideaText = temp.getString(1).replace(SPACE_REPLACE, SPACE);
                String poster = temp.getString(2);
                String approvalNum = temp.getString(3);
                JSONArray tagJsonArray = temp.getJSONArray(4);
                ArrayList<String> tags = new ArrayList<String>();
                for (int n = 0; n < tagJsonArray.length(); n++) {
                    tags.add(tagJsonArray.getString(n));
                }
                ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum, tags));
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

    // Takes a user name or e-mail and returns
    // a list of that users most recent ideas
    public static ArrayList<IdeaRecord> getOtherUserRecentIdeas(String identifier) {
        String recentIdeasURL = BASE_URL + "_get_other_user_recent_ideas_/" + identifier;
        recentIdeasURL = recentIdeasURL.replace(SPACE, SPACE_REPLACE);
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(recentIdeasURL));
            JSONArray ideaArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideaArray.length(); i++) {
                JSONArray temp = (JSONArray) ideaArray.get(i);
                String ideaId = temp.getString(0);
                String ideaText = temp.getString(1).replace(SPACE_REPLACE, SPACE);
                String poster = temp.getString(2);
                String approvalNum = temp.getString(3);
                JSONArray tagJsonArray = temp.getJSONArray(4);
                ArrayList<String> tags = new ArrayList<String>();
                for (int n = 0; n < tagJsonArray.length(); n++) {
                    tags.add(tagJsonArray.getString(n));
                }
                ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum, tags));
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

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

                // Converts the JSON Arrays to strings and
                // adds them to the userData array list
                userData.add(userNameArray.getString(0));
                userData.add(emailArray.getString(0));
                userData.add(countryArray.getString(0));
                userData.add(cityArray.getString(0));
                userData.add(followersArray.getString(0));
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
            } catch (JSONException e) {
                System.out.println("JSON Exception");
                e.printStackTrace();
            }
            return userName;
        } else {
            return "ERROR!!!";
        }
    }

    public static String getEMail(String userName) {
        String getEMailURL = BASE_URL + "_get_e_mail_/" + userName;
        getEMailURL = getEMailURL.replace(SPACE, SPACE_REPLACE);
        String eMail = "";

        if (getJsonResponse(getEMailURL).equals("Success")) {
            try {
                JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(getEMailURL));
                JSONArray eMailArray = jsonResponseObject.getJSONArray("e_mail");
                eMail = eMailArray.getString(0);
            } catch (JSONException e) {
                System.out.println("JSON Exception");
                e.printStackTrace();
            }
            return eMail;
        } else {
            return "ERROR!!!";
        }
    }

    public static boolean userIsFollowing(String user, String otherUser) {
        return true;
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

    public static ArrayList<IdeaRecord> searchIdeas(String tags) {
        String searchIdeasURL = BASE_URL + "_search_ideas_/" + tags;
        searchIdeasURL = searchIdeasURL.replace(SPACE, "");
        searchIdeasURL = searchIdeasURL.replace(HASH_TAG, HASH_TAG_REPLACE);
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(searchIdeasURL));
            JSONArray ideaJsonArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideaJsonArray.length(); i++) {
                JSONArray tempArray = ideaJsonArray.getJSONArray(i);
                String ideaId = tempArray.getString(0);
                String ideaText = tempArray.getString(1);
                String poster = tempArray.getString(2);
                String approvalNum = tempArray.getString(3);
                JSONArray tempTags = tempArray.getJSONArray(4);
                ArrayList<String> tagList = new ArrayList<String>();
                for (int n = 0; n < tempTags.length(); n++) {
                    tagList.add(tempTags.getString(n));
                }
                ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum, tagList));
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

    private static String getUrlResponseString(String url) {
        // Reading URL response
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
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
