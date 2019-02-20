package com.app.shovelerapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.nostra13.universalimageloader.utils.L;

/**
 * Created by Administrator on 4/7/2017.
 */
public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener,ILoadService{

    public EditText editCode;
    public Button btnCheckcode;
    public EditText editNewPassword;
    public EditText editConfirmPassword;
    public Button btnResetPassword;
    public LinearLayout layoutPassword;
    public ProgressDialog mProgressDilog;
    public LinearLayout layoutCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_resetpassword);
        init();
    }

    public void init()
    {
        editCode = (EditText) this.findViewById(R.id.editResetCode);
        btnCheckcode = (Button) this.findViewById(R.id.btnResetCode);
        editNewPassword = (EditText) this.findViewById(R.id.editResetNew);
        editConfirmPassword = (EditText) this.findViewById(R.id.editResetConfirm);
        btnResetPassword = (Button) this.findViewById(R.id.btnResetPassword);
        layoutPassword = (LinearLayout) this.findViewById(R.id.layoutResetPassword);
        layoutCode = (LinearLayout) this.findViewById(R.id.layoutCheckCode);

        layoutPassword.setVisibility(View.GONE);

        btnResetPassword.setOnClickListener(this);
        btnCheckcode.setOnClickListener(this);
    }

    @Override
    public void onResponse(int code) {
        mProgressDilog.dismiss();
        switch (code)
        {
            case 1:
                Intent m = new Intent(this,LoginActivity.class);
                m.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(m);
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnResetCode:
                String code = editCode.getText().toString();
                if (Globals.g_code.equals(code))
                {
                    layoutCode.setVisibility(View.GONE);
                    layoutPassword.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(this,"Invalid Code",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnResetPassword:
                String pw = editNewPassword.getText().toString();
                String confirm = editConfirmPassword.getText().toString();
                if (pw.equals(confirm))
                {
                    if (!Constants.isValidPassword(editNewPassword.getText().toString())) {
                        setErrorFun(editNewPassword, getResources().getString(R.string.enter_valid_pass));
                    }
                    else
                    {
                        mProgressDilog = ProgressDialog.show(ResetPasswordActivity.this, "",
                                getResources().getString(R.string.loading), true, false);
                        ServiceManager.onResetPassword(Globals.g_email,editNewPassword.getText().toString(),this);
                    }
                }
                else
                {
                    Toast.makeText(this,"Two Passwords are not match",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }
}
