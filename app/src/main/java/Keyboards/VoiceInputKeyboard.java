package Keyboards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ioana.vizzioapp.Constant;
import com.example.ioana.vizzioapp.PermissionHandler;
import com.example.ioana.vizzioapp.R;
import com.example.ioana.vizzioapp.SpeechRecognizerManager;

import java.util.ArrayList;

public class VoiceInputKeyboard  extends LinearLayout implements View.OnClickListener
{

    private SpeechRecognizerManager mSpeechManager;


    // // // // // // // // // // //


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
    private ImageButton backspaceButton;


    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.



    ///////////////////////////////////////////////
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    ///////////////////////////////////////////////





    private void init(Context context) {



        mHostActivity = (Activity) context;





///////////////////////////////////////////////////////////////////////

        app_preferences = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            mHostActivity.setTheme(Constant.theme);
        }else if (appTheme == 0){
            mHostActivity.setTheme(Constant.theme);
        }else{
            mHostActivity.setTheme(appTheme);
        }
///////////////////////////////////////////////////////////////////////



        // initialize buttons

         LayoutInflater.from(context).inflate(R.layout.voice_input_keyboard, this, true);


        keyboardButton = (ImageButton) findViewById(R.id.keyboard_btn);
        startListenButton = (ImageButton) findViewById(R.id.start_listen_btn);
        backspaceButton = (ImageButton) findViewById(R.id.backspace_btn);



        GradientDrawable bgShapeKeyboard = (GradientDrawable)keyboardButton.getBackground();
        bgShapeKeyboard.mutate();
        bgShapeKeyboard.setColor(Constant.color);

        GradientDrawable bgShapeListen = (GradientDrawable)startListenButton.getBackground();
        bgShapeListen.mutate();
        bgShapeListen.setColor(Constant.color);

        GradientDrawable bgShapeBackspace = (GradientDrawable)backspaceButton.getBackground();
        bgShapeBackspace.mutate();
        bgShapeBackspace.setColor(Constant.color);




        // set button click listeners
        keyboardButton.setOnClickListener(this);
       // startListenButton.setOnClickListener(this);
        backspaceButton.setOnClickListener(this);


        startListenButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                EditText MessageInputText= (EditText)mHostActivity.findViewById(mMessageInputId);

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        if(PermissionHandler.checkPermission(mHostActivity,PermissionHandler.RECORD_AUDIO))
                        {
                            if (mSpeechManager != null)
                            {
                                mSpeechManager.destroy();
                                mSpeechManager = null;
                            }
                            startListenButton.setImageResource(R.drawable.microphone);
                        }
                        else
                        {
                            PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,mHostActivity);
                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(mHostActivity, "Listening...", Toast.LENGTH_SHORT).show();
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
                            startListenButton.setImageResource(R.drawable.speaking);
                        }
                        else
                        {
                            PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,mHostActivity);
                        }
                        break;
                }

                return false;
            }
        });

    }

    @Override
    public void onClick(View v)
    {

        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();

        //if( focusCurrent==null || focusCurrent.getClass()!=EditText.class ) return;
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();


        int start = edittext.getSelectionStart();


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
            //__________BACKSPACE__________//
            case R.id.backspace_btn:
                if( editable!=null && start>0 ) editable.delete(start - 1, start);

                break;
        }

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
                    MessageInputText.setText(MessageInputText.getText().toString()+" "+results.get(0));
                    MessageInputText.setSelection(MessageInputText.getText().length());
                }

            }
        });
    }



}
