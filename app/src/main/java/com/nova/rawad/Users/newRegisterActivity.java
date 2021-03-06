package com.nova.rawad.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nova.rawad.R;
import com.nova.rawad.classDate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class newRegisterActivity extends AppCompatActivity {



    EditText edtFullName , edtPhone , edtPassword ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_register);
        init();


    }

    void init(){
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
            FirebaseFirestore.getInstance().collection("Users")
                    .whereEqualTo("phone",edtPhone.getText().toString())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size()==0){
                        OtpDialog a = new OtpDialog();
                        a.setCancelable(false);
                        a.show();
                    }else {
                        Toast.makeText(getApplicationContext(), "هذا الرقم مسجل مسبقاً, الرجاء المحاولة برقم اخر", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }


    class OtpDialog extends Dialog {

        Activity c ;
        public OtpDialog(){
            super(newRegisterActivity.this);
            c=newRegisterActivity.this;
        }

        // variable for FirebaseAuth class
        private FirebaseAuth mAuth;

        // variable for our text input
        // field for phone and OTP.
        private EditText  edtOTP;

        // buttons for generating OTP and verifying OTP
        private Button verifyOTPBtn;

        // string for storing our verification ID
        private String verificationId;

        TextView txvCancel;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main3);

            // below line is for getting instance
            // of our FirebaseAuth.
            mAuth = FirebaseAuth.getInstance();

            // initializing variables for button and Edittext.

            edtOTP = findViewById(R.id.idEdtOtp);
            verifyOTPBtn = findViewById(R.id.idBtnVerify);
            txvCancel = findViewById(R.id.textView4);


            // setting for generate OTP .
            String phone =  edtPhone.getText().toString();
            sendVerificationCode(phone);
            // initializing on click listener
            // for verify otp button
            verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // validating if the OTP text field is empty or not.
                    if (TextUtils.isEmpty(edtOTP.getText().toString())) {
                        // if the OTP text field is empty display
                        // a message to user to enter OTP
                        Toast.makeText(c, "الرجاء ادخال الرمز", Toast.LENGTH_SHORT).show();
                    } else {
                        // if OTP field is not empty calling
                        // method to verify the OTP.
                        verifyCode(edtOTP.getText().toString());
                    }
                }
            });

            txvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtpDialog.super.dismiss();
                }
            });
        }

        private void signInWithCredential(PhoneAuthCredential credential) {
            // inside this method we are checking if
            // the code entered is correct or not.
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // if the code is correct and the task is successful
                                // we are sending our user to new activity.

//                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
//                            startActivity(i);
//                            finish();

                                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                builder.setTitle("تم التأكيد");


                                String key = classDate.currentTimeAtMs();
                                Map<String,Object> map = new HashMap<>();
                                map.put("fullName", edtFullName.getText().toString());
                                map.put("phone", edtPhone.getText().toString());
                                map.put("password",edtPassword.getText().toString() );
                                map.put("id", key);
                                SharedPreferences.Editor editor = getSharedPreferences("User",MODE_PRIVATE).edit();
                                FirebaseFirestore.getInstance().collection("Users").document(key).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            editor.putString("fullName", edtFullName.getText().toString());
                                            editor.putString("phone", edtPhone.getText().toString());
                                            editor.putString("password",edtPassword.getText().toString() );
                                            editor.putString("id", key);
                                            editor.apply();
                                            Toast.makeText(c, "تم التأكيد", Toast.LENGTH_SHORT).show();
                                            OtpDialog.super.dismiss();
                                            startActivity(new Intent(c,userDashboardActivity.class));
                                            c.finish();
                                        }
                                    }
                                });


                            } else {
                                // if the code is not correct then we are
                                // displaying an error message to the user.
                                Toast.makeText(c, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                builder.setMessage(task.getException().getMessage());
                                Toast.makeText(c, "الرمز خاطئ, الرجاء ادخال الرمز الصحيح", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


        private void sendVerificationCode(String number) {
            // this method is used for getting
            // OTP on user phone number.
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(number)		 // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(c)				 // Activity (for callback binding)
                            .setCallbacks(mCallBack)		 // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }

        // callback method is called on Phone auth provider.
        private PhoneAuthProvider.OnVerificationStateChangedCallbacks

                // initializing our callbacks for on
                // verification callback method.
                mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // below method is used when
            // OTP is sent from Firebase
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // when we receive the OTP it
                // contains a unique id which
                // we are storing in our string
                // which we have already created.
                verificationId = s;
            }

            // this method is called when user
            // receive OTP from Firebase.
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                final String code = phoneAuthCredential.getSmsCode();

                // checking if the code
                // is null or not.
                if (code != null) {
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.
                    edtOTP.setText(code);

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.
                    verifyCode(code);
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // displaying error message with firebase exception.
                Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // below method is use to verify code from Firebase.
        private void verifyCode(String code) {
            // below line is used for getting getting
            // credentials from our verification id and code.
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                // after getting credential we are
                // calling sign in method.
                signInWithCredential(credential);
            }catch (Exception e){
                Toast.makeText(c, "حدث خطأ اثناء الإنشاء, الرجاء المحاولة مرة اخرى", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),UserLoginActivity.class));
    }
}