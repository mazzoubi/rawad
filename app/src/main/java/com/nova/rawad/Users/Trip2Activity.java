package com.nova.rawad.Users;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nova.rawad.PassengerPassportsClass;
import com.nova.rawad.R;
import com.nova.rawad.TrpipClass;
import com.nova.rawad.classDate;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trip2Activity extends AppCompatActivity {
    
    ArrayList<PassengerPassportsClass> passengers = null;
    ArrayList<TrpipClass> trips = null;
    AutoCompleteTextView act;
    EditText name, d_id, req, conf;
    Button btn_sea;
    
    Request request ;

    Uri uri , uri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("رواد الرمثا");

        Toast.makeText(this, "جاري جلب البيانات, يرجى الإنتظار...", Toast.LENGTH_LONG).show();

        act = findViewById(R.id.sea);
        name = findViewById(R.id.txvDate1);
        req = findViewById(R.id.txvDate12);
        conf = findViewById(R.id.txvDate2);
        d_id = findViewById(R.id.txvDate4);
        btn_sea = findViewById(R.id.btn_sea);

        FirebaseFirestore.getInstance().collection("PassengerPassports")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                passengers = new ArrayList<>();

                for(int i=0; i<list.size(); i++)
                    passengers.add(list.get(i).toObject(PassengerPassportsClass.class));

                Toast.makeText(Trip2Activity.this, "تم جلب البيانات", Toast.LENGTH_SHORT).show();

            }
        });
        

            name.setText(MyRequestActivity.requestsClass.userName);
            req.setText(MyRequestActivity.requestsClass.dateOfRequest);
            conf.setText(MyRequestActivity.requestsClass.dateOfPermit);
            d_id.setText(MyRequestActivity.requestsClass.userId);

            FirebaseFirestore.getInstance().collection("Trips")
                    .whereEqualTo("RequestId", MyRequestActivity.requestsClass.transId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    trips = new ArrayList<>();

                    for(int i=0; i<list.size(); i++)
                        trips.add(list.get(i).toObject(TrpipClass.class));

                    Toast.makeText(Trip2Activity.this, "تم جلب البيانات", Toast.LENGTH_SHORT).show();

                }
            });

    
    }

    public void ViewRemovePassengers(View view) {

        ArrayList<PassengerPassportsClass> TempPassengers = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for(int i=0; i<passengers.size(); i++){
            for(int j=0; j<trips.size(); j++){
                if(passengers.get(i).id.equals(trips.get(j).PassengerId)){
                    TempPassengers.add(passengers.get(i));
                    names.add(passengers.get(i).p_name);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Trip2Activity.this, android.R.layout.simple_spinner_dropdown_item, names);
        ListView list = new ListView(Trip2Activity.this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new AlertDialog.Builder(Trip2Activity.this)
                        .setMessage("هل ترغب ب...")
                        .setPositiveButton("عرض الراكب", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder((Trip2Activity.this));
                                LayoutInflater inflater = (Trip2Activity.this).getLayoutInflater();
                                builder.setView(inflater.inflate(R.layout.dialog_passenger_passport_view, null));
                                final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                                ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog2.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                dialog2.show();
                                dialog2.getWindow().setAttributes(lp);
                                dialog2.setCanceledOnTouchOutside(false);

                                EditText name = dialog2.findViewById(R.id.txvDate1);
                                EditText pnum = dialog2.findViewById(R.id.txvDate2);
                                EditText pissue = dialog2.findViewById(R.id.txvDate3);
                                EditText pexpiry = dialog2.findViewById(R.id.txvDate4);
                                EditText uid = dialog2.findViewById(R.id.txvDate8);
                                EditText mobile = dialog2.findViewById(R.id.txvDate12);

                                ImageView img_dia1 = dialog2.findViewById(R.id.imageView);
                                ImageView img_dia2 = dialog2.findViewById(R.id.imageView2);
                                Spinner pconfirm = dialog2.findViewById(R.id.txvDate6);

                                name.setText(TempPassengers.get(position).p_name);
                                pnum.setText(TempPassengers.get(position).p_num);
                                pissue.setText(TempPassengers.get(position).p_issue);
                                pexpiry.setText(TempPassengers.get(position).p_expiry);
                                uid.setText(TempPassengers.get(position).id);
                                mobile.setText(TempPassengers.get(position).phone);

                                if(TempPassengers.get(position).office_state.equals("") || TempPassengers.get(position).office_state.equals("0"))
                                    pconfirm.setSelection(1);
                                else
                                    pconfirm.setSelection(0);

                                uri = Uri.parse(TempPassengers.get(position).p_img);
                                uri2 = Uri.parse(TempPassengers.get(position).p_img2);
                                Picasso.get().load(Uri.parse(TempPassengers.get(position).p_img)).into(img_dia1);
                                Picasso.get().load(Uri.parse(TempPassengers.get(position).p_img2)).into(img_dia2);
                            }
                        }).setNegativeButton("حذف الراكب", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(Trip2Activity.this)
                                .setTitle("تنبيه")
                                .setMessage("سيتم حذف"+names.get(position)+" من الرحلة, هل ترغب بالمتابعة ؟ ")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        int index = -1;
                                        for(int i=0; i<trips.size(); i++)
                                            if(trips.get(i).PassengerId.equals(TempPassengers.get(position).id))
                                                index = i;

                                        FirebaseFirestore.getInstance().collection("Trips")
                                                .document(trips.get(index).TransId)
                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Trip2Activity.this, "تم حذف الراكب", Toast.LENGTH_SHORT).show();
                                                    recreate(); }
                                                else
                                                    Toast.makeText(Trip2Activity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).create().show();
                    }
                }).create().show();
            }
        });

        new AlertDialog.Builder(Trip2Activity.this)
                .setView(list)
                .create().show();

    }

    public void ViewAddPassengers(View view) {

        ArrayList<String> names = new ArrayList<>();

        for(int i=0; i<passengers.size(); i++)
            names.add(passengers.get(i).p_name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Trip2Activity.this, android.R.layout.simple_spinner_dropdown_item, names);
        ListView list = new ListView(Trip2Activity.this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new AlertDialog.Builder(Trip2Activity.this)
                        .setMessage("هل ترغب ب...")
                        .setPositiveButton("عرض الراكب", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder((Trip2Activity.this));
                                LayoutInflater inflater = (Trip2Activity.this).getLayoutInflater();
                                builder.setView(inflater.inflate(R.layout.dialog_passenger_passport_view, null));
                                final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                                ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog2.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                dialog2.show();
                                dialog2.getWindow().setAttributes(lp);
                                dialog2.setCanceledOnTouchOutside(false);

                                EditText name = dialog2.findViewById(R.id.txvDate1);
                                EditText pnum = dialog2.findViewById(R.id.txvDate2);
                                EditText pissue = dialog2.findViewById(R.id.txvDate3);
                                EditText pexpiry = dialog2.findViewById(R.id.txvDate4);
                                EditText uid = dialog2.findViewById(R.id.txvDate8);
                                EditText mobile = dialog2.findViewById(R.id.txvDate12);

                                ImageView img_dia1 = dialog2.findViewById(R.id.imageView);
                                ImageView img_dia2 = dialog2.findViewById(R.id.imageView2);
                                Spinner pconfirm = dialog2.findViewById(R.id.txvDate6);

                                name.setText(passengers.get(position).p_name);
                                pnum.setText(passengers.get(position).p_num);
                                pissue.setText(passengers.get(position).p_issue);
                                pexpiry.setText(passengers.get(position).p_expiry);
                                uid.setText(passengers.get(position).id);
                                mobile.setText(passengers.get(position).phone);

                                if(passengers.get(position).office_state.equals("") || passengers.get(position).office_state.equals("0"))
                                    pconfirm.setSelection(1);
                                else
                                    pconfirm.setSelection(0);


                                uri = Uri.parse(passengers.get(position).p_img);
                                uri2 = Uri.parse(passengers.get(position).p_img2);
                                Picasso.get().load(Uri.parse(passengers.get(position).p_img)).into(img_dia1);
                                Picasso.get().load(Uri.parse(passengers.get(position).p_img2)).into(img_dia2);
                            }
                        }).setNegativeButton("إضافة الراكب", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(Trip2Activity.this)
                                .setTitle("تنبيه")
                                .setMessage("سيتم إضافة"+names.get(position)+" إلى الرحلة, هل ترغب بالمتابعة ؟ ")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("DriverId", d_id.getText().toString());
                                        map.put("PassengerAddDate", classDate.date());
                                        map.put("PassengerId", passengers.get(position).id);
                                        map.put("RequestId", act.getText().toString());
                                        map.put("TransId", classDate.currentTimeAtMs());

                                        FirebaseFirestore.getInstance().collection("Trips")
                                                .document(map.get("TransId"))
                                                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Trip2Activity.this, "تم إضافة الراكب", Toast.LENGTH_SHORT).show();
                                                    recreate(); }
                                                else
                                                    Toast.makeText(Trip2Activity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).create().show();
                    }
                }).create().show();

            }
        });

        new AlertDialog.Builder(Trip2Activity.this)
                .setView(list)
                .create().show();

    }

    public void ViewVisa(View view) {


        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder((Trip2Activity.this));
        LayoutInflater inflater = (Trip2Activity.this).getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_visa_view, null));
        final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
        ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog2.show();
        dialog2.getWindow().setAttributes(lp);
        dialog2.setCanceledOnTouchOutside(false);

        AutoCompleteTextView act = dialog2.findViewById(R.id.sea);
        EditText name = dialog2.findViewById(R.id.txvDate1);
        EditText req = dialog2.findViewById(R.id.txvDate12);
        EditText conf = dialog2.findViewById(R.id.txvDate2);
        EditText trans = dialog2.findViewById(R.id.txvDate3);

        ImageView img_dia1 = dialog2.findViewById(R.id.imageView);

        Spinner pconfirm = dialog2.findViewById(R.id.txvDate6);

        name.setText(MyRequestActivity.requestsClass.userName);
        req.setText(MyRequestActivity.requestsClass.dateOfRequest);
        conf.setText(MyRequestActivity.requestsClass.dateOfPermit);
        trans.setText(MyRequestActivity.requestsClass.transId);

        if(MyRequestActivity.requestsClass.payment.equals("") || MyRequestActivity.requestsClass.payment.equals("0"))
            pconfirm.setSelection(1);
        else
            pconfirm.setSelection(0);


        Picasso.get().load(Uri.parse(MyRequestActivity.requestsClass.img)).into(img_dia1);

    }

    public class ShowImageDialog extends Dialog {
        Activity c ;
        Uri urrri ;
        public ShowImageDialog(Uri urrri){
            super(Trip2Activity.this);
            c= Trip2Activity.this;
            this.urrri = urrri;
        }

        PhotoView imageView ;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_show_image);
            imageView = findViewById(R.id.imageViewMain);
            Picasso.get().load(urrri).into(imageView);
        }
    }
}