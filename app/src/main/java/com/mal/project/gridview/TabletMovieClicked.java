package com.mal.project.gridview;

import java.util.ArrayList;

/**
 * Created by S1ckSec on 31/08/2016.
 */

public class TabletMovieClicked {
    String posterUrl,title,overview,lang,vote_count,rate,date,id;
    ArrayList<String> fav_ids=new ArrayList<>();
    public TabletMovieClicked(String title, String posterUrl, String overview, String lang, String vote_count, String rate, String date, String id, ArrayList<String> fav_ids){
        this.title=title;
        this.posterUrl=posterUrl;
        this.overview=overview;
        this.lang=lang;
        this.vote_count=vote_count;
        this.rate=rate;
        this.date=date;
        this.id=id;
        this.fav_ids=fav_ids;
    }
}
