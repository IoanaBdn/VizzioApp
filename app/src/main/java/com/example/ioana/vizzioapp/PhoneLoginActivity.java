package com.example.ioana.vizzioapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
{

    private Button SendVerificationButton, VerifyButton;
    private EditText InputVerificationCode;
    private  EditText InputPhoneNumber;

    private TextView PhoneNumberReq, VerificationCodeReq;
    private TextView PhoneLoginInfo, CodeVerificationInfo;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private CountryCodePicker CountryPicker;
    private LinearLayout LayoutPhoneNumber;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        SendVerificationButton = (Button) findViewById(R.id.send_verification_code_button);
        VerifyButton = (Button) findViewById(R.id.verify_buton);

        InputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);

        InputVerificationCode = (EditText) findViewById(R.id.verification_code_input);
        PhoneNumberReq = (TextView) findViewById(R.id.phone_number_requirement);
        LayoutPhoneNumber = (LinearLayout) findViewById(R.id.layout_phone_number);

        VerificationCodeReq = (TextView) findViewById(R.id.verification_code_requirement);
        PhoneLoginInfo = (TextView) findViewById(R.id.phone_login_info);
        CodeVerificationInfo = (TextView) findViewById(R.id.code_verification_info);


        mToolbar = (Toolbar) findViewById(R.id.phone_login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Phone Authentication");

        //

        CountryPicker = findViewById(R.id.country_picker);
        CountryPicker.setAutoDetectedCountry(true);

        //InputPhoneNumber.setText("+"+CountryCode);


/*
        CountryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener()
        {
            @Override
            public void onCountrySelected()
            {

                String CountryCode = CountryPicker.getSelectedCountryCode();

                InputPhoneNumber.setText("+"+CountryCode);
            }
        });
        */
        //


        InputPhoneNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if( InputPhoneNumber.getText().length() == 0)
                {
                    String CountryCode = CountryPicker.getSelectedCountryCode();


                    InputPhoneNumber.setText("+"+CountryCode);

                    Selection.setSelection(InputPhoneNumber.getText(), InputPhoneNumber.getText().length());

                    InputPhoneNumber.setSelection(InputPhoneNumber.getText().length());
                }

                return false;
            }
        });


        InputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {


            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String CountryCode  = "+"+ CountryPicker.getSelectedCountryCode();
                if( !s.toString().startsWith(CountryCode) )
                {
                    InputPhoneNumber.setText(CountryCode);
                    Selection.setSelection(InputPhoneNumber.getText(), InputPhoneNumber.getText().length());

                }
            }
        });



        loadingBar = new ProgressDialog(this);





        SendVerificationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {



                String phoneNumber = InputPhoneNumber.getText().toString();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneNumberReq.setVisibility(View.INVISIBLE);

                    // code verification text and wrong number

                    String verificationInfoText = "Waiting to automatically detect an SMS sent to <b>"+ phoneNumber +"</b>. If the SMS is not detected automatically, please enter the 6 digit code you were received by SMS.";

                    //CodeVerificationInfo.setText( Html.fromHtml("<p>Waiting to automatically detect an SMS sent to <b>"+ phoneNumber +"</b>. If the SMS is not detected automatically, please enter the 6 digit code you were received by SMS.</p>") );
                    SpannableStringBuilder spanText = new SpannableStringBuilder( Html.fromHtml(verificationInfoText) );
                    spanText.append("Wrong number?");
                    spanText.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick( View widget)
                        {
                            //Toast.makeText(getApplicationContext(), "Wrong number Clicked", Toast.LENGTH_SHORT).show();

                            CodeVerificationInfo.setVisibility(View.INVISIBLE);
                            VerifyButton.setVisibility(View.INVISIBLE);
                            InputVerificationCode.setVisibility(View.INVISIBLE);
                            VerificationCodeReq.setVisibility(View.INVISIBLE);


                            SendVerificationButton.setVisibility(View.VISIBLE);
                            LayoutPhoneNumber.setVisibility(View.VISIBLE);
                            PhoneLoginInfo.setVisibility(View.VISIBLE);
                            PhoneNumberReq.setVisibility(View.VISIBLE);
                        }
                    },spanText.length()- "Wrong number?".length(), spanText.length(), 0);

                    CodeVerificationInfo.setMovementMethod(LinkMovementMethod.getInstance());
                    CodeVerificationInfo.setText( spanText, TextView.BufferType.SPANNABLE);

                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait while we are authenticating your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            120,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }

            }
        });


        VerifyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                SendVerificationButton.setVisibility(View.INVISIBLE);
                //InputPhoneNumber.setVisibility(View.INVISIBLE);
                LayoutPhoneNumber.setVisibility(View.INVISIBLE);
                PhoneLoginInfo.setVisibility(View.INVISIBLE);

                String  verificationCode  = InputVerificationCode.getText().toString();

                if(TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this,"Please write verification code first",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Verification Code ");
                    loadingBar.setMessage("Please wait while we are verifying code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    //Create a PhoneAuthCredential object
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);


                }

            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid phone number, please enter a correct phone number and select your country.", Toast.LENGTH_SHORT).show();

                SendVerificationButton.setVisibility(View.VISIBLE);
               //InputPhoneNumber.setVisibility(View.VISIBLE);
                LayoutPhoneNumber.setVisibility(View.VISIBLE);
                PhoneLoginInfo.setVisibility(View.VISIBLE);
                PhoneNumberReq.setVisibility(View.VISIBLE);


                VerifyButton.setVisibility(View.INVISIBLE);
                InputVerificationCode.setVisibility(View.INVISIBLE);
                VerificationCodeReq.setVisibility(View.INVISIBLE);

            }



            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token)
            {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();

                Toast.makeText(PhoneLoginActivity.this,"Code has been sent, check and verify. ",Toast.LENGTH_SHORT).show();

                SendVerificationButton.setVisibility(View.INVISIBLE);
                //InputPhoneNumber.setVisibility(View.INVISIBLE);
                LayoutPhoneNumber.setVisibility(View.INVISIBLE);
                PhoneLoginInfo.setVisibility(View.INVISIBLE);


                VerifyButton.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);
                VerificationCodeReq.setVisibility(View.VISIBLE);
                CodeVerificationInfo.setVisibility(View.VISIBLE);

            }


        };
    }

    //sing in the user
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in successfully", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: "+message  , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainIntent);
        finish();
    }
}
