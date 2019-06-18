package com.example.ioana.vizzioapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

///////////////////////////////////////////////////////
SharedPreferences sharedPreferences, app_preferences;
    SharedPreferences.Editor editor;
    Button colorButton;


    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;
///////////////////////////////////////////////////////



    private Button UpdateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private SeekBar seekBarBrightness, seekBarTextSize;
    private TextView textPreview;


    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private UserPreferencesManager userPreferencesManager = new UserPreferencesManager();

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;

     private ProgressDialog loadingBar;

     private android.support.v7.widget.Toolbar SettingsToolbar;

    Spinner profileTypeSpinner, inputMethodSpinner;
    List<String> profileTypeList  = new ArrayList<>();
    List<String> inputMethodList  = new ArrayList<>();
    ArrayAdapter<String> profileTypeSpinnerAdapter;
    ArrayAdapter<String> inputMethodSpinnerAdapter;

///////////////////////////////////////////////////////////////////////////////////////////////////////

    SeekBar.OnSeekBarChangeListener mySeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            if(seekBar.equals(seekBarBrightness))
            {
                Toast.makeText(SettingsActivity.this, "Value: "+progress, Toast.LENGTH_SHORT).show();
                userPreferencesManager.adjustScreenBrightness(SettingsActivity.this,progress);
            }
            else if(seekBar.equals(seekBarTextSize))
            {
                switch (progress)
                {

                    //small
                    case 1:
                        textPreview.setTextSize(13);
                        textPreview.setText("Small");
                        break;
                    //default
                    case 2:
                        float size = new TextView(SettingsActivity.this).getTextSize();
                        textPreview.setTextSize(15);

                        textPreview.setText("Default");
                        break;
                    //large
                    case 3:
                        textPreview.setTextSize(18);
                        textPreview.setText("Large");
                        break;
                    //largest
                    case 4:
                        textPreview.setTextSize(22);
                        textPreview.setText("Largest");
                        break;
                }

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
///////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        /////////////////////////////////////////////////////////

        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;
        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
        /////////////////////////////////////////////////////////












        setContentView(R.layout.activity_settings);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        seekBarBrightness.setOnSeekBarChangeListener(mySeekBarChangeListener);
        seekBarTextSize.setOnSeekBarChangeListener(mySeekBarChangeListener);

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



        colorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ColorChooserDialog dialog = new ColorChooserDialog(SettingsActivity.this);
                dialog.setTitle("Select");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        colorize();
                        Constant.color = color;

                        setColorTheme();
                        editor.putInt("color", color);
                        editor.putInt("theme",Constant.theme);
                        editor.commit();

                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                dialog.show();
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

        seekBarBrightness =(SeekBar) findViewById(R.id.seekbar_screen_brightness);
        seekBarTextSize =(SeekBar) findViewById(R.id.seekbar_text_size);
        textPreview = (TextView) findViewById(R.id.preview_text_size);


        loadingBar = new ProgressDialog(this);

        SettingsToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.settings_toolbar);
        SettingsToolbar.setBackgroundColor(Constant.color);
        setSupportActionBar(SettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


        ////////////////////////////////////////////////////
        colorButton = (Button) findViewById(R.id.button_color);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        colorize();


        ////////////////////////////////////////////////////
    }

    private void UpdateSettings()
    {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();
        //update profile status
        String setProfileType = profileTypeSpinner.getSelectedItem().toString();
        String setInputMethod = inputMethodSpinner.getSelectedItem().toString();

        String setScreenBrightness = String.valueOf(seekBarBrightness.getProgress());
        String setTextSize = String.valueOf(seekBarTextSize.getProgress());

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
            profileMap.put("screenBrightness", setScreenBrightness);
            profileMap.put("textSize", setTextSize);


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

                        String retrieveScreenBrightness=null;
                        if(dataSnapshot.hasChild("screenBrightness"))
                        {
                            retrieveScreenBrightness = dataSnapshot.child("screenBrightness").getValue().toString();
                        }

                        String retrieveTextSize=null;
                        if(dataSnapshot.hasChild("textSize"))
                        {
                            retrieveTextSize = dataSnapshot.child("textSize").getValue().toString();
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

                        if (retrieveScreenBrightness != null)
                        {
                            seekBarBrightness.setProgress(Integer.parseInt(retrieveScreenBrightness));
                        }


                        if (retrieveTextSize != null)
                        {

                            seekBarTextSize.setProgress(Integer.parseInt(retrieveTextSize));

                            switch (Integer.parseInt(retrieveTextSize))
                            {

                                case 1:
                                    //small
                                    userPreferencesManager.adjustFontScale(SettingsActivity.this,(float)0.8);
                                    break;

                                case 2:
                                    //default
                                    userPreferencesManager.adjustFontScale(SettingsActivity.this,(float)1.0);
                                    break;

                                case 3:
                                    //large
                                    userPreferencesManager.adjustFontScale(SettingsActivity.this,(float)1.3);
                                    break;

                                case 4:
                                    //largest
                                    userPreferencesManager.adjustFontScale(SettingsActivity.this,(float)1.5);
                                    break;
                            }

                        }
                        else
                        {
                            seekBarTextSize.setProgress(1);

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



    public void setColorTheme()
    {

        switch (Constant.color) {
            case 0xffF44336:
                Constant.theme = R.style.AppTheme_red;
                break;
            case 0xffE91E63:
                Constant.theme = R.style.AppTheme_pink;
                break;
            case 0xff9C27B0:
                Constant.theme = R.style.AppTheme_darpink;
                break;
            case 0xff673AB7:
                Constant.theme = R.style.AppTheme_violet;
                break;
            case 0xff3F51B5:
                Constant.theme = R.style.AppTheme_blue;
                break;
            case 0xff03A9F4:
                Constant.theme = R.style.AppTheme_skyblue;
                break;
            case 0xff4CAF50:
                Constant.theme = R.style.AppTheme_green;
                break;
            case 0xffFF9800:
                Constant.theme = R.style.AppTheme;
                break;
            case 0xff9E9E9E:
                Constant.theme = R.style.AppTheme_grey;
                break;
            case 0xff795548:
                Constant.theme = R.style.AppTheme_brown;
                break;
            default:
                Constant.theme = R.style.AppTheme;
                break;
        }
    }


    private void colorize(){
        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.setBounds(58, 58, 58, 58);

        d.getPaint().setStyle(Paint.Style.FILL);

        d.getPaint().setColor(Constant.color);

        colorButton.setBackground(d);
    }

}
