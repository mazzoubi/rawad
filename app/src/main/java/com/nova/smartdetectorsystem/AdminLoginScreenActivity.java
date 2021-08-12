package com.nova.smartdetectorsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLoginScreenActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, InputSecret;
    private FirebaseAuth auth;

    Button btnLogin;
    ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.admin_activity_login_screen);

        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        InputSecret = findViewById(R.id.password2);
        btnLogin = findViewById(R.id.btn_login);

        pb = new ProgressDialog(this);
        pb.setMessage("Please Wait...");
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void Login() {

        pb.show();
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        final String secret = InputSecret.getText().toString();

        if (email.equals("")) {
            Toast.makeText(getApplicationContext(), "Don't Leave Email Blank", Toast.LENGTH_SHORT).show();
            pb.dismiss();
            return; }

        if ( password.equals("")) {
            Toast.makeText(getApplicationContext(), "Don't Leave Password Blank", Toast.LENGTH_SHORT).show();
            pb.dismiss();
            return; }

        if ( !secret.equals("123321")) {
            Toast.makeText(getApplicationContext(), "You Don't Have Admin Permission", Toast.LENGTH_SHORT).show();
            pb.dismiss();
            return; }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                pb.dismiss();
                                inputPassword.setError("Password Must Be 6 Or More characters"); }
                            else {
                                pb.dismiss();
                                Toast.makeText(AdminLoginScreenActivity.this, "Error, Try Again Later", Toast.LENGTH_LONG).show(); }
                        }
                        else {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Admin")
                                    .document(auth.getCurrentUser().getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                                    editor.putString("username", documentSnapshot.getString("username"));
                                    editor.putString("mobile", documentSnapshot.getString("mobile"));
                                    editor.putString("email", documentSnapshot.getString("email"));
                                    editor.putString("password", documentSnapshot.getString("password"));

                                    pb.dismiss();

                                    startActivity(new Intent(AdminLoginScreenActivity.this, AdminMainActivity.class));
                                    finish(); }
                            }); }
                    }
                });
    }
}
