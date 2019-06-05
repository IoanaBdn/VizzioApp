package com.example.ioana.vizzioapp;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class BrailleKeyboard extends LinearLayout implements View.OnClickListener
{

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity     mHostActivity;

    private Keyboard mKeyboard;
    private int mMessageInputId;

    // constructors
    public BrailleKeyboard(Context context)
    {
        this(context, null, 0);
        init(context);
    }
    public BrailleKeyboard(Context context, Activity host)
    {
        this(context, null, 0);

        init(context);
    }
    public BrailleKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrailleKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    // keyboard keys (buttons)
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;// keyboard keys (buttons)
    private Button btn_5;
    private Button btn_6;

    private void init(Context context)
    {
        mHostActivity = (Activity) context;
        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.braille_keyboard, this, true);

        //btn_1 = (Button) mHostActivity.findViewById(R.id.btn_1);
       // btn_2 = (Button) mHostActivity.findViewById(R.id.btn_2);
        //btn_3 = (Button) mHostActivity.findViewById(R.id.btn_3);
        //btn_4 = (Button) mHostActivity.findViewById(R.id.btn_4);
       // btn_5 = (Button) mHostActivity.findViewById(R.id.btn_5);
       // btn_6 = (Button) mHostActivity.findViewById(R.id.btn_6);

    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view v. */
    public void showCustomKeyboard( View v ) {
        this.setVisibility(View.VISIBLE);
        this.setEnabled(true);
        if( v!=null ) ((InputMethodManager)mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        this.setVisibility(View.GONE);
        this.setEnabled(false);
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    public void registerEditText(int resid)
    {
        mMessageInputId= resid;

        // Find the EditText 'resid'
        EditText edittext= (EditText)mHostActivity.findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) showCustomKeyboard(v); else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override public void onClick(View v)
            {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                //edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type

                return true; // Consume touch event
            }
        });



        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
    @Override
    public void onClick(View v) {

    }
}
