package com.dashingqi.sharedpreferencestest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText accountText;
    private EditText passwordText;
    private CheckBox rememberPass;
    private Button login;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = prefs.getBoolean("remember_pass", false);
        if (isRemember){
            String account = prefs.getString("accountText", "");
            String password = prefs.getString("passwordText", "");
            accountText.setText(account);
            passwordText.setText(password);
            rememberPass.setChecked(true);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                if (account.equals("admin")&&password.equals("123456")){
                    edit = prefs.edit();
                    if (rememberPass.isChecked()){
                        edit.putString("accountText",account);
                        edit.putString("passwordText",password);
                        edit.putBoolean("remember_pass",true);
                    }else {
                        edit.clear();
                    }
                    edit.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"Account or password is error",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void initView() {
        accountText = (EditText) findViewById(R.id.account);
        passwordText = (EditText) findViewById(R.id.password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        login = (Button) findViewById(R.id.login);
    }
}
