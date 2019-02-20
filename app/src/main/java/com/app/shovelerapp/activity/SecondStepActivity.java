package com.app.shovelerapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.MyFilterableAdapter;
import com.app.shovelerapp.callback.CallGetPriceCallback;
import com.app.shovelerapp.callback.GetBusinessPriceCallback;
import com.app.shovelerapp.callback.GetCarDetailsCallBack;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.LineColorPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by supriya.n on 25-07-2016.
 */
public class SecondStepActivity extends AppCompatActivity implements GetCarDetailsCallBack, CallGetPriceCallback, GetBusinessPriceCallback {
    private LineColorPicker colorPicker;

    private LinearLayout mCarLayout, mHomeLayout, mBusinessLayout;
    private ScrollView scrollView;
    private Button msecondContButton;
    private ImageView mAddInstructionImageView;
    private TextView faqTextView;
    private EditText mAddInstructionEditText, mPlateNumber, mSizeEditText;
    private AutoCompleteTextView mModelEdit, mStateEditText;
    private ImageView mCarUpload, mHomeUpload, mBusinessUpload;
    private TextView mPriceTextView, mHomeTextView;
    private ImageView mSubLogo;
    private TextView mTitle, mHomePrice;
    private File destination = null;
    private ProgressDialog mProgressDilog;
    private ImageView mClose;
    private ArrayList<String> modelList = new ArrayList<String>();
    private ArrayList<String> stateList = new ArrayList<String>();
    MyFilterableAdapter adapter;
    // private String[] modelList1 = null;
    private String[] stateList1 = null;
    private ArrayList<String> colorList = new ArrayList<String>();
    //private ArrayAdapter<String> adapter;
    int modelFlag = 0, stateFlag = 0;

    private int flag = 0;
    private TextView sizeRestrictionTextView;

