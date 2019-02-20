package com.app.shovelerapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.doc.Globals;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Administrator on 4/7/2017.
 */
public class ImageShowActivity extends AppCompatActivity {

    public ImageView imgSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageshow);
        String path = getIntent().getStringExtra("path");
        imgSlide = (ImageView) this.findViewById(R.id.imgLargeImage);

        Glide.with(this).load(path).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(imgSlide);


    }
}
