package com.nova.rawad.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nova.rawad.R;
import com.nova.rawad.classDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {
    String dateFrom="";
    TextView txvDate ;
    EditText edtEmail ;

    RadioButton radioButtonEntry ,radioButtonExit ;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        init();
    }

    void init(){
        progressDialog = new ProgressDialog(RequestActivity.this);
        progressDialog.setTitle("الرجاء الإنتظار...");
        txvDate = findViewById(R.id.textView18);
        edtEmail = findViewById(R.id.editTextTextEmailAddress);
        radioButtonEntry = findViewById(R.id.radioButton);
        radioButtonExit = findViewById(R.id.radioButton2);
    }

    void showDateFrom(){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date_ = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "d-M-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dateFrom=sdf.format(myCalendar.getTime());
                txvDate.setText(dateFrom);

            }
        };

        new DatePickerDialog(RequestActivity.this, date_, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onClickDate(View view) {
        showDateFrom();
    }

    public void onClickRequest(View view) {
        if (dateFrom.isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء اختيار تاريخ التصريح", Toast.LENGTH_SHORT).show();
        }else {
            String entry = "0" ;
            String exit = "0" ;
            if (radioButtonEntry.isChecked()){
                entry="1";
                exit="0";
            }else {
                entry="0";
                exit="1";
            }

            String key = classDate.currentTimeAtMs();
            Map<String,Object> map = new HashMap<>();
            map.put("dateOfRequest", classDate.date());
            map.put("dateOfPermit",dateFrom );
            map.put("transId",key );
            map.put("userId", getSharedPreferences("User",MODE_PRIVATE).getString("id",""));
            map.put("userName", getSharedPreferences("User",MODE_PRIVATE).getString("fullName",""));
            map.put("entry",entry );
            map.put("exit",exit );
            map.put("payment","0");

            progressDialog.show();
            FirebaseFirestore.getInstance().collection("Requests").document(key).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "تم الطلب", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}