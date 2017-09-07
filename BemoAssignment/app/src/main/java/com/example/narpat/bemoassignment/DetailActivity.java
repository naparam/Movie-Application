package com.example.narpat.bemoassignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.BundleCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.narpat.bemoassignment.adapter.TrailerAdapter;
import com.example.narpat.bemoassignment.api.Client;
import com.example.narpat.bemoassignment.api.Service;
import com.example.narpat.bemoassignment.model.Trailer;
import com.example.narpat.bemoassignment.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by narpat on 30/8/17.
 */

public class DetailActivity extends AppCompatActivity {
    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;
    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        imageView =(ImageView) findViewById(R.id.thumbnail_image_header);
        nameOfMovie =(TextView) findViewById(R.id.title);
        plotSynopsis =(TextView) findViewById(R.id.plotsynopsis);
        userRating =(TextView) findViewById(R.id.userrating);
        releaseDate =(TextView) findViewById(R.id.releasedate);


        Intent intentThatStartedThisActivity=getIntent();
        if (intentThatStartedThisActivity.hasExtra("original_title")){

            String thumbnail = getIntent().getExtras().getString("poster_path");
            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String dateOfRelease = getIntent().getExtras().getString("release_date");


            Glide.with(this)
                    .load(thumbnail)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);


        }else {

            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }
        initViews();





    }
    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){

            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;

                }else if (isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }

        });
    }
    //Trailer
    public void initViews(){
        trailerList =new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        loadJSON();

    }

    private void loadJSON(){
        int movie_id=getIntent().getExtras().getInt("id");
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "PLease use API Key from themoviedb", Toast.LENGTH_SHORT).show();
                return;
            }
            Client Client=new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer =response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(),trailer));
                    recyclerView.smoothScrollToPosition(0);

                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Errror",t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error to fetching the trailer data", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}