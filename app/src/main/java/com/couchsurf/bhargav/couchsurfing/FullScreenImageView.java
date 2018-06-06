package com.couchsurf.bhargav.couchsurfing;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullScreenImageView extends AppCompatActivity {
    static Toolbar toolbar;
    String currentImg;
    public static void setTitle(String title){toolbar.setTitle(title);}
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full_screen);

        PhotoView photoView = findViewById(R.id.imgNew);
        currentImg = getIntent().getStringExtra("CURRENT_IMG");
        //Toast.makeText(this, currentImg,Toast.LENGTH_LONG).show();

        toolbar = findViewById(R.id.toolbarImageView);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Image");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Glide.with(getBaseContext()).load(currentImg).into(photoView);

       /* Handler handler
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoView);
        photoViewAttacher.update();*/

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
