package com.app.shovelerapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.FAQAdapter;
import com.app.shovelerapp.callback.callStaticAPI;
import com.app.shovelerapp.model.FAQModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.ExpandableListUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by supriya.n on 27-08-2016.
 */
public class FAQActivity extends AppCompatActivity implements callStaticAPI/*, View.OnClickListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener */ {
    private ImageView mSubLogo;
    private TextView mTitle;
    private ProgressDialog progressDialog;

    private ScrollView mScrollView;
    ExpandableListView busi_expandableListView, car_expandableListView, home_expandableListView, general_expandableListView;
    FAQAdapter expandableListAdapter;
    FAQAdapter generalexpandableListAdapter;
    FAQAdapter carexpandableListAdapter;
    FAQAdapter homeexpandableListAdapter;
    FAQAdapter shovelerexpandableListAdapter;
    TextView mCar, mHome, mBusiness, mGeneral;
    private HashMap<String, List<String>> generalexpandableListDetail = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> carexpandableListDetail = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> busiexpandableListDetail = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> homeexpandableListDetail = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> shovelerexpandableListDetail = new HashMap<String, List<String>>();

    private int clickedPosition = 1;
    private int childCount = 0;
    private ExpandableListView shoveler_expandableListView;
    private TextView mShoveler;
    private int genLastExpandedPosition = -1;
    private int carLastExpandedPosition = -1;
    private int homeLastExpandedPosition = -1;
    private int busLastExpandedPosition = -1;
    private int shovLastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_faq);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("FAQ");

        /*mSubLogo.setImageResource(R.drawable.configuration);
        mTitle.setText("FAQ");*/

        callFAQ();

        mScrollView = (ScrollView) findViewById(R.id.sv_faq);
        general_expandableListView = (ExpandableListView) findViewById(R.id.general_expandableListView);
        car_expandableListView = (ExpandableListView) findViewById(R.id.car_expandableListView);
        home_expandableListView = (ExpandableListView) findViewById(R.id.home_expandableListView);
        busi_expandableListView = (ExpandableListView) findViewById(R.id.busi_expandableListView);
        shoveler_expandableListView = (ExpandableListView) findViewById(R.id.shoveler_expandableListView);

        mGeneral = (TextView) findViewById(R.id.general);
        mCar = (TextView) findViewById(R.id.car);
        mHome = (TextView) findViewById(R.id.home);
        mBusiness = (TextView) findViewById(R.id.business);
        mShoveler = (TextView) findViewById(R.id.shoveler);


        general_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (genLastExpandedPosition != -1
                        && groupPosition != genLastExpandedPosition) {
                    general_expandableListView.collapseGroup(genLastExpandedPosition);
                }
                genLastExpandedPosition = groupPosition;
            }
        });

        car_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (carLastExpandedPosition != -1
                        && groupPosition != carLastExpandedPosition) {
                    car_expandableListView.collapseGroup(carLastExpandedPosition);
                }
                carLastExpandedPosition = groupPosition;
            }
        });

        home_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (homeLastExpandedPosition != -1
                        && groupPosition != homeLastExpandedPosition) {
                    home_expandableListView.collapseGroup(homeLastExpandedPosition);
                }
                homeLastExpandedPosition = groupPosition;
            }
        });

        busi_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (busLastExpandedPosition != -1
                        && groupPosition != busLastExpandedPosition) {
                    busi_expandableListView.collapseGroup(busLastExpandedPosition);
                }
                busLastExpandedPosition = groupPosition;

