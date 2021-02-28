package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sdsmdg.tastytoast.TastyToast;

public class ForgetPassActivity extends AppCompatActivity {

    String TAG = "ForgetPassActivity";
    private EditText useremail;
    private Button forgetbtn;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private LinearLayout nointernet,linear;
    private Button retry;

    private RequestNetwork internet;
    private RequestNetwork.RequestListener _internet_request_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        initialize();
        inttializeLogic();
    }

    private void inttializeLogic() {
        useremail.setText(getIntent().getStringExtra("email"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void initialize() {

        useremail = findViewById(R.id.UI_email_et);
        forgetbtn = findViewById(R.id.UI_forget_btn);
        Currauth = FirebaseAuth.getInstance();


        internet = new RequestNetwork(this);
        nointernet = findViewById(R.id.nointernet);
        linear = findViewById(R.id.linear);
        retry = findViewById(R.id.retry);



        forgetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if(!(useremail.getText().toString().equals(""))) {
                    Currauth.sendPasswordResetEmail(useremail.getText().toString()).addOnCompleteListener(_Currauth_reset_password_listener);
                    TastyToast.makeText(getApplicationContext(), "Email Sent Successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    supportFinishAfterTransition();
                }
                else
                    TastyToast.makeText(getApplicationContext(), "Enter Valid Email ID", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });

        _Currauth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {

            }
        };

        _Currauth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {

            }
        };

        _Currauth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> _param1) {

            }
        };


    }
}