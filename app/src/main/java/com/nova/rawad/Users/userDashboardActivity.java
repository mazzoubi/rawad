package com.nova.rawad.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nova.rawad.AdminMainActivity;
import com.nova.rawad.R;

public class userDashboardActivity extends AppCompatActivity {

    TextView txvUserInfo ;

    TextView txv1 ;
    TextView txv2 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        init();

    }

    void init(){
        txvUserInfo = findViewById(R.id.textView6);
        txv1 = findViewById(R.id.textView8);
        txv2 = findViewById(R.id.textView811);

        txvUserInfo.setText("الإسم: "+getSharedPreferences("User",MODE_PRIVATE).getString("fullName","")+"\n"
        +"ٌرقم الهاتف: "+getSharedPreferences("User",MODE_PRIVATE).getString("phone","")
        );
        getInfo();
    }

    void getInfo(){
        FirebaseFirestore.getInstance().collection("DriverPassports").whereEqualTo("d_id",
                getSharedPreferences("User",MODE_PRIVATE).getString("id","")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    if (queryDocumentSnapshots.getDocuments().size()==0){
                        txv1.setText("لم يتم التفعيل");
                    }else {
                        if (queryDocumentSnapshots.getDocuments().get(0).get("office_state").equals("0")){
                            txv1.setText("لم يتم التفعيل");
                        }else {
                            txv1.setVisibility(View.GONE);
                        }
                    }
                }catch (Exception e){txv1.setText("لم يتم التفعيل");}
            }
        });


        FirebaseFirestore.getInstance().collection("DriverLicence").whereEqualTo("d_id",
                getSharedPreferences("User",MODE_PRIVATE).getString("id","")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    if (queryDocumentSnapshots.getDocuments().size()==0){
                        txv2.setText("لم يتم التفعيل");
                    }else {
                        if (queryDocumentSnapshots.getDocuments().get(0).get("office_state").equals("0")){
                            txv2.setText("لم يتم التفعيل");
                        }else {
                            txv2.setVisibility(View.GONE);
                        }
                    }
                }catch (Exception e){txv2.setText("لم يتم التفعيل");}
            }
        });


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
        startActivity(new Intent(getApplicationContext(),ServicesActivity.class));
    }

    public void onClickAdmin(View view) {
        startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));
    }
}