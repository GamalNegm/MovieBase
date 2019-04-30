package com.mal.project.gridview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.gridview.gridview.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by S1ckSec on 08/08/2016.
 */
public class GridAdapter extends BaseAdapter {
    List<Movie> movies;
    GridAdapter(List<Movie> movies) {
        this.movies=movies;
    }
    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.movie_row,parent,false);
        Viewholder viewholder = new Viewholder(view);
        Movie movie = movies.get(position);
        viewholder.title.setText(movie.title);
        Picasso.with(parent.getContext()).load(movie.poster).into(viewholder.poster);
        viewholder.title.setTag(movies.get(position));
        view.setTag(viewholder);
        return view;
    }
    class Viewholder {
        TextView title;
        ImageView poster;

        Viewholder(View itemView){
            title= (TextView) itemView.findViewById(R.id.textview) ;
            poster=(ImageView) itemView.findViewById(R.id.flag);
        }

    }
}
