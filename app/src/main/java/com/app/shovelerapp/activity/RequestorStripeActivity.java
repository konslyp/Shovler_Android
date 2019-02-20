package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.PromocodeAdapter;
import com.app.shovelerapp.callback.CheckPromoCodeCallback;
import com.app.shovelerapp.callback.GetProfileCallback;
import com.app.shovelerapp.callback.GetPromoCodeCallback;
import com.app.shovelerapp.callback.PostJobCallback;
import com.app.shovelerapp.callback.SaveProfileCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.JobDetails;
import com.app.shovelerapp.model.Promocode;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.stripe.android.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestorStripeActivity extends AppCompatActivity implements SaveProfileCallback,PaymentMethodNonceCreatedListener,BraintreeErrorListener,
        PostJobCallback, GetProfileCallback, GetPromoCodeCallback, CheckPromoCodeCallback ,View.OnClickListener{
    private ImageView mSubLogo;
    private TextView mTitle, mInstructionTextView, mHavePromocode;
    private EditText mCardEditTextEditText, mYearEditText, mMonthEditText, mCVCEditText;
    private Button mSubmit;
    private ProgressDialog mProgressDilog;
    private SharedPrefClass prefClass;
    private String card = "", month, year, cvc = "", promocode = "";
    private JobDetails.JobDetailsEntity jobDetailsEntity = new JobDetails.JobDetailsEntity();
    private File file = new File("");
    private ArrayList<Promocode> promocodeArrayList = new ArrayList<Promocode>();
    private PromocodeAdapter promocodeAdapter;

    private boolean newCardFlag = false;

    public LinearLayout layoutPaypal;
    public LinearLayout layoutCard;
    public RadioButton radPaypal;
    public RadioButton radCard;

    public LinearLayout layoutPaypalInput;
    public LinearLayout layoutCardInput;

    public BraintreeFragment mBraintreeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestor_stripe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("DEPOSIT FUND");

        prefClass = new SharedPrefClass(RequestorStripeActivity.this);

        getUser();
        init();
//        setListeners();

        layoutPaypal = (LinearLayout) this.findViewById(R.id.layoutRequestPaypal);
        layoutCard = (LinearLayout) this.findViewById(R.id.layoutRequestCard);
        radCard = (RadioButton) this.findViewById(R.id.radRequestCard);
        radPaypal = (RadioButton) this.findViewById(R.id.radRequestPaypal);
        layoutCardInput = (LinearLayout) this.findViewById(R.id.layoutCardInfo);

        layoutPaypal.setOnClickListener(this);
        layoutCard.setOnClickListener(this);
        jobDetailsEntity = FirstStepActivity.details.getJobDetails();

        layoutCardInput.setVisibility(View.GONE);


        // TODO: 06-10-2016 Promo code list is removed from dialog. Hence commented code below
//        if (prefClass.getSavedStringPreference(SharedPrefClass.JOB_CNT).equals("0")) {
        mHavePromocode.setVisibility(View.VISIBLE);
//            getPromocode();
//        } else {
//            mHavePromocode.setVisibility(View.GONE);
//        }
        initOption();

        //Add Payment Yujin


        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, Globals.g_clientToken);
            // mBraintreeFragment is ready to use!
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
        }

        ///
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubmit.setEnabled(false);
                Constants.hideKeyboard(RequestorStripeActivity.this, v);
                String monthET = mMonthEditText.getText().toString();
                String cardET = mCardEditTextEditText.getText().toString();
                String cvcET = mCVCEditText.getText().toString();
                String yearET = mYearEditText.getText().toString();

                //Edit by Yujin

