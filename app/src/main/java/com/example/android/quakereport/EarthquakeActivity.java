/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<data>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL= "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";
    private static final int EARTHQUAKE_LOADER_ID = 1; // creating an unique loader id
    private dataAdaptor adapter;
    ListView earthquakeListView;
    TextView emptyTextDisplay;



    @Override
    public Loader<ArrayList<data>> onCreateLoader(int i, Bundle bundle) {
        Log.e("Log_TAG","new Loader created");
        return new EarthquakeLoader(this,USGS_REQUEST_URL);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<data>> loader, ArrayList<data> earthquakes) {
        Log.e("Log_TAG","On Load finishedddd");
        ProgressBar progressBar= (ProgressBar)findViewById(R.id.LodingBar);
        progressBar.setVisibility(View.GONE);
        if (earthquakes != null && !earthquakes.isEmpty()){
            if(adapter!=null){
            adapter.clear();}
            UpdateUI(earthquakes);
        }
        else{

            earthquakeListView = (ListView) findViewById(R.id.list);
            emptyTextDisplay=(TextView)findViewById(R.id.emptytextdisplay);
            emptyTextDisplay.setText(R.string.no_earthquakes);
            earthquakeListView.setEmptyView(emptyTextDisplay);
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<data>> loader) {
        adapter.clear();
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static class EarthquakeLoader extends AsyncTaskLoader<ArrayList<data>>{
        private String mUrl;

        //constructor for loader calling super constructor and initializing url
        public EarthquakeLoader(Context context,String url) {
            super(context);
            mUrl=USGS_REQUEST_URL;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();        // this will run load in background method
        }

        @Override
        public ArrayList<data> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            ArrayList<data> earthquakes= QueryUtils.fetchEarthquakeData(USGS_REQUEST_URL); //fetching data from query utils
            return  earthquakes;  // Return list of earthquakes fetched.
        }
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //checking if device id connected to internet or not
        ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo == null || !networkInfo.isConnected()){
                ProgressBar progressBar= (ProgressBar)findViewById(R.id.LodingBar); // remove progress bar
                progressBar.setVisibility(View.GONE);
                earthquakeListView = (ListView) findViewById(R.id.list); // set empty view text to no internet connection
                emptyTextDisplay=(TextView)findViewById(R.id.emptytextdisplay);
                emptyTextDisplay.setText(R.string.No_Internet);
                earthquakeListView.setEmptyView(emptyTextDisplay);

            }

        //calling loaderManager upon creating an Activity
        else{
        LoaderManager loaderManager= getLoaderManager();
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);}


    }

    private void UpdateUI (ArrayList<data> earthquakes){
         earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
         adapter= new dataAdaptor(this,earthquakes,0);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                data currentEarthquake = (data) adapter.getItem(i);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });



    }


}
