package com.example.android.quakereport;


import android.text.TextUtils;
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
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public  static  final String LOG_TAG= QueryUtils.class.getSimpleName();

    public static ArrayList<data> fetchEarthquakeData(String requesturl)
    {
        URL url= createUrl(requesturl); //calling create url method to generate url from string url
        //make http request
        String JsonResponse = null;
        try{
            JsonResponse= makehttpRequest(url);
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"error in finding InputStream",e);
        }

        ArrayList<data> earthquakes= extractEarthquakes(JsonResponse);
        return earthquakes;
    }
    //creating url from the string provided
    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makehttpRequest (URL url) throws IOException{
        String JsonResponse="";
        if(url==null){
            return  JsonResponse;
        }

        HttpURLConnection urlConnection= null;
        InputStream inputStream= null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.

            if(urlConnection.getResponseCode()==200){

                inputStream = urlConnection.getInputStream();

                JsonResponse= readfromStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }

        catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JsonResponse;

    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readfromStream(InputStream inputStream) throws IOException {
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


    private static ArrayList<data> extractEarthquakes(String Jsonresponse){

        //if jsonresponse is empty return null
        if (TextUtils.isEmpty(Jsonresponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<data> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject baseJSONResponse=new JSONObject(Jsonresponse);
            JSONArray earthquakearray= baseJSONResponse.getJSONArray("features");
            for (int i=0;i<earthquakearray.length();i++)
            {
                JSONObject currentearthquake= earthquakearray.getJSONObject(i);
                JSONObject properties =currentearthquake.getJSONObject("properties");
                double Magnitude = properties.getDouble("mag");
                String location= properties.getString("place");
                long time= properties.getLong("time");
                String url = properties.getString("url");

                data object= new data(Magnitude,location,time,url);
                  earthquakes.add(object);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}