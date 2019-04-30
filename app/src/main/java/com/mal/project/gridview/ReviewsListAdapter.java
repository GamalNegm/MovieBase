package com.mal.project.gridview;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mal.gridview.gridview.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.VideoHolder> {

    List<String> ReviewsBody=new ArrayList<>();

    ViewGroup parent;
    public ReviewsListAdapter(List<String> ReviewsBody) {
        this.ReviewsBody.clear();
        this.ReviewsBody = ReviewsBody;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent=parent;
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row,parent,false);
        VideoHolder holder = new VideoHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        holder.reviewText.setText(ReviewsBody.get(position));
    }

    @Override
    public int getItemCount() {
        return ReviewsBody.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        TextView reviewText;

        public VideoHolder(View itemView) {
            super(itemView);

            reviewText = (TextView) itemView.findViewById(R.id.review_text);
        }
    }


}



