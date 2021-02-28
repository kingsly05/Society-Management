package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
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

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
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


public class AdminMemDetActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private Toolbar _toolbar;
    private CardView UI_cardview3;
    private Button cancel;
    private EditText edittext1;
    private SwipeRefreshLayout refresh;
    private FloatingActionButton _fab, _fabsearch, _fabadd;
    Float transitionY = 100f;
    Float transitionX = 30f;
    private double n = 0;
    private double len = 0;
    private String searchby = "";
    private Spinner spinner1;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private Boolean isMenuOpen = false;
    private Boolean isSearchOpen = false;
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<String> liststring = new ArrayList<>();
    private HashMap<String, Object> map = new HashMap<>();

    private ListView listview1;
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private Intent intent = new Intent();
    private SharedPreferences file;
    private ArrayList<String> search= new ArrayList<>();
    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mem_det);
        initialize();
    }

    private void initialize() {
        _fab = (FloatingActionButton) findViewById(R.id._fab);
        _fabsearch = (FloatingActionButton) findViewById(R.id._fabsearch);
        _fabadd = (FloatingActionButton) findViewById(R.id._fabadd);
        listview1 = (ListView) findViewById(R.id.listview1);
        Currauth = FirebaseAuth.getInstance();
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);
        spinner1 = findViewById(R.id.spinner1);
        UI_cardview3 = findViewById(R.id.UI_cardview3);
        cancel = findViewById(R.id.cancel);
        refresh = findViewById(R.id.refresh);
        edittext1 = findViewById(R.id.edittext1);

        _fabadd.setAlpha(0f);
        _fabsearch.setAlpha(0f);
        UI_cardview3.setAlpha(0f);
        cancel.setAlpha(0f);

        _fabadd.setTranslationY(transitionY);
        _fabsearch.setTranslationY(transitionX);
        UI_cardview3.setTranslationX(transitionX);
        cancel.setTranslationY(transitionY);

        _fab.setOnClickListener(this);
        _fabadd.setOnClickListener(this);
        _fabsearch.setOnClickListener(this);
        cancel.setOnClickListener(this);


        search.add("Name");
        search.add("Member ID");
        search.add("Email ID");
        search.add("Phone Number");
        spinner1.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,search));


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Currdata.removeEventListener(_Currdata_child_listener);
                listmap.clear();
                Currdata.addChildEventListener(_Currdata_child_listener);
                refresh.setRefreshing(false);
                listview1.setAdapter(new Listview1Adapter(listmap));
                ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
                final int _position = _param3;
                ((TextView) _param1.getChildAt(0)).setTextColor(Color.GRAY);

                edittext1.setText("");
                if(spinner1.getSelectedItem() != null)
                {
                    if(spinner1.getSelectedItemId() == 0)
                    {
                        searchby = "name";
                    }else if(spinner1.getSelectedItemId() == 1)
                    {
                        searchby = "memberid";
                    }else if(spinner1.getSelectedItemId() == 2)
                    {
                        searchby = "emailid";
                    }else if(spinner1.getSelectedItemId() == 3)
                    {
                        searchby = "phonenumber";
                    }else{
                        searchby = "name";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> _param1) {

            }
        });

        edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
                final String _charSeq = _param1.toString();
                Currdata.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        listmap = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                listmap.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                        if (_charSeq.length() > 0) {
                            n = listmap.size() - 1;
                            len = listmap.size();
                            for(int _repeat20 = 0; _repeat20 < (int)(len); _repeat20++) {
                                if (listmap.get((int)n).get(searchby).toString().toLowerCase().contains(_charSeq.toLowerCase())) {

                                }
                                else {
                                    listmap.remove((int)(n));
                                }
                                n--;
                            }
                        }
                        listview1.setAdapter(new Listview1Adapter(listmap));
                        ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
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
                Currdata.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        listmap = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                listmap.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                         map = new HashMap<>();
                        map.put("name", _childValue.get("name").toString());
                        map.put("maintenancep", _childValue.get("maintenancep").toString());
                        map.put("memberid", _childValue.get("memberid").toString());
                        map.put("miscellaneousp", _childValue.get("miscellaneousp").toString());
                        map.put("maintenance", _childValue.get("maintenance").toString());
                        map.put("miscellaneous", _childValue.get("miscellaneous").toString());
                        map.put("status", _childValue.get("status").toString());
                        map.put("flattype", _childValue.get("flattype").toString());
                        map.put("profileurl", _childValue.get("profileurl").toString());
                        map.put("profilename", _childValue.get("profilename").toString());
                        listmap.add(map);
                        liststring.clear();
                        for(DataSnapshot dshot:_dataSnapshot.getChildren()){liststring.add(dshot.getKey());}
                        listview1.setAdapter(new Listview1Adapter(listmap));
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
                Currdata.removeEventListener(_Currdata_child_listener);
                listmap.clear();
                Currdata.addChildEventListener(_Currdata_child_listener);

            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                Currdata.removeEventListener(_Currdata_child_listener);
                listmap.clear();
                Currdata.addChildEventListener(_Currdata_child_listener);
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

    private void openMenu()
    {
        isMenuOpen = !isMenuOpen;
        _fab.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        _fabadd.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        _fabsearch.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu()
    {
        isMenuOpen = !isMenuOpen;
        _fab.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        _fabadd.animate().translationY(transitionY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        _fabsearch.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void openSearch()
    {
        isSearchOpen = !isSearchOpen;
        UI_cardview3.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        cancel.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        closeMenu();
    }
    private void closeSearch()
    {
        isSearchOpen = !isSearchOpen;
        UI_cardview3.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        cancel.animate().translationY(transitionY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        closeMenu();
    }
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id._fab:
                if(isMenuOpen)
                {
                    closeMenu();
                }else{
                    openMenu();
                }
                break;
            case R.id._fabadd:
                intent.setClass(getApplicationContext(), SignUpActivity.class);
                closeMenu();
                startActivity(intent);
                break;
            case R.id._fabsearch:
                spinner1.setSelection(0);
                if(isSearchOpen)
                {
                    closeSearch();
                }else{
                    openSearch();
                }
                break;
            case R.id.cancel:
                if(edittext1.getText().toString().trim().equals(""))
                {
                    spinner1.setSelection(0);
                    UI_cardview3.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
                    isSearchOpen = false;
                    isMenuOpen = false;
                }
                else{
                    spinner1.setSelection(0);
                    edittext1.setText("");
                }
                break;
        }
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
                _v = _inflater.inflate(R.layout.custom_member_detail, null);
            }

            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final LinearLayout linear2 = (LinearLayout) _v.findViewById(R.id.linear2);
            final LinearLayout linear3 = (LinearLayout) _v.findViewById(R.id.linear3);
            final LinearLayout linear12 = (LinearLayout) _v.findViewById(R.id.linear12);
            final LinearLayout linear13 = (LinearLayout) _v.findViewById(R.id.linear13);
            final TextView textview1 = (TextView) _v.findViewById(R.id.textview1);
            final TextView name = (TextView) _v.findViewById(R.id.name);
            final TextView textview3 = (TextView) _v.findViewById(R.id.textview3);
            final TextView memberid = (TextView) _v.findViewById(R.id.memberid);
            final LinearLayout linear8 = (LinearLayout) _v.findViewById(R.id.linear8);
            final LinearLayout linear9 = (LinearLayout) _v.findViewById(R.id.linear9);
            final LinearLayout linear10 = (LinearLayout) _v.findViewById(R.id.linear10);
            final LinearLayout linear11 = (LinearLayout) _v.findViewById(R.id.linear11);
            final TextView textview13 = (TextView) _v.findViewById(R.id.textview13);
            final TextView maintenancep = (TextView) _v.findViewById(R.id.maintenancep);
            final TextView textview15 = (TextView) _v.findViewById(R.id.textview15);
            final TextView miscellaneousp = (TextView) _v.findViewById(R.id.miscellaneousp);
            final TextView textview17 = (TextView) _v.findViewById(R.id.textview17);
            final TextView maintenance = (TextView) _v.findViewById(R.id.maintenance);
            final TextView textview19 = (TextView) _v.findViewById(R.id.textview19);
            final TextView miscellaneous = (TextView) _v.findViewById(R.id.miscellaneous);
            final TextView textview20 = (TextView) _v.findViewById(R.id.textview20);
            final TextView flattype = (TextView) _v.findViewById(R.id.flattype);
            final TextView textview22 = (TextView) _v.findViewById(R.id.textview22);
            final TextView status = (TextView) _v.findViewById(R.id.status);
            final Button edit = (Button) _v.findViewById(R.id.edit);
            final Button delete = (Button) _v.findViewById(R.id.delete);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);

            name.setText(listmap.get((int)_position).get("name").toString());
            memberid.setText(listmap.get((int)_position).get("memberid").toString());
            maintenancep.setText(listmap.get((int)_position).get("maintenancep").toString());
            maintenance.setText(listmap.get((int)_position).get("maintenance").toString());
            miscellaneousp.setText(listmap.get((int)_position).get("miscellaneousp").toString());
            miscellaneous.setText(listmap.get((int)_position).get("miscellaneous").toString());
            status.setText(listmap.get((int)_position).get("status").toString());
            flattype.setText(listmap.get((int)_position).get("flattype").toString());


            if(listmap.get((int)_position).get("status").toString().toLowerCase().equals("unpaid"))
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

            imageview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(listmap.get((int)_position).get("profileurl").toString().equals(""))) {
                        ImageView imageView, imageView1;
                        Button btnok, btncancel;
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMemDetActivity.this);

                        View view1 = getLayoutInflater().inflate(R.layout.custom_image_pop_up, null);

                        builder.setView(view1);
                        final AlertDialog alertDialog = builder.create();
                        imageView = view1.findViewById(R.id.imageview1);
                        Glide.with(getApplicationContext()).load(Uri.parse(listmap.get((int)_position).get("profileurl").toString())).into(imageView);

                        alertDialog.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }else{
                        ImageView imageView, imageView1;
                        Button btnok, btncancel;
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMemDetActivity.this);

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

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    file.edit().putString("editmemberid", listmap.get((int)_position).get("memberid").toString()).commit();
                    intent.setClass(getApplicationContext(), AdminMemDetEditActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(imageview1,"imageview1Trans");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AdminMemDetActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Currdata.child(liststring.get((int)(_position))).removeValue();
                }
            });

            return _v;
        }
    } 

}