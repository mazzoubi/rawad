package com.nova.rawad.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nova.rawad.R;

public class ServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srevices);

    }

    public void onClickRequest(View view) {
        startActivity(new Intent(getApplicationContext(),RequestActivity.class));
    }
    public void onClickMyRequest(View view) {
        startActivity(new Intent(getApplicationContext(),MyRequestActivity.class));
    }
}