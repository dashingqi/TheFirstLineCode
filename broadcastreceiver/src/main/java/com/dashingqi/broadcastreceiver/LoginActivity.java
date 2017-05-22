package com.dashingqi.broadcastreceiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by zhangqi on 2017/5/16.
 */

public class LoginActivity extends BaseActivity {

    private EditText accountText;
    private EditText passwordText;
    private Button login;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountText = (EditText) findViewById(R.id.account);
        passwordText = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                if (account.equals("zhangqi")&&password.equals("123456")){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();//返回不在显示loginactivity;
                }else {
                    Toast.makeText(getApplicationContext(),
                            "account or password is invalid",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
