package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;

import java.util.ArrayList;
import java.util.HashMap;

public class NoticeActivity extends AppCompatActivity {

    private ListView listview1;
    private AlertDialog.Builder dialog;

    private String path = "";
    private String filename = "";
    private String myurl = "";
    private String result = "";
    private double size = 0;
    private double sumCount = 0;
    private ArrayList<HashMap<String, Object>> lop = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference chat = _firebase.getReference("chat");
    private ChildEventListener _chat_child_listener;
    private OnSuccessListener _store_delete_success_listener;
    private OnProgressListener _store_upload_progress_listener;
    private OnProgressListener _store_download_progress_listener;
    private OnFailureListener _store_failure_listener;
    private FirebaseAuth Currauth;
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private DatabaseReference notice = _firebase.getReference("notice");
    private ChildEventListener _notice_child_listener;
    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initialize();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        else {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initialize() {
        listview1 = (ListView) findViewById(R.id.listview1);
        dialog = new AlertDialog.Builder(this);
        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);





        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            Toast.makeText(NoticeActivity.this, "Downloading......", Toast.LENGTH_SHORT).show();
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
		
		listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                            Toast.makeText(NoticeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
							if (lop.get((int)_position).containsKey("image")) {
								_firebase_storage.getReferenceFromUrl(lop.get((int)_position).get("image").toString()).delete().addOnSuccessListener(_store_delete_success_listener).addOnFailureListener(_store_failure_listener);
                                Toast.makeText(NoticeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
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
				listview1.setAdapter(new Listview1Adapter(lop));
				((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				return true;
			}
		});



        _notice_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                notice.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        list.add(_childKey);
                        listview1.setAdapter(new Listview1Adapter(lop));
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
                notice.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        listview1.setAdapter(new Listview1Adapter(lop));
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
                notice.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        listview1.setAdapter(new Listview1Adapter(lop));
                        ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
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
        notice.addChildEventListener(_notice_child_listener);
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
        //    showMessage(s);
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

//            if (lop.get((int)_position).containsKey("message")) {
//                message.setVisibility(View.VISIBLE);
//            }
//            else {
//                message.setVisibility(View.GONE);
//            }
//            profile.setVisibility(View.GONE);
//            if (lop.get((int)_position).containsKey("image")) {
//                imageview1.setVisibility(View.VISIBLE);
//                Glide.with(getApplicationContext()).load(Uri.parse(lop.get((int)_position).get("image").toString())).into(imageview1);
//            }
//            else {
//                imageview1.setVisibility(View.GONE);
//            }
//            linear1.setGravity(Gravity.LEFT);
//            profile.setVisibility(View.GONE);
//            username.setGravity(Gravity.LEFT);
//            noprofile.setVisibility(View.VISIBLE);
//            linear2.setBackgroundResource(R.drawable.chat_receiver);
//            username.setText(lop.get((int)_position).get("username").toString());
//            time.setText(lop.get((int)_position).get("time").toString());
//            profile.setImageResource(R.drawable.app_icon);

            if (lop.get((int)_position).containsKey("message") || lop.get((int)_position).containsKey("image") || lop.get((int)_position).containsKey("video")) {
                username.setText(lop.get((int)_position).get("username").toString());
                time.setText(lop.get((int)_position).get("time").toString());

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
                linear1.setGravity(Gravity.LEFT);
                profile.setVisibility(View.GONE);
                username.setGravity(Gravity.LEFT);
                noprofile.setVisibility(View.VISIBLE);
                noprofile.setImageResource(R.drawable.app_icon);
                linear2.setBackgroundResource(R.drawable.chat_receiver);

            return _v;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {

        }
    }
}