package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class BillActivity extends AppCompatActivity {

    private LinearLayout linear10;
    private LinearLayout linear1;
    private TextView memberid;
    private TextView name;
    private TextView maintenance;
    private TextView miscellaneous;
    private TextView status;
    private TextView maintenancep;
    private TextView miscellaneousp;
    private TextView textview11;
    private Button save;
    private Button payment;
    private Button back;
    private EditText edittext1;
    private Button button1;
    private ImageView imageview1;

    private HashMap<String, Object> map = new HashMap<>();
    private String command = "";
    private boolean Connected = false;
    private String pos = "";
    private String string = "";

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference Currdata = _firebase.getReference("Currdata");
    private ChildEventListener _Currdata_child_listener;
    private Intent intent = new Intent();
    private SharedPreferences file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        else {
            initializeLogic();
        }
    }

    private void initializeLogic() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initialize() {
        memberid = (TextView) findViewById(R.id.memberid);
        name = (TextView) findViewById(R.id.name);
        maintenance = (TextView) findViewById(R.id.maintenance);
        miscellaneous = (TextView) findViewById(R.id.miscellaneous);
        status = (TextView) findViewById(R.id.status);
        maintenancep = (TextView) findViewById(R.id.maintenancep);
        linear10 = (LinearLayout) findViewById(R.id.linear10);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        miscellaneousp = (TextView) findViewById(R.id.miscellaneousp);
        edittext1 = (EditText) findViewById(R.id.edittext1);
        save = (Button) findViewById(R.id.save);
        payment = (Button) findViewById(R.id.payment);
        back = (Button) findViewById(R.id.back);
        imageview1 = findViewById(R.id.imageview1);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                linear10.setBackgroundColor(getResources().getColor(R.color.white));
                try{
                    android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
                    android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(linear10.getWidth(), linear10.getHeight(), 1).create();
                    android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas(); Paint paint = new Paint();
                    canvas.drawPaint(paint); linear1.draw(canvas); document.finishPage(page);
                    string = FileUtil.getExternalStorageDir().concat("/").concat(edittext1.getText().toString().trim().concat(".pdf"));
                    FileUtil.writeFile(string, "");
                    java.io.File myFile = new java.io.File(string);
                    java.io.FileOutputStream fOut = new java.io.FileOutputStream(myFile);
                    java.io.OutputStreamWriter myOutWriter = new java.io.OutputStreamWriter(fOut);
                    document.writeTo(fOut); document.close(); myOutWriter.close();
                    fOut.close();
                    TastyToast.makeText(getApplicationContext(), edittext1.getText().toString()+".pdf File Saved", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                } catch (Exception e) {
                    TastyToast.makeText(getApplicationContext(), e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
                linear10.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setClass(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                finish();
            }
        });


        _Currdata_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                    memberid.setText(_childValue.get("memberid").toString());
                    name.setText(_childValue.get("name").toString());
                    maintenance.setText(_childValue.get("maintenance").toString());
                    miscellaneous.setText(_childValue.get("miscellaneous").toString());
                    status.setText(_childValue.get("status").toString());
                    maintenancep.setText(_childValue.get("maintenancep").toString());
                    miscellaneousp.setText(_childValue.get("miscellaneousp").toString());
                    edittext1.setText(_childValue.get("memberid").toString().trim().concat(_childValue.get("name").toString().trim()));
                    if(_childValue.get("memberid").toString().equals(""))
                    {
                        imageview1.setBackgroundResource(R.drawable.default_profile);
                    }else{
                        Glide.with(getApplicationContext()).load(Uri.parse(_childValue.get("profileurl").toString())).into(imageview1);
                    }
                }
                else {
                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {
            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
            }
        };
        Currdata.addChildEventListener(_Currdata_child_listener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            initializeLogic();
        }
    }


}