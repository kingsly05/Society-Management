package com.sonawane_ad.societymanagement;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.*;
import android.graphics.drawable.ColorDrawable;
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
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.SharedPreferences;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import android.view.View;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

public class AdminVotingActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private Toolbar _toolbar;
    private FloatingActionButton _fab;

    private ArrayList<HashMap<String, Object>> votelistmap = new ArrayList<>();

    private ArrayList<String> liststring = new ArrayList<>();
    private ListView listview1;

    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private StorageReference votestore = _firebase_storage.getReference("votestore");
    private OnCompleteListener<Uri> _votestore_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _votestore_download_success_listener;
    private OnSuccessListener _votestore_delete_success_listener;
    private OnProgressListener _votestore_upload_progress_listener;
    private OnProgressListener _votestore_download_progress_listener;
    private OnFailureListener _votestore_failure_listener;
    private Intent intent = new Intent();
    private SharedPreferences file;
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
        setContentView(R.layout.activity_admin_voting);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initialize();
    }

    private void initialize() {

        _fab = (FloatingActionButton) findViewById(R.id._fab);
        listview1 = (ListView) findViewById(R.id.listview1);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);




        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setClass(getApplicationContext(), AdminVotingDetActivity.class);
                startActivity(intent);
            }
        });

        _Currdata_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

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

        _internet_request_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String _param1, String _param2) {
                final String _tag = _param1;
                final String _response = _param2;
                linear.setVisibility(View.VISIBLE);
                nointernet.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(String _param1, String _param2) {
                final String _tag = _param1;
                final String _message = _param2;
                linear.setVisibility(View.GONE);
                _fab.setVisibility(View.GONE);
                nointernet.setVisibility(View.VISIBLE);
            }
        };


        _Vote_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
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
                        liststring.clear();
                        for(DataSnapshot dshot:_dataSnapshot.getChildren()){liststring.add(dshot.getKey());}
                        listview1.setAdapter(new Listview1Adapter(votelistmap));
                        ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
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
                        liststring.clear();
                        for(DataSnapshot dshot:_dataSnapshot.getChildren()){liststring.add(dshot.getKey());}
                        listview1.setAdapter(new Listview1Adapter(votelistmap));
                        ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
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
                _v = _inflater.inflate(R.layout.custom_vote_details, null);
            }

            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final LinearLayout linear7 = (LinearLayout) _v.findViewById(R.id.linear7);
            final LinearLayout linear3 = (LinearLayout) _v.findViewById(R.id.linear3);
            final LinearLayout linear4 = (LinearLayout) _v.findViewById(R.id.linear4);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);
            final LinearLayout linear5 = (LinearLayout) _v.findViewById(R.id.linear5);
            final LinearLayout linear6 = (LinearLayout) _v.findViewById(R.id.linear6);
            final TextView name = (TextView) _v.findViewById(R.id.name);
            final TextView age = (TextView) _v.findViewById(R.id.age);
            final TextView gender = (TextView) _v.findViewById(R.id.gender);
            final TextView votecount = (TextView) _v.findViewById(R.id.votecount);
            final TextView textview2 = (TextView) _v.findViewById(R.id.textview2);
            final Button delete = (Button) _v.findViewById(R.id.delete);

            if (votelistmap.get((int)_position).get("profileurl").toString().equals("")) {
                imageview1.setImageResource(R.drawable.default_profile);
                name.setText(votelistmap.get((int)_position).get("name").toString());
                age.setText(votelistmap.get((int)_position).get("age").toString());
                gender.setText(votelistmap.get((int)_position).get("gender").toString());
                votecount.setText(votelistmap.get((int)_position).get("votecount").toString());
            }
            else {
                name.setText(votelistmap.get((int)_position).get("name").toString());
                age.setText(votelistmap.get((int)_position).get("age").toString());
                gender.setText(votelistmap.get((int)_position).get("gender").toString());
                votecount.setText(votelistmap.get((int)_position).get("votecount").toString());
                Glide.with(getApplicationContext()).load(Uri.parse(votelistmap.get((int)_position).get("profileurl").toString())).into(imageview1);
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Vote.child(liststring.get((int)(_position))).removeValue();
                    _firebase_storage.getReferenceFromUrl(votelistmap.get((int)_position).get("profileurl").toString()).delete().addOnSuccessListener(_votestore_delete_success_listener).addOnFailureListener(_votestore_failure_listener);
                    finish();
                }
            });
            imageview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(votelistmap.get((int)_position).get("profileurl").toString().equals(""))) {
                        ImageView imageView, imageView1;
                        Button btnok, btncancel;
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminVotingActivity.this);

                        View view1 = getLayoutInflater().inflate(R.layout.custom_image_pop_up, null);

                        builder.setView(view1);
                        final AlertDialog alertDialog = builder.create();
                        imageView = view1.findViewById(R.id.imageview1);
                        Glide.with(getApplicationContext()).load(Uri.parse(votelistmap.get((int)_position).get("profileurl").toString())).into(imageView);

                        alertDialog.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }else{
                        ImageView imageView, imageView1;
                        Button btnok, btncancel;
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminVotingActivity.this);

                        View view1 = getLayoutInflater().inflate(R.layout.custom_image_pop_up, null);

                        builder.setView(view1);
                        final AlertDialog alertDialog = builder.create();
                        imageView = view1.findViewById(R.id.imageview1);
                        imageView.setImageResource(R.drawable.default_profile);
                        alertDialog.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                }

            });
            return _v;
        }
    }
}