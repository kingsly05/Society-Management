package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.HashMap;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Continuation;
import java.io.File;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ClipData;
import android.provider.MediaStore;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.view.View;
import com.bumptech.glide.Glide;
import com.sdsmdg.tastytoast.TastyToast;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class AdminMemDetEditActivity extends AppCompatActivity {

    public final int REQ_CD_FILEPICKER = 101;
    public final int REQ_CD_CAMERA = 102;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();

    private Toolbar _toolbar;
    private HashMap<String, Object> map = new HashMap<>();
    private String imgname = "";
    private String imgpath = "";
    private double img = 0;
    private String editmemberid="";

    private ImageView profileimage;
    private LinearLayout linear12;
    private ImageView delprofile;
    private ImageView addimg;
    private EditText name;
    private EditText emailid;
    private EditText flattype;
    private EditText memberid;
    private EditText phonenumber;
    private EditText maintenance;
    private EditText miscellaneous;
    private EditText status;
    private Button button1;
    private ProgressDialog progressDialog;

    private Intent intent = new Intent();
    private SharedPreferences file;
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private StorageReference profile = _firebase_storage.getReference("profile");
    private OnCompleteListener<Uri> _profile_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _profile_download_success_listener;
    private OnSuccessListener _profile_delete_success_listener;
    private OnProgressListener _profile_upload_progress_listener;
    private OnProgressListener _profile_download_progress_listener;
    private OnFailureListener _profile_failure_listener;
    private AlertDialog.Builder dialog;
    private Intent filepicker = new Intent(Intent.ACTION_GET_CONTENT);
    private Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_camera;
    private Calendar calendar = Calendar.getInstance();
    private  Window window;
    private double imgsize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mem_det_edit);
        com.google.firebase.FirebaseApp.initializeApp(this);
        window = this.getWindow();
        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
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
        profileimage = (ImageView) findViewById(R.id.profileimage);
        linear12 = (LinearLayout) findViewById(R.id.linear12);
        delprofile = (ImageView) findViewById(R.id.delprofile);
        addimg = (ImageView) findViewById(R.id.addimg);
        name = (EditText) findViewById(R.id.name);
        emailid = (EditText) findViewById(R.id.emailid);
        flattype = (EditText) findViewById(R.id.flattype);
        memberid = (EditText) findViewById(R.id.memberid);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        maintenance = (EditText) findViewById(R.id.maintenance);
        miscellaneous = (EditText) findViewById(R.id.miscellaneous);
        status = (EditText) findViewById(R.id.status);
        button1 = (Button) findViewById(R.id.button1);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        dialog = new AlertDialog.Builder(this);
        filepicker.setType("image/*");
        filepicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        progressDialog = new ProgressDialog(AdminMemDetEditActivity.this);
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

        delprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (file.getString("editprofileurl", "").equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Can't able to delete profile", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
                else {
                    imgname = "";
                    imgpath = "";
                    _firebase_storage.getReferenceFromUrl(file.getString("editprofileurl","")).delete().addOnSuccessListener(_profile_delete_success_listener).addOnFailureListener(_profile_failure_listener);

                    map = new HashMap<>();
                    map.put("profilename", "");
                    map.put("profileurl", "");
                    calendar = Calendar.getInstance();
                    map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                    Currdata.child(editmemberid).updateChildren(map);
                    delprofile.setVisibility(View.GONE);
                    addimg.setVisibility(View.VISIBLE);
                    profileimage.setImageResource(R.drawable.default_profile);
                }
            }
        });

        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                dialog.setTitle("Profile Picture");
                dialog.setMessage("Choose picture from");
                dialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        startActivityForResult(camera, REQ_CD_CAMERA);
                    }
                });
                dialog.setNeutralButton("File", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        startActivityForResult(filepicker, REQ_CD_FILEPICKER);
                    }
                });
                dialog.show();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (miscellaneous.getText().toString().equals("") && (status.getText().toString().equals("") && (name.getText().toString().equals("") && (emailid.getText().toString().equals("") && (flattype.getText().toString().equals("") && (memberid.getText().toString().equals("") && (phonenumber.getText().toString().equals("") && maintenance.getText().toString().equals("")))))))) {
                    TastyToast.makeText(getApplicationContext(), "Please Enter all credentials", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
                else {
                    if (img == 0) {
                        map = new HashMap<>();
                        map.put("name", name.getText().toString());
                        map.put("emailid", emailid.getText().toString());
                        map.put("memberid", memberid.getText().toString());
                        map.put("flattype", flattype.getText().toString());
                        map.put("phonenumber", phonenumber.getText().toString());
                        map.put("maintenance", maintenance.getText().toString());
                        map.put("miscellaneous", miscellaneous.getText().toString());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        map.put("status", status.getText().toString());
                        Currdata.child(memberid.getText().toString()).updateChildren(map);
                        TastyToast.makeText(getApplicationContext(), "Updated Successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        finish();
                    }
                    else {
                        profile.child(imgname).putFile(Uri.fromFile(new File(imgpath))).addOnFailureListener(_profile_failure_listener).addOnProgressListener(_profile_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return profile.child(imgname).getDownloadUrl();
                            }}).addOnCompleteListener(_profile_upload_success_listener);
                        button1.setEnabled(false);
                        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_loading_box);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
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
                if (!(file.getString("editmemberid", "").equals(""))) {
                    if (_childValue.get("memberid").toString().equals(editmemberid)) {
                        name.setText(_childValue.get("name").toString());
                        emailid.setText(_childValue.get("emailid").toString());
                        flattype.setText(_childValue.get("flattype").toString());
                        memberid.setText(_childValue.get("memberid").toString());
                        phonenumber.setText(_childValue.get("phonenumber").toString());
                        maintenance.setText(_childValue.get("maintenance").toString());
                        miscellaneous.setText(_childValue.get("miscellaneous").toString());
                        status.setText(_childValue.get("status").toString());
                        if (_childValue.get("profileurl").toString().equals("")) {
                            delprofile.setVisibility(View.GONE);
                            profileimage.setImageResource(R.drawable.default_profile);
                            addimg.setVisibility(View.VISIBLE);
                        }
                        else {
                            Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(profileimage);
                            delprofile.setVisibility(View.VISIBLE);
                            addimg.setVisibility(View.GONE);
                            file.edit().putString("editprofileurl", _childValue.get("profileurl").toString()).commit();
                        }
                    }
                }
                else {
                    TastyToast.makeText(getApplicationContext(), "No data found", TastyToast.LENGTH_LONG, TastyToast.WARNING);
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
                if (!(img == 0)) {
                    map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    map.put("emailid", emailid.getText().toString());
                    map.put("memberid", memberid.getText().toString());
                    map.put("flattype", flattype.getText().toString());
                    map.put("phonenumber", phonenumber.getText().toString());
                    map.put("maintenance", maintenance.getText().toString());
                    map.put("status", status.getText().toString());
                    map.put("miscellaneous", miscellaneous.getText().toString());
                    map.put("maintenancep", file.getString("editmaintenance", ""));
                    map.put("miscellaneousp", file.getString("editmiscellaneous", ""));
                    map.put("profilename", imgname);
                    map.put("profileurl", _downloadUrl);
                    calendar = Calendar.getInstance();
                    map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                    Currdata.child(memberid.getText().toString()).updateChildren(map);
                    button1.setEnabled(true);
                    TastyToast.makeText(getApplicationContext(), "Updated Successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    map.clear();
                    progressDialog.dismiss();
                    finish();
                }
                else {
                      TastyToast.makeText(getApplicationContext(), "Unknown Error Occured", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    progressDialog.dismiss();
                }
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
    }
    private void initializeLogic() {
        img = 0;
        addimg.setVisibility(View.GONE);
        delprofile.setVisibility(View.GONE);
        memberid.setEnabled(false);
        editmemberid = file.getString("editmemberid","");
    }
    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_CAMERA:
                if (_resultCode == Activity.RESULT_OK) {
                    String _filePath = _file_camera.getAbsolutePath();
                    imgpath = "";
                    imgname = "";
                    imgsize = 0;
                    img = 1;
                    imgpath = _filePath;

                    imgsize= new java.io.File(imgpath).length()/1024;
                    if ((imgsize < 2000) && (imgsize > 0)) {
                        imgname = Uri.parse(_filePath).getLastPathSegment();
                        profileimage.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath, 1024, 1024));
                        profileimage.setVisibility(View.VISIBLE);
                        delprofile.setVisibility(View.GONE);
                        addimg.setVisibility(View.GONE);
                    }
                    else {
                        TastyToast.makeText(getApplicationContext(), "Captured image must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        profileimage.setImageResource(R.drawable.default_profile);
                        delprofile.setVisibility(View.GONE);
                        addimg.setVisibility(View.VISIBLE);
                        imgpath = "";
                        imgname = "";
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

                        imgname = "";
                        imgpath = "";
                        imgsize = 0;
                        img = 1;
                        imgpath = _filePath.get((int)(0));

                        imgsize= new java.io.File(imgpath).length()/1024;
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            imgname = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                            profileimage.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                            profileimage.setVisibility(View.VISIBLE);
                            delprofile.setVisibility(View.GONE);
                            addimg.setVisibility(View.GONE);
                        }
                        else {
                            TastyToast.makeText(getApplicationContext(), "Attachment must be less than 2Mb", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            profileimage.setImageResource(R.drawable.default_profile);
                            delprofile.setVisibility(View.GONE);
                            addimg.setVisibility(View.VISIBLE);
                            imgpath = "";
                            imgname = "";
                            img = 0;
                        }


                }

                else {
                }
                break;
            default:
                break;
        }
    }

}