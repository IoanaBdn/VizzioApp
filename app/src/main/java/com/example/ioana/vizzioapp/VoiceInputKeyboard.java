package com.example.ioana.vizzioapp;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class VoiceInputKeyboard  extends LinearLayout implements View.OnClickListener
{

    private SpeechRecognizerManager mSpeechManager;


    // // // // // // // // // // //
    private VoiceInputKeyboard mKeyboardLayout;

    private Activity mHostActivity;

    private int mMessageInputId;
    // // // // // // // // // // //


    // constructors
    public VoiceInputKeyboard(Context context)
    {
        this(context, null, 0);
        init(context);
    }
    public VoiceInputKeyboard(Context context, Activity host)
    {
        this(context, null, 0);

        init(context);
    }
    public VoiceInputKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceInputKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }





    // keyboard keys (buttons)
    private ImageButton keyboardButton;
    private ImageButton startListenButton;
    private ImageButton stopListenButton;
    private ImageButton backspaceButton;

    /*
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private Button mButtonDelete;
    private Button mButtonEnter;
    */
    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    SparseArray<String> keyValues = new SparseArray<>();

    // Our communication link to the EditText
    InputConnection inputConnection;

    private void init(Context context) {

        mHostActivity = (Activity) context;

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.voice_input_keyboard, this, true);


        keyboardButton = (ImageButton) findViewById(R.id.keyboard_btn);
        startListenButton = (ImageButton) findViewById(R.id.start_listen_btn);
        stopListenButton = (ImageButton) findViewById(R.id.stop_listen_btn);
        backspaceButton = (ImageButton) findViewById(R.id.backspace_btn);

        // set button click listeners
        keyboardButton.setOnClickListener(this);
        startListenButton.setOnClickListener(this);
        stopListenButton.setOnClickListener(this);
        backspaceButton.setOnClickListener(this);


        /*
        mButton1 = (Button) findViewById(R.id.button_1);
        mButton2 = (Button) findViewById(R.id.button_2);
        mButton3 = (Button) findViewById(R.id.button_3);
        mButton4 = (Button) findViewById(R.id.button_4);
        mButton5 = (Button) findViewById(R.id.button_5);
        mButton6 = (Button) findViewById(R.id.button_6);
        mButton7 = (Button) findViewById(R.id.button_7);
        mButton8 = (Button) findViewById(R.id.button_8);
        mButton9 = (Button) findViewById(R.id.button_9);
        mButton0 = (Button) findViewById(R.id.button_0);
        mButtonDelete = (Button) findViewById(R.id.button_delete);
        mButtonEnter = (Button) findViewById(R.id.button_enter);

        // set button click listeners
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);
        mButtonEnter.setOnClickListener(this);

        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
        keyValues.put(R.id.button_enter, "\n");
        */
    }

    @Override
    public void onClick(View v)
    {

        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();

        //if( focusCurrent==null || focusCurrent.getClass()!=EditText.class ) return;
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();


        int start = edittext.getSelectionStart();

        // do nothing if the InputConnection has not been set yet
        //if (inputConnection == null) return;

        EditText MessageInputText= (EditText)mHostActivity.findViewById(mMessageInputId);


        switch (v.getId())
        {
            //__________KEYBOARD__________//
            case R.id.keyboard_btn:

                this.setVisibility(View.GONE);

                CustomKeyboard mCustomKeyboard;
                mCustomKeyboard= new CustomKeyboard(mHostActivity, R.id.keyboardview, R.xml.normal_keyboard, R.id.keyboard_voice );

                mCustomKeyboard.registerEditText(mMessageInputId);

                mCustomKeyboard.showCustomKeyboard(v);
                break;
            //__________START LISTEN__________//
            case R.id.start_listen_btn:
                if(PermissionHandler.checkPermission(mHostActivity,PermissionHandler.RECORD_AUDIO))
                {

                    if(mSpeechManager==null)
                    {
                        SetSpeechListener(MessageInputText);
                    }
                    else if(!mSpeechManager.ismIsListening())
                    {
                        mSpeechManager.destroy();
                        SetSpeechListener(MessageInputText);
                    }
                    //MessageInputText.setText(getString(R.string.you_may_speak));
                    startListenButton.setVisibility(View.GONE);
                    stopListenButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,mHostActivity);
                }
                break;
            //__________STOP LISTEN__________//
            case R.id.stop_listen_btn:
                if(PermissionHandler.checkPermission(mHostActivity,PermissionHandler.RECORD_AUDIO))
                {

                    if (mSpeechManager != null)
                    {
                        //MessageInputText.setText(getString(R.string.destroied));
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                    }
                    stopListenButton.setVisibility(View.GONE);
                    startListenButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,mHostActivity);
                }
                break;
            //__________BACKSPACE__________//
            case R.id.backspace_btn:
                if( editable!=null && start>0 ) editable.delete(start - 1, start);

                break;
        }
/*
        if(v.getId() == R.id.keyboard_btn)
        {
            Log.d("APASAT:","KEYBOARD");
        }
        else if(v.getId() == R.id.start_listen_btn)
        {
            Log.d("APASAT:","start listen");


        }
        else if(v.getId() == R.id.stop_listen_btn)
        {
            Log.d("APASAT:","stop listen");

        }
        else if(v.getId() == R.id.backspace_btn)
        {
            Log.d("APASAT:","backspace");

        }
*/

        // Delete text or input key value
        // All communication goes through the InputConnection
        /*
        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
        */
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }


    private void SetSpeechListener(final EditText MessageInputText)
    {
        mSpeechManager=new SpeechRecognizerManager(mHostActivity, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {

                Toast.makeText(mHostActivity, "Result: "+results, Toast.LENGTH_SHORT).show();

                if(results!=null && results.size()>0)
                {

                    mSpeechManager.destroy();
                    mSpeechManager = null;
                    MessageInputText.setText(results.get(0));

                }
                //else
                    //MessageInputText.setText(getString(R.string.no_results_found));
            }
        });
    }



}
