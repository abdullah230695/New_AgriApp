package com.shivaconsulting.agriapp.chat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.shivaconsulting.agriapp.R;
import com.shivaconsulting.agriapp.fragment.ChatFragment;
import com.shivaconsulting.agriapp.fragment.UserListInRoomFragment;


public class ChatActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ChatFragment chatFragment;
    private UserListInRoomFragment userListInRoomFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            String toUid = getIntent().getStringExtra("toUid");
            final String roomID = getIntent().getStringExtra("roomID");
            String roomTitle = getIntent().getStringExtra("roomTitle");
            if (roomTitle != null) {
                actionBar.setTitle(roomTitle);
            }

            // left drawer
      /*      drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            findViewById(R.id.rightMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                    } else {
                        if (userListInRoomFragment == null) {
                            userListInRoomFragment = UserListInRoomFragment.getInstance(roomID, chatFragment.getUserList());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.drawerFragment, userListInRoomFragment)
                                    .commit();
                        }
                        drawerLayout.openDrawer(Gravity.RIGHT);
                    }
                    }catch (Exception e){}
                }
            });*/
            try{
            // chatting area
            chatFragment = ChatFragment.getInstance(toUid, roomID);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, chatFragment)
                    .commit();
            }catch (Exception e){}
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onBackPressed () {

        }

}
