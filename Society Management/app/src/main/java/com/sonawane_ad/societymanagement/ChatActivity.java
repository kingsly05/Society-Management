package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;
import android.content.ClipData;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import com.bumptech.glide.Glide;
import com.sdsmdg.tastytoast.TastyToast;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class ChatActivity extends AppCompatActivity {

    public final int REQ_CD_CAMERA = 101;
    public final int REQ_CD_FILEPICKER = 102;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();

    private double img = 0;
    private HashMap<String, Object> mm = new HashMap<>();
    private String get_name = "";
    private String db_name = "";
    private String imgPath = "";

    private String imgurl = "";
    private double imgsize = 0;
    private double videosize = 0;
    private String imgName = "";
    private String path = "";
    private String filename = "";
    private String myurl = "";
    private String result = "";
    private double size = 0;
    private double sumCount = 0;

    private ArrayList<HashMap<String, Object>> lop = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    private LinearLayout linear3;
    private LinearLayout linear1;
    private LinearLayout linear2;
    private ListView listview2;
    private ImageView imageview1,back;
    private EditText edittext1;
    private ImageView attachment;
    private ImageView camer;
    private LinearLayout attachex;
    private ImageView attach;
    private VideoView videoView;
    private TextView textview1;
    private ProgressBar progress;
    private ImageView button1;

    private AlertDialog.Builder dialog;
    private Intent intent = new Intent();
    private Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_camera;
    private Intent filepicker = new Intent(Intent.ACTION_GET_CONTENT);
    private Calendar calendar = Calendar.getInstance();
    private StorageReference store = _firebase_storage.getReference("store");
    private OnCompleteListener<Uri> _store_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _store_download_success_listener;
    private OnSuccessListener _store_delete_success_listener;
    private OnProgressListener _store_upload_progress_listener;
    private OnProgressListener _store_download_progress_listener;
    private OnFailureListener _store_failure_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private SharedPreferences file;
    private DatabaseReference chat = _firebase.getReference("chat");
    private ChildEventListener _chat_child_listener;
    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_chat);

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            initializeLogic();
        }
    }

    private void initialize() {


        linear3 = (LinearLayout) findViewById(R.id.linear3);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        listview2 = (ListView) findViewById(R.id.listview2);
        imageview1 = (ImageView) findViewById(R.id.imageview1);
        edittext1 = (EditText) findViewById(R.id.edittext1);
        attachment = (ImageView) findViewById(R.id.attachment);
        camer = (ImageView) findViewById(R.id.camera);
        button1 = (ImageView) findViewById(R.id.button1);
        attachex = (LinearLayout) findViewById(R.id.attachex);
        attach = (ImageView) findViewById(R.id.attach);
        progress = (ProgressBar) findViewById(R.id.progress);
        textview1 = (TextView) findViewById(R.id.textview1);
        videoView = findViewById(R.id.videoview);
        back = findViewById(R.id.back);
        dialog = new AlertDialog.Builder(this);
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
        filepicker.setType("*/*");
        filepicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Currauth = FirebaseAuth.getInstance();
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);

         

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
                final int _position = _param3;
                if (lop.get((int)_position).containsKey("image")) {
                    dialog.setTitle(lop.get((int)_position).get("username").toString());
                    dialog.setMessage(lop.get((int)_position).get("image").toString());
                    dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            myurl = lop.get((int)_position).get("image").toString();
                            new DownloadTask().execute(myurl);
                            TastyToast.makeText(getApplicationContext(), "Downloading......", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
                else if (lop.get((int)_position).containsKey("video")) {
                    dialog.setTitle(lop.get((int)_position).get("username").toString());
                    dialog.setMessage(lop.get((int)_position).get("video").toString());
                    dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            myurl = lop.get((int)_position).get("video").toString();
                            new DownloadTask().execute(myurl);
                            TastyToast.makeText(getApplicationContext(), "Downloading......", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
                else {
                    dialog.setTitle(lop.get((int)_position).get("username").toString());
                    dialog.setMessage(lop.get((int)_position).get("message").toString());
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
            }
        });

        listview2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
                final int _position = _param3;
                if (lop.get((int)_position).get("user_uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    dialog.setMessage("Delete message?");
                    dialog.setPositiveButton("DELETE FOR EVERYONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            chat.child(list.get((int)(_position))).removeValue();
                            list.remove((int)(_position));
                            TastyToast.makeText(getApplicationContext(), "Deleted", TastyToast.LENGTH_LONG, TastyToast.INFO);
                            if (lop.get((int)_position).containsKey("image")) {
                                _firebase_storage.getReferenceFromUrl(lop.get((int)_position).get("image").toString()).delete().addOnSuccessListener(_store_delete_success_listener).addOnFailureListener(_store_failure_listener);
                                TastyToast.makeText(getApplicationContext(), "Deleted", TastyToast.LENGTH_LONG, TastyToast.INFO);
                            }
                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
                listview2.setAdapter(new Listview2Adapter(lop));
                ((BaseAdapter)listview2.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
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

        camer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(camera, REQ_CD_CAMERA);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (img == 0) {
                    if (edittext1.getText().toString().trim().equals("")) {
                        TastyToast.makeText(getApplicationContext(), "Type a message", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        edittext1.setText("");
                    }
                    else {
                        mm = new HashMap<>();
                        mm.put("username", db_name);
                        mm.put("message", edittext1.getText().toString().trim());
                        mm.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mm.put("profileurl", file.getString("profileurl", ""));
                        calendar = Calendar.getInstance();
                        mm.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        chat.push().updateChildren(mm);
                        mm.clear();
                        edittext1.setText("");
                    }
                }
                else {
                    if (img == 1) {
                        if ((imgsize < 2000) && (imgsize > 0)) {
                    store.child(imgurl).putFile(Uri.fromFile(new File(imgPath))).addOnFailureListener(_store_failure_listener).addOnProgressListener(_store_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return store.child(imgurl).getDownloadUrl();
                        }}).addOnCompleteListener(_store_upload_success_listener);
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
                            store.child(imgurl).putFile(Uri.fromFile(new File(imgPath))).addOnFailureListener(_store_failure_listener).addOnProgressListener(_store_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    return store.child(imgurl).getDownloadUrl();
                                }}).addOnCompleteListener(_store_upload_success_listener);
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

        _store_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _store_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _store_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                if (edittext1.getText().toString().trim().equals("")) {
                    if (img == 1) {
                        mm = new HashMap<>();
                        mm.put("username", db_name);
//                      mm.put("message", edittext1.getText().toString().trim());
                        mm.put("image", _downloadUrl);
                        mm.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mm.put("profileurl", file.getString("profileurl", ""));
                        calendar = Calendar.getInstance();
                        mm.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        chat.push().updateChildren(mm);
                        mm.clear();
                        img = 0;
                        button1.setEnabled(true);
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setText("");
                    }
                    else {
                        mm = new HashMap<>();
                        mm.put("username", db_name);
//                      mm.put("message", edittext1.getText().toString().trim());
                        mm.put("video", _downloadUrl);
                        mm.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mm.put("profileurl", file.getString("profileurl", ""));
                        calendar = Calendar.getInstance();
                        mm.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        chat.push().updateChildren(mm);
                        mm.clear();
                        img = 0;
                        button1.setEnabled(true);
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setText("");
                    }
                }else{
                    if (img == 1) {
                        mm = new HashMap<>();
                        mm.put("username", db_name);
                        mm.put("message", edittext1.getText().toString().trim());
                        mm.put("image", _downloadUrl);
                        mm.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mm.put("profileurl", file.getString("profileurl", ""));
                        calendar = Calendar.getInstance();
                        mm.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        chat.push().updateChildren(mm);
                        mm.clear();
                        img = 0;
                        button1.setEnabled(true);
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setText("");
                    }
                    else {
                        mm = new HashMap<>();
                        mm.put("username", db_name);
                        mm.put("message", edittext1.getText().toString().trim());
                        mm.put("video", _downloadUrl);
                        mm.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mm.put("profileurl", file.getString("profileurl", ""));
                        calendar = Calendar.getInstance();
                        mm.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        chat.push().updateChildren(mm);
                        mm.clear();
                        img = 0;
                        button1.setEnabled(true);
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setText("");
                    }
                }
            }
        };

        _store_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _store_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _store_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

            }
        };

        _Currdata_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (!file.getString("emailid", "").equals("")) {
                    if (_childValue.get("emailid").toString().equals(get_name)) {
                        db_name = _childValue.get("name").toString();

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

          

        _chat_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                chat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        lop = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                lop.add(_map);
                                //notification();
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                        list.add(_childKey);
                        listview2.setAdapter(new Listview2Adapter(lop));
                        ((BaseAdapter)listview2.getAdapter()).notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
                button1.setEnabled(true);
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                chat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        lop = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                lop.add(_map);
                                //notification();
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                        listview2.setAdapter(new Listview2Adapter(lop));
                        ((BaseAdapter)listview2.getAdapter()).notifyDataSetChanged();
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
                chat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        lop = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                lop.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                        listview2.setAdapter(new Listview2Adapter(lop));
                        ((BaseAdapter)listview2.getAdapter()).notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        chat.addChildEventListener(_chat_child_listener);

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
//    private void notification() {
//        String message = "New message arrived";
//        final Notification.Builder builder = new Notification.Builder(ChatActivity.this).setSmallIcon(R.drawable.app_icon).setContentTitle("Society").setContentText(message).setAutoCancel(true);
//        Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
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
//    }
    private void initializeLogic() {
        listview2.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listview2.setStackFromBottom(true);
        edittext1.setMaxLines(5);
        Currdata.addChildEventListener(_Currdata_child_listener);
        get_name = file.getString("emailid", "");
        img = 0;
        imageview1.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        textview1.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        button1.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_CAMERA:
                if (_resultCode == Activity.RESULT_OK) {
                    String _filePath = _file_camera.getAbsolutePath();
                    attachex.setVisibility(View.GONE);
                    edittext1.setVisibility(View.VISIBLE);
                    edittext1.setEnabled(true);
                    imgPath = "";
                    imgurl = "";
                    imgsize = 0;
                    videosize = 0;
                    img = 1;
                    imgPath = _filePath;
                    imgurl = Uri.parse(_filePath).getLastPathSegment();
                    imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath, 1024, 1024));
                    imageview1.setVisibility(View.VISIBLE);
                    imgsize= new java.io.File(imgPath).length()/1024;
                    if ((imgsize < 2000) && (imgsize > 0)) {
                        textview1.setVisibility(View.VISIBLE);
                        textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                        edittext1.setVisibility(View.VISIBLE);
                        edittext1.setEnabled(true);
                    }
                    else {
                        TastyToast.makeText(getApplicationContext(), "Captured image must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        imageview1.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        imgPath = "";
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

                    edittext1.setVisibility(View.VISIBLE);
                    edittext1.setEnabled(true);
                    if (Uri.parse(_filePath.get((int)(0))).getLastPathSegment().endsWith(".mp4"))
                    {
                        attachex.setVisibility(View.GONE);
                        imgPath = "";
                        imgurl = "";
                        imageview1.setVisibility(View.GONE);
                        img = 2;
                        edittext1.setVisibility(View.VISIBLE);
                        edittext1.setEnabled(true);
                        imgPath = _filePath.get((int)(0));
                        imgurl = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                        videosize=new java.io.File(imgPath).length()/1024;
                        if ((videosize < 5000) && (videosize > 0)) {
                            videoView.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(Uri.parse(imgPath));
                            videoView.requestFocus();
                            videoView.start();
                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    videoView.start();
                                }
                            });

                            edittext1.setVisibility(View.VISIBLE);
                            edittext1.setEnabled(true);
                            textview1.setVisibility(View.VISIBLE);
                            textview1.setText("File Size: ".concat(String.valueOf(videosize).concat("Kb")));
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment must be less than 5Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            attachex.setVisibility(View.VISIBLE);
                            textview1.setText("");
                            attachex.setVisibility(View.VISIBLE);
                            textview1.setVisibility(View.GONE);
                            edittext1.setVisibility(View.VISIBLE);
                            edittext1.setEnabled(true);
                            imageview1.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            imgPath = "";
                            imgurl = "";
                            img = 0;
                        }
                    }
                    else
                    {
                        imgurl = "";
                        imgPath = "";
                        imgsize = 0;
                        videosize =0;
                        attachex.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        img = 1;
                        imgPath = _filePath.get((int)(0));
                        imgurl = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                        imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                        imageview1.setVisibility(View.VISIBLE);
                        imgsize= new java.io.File(imgPath).length()/1024;
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            textview1.setVisibility(View.VISIBLE);
                            edittext1.setVisibility(View.VISIBLE);
                            edittext1.setEnabled(true);
                            textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            imageview1.setVisibility(View.GONE);
                            textview1.setVisibility(View.GONE);
                            attachex.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            edittext1.setVisibility(View.VISIBLE);
                            edittext1.setEnabled(true);
                            textview1.setText("");
                            imgPath = "";
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

    private class DownloadTask extends AsyncTask<String, Integer, String> {


        @Override

        protected void onPreExecute() {

        }
        protected String doInBackground(String... address) {
            try {
                filename= URLUtil.guessFileName(address[0], null, null);
                int resCode = -1;
                java.io.InputStream in = null;
                java.net.URL url = new java.net.URL(address[0]);
                java.net.URLConnection urlConn = url.openConnection();
                if (!(urlConn instanceof java.net.HttpURLConnection)) {
                    throw new java.io.IOException("URL is not an Http URL"); }
                java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) urlConn; httpConn.setAllowUserInteraction(false); httpConn.setInstanceFollowRedirects(true); httpConn.setRequestMethod("GET"); httpConn.connect();
                resCode = httpConn.getResponseCode();
                if (resCode == java.net.HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    size = httpConn.getContentLength();

                } else { result = "There was an error"; }

                path = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS).concat("/".concat(filename));
                FileUtil.writeFile(path, "");
                java.io.File file = new java.io.File(path);

                java.io.OutputStream output = new java.io.FileOutputStream(file);
                try {
                    int bytesRead;
                    sumCount = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = in.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                        sumCount += bytesRead;
                        if (size > 0) {
                            publishProgress((int)Math.round(sumCount*100 / size));
                        }
                    }
                } finally {
                    output.close();
                }
                result = filename + " saved";
                in.close();
            } catch (java.net.MalformedURLException e) {
                result = e.getMessage();
            } catch (java.io.IOException e) {
                result = e.getMessage();
            } catch (Exception e) {
                result = e.toString();
            }
            return result;

        }
        protected void onPostExecute(String s){
            TastyToast.makeText(getApplicationContext(), s, TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
        }
    }


    public class Listview2Adapter extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview2Adapter(ArrayList<HashMap<String, Object>> _arr) {
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

            if (lop.get((int)_position).containsKey("message") || lop.get((int)_position).containsKey("image") || lop.get((int)_position).containsKey("video")) {
                username.setText(lop.get((int)_position).get("username").toString());
                time.setText(lop.get((int)_position).get("time").toString());

                if (lop.get((int)_position).get("profileurl").toString().trim().equals("")) {
                    noprofile.setBackgroundResource(R.drawable.default_profile);
                    profile.setBackgroundResource(R.drawable.default_profile);
                }else{
                    Glide.with(getApplicationContext()).load(Uri.parse(lop.get((int)_position).get("profileurl").toString())).into(noprofile);
                    Glide.with(getApplicationContext()).load(Uri.parse(lop.get((int)_position).get("profileurl").toString())).into(profile);
                }
            }
            if (lop.get((int)_position).containsKey("message")) {
                message.setText(lop.get((int)_position).get("message").toString());
                message.setVisibility(View.VISIBLE);
            }
            else {
                message.setVisibility(View.GONE);
            }
            if (lop.get((int)_position).containsKey("image")) {
                imageview1.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(Uri.parse(lop.get((int)_position).get("image").toString())).into(imageview1);
            }
            else {
                imageview1.setVisibility(View.GONE);
            }
            if ((lop.get((int)_position).containsKey("video"))) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(lop.get((int)_position).get("video").toString()));
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
            if (lop.get((int)_position).get("user_uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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