package com.example.ioana.vizzioapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public class UserPreferencesManager
{

    public static void initializePreferences(Activity activity)
    {
        adjustFontScale(activity,(float)1.5);
        adjustScreenBrightness(activity, 100);

    }

    public static void adjustFontScale(Activity activity, float scale)
    {
        Configuration configuration = activity.getResources().getConfiguration();
        configuration.fontScale = scale;
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    public static void adjustScreenBrightness(Activity activity, int value)
    {
        float BackLightValue = (float)value/100;

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes(); // Get Params
        layoutParams.screenBrightness = BackLightValue; // Set Value

        activity.getWindow().setAttributes(layoutParams); // Set params

    }

    ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!/////


    public static void adjustScreenContrast(Activity activity,int value)
    {
        float BackLightValue = (float)value/100;
        // float BackLightValue = (float)value;

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes(); // Get Params
        //layoutParams.screenBrightness = BackLightValue; // Set Value
        layoutParams.alpha = (float)0.2; // Set Value


        activity.getWindow().setAttributes(layoutParams); // Set params

    }

}
