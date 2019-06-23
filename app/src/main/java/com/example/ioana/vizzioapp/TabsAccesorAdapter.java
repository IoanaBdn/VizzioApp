package com.example.ioana.vizzioapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsAccesorAdapter extends FragmentStatePagerAdapter {

    public TabsAccesorAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) //i pozitia fragmentului, gen tab un fel de array
    {
        switch (i)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
           /* case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
                */
            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 2:
                RequestFragment requestsFragment = new RequestFragment();
                return requestsFragment;
             default:
                 return null;
        }

    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //aici se populeaza tab layout cu numele taburilor
        switch (position)
        {
            case 0:
                return "Chats";

           /* case 1:
                return "Groups";
                */
            case 1:
                return "Contacts";
            case 2:
                return "Requests";
            default:
                return null;
        }
    }
}
