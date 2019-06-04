package com.example.ioana.vizzioapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    //locul in care se afiseaza continutul taburilor
    private ViewPager myViewPager;
    //locul taburilor
    private TabLayout myTabLayout;
    //accesatorul taburilor
    private TabsAccesorAdapter myTabsAccesorAdapter;

    //userul conectat
    //private FirebaseUser currentUser;

    private FirebaseAuth mAuth;

    //referinta la baza de date
    private DatabaseReference RootRef;


    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("VizzioApp");

        //tabs
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccesorAdapter = new TabsAccesorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccesorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            UpdateUserStatus("online");

            VerifyUserExistance();
        }
    }

    @Override
    protected void onStop()
    {


        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            UpdateUserStatus("offline");

        }

    }

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            UpdateUserStatus("offline");

        }
    }

    private void VerifyUserExistance()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        //checking for the user id, either if the user has recistered or not
        //if is authenticated or not
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //insemna ca userul exista
                if ((dataSnapshot.child("name").exists() ))
                {
                    //Toast.makeText(MainActivity.this, "A Welcome!", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        //user nou nu si-a actualizat setarile poza status si il trimit sa isi puna usernameul intai
                        SendUserToSettingsActivity();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    private void SendUserToLoginActivity()
    {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(loginActivity);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_option)
        {
            UpdateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if(item.getItemId() == R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }
        /*
        if(item.getItemId() == R.id.main_create_group_option)
        {
            RequestNewGroup();
        }
        */
        if(item.getItemId() == R.id.main_find_friends_option)
        {
            SendUserToFindFriendsActivity();

        }
        return true;
    }

    private void SendUserToFindFriendsActivity()
    {
        Intent findFriendsIntent = new Intent(this, FindFriendsActivity.class);

        startActivity(findFriendsIntent);
        finish();
    }

    /*
    private void RequestNewGroup()
    {
        AlertDialog.Builder buider = new AlertDialog.Builder( MainActivity.this, R.style.AlertDialog);
        buider.setTitle("Enter Group Name:");

        final EditText groupNameField = new EditText (MainActivity.this);
        groupNameField.setHint("e.g Some Group Name");
        buider.setView(groupNameField);
        buider.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String  groupName  = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write group name!", Toast.LENGTH_SHORT);
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });
        buider.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        buider.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        // in cazul asta group name este drept id
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName + " group is created successfully", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

*/

    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent= new Intent(this, SettingsActivity.class);

        startActivity(settingsIntent);
    }

    private void UpdateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap <String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }


}
