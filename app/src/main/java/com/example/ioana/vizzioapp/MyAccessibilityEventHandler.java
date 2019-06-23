package com.example.ioana.vizzioapp;

import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;


public class MyAccessibilityEventHandler extends View.AccessibilityDelegate
{
    @Override
    public boolean dispatchPopulateAccessibilityEvent(View v, AccessibilityEvent event)
    {


        System.out.println("XXXXSSDXSDSXXxxxxxxxxxxxxxxxx:"+ v.getId());


        //Send no event if the touch was inside Braille dots
        switch (v.getId())
        {



            case R.id.dot_1:
            case R.id.dot_2:
            case R.id.dot_3:
            case R.id.dot_4:
            case R.id.dot_5:
            case R.id.dot_6:
            case R.id.main_layout_braille:
                event.setEventType(0); //Don't handle anything here!!
                break;
        }
        return true;
    }
}
