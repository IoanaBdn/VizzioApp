package com.example.ioana.vizzioapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private Button UpdateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;

     private ProgressDialog loadingBar;

     private android.support.v7.widget.Toolbar SettingsToolbar;

    Spinner profileTypeSpinner, inputMethodSpinner;
    List<String> profileTypeList  = new ArrayList<>();
    List<String> inputMethodList  = new ArrayList<>();
    ArrayAdapter<String> profileTypeSpinnerAdapter;
    ArrayAdapter<String> inputMethodSpinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        //userName.setVisibility(View.INVISIBLE);

        //Define spinners
        //select profile type
        profileTypeSpinner=(Spinner) findViewById(R.id.spinner_ProfileType);
        profileTypeSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1);
        profileTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileTypeSpinner.setAdapter(profileTypeSpinnerAdapter);
        profileTypeList.add("Normal Vision");
        profileTypeList.add("Blind");
        profileTypeList.add("Visual Impaired");



        profileTypeSpinnerAdapter.addAll(profileTypeList);
        profileTypeSpinnerAdapter.notifyDataSetChanged();
        /////////////////////////////////////////
        //select input method
        inputMethodSpinner=(Spinner) findViewById(R.id.spinner_InputMethod);
        inputMethodSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1);
        inputMethodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputMethodSpinner.setAdapter(inputMethodSpinnerAdapter);
        inputMethodList.add("Normal Keyboard");
        inputMethodList.add("Braille Keyboard");
        inputMethodList.add("Voice Input Keyboard");



        inputMethodSpinnerAdapter.addAll(inputMethodList);
        inputMethodSpinnerAdapter.notifyDataSetChanged();
        /////////////////////////////////////////

        profileTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int selectedPosition= position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UpdateSettings();
            }
        });


        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                //porneste activitatea care va da un rezultat
                startActivityForResult(galleryIntent, GalleryPick );
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            Toast.makeText(this,"Ai111111111",Toast.LENGTH_SHORT).show();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");


                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String downloadUrl = uri.toString();
                                RootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SettingsActivity.this, "Profile image stored to firebase database successfully.", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });





            }
        }


    }

    private void InitializeFields()
    {
        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);

        SettingsToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }

    private void UpdateSettings()
    {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();
        //update profile status
        String setProfileType = profileTypeSpinner.getSelectedItem().toString();
        String setInputMethod = inputMethodSpinner.getSelectedItem().toString();

        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Please write your user name!",Toast.LENGTH_SHORT);
        }
        if(TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(this, "Please write your status name!",Toast.LENGTH_SHORT);
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            profileMap.put("profileType", setProfileType);
            profileMap.put("inputMethod", setInputMethod);

            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this,"Profile Updated Successfully", Toast.LENGTH_SHORT);
                    }
                    else
                    {
                        String errorMessage = task.getException().toString();
                        Toast.makeText( SettingsActivity.this, "Error: "+errorMessage, Toast.LENGTH_SHORT);
                    }

                }
            });
        }


    }


    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        //daca userul si-a creat profilul
                        if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")) )
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);

                            //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(userProfileImage);

                        }
                        else if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) )
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);

                        }
                        else
                        {
                            //userName.setVisibility(View.VISIBLE);

                            Toast.makeText(SettingsActivity.this,"Please set and update your profile information", Toast.LENGTH_SHORT);
                        }



                        String retrieveProfileType=null;
                        if(dataSnapshot.hasChild("profileType"))
                        {
                            retrieveProfileType = dataSnapshot.child("profileType").getValue().toString();

                        }
                        String retrieveInputMethod=null;

                        if(dataSnapshot.hasChild("inputMethod"))
                        {
                            retrieveInputMethod = dataSnapshot.child("inputMethod").getValue().toString();
                        }

                        //profile type spinner
                        if (retrieveProfileType != null)
                        {
                            int spinnerPosition = profileTypeSpinnerAdapter.getPosition(retrieveProfileType);
                            profileTypeSpinner.setSelection(spinnerPosition);
                        }
                        if (retrieveInputMethod != null)
                        {
                            int spinnerPosition = inputMethodSpinnerAdapter.getPosition(retrieveInputMethod);
                            inputMethodSpinner.setSelection(spinnerPosition);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainIntent);
        finish();
    }



}
