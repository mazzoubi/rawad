package com.nova.rawad.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.nova.rawad.R;

public class newRegisterActivity extends AppCompatActivity {


    PhoneAuthOptions phoneAuthOptions ;
    FirebaseAuth firebaseAuth ;
    String phoneNumber ="";
    String smsCode = "" ;

    EditText edtFullName , edtPhone , edtPassword ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_register);
        init();


    }

    void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
    }


    public void onClickCreateAccount(View view) {
        if (edtFullName.getText().toString().isEmpty()){
            Toast.makeText(this, "الرجاء ادخال الاسم الكامل", Toast.LENGTH_SHORT).show();
        }else if (edtPhone.getText().toString().isEmpty()){
            Toast.makeText(this, "الرجاء ادخال رقم الهاتف", Toast.LENGTH_SHORT).show();
        }else if (edtPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "الرجاء ادخال كلمة المرور", Toast.LENGTH_SHORT).show();
        }else {

        }
    }
}