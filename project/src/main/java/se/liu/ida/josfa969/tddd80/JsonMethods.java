package se.liu.ida.josfa969.tddd80;

import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;

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

/**
 * Created by Josef on 18/07/14.
 *
 * A class containing methods used to get JSON-responses from URLs
 */
public class JsonMethods {
    private static String BASE_URL = "http://localhost:5000/_register_user_/";

    // Sends the new user's information as a URL to a python
    // module which stores it in a sqlite3 database. A JSON
    // response is then received telling if the transaction
    // to the database succeeded or failed.
    public static String registerNewUser(String firstName, String lastName, String eMail, String country, String city) {
        String registerURL = BASE_URL + firstName + "/" + lastName + "/" + eMail + "/" + country + "/" + city;
        String response = "Failed";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            JSONObject jsonResponseObject = new JSONObject(getUrlResponseString(registerURL));
            JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("response");
            response = jsonResponseArray.get(0).toString();
        } catch (JSONException e) {
            System.out.println("JSON Exception");
        }
        return response;
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
}
