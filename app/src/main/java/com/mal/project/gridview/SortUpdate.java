package com.mal.project.gridview;


import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class SortUpdate extends AsyncTask<String, Void, List<Movie>> {
    List<Movie> movies = new ArrayList<>();
    GridView gridView;
    GridAdapter gridAdapter;
    Context context;
    ProgressBar progressBar;
    LinearLayout noInternetText;
    Button tryAgain;
    boolean connectionStatusFlag=true;


    SortUpdate(GridView gridView,GridAdapter gridAdapter,Context context
            ,ProgressBar progressBar,LinearLayout noInternetText,Button tryAgain){

        this.gridView=gridView;
        this.gridAdapter=gridAdapter;
        this.context=context;
        this.progressBar=progressBar;
        this.noInternetText=noInternetText;
        this.tryAgain =tryAgain;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        gridView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    }
    @Override
    protected List<Movie> doInBackground(String... params) {
        HttpURLConnection connection=null;
        BufferedReader bufferedReader=null;
        // Behind the scenes work
        String JSON;
        try {
            // Network Connection and JSON reading
            URL url =new URL(params[0]);
            connection= (HttpURLConnection) url.openConnection();
            InputStreamReader streamReader= new InputStreamReader(connection.getInputStream());
            bufferedReader = new BufferedReader(streamReader);
            // JSON Understanding
            JSON = bufferedReader.readLine();
            JSONObject jsonObject = new JSONObject(JSON);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            String date;
            // Holding clear data
            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                date=jsonObject1.getString("release_date");
                date=date.substring(0,4);
                movies.add(new Movie(jsonObject1.getString("original_title")
                        ,"https://image.tmdb.org/t/p/w300_and_h450_bestv2"+jsonObject1.getString("poster_path")
                        ,jsonObject1.getString("overview"),jsonObject1.getString("original_language").toUpperCase()
                        ,jsonObject1.getString("vote_count"),jsonObject1.getString("vote_average"),date,jsonObject1.getInt("id")+""));
            }
        }     catch (IOException e) {
            e.printStackTrace();
            connectionStatusFlag=false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (bufferedReader!= null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {

                }
            }
        }
        return movies;
    }
    @Override
    protected void onPostExecute(List<Movie> s) {
        super.onPostExecute(s);
        if(!connectionStatusFlag){
            tryAgain.setVisibility(View.VISIBLE);
            noInternetText.setVisibility(View.VISIBLE);
        }
        gridAdapter= new GridAdapter(s);
        gridView.setAdapter(gridAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);
    }




}
