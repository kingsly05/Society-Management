package com.sonawane_ad.societymanagement;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    public final int REQ_CD_PROFILEPIC = 101;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();

    private HashMap<String, Object> hashmap = new HashMap<>();
    private String profileimgpath = "";
    private String profileimgname = "";
    private double img = 0;
    private double n = 0;
    private double exist = 0;

    private ArrayList<String> liststring = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    private ImageView imageview1,remove_img,add_img;
    private EditText edittext1;
    private EditText edittext2;
    private EditText edittext3;
    private EditText edittext5;
    private EditText edittext6;
    private EditText edittext7;
    private Button button1;
    private TextView textview1;

    private String str,str1 ="";
    private Intent intent = new Intent();
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private StorageReference profile = _firebase_storage.getReference("profile");
    private OnCompleteListener<Uri> _profile_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _profile_download_success_listener;
    private OnSuccessListener _profile_delete_success_listener;
    private OnProgressListener _profile_upload_progress_listener;
    private OnProgressListener _profile_download_progress_listener;
    private OnFailureListener _profile_failure_listener;
    private Intent profilepic = new Intent(Intent.ACTION_GET_CONTENT);
    private Calendar calendar = Calendar.getInstance();
    private SharedPreferences file;
    private  Window window;
    private ProgressDialog progressDialog;
    private long profileimgsize=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        com.google.firebase.FirebaseApp.initializeApp(this);
        window = this.getWindow();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.semi_transparent));

        initialize();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);

        }
        img = 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void initialize() {

        imageview1 = (ImageView) findViewById(R.id.imageview1);
        edittext1 = (EditText) findViewById(R.id.edittext1);
        edittext2 = (EditText) findViewById(R.id.edittext2);
        edittext3 = (EditText) findViewById(R.id.edittext3);
        edittext5 = (EditText) findViewById(R.id.edittext5);
        edittext6 = (EditText) findViewById(R.id.edittext6);
        edittext7 = (EditText) findViewById(R.id.edittext7);
        button1 = (Button) findViewById(R.id.button1);
        textview1 = (TextView) findViewById(R.id.textview1);
        remove_img = findViewById(R.id.del_img);
        add_img = findViewById(R.id.add_img);
        progressDialog = new ProgressDialog(SignUpActivity.this);


        profilepic.setType("image/*");
        profilepic.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        Random random = new Random();
        edittext6.setText(String.valueOf(random.nextInt(9999)));

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(profilepic, REQ_CD_PROFILEPIC);
            }
        });

        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(profilepic, REQ_CD_PROFILEPIC);
            }
        });
        remove_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileimgpath = "";
                profileimgname = "";
                imageview1.setImageResource(R.drawable.default_profile);
                img = 0;
                add_img.setVisibility(View.VISIBLE);
                remove_img.setVisibility(View.GONE);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (edittext1.getText().toString().equals("") && (edittext2.getText().toString().equals("") && (edittext3.getText().toString().equals("") && (edittext5.getText().toString().equals("") && (edittext6.getText().toString().equals("") && edittext7.getText().toString().equals("")))))) {
                    TastyToast.makeText(getApplicationContext(), "Please Enter all credentials", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
                else {
                    if (edittext3.length() >= 8){
                    if (img == 0) {
                        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_loading_box);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
                        n = 0;
                        exist = 0;
                        for (int _repeat156 = 0; _repeat156 < (int) (listmap.size()); _repeat156++) {
                            if (listmap.get((int) n).get("memberid").toString().toLowerCase().equals(edittext6.getText().toString().toLowerCase())) {
                                TastyToast.makeText(getApplicationContext(), "Id Already in Use", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                                progressDialog.dismiss();
                                exist = 1;
                            }
                            n++;
                        }
                        if (exist == 0) {
                            Currauth.createUserWithEmailAndPassword(edittext2.getText().toString(), edittext3.getText().toString()).addOnCompleteListener(SignUpActivity.this, _Currauth_create_user_listener);
                            hashmap = new HashMap<>();
                            hashmap.put("name", edittext1.getText().toString());
                            hashmap.put("emailid", edittext2.getText().toString());
                            hashmap.put("password", edittext3.getText().toString());
                            hashmap.put("memberid", edittext6.getText().toString());
                            hashmap.put("flattype", edittext5.getText().toString());
                            hashmap.put("phonenumber", edittext7.getText().toString());
                            hashmap.put("maintenance", "-----");
                            hashmap.put("miscellaneousp", "-----");
                            hashmap.put("miscellaneous", "-----");
                            hashmap.put("maintenancep", "-----");
                            hashmap.put("user_uid", "");
                            hashmap.put("profilename", "");
                            hashmap.put("profileurl", "");
                            hashmap.put("paidp", "");
                            hashmap.put("paidc", "");
                            calendar = Calendar.getInstance();
                            hashmap.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                            hashmap.put("status", "Unpaid");
                            Currdata.child(edittext6.getText().toString()).updateChildren(hashmap);
                            TastyToast.makeText(getApplicationContext(), "Successfully SignUp", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                progressDialog.dismiss();
                            finish();
                        } else {

                        }
                    } else {
                        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_loading_box);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

                        n = 0;
                        exist = 0;
                        for (int _repeat196 = 0; _repeat196 < (int) (listmap.size()); _repeat196++) {
                            if (listmap.get((int) n).get("memberid").toString().toLowerCase().equals(edittext6.getText().toString().toLowerCase())) {
                                TastyToast.makeText(getApplicationContext(), "Id Already in Use", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                                progressDialog.dismiss();
                                exist = 1;
                            }
                            n++;
                        }
                        if (exist == 0) {
                            profile.child(profileimgname).putFile(Uri.fromFile(new File(profileimgpath))).addOnFailureListener(_profile_failure_listener).addOnProgressListener(_profile_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    return profile.child(profileimgname).getDownloadUrl();
                                }
                            }).addOnCompleteListener(_profile_upload_success_listener);
                            button1.setEnabled(false);
                        } else {

                        }
                    }
                }else{
                        edittext3.setText("");
                        TastyToast.makeText(getApplicationContext(), "Password Length is Small", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    }
                }
            }
        });

        textview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setClass(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        edittext3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
                final String _charSeq = _param1.toString();
                if (_charSeq.length() > 16) {
                    str1 = _charSeq.substring((int)(0), (int)(16));
                    edittext3.setText("");
                }
                if (_charSeq.length() == 0) {
                    edittext3.append(str1);
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

        edittext7.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
                final String _charSeq = _param1.toString();
                if (_charSeq.length() > 10) {
                    str = _charSeq.substring((int)(0), (int)(10));
                    edittext7.setText("");
                }
                if (_charSeq.length() == 0) {
                    edittext7.append(str);
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

        _profile_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _profile_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _profile_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                Currauth.createUserWithEmailAndPassword(edittext2.getText().toString(), edittext3.getText().toString()).addOnCompleteListener(SignUpActivity.this, _Currauth_create_user_listener);
                hashmap = new HashMap<>();
                hashmap.put("name", edittext1.getText().toString());
                hashmap.put("emailid", edittext2.getText().toString());
                hashmap.put("password", edittext3.getText().toString());
                hashmap.put("memberid", edittext6.getText().toString());
                hashmap.put("flattype", edittext5.getText().toString());
                hashmap.put("phonenumber", edittext7.getText().toString());
                hashmap.put("maintenance", "-----");
                hashmap.put("status", "Unpaid");
                hashmap.put("miscellaneous", "-----");
                hashmap.put("maintenancep", "-----");
                hashmap.put("miscellaneousp", "-----");
                hashmap.put("profilename", profileimgname);
                hashmap.put("profileurl", _downloadUrl);
                hashmap.put("user_uid", "");
                hashmap.put("paidp","");
                hashmap.put("paidc","");
                calendar = Calendar.getInstance();
                hashmap.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                Currdata.child(edittext6.getText().toString()).updateChildren(hashmap);
                button1.setEnabled(true);
                progressDialog.dismiss();
                TastyToast.makeText(getApplicationContext(), "Successfully SignUp", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                hashmap.clear();
                finish();
            }
        };

        _profile_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _profile_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _profile_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

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


    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_PROFILEPIC:
                if (_resultCode == Activity.RESULT_OK) {
                    ArrayList<String> _filePath = new ArrayList<>();
                    if (_data != null) {
                        if (_data.getClipData() != null) {
                            for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                                ClipData.Item _item = _data.getClipData().getItemAt(_index);
                                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                            }
                        }
                        else {
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                        }
                    }
                    add_img.setVisibility(View.GONE);
                    remove_img.setVisibility(View.VISIBLE);

                    profileimgname = "";
                    profileimgpath = "";
                    profileimgsize = 0;
                    img = 1;
                    profileimgpath = _filePath.get((int)(0));
                    profileimgname = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                    imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                    imageview1.setVisibility(View.VISIBLE);
                    profileimgsize= new java.io.File(profileimgpath).length()/1024;
                        if ((profileimgsize < 2000) && (profileimgsize > 0)) {
                            profileimgpath = _filePath.get((int)(0));
                            profileimgname = Uri.parse(profileimgpath).getLastPathSegment();
                            imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(profileimgpath, 1024, 1024));
                            img=1;
                            add_img.setVisibility(View.GONE);
                            remove_img.setVisibility(View.VISIBLE);
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Profile Pic must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            profileimgpath = "";
                            profileimgname = "";
                            imageview1.setImageResource(R.drawable.default_profile);
                            img = 0;
                            add_img.setVisibility(View.VISIBLE);
                            remove_img.setVisibility(View.GONE);
                        }
                    window.setStatusBarColor(ContextCompat.getColor(this,R.color.semi_transparent));

                }
                else {

                }
                break;
            default:
                break;
        }
    }

}