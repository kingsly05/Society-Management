package com.sonawane_ad.societymanagement;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.text.*;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sdsmdg.tastytoast.TastyToast;

import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

public class PaymentGatewayActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private Toolbar _toolbar;
    private FloatingActionButton _fab;
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<String> months= new ArrayList<>();
    private ArrayList<String> years= new ArrayList<>();
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private Intent intent = new Intent();
    private SharedPreferences file;
    private HashMap<String, Object> maps = new HashMap<>();

    private String Maintenance,Miscellaneous,Paidc,Memberid ="";
    private String str ,str1= "";
    private ImageView imageview1;
    private TextView name,memberid,total,maintenance,miscellaneous;
    private EditText pincode,cvv;
    private Spinner month,year;
    private Button pay;
    private Calendar calendar = Calendar.getInstance();
    private Bundle bundle;
    private ProgressDialog progressDialog;
    private LinearLayout nointernet;
    private RelativeLayout linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        initialize();
    }

    private void initialize() {

        Currauth = FirebaseAuth.getInstance();
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        imageview1 = findViewById(R.id.imageview1);
        name = findViewById(R.id.name);
        memberid = findViewById(R.id.memberid);
        total = findViewById(R.id.total);
        bundle = getIntent().getExtras();
        month = findViewById(R.id.months);
        year = findViewById(R.id.years);
        pincode = findViewById(R.id.pin);
        cvv = findViewById(R.id.cvv);
        pay = findViewById(R.id.pay);
        maintenance = findViewById(R.id.maintenance);
        miscellaneous = findViewById(R.id.miscellaneous);
        progressDialog = new ProgressDialog(PaymentGatewayActivity.this);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);



        pay.setEnabled(false);
        months.add("JAN");
        months.add("FEB");
        months.add("MAR");
        months.add("APR");
        months.add("MAY");
        months.add("JUN");
        months.add("JUL");
        months.add("AUG");
        months.add("SEP");
        months.add("OCT");
        months.add("NOV");
        months.add("DEC");
        month.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,months));
        years.add("2020");
        years.add("2021");
        years.add("2022");
        years.add("2023");
        years.add("2024");
        years.add("2025");
        years.add("2026");
        years.add("2027");
        years.add("2028");
        years.add("2029");
        years.add("2030");
        years.add("2031");
        years.add("2032");
        years.add("2033");
        years.add("2034");
        years.add("2035");
        years.add("2036");
        years.add("2037");
        years.add("2038");
        years.add("2039");
        years.add("2040");
        year.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,years));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
        progressDialog.setContentView(R.layout.custom_loading_box);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

                if (pincode.getText().toString().trim().length() == 16 && cvv.getText().toString().trim().length() == 3)
                {

                    maps = new HashMap<>();
                    calendar = Calendar.getInstance();
                    maps.put("maintenancep", Maintenance);
                    maps.put("miscellaneousp", Miscellaneous);
                    maps.put("maintenance", "-----");
                    maps.put("miscellaneous", "-----");
                    maps.put("paidc", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                    maps.put("paidp", Paidc);
                    maps.put("status", "Paid");
                    maps.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                    Currdata.child(Memberid).updateChildren(maps);
                    maps.clear();
                    progressDialog.dismiss();
                    finish();
                }else{
                    progressDialog.dismiss();
                    TastyToast.makeText(getApplicationContext(), "Please Enter all credentials", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
            }
        });



        cvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
                final String _charSeq = _param1.toString();
                if (_charSeq.length() > 3) {
                    str = _charSeq.substring((int)(0), (int)(3));
                    cvv.setText("");
                }
                if (_charSeq.length() == 0) {
                    cvv.append(str);
                    str = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });

        pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
                final String _charSeq = _param1.toString();
                if (_charSeq.length() > 16) {
                    str1 = _charSeq.substring((int)(0), (int)(16));
                    pincode.setText("");
                }
                if (_charSeq.length() == 0) {
                    pincode.append(str1);
                    str1 = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });


        _Currdata_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if(bundle.getString("emailid")!= null) {
                    if (_childValue.get("emailid").toString().equals(bundle.getString("emailid"))) {
                        name.setText(_childValue.get("name").toString());
                        memberid.setText(_childValue.get("memberid").toString());
                        if((_childValue.get("maintenance").toString()).equals("-----") && ((_childValue.get("miscellaneous").toString()).equals("-----")))
                        {
                            pay.setEnabled(false);
                            pay.setTextColor(Color.parseColor("#4CAF50"));
                            maintenance.setText(("Maintenance Rs ").concat(_childValue.get("maintenance").toString()));
                            miscellaneous.setText(("Miscellaneous Rs ").concat(_childValue.get("miscellaneous").toString()));
                        }else{
                            pay.setEnabled(true);
                            maintenance.setText(("Maintenance Rs ").concat(_childValue.get("maintenance").toString()));
                            miscellaneous.setText(("Miscellaneous Rs ").concat(_childValue.get("miscellaneous").toString()));
                        }
                        if((_childValue.get("maintenance").toString()).equals("-----")||(_childValue.get("miscellaneous").toString()).equals("-----"))
                        {
                            pay.setEnabled(false);
                            pay.setTextColor(Color.parseColor("#4CAF50"));
                            total.setText("No Current Bill to pay");
                        }else{
                            pay.setEnabled(true);
                            total.setText("Total Rs ".concat(new DecimalFormat("00").format(Double.parseDouble(_childValue.get("maintenance").toString()) + Double.parseDouble(_childValue.get("miscellaneous").toString()))));
                        }
                        if (!_childValue.get("profileurl").toString().equals("")) {
                            Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(imageview1);
                        }
                        else {
                            imageview1.setBackgroundResource(R.drawable.default_profile);
                        }

                        Memberid = _childValue.get("memberid").toString();
                        Maintenance = _childValue.get("maintenance").toString();
                        Miscellaneous = _childValue.get("miscellaneous").toString();
                        Paidc = _childValue.get("paidc").toString();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if(bundle.getString("emailid")!= null) {
                    if (_childValue.get("emailid").toString().equals(bundle.getString("emailid"))) {
                        name.setText(_childValue.get("name").toString());
                        memberid.setText(_childValue.get("memberid").toString());
                        if((_childValue.get("maintenance").toString()).equals("-----") && ((_childValue.get("miscellaneous").toString()).equals("-----")))
                        {
                            pay.setEnabled(false);
                            pay.setTextColor(Color.parseColor("#4CAF50"));
                            maintenance.setText(("Maintenance Rs ").concat(_childValue.get("maintenance").toString()));
                            miscellaneous.setText(("Miscellaneous Rs ").concat(_childValue.get("miscellaneous").toString()));
                        }else{
                            pay.setEnabled(true);
                            maintenance.setText(("Maintenance Rs ").concat(_childValue.get("maintenance").toString()));
                            miscellaneous.setText(("Miscellaneous Rs ").concat(_childValue.get("miscellaneous").toString()));
                        }
                        if((_childValue.get("maintenance").toString()).equals("-----")||(_childValue.get("miscellaneous").toString()).equals("-----"))
                        {
                            pay.setEnabled(false);
                            pay.setTextColor(Color.parseColor("#4CAF50"));
                            total.setText("No Current Bill to pay");
                        }else{
                            pay.setEnabled(true);
                            total.setText("Total Rs ".concat(new DecimalFormat("00").format(Double.parseDouble(_childValue.get("maintenance").toString()) + Double.parseDouble(_childValue.get("miscellaneous").toString()))));
                        }
                        if (!_childValue.get("profileurl").toString().equals("")) {
                            Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(imageview1);
                        }
                        else {
                            imageview1.setBackgroundResource(R.drawable.default_profile);
                        }

                        Memberid = _childValue.get("memberid").toString();
                        Maintenance = _childValue.get("maintenance").toString();
                        Miscellaneous = _childValue.get("miscellaneous").toString();
                        Paidc = _childValue.get("paidc").toString();
                    }
                }

            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        Currdata.addChildEventListener(_Currdata_child_listener);

    }
}
