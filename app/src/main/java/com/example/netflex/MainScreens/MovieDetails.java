package com.example.netflex.MainScreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.netflex.R;

public class MovieDetails extends AppCompatActivity {
    ImageView movieImage;
    TextView movieName;
    Button Play;
    String name, image, fileURL, moviesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        assignIDs();

        getIntentContent();

        Glide.with(this).load(image).into(movieImage);
        movieName.setText(name);

        Play.setOnClickListener(view -> {
            Intent i = new Intent(MovieDetails.this, VideoPlayer.class);
            i.putExtra("url", fileURL);
            startActivity(i);
        });


    }

    private void getIntentContent() {
        moviesID = getIntent().getStringExtra("movieId");
        name = getIntent().getStringExtra("movieName");
        image = getIntent().getStringExtra("movieImageUrl");
        fileURL = getIntent().getStringExtra("movieFile");
    }

    private void assignIDs() {
        movieImage = findViewById(R.id.imagedeatils);
        movieName = findViewById(R.id.moviename);
        Play = findViewById(R.id.playbutton);
    }


}