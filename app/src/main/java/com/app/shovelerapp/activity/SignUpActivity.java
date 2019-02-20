package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;

import com.app.shovelerapp.callback.SignUpCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements SignUpCallback {
    private Button mContinueButton, mAlreadyAccButton;
    private String pass_str;
    private ProgressDialog mProgressDilog;
    CallbackManager callbackManager;
    private CheckBox mTermsCheckBox;
    private TextView mEmailTitle, mSignUpTitle;
    private EditText mEmailEditText, mPassEditText, mConfPassEditText;
    private EditText mFNameEditText, mLNameEditText;
    private Boolean isCheckedVal=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);

        init();

        spannableClick();


        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidate())
                    callSignUpAPI();
            }
        });

        mAlreadyAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTermsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isCheckedVal=true;
                }else {
                    isCheckedVal=false;
                }
            }
        });


    }

    private void callSignUpAPI() {
        mProgressDilog = ProgressDialog.show(SignUpActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallSignUp(mEmailEditText.getText().toString().trim(), mConfPassEditText.getText().toString().trim(), mFNameEditText.getText().toString(), mLNameEditText.getText().toString().trim(), this, SignUpActivity.this);
    }

    private void init() {
        mContinueButton = (Button) findViewById(R.id.conti);
        mAlreadyAccButton = (Button) findViewById(R.id.already_acc);
        mTermsCheckBox = (CheckBox) findViewById(R.id.terms_text);


        mSignUpTitle = (TextView) findViewById(R.id.sign_up_title);
        mEmailTitle = (TextView) findViewById(R.id.sign_up_email_title);

        mEmailEditText = (EditText) findViewById(R.id.email);
        mPassEditText = (EditText) findViewById(R.id.set_pass);
        mConfPassEditText = (EditText) findViewById(R.id.set_conf_pass);
        mFNameEditText = (EditText) findViewById(R.id.fname);
        //mAddressEditText = (EditText) findViewById(R.id.add_location);
        //mMobileEditText = (EditText) findViewById(R.id.mobile_no);

        mLNameEditText = (EditText) findViewById(R.id.lname);


        setTypeface();
    }

    private void setTypeface() {
        Typeface tfRegular = Constants.setRegularLatoFont(SignUpActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(SignUpActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(SignUpActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(SignUpActivity.this);


        mAlreadyAccButton.setTypeface(tfRegular);

        mSignUpTitle.setTypeface(tfRegular);
        mEmailTitle.setTypeface(tfRegular);

        mEmailEditText.setTypeface(tfRegular);
        mPassEditText.setTypeface(tfRegular);
        mConfPassEditText.setTypeface(tfRegular);
        mFNameEditText.setTypeface(tfRegular);
        //mAddressEditText.setTypeface(tfRegular);
        mContinueButton.setTypeface(tfRegular);
        //mMobileEditText.setTypeface(tfRegular);
        mLNameEditText.setTypeface(tfRegular);

    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }

    public boolean isValidate() {
        if (mEmailEditText.getText().toString().equals("") && mPassEditText.getText().toString().equals("") && mConfPassEditText.getText().toString().equals("")) {
            setErrorFun(mEmailEditText, getResources().getString(R.string.all_fields));
            return false;
        } else if (mFNameEditText.getText().toString().equals("")) {
            setErrorFun(mFNameEditText, getResources().getString(R.string.enter_fname));
            return false;
        } else if (mLNameEditText.getText().toString().equals("")) {
            setErrorFun(mLNameEditText, getResources().getString(R.string.enter_lname));
            return false;
        } else if (mEmailEditText.getText().toString().equals("")) {
            setErrorFun(mEmailEditText, getResources().getString(R.string.enter_email));
            return false;
        } else if (!Constants.isValidEmail(mEmailEditText.getText().toString())) {
            setErrorFun(mEmailEditText, getResources().getString(R.string.enter_valid_email));
            return false;
        } else if (mPassEditText.getText().toString().equals("")) {
            setErrorFun(mPassEditText, getResources().getString(R.string.enter_pass));
            return false;
        } else if (!Constants.isValidPassword(mPassEditText.getText().toString())) {
            setErrorFun(mPassEditText, getResources().getString(R.string.enter_valid_pass));
            return false;
        } else if (mConfPassEditText.getText().toString().equals("")) {
            setErrorFun(mConfPassEditText, getResources().getString(R.string.enter_conf_pass));
            return false;
        } else if (!mPassEditText.getText().toString().equals(mConfPassEditText.getText().toString())) {
            setErrorFun(mPassEditText, getResources().getString(R.string.same_pass));
            return false;
        } else if (!isCheckedVal){
            Constants.showAlert(SignUpActivity.this,"You must accept terms and conditions to sign up");
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void SignUpCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        Log.v("success", "" + success);
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpActivity.this);
//                builder1.setMessage("Welcome to Shovler! Please check your email (including spam folder) to activate your account.");
//                builder1.setCancelable(true);
//                builder1.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                                finish();
//                            }
//                        });
//
//
//                AlertDialog alert11 = builder1.create();
//                alert11.show();
                setResult(100);
                finish();
            } else {
                Constants.showAlert(SignUpActivity.this, jsonObject.getString("items"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void spannableClick() {
        SpannableString ss = new SpannableString("By signing up you agree to the Terms of Use and acknowledge that you have read the Privacy policy");
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Toast.makeText(SignUpActivity.this, "Terms and condition",
                        Toast.LENGTH_SHORT).show();

              /*  Intent forgotPassowordIntent = new Intent(SignUpActivity.this,
                        ForgotPasswordActivity.class);
                startActivity(forgotPassowordIntent);
                finish();*/
                startActivity(new Intent(SignUpActivity.this,TermsAndCondition.class));
            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do another thing
               /* Toast.makeText(SignUpActivity.this, "Privacy Policy",
                        Toast.LENGTH_SHORT).show();*/
                startActivity(new Intent(SignUpActivity.this,PrivacyActivity.class));
            }
        };

        ss.setSpan(span1, 31, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, 83, 97, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTermsCheckBox.setText(ss);
        mTermsCheckBox.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void SignUpCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(SignUpActivity.this, error);
    }
}
