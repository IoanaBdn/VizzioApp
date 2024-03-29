package Keyboards;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.media.AudioManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.InputType;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ioana.vizzioapp.Constant;
import com.example.ioana.vizzioapp.R;

import Keyboards.VoiceInputKeyboard;

import static android.content.Context.AUDIO_SERVICE;


public class CustomKeyboard {

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity mHostActivity;

    private Keyboard mKeyboard;


    TextView keyPreview;


    //voice keyboard
    private VoiceInputKeyboard voiceKeyboard ;

    Boolean isCaps = false;

    /** The key (code) handler. */


    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {
        public final static int CodeVoiceKeyboard    = -4;

        @Override public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable

            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();

            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();

            int start = edittext.getSelectionStart();

            playClick(primaryCode);
            switch (primaryCode){

                case Keyboard.KEYCODE_DELETE:
                    //inputConnection.deleteSurroundingText(1,0);
                    if( editable!=null && start>0 ) editable.delete(start - 1, start);
                    break;

                case Keyboard.KEYCODE_SHIFT:
                    isCaps = !isCaps;
                    mKeyboard.setShifted(isCaps);
                    mKeyboardView.invalidateAllKeys();
                    break;

                case CodeVoiceKeyboard:
                    int inputId = edittext.getId();
                    hideCustomKeyboard();
                    voiceKeyboard.registerEditText(inputId);
                    View rootView = mHostActivity.getWindow().getDecorView().findViewById(android.R.id.content);

                    voiceKeyboard.showCustomKeyboard(rootView);

                    break;

                default:
                    char code = (char) primaryCode;
                    if(Character.isLetter(code) && isCaps){
                        code = Character.toUpperCase(code);
                    }
                    editable.insert(start, String.valueOf(code));

            }


        }
        private void playClick(int i){

            AudioManager audioManager = (AudioManager) mHostActivity.getSystemService(AUDIO_SERVICE);
            switch(i){
                case 32:
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                    break;

                case Keyboard.KEYCODE_DONE:
                case 10:
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                    break;

                case Keyboard.KEYCODE_DELETE:
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                    break;

                default:
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }

        }
        @Override public void onPress(int arg0) {
        }
        @Override public void onRelease(int primaryCode) {
        }
        @Override public void onText(CharSequence text) {
        }
        @Override public void swipeDown() {
        }
        @Override public void swipeLeft() {
        }
        @Override public void swipeRight() {
        }
        @Override public void swipeUp() {
        }
    };



    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(int)}.
     *
     * @param host The hosting activity.
     * @param viewid The id of the KeyboardView.
     * @param layoutid The id of the xml file containing the keyboard layout.
     */
    public CustomKeyboard(Activity host, int viewid, int layoutid, int kVoiceLayoutId) {
        mHostActivity = host;
        mKeyboardView = (KeyboardView)mHostActivity.findViewById(viewid);
        voiceKeyboard = (VoiceInputKeyboard) mHostActivity.findViewById(kVoiceLayoutId);

       // NinePatchDrawable drawable = (NinePatchDrawable) mKeyboardView.getBackground();
       //drawable.setTint(Color.GREEN);

/*
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(mHostActivity, R.drawable.normal);

        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.RED);


        Toast.makeText(mHostActivity, " "+drawable, Toast.LENGTH_SHORT).show();

        */

        //GradientDrawable bgShapeKeyboard = (GradientDrawable)voiceKeyboard.getBackground();
        //bgShapeKeyboard.mutate();
        //bgShapeKeyboard.setColor(Constant.color);

        mKeyboard =  new Keyboard(mHostActivity, layoutid);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view v. */
    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param resid The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(int resid) {
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



}


// NOTE How can we change the background color of some keys (like the shift/ctrl/alt)?
// NOTE What does android:keyEdgeFlags do/mean
