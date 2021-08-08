package com.nova.smartdetectorsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginScreenActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;

    Button btnLogin;
    ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_screen);

        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);

        inputEmail.setText(getSharedPreferences("UserInfo", MODE_PRIVATE).getString("email", ""));
        inputPassword.setText(getSharedPreferences("UserInfo", MODE_PRIVATE).getString("password", ""));

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

        if (email.equals("")) {
            Toast.makeText(getApplicationContext(), "Don't Leave Email Blank", Toast.LENGTH_SHORT).show();
            pb.dismiss();
            return; }

        if ( password.equals("")) {
            Toast.makeText(getApplicationContext(), "Don't Leave Password Blank", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LoginScreenActivity.this, "Error, Try Again Later", Toast.LENGTH_LONG).show(); }
                        }
                        else {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users")
                                    .document(auth.getCurrentUser().getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                                    editor.putString("username", documentSnapshot.getString("username"));
                                    editor.putString("mobile", documentSnapshot.getString("mobile"));
                                    editor.putString("email", documentSnapshot.getString("email"));
                                    editor.putString("password", documentSnapshot.getString("password"));
                                    editor.apply();

                                    pb.dismiss();

                                    startActivity(new Intent(LoginScreenActivity.this, MainActivity.class));
                                    finish(); }
                            }); }
                    }
                });
    }

    public void adminlogin(View view) {

        startActivity(new Intent(LoginScreenActivity.this, AdminLoginScreenActivity.class));

    }

    public void reg_acc(View view) {

        startActivity(new Intent(LoginScreenActivity.this, RegisterScreen.class));

    }
}
