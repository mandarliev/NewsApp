package com.example.android.newsapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving articles from the Guardian
 */
public class QueryUtils {

    /**
     * Query the Guardian dataset and return a list of News objects.
     */
    static String createStringUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("show-references", "author")
                .appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("q", "Android")
                .appendQueryParameter("api-key", "5e8732dd-9180-4460-a32d-a36df1406152");
        String url = builder.build().toString();
        return url;
    }

    // Returns new URL object from the given string URL
    static URL createUrl() {
        String stringUrl = createStringUrl();
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Queryutils", "Error creating URL: ", e);
            return null;
        }
    }

    // Returns a formatted date and time string for when the earthquake happened.
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }

    // Make an HTTP request to the given URL and return a String as the response.
    static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early
        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Queryutils", "Error making HTTP request: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of News objects that has been built up from
     * parsing the given JSON response.
     */
    static List<News> parseJson(String response) {
        ArrayList<News> listOfNews = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject jsonResponse = new JSONObject(response);

            // Extract the JSONObject associated with the key called "response"
            JSONObject jsonResults = jsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results"
            JSONArray resultsArray = jsonResults.getJSONArray("results");

            // For each article in the resultsArray, create a News object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single article at position i within the list of earthquakes
                JSONObject oneResult = resultsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String webTitle = oneResult.getString("webTitle");

                // Extract the value for the key called "webUrl"
                String url = oneResult.getString("webUrl");

                // Extract the value for the key called "webPublicationDate"
                String date = oneResult.getString("webPublicationDate");

                // Format the date
                date = formatDate(date);

                // Extract the value for the key called "sectionName"
                String section = oneResult.getString("sectionName");

                // Extract the value for the key called "tags"
                JSONArray tagsArray = oneResult.getJSONArray("tags");
                String author = "";

                // Check if there is an author passed
                if (tagsArray.length() == 0) {
                    author = null;
                } else {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject firstObject = tagsArray.getJSONObject(j);
                        author += firstObject.getString("webTitle") + ". ";
                    }
                }

                // Add the new article to the list of news.
                listOfNews.add(new News(webTitle, author, url, date, section));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Queryutils", "Error parsing JSON response", e);
        }
        // Return the list of news
        return listOfNews;
    }
}
