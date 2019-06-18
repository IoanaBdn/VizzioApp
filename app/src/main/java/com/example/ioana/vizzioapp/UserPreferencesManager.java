package com.example.ioana.vizzioapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.WINDOW_SERVICE;

public class UserPreferencesManager
{





    public void initializePreferences(final Activity activity)
    {

        //set screen brightness

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if( firebaseUser != null )
        {

            String currentUserID = mAuth.getCurrentUser().getUid();
            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

            final int[] screenBrightnessValue = new int[1];



            RootRef.child("Users").child(currentUserID)
                    .addValueEventListener(new ValueEventListener()
                    {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            //ADJUST SCREEN BRIGHTNESS
                            if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("screenBrightness"))  )
                            {

                                screenBrightnessValue[0] = Integer.parseInt(dataSnapshot.child("screenBrightness").getValue().toString());

                                adjustScreenBrightness(activity,  screenBrightnessValue[0]);

                            }
                            else
                            {


                                WindowManager.LayoutParams settings = activity.getWindow().getAttributes();

                                screenBrightnessValue[0] = Math.round(settings.screenBrightness);

                                adjustScreenBrightness(activity,  screenBrightnessValue[0]);

                            }

                            //SET FONT SIZE
                            if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("textSize"))  )
                            {

                                int fontSizeType =Integer.parseInt(dataSnapshot.child("textSize").getValue().toString());

                                switch(fontSizeType)
                                {
                                    case 1:
                                        //small
                                        adjustFontScale(activity,(float)0.8);
                                        break;

                                    case 2:
                                        //default
                                       adjustFontScale(activity,(float)1.0);
                                        break;

                                    case 3:
                                        //large
                                        adjustFontScale(activity,(float)1.3);
                                        break;

                                    case 4:
                                        //largest
                                       adjustFontScale(activity,(float)1.5);
                                        break;
                                }


                            }
                            else
                            {

                                //default
                                adjustFontScale(activity,(float)1.0);

                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });

        }


        //set font size
        //default
        //adjustFontScale(activity,(float)1.0);
        //small
        //adjustFontScale(activity,(float)0.8);
        //large
        //adjustFontScale(activity,(float)1.3);
        //largest
        //adjustFontScale(activity,(float)1.5);
    }





    public void adjustFontScale(Activity activity, float scale)
    {
        Configuration configuration = activity.getResources().getConfiguration();
        configuration.fontScale = scale;
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;


        activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);




    }

    public void adjustScreenBrightness(Activity activity, int value)
    {
        float BackLightValue = (float)value/100;

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes(); // Get Params
        layoutParams.screenBrightness = BackLightValue; // Set Value

        activity.getWindow().setAttributes(layoutParams); // Set params

    }

    ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!/////


    public void adjustScreenContrast(Activity activity,int value)
    {
        float BackLightValue = (float)value/100;
        // float BackLightValue = (float)value;

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes(); // Get Params
        //layoutParams.screenBrightness = BackLightValue; // Set Value
        layoutParams.alpha = (float)0.2; // Set Value


        activity.getWindow().setAttributes(layoutParams); // Set params

    }

}
