package com.nova.smartdetectorsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterScreen extends AppCompatActivity {

    EditText inputEmail, inputPassword, inputMobile, inputUsername;
    Button btnSignUp;
    CircleImageView pic;
    ProgressDialog pb;

    Bitmap bitmap;
    String DownloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_screen);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputMobile = findViewById(R.id.mobile);
        inputUsername = findViewById(R.id.nameField);
        btnSignUp = findViewById(R.id.sign_up_button);
        pic = findViewById(R.id.img);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1996);

            }
        });

        pb = new ProgressDialog(this);
        pb.setMessage("Loading, Please Wait...");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.show();

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String username = inputUsername.getText().toString().trim();
                final String mobile = inputMobile.getText().toString().trim();

                if(email.equals("") || password.equals("") || username.equals("") || mobile.equals("")){
                    Toast.makeText(RegisterScreen.this, "Don't Leave Any Blanks Please.", Toast.LENGTH_SHORT).show();
                    pb.dismiss(); }

                else {
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterScreen.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful())
                                Toast.makeText(RegisterScreen.this, "Error, Please Try Again Later.", Toast.LENGTH_SHORT).show();

                            else {

                                Map<String, String> map = new HashMap<>();
                                map.put("username", username);
                                map.put("email", email);
                                map.put("password", password);
                                map.put("mobile", mobile);
                                map.put("uid", auth.getUid());
                                map.put("pic", DownloadUrl);
                                map.put("create", classDate.date());

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Users")
                                        .document(map.get("uid"))
                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (!task.isSuccessful())
                                            Toast.makeText(RegisterScreen.this, "Error, Please Try Again Later.", Toast.LENGTH_SHORT).show();
                                        else{
                                            Toast.makeText(RegisterScreen.this, "Welcome "+map.get("username")+" Your Account Has Been Created.", Toast.LENGTH_LONG).show();
                                            RegisterScreen.this.finish(); }
                                    }
                                });
                            } }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Toast.makeText(this, "Uploading Image...", Toast.LENGTH_SHORT).show();

            Uri uri = data.getData();

            try { bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); }
            catch (IOException e) {}
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

            String path = MediaStore.Images.Media.insertImage(RegisterScreen.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
            uri = Uri.parse(path);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ImageName =  storageReference.child("image"+classDate.currentTimeAtMs()+uri.getLastPathSegment());

            ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            DownloadUrl = uri.toString();
                            Toast.makeText(RegisterScreen.this, "Profile Image Uploaded.", Toast.LENGTH_SHORT).show();

                            pic.setImageBitmap(bitmap);

                        }
                    });
                }
            });
        }

    }

}
