package com.app.shovelerapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.SharedPrefClass;

/**
 * Created by Administrator on 5/2/2017.
 */
public class SmsCheckActivity extends AppCompatActivity implements View.OnClickListener,ILoadService {


    public EditText editCode;
    public Button btnCheckCode;
    public Button btnResendCode;
    public SharedPrefClass prefClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sms_check);
        prefClass=new SharedPrefClass(SmsCheckActivity.this);
        init();
    }
    public void init()
    {
        editCode = (EditText) this.findViewById(R.id.editPhoneCode);
        btnResendCode = (Button) this.findViewById(R.id.btnPhoneResend);
        btnCheckCode = (Button) this.findViewById(R.id.btnPhoneCode);

        btnResendCode.setOnClickListener(this);
        btnCheckCode.setOnClickListener(this);

    }
    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 1:
                Toast.makeText(this,"Sms sent to your phone. Please check",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                prefClass.savePreferenceBoolean(SharedPrefClass.PHONE_STATUS, true);
                startActivity(new Intent(SmsCheckActivity.this,
                        AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case 800:
                Toast.makeText(this,"Fail to send sms to your phone.",Toast.LENGTH_SHORT).show();
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
            case R.id.btnPhoneResend:
                break;
            case R.id.btnPhoneCode:

                String code = editCode.getText().toString();
                if (code.equals(""))
                {
                    Toast.makeText(this,"Please input code",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (Globals.g_phoneNumber.equals("") || Globals.g_phoneCode.equals(""))
                {
                    Toast.makeText(this,"Please get verification code first.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code.equals(Globals.g_phoneCode))
                {
                    ServiceManager.onVerifyPhone(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),Globals.g_phoneNumber,this);
                }
                else
                {
                    Toast.makeText(this,"Wrong Code",Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
        }
    }
}
