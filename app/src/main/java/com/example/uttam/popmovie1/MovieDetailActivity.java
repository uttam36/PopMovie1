package com.example.uttam.popmovie1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvRatings;
    private TextView tvDate;
    private TextView eOverview;
    private ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRatings = (TextView) findViewById(R.id.tv_ratings);
        tvDate = (TextView) findViewById(R.id.tv_date);
        eOverview = (TextView) findViewById(R.id.eOverview);
        ivPoster = (ImageView) findViewById(R.id.Iv_poster);

        Intent intent = getIntent();
        int position = Integer.parseInt(intent.getStringExtra(Intent.EXTRA_TEXT));


        Picasso.with(this)
                .load(ImageAdapter.image_url[position])
                .into(ivPoster);
        tvTitle.setText(ImageAdapter.Data[position][0]);
        tvRatings.setText("Ratings: "+ImageAdapter.Data[position][1]);
        tvDate.setText(ImageAdapter.Data[position][2]);
        eOverview.setText(ImageAdapter.Data[position][3]);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
