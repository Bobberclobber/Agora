package se.liu.ida.josfa969.tddd80;

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

    public static ArrayList<IdeaRecord> getRecentEvents(String identifier) {
        String recentEventsURL = BASE_URL + "_get_recent_activities_/" + identifier;
        recentEventsURL = recentEventsURL.replace(replaceString, replacementString);

        ArrayList<IdeaRecord> ideas = new ArrayList<IdeaRecord>();

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(recentEventsURL));
            JSONArray ideaArray = jsonResponseObject.getJSONArray("ideas");
            for (int i = 0; i < ideaArray.length(); i += 4) {
                String ideaId = ideaArray.get(i).toString();
                String ideaText = ideaArray.get(i + 1).toString().replace(replacementString, replaceString);
                String poster = ideaArray.get(i + 2).toString();
                String approvalNum = ideaArray.get(i + 3).toString();
                ideas.add(new IdeaRecord(ideaId, ideaText, poster, approvalNum));
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
            response = jsonResponseArray.get(0).toString();
        } catch (JSONException e) {
            System.out.println("JSON Exception");
            e.printStackTrace();
        }
        return response;
    }
}
