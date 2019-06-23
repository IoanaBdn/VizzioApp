package com.example.ioana.vizzioapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText UserEmail;
    private Toolbar mToolbar;
    private Button ResetPasswordButton;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        InitializeFields();



        ResetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ResetPassword();
            }
        });


    }



    private void ResetPassword()
    {
        if(!TextUtils.isEmpty(UserEmail.getText().toString()))
        {
            mAuth.sendPasswordResetEmail(UserEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ForgetPasswordActivity.this, "Password send to your email ",
                                                Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
            else
        {
            Toast.makeText(ForgetPasswordActivity.this, "Insert your email address",
                            Toast.LENGTH_SHORT).show();
        }
    }



    private void InitializeFields() {
        UserEmail = (EditText) findViewById(R.id.email_address_input);
        ResetPasswordButton = (Button) findViewById(R.id.reset_password_button);

        mToolbar = (Toolbar) findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Forget Password");

    }


}