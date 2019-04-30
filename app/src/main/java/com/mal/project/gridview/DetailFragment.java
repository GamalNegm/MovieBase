package com.mal.project.gridview;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mal.gridview.gridview.BuildConfig;
import com.mal.gridview.gridview.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    String posterUrl,title,overview,lang,vote_count,rate,date,id;
    ArrayList<String> fav_ids=new ArrayList<>();
    ImageView imageView;
    TextView overview_text,lang_text,vote_count_text,rate_text,date_text;
    RecyclerView recyclerView;
    RecyclerView ReviewRecyclerView;
    VideosListAdapter adapter;
    ReviewsListAdapter reviewsListAdapter;
    List<String> videosLinks=new ArrayList<>();
    List<String> ReviewsBody=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_detail,container,false);

        int screenSize =getResources().getInteger(R.integer.screen_size);
        if(screenSize>=600)
            EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        imageView= (ImageView) view.findViewById(R.id.poster_detail);
        overview_text= (TextView) view.findViewById(R.id.overview_text);
        lang_text= (TextView) view.findViewById(R.id.lang_text);
        vote_count_text= (TextView) view.findViewById(R.id.vote_count_text);
        rate_text= (TextView) view.findViewById(R.id.rate_text);
        date_text= (TextView)view.findViewById(R.id.date_text);
        recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_recyclerView);
        ReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        Intent intent = getActivity().getIntent();
        if(screenSize<600){
            if (intent!= null){
                title = intent.getStringExtra("title");
                posterUrl = intent.getStringExtra("poster");
                overview = intent.getStringExtra("overview");
                lang = intent.getStringExtra("lang");
                vote_count = intent.getStringExtra("vote_count");
                rate = intent.getStringExtra("rate");
                date = intent.getStringExtra("date");
                id = intent.getStringExtra("id");
                fav_ids = intent.getStringArrayListExtra("fav_ids");

                new VideosDataWork().execute("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+ BuildConfig.API_KEY);
                new ReviewsDataWork().execute("https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+BuildConfig.API_KEY);

                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
                Picasso.with(getActivity()).load(posterUrl).into(imageView);
                overview_text.setText(overview);
                lang_text.setText(lang);
                vote_count_text.setText(vote_count);
                rate_text.setText(rate);
                date_text.setText(date);
            }
        }

        return view;

    }
    @Override
    public void onStart() {
        super.onStart();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void buttonClicked(TabletMovieClicked buttonClicked){
        title=buttonClicked.title;
        posterUrl=buttonClicked.posterUrl;
        overview=buttonClicked.overview;
        lang=buttonClicked.lang;
        vote_count=buttonClicked.vote_count;
        rate=buttonClicked.rate;
        date=buttonClicked.date;
        id=buttonClicked.id;
        fav_ids=buttonClicked.fav_ids;

        new VideosDataWork().execute("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+BuildConfig.API_KEY);
        new ReviewsDataWork().execute("https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+BuildConfig.API_KEY);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        Picasso.with(getActivity()).load(posterUrl).into(imageView);
        overview_text.setText(overview);
        lang_text.setText(lang);
        vote_count_text.setText(vote_count);
        rate_text.setText(rate);
        date_text.setText(date);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu_layout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (id!=null){

        try {
            boolean flag = true;
            for (int i = 0; i < fav_ids.size(); i++) {

                if (id.equals(fav_ids.get(i))) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                FileOutputStream fileOutputStream = getActivity().openFileOutput("fav_json_file", Context.MODE_APPEND);
                Toast.makeText(getActivity(), title + " added to favorites ", Toast.LENGTH_SHORT).show();
                fileOutputStream.write(id.getBytes());
                fileOutputStream.write(System.getProperty("line.separator").getBytes());
                fileOutputStream.close();
                fav_ids.add(id);
            } else {
                Toast.makeText(getActivity(), "This movie is already favorite ", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        else
            Toast.makeText(getActivity(),"No movie selected", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        int screenSize =getResources().getInteger(R.integer.screen_size);
        if(screenSize>=600)
        EventBus.getDefault().unregister(this);
    }
    class VideosDataWork extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // task will done in background
            String link = params[0];
            String result = null;
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                result = reader.readLine();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                videosLinks.clear();
                for(int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    videosLinks.add(jsonObject1.getString("key"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(layoutManager);
            adapter = new VideosListAdapter(videosLinks);

            recyclerView.setAdapter(adapter);
        }

    }
    //================================================================

    class ReviewsDataWork extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // task will done in background
            String link = params[0];
            String result = null;
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                result = reader.readLine();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                ReviewsBody.clear();
                for(int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    ReviewsBody.add(jsonObject1.getString("content"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity());

            ReviewRecyclerView.setLayoutManager(layoutManager);
            reviewsListAdapter = new ReviewsListAdapter(ReviewsBody);

            ReviewRecyclerView.setAdapter(reviewsListAdapter);
        }

    }
















}
