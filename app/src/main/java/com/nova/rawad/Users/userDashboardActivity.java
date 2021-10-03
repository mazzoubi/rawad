package com.nova.rawad.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.nova.rawad.R;

public class userDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
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
}