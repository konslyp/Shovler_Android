package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.ForgetPassword;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgetActivity extends AppCompatActivity implements ForgetPassword{
private Button mRememberPass,mSendPassButton;
    private EditText mEmailEditText;
    private TextView mForgetPassTitle;
    private ProgressDialog mProgressDilog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget);
        init();

        mRememberPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mRememberPass= (Button) findViewById(R.id.remember_pass);
        mSendPassButton= (Button) findViewById(R.id.send_pass);
        mEmailEditText= (EditText) findViewById(R.id.email);
        mForgetPassTitle= (TextView) findViewById(R.id.forget_pass_id);

        mEmailEditText= (EditText) findViewById(R.id.email);

        mSendPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidate()){
                    Globals.g_email = mEmailEditText.getText().toString();
                    callForgetPassApi();
                }
            }
        });
        
        setFont();
    }

    private void callForgetPassApi() {
        mProgressDilog = ProgressDialog.show(ForgetActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallForget(mEmailEditText.getText().toString(),this,ForgetActivity.this);
    }

    private boolean isValidate() {
    if (mEmailEditText.getText().toString().equals("")){
        setErrorFun(mEmailEditText,getResources().getString(R.string.enter_email));
        return false;
    }else {
        return true;
    }
    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }

    private void setFont() {
        Typeface tfRegular= Constants.setRegularLatoFont(ForgetActivity.this);
        Typeface tfLight=Constants.setLightLatoFont(ForgetActivity.this);
        Typeface tfThin=Constants.setThinLatoFont(ForgetActivity.this);
        Typeface tfMedium=Constants.setMediumLatoFont(ForgetActivity.this);

        mRememberPass.setTypeface(tfRegular);
        mEmailEditText.setTypeface(tfRegular);
        mForgetPassTitle.setTypeface(tfRegular);
        mEmailEditText.setTypeface(tfRegular);
        mSendPassButton.setTypeface(tfRegular);
    }

    @Override
    public void ForgetPasswordSuccess(String success) {
        mProgressDilog.dismiss();

        try {
            JSONObject jsonObject=new JSONObject(success);
            if (jsonObject.getString("status").equals("true")){
              //  Constants.showAlert(ForgetActivity.this,jsonObject.getString("message"));
                Globals.g_code = jsonObject.getString("code");
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgetActivity.this);
                builder1.setMessage(jsonObject.getString("message"));
                builder1.setCancelable(false);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent m = new Intent(ForgetActivity.this,ResetPasswordActivity.class);
                                ForgetActivity.this.startActivity(m);
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }else {
                Constants.showAlert(ForgetActivity.this,jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ForgetPasswordError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(ForgetActivity.this,error);
    }
}
