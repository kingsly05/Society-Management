package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ImageView imageview1;
    private FloatingActionButton fab;

    String TAG="MainActivity";
    private String Title = "";
    private String Message = "";
    private Intent intent = new Intent();
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initialize();
        initializeLogic();
    }


    private void initialize() {

        fab = findViewById(R.id._fab);

        imageview1 = (ImageView) findViewById(R.id.imageview1);
        dialog = new AlertDialog.Builder(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setClass(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initializeLogic() {
//        intent.setClass(getApplicationContext(), SplashActivity.class);
//        startActivity(intent);
//        finish();
    }


    @Override
    public void onBackPressed() {
        dialog.setTitle("Exit");
        dialog.setMessage("Do you want to exit?");
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
}