//                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) busi_expandableListView.getLayoutParams();
//                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                busi_expandableListView.setLayoutParams(param);
//                busi_expandableListView.refreshDrawableState();
//                mScrollView.refreshDrawableState();
            }
        });

        shoveler_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (shovLastExpandedPosition != -1
                        && groupPosition != shovLastExpandedPosition) {
                    shoveler_expandableListView.collapseGroup(shovLastExpandedPosition);
                }
                shovLastExpandedPosition = groupPosition;
            }
        });
    }

    private void callFAQ() {
        progressDialog = ProgressDialog.show(FAQActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallFAQ(this, FAQActivity.this);
    }

    @Override
    public void callStaticAPISuccess(String success) {
        progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                FAQModel faqModel = null;


                JSONObject faqObject = jsonArray.getJSONObject(0);

                try {
                    JSONArray busiArray = faqObject.getJSONArray("Business");
                    int businessCount = busiArray.length();
                    List<String> expandableListTitle = new ArrayList<String>();

                    ArrayList<FAQModel> faqModels = new ArrayList<FAQModel>();
                    for (int i = 0; i < businessCount; i++) {
                        List<String> expandableListAns = new ArrayList<String>();
                        faqModel = new FAQModel();
                        JSONObject busObject = busiArray.getJSONObject(i);
                        faqModel.setQue(busObject.getString("Que"));
                        expandableListTitle.add(busObject.getString("Que"));
                        faqModel.setAns(busObject.getString("Ans"));
                        expandableListAns.add(busObject.getString("Ans"));

                        busiexpandableListDetail.put(busObject.getString("Que"), expandableListAns);
                        faqModels.add(faqModel);
                    }

                   /* faqModel.setQue(faqObject.getString("Que"));
                    expandableListTitle.add(faqObject.getString("Que"));
                    faqModel.setAns(faqObject.getString("Ans"));
                    expandableListAns.add(faqObject.getString("Ans"));*/
                    expandableListAdapter = new FAQAdapter(this, expandableListTitle, busiexpandableListDetail);
                    busi_expandableListView.setAdapter(expandableListAdapter);
                    ExpandableListUtility.setListViewHeightBasedOnChildren(busi_expandableListView);
                } catch (JSONException e) {
                    e.printStackTrace();
                    busi_expandableListView.setVisibility(View.GONE);
                    mBusiness.setVisibility(View.GONE);
                }

                try {
                    List<String> expandableListTitle = new ArrayList<String>();

                    ArrayList<FAQModel> faqModels = new ArrayList<FAQModel>();
                    JSONArray carArray = faqObject.getJSONArray("Car");
                    int carCount = carArray.length();
                    for (int i = 0; i < carCount; i++) {
                        List<String> expandableListAns = new ArrayList<String>();
                        faqModel = new FAQModel();
                        JSONObject carObject = carArray.getJSONObject(i);
                        faqModel.setQue(carObject.getString("Que"));
                        expandableListTitle.add(carObject.getString("Que"));
                        faqModel.setAns(carObject.getString("Ans"));
                        expandableListAns.add(carObject.getString("Ans"));

                        carexpandableListDetail.put(carObject.getString("Que"), expandableListAns);
                        faqModels.add(faqModel);

                    }
                   /* faqModel.setQue(faqObject.getString("Que"));
                    expandableListTitle.add(faqObject.getString("Que"));
                    faqModel.setAns(faqObject.getString("Ans"));
                    expandableListAns.add(faqObject.getString("Ans"));*/
                    carexpandableListAdapter = new FAQAdapter(this, expandableListTitle, carexpandableListDetail);
                    car_expandableListView.setAdapter(carexpandableListAdapter);
                    ExpandableListUtility.setListViewHeightBasedOnChildren(car_expandableListView);
                } catch (JSONException e) {
                    e.printStackTrace();
                    car_expandableListView.setVisibility(View.GONE);
                    mCar.setVisibility(View.GONE);
                }


                try {
                    JSONArray generalArray = faqObject.getJSONArray("General");
                    int generalCount = generalArray.length();
                    List<String> expandableListTitle = new ArrayList<String>();
                    ArrayList<FAQModel> faqModels = new ArrayList<FAQModel>();

                    expandableListTitle.clear();
                    for (int i = 0; i < generalCount; i++) {
                        List<String> expandableListAns = new ArrayList<String>();
                        faqModel = new FAQModel();
                        JSONObject generalObject = generalArray.getJSONObject(i);
                        faqModel.setQue(generalObject.getString("Que"));
                        expandableListTitle.add(generalObject.getString("Que"));
                        faqModel.setAns(generalObject.getString("Ans"));
                        expandableListAns.add(generalObject.getString("Ans"));

                        generalexpandableListDetail.put(generalObject.getString("Que"), expandableListAns);
                        faqModels.add(faqModel);
                    }

                   /* faqModel.setQue(faqObject.getString("Que"));
                    expandableListTitle.add(faqObject.getString("Que"));
                    faqModel.setAns(faqObject.getString("Ans"));
                    expandableListAns.add(faqObject.getString("Ans"));*/
                    generalexpandableListAdapter = new FAQAdapter(this, expandableListTitle, generalexpandableListDetail);
                    general_expandableListView.setAdapter(generalexpandableListAdapter);
                    ExpandableListUtility.setListViewHeightBasedOnChildren(general_expandableListView);
                } catch (JSONException e) {
                    e.printStackTrace();
                    general_expandableListView.setVisibility(View.GONE);
                    mGeneral.setVisibility(View.GONE);
                }

                try {
                    List<String> expandableListTitle = new ArrayList<String>();

                    JSONArray homeArray = faqObject.getJSONArray("Home");
                    int homeCount = homeArray.length();
                    ArrayList<FAQModel> faqModels = new ArrayList<FAQModel>();
                    expandableListTitle.clear();
                    for (int i = 0; i < homeArray.length(); i++) {
                        List<String> expandableListAns = new ArrayList<String>();
                        faqModel = new FAQModel();
                        JSONObject homeObject = homeArray.getJSONObject(i);
                        faqModel.setQue(homeObject.getString("Que"));
                        expandableListTitle.add(homeObject.getString("Que"));
                        faqModel.setAns(homeObject.getString("Ans"));
                        expandableListAns.add(homeObject.getString("Ans"));

                        homeexpandableListDetail.put(homeObject.getString("Que"), expandableListAns);
                        faqModels.add(faqModel);
                    }
                   /* faqModel.setQue(faqObject.getString("Que"));
                    expandableListTitle.add(faqObject.getString("Que"));
                    faqModel.setAns(faqObject.getString("Ans"));
                    expandableListAns.add(faqObject.getString("Ans"));*/
                    homeexpandableListAdapter = new FAQAdapter(this, expandableListTitle, homeexpandableListDetail);
                    home_expandableListView.setAdapter(homeexpandableListAdapter);
                    ExpandableListUtility.setListViewHeightBasedOnChildren(home_expandableListView);
                } catch (JSONException e) {
                    e.printStackTrace();
                    home_expandableListView.setVisibility(View.GONE);
                    mHome.setVisibility(View.GONE);
                }

                try {
                    List<String> expandableListTitle = new ArrayList<String>();

                    JSONArray shovelerArray = faqObject.getJSONArray("Shovelers");
                    int shovelerCount = shovelerArray.length();
                    ArrayList<FAQModel> faqModels = new ArrayList<FAQModel>();
                    expandableListTitle.clear();
                    for (int i = 0; i < shovelerCount; i++) {
                        List<String> expandableListAns = new ArrayList<String>();
                        faqModel = new FAQModel();
                        JSONObject shovelerObject = shovelerArray.getJSONObject(i);
                        faqModel.setQue(shovelerObject.getString("Que"));
                        expandableListTitle.add(shovelerObject.getString("Que"));
                        faqModel.setAns(shovelerObject.getString("Ans"));
                        expandableListAns.add(shovelerObject.getString("Ans"));

                        shovelerexpandableListDetail.put(shovelerObject.getString("Que"), expandableListAns);
                        faqModels.add(faqModel);
                    }
                   /* faqModel.setQue(faqObject.getString("Que"));
                    expandableListTitle.add(faqObject.getString("Que"));
                    faqModel.setAns(faqObject.getString("Ans"));
                    expandableListAns.add(faqObject.getString("Ans"));*/
                    shovelerexpandableListAdapter = new FAQAdapter(this, expandableListTitle, shovelerexpandableListDetail);
                    shoveler_expandableListView.setAdapter(shovelerexpandableListAdapter);
                    ExpandableListUtility.setListViewHeightBasedOnChildren(shoveler_expandableListView);
                } catch (JSONException e) {
                    e.printStackTrace();
                    shoveler_expandableListView.setVisibility(View.GONE);
                    mShoveler.setVisibility(View.GONE);
                }

            } else {
                Constants.showAlert(FAQActivity.this, "No data found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callStaticAPIError(String error) {
        progressDialog.dismiss();
        Constants.showAlert(FAQActivity.this, error);
    }

//    @Override
//    public void onGroupCollapse(int groupPosition) {
//        ExpandableListView tempExpandableListView = null;
//        if (clickedPosition == 1) {
//            tempExpandableListView = general_expandableListView;
//            childCount = general_expandableListView.getChildCount();
//        }
//        else if (clickedPosition == 2)
//            tempExpandableListView = car_expandableListView;
//        else if (clickedPosition == 3)
//            tempExpandableListView = home_expandableListView;
//        else if (clickedPosition == 4)
//            tempExpandableListView = busi_expandableListView;
//        else if (clickedPosition == 5)
//            tempExpandableListView = shoveler_expandableListView;
//
//        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tempExpandableListView.getLayoutParams();
//        param.height = (childCount * tempExpandableListView.getHeight());
//        tempExpandableListView.setLayoutParams(param);
//        tempExpandableListView.refreshDrawableState();
//        mScrollView.refreshDrawableState();
//    }

//    @Override
//    public void onGroupExpand(int groupPosition) {
//        ExpandableListView tempExpandableListView = null;
//        if (clickedPosition == 1) {
//            tempExpandableListView = general_expandableListView;
//            childCount = general_expandableListView.getChildCount();
//        }
//        else if (clickedPosition == 2)
//            tempExpandableListView = car_expandableListView;
//        else if (clickedPosition == 3)
//            tempExpandableListView = home_expandableListView;
//        else if (clickedPosition == 4)
//            tempExpandableListView = busi_expandableListView;
//        else if (clickedPosition == 5)
//            tempExpandableListView = shoveler_expandableListView;
//
//        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tempExpandableListView.getLayoutParams();
//        param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        tempExpandableListView.setLayoutParams(param);
//        tempExpandableListView.refreshDrawableState();
//        mScrollView.refreshDrawableState();
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.general_expandableListView:
//                clickedPosition = 1;
//                childCount = general_expandableListView.getChildCount();
//                break;
//
//            case R.id.car_expandableListView:
//                clickedPosition = 2;
//                break;
//
//            case R.id.home_expandableListView:
//                clickedPosition = 3;
//                break;
//
//            case R.id.busi_expandableListView:
//                clickedPosition = 4;
//                break;
//
//            case R.id.shoveler_expandableListView:
//                clickedPosition = 5;
//                break;
//        }
//    }
}
