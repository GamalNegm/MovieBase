package com.mal.project.gridview;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mal.gridview.gridview.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideosListAdapter extends RecyclerView.Adapter<VideosListAdapter.VideoHolder> {

    List<String> videosLinks=new ArrayList<>();

    ViewGroup parent;
    public VideosListAdapter(List<String> videosLinks) {
        this.videosLinks.clear();
        this.videosLinks = videosLinks;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent=parent;
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row,parent,false);
        VideoHolder holder = new VideoHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        
        Picasso.with(parent.getContext())
                .load("http://img.youtube.com/vi/"+videosLinks.get(position)+"/0.jpg")
                .resize(250,250)
                .into(holder.poster);
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("http://www.youtube.com/watch?v=" +videosLinks.get(position)));
                parent.getContext().startActivity(intent1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videosLinks.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        ImageView poster;

        public VideoHolder(View itemView) {
            super(itemView);

            poster = (ImageView) itemView.findViewById(R.id.video_cover);
        }
    }


}



