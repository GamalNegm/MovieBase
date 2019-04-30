package com.mal.project.gridview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.mal.gridview.gridview.BuildConfig;
import com.mal.gridview.gridview.R;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener,AdapterView.OnItemClickListener {
    List<Movie> movies = new ArrayList<>();
    ArrayList<String> fav_ids=new ArrayList<>();
    GridView gridView;
    GridAdapter gridAdapter;
    ArrayAdapter<CharSequence> sort_list_adapter;
    Spinner spinner;
    SharedPreferences spinnerSP;
    ProgressBar progressBar;
    LinearLayout noInternetText;
    Button tryAgain;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main,container,false);;
        setHasOptionsMenu(true);
        tryAgain = (Button) view.findViewById(R.id.try_again_button);
        tryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                noInternetText.setVisibility(View.INVISIBLE);
                tryAgain.setVisibility(View.INVISIBLE);
                switch (spinner.getSelectedItemPosition()){
                    case 0:case 2:
                        new SortUpdate(gridView,gridAdapter,getActivity(),progressBar,noInternetText,tryAgain).execute("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+ BuildConfig.API_KEY);
                        break;
                    case 1:
                        new SortUpdate(gridView,gridAdapter,getActivity(),progressBar,noInternetText,tryAgain).execute("https://api.themoviedb.org/3/discover/movie?sort_by=vote_count.desc&api_key="+BuildConfig.API_KEY);
                        break;

                    case 3:
                        readFavFile();
                        new Favorite(gridView,gridAdapter,getActivity(),progressBar,fav_ids,noInternetText,tryAgain).execute();
                        break;
                }

            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noInternetText= (LinearLayout) view.findViewById(R.id.no_internet_warning);

        progressBar.setVisibility(View.INVISIBLE);
        noInternetText.setVisibility(View.INVISIBLE);
        tryAgain.setVisibility(View.INVISIBLE);
        spinnerSP = getActivity().getSharedPreferences("spinnerSP",Context.MODE_PRIVATE);
        sort_list_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        sort_list_adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        gridView= (GridView)view.findViewById(R.id.gridView);
        gridAdapter = new GridAdapter(movies);
        readFavFile();
        gridView.setOnItemClickListener(this);
        return view;
    }
    public void onStart() {
        super.onStart();
    }
    @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.menu_layout, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem item = menu.findItem(R.id.sort);
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setAdapter(sort_list_adapter);
        spinner.setSelection(spinnerSP.getInt("spinnerSelection",0));
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    switch (position){


        case 1:
            new SortUpdate(gridView,gridAdapter,this.getActivity(),progressBar,noInternetText,tryAgain).execute("https://api.themoviedb.org/3/discover/movie?sort_by=vote_count.desc&api_key="+BuildConfig.API_KEY);
        break;
        case 0:case 2:
            new SortUpdate(gridView,gridAdapter,this.getActivity(),progressBar,noInternetText,tryAgain).execute("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+BuildConfig.API_KEY);
        break;
        case 3:
            readFavFile();
            new Favorite(gridView,gridAdapter,this.getActivity(),progressBar,fav_ids,noInternetText,tryAgain).execute();
        break;
    }
        SharedPreferences.Editor spinnerSP_editor = spinnerSP.edit();
        spinnerSP_editor.putInt("spinnerSelection", spinner.getSelectedItemPosition());
        spinnerSP_editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GridAdapter.Viewholder viewholder = (GridAdapter.Viewholder) view.getTag();
        Movie movie = (Movie) viewholder.title.getTag();
        int screenSize=getResources().getInteger(R.integer.screen_size);
        if(screenSize<600){

            Intent intent = new Intent(view.getContext(),DetailActivity.class);
            intent.putExtra("title",movie.title);
            intent.putExtra("poster",movie.poster);
            intent.putExtra("overview",movie.overview);
            intent.putExtra("lang",movie.lang);
            intent.putExtra("vote_count",movie.vote_count);
            intent.putExtra("rate",movie.rate);
            intent.putExtra("date",movie.date);
            intent.putExtra("id",movie.id);
            readFavFile();
            intent.putExtra("fav_ids",fav_ids);

            startActivity(intent);
        }
        else{

            EventBus.getDefault().post(new TabletMovieClicked(movie.title,movie.poster,movie.overview,
                    movie.lang,movie.vote_count,movie.rate,movie.date,movie.id,fav_ids));
        }

    }
    void readFavFile (){
        FileInputStream favFileInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        try {
            fav_ids.clear();
            String line;
            favFileInputStream= getActivity().openFileInput("fav_json_file");
            inputStreamReader=new InputStreamReader(favFileInputStream);
            bufferedReader=new BufferedReader(inputStreamReader);
            while((line=bufferedReader.readLine())!=null){
                fav_ids.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
