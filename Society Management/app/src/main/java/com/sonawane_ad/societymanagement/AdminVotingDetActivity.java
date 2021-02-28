package com.sonawane_ad.societymanagement;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ClipData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import java.util.HashMap;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Continuation;
import com.sdsmdg.tastytoast.TastyToast;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class AdminVotingDetActivity extends AppCompatActivity {

    public final int REQ_CD_CAMERA = 101;
    public final int REQ_CD_FILEPICKER = 102;
    private String str,str1 ="";
    private String profileimgpath = "";
    private String profileimgname = "";
    private double img = 0;
    private double n = 0;
    private long imgsize = 0;
    private double exist = 0;

    private HashMap<String, Object> hashmap = new HashMap<>();
    private ArrayList<String> liststring = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();


    private ImageView imageview1,remove_img,add_img;
    private EditText name;
    private EditText emailid;
    private EditText age;
    private EditText gender;
    private Button submit;

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private Intent intent = new Intent();
    private SharedPreferences file;
    private Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_camera;
    private AlertDialog.Builder dialog;
    private Intent filepicker = new Intent(Intent.ACTION_GET_CONTENT);
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private StorageReference votestore = _firebase_storage.getReference("votestore");
    private OnCompleteListener<Uri> _votestore_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _votestore_download_success_listener;
    private OnSuccessListener _votestore_delete_success_listener;
    private OnProgressListener _votestore_upload_progress_listener;
    private OnProgressListener _votestore_download_progress_listener;
    private OnFailureListener _votestore_failure_listener;
    private DatabaseReference Vote = _firebase.getReference("Vote");
    private ChildEventListener _Vote_child_listener;
    private  Window window;
    private ProgressDialog progressDialog;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_voting_det);
        com.google.firebase.FirebaseApp.initializeApp(this);
        window = this.getWindow();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.semi_transparent));
        initialize();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    private void initialize() {

        imageview1 = (ImageView) findViewById(R.id.imageview1);
        name = (EditText) findViewById(R.id.name);
        emailid = (EditText) findViewById(R.id.emailid);
        age = (EditText) findViewById(R.id.age);
        gender = (EditText) findViewById(R.id.gender);
        remove_img = findViewById(R.id.del_img);
        add_img = findViewById(R.id.add_img);
        submit = findViewById(R.id.submit);
        progressDialog = new ProgressDialog(AdminVotingDetActivity.this);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        _file_camera = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_camera = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _uri_camera= FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_camera);
        }
        else {
            _uri_camera = Uri.fromFile(_file_camera);
        }
        camera.putExtra(MediaStore.EXTRA_OUTPUT, _uri_camera);
        camera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        dialog = new AlertDialog.Builder(this);
        filepicker.setType("image/*");
        filepicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Currauth = FirebaseAuth.getInstance();

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                dialog.setTitle("Choose mode");
                dialog.setMessage("Pick Image from");
                dialog.setPositiveButton("File", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        startActivityForResult(filepicker, REQ_CD_FILEPICKER);
                    }
                });
                dialog.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        startActivityForResult(camera, REQ_CD_CAMERA);
                    }
                });
                dialog.create().show();
            }
        });

        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setTitle("Choose mode");
                dialog.setMessage("Pick Image from");
                dialog.setPositiveButton("File", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        startActivityForResult(filepicker, REQ_CD_FILEPICKER);
                    }
                });
                dialog.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        startActivityForResult(camera, REQ_CD_CAMERA);
                    }
                });
                dialog.create().show();
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (name.getText().toString().equals("") && (age.getText().toString().equals("") && (gender.getText().toString().equals("") && (emailid.getText().toString().equals(""))))) {
                    TastyToast.makeText(getApplicationContext(), "Please Enter all credentials", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
                else {
                        if (img == 0) {
                            progressDialog.show();
        progressDialog.setContentView(R.layout.custom_loading_box);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
                            n = 0;
                            exist = 0;
                            for (int _repeat156 = 0; _repeat156 < (int) (listmap.size()); _repeat156++) {
                                if (listmap.get((int) n).get("name").toString().toLowerCase().equals(name.getText().toString().toLowerCase())) {
                                    TastyToast.makeText(getApplicationContext(), "Emaild Id Already in Use", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                                    progressDialog.dismiss();
                                    exist = 1;
                                }
                                n++;
                            }
                            if (exist == 0) {
                                hashmap = new HashMap<>();
                                hashmap.put("name", name.getText().toString());
                                hashmap.put("emailid", emailid.getText().toString());
                                hashmap.put("age", age.getText().toString());
                                hashmap.put("gender", gender.getText().toString());
                                hashmap.put("profilename", "");
                                hashmap.put("profileurl", "");
                                hashmap.put("voteby", "");
                                hashmap.put("votecount", "0");
                                Vote.child(name.getText().toString()).updateChildren(hashmap);
                                TastyToast.makeText(getApplicationContext(), "Successfully Submitted", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
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
                                if (listmap.get((int) n).get("name").toString().toLowerCase().equals(name.getText().toString().toLowerCase())) {
                                    TastyToast.makeText(getApplicationContext(), "Email Id Already in Use", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                                    progressDialog.dismiss();
                                    exist = 1;
                                }
                                n++;
                            }
                            if (exist == 0) {
                                votestore.child(profileimgname).putFile(Uri.fromFile(new File(profileimgpath))).addOnFailureListener(_votestore_failure_listener).addOnProgressListener(_votestore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        return votestore.child(profileimgname).getDownloadUrl();
                                    }
                                }).addOnCompleteListener(_votestore_upload_success_listener);
                                submit.setEnabled(false);
                            } else {

                            }
                        }

                }
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

        _votestore_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();
            }
        };

        _votestore_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();
            }
        };

        _votestore_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                hashmap = new HashMap<>();
                hashmap.put("name", name.getText().toString());
                hashmap.put("emailid", emailid.getText().toString());
                hashmap.put("age", age.getText().toString());
                hashmap.put("gender", gender.getText().toString());
                hashmap.put("profilename", profileimgname);
                hashmap.put("profileurl", _downloadUrl);
                hashmap.put("voteby", "");
                hashmap.put("votecount", "0");
                Vote.child(name.getText().toString()).updateChildren(hashmap);
                submit.setEnabled(true);
                progressDialog.dismiss();
                TastyToast.makeText(getApplicationContext(), "Successfully SignUp", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                hashmap.clear();
                finish();
            }
        };

        _votestore_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _votestore_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _votestore_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

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
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listmap = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : snapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                listmap.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
        Vote.addChildEventListener(_Vote_child_listener);

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
            case REQ_CD_CAMERA:
                if (_resultCode == Activity.RESULT_OK) {
                    String _filePath = _file_camera.getAbsolutePath();

                    profileimgpath = "";
                    profileimgname = "";
                    imgsize = 0;
                    img = 1;
                    profileimgpath = _filePath;
                    profileimgname = Uri.parse(_filePath).getLastPathSegment();
                    imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath, 1024, 1024));
                    imageview1.setVisibility(View.VISIBLE);
                    imgsize= new java.io.File(profileimgpath).length()/1024;
                    if ((imgsize < 2000) && (imgsize > 0)) {
                        profileimgpath = _filePath;
                        add_img.setVisibility(View.GONE);
                        remove_img.setVisibility(View.VISIBLE);
                        profileimgname = Uri.parse(_filePath).getLastPathSegment();
                        img = 1;
                    }
                    else {
                        TastyToast.makeText(getApplicationContext(), "Captured image must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        add_img.setVisibility(View.VISIBLE);
                        remove_img.setVisibility(View.GONE);
                        profileimgpath = "";
                        profileimgname = "";
                        imageview1.setImageResource(R.drawable.default_profile);
                        img = 0;
                    }
                }
                else {

                }
                break;
            case REQ_CD_FILEPICKER:
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
                        profileimgname = "";
                        profileimgpath = "";
                        imgsize = 0;
                        profileimgpath = _filePath.get((int)(0));
                        profileimgname = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                        imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                        imageview1.setVisibility(View.VISIBLE);
                        imgsize= new java.io.File(profileimgpath).length()/1024;
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            profileimgpath = _filePath.get((int)(0));
                            add_img.setVisibility(View.GONE);
                            remove_img.setVisibility(View.VISIBLE);
                            profileimgname = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                            img = 1;
                        }
                        else {
                            add_img.setVisibility(View.VISIBLE);
                            remove_img.setVisibility(View.GONE);
                            TastyToast.makeText(getApplicationContext(), "Attachment must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            profileimgpath = "";
                            profileimgname = "";
                            imageview1.setImageResource(R.drawable.default_profile);
                            img = 0;
                        }
                    }
                break;
            default:
                break;
        }
    }

}