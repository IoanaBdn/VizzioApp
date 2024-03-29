package com.example.ioana.vizzioapp;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment
{


    private View PrivateChatsView;
    private RecyclerView chatsList;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public ChatsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        PrivateChatsView =  inflater.inflate(R.layout.fragment_chats, container, false);



        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");





        return PrivateChatsView;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions <Contacts> options  =
                new FirebaseRecyclerOptions.Builder <Contacts>()
                        .setQuery( ChatsRef , Contacts.class)
                        .build();

        FirebaseRecyclerAdapter <Contacts, ChatsViewHolder > adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
            {

                final String usersIDs = getRef(position).getKey();
                final String[] profileImage = {"default_image"};


                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                       if(dataSnapshot.exists())
                       {
                           if(dataSnapshot.hasChild("image"))
                           {
                               profileImage[0] = dataSnapshot.child("image").getValue().toString();
                               Picasso.get().load(profileImage[0]).into(holder.userImage);

                           }

                           //retrieve
                           final String profileName = dataSnapshot.child("name").getValue().toString();
                           final String profileStatus = dataSnapshot.child("status").getValue().toString();

                           holder.userName.setText(profileName);
                           holder.userStatus.setText("Last seen:" + "\n" + "Date " + "Time");


                           if( dataSnapshot.child("userState").hasChild("state"))
                           {
                                String state  = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date  = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time  = dataSnapshot.child("userState").child("time").getValue().toString();

                                if (state.equals("online"))
                                {
                                    holder.userStatus.setText("online");

                                }
                               else if (state.equals("offline"))
                               {
                                   holder.userStatus.setText("Last seen:" + date+ " " + time);

                               }
                           }
                           else
                           {
                               holder.userStatus.setText("offline");
                           }


                           holder.itemView.setOnClickListener(new View.OnClickListener()
                           {
                               @Override
                               public void onClick(View v)
                               {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", usersIDs);
                                    chatIntent.putExtra("visit_user_name", profileName);
                                    chatIntent.putExtra("visit_user_image", profileImage[0]);
                                    startActivity(chatIntent);
                               }
                           });
                       }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
               View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup, false);
                ChatsViewHolder holder = new ChatsViewHolder(view);

                return holder;
            }
        };

        chatsList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView userImage;
        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            userImage = itemView.findViewById(R.id.user_profile_image);

        }
    }
}
