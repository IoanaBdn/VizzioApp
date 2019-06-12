package com.example.ioana.vizzioapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BrailleKeyboard extends RelativeLayout implements View.OnTouchListener
{

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity     mHostActivity;

    private Keyboard mKeyboard;
    private int mMessageInputId;

    //////////////////////////////////////////////////////
    private RelativeLayout mMainLayoutBraille;

    private boolean areaDetected;
    private String currentTag;

   // GestureDetector gestureDetector;
//


    ArrayList<Integer> selectedPatternArrayList = new ArrayList<>();
    Set<Integer> selectedPattern = new HashSet(selectedPatternArrayList);
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////

    //Get double tap gesture
    GestureDoubleTap gestureDoubleTap = new GestureDoubleTap();
    GestureDetector gestureDetector;
    //Detect special moves inside free area
    boolean isSpecialMove, isMainFingerUp = true, isPointersFingersUp = true;
    ///////////////////////////////////////////////////
    Vibrator vibrator = null;
    //Settings
    boolean isInsideBrailleDot;

    int brailleDotRadius;
    int brailleDotRadiusDoubled;

    ///////////////////////////////////////////////////

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

    private TextView txt_dot1;
    private TextView txt_dot2;
    private TextView txt_dot3;
    private TextView txt_dot4;
    private TextView txt_dot5;
    private TextView txt_dot6;

    private RelativeLayout dot_1;
    private RelativeLayout dot_2;
    private RelativeLayout dot_3;
    private RelativeLayout dot_4;
    private RelativeLayout dot_5;
    private RelativeLayout dot_6;

    private void init(Context context)
    {
        //Vibration on dot tap
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        //Detect double tap gesture to hide the keyboard
        gestureDetector = new GestureDetector(context, gestureDoubleTap);

        mHostActivity = (Activity) context;
        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.braille_keyboard, this, true);

         brailleDotRadius = 110+30;
         brailleDotRadiusDoubled = brailleDotRadius*brailleDotRadius;

        dot_1 = (RelativeLayout) findViewById(R.id.dot_1);
        dot_2 = (RelativeLayout) findViewById(R.id.dot_2);
        dot_3 = (RelativeLayout) findViewById(R.id.dot_3);
        dot_4 = (RelativeLayout) findViewById(R.id.dot_4);
        dot_5 = (RelativeLayout) findViewById(R.id.dot_5);
        dot_6 = (RelativeLayout) findViewById(R.id.dot_6);

        txt_dot1 = (TextView) findViewById(R.id.txt_dot_1);
        txt_dot2 = (TextView) findViewById(R.id.txt_dot_2);
        txt_dot3 = (TextView) findViewById(R.id.txt_dot_3);
        txt_dot4 = (TextView) findViewById(R.id.txt_dot_4);
        txt_dot5 = (TextView) findViewById(R.id.txt_dot_5);
        txt_dot6 = (TextView) findViewById(R.id.txt_dot_6);

        /*
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        */
        mMainLayoutBraille = (RelativeLayout) findViewById(R.id.main_layout_braille);



        //Toast.makeText(mHostActivity, "btn:"+btn_1, Toast.LENGTH_SHORT).show();

        /*
        btn_1.setOnTouchListener(mOnTouchListener);
        btn_2.setOnTouchListener(mOnTouchListener);
        btn_3.setOnTouchListener(mOnTouchListener);
        btn_4.setOnTouchListener(mOnTouchListener);
        btn_5.setOnTouchListener(mOnTouchListener);
        btn_6.setOnTouchListener(mOnTouchListener);
        */



    }


    //--------------------------------------------------------------------------------------------//
    //To handle double tap and hide the keyboard
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    //--------------------------------------------------------------------------------------------//

/*
    OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    // PRESSED
                    actionDown(v.getId());
                    return true; // if you want to handle the touch event
                case MotionEvent.ACTION_UP:
                    // RELEASED
                    actionUp(v.getId());
                    return true; // if you want to handle the touch event
                case MotionEvent.ACTION_MOVE:
                    actionHoverEnter(v.getId());
            }
            return false;
        }


    };



    private void actionHoverEnter(int id)
    {
        switch (id)
        {
            case R.id.btn_1:
                btn_1.setTextColor(Color.BLUE);
                break;
            case R.id.btn_2:
                btn_2.setTextColor(Color.BLUE);
                break;
            case R.id.btn_3:
                btn_3.setTextColor(Color.BLUE);
                break;
            case R.id.btn_4:
                btn_4.setTextColor(Color.BLUE);
                break;
            case R.id.btn_5:
                btn_5.setTextColor(Color.BLUE);
                break;
            case R.id.btn_6:
                btn_6.setTextColor(Color.BLUE);
                break;
        }
    }

    private void actionUp(int id)
    {
        switch (id)
        {
            case R.id.btn_1:
                btn_1.setTextColor(Color.WHITE);
                break;
            case R.id.btn_2:
                btn_2.setTextColor(Color.WHITE);
                break;

            case R.id.btn_3:
                btn_3.setTextColor(Color.WHITE);
                break;
            case R.id.btn_4:
                btn_4.setTextColor(Color.WHITE);
                break;
            case R.id.btn_5:
                btn_5.setTextColor(Color.WHITE);
                break;
            case R.id.btn_6:
                btn_6.setTextColor(Color.WHITE);
                break;
        }
    }

    private void actionDown(int id)
    {
        switch (id)
        {
            case R.id.btn_1:
                btn_1.setTextColor(Color.RED);
                break;
            case R.id.btn_2:
                btn_2.setTextColor(Color.RED);
                break;
            case R.id.btn_3:
                btn_3.setTextColor(Color.RED);
                break;
            case R.id.btn_4:
                btn_4.setTextColor(Color.RED);
                break;
            case R.id.btn_5:
                btn_5.setTextColor(Color.RED);
                break;
            case R.id.btn_6:
                btn_6.setTextColor(Color.RED);
                break;
        }
    }
*/
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
                String binaryString = "01100001";
                String hexString = new BigInteger(binaryString, 2).toString(16);
                Log.d("BYTES","hex:"+hexString);

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
    public boolean onTouchEvent(MotionEvent event)
    {
        try
        {

        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_HOVER_ENTER:
                isMainFingerUp = false;
                boolean isDownDot1Selected = (event.getX() - (dot_1.getX() + brailleDotRadius)) * (event.getX() - (dot_1.getX() + brailleDotRadius)) + (event.getY() - (dot_1.getY() + brailleDotRadius)) * (event.getY() - (dot_1.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isDownDot2Selected = (event.getX() - (dot_2.getX() + brailleDotRadius)) * (event.getX() - (dot_2.getX() + brailleDotRadius)) + (event.getY() - (dot_2.getY() + brailleDotRadius)) * (event.getY() - (dot_2.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isDownDot3Selected = (event.getX() - (dot_3.getX() + brailleDotRadius)) * (event.getX() - (dot_3.getX() + brailleDotRadius)) + (event.getY() - (dot_3.getY() + brailleDotRadius)) * (event.getY() - (dot_3.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isDownDot4Selected = (event.getX() - (dot_4.getX() + brailleDotRadius)) * (event.getX() - (dot_4.getX() + brailleDotRadius)) + (event.getY() - (dot_4.getY() + brailleDotRadius)) * (event.getY() - (dot_4.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isDownDot5Selected = (event.getX() - (dot_5.getX() + brailleDotRadius)) * (event.getX() - (dot_5.getX() + brailleDotRadius)) + (event.getY() - (dot_5.getY() + brailleDotRadius)) * (event.getY() - (dot_5.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isDownDot6Selected = (event.getX() - (dot_6.getX() + brailleDotRadius)) * (event.getX() - (dot_6.getX() + brailleDotRadius)) + (event.getY() - (dot_6.getY() + brailleDotRadius)) * (event.getY() - (dot_6.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;

                if (isDownDot1Selected)
                {
                    isInsideBrailleDot = true;
                    selectedPattern.add(1);

                    Toast.makeText(mHostActivity,"Hover enter 1", Toast.LENGTH_SHORT).show();
                    txt_dot1.setTextColor(Color.RED);

                    vibrator.vibrate(100);
                }

                if (isDownDot2Selected)
                {
                    isInsideBrailleDot = true;
                    selectedPattern.add(2);

                    Toast.makeText(mHostActivity,"Hover enter 2", Toast.LENGTH_SHORT).show();
                    txt_dot2.setTextColor(Color.RED);

                    vibrator.vibrate(100);

                }
                if (isDownDot3Selected)
                {
                    isInsideBrailleDot = true;
                    selectedPattern.add(3);

                    Toast.makeText(mHostActivity,"Hover enter 3", Toast.LENGTH_SHORT).show();
                    txt_dot3.setTextColor(Color.RED);

                    vibrator.vibrate(100);
                }
                if (isDownDot4Selected)
                {
                    isInsideBrailleDot = true;
                    selectedPattern.add(4);

                    Toast.makeText(mHostActivity,"Hover enter 4", Toast.LENGTH_SHORT).show();
                    txt_dot4.setTextColor(Color.RED);

                    vibrator.vibrate(100);
                }
                if (isDownDot5Selected)
                {
                    isInsideBrailleDot = true;
                    selectedPattern.add(5);

                    Toast.makeText(mHostActivity,"Hover enter 5", Toast.LENGTH_SHORT).show();
                    txt_dot5.setTextColor(Color.RED);

                    vibrator.vibrate(100);
                }
                if (isDownDot6Selected)
                {
                    isInsideBrailleDot = true;
                    selectedPattern.add(6);

                    Toast.makeText(mHostActivity,"Hover enter 6", Toast.LENGTH_SHORT).show();
                    txt_dot6.setTextColor(Color.RED);

                    vibrator.vibrate(100);
                }

                break;
            case (MotionEvent.ACTION_POINTER_DOWN):
                isPointersFingersUp = false;
                //To handle multi-touch
                for (int pointers = 1; pointers < pointerCount; pointers++) {
                    boolean isPointerDownDot1Selected = (event.getX(pointers) - (dot_1.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_1.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_1.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_1.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerDownDot2Selected = (event.getX(pointers) - (dot_2.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_2.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_2.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_2.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerDownDot3Selected = (event.getX(pointers) - (dot_3.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_3.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_3.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_3.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerDownDot4Selected = (event.getX(pointers) - (dot_4.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_4.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_4.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_4.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerDownDot5Selected = (event.getX(pointers) - (dot_5.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_5.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_5.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_5.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerDownDot6Selected = (event.getX(pointers) - (dot_6.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_6.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_6.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_6.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;

                    if (isPointerDownDot1Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(1);

                        Toast.makeText(mHostActivity,"Pointer down 1", Toast.LENGTH_SHORT).show();
                        txt_dot1.setTextColor(Color.BLUE);

                        vibrator.vibrate(100);
                    }
                    if (isPointerDownDot2Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(2);

                        Toast.makeText(mHostActivity,"Pointer down 2", Toast.LENGTH_SHORT).show();
                        txt_dot2.setTextColor(Color.BLUE);

                        vibrator.vibrate(100);
                    }
                    if (isPointerDownDot3Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(3);

                        Toast.makeText(mHostActivity,"Pointer down 3", Toast.LENGTH_SHORT).show();
                        txt_dot3.setTextColor(Color.BLUE);

                        vibrator.vibrate(100);
                    }
                    if (isPointerDownDot4Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(4);

                        Toast.makeText(mHostActivity,"Pointer down 4", Toast.LENGTH_SHORT).show();
                        txt_dot4.setTextColor(Color.BLUE);

                        vibrator.vibrate(100);
                    }
                    if (isPointerDownDot5Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(5);

                        Toast.makeText(mHostActivity,"Pointer down 5", Toast.LENGTH_SHORT).show();
                        txt_dot5.setTextColor(Color.BLUE);

                        vibrator.vibrate(100);
                    }
                    if (isPointerDownDot6Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(6);

                        Toast.makeText(mHostActivity,"Pointer down 6", Toast.LENGTH_SHORT).show();
                        txt_dot6.setTextColor(Color.BLUE);

                        vibrator.vibrate(100);
                    }

                } //End for loop
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_HOVER_MOVE:
                //Must be outside of the free area, because of gestures
                for (int pointers = 0; pointers < pointerCount; pointers++)
                {
                    boolean isMovePointerDot1Selected = (event.getX(pointers) - (dot_1.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_1.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_1.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_1.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isMovePointerDot2Selected = (event.getX(pointers) - (dot_2.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_2.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_2.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_2.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isMovePointerDot3Selected = (event.getX(pointers) - (dot_3.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_3.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_3.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_3.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isMovePointerDot4Selected = (event.getX(pointers) - (dot_4.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_4.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_4.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_4.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isMovePointerDot5Selected = (event.getX(pointers) - (dot_5.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_5.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_5.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_5.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isMovePointerDot6Selected = (event.getX(pointers) - (dot_6.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_6.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_6.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_6.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    if (isMovePointerDot1Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(1);
                        Toast.makeText(mHostActivity,"Hover 1", Toast.LENGTH_SHORT).show();
                        txt_dot1.setTextColor(Color.GREEN);

                        vibrator.vibrate(20);
                    }
                    if (isMovePointerDot2Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(2);

                        Toast.makeText(mHostActivity,"Hover 2", Toast.LENGTH_SHORT).show();
                        txt_dot2.setTextColor(Color.GREEN);

                        vibrator.vibrate(20);
                    }
                    if (isMovePointerDot3Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(3);

                        Toast.makeText(mHostActivity,"Hover 3", Toast.LENGTH_SHORT).show();
                        txt_dot3.setTextColor(Color.GREEN);

                        vibrator.vibrate(20);
                    }
                    if (isMovePointerDot4Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(4);

                        Toast.makeText(mHostActivity,"Hover 4", Toast.LENGTH_SHORT).show();
                        txt_dot4.setTextColor(Color.GREEN);

                        vibrator.vibrate(20);
                    }
                    if (isMovePointerDot5Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(5);

                        Toast.makeText(mHostActivity,"Hover 5", Toast.LENGTH_SHORT).show();
                        txt_dot5.setTextColor(Color.GREEN);

                        vibrator.vibrate(20);
                    }
                    if (isMovePointerDot6Selected)
                    {
                        isInsideBrailleDot = true;
                        selectedPattern.add(6);

                        Toast.makeText(mHostActivity,"Hover 6", Toast.LENGTH_SHORT).show();
                        txt_dot6.setTextColor(Color.GREEN);

                        vibrator.vibrate(20);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isPointersFingersUp = true;
                for (int pointers = 1; pointers < pointerCount; pointers++) {
                    boolean isPointerUpDot1Selected = (event.getX(pointers) - (dot_1.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_1.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_1.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_1.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerUpDot2Selected = (event.getX(pointers) - (dot_2.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_2.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_2.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_2.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerUpDot3Selected = (event.getX(pointers) - (dot_3.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_3.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_3.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_3.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerUpDot4Selected = (event.getX(pointers) - (dot_4.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_4.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_4.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_4.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerUpDot5Selected = (event.getX(pointers) - (dot_5.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_5.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_5.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_5.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                    boolean isPointerUpDot6Selected = (event.getX(pointers) - (dot_6.getX() + brailleDotRadius)) * (event.getX(pointers) - (dot_6.getX() + brailleDotRadius)) + (event.getY(pointers) - (dot_6.getY() + brailleDotRadius)) * (event.getY(pointers) - (dot_6.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;

                    if (isPointerUpDot1Selected) {
                        selectedPattern.add(1);
                    }

                    if (isPointerUpDot2Selected) {
                        selectedPattern.add(2);
                    }

                    if (isPointerUpDot3Selected) {
                        selectedPattern.add(3);
                    }

                    if (isPointerUpDot4Selected) {
                        selectedPattern.add(4);
                    }

                    if (isPointerUpDot5Selected) {
                        selectedPattern.add(5);
                    }

                    if (isPointerUpDot6Selected) {
                        selectedPattern.add(6);
                    }

                    //Send patterns when all fingers up
                    sendPatterns();
                    //Clear pattern
                    selectedPattern.clear();
                }

                    Toast.makeText(mHostActivity,"Se fini la comedi", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_HOVER_EXIT:
                isMainFingerUp = true;
                isInsideBrailleDot = false;

                //Get the dot when the user up his finger
                //Don't remove this, if the user selected stop over dots, I need to get the action up
                boolean isUpDot1Selected = (event.getX() - (dot_1.getX() + brailleDotRadius)) * (event.getX() - (dot_1.getX() + brailleDotRadius)) + (event.getY() - (dot_1.getY() + brailleDotRadius)) * (event.getY() - (dot_1.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isUpDot2Selected = (event.getX() - (dot_2.getX() + brailleDotRadius)) * (event.getX() - (dot_2.getX() + brailleDotRadius)) + (event.getY() - (dot_2.getY() + brailleDotRadius)) * (event.getY() - (dot_2.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isUpDot3Selected = (event.getX() - (dot_3.getX() + brailleDotRadius)) * (event.getX() - (dot_3.getX() + brailleDotRadius)) + (event.getY() - (dot_3.getY() + brailleDotRadius)) * (event.getY() - (dot_3.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isUpDot4Selected = (event.getX() - (dot_4.getX() + brailleDotRadius)) * (event.getX() - (dot_4.getX() + brailleDotRadius)) + (event.getY() - (dot_4.getY() + brailleDotRadius)) * (event.getY() - (dot_4.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isUpDot5Selected = (event.getX() - (dot_5.getX() + brailleDotRadius)) * (event.getX() - (dot_5.getX() + brailleDotRadius)) + (event.getY() - (dot_5.getY() + brailleDotRadius)) * (event.getY() - (dot_5.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;
                boolean isUpDot6Selected = (event.getX() - (dot_6.getX() + brailleDotRadius)) * (event.getX() - (dot_6.getX() + brailleDotRadius)) + (event.getY() - (dot_6.getY() + brailleDotRadius)) * (event.getY() - (dot_6.getY() + brailleDotRadius)) <= brailleDotRadiusDoubled;

                if (isUpDot1Selected)
                {
                    selectedPattern.add(1);

                    vibrator.vibrate(100);
                }

                if (isUpDot2Selected)
                {
                    selectedPattern.add(2);

                    vibrator.vibrate(100);
                }

                if (isUpDot3Selected)
                {
                    selectedPattern.add(3);

                    vibrator.vibrate(100);
                }

                if (isUpDot4Selected)
                {
                    selectedPattern.add(4);

                    vibrator.vibrate(100);
                }

                if (isUpDot5Selected)
                {
                    selectedPattern.add(5);

                    vibrator.vibrate(100);
                }

                if (isUpDot6Selected)
                {
                    selectedPattern.add(6);

                    vibrator.vibrate(100);
                }


                //Send patterns when all fingers up
                sendPatterns();
                //Clear pattern
                selectedPattern.clear();
                break;
        }

    } catch (Exception e) {
    e.printStackTrace();
}
        return true;
    }



    //--------------------------------------------------------------------------------------------//
    //Send patterns when all fingers up
    private void sendPatterns() {
        if (isMainFingerUp && isPointersFingersUp)
        {

            Toast.makeText(mHostActivity, "SEnd Patern"+selectedPattern.toString(), Toast.LENGTH_SHORT).show();
            //Clear the last up
            ///////selectedPatternHandler.removeCallbacks(selectedPatternRunnable);
            //Send the selected pattern
           /////// selectedPatternHandler.postDelayed(selectedPatternRunnable, selectedDotsPeriod);
        }
    }





    //--------------------------------------------------------------------------------------------//
    //To handle double tap and hide the keyboard
    boolean isDoubleTap = true;
    public class GestureDoubleTap extends GestureDetector.SimpleOnGestureListener {

        //Because of fast switch between keyboards, sometimes it hide the keyboard
        @Override
        public boolean onDown(MotionEvent e) {
            isDoubleTap = true;
            return super.onDown(e);
        }

        //Because of fast switch between keyboards, sometimes it hide the keyboard
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            isDoubleTap = false;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            try {
                /*
                //Check for special moves in free area
                boolean doubleTapInsideBrailleDot = (event.getX() - (brailleDot1.getX() + brailleDotRadius * 1.5)) * (event.getX() - (brailleDot1.getX() + brailleDotRadius * 1.5)) + (event.getY() - (brailleDot1.getY() + brailleDotRadius * 1.5)) * (event.getY() - (brailleDot1.getY() + brailleDotRadius * 1.5)) <= brailleDotRadiusDoubled * 1.5 ||
                        (event.getX() - (brailleDot2.getX() + brailleDotRadius * 1.5)) * (event.getX() - (brailleDot2.getX() + brailleDotRadius * 1.5)) + (event.getY() - (brailleDot2.getY() + brailleDotRadius * 1.5)) * (event.getY() - (brailleDot2.getY() + brailleDotRadius * 1.5)) <= brailleDotRadiusDoubled * 1.5 ||
                        (event.getX() - (brailleDot3.getX() + brailleDotRadius * 1.5)) * (event.getX() - (brailleDot3.getX() + brailleDotRadius * 1.5)) + (event.getY() - (brailleDot3.getY() + brailleDotRadius * 1.5)) * (event.getY() - (brailleDot3.getY() + brailleDotRadius * 1.5)) <= brailleDotRadiusDoubled * 1.5 ||
                        (event.getX() - (brailleDot4.getX() + brailleDotRadius * 1.5)) * (event.getX() - (brailleDot4.getX() + brailleDotRadius * 1.5)) + (event.getY() - (brailleDot4.getY() + brailleDotRadius * 1.5)) * (event.getY() - (brailleDot4.getY() + brailleDotRadius * 1.5)) <= brailleDotRadiusDoubled * 1.5 ||
                        (event.getX() - (brailleDot5.getX() + brailleDotRadius * 1.5)) * (event.getX() - (brailleDot5.getX() + brailleDotRadius * 1.5)) + (event.getY() - (brailleDot5.getY() + brailleDotRadius * 1.5)) * (event.getY() - (brailleDot5.getY() + brailleDotRadius * 1.5)) <= brailleDotRadiusDoubled * 1.5 ||
                        (event.getX() - (brailleDot6.getX() + brailleDotRadius * 1.5)) * (event.getX() - (brailleDot6.getX() + brailleDotRadius * 1.5)) + (event.getY() - (brailleDot6.getY() + brailleDotRadius * 1.5)) * (event.getY() - (brailleDot6.getY() + brailleDotRadius * 1.5)) <= brailleDotRadiusDoubled * 1.5;

                if (isDoubleTap && !doubleTapInsideBrailleDot && !Common.touchInsideOpsBars) {
                    brailleIME.requestHideSelf(0);
                }
                */
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }




}



