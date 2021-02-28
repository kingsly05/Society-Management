package com.sonawane_ad.societymanagement;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.HashMap;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import com.bumptech.glide.Glide;


public class VotingActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private Toolbar _toolbar;
    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> votemap = new HashMap<>();
    private String name = "";
    private double pos = 0;
    private double flag = 0;
    private String voteby = "";

    private ArrayList<HashMap<String, Object>> votelistmap = new ArrayList<>();

    private LinearLayout linear1;
    private LinearLayout linear2;
    private LinearLayout thankslinear;
    private LinearLayout votelinear;
    private ImageView imageview1;
    private TextView textview1;
    private TextView textview2;
    private TextView textview3;
    private ListView listview1;

    private Intent intent = new Intent();
    private SharedPreferences file;
    private Calendar calendar = Calendar.getInstance();
    private AlertDialog.Builder dialog;
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private DatabaseReference Vote = _firebase.getReference("Vote");
    private ChildEventListener _Vote_child_listener;

    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initialize();
        initializeLogic();
    }

    private void initializeLogic() {
        flag = 0;
        thankslinear.setVisibility(View.GONE);
        votelinear.setVisibility(View.GONE);
    }

    private void initialize() {

        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        thankslinear = (LinearLayout) findViewById(R.id.thankslinear);
        votelinear = (LinearLayout) findViewById(R.id.votelinear);
        imageview1 = (ImageView) findViewById(R.id.imageview1);
        textview1 = (TextView) findViewById(R.id.textview1);
        textview2 = (TextView) findViewById(R.id.textview2);
        textview3 = (TextView) findViewById(R.id.textview3);
        listview1 = (ListView) findViewById(R.id.listview1);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        dialog = new AlertDialog.Builder(this);
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
                if (file.getString("emailid", "").equals("")) {

                }
                else {
                    if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                        name = _childValue.get("name").toString();
                        textview2.setText(_childValue.get("name").toString());
                        if (_childValue.get("profileurl").toString().equals("")) {
                            imageview1.setImageResource(R.drawable.default_profile);
                        }
                        else {
                            Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(imageview1);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

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

        _Vote_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.containsKey("voteby")) {
                    voteby = voteby.concat(" , ".concat(_childValue.get("voteby").toString()));
                }
                else {

                }
                if (voteby.contains(file.getString("emailid", ""))) {
                    thankslinear.setVisibility(View.VISIBLE);
                    votelinear.setVisibility(View.GONE);
                }
                else {
                    thankslinear.setVisibility(View.GONE);
                    votelinear.setVisibility(View.VISIBLE);
                    Vote.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot _dataSnapshot) {
                            votelistmap = new ArrayList<>();
                            try {
                                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                                for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                    HashMap<String, Object> _map = _data.getValue(_ind);
                                    votelistmap.add(_map);
                                }
                            }
                            catch (Exception _e) {
                                _e.printStackTrace();
                            }
                            listview1.setAdapter(new Listview1Adapter(votelistmap));
                            ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError _databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.containsKey("voteby")) {
                    voteby = voteby.concat(" , ".concat(_childValue.get("voteby").toString()));
                }
                else {

                }
                if (voteby.contains(file.getString("emailid", ""))) {
                    thankslinear.setVisibility(View.VISIBLE);
                    votelinear.setVisibility(View.GONE);
                }
                else {
                    thankslinear.setVisibility(View.GONE);
                    votelinear.setVisibility(View.VISIBLE);
                    Vote.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot _dataSnapshot) {
                            votelistmap = new ArrayList<>();
                            try {
                                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                                for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                    HashMap<String, Object> _map = _data.getValue(_ind);
                                    votelistmap.add(_map);
                                }
                            }
                            catch (Exception _e) {
                                _e.printStackTrace();
                            }
                            listview1.setAdapter(new Listview1Adapter(votelistmap));
                            ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError _databaseError) {
                        }
                    });
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
                if (_childValue.containsKey("voteby")) {
                    voteby = voteby.concat(" , ".concat(_childValue.get("voteby").toString()));
                }
                else {

                }
                if (voteby.contains(file.getString("emailid", ""))) {
                    thankslinear.setVisibility(View.VISIBLE);
                    votelinear.setVisibility(View.GONE);
                }
                else {
                    thankslinear.setVisibility(View.GONE);
                    votelinear.setVisibility(View.VISIBLE);
                    Vote.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot _dataSnapshot) {
                            votelistmap = new ArrayList<>();
                            try {
                                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                                for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                    HashMap<String, Object> _map = _data.getValue(_ind);
                                    votelistmap.add(_map);
                                }
                            }
                            catch (Exception _e) {
                                _e.printStackTrace();
                            }
                            listview1.setAdapter(new Listview1Adapter(votelistmap));
                            ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError _databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        Vote.addChildEventListener(_Vote_child_listener);

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
                _v = _inflater.inflate(R.layout.custom_vote, null);
            }

            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final CheckBox  checkbox1 = (CheckBox) _v.findViewById(R.id.checkbox1);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);
            final TextView voter = (TextView) _v.findViewById(R.id.voter);

            if (votelistmap.get((int)_position).get("profileurl").toString().trim().equals("")) {
                imageview1.setBackgroundResource(R.drawable.default_profile);
            }else{
                Glide.with(getApplicationContext()).load(Uri.parse(votelistmap.get((int)_position).get("profileurl").toString())).into(imageview1);
            }
            voter.setText(votelistmap.get((int)_position).get("name").toString());
            if (votelistmap.get((int)_position).get("voteby").toString().contains(name)) {
                checkbox1.setChecked(true);
                linear1.setEnabled(false);
                checkbox1.setEnabled(false);
            }
            else {
                checkbox1.setEnabled(true);
                checkbox1.setChecked(false);
                linear1.setEnabled(true);
                checkbox1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View _view) {
                        dialog.setTitle(votelistmap.get((int)_position).get("name").toString());
                        dialog.setMessage("Do you want to vote ".concat(votelistmap.get((int)_position).get("name").toString().concat(" ?.")));
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {
                                checkbox1.setChecked(true);
                                votemap = new HashMap<>();
                                votemap.put("votecount", String.valueOf((long)(Double.parseDouble(votelistmap.get((int)_position).get("votecount").toString()) + Double.parseDouble("1"))));
                                votemap.put("voteby", " ".concat(votelistmap.get((int)_position).get("voteby").toString().concat(" , ".concat(file.getString("emailid", "")))));
                                Vote.child(votelistmap.get((int)_position).get("name").toString()).updateChildren(votemap);
                            }
                        });
                        dialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {
                                checkbox1.setChecked(false);
                            }
                        });
                        dialog.create().show();
                    }
                });
            }

            return _v;
        }
    }
}