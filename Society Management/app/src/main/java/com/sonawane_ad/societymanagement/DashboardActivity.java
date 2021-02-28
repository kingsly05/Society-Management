package com.sonawane_ad.societymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {

    private CardView cd1,cd2,cd3,cd4,cd5,cd6,cd7,cd8;
    private TextView username,useraddress;
    private ImageButton logoutbtn;
    private ImageView imageview1;
    Dialog myDialog;
    private LinearLayout nointernet,linear;
    private Button retry;

    String TAG="DashboardActivity";
    double soslat, soslon;
    private String get_name = "";
    private String db_name = "";
    private String useremail = "";
    private HashMap<String, Object> map = new HashMap<>();
    private String memberid = "";

//    BottomNavigationView bottomNavigationView;

    Intent intent = new Intent();
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private DatabaseReference chat = _firebase.getReference("chat");
    private ChildEventListener _chat_child_listener;

    private SharedPreferences file;
    private AlertDialog.Builder dialog;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver;
    private static final int REQUEST_CALL = 1;
    String address;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        com.google.firebase.FirebaseApp.initializeApp(this);

        initialize();
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
        initializeLogic();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this, "Permsission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            soslat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            soslon = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Log.e("Lat",String.valueOf(soslat));
                            Log.e("lon",String.valueOf(soslon));

                            Location location = new Location("ProvideNA");
                            location.setLatitude(soslat);
                            location.setLongitude(soslon);
                            //   SharedPreferencesConFig.saveLat(String.valueOf(latitude),getApplicationContext());
                            // SharedPreferencesConFig.saveLon(String.valueOf(longitude),getApplicationContext());

                            fetchAddressFromLatLong(location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler){
            super(handler);
        } protected void onReceiveResult(int resultCode,Bundle resultData){
        super.onReceiveResult(resultCode,resultData);
        if(resultCode == Constants.SUCCESS_RESULT){
            address = resultData.getString(Constants.RESULT_DATA_KEY);
            useraddress.setText(address);
            file.edit().putString("Current_address",address).apply();
            Log.e("RESULT",address);

        }else{
            Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
        }
    }
    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent=new Intent(DashboardActivity.this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        getApplicationContext().startService(intent);
    }


    private void initializeLogic() {
        useremail = file.getString("emailid", "");
        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
    }

    private void initialize() {
        cd1 = findViewById(R.id.UI_cardview1);
        cd2 = findViewById(R.id.UI_cardview2);
        cd3 = findViewById(R.id.UI_cardview3);
        cd4 = findViewById(R.id.UI_cardview4);
        cd5 = findViewById(R.id.UI_cardview5);
        cd6 = findViewById(R.id.UI_cardview6);
        cd7 = findViewById(R.id.UI_cardview7);
        cd8 = findViewById(R.id.UI_cardview8);
        imageview1 =  findViewById(R.id.imageview1);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);

//     <com.google.android.material.bottomnavigation.BottomNavigationView
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        app:itemBackground="@color/navbar"
//        android:layout_alignParentBottom="true"
//        app:itemTextColor="@drawable/nav_bar_selector"
//        app:itemIconTint="@drawable/nav_bar_selector"
//        app:menu="@menu/nav_bar_menu"
//        android:id="@+id/UI_bottomnavbar"/>
//        bottomNavigationView = findViewById(R.id.UI_bottomnavbar);
        username = findViewById(R.id.UI_Username_txt);
        useraddress = findViewById(R.id.UI_address_txt);
        logoutbtn = findViewById(R.id.UI_login_btn);

        dialog = new AlertDialog.Builder(this);
        resultReceiver = new AddressResultReceiver(new Handler());
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

//        bottomNavigationView.setSelectedItemId(R.id.profile);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId())
//                {
//                    case R.id.profile:
//                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                    case R.id.bill:
//                        startActivity(new Intent(getApplicationContext(),BillActivity.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                    case R.id.notice:
//                        if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
//                            startActivity(new Intent(getApplicationContext(),AdminNoticeActivity.class));
//                            overridePendingTransition(0,0);
//                        }else{
//                            startActivity(new Intent(getApplicationContext(),NoticeActivity.class));
//                            overridePendingTransition(0,0);
//                        }
//                        return true;
//                    case R.id.complaint:
//                        if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
//                            startActivity(new Intent(getApplicationContext(),AdminComplaintActivity.class));
//                            overridePendingTransition(0,0);
//                        }else{
//                            startActivity(new Intent(getApplicationContext(),ComplaintActivity.class));
//                            overridePendingTransition(0,0);
//                        }
//
//                        return true;
//                }
//                return false;
//            }
//        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setTitle("Logout");
                dialog.setMessage("Do you want to logout?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        FirebaseAuth.getInstance().signOut();
                        file.edit().putString("emailid", "").commit();
                        file.edit().putString("profileurl", "").commit();
                        intent.setClass(getApplicationContext(), LoginActivity.class);
                        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {

                    }
                });
                dialog.create().show();
            }
        });

        cd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(),ProfileActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(imageview1,"imageview1Trans");
                pairs[1] = new Pair<View, String>(username,"UI_Username_textTrans");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DashboardActivity.this,pairs);
                internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                startActivity(intent,options.toBundle());

            }
        });
        cd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(),BillActivity.class);
                startActivity(intent);
            }
        });
        cd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
                    intent.setClass(getApplicationContext(), AdminNoticeActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }else{
                    intent.setClass(getApplicationContext(), NoticeActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }
            }
        });
        cd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
                    intent.setClass(getApplicationContext(), AdminComplaintActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }else{
                    intent.setClass(getApplicationContext(), ComplaintActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }
            }
        });
        cd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
                    intent.setClass(getApplicationContext(), AdminPaymentActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }else{
                    intent.setClass(getApplicationContext(), PaymentActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }
            }
        });
        cd6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(),ChatActivity.class);
                internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                startActivity(intent);
            }
        });
        cd7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    intent.setClass(getApplicationContext(), AdminVotingActivity.class);
                    startActivity(intent);
                }else{
                    intent.setClass(getApplicationContext(), VotingActivity.class);
                    internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                    startActivity(intent);
                }
            }
        });
        if((file.getString("emailid","").toLowerCase().equals("admin123@gmail.com"))) {
            cd8.setVisibility(View.VISIBLE);
            cd8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.setClass(getApplicationContext(),AdminMemDetActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            cd8.setVisibility(View.GONE);
        }

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
            }
        });
        //Currdata

        _Currdata_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
                if (!file.getString("emailid", "").equals("")) {
                    if (_childValue.get("emailid").toString().equals(useremail)) {
                        if (!_childValue.get("profileurl").toString().equals("")) {
                            username.setText("Hi, ".concat(_childValue.get("name").toString()));
                            Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(imageview1);
                            file.edit().putString("name", _childValue.get("name").toString()).commit();
                            file.edit().putString("profileurl", _childValue.get("profileurl").toString()).commit();
                        }
                        else {
                            username.setText("Hi, ".concat(_childValue.get("name").toString()));
                            file.edit().putString("name", _childValue.get("name").toString()).commit();
                            imageview1.setBackgroundResource(R.drawable.default_profile);
                        }
                        memberid = _childValue.get("memberid").toString();
                        map = new HashMap<>();
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Currdata.child(memberid).updateChildren(map);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (!file.getString("emailid", "").equals("")) {
                    if (_childValue.get("emailid").toString().equals(useremail)) {
                        if (!_childValue.get("profileurl").toString().equals("")) {
                            username.setText("Hi, ".concat(_childValue.get("name").toString()));
                            Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(imageview1);
                            file.edit().putString("name", _childValue.get("name").toString()).commit();
                            file.edit().putString("profileurl", _childValue.get("profileurl").toString()).commit();
                        }
                        else {
                            username.setText("Hi, ".concat(_childValue.get("name").toString()));
                            file.edit().putString("name", _childValue.get("name").toString()).commit();
                            imageview1.setImageResource(R.drawable.default_profile);
                        }
                        memberid = _childValue.get("memberid").toString();
                        map = new HashMap<>();
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Currdata.child(memberid).updateChildren(map);
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


        _chat_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                //notification();
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                chat.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {

                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                //notification();
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }

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
        chat.addChildEventListener(_chat_child_listener);

        //Image PopUp

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView,imageView1;
                Button btnok,btncancel;
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);

                View view1 = getLayoutInflater().inflate(R.layout.custom_image_pop_up,null);

                builder.setView(view1);
                final AlertDialog alertDialog = builder.create();
                imageView = view1.findViewById(R.id.imageview1);
                Glide.with(getApplicationContext()).load(Uri.parse(file.getString("profileurl", ""))).into(imageView);

                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

        });

        //Image PopUp
    }


//    private void notification() {
//        String message = "New message arrived";
//        final Notification.Builder builder = new Notification.Builder(DashboardActivity.this).setSmallIcon(R.drawable.app_icon).setContentTitle("Society").setContentText(message).setAutoCancel(true);
//        Intent intent = new Intent(DashboardActivity.this, ChatActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(DashboardActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        final NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        final int id = 0;
//        notificationManager.notify(id, builder.build());
//
//        Handler handler = new Handler();
//        long delayInMilliseconds = 20000;
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                notificationManager.cancel(id);
//                finish();
//            }
//        }, delayInMilliseconds);
////        builder.
//    }


    @Override
    public void onBackPressed() {
        if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
            dialog.setTitle("Exit");
            dialog.setMessage("Do you want to Exit?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    finish();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {

                }
            });
            dialog.create().show();
        }
        else {

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        internet.startRequestNetwork(RequestNetworkController.GET, "https://google.com", "A", _internet_request_listener);
    }
}