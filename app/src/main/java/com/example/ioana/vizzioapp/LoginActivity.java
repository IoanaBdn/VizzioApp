package com.example.ioana.vizzioapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity
{


    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NewAccountLink, ForgetPasswordLink, PhoneLoginLink;

    private DatabaseReference UsersRef;
    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        InitializeFields();

        NewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();
            }
        });

        PhoneLoginLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent phoneLoginIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowUserToLogin();
            }
        });
    }


    private void AllowUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT);
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT);
        }
        else
        {
            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String currentUSerId = mAuth.getCurrentUser().getUid();
                                //String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                

                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successful...", Toast.LENGTH_SHORT);
                                loadingBar.dismiss();

                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error : "+ message , Toast.LENGTH_SHORT);
                                loadingBar.dismiss();

                            }

                        }
                    }
            );
        }

    }

    private void InitializeFields()
    {
        LoginButton = (Button) findViewById(R.id.login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NewAccountLink = (TextView) findViewById(R.id.new_account_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        PhoneLoginLink = (TextView) findViewById(R.id.phone_login_link);
        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.phone_login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Login");
    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
