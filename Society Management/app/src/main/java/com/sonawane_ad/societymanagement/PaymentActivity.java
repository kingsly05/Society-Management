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
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

public class PaymentActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private Toolbar _toolbar;
    private FloatingActionButton _fab;
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<String> liststring = new ArrayList<>();

    private ListView listview1;

    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private Intent intent = new Intent();
    private SharedPreferences file;

    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initialize();
    }

    private void initialize() {

        listview1 = (ListView) findViewById(R.id.listview1);
        Currauth = FirebaseAuth.getInstance();
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);




        _Currdata_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (!file.getString("emailid", "").equals("")) {
                    if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                        map = new HashMap<>();
                        map.put("name", _childValue.get("name").toString());
                        map.put("emailid", _childValue.get("emailid").toString());
                        map.put("memberid", _childValue.get("memberid").toString());
                        map.put("user_uid", _childValue.get("user_uid").toString());
                        map.put("maintenancep", _childValue.get("maintenancep").toString());
                        map.put("maintenance", _childValue.get("maintenance").toString());
                        map.put("miscellaneous", _childValue.get("miscellaneous").toString());
                        map.put("miscellaneousp", _childValue.get("miscellaneousp").toString());
                        map.put("flattype", _childValue.get("flattype").toString());
                        map.put("status", _childValue.get("status").toString());
                        map.put("paidp", _childValue.get("paidp").toString());
                        map.put("paidc", _childValue.get("paidc").toString());
                        map.put("profileurl", _childValue.get("profileurl").toString());
                        listmap.add(map);
                    } else {

                    }
                }
                listview1.setAdapter(new Listview1Adapter(listmap));
                ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (!file.getString("emailid", "").equals("")) {
                    if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                        map = new HashMap<>();
                        map.put("name", _childValue.get("name").toString());
                        map.put("memberid", _childValue.get("memberid").toString());
                        map.put("emailid", _childValue.get("emailid").toString());
                        map.put("user_uid", _childValue.get("user_uid").toString());
                        map.put("maintenancep", _childValue.get("maintenancep").toString());
                        map.put("maintenance", _childValue.get("maintenance").toString());
                        map.put("miscellaneous", _childValue.get("miscellaneous").toString());
                        map.put("miscellaneousp", _childValue.get("miscellaneousp").toString());
                        map.put("flattype", _childValue.get("flattype").toString());
                        map.put("status", _childValue.get("status").toString());
                        map.put("paidp", _childValue.get("paidp").toString());
                        map.put("paidc", _childValue.get("paidc").toString());
                        map.put("profileurl", _childValue.get("profileurl").toString());
                        listmap.add(map);
                    } else {

                    }
                }
                listview1.setAdapter(new Listview1Adapter(listmap));
                ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
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

        _Currauth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _Currauth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _Currauth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> _param1) {
                final boolean _success = _param1.isSuccessful();

            }
        };
    }
    public class Listview1Adapter extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }
        @Override
        public View getView(final int _position, View _view, ViewGroup _viewGroup) {
            LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _view;
            if (_v == null) {
                _v = _inflater.inflate(R.layout.custom_payment, null);
            }
            final TextView textview1 = (TextView) _v.findViewById(R.id.textview1);
            final TextView name = (TextView) _v.findViewById(R.id.name);
            final TextView textview3 = (TextView) _v.findViewById(R.id.textview3);
            final TextView memberid = (TextView) _v.findViewById(R.id.memberid);
            final TextView textview13 = (TextView) _v.findViewById(R.id.textview13);
            final TextView textview17 = (TextView) _v.findViewById(R.id.textview17);
            final TextView maintenance = (TextView) _v.findViewById(R.id.maintenance);
            final TextView textview19 = (TextView) _v.findViewById(R.id.textview19);
            final TextView miscellaneous = (TextView) _v.findViewById(R.id.miscellaneous);
            final TextView textview20 = (TextView) _v.findViewById(R.id.textview20);
            final TextView flattype = (TextView) _v.findViewById(R.id.flattype);
            final TextView textview22 = (TextView) _v.findViewById(R.id.textview22);
            final TextView status = (TextView) _v.findViewById(R.id.status);
            final Button postpone = (Button) _v.findViewById(R.id.postpone);
            final Button proceed = (Button) _v.findViewById(R.id.proceed);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);
            final TextView paidp = (TextView) _v.findViewById(R.id.paidp);
            paidp.setVisibility(View.GONE);
            name.setText(listmap.get((int)_position).get("name").toString());
            memberid.setText(listmap.get((int)_position).get("memberid").toString());
            maintenance.setText(listmap.get((int)_position).get("maintenance").toString());
            miscellaneous.setText(listmap.get((int)_position).get("miscellaneous").toString());
            status.setText(listmap.get((int)_position).get("status").toString());
            flattype.setText(listmap.get((int)_position).get("flattype").toString());
            if(listmap.get((int)_position).get("paidp").toString().equals(""))
            {
                paidp.setVisibility(View.GONE);
            }else{
                paidp.setVisibility(View.VISIBLE);
                paidp.setText(("Previous Bill Paid on ").concat(listmap.get((int) _position).get("paidp").toString()));
            }
            if(listmap.get((int)_position).get("status").toString().equals("Unpaid"))
            {
                status.setTextColor(Color.parseColor("#F44336"));
            }else{
                status.setTextColor(Color.parseColor("#4CAF50"));
            }
            if(listmap.get((int)_position).get("profileurl").toString().equals(""))
            {
                imageview1.setImageResource(R.drawable.default_profile);
            }else{
                Glide.with(getApplicationContext()).load(Uri.parse(listmap.get((int)_position).get("profileurl").toString())).into(imageview1);
            }
            postpone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    finish();
                }
            });
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    intent.setClass(getApplicationContext(),PaymentGatewayActivity.class);
                    intent.putExtra("emailid",listmap.get((int)_position).get("emailid").toString());
                    startActivity(intent);
                }
            });

            return _v;
        }
    }
}