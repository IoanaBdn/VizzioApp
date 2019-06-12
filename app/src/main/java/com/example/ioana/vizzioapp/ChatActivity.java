package com.example.ioana.vizzioapp;




import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar ChatToolbar;

    private ImageButton SendMessageButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private final List <Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String inputMethod="";

    private SpeechRecognizerManager mSpeechManager;

    //voice keyboard
    private VoiceInputKeyboard voiceKeyboard ;
    //normal keyboard
    private CustomKeyboard mCustomKeyboard;
    //braille keyboard
    private BrailleKeyboard brailleKeyboard;
  
    private ImageButton StartListeningButton;
    private ImageButton StopListeningButton;
    private ImageButton BackspaceButton;
    private ImageButton KeyboardButton;


    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UserPreferencesManager.initializePreferences(this);


        setContentView(R.layout.activity_chat);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params)
            {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results)
            {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matches!=null)
                {
                    MessageInputText.setText(matches.get(0));
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_user_image").toString();


        //////////////////////////////////////////////////////////////////////////////////////////

        InitializeControllers();

        ///////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////////////



        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);



        SendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });

    }




    private void InitializeControllers()
    {


        ///////////////////////////////////////////////////////////////////////////////////////////
        MessageInputText = (EditText) findViewById(R.id.input_message);

        setKeyboard();


        /*
        MessageInputText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {




                keyboard.setVisibility(View.VISIBLE);

            }
        });

        */


        ///////////////////////////////////////////////////////////////////////////////////////////


        ChatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater  = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);


        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        //MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList  = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager =  new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


    }


    @Override
    protected void onPause()
    {
        if(mSpeechManager!=null)
        {
            mSpeechManager.destroy();
            mSpeechManager=null;
        }
        super.onPause();
    }


    private void setKeyboard()
    {

        RootRef.child("Users").child(messageSenderID).child("inputMethod")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        //Toast.makeText(ChatActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                        if(dataSnapshot.exists())
                        {
                            inputMethod = dataSnapshot.getValue().toString();
                            Toast.makeText(ChatActivity.this, inputMethod, Toast.LENGTH_SHORT).show();

                            switch (inputMethod)
                            {
                                case "Voice Input Keyboard":




                                   // keyboard= new VoiceInputKeyboard(getApplicationContext(),ChatActivity.this, R.id.keyboard_voice );
                                   // keyboard.registerEditText(R.id.input_message);

                                    voiceKeyboard = (VoiceInputKeyboard) findViewById(R.id.keyboard_voice);
                                    //StartListeningButton = (ImageButton) findViewById(R.id.start_listen_btn);
                                   // StopListeningButton = (ImageButton) findViewById(R.id.stop_listen_btn);
                                    //BackspaceButton = (ImageButton) findViewById(R.id.backspace_btn);
                                   // KeyboardButton = (ImageButton) findViewById(R.id.keyboard_btn);



                                    hideSystemKeyboard();




                                    /*
                                    MessageInputText.setOnTouchListener(new View.OnTouchListener()
                                    {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event)
                                        {
                                            keyboard.setVisibility(View.VISIBLE);
                                            return false;
                                        }
                                    });
                                    */

                                    // pass the InputConnection from the EditText to the keyboard
                                   // InputConnection ic = MessageInputText.onCreateInputConnection(new EditorInfo());
                                   // keyboard.setInputConnection(ic);

                                    voiceKeyboard.registerEditText(R.id.input_message);

                                    //Toast.makeText(ChatActivity.this, "aici voice...", Toast.LENGTH_SHORT).show();

                                    /*
                                    StartListeningButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            Toast.makeText(ChatActivity.this, "start listening...", Toast.LENGTH_SHORT).show();
                                            if(PermissionHandler.checkPermission(ChatActivity.this,PermissionHandler.RECORD_AUDIO))
                                            {

                                                if(mSpeechManager==null)
                                                {
                                                    SetSpeechListener();
                                                }
                                                else if(!mSpeechManager.ismIsListening())
                                                {
                                                    mSpeechManager.destroy();
                                                    SetSpeechListener();
                                                }
                                                MessageInputText.setText(getString(R.string.you_may_speak));
                                                StartListeningButton.setVisibility(View.GONE);
                                                StopListeningButton.setVisibility(View.VISIBLE);
                                            }
                                            else
                                            {
                                                PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,ChatActivity.this);
                                            }
                                        }
                                    });






                                    StopListeningButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            if(PermissionHandler.checkPermission(ChatActivity.this,PermissionHandler.RECORD_AUDIO))
                                            {

                                                if (mSpeechManager != null)
                                                {
                                                    //MessageInputText.setText(getString(R.string.destroied));
                                                    mSpeechManager.destroy();
                                                    mSpeechManager = null;
                                                }
                                                StopListeningButton.setVisibility(View.GONE);
                                                StartListeningButton.setVisibility(View.VISIBLE);
                                            }
                                            else
                                            {
                                                PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,ChatActivity.this);
                                            }
                                        }

                                    });





                                    BackspaceButton.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // the code to execute repeatedly

                                            int length = MessageInputText.getText().length();
                                            if (length > 0) {
                                                MessageInputText.getText().delete(length - 1, length);
                                            }
                                        }
                                    }));

                                    KeyboardButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {

                                            keyboard.setVisibility(View.GONE);
                                            mCustomKeyboard= new CustomKeyboard(ChatActivity.this, R.id.keyboardview, R.xml.normal_keyboard );

                                            mCustomKeyboard.registerEditText(R.id.input_message);
                                            MessageInputText.requestFocus();
                                        }
                                    });

                                    */

                                    break;
                                case "Normal Keyboard":

                                    hideSystemKeyboard();


                                    mCustomKeyboard= new CustomKeyboard(ChatActivity.this, R.id.keyboardview, R.xml.normal_keyboard, R.id.keyboard_voice);
                                   // mCustomKeyboard= new CustomKeyboard(ChatActivity.this, R.id.keyboardview, R.layout.voice_input_keyboard );

                                    mCustomKeyboard.registerEditText(R.id.input_message);


                                    break;
                                case "Braille Keyboard":
                                    hideSystemKeyboard();
                                    brailleKeyboard = (BrailleKeyboard) findViewById(R.id.keyboard_braille);
                                    brailleKeyboard.registerEditText(R.id.input_message);

                                    break;
                                default: break;
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });






    }

    @Override public void onBackPressed()
    {
        if(mCustomKeyboard!=null)
            if( mCustomKeyboard.isCustomKeyboardVisible() ) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
        if(voiceKeyboard!=null)
            if( voiceKeyboard.isCustomKeyboardVisible() ) voiceKeyboard.hideCustomKeyboard(); else this.finish();
        if(brailleKeyboard!=null)
            if( brailleKeyboard.isCustomKeyboardVisible() ) brailleKeyboard.hideCustomKeyboard(); else this.finish();

    }

    public void hideSystemKeyboard()
    {
        // prevent system keyboard from appearing when EditText is tapped
        //MessageInputText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        //MessageInputText.setTextIsSelectable(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MessageInputText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(MessageInputText, false);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case PermissionHandler.RECORD_AUDIO:
                if(grantResults.length>0) {
                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                        StartListeningButton.performClick();
                    }
                }
                break;

        }
    }

    private void SetSpeechListener()
    {
        mSpeechManager=new SpeechRecognizerManager(ChatActivity.this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {

                Toast.makeText(ChatActivity.this, "Result: "+results, Toast.LENGTH_SHORT).show();

                if(results!=null && results.size()>0)
                {

                   // if(results.size()==1)
                    //{
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                        MessageInputText.setText(results.get(0));
                    //}
                    //else {
                    //    StringBuilder sb = new StringBuilder();
                     //   if (results.size() > 5) {
                     //       results = (ArrayList<String>) results.subList(0, 5);
                       // }
                        //for (String result : results) {
                          //  sb.append(result).append("\n");
                        //}
                        //MessageInputText.setText(sb.toString());
                    //}
                }
                else
                    MessageInputText.setText(getString(R.string.no_results_found));
            }
        });
    }



    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageSenderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if( dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state  = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date  = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time  = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");

                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last seen:" + date+ " " + time);

                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                      Messages messages = dataSnapshot.getValue(Messages.class);
                      messagesList.add(messages);
                      messageAdapter.notifyDataSetChanged();
                      userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage()
    {
        String messageText =  MessageInputText.getText().toString();

        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID +"/"+messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID +"/"+messageSenderID;

            // cheie la fiecare mesaj sa nu se faca replace
            //this basically create a key
            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderRef).child(messageReceiverRef).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT ).show();

                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT ).show();

                    }

                    MessageInputText.setText("");

                }
            });

        }



    }

}