//                if ((card.equals("") || month.equals("") || year.equals("") || cvc.equals("")) ||
//                        (!cardET.isEmpty() || !monthET.equals(month) || !yearET.equals(year) || !cvcET.isEmpty())) {
                if ((!cardET.isEmpty() && !monthET.isEmpty() && !yearET.isEmpty() && !cvcET.isEmpty())) {
                    CardBuilder cardBuilder = new CardBuilder()
                            .cardNumber(cardET)
                            .cvv(cvcET)
                            .expirationDate(monthET + "/" + yearET);
                    Card.tokenize(mBraintreeFragment, cardBuilder);


//                    Card ccard;
//                    ccard = new Card(mCardEditTextEditText.getText().toString(), Integer.parseInt(monthET), Integer.parseInt(yearET), mCVCEditText.getText().toString());
//                    if (mCardEditTextEditText.getText().toString().equals("") || !ccard.validateNumber()) {
//                        setErrorFun(mCardEditTextEditText, getResources().getString(R.string.enter_card));
//                    } else if (mMonthEditText.getText().toString().equals("") || !ccard.validateExpMonth()) {
//                        setErrorFun(mMonthEditText, getResources().getString(R.string.enter_month));
//                    } else if (mYearEditText.getText().toString().equals("") || !ccard.validateExpYear()) {
//                        setErrorFun(mYearEditText, getResources().getString(R.string.enter_Year));
//                    } else if (mCVCEditText.getText().toString().equals("") || !ccard.validateCVC()) {
//                        setErrorFun(mCVCEditText, getResources().getString(R.string.enter_CVC));
//                    } else {
//                        card = "";
//                        callSaveCard();
//                    }
                } else {
                    if (mCardEditTextEditText.getText().toString().equals("")) {
                        setErrorFun(mCardEditTextEditText, getResources().getString(R.string.enter_card));
                    } else if (mMonthEditText.getText().toString().equals("")) {
                        setErrorFun(mMonthEditText, getResources().getString(R.string.enter_month));
                    } else if (mYearEditText.getText().toString().equals("")) {
                        setErrorFun(mYearEditText, getResources().getString(R.string.enter_Year));
                    } else if (mCVCEditText.getText().toString().equals("")) {
                        setErrorFun(mCVCEditText, getResources().getString(R.string.enter_CVC));
                    }
                    //callPostJobAPI();
                }
                //callPostJobAPI();
            }
        });

        mHavePromocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProocodeDialog();
            }
        });

    }

    private void getPromocode() {
       /* mProgressDilog = ProgressDialog.show(RequestorStripeActivity.this, "",
                getResources().getString(R.string.loading), true, false);*/
        NetUtils.CallGetPromocode(this, RequestorStripeActivity.this);
    }

    Dialog showPromoCodeDialog = null;


    @Override
    public void onError(Exception error) {
        mSubmit.setEnabled(true);
        setErrorFun(mCardEditTextEditText, getResources().getString(R.string.enter_card1));
        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;
            BraintreeError cardErrors = errorWithResponse.errorFor("creditCard");
            if (cardErrors != null) {
                // There is an issue with the credit card.
                BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    String ss = expirationMonthError.getMessage();
                    //setErrorMessage(expirationMonthError.getMessage());
                }
            }
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        mSubmit.setEnabled(true);
        String nonce = paymentMethodNonce.getNonce();
        if (paymentMethodNonce instanceof PayPalAccountNonce) {
            PayPalAccountNonce paypalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
            PostalAddress billingAddress = paypalAccountNonce.getBillingAddress();
            String streetAddress = billingAddress.getStreetAddress();
            String extendedAddress = billingAddress.getExtendedAddress();
            String locality = billingAddress.getLocality();
            String countryCodeAlpha2 = billingAddress.getCountryCodeAlpha2();
            String postalCode = billingAddress.getPostalCode();
            String region = billingAddress.getRegion();

        }
        callPostJobAPI(nonce);

    }
    private void showProocodeDialog() {
        showPromoCodeDialog = new Dialog(RequestorStripeActivity.this);
        ListView mBankListView;
        Button mCancelButton, mOkButton;
        final EditText mPromoCodeEditText;
        //BankAdapter bankAdapter;
        showPromoCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showPromoCodeDialog.setContentView(R.layout.promo_code_dilog);
        showPromoCodeDialog.setTitle("");

        mBankListView = (ListView) showPromoCodeDialog.findViewById(R.id.promocode_list);
        mPromoCodeEditText = (EditText) showPromoCodeDialog.findViewById(R.id.promocode_edit);
        mCancelButton = (Button) showPromoCodeDialog.findViewById(R.id.cancel);
        mOkButton = (Button) showPromoCodeDialog.findViewById(R.id.ok);

        promocodeAdapter = new PromocodeAdapter(RequestorStripeActivity.this, promocodeArrayList);
        mBankListView.setAdapter(promocodeAdapter);
        mBankListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mBankListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*str_selected_bank = bankNames.get(position).getBank_code();
                Log.v("str_selected_bank",""+str_selected_bank);*/
                promocode = promocodeArrayList.get(position).getPcode();
                mPromoCodeEditText.setText(promocode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                promocode = promocodeArrayList.get(position).getPcode();
                mPromoCodeEditText.setText(promocode);
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 19-10-2016 Check valid if yes do below else show alert
                promocode = mPromoCodeEditText.getText().toString();
                if (!promocode.trim().isEmpty()) {
                    NetUtils.CallCheckPromocode(promocode,
                            prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),
                            RequestorStripeActivity.this, RequestorStripeActivity.this);
                } else {
                    Toast.makeText(RequestorStripeActivity.this, "Please enter valid Promo Code!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPromoCodeDialog.dismiss();
            }
        });
        // set the custom dialog components - text, image and button

        showPromoCodeDialog.show();
    }

    private void getUser() {
        mProgressDilog = ProgressDialog.show(RequestorStripeActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetProfile(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), this, RequestorStripeActivity.this);
    }

    private void callSaveCard() {
        mProgressDilog = ProgressDialog.show(RequestorStripeActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        mSubmit.setEnabled(true);
        if (card.equals("")) {
            NetUtils.CallSaveProfile(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), mCardEditTextEditText.getText().toString(), mMonthEditText.getText().toString(), mYearEditText.getText().toString(), mCVCEditText.getText().toString(), this, RequestorStripeActivity.this);
        } else {
            NetUtils.CallSaveProfile(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), card, mMonthEditText.getText().toString(), mYearEditText.getText().toString(), cvc, this, RequestorStripeActivity.this);
        }

    }

    private void init() {
        mCardEditTextEditText = (EditText) findViewById(R.id.card_number);
        mYearEditText = (EditText) findViewById(R.id.year_id);
        mMonthEditText = (EditText) findViewById(R.id.month_id);
        mCVCEditText = (EditText) findViewById(R.id.cvc);
        mSubmit = (Button) findViewById(R.id.submit);
        mInstructionTextView = (TextView) findViewById(R.id.instru_text);
        mHavePromocode = (TextView) findViewById(R.id.have_promocode);
    }

    private void setListeners() {
        mCardEditTextEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardEditTextEditText.getText().toString().equals("xxxxxxxxxxxxxxxxx")) {
                    mCardEditTextEditText.setSelection(0);
                    newCardFlag = true;
                }
            }
        });

        mCardEditTextEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (mCardEditTextEditText.getText().toString().equals("xxxxxxxxxxxxxxxxx")) {
                        mCardEditTextEditText.setSelection(0);
                        newCardFlag = true;
                    }
                }

                return false;
            }
        });

        mCardEditTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (=.getText().toString().equals("xxxxxxxxxxxxxxxxx")){
//                    newCardFlag = true;
//                    mCardEditTextEditText.setText("");
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (newCardFlag) {
                    newCardFlag = false;
                    String firstChar = String.valueOf(s.charAt(0));
                    mCardEditTextEditText.setText(firstChar);
                    mCardEditTextEditText.setSelection(1);
//                        mCardEditTextEditText.setText(firstChar);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mCVCEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCVCEditText.getText().toString().equals("xxx")){
//                    newCardFlag = true;
//                }
//            }
//        });
//
//        mCVCEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////                if (newCardFlag){
////                    mCVCEditText.setText("");
////                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (newCardFlag){
//                    mCVCEditText.setText("");
////                    mCVCEditText.setText(s.toString());
//                    newCardFlag = false;
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        mSubmit.setEnabled(true);
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }

    @Override
    public void SaveProfileCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RequestorStripeActivity.this);
                builder1.setMessage("Your credit cards details have been submitted.");
                builder1.setCancelable(false);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                callPostJobAPI("");
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                String items = jsonObject.getString("items");
                Constants.showAlert(RequestorStripeActivity.this, items);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SaveProfileCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(RequestorStripeActivity.this, error);
    }

    private void callPostJobAPI(String nonce) {
        mProgressDilog = ProgressDialog.show(RequestorStripeActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        mSubmit.setEnabled(true);
        if (jobDetailsEntity.getCar() != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uid", prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
            map.put("jobtype", "Car");
            map.put("nonce",nonce);
            map.put("loclat", jobDetailsEntity.getLat());
            map.put("loclng", jobDetailsEntity.getLng());
            map.put("address", jobDetailsEntity.getAddress());
            map.put("descp", jobDetailsEntity.getDesc());
            map.put("model", jobDetailsEntity.getCar().getModel());
            map.put("color", jobDetailsEntity.getCar().getColor());
            map.put("licplateno", jobDetailsEntity.getCar().getLicense());
            map.put("licplatestate", jobDetailsEntity.getCar().getState());
            map.put("pcode", promocode);
            map.put("zipcode", Constants.Postal_code);
            if (jobDetailsEntity.getCar().getFile() == null) {
//                NetUtils.CallAddJob(map, file, this, RequestorStripeActivity.this);
                NetUtils.CallAddJob(map, this, RequestorStripeActivity.this);
            } else
                NetUtils.CallAddJob(map, jobDetailsEntity.getCar().getFile(), this, RequestorStripeActivity.this);
        } else if (jobDetailsEntity.getHome() != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uid", prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
            map.put("jobtype", "Home");
            map.put("nonce",nonce);
            map.put("loclat", jobDetailsEntity.getLat());
            map.put("loclng", jobDetailsEntity.getLng());
            map.put("address", jobDetailsEntity.getAddress());
            map.put("descp", jobDetailsEntity.getDesc());
            map.put("pcode", promocode);
            map.put("zipcode", Constants.Postal_code);
            if (jobDetailsEntity.getHome().getFile() == null) {
//                NetUtils.CallAddJob(map, file, this, RequestorStripeActivity.this);
                NetUtils.CallAddJob(map, this, RequestorStripeActivity.this);
            } else
                NetUtils.CallAddJob(map, jobDetailsEntity.getHome().getFile(), this, RequestorStripeActivity.this);
        } else if (jobDetailsEntity.getBusiness() != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uid", prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
            map.put("jobtype", "Business");
            map.put("loclat", jobDetailsEntity.getLat());
            map.put("loclng", jobDetailsEntity.getLng());
            map.put("address", jobDetailsEntity.getAddress());
            map.put("nonce",nonce);
            map.put("descp", jobDetailsEntity.getDesc());
            map.put("sizeofwork", jobDetailsEntity.getBusiness().getSize());
            map.put("pcode", promocode);
            map.put("zipcode", Constants.Postal_code);
            // If the file is not attached
            if (jobDetailsEntity.getBusiness().getFile() == null) {
//                NetUtils.CallAddJob(map, file, this, RequestorStripeActivity.this);
                NetUtils.CallAddJob(map, this, RequestorStripeActivity.this);
            } else
                //When the file is attached
                NetUtils.CallAddJob(map, jobDetailsEntity.getBusiness().getFile(), this, RequestorStripeActivity.this);
        }
    }

    @Override
    public void PostJobCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                prefClass.savePreference(SharedPrefClass.JOB_CNT, "1");
                startActivity(new Intent(RequestorStripeActivity.this, PostJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                String error = jsonObject.getString("items");
                Constants.showAlert(RequestorStripeActivity.this, error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void PostJobCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(RequestorStripeActivity.this, error);
    }

    @Override
    public void GetProfileCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);

            if (jsonObject.getString("status").equals("true")) {
                JSONArray array = jsonObject.getJSONArray("items");
                JSONObject object = array.getJSONObject(0);

//                mCardEditTextEditText.setText("xxxxxxxxxxxxxxxxx");
                mCardEditTextEditText.setHint("xxxxxxxxxxxxxxxxx");
                mCardEditTextEditText.setHintTextColor(getResources().getColor(R.color.black));
                card = object.getString("cardnumber");
                mMonthEditText.setText(object.getString("month"));
                month = object.getString("month");
                mYearEditText.setText(object.getString("year"));
                year = object.getString("year");
//                mCVCEditText.setText("xxx");
                mCVCEditText.setHint("xxx");
                mCVCEditText.setHintTextColor(getResources().getColor(R.color.black));
                cvc = object.getString("cvc");

                if (card.equals("")) {
                    mCardEditTextEditText.setHint("Enter Card Number");
                    mCardEditTextEditText.setFocusable(true);
                    mCardEditTextEditText.setText("");
                    mMonthEditText.setText("");
                    mYearEditText.setText("");
                    mCVCEditText.setText("");
                } else {
                    mInstructionTextView.setText("Your previous card details are stored below. Click SUBMIT to post a job using the same card OR to use another card, enter new card details below.\nYour card will not be charged until your job is complete.");
                }
                // mPhoneValue.setText(object.getString("mobno"));

            } else {
                Constants.showAlert(RequestorStripeActivity.this, jsonObject.getString("items"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void GetProfileCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(RequestorStripeActivity.this, error);
    }

    @Override
    public void GetPromoCodeCallbackSuccess(String success) {
        // mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                Promocode promocode;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    promocode = new Promocode();
                    promocode.setPcode(object.getString("pcode"));
                    promocode.setDiscount(object.getString("discount"));
                    promocode.setFromdt(object.getString("fromdt"));
                    promocode.setTodate(object.getString("todate"));
                    promocodeArrayList.add(promocode);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void GetPromoCodeCallbackError(String error) {
        //mProgressDilog.dismiss();
        Constants.showAlert(RequestorStripeActivity.this, error);
    }

    @Override
    public void CheckPromoCodeCallbackSuccess(String success) {
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                String msg = jsonObject.getString("items");

                Toast.makeText(RequestorStripeActivity.this, msg, Toast.LENGTH_LONG).show();
                showPromoCodeDialog.dismiss();
            } else {
                String msg = jsonObject.getString("items");

                Toast.makeText(RequestorStripeActivity.this, msg, Toast.LENGTH_LONG).show();

                promocode = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CheckPromoCodeCallbackError(String error) {
//        progressDialog.dismiss();
        showPromoCodeDialog.dismiss();
        Constants.showAlert(RequestorStripeActivity.this, error);
    }
    public void initOption()
    {
        radPaypal.setChecked(false);
        radCard.setChecked(false);
    }
    public void setOption(int i)
    {
        initOption();
        if (i == 0) {
            layoutCardInput.setVisibility(View.INVISIBLE);
            radPaypal.setChecked(true);
        }
        else if (i == 1)
        {
            layoutCardInput.setVisibility(View.VISIBLE);
            radCard.setChecked(true);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layoutRequestCard:
                setOption(1);
                break;
            case R.id.layoutRequestPaypal:
                setOption(0);
                PayPal.authorizeAccount(mBraintreeFragment);
                break;
        }
    }
}