    /*private JobDetails details = new JobDetails();*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set flag for open Third step
        Constants.JOB_STATUS_DETAILS_FLAG=false;

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.ic_action_request);
        mTitle.setText("REQUEST");


        Log.v("JOB_DETAILS", "" + FirstStepActivity.details);

        colorPicker = (LineColorPicker) findViewById(R.id.picker);

        mCarLayout = (LinearLayout) findViewById(R.id.car_layout);
        mHomeLayout = (LinearLayout) findViewById(R.id.home_layout);
        mBusinessLayout = (LinearLayout) findViewById(R.id.business_layout);

        mModelEdit = (AutoCompleteTextView) findViewById(R.id.model_edit);
        mPlateNumber = (EditText) findViewById(R.id.license_edit);
        mStateEditText = (AutoCompleteTextView) findViewById(R.id.state_name);
        mSizeEditText = (EditText) findViewById(R.id.size_editText);

        mPriceTextView = (TextView) findViewById(R.id.price_tv);
        mHomePrice = (TextView) findViewById(R.id.home_price);
        mHomeTextView = (TextView) findViewById(R.id.home_price);
        faqTextView= (TextView) findViewById(R.id.faq);
        sizeRestrictionTextView= (TextView) findViewById(R.id.tv_size_restrictions_apply);

        faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondStepActivity.this, NewFAQActivity.class));
            }
        });


        sizeRestrictionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondStepActivity.this, NewFAQActivity.class));
            }
        });

        mCarUpload = (ImageView) findViewById(R.id.upload_image);
        mHomeUpload = (ImageView) findViewById(R.id.upload_image1);
        mBusinessUpload = (ImageView) findViewById(R.id.upload_image2);
        mClose = (ImageView) findViewById(R.id.close_btn);
        scrollView= (ScrollView) findViewById(R.id.scroll);

        msecondContButton = (Button) findViewById(R.id.second_cont);

        mAddInstructionImageView = (ImageView) findViewById(R.id.add_instruction);

        mAddInstructionEditText = (EditText) findViewById(R.id.extra_instruction_edit);

        setFont();

        callGetPrice();

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModelEdit.setText("");
            }
        });

        faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SecondStepActivity.this,FAQActivity.class));
                startActivity(new Intent(SecondStepActivity.this,NewFAQActivity.class));
            }
        });


        mStateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter = new MyFilterableAdapter(SecondStepActivity.this, stateList);
                //adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.items, stateList);
                mStateEditText.setAdapter(adapter);
                mStateEditText.setThreshold(0);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mModelEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter = new MyFilterableAdapter(SecondStepActivity.this, modelList);
                //adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.items, modelList);
                mModelEdit.setAdapter(adapter);
                mModelEdit.setThreshold(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mStateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //onCreateDialogSingleChoice(stateList, "Select State", mStateEditText).show();

                final AlertDialog.Builder chooseDrinkdialog = new AlertDialog.Builder(
                        SecondStepActivity.this);
                View titleview = getLayoutInflater().inflate(R.layout.alert_title, null);
                TextView title1 = (TextView) titleview.findViewById(R.id.dialogtitle);
                title1.setText("Select Model");
                chooseDrinkdialog.setCustomTitle(titleview);
                chooseDrinkdialog.setSingleChoiceItems(stateList1, stateFlag,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                stateFlag = item;
                                mStateEditText.setText(stateList1[item]);
                                dialog.dismiss();
                            }
                        });
*/
                //chooseDrinkdialog.show();
            }
        });


        if (FirstStepActivity.details.getJobDetails().getCar() != null) {
            mCarLayout.setVisibility(View.VISIBLE);
            mHomeLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);
            callGetCarDetailsApi();
        } else if (FirstStepActivity.details.getJobDetails().getHome() != null) {
            mHomeLayout.setVisibility(View.VISIBLE);
            mCarLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);
        } else {
            mBusinessLayout.setVisibility(View.VISIBLE);
            mCarLayout.setVisibility(View.GONE);
            mHomeLayout.setVisibility(View.GONE);
        }


        mCarUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                Constants.selectImage(SecondStepActivity.this);
            }
        });

        mHomeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Constants.selectImage(SecondStepActivity.this);
            }
        });

        mBusinessUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                Constants.selectImage(SecondStepActivity.this);
            }
        });


        mAddInstructionImageView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (mAddInstructionImageView.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.add).getConstantState())) {
                    mAddInstructionEditText.setVisibility(View.VISIBLE);
                    mAddInstructionImageView.setBackground(getResources().getDrawable(R.drawable.minus));

                    scrollView.post(new Runnable()
                    {
                        public void run()
                        {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } else {
                    mAddInstructionEditText.setVisibility(View.GONE);
                    mAddInstructionEditText.setText("");
                    mAddInstructionImageView.setBackground(getResources().getDrawable(R.drawable.add));
                }

            }
        });

        msecondContButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirstStepActivity.details.getJobDetails().setDesc(mAddInstructionEditText.getText().toString());

                if (FirstStepActivity.details.getJobDetails().getCar() != null) {
                    if (mModelEdit.getText().toString().equals("")) {
                        showSnackbar(mModelEdit, getResources().getString(R.string.enter_model));
                    //} else if(!modelList.contains(mModelEdit.getText().toString())){
                    //    showSnackbar(mModelEdit, getResources().getString(R.string.enter_valid_model));
                    }else if (mStateEditText.getText().toString().equals("")) {
                        showSnackbar(mStateEditText, getResources().getString(R.string.enter_state));
                    }else if(!stateList.contains(mStateEditText.getText().toString())){
                        showSnackbar(mStateEditText, getResources().getString(R.string.enter_valid_state));
                    } else if (mPlateNumber.getText().toString().equals("")) {
                        showSnackbar(mPlateNumber, getResources().getString(R.string.enter_plate));
                    }
                    else {
                        FirstStepActivity.details.getJobDetails().getCar().setColor(String.format("#%06X", (0xFFFFFF & colorPicker.getColor())));
                        FirstStepActivity.details.getJobDetails().getCar().setModel(mModelEdit.getText().toString());
                        FirstStepActivity.details.getJobDetails().getCar().setLicense(mPlateNumber.getText().toString());
                        FirstStepActivity.details.getJobDetails().getCar().setPrice(mPriceTextView.getText().toString().replace("$",""));
                        FirstStepActivity.details.getJobDetails().getCar().setState(mStateEditText.getText().toString());
                        CallActivityFun();
                    }
                } else if (FirstStepActivity.details.getJobDetails().getHome() != null) {
                    FirstStepActivity.details.getJobDetails().getHome().setPrice(mHomePrice.getText().toString().replace("$",""));
                    CallActivityFun();
                } else {
                    Double mSize = 0.0;
                    try {
                        mSize = Double.valueOf(mSizeEditText.getText().toString());
                    } catch (Exception e) {
                    }

                    if (mSizeEditText.getText().toString().equals("")) {
                        showSnackbar(mSizeEditText, getResources().getString(R.string.enter_size));
//                    } else if (mSize > 1000) {
//                        showSnackbar(mSizeEditText, getResources().getString(R.string.enter_valid_size));
                    } else {
                        FirstStepActivity.details.getJobDetails().getBusiness().setPrice(mPriceTextView.getText().toString().replace("$",""));
                        FirstStepActivity.details.getJobDetails().getBusiness().setSize(mSizeEditText.getText().toString());

                        callBusinessPriceAPI(mSize);

//                        CallActivityFun();
                    }
                }
            }
        });

    }

    private void callGetPrice() {
        NetUtils.CallGetPrice(this, SecondStepActivity.this);
    }

    private void callGetCarDetailsApi() {
        mProgressDilog = ProgressDialog.show(SecondStepActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetCarDetails(this, SecondStepActivity.this);
    }

    private void callBusinessPriceAPI(Double mSize) {
        mProgressDilog = ProgressDialog.show(SecondStepActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetBusinessPrice(String.valueOf(mSize), this, SecondStepActivity.this);
    }

    @Override
    public void GetBusinessPriceCallbackSuccess(String success) {
        mProgressDilog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONObject object = jsonObject.getJSONObject("items");
                Constants.BUSINESS_PRICE = object.getString("businessprice");

//                mPriceTextView.setText("$ " + );

                CallActivityFun();
            }else{
                String error = jsonObject.getString("items");
                Constants.showAlert(SecondStepActivity.this, error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void GetBusinessPriceCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(SecondStepActivity.this, error);
    }

    private void CallActivityFun() {
        startActivity(new Intent(SecondStepActivity.this, ThirdStepActivity.class));
    }

    private void showSnackbar(View v, String msg) {
        Snackbar snackbar = Snackbar
                .make(v, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }


    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(SecondStepActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(SecondStepActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(SecondStepActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(SecondStepActivity.this);

        msecondContButton.setTypeface(tfRegular);

        mModelEdit.setTypeface(tfRegular);
        mPlateNumber.setTypeface(tfRegular);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v("here", "onActivityResult" + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == Constants.REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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
            mCarUpload.setImageBitmap(bm);
            FirstStepActivity.details.getJobDetails().getCar().setUrl(destination.getPath());
            FirstStepActivity.details.getJobDetails().getCar().setFile(destination);
        } else if (flag == 1) {
            mHomeUpload.setImageBitmap(bm);
            FirstStepActivity.details.getJobDetails().getHome().setUrl(destination.getPath());
            FirstStepActivity.details.getJobDetails().getHome().setFile(destination);
        } else if (flag == 2) {
            mBusinessUpload.setImageBitmap(bm);
            FirstStepActivity.details.getJobDetails().getBusiness().setUrl(destination.getPath());
            FirstStepActivity.details.getJobDetails().getBusiness().setFile(destination);
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
            mCarUpload.setImageBitmap(thumbnail);
            FirstStepActivity.details.getJobDetails().getCar().setUrl(destination.getPath());
            FirstStepActivity.details.getJobDetails().getCar().setFile(destination);
        } else if (flag == 1) {
            mHomeUpload.setImageBitmap(thumbnail);
            FirstStepActivity.details.getJobDetails().getHome().setUrl(destination.getPath());
            FirstStepActivity.details.getJobDetails().getHome().setFile(destination);
        } else if (flag == 2) {
            mBusinessUpload.setImageBitmap(thumbnail);
            FirstStepActivity.details.getJobDetails().getBusiness().setUrl(destination.getPath());
            FirstStepActivity.details.getJobDetails().getBusiness().setFile(destination);
            /*drivewayPicEntity.setPic3(destination.getPath());
            details.getJobdetails().getDriveway().setPictures(drivewayPicEntity);*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Constants.userChoosenTask.equals("Take Photo"))
                        Constants.cameraIntent(SecondStepActivity.this);
                    else if (Constants.userChoosenTask.equals("Choose from Library"))
                        Constants.galleryIntent(SecondStepActivity.this);
                } else {
                    //code for deny
                }
                break;
        }
    }


    @Override
    public void GetCarDetailsCallBackSuccess(String success) {
        mProgressDilog.dismiss();
        JSONArray modelArray = null;
        JSONArray stateArray = null;
        JSONArray colorArray = null;
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONObject object = jsonObject.getJSONObject("items");
                modelArray = object.getJSONArray("model");
                stateArray = object.getJSONArray("state");
                colorArray = object.getJSONArray("color");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //modelList1 = new String[modelArray.length()];

        for (int i = 0; i < modelArray.length(); i++) {
            try {
                modelList.add(modelArray.getJSONObject(i).getString("model"));
                // modelList1[i] = modelArray.getJSONObject(i).getString("model");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        stateList1 = new String[stateArray.length()];

        for (int i = 0; i < stateArray.length(); i++) {
            try {
                stateList.add(stateArray.getJSONObject(i).getString("state"));
                stateList1[i] = stateArray.getJSONObject(i).getString("state");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //adapter.notifyDataSetChanged();

        adapter = new MyFilterableAdapter(SecondStepActivity.this, modelList);
        mModelEdit.setAdapter(adapter);
        //adapter.notifyDataSetChanged();



      /*  modelList = new Gson().fromJson(modelArray.toString(), new TypeToken<List<String>>() {
        }.getType());
        colorList = new Gson().fromJson(stateArray.toString(), new TypeToken<List<String>>() {
        }.getType());
        stateList = new Gson().fromJson(colorArray.toString(), new TypeToken<List<String>>() {
        }.getType());*/
    }

    @Override
    public void GetCarDetailsCallBackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(SecondStepActivity.this, error);
    }

    public void onCreateDialogSingleChoice(final String[] arrayList, String title, final EditText editText) {

    }

    @Override
    public void CallGetPriceCallbackSuccess(String success) {
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (FirstStepActivity.details.getJobDetails().getHome() != null) {
                        mHomePrice.setText("$ " + object.getString("homeprice"));
                    } else if (FirstStepActivity.details.getJobDetails().getBusiness() != null) {
                        mPriceTextView.setText("$ " + object.getString("businessprice"));
                    } else if (FirstStepActivity.details.getJobDetails().getCar() != null) {
                        mPriceTextView.setText("$ " + object.getString("carprice"));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CallGetPriceCallbackError(String error) {
        try {
            mProgressDilog.dismiss();
        } catch (Exception e) {
        }
    }
}

