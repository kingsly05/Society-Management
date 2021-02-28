package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import java.util.Timer;
import java.util.TimerTask;

public class ComplaintChatActivity extends AppCompatActivity {

    public final int REQ_CD_CAMER = 101;
    public final int REQ_CD_FILEPICKER = 102;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();

    private String imgpath = "";
    private VideoView videoView;
    private String imgurl = "";
    private double imgsize = 0;
    private double videosize = 0;
//    private String imgPath = "";
//    private String imgName = "";
//    private String path = "";
//    private String filename = "";
//    private String myurl = "";
//    private String result = "";
//    private double size = 0;
//    private double sumCount = 0;
    private TimerTask timer;
    private String chatroom = "";
    private String chatcopy = "";
    private String user1 = "";
    private String user2 = "";
    private HashMap<String, Object> map = new HashMap<>();
    private double img = 0;
    private LinearLayout attachex;

    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    private ListView listview1;
    private ImageView back,currprofile;
    private TextView currname;
    private ImageView imageview1,attach;
    private LinearLayout linear1;
    private ProgressBar progress;
    private EditText edittext1;
    private ImageView attachment;
    private ImageView camera;
    private ImageView button1;
    private TextView textview1;
    private int shimmerCount=0;
    private Timer _timer = new Timer();
    private ShimmerFrameLayout shimmerFrameLayout;

    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private AlertDialog.Builder dialog;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private DatabaseReference Chat1 = _firebase.getReference(""+chatroom+" ");
    private ChildEventListener _Chat1_child_listener;
    private Intent camer = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private ArrayList<String> list = new ArrayList<>();
    private File _file_camer;
    private Calendar calendar = Calendar.getInstance();
    private SharedPreferences file;
    private StorageReference complaintstore = _firebase_storage.getReference("complaintstore");
    private OnCompleteListener<Uri> _complaintstore_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _complaintstore_download_success_listener;
    private OnSuccessListener _complaintstore_delete_success_listener;
    private OnProgressListener _complaintstore_upload_progress_listener;
    private OnProgressListener _complaintstore_download_progress_listener;
    private OnFailureListener _complaintstore_failure_listener;
    private DatabaseReference Chat2 = _firebase.getReference(""+chatcopy+" ");
    private ChildEventListener _Chat2_child_listener;
    private Intent filepicker = new Intent(Intent.ACTION_GET_CONTENT);
    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_chat);
        initialize();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        else {
            initializeLogic();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initialize() {

        listview1 = (ListView) findViewById(R.id.listview1);
        imageview1 = (ImageView) findViewById(R.id.imageview1);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        edittext1 = (EditText) findViewById(R.id.edittext1);
        attachment = (ImageView) findViewById(R.id.attachment);
        camera = (ImageView) findViewById(R.id.camera);
        button1 = (ImageView) findViewById(R.id.button1);
        back = (ImageView) findViewById(R.id.back);
        currname = (TextView) findViewById(R.id.currname);
        currprofile = (ImageView) findViewById(R.id.currprofile);
        attach = (ImageView) findViewById(R.id.attach);
        textview1 = (TextView) findViewById(R.id.textview1);
        videoView = findViewById(R.id.videoview);
        attachex = findViewById(R.id.attachex);
        progress = findViewById(R.id.progress);
        Currauth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(this);
        shimmerFrameLayout = findViewById(R.id.UI_shimmer);
        shimmerFrameLayout.setVisibility(View.GONE);
        listview1.setVisibility(View.GONE);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);




        if(shimmerCount <= 2)
        {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            timer = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.setVisibility(View.GONE);
                            listview1.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.stopShimmer();
                        }
                    });
                }
            };
            _timer.schedule(timer, (int) (1000));
            shimmerCount++;
        }else{
            shimmerFrameLayout.setVisibility(View.GONE);
            listview1.setVisibility(View.VISIBLE);
        }
        _file_camer = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_camer = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _uri_camer= FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_camer);
        }
        else {
            _uri_camer = Uri.fromFile(_file_camer);
        }
        camer.putExtra(MediaStore.EXTRA_OUTPUT, _uri_camer);
        camer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        filepicker.setType("*/*");
        filepicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attachex.getVisibility() == View.GONE)
                {
                    attachex.setVisibility(View.VISIBLE);
                }else{
                    attachex.setVisibility(View.GONE);
                }
            }
        });
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(filepicker, REQ_CD_FILEPICKER);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(camer, REQ_CD_CAMER);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (img == 0) {
                    if (edittext1.getText().toString().trim().equals("")) {
                        TastyToast.makeText(getApplicationContext(), "Enter Message", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        edittext1.setText("");
                    }
                    else {
                        button1.setEnabled(false);
                        edittext1.setEnabled(false);
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        Chat1.push().updateChildren(map);
                        Chat2.push().updateChildren(map);
                        map.clear();
                        edittext1.setText("");
                        button1.setEnabled(true);
                        edittext1.setEnabled(true);
                    }
                }
                else {
                    if (img == 1) {
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            complaintstore.child(imgurl).putFile(Uri.fromFile(new File(imgpath))).addOnFailureListener(_complaintstore_failure_listener).addOnProgressListener(_complaintstore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    return complaintstore.child(imgurl).getDownloadUrl();
                                }}).addOnCompleteListener(_complaintstore_upload_success_listener);
                            button1.setEnabled(false);
                            edittext1.setEnabled(false);
                            progress.setVisibility(View.VISIBLE);
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment size must be less than 1Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }
                    }
                    if (img == 2) {
                        if ((videosize < 5000) && (videosize > 0)) {
                            complaintstore.child(imgurl).putFile(Uri.fromFile(new File(imgpath))).addOnFailureListener(_complaintstore_failure_listener).addOnProgressListener(_complaintstore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    return complaintstore.child(imgurl).getDownloadUrl();
                                }}).addOnCompleteListener(_complaintstore_upload_success_listener);
                            button1.setEnabled(false);
                            edittext1.setEnabled(false);
                            progress.setVisibility(View.VISIBLE);
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment size must be less than 5Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
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
                if (_childKey.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    user1 = _childValue.get("name").toString();

                }
                if (_childKey.equals(getIntent().getStringExtra("seconduser"))) {
                    user2 = _childValue.get("name").toString();
                }

                if (_childValue.get("user_uid").toString().equals(getIntent().getStringExtra("seconduser"))) {
                    currname.setText(_childValue.get("name").toString());
                    if (!_childValue.get("profileurl").toString().equals("")) {
                        Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(currprofile);
                    }
                    else {
                        currprofile.setBackgroundResource(R.drawable.default_profile);
                    }

                }
                else {

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

        _Chat1_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                Chat1.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Chat1.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        listview1.setAdapter(new Listview1Adapter(listmap));
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
        Chat1.addChildEventListener(_Chat1_child_listener);

        _complaintstore_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _complaintstore_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _complaintstore_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                if (edittext1.getText().toString().trim().equals("")) {
                    if(img == 1)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("image", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        Chat1.push().updateChildren(map);
                        Chat2.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }else{
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("video", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        Chat1.push().updateChildren(map);
                        Chat2.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                }
                else {
                    if(img == 1)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("image", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        Chat1.push().updateChildren(map);
                        Chat2.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }else{
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("video", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        Chat1.push().updateChildren(map);
                        Chat2.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                }
            }
        };

        _complaintstore_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _complaintstore_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _complaintstore_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

            }
        };

        _Chat2_child_listener = new ChildEventListener() {
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
        Chat2.addChildEventListener(_Chat2_child_listener);

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



    private void initializeLogic() {
        Chat1.removeEventListener(_Chat1_child_listener);
        Chat2.removeEventListener(_Chat2_child_listener);
        chatroom = "complaint/".concat(getIntent().getStringExtra("firstuser").concat("/".concat(getIntent().getStringExtra("seconduser"))));
        chatcopy = "complaint/".concat(getIntent().getStringExtra("seconduser").concat("/".concat(getIntent().getStringExtra("firstuser"))));
        Chat1=
                _firebase.getReference(chatroom);
        Chat2=
                _firebase.getReference(chatcopy);

        Chat1.addChildEventListener(_Chat1_child_listener);
        Chat2.addChildEventListener(_Chat2_child_listener);
        imageview1.setVisibility(View.GONE);
        textview1.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            initializeLogic();
        }
    }



    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_CAMER:
                if (_resultCode == Activity.RESULT_OK) {
                    String _filePath = _file_camer.getAbsolutePath();
                    attachex.setVisibility(View.GONE);
                    imgpath = "";
                    imgurl = "";
                    imgsize = 0;
                    videosize = 0;
                    img = 1;
                    imgpath = _filePath;
                    imgurl = Uri.parse(_filePath).getLastPathSegment();
                    imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath, 1024, 1024));
                    imageview1.setVisibility(View.VISIBLE);
                    imgsize= new java.io.File(imgpath).length()/1024;
                    if ((imgsize < 2000) && (imgsize > 0)) {
                        textview1.setVisibility(View.VISIBLE);
                        textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                    }
                    else {
                        TastyToast.makeText(getApplicationContext(), "Captured image must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        imageview1.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        imgpath = "";
                        imgurl = "";
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

                    if (Uri.parse(_filePath.get((int)(0))).getLastPathSegment().endsWith(".mp4"))
                    {
                        attachex.setVisibility(View.GONE);
                        imgpath = "";
                        imgurl = "";
                        imageview1.setVisibility(View.GONE);
                        img = 2;
                        imgpath = _filePath.get((int)(0));
                        imgurl = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                        videosize=new java.io.File(imgpath).length()/1024;
                        if ((videosize < 5000) && (videosize > 0)) {
                            videoView.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(Uri.parse(imgpath));
                            videoView.requestFocus();
                            videoView.start();
                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    videoView.start();
                                }
                            });
                            textview1.setVisibility(View.VISIBLE);
                            textview1.setText("File Size: ".concat(String.valueOf(videosize).concat("Kb")));
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment must be less than 5Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            attachex.setVisibility(View.VISIBLE);
                            textview1.setText("");
                            attachex.setVisibility(View.VISIBLE);
                            textview1.setVisibility(View.GONE);
                            imageview1.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            imgpath = "";
                            imgurl = "";
                            img = 0;
                        }
                    }
                    else
                    {
                        imgurl = "";
                        imgpath = "";
                        imgsize = 0;
                        videosize =0;
                        attachex.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        img = 1;
                        imgpath = _filePath.get((int)(0));
                        imgurl = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                        imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                        imageview1.setVisibility(View.VISIBLE);
                        imgsize= new java.io.File(imgpath).length()/1024;
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            textview1.setVisibility(View.VISIBLE);
                            textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            imageview1.setVisibility(View.GONE);
                            textview1.setVisibility(View.GONE);
                            attachex.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            textview1.setText("");
                            imgpath = "";
                            imgurl = "";
                            img = 0;
                        }
                    }

                }

                else {
                }
                break;
            default:
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
                _v = _inflater.inflate(R.layout.custom_chat_message, null);
            }

            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final ImageView noprofile = (ImageView) _v.findViewById(R.id.noprofile);
            final LinearLayout linear2 = (LinearLayout) _v.findViewById(R.id.linear2);
            final ImageView profile = (ImageView) _v.findViewById(R.id.profile);
            final TextView username = (TextView) _v.findViewById(R.id.username);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);
            final LinearLayout linear3 = (LinearLayout) _v.findViewById(R.id.linear3);
            final TextView message = (TextView) _v.findViewById(R.id.message);
            final TextView time = (TextView) _v.findViewById(R.id.time);
            final VideoView videoView = (VideoView) _v.findViewById(R.id.videoview);

            if (listmap.get((int)_position).containsKey("message") || listmap.get((int)_position).containsKey("image") || listmap.get((int)_position).containsKey("video")) {
                username.setText(listmap.get((int)_position).get("username").toString());
                time.setText(listmap.get((int)_position).get("time").toString());

                if (listmap.get((int)_position).get("profileurl").toString().trim().equals("")) {
                    noprofile.setBackgroundResource(R.drawable.default_profile);
                    profile.setBackgroundResource(R.drawable.default_profile);
                }else{
                    Glide.with(getApplicationContext()).load(Uri.parse(listmap.get((int)_position).get("profileurl").toString())).into(noprofile);
                    Glide.with(getApplicationContext()).load(Uri.parse(listmap.get((int)_position).get("profileurl").toString())).into(profile);
                }
            }
            if (listmap.get((int)_position).containsKey("message")) {
                message.setText(listmap.get((int)_position).get("message").toString());
                message.setVisibility(View.VISIBLE);
            }
            else {
                message.setVisibility(View.GONE);
            }
            if (listmap.get((int)_position).containsKey("image")) {
                imageview1.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(Uri.parse(listmap.get((int)_position).get("image").toString())).into(imageview1);
            }
            else {
                imageview1.setVisibility(View.GONE);
            }
            if ((listmap.get((int)_position).containsKey("video"))) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(listmap.get((int)_position).get("video").toString()));
                videoView.requestFocus();
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.start();
                    }
                });
            }
            else {
                videoView.setVisibility(View.GONE);
            }
            if (listmap.get((int)_position).get("user_uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                linear1.setGravity(Gravity.RIGHT);
                noprofile.setVisibility(View.GONE);
                username.setText("me");
                username.setGravity(Gravity.RIGHT);
                profile.setVisibility(View.VISIBLE);
                linear2.setBackgroundResource(R.drawable.chat_sender);
            }
            else {
                linear1.setGravity(Gravity.LEFT);
                profile.setVisibility(View.GONE);
                username.setGravity(Gravity.LEFT);
                noprofile.setVisibility(View.VISIBLE);
                linear2.setBackgroundResource(R.drawable.chat_receiver);

            }

            return _v;
        }
    }
}