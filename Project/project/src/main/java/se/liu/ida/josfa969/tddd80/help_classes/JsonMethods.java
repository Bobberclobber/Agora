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
    private static String replaceString = " ";
    private static String replacementString = "&nbsp";

    // Sends the new user's information as a URL to a python
    // module which stores it in a sqlite3 database. A JSON
    // response is then received telling if the transaction
    // to the database succeeded or failed.
    public static String registerNewUser(String userName, String password, String eMail, String country, String city) {
        String registerURL = BASE_URL + "_register_user_/" + userName + "/" + password + "/" + eMail + "/" + country + "/" + city;
        registerURL = registerURL.replace(replaceString, replacementString);
        return getJsonResponse(registerURL);
    }

    public static String loginUser(String identifier, String password) {
        String loginURL = BASE_URL + "_login_user_/" + identifier + "/" + password;
        loginURL = loginURL.replace(replaceString, replacementString);
        return getJsonResponse(loginURL);
    }

    public static String postIdea(String ideaText, String poster) {
        String postURL = BASE_URL + "_post_idea_/" + ideaText + "/" + poster;
        postURL = postURL.replace(replaceString, replacementString);
        return getJsonResponse(postURL);
    }

    public static ArrayList<IdeaRecord> getRecentIdeas(String identifier) {
        String recentEventsURL = BASE_URL + "_get_recent_activities_/" + identifier;
        recentEventsURL = recentEventsURL.replace(replaceString, replacementString);
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        if (getJsonResponse(recentEventsURL).equals("Success")) {
            try {
                JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(recentEventsURL));
                JSONArray ideaArray = jsonResponseObject.getJSONArray("ideas");
                for (int i = 0; i < ideaArray.length(); i += 4) {
                    String ideaId = ideaArray.getString(i);
                    String ideaText = ideaArray.getString(i + 1).replace(replacementString, replaceString);
                    String poster = ideaArray.getString(i + 2);
                    String approvalNum = ideaArray.getString(i + 3);
                    ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum));
                }
            } catch (JSONException e) {
                System.out.println("JSON Exception");
                e.printStackTrace();
            }
            return ideas;
        } else {
            return null;
        }
    }

    // Takes a user name or e-mail and returns
    // a list of that users most recent ideas
    public static ArrayList<IdeaRecord> getOtherUserRecentIdeas(String identifier) {
        String recentIdeasURL = BASE_URL + "_get_other_user_recent_ideas_/" + identifier;
        recentIdeasURL = recentIdeasURL.replace(replaceString, replacementString);
        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(recentIdeasURL));
            JSONArray ideasArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideasArray.length(); i++) {
                JSONArray temp = (JSONArray) ideasArray.get(i);
                String ideaId = temp.getString(0);
                String ideaText = temp.getString(1).replace(replacementString, replaceString);
                String poster = temp.getString(2);
                String approvalNum = temp.getString(3);
                ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum));
            }
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return ideas;
    }

    public static ArrayList<String> getUserData(String identifier) {
        String getUserDataURL = BASE_URL + "_get_user_data_/" + identifier;
        getUserDataURL = getUserDataURL.replace(replaceString, replacementString);
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
        getUserNameURL = getUserNameURL.replace(replaceString, replacementString);
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
        getEMailURL = getEMailURL.replace(replaceString, replacementString);
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
