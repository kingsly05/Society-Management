package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sdsmdg.tastytoast.TastyToast;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login;
    private TextView forget,signup;
    private LottieAnimationView lottieAnimationView;
    private AlertDialog.Builder dialog;
    String TAG="LoginActivity";
    private Intent intent = new Intent();
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private SharedPreferences file;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        initializeLogic();
    }
    private void initialize()
    {
        lottieAnimationView =  findViewById(R.id.UI_login_lottie);
        email = findViewById(R.id.UI_email_et);
        password =  findViewById(R.id.UI_password_et);
        forget =  findViewById(R.id.UI_forget_txt);
        login =  findViewById(R.id.UI_login_btn);
        signup =  findViewById(R.id.UI_signup_txt);
        Currauth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(LoginActivity.this);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.putExtra("email", email.getText().toString());
                intent.setClass(getApplicationContext(), ForgetPassActivity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(forget,"forgetTrans");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                startActivity(intent,options.toBundle());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (email.getText().toString().equals("") && password.getText().toString().equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Please enter valid credentials", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
                else {
                    progressDialog.show();
        progressDialog.setContentView(R.layout.custom_loading_box);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
                    Currauth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(LoginActivity.this, _Currauth_sign_in_listener);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setClass(getApplicationContext(), SignUpActivity.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(signup,"signupTrans");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                startActivity(intent,options.toBundle());
            }
        });

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
                final String errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
                if (_success) {
                    file.edit().putString("emailid", email.getText().toString()).commit();
                    file.edit().putString("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid()).commit();
                    intent.setClass(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    TastyToast.makeText(getApplicationContext(), "Logged in as, "+email.getText().toString(), TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    finish();
                }
                else {
                    progressDialog.dismiss();
                    TastyToast.makeText(getApplicationContext(), errorMessage, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
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
        if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
            intent.setClass(getApplicationContext(), DashboardActivity.class);
            TastyToast.makeText(getApplicationContext(), "Welcome back, "+file.getString("db_name",""), TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
            startActivity(intent);
            finish();
        }
        else {

        }
    }


}