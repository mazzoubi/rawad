package com.nova.rawad.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nova.rawad.R;

public class userDashboardActivity extends AppCompatActivity {

    TextView txvUserInfo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        init();

    }

    void init(){
        txvUserInfo = findViewById(R.id.textView6);

        txvUserInfo.setText("الإسم: "+getSharedPreferences("User",MODE_PRIVATE).getString("fullName","")+"\n"
        +"ٌرقم الهاتف: "+getSharedPreferences("User",MODE_PRIVATE).getString("phone","")
        );
    }

    public void onClickSignOut(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("User",MODE_PRIVATE).edit();
        editor.putString("fullName", "");
        editor.putString("phone", "");
        editor.putString("password", "" );
        editor.putString("id", "" );
        editor.apply();
        finish();
    }

    public void onClickPassport(View view) {
        startActivity(new Intent(getApplicationContext(),PassportDetailActivity.class));
    }

    public void onClickCarDetail(View view) {
        startActivity(new Intent(getApplicationContext(),CarDetailActivity.class));
    }

    public void onClickServices(View view) {

    }
}