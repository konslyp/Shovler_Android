package com.app.shovelerapp.activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.FinishJobCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by supriya.n on 16-06-2016.
 */
public class UploadPhotoNotification extends AppCompatActivity implements FinishJobCallback{
    private ImageView mUpload1,mUpload2,mUpload3;
    private Button mSubmitButton;
    private EditText mDescEditText;
    private String jid,ajuid;
    private int flag = 0;
    private File destination = null;
    private ProgressDialog mProgressDialog;
    ArrayList<File> files=new ArrayList<File>();
    private SharedPrefClass prefClass;

    private ImageView mSubLogo;
    private TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.shoveler);
        mTitle.setText("COMPLETED JOB PICTURES");


        mSubmitButton= (Button) findViewById(R.id.submit_pictures);
        mDescEditText= (EditText) findViewById(R.id.comments_et);
        mUpload1= (ImageView) findViewById(R.id.upload_image1);
        mUpload2= (ImageView) findViewById(R.id.upload_image2);
        mUpload3= (ImageView) findViewById(R.id.upload_image3);

        prefClass=new SharedPrefClass(UploadPhotoNotification.this);

        jid=getIntent().getExtras().getString("jid");
        ajuid=getIntent().getExtras().getString("ajuid");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size()<=0){
                   Constants.showAlert(UploadPhotoNotification.this,"Please upload at least one image.");
                }else {

                    callFinishJob();
                }

            }


        });

        mUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                Constants.selectImage(UploadPhotoNotification.this);
            }
        });

        mUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Constants.selectImage(UploadPhotoNotification.this);
            }
        });

        mUpload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                Constants.selectImage(UploadPhotoNotification.this);
            }
        });

    }

    private void callFinishJob() {
        mProgressDialog = ProgressDialog.show(UploadPhotoNotification.this, "",
                getResources().getString(R.string.loading), true, false);

        HashMap<String,String> map=new HashMap<String, String>();
        map.put("jid",jid);
        map.put("deviceflag","a");
        map.put("scomments",mDescEditText.getText().toString());
        map.put("ajuid",prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
        NetUtils.CallFinishJob(map,files, this, UploadPhotoNotification.this);
    }

    private void showJobDoneFragment() {
        TextView mOk;
        final Dialog dialog=new Dialog(UploadPhotoNotification.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_image_sent);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#df8cc63e")));
        mOk= (TextView) dialog.findViewById(R.id.ok_tv);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(UploadPhotoNotification.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == Constants.REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {

                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                Log.v("imageFilegallery", "" + destination);

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (flag == 0) {
            mUpload1.setImageBitmap(bm);
            files.add(destination);
        } else if (flag == 1) {
            mUpload2.setImageBitmap(bm);
            files.add(destination);
        } else if (flag == 2) {
            mUpload3.setImageBitmap(bm);
            files.add(destination);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        Log.v("imageFilecamera", "" + destination);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (flag == 0) {
            mUpload1.setImageBitmap(thumbnail);
            files.add(destination);
        } else if (flag == 1) {
            mUpload2.setImageBitmap(thumbnail);
            files.add(destination);
        } else if (flag == 2) {
            mUpload3.setImageBitmap(thumbnail);
            files.add(destination);
            /*drivewayPicEntity.setPic3(destination.getPath());
            details.getJobdetails().getDriveway().setPictures(drivewayPicEntity);*/
        }
    }

    @Override
    public void CallGetPriceCallbackSuccess(String success) {
        mProgressDialog.dismiss();
        try {
            JSONObject jsonObject=new JSONObject(success);
            if (jsonObject.getString("status").equals("true")){
                showJobDoneFragment();
            }else {
                String msg=jsonObject.getString("items");
                Constants.showAlert(UploadPhotoNotification.this,msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CallGetPriceCallbackError(String error) {
        mProgressDialog.dismiss();
        Constants.showAlert(UploadPhotoNotification.this,error);
    }
}
