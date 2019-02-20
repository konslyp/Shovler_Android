package com.app.shovelerapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;

/**
 * Created by ApkDev1 on 23-12-2015.
 */
public class ImagePagerAdapter extends PagerAdapter {
    ArrayList<Integer> mImageList=new ArrayList<Integer>();
    private Context mContext;

    public ImagePagerAdapter(ArrayList<Integer> mImageList, Context mContext)
    {
        this.mImageList=mImageList;
        this.mContext=mContext;

        Log.e("size", "" + mImageList.size());
    }

//        private final int[] mImages = new int[] {
//                R.drawable.baby,
//
//        };

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return this.mImageList.size();
    }



    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {


        final ImageView imageView = new ImageView(mContext);
        final int padding = 8;
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

          imageView.setImageResource(mImageList.get(position));



        /*Picasso.with(mContext)
                .load(imagearry.get(position))
//                    .placeholder(R.drawable.ic_facebook)
                .resize( 346,
                        320)
                .placeholder(R.drawable.loading_img)
                .error(android.R.drawable.stat_notify_error)
                .into(imageView);*/


       /* Picasso.with(mContext)
                .load(imagearry.get(position))
                .placeholder(R.drawable.loading_img)
                .into(imageView);*/

        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == ((ImageView) object);
    }
}
