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
 * Created by Administrator on 4/11/2017.
 */
public class PhoneVerifyActivity extends AppCompatActivity implements View.OnClickListener,ILoadService{

    public EditText editPhone;
    public Button btnSendCode;
    public SharedPrefClass prefClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_phoneverify);
        prefClass=new SharedPrefClass(PhoneVerifyActivity.this);
        init();
    }
    public void init()
    {
        editPhone = (EditText) this.findViewById(R.id.editPhoneNumber);
        btnSendCode = (Button) this.findViewById(R.id.btnSendCode);

        btnSendCode.setOnClickListener(this);
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
                startActivity(new Intent(PhoneVerifyActivity.this,
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
            case R.id.btnSendCode:
                String phone = editPhone.getText().toString();
                if (phone.equals(""))
                {
                    Toast.makeText(this,"Please input Phone Number",Toast.LENGTH_SHORT).show();
                    return;
                }
                phone = "+1" + phone;
                ServiceManager.onSendSms(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),phone,this);
                Globals.g_testPhone = phone;
                Intent m = new Intent(this,SmsCheckActivity.class);
                this.startActivity(m);
                break;

        }
    }
}
