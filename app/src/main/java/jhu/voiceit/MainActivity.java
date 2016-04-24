package jhu.voiceit;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import layout.HomeFeedFragment;
import layout.BaseFragment;
import layout.NotificationsFragment;
import layout.ProfileFragment;
import layout.RecordFragment;
import layout.SearchFragment;
import layout.SettingsFragment;

public class MainActivity extends AppCompatActivity{

    /*
    ####################### Data Variables #####################
     */
    private final String CURRENTFRAGMENT = "currentFragment";
    private User user;

    /*
    ####################### Storage Variables #####################
     */
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor peditor;
    private Firebase mRef;

    /*
    ####################### View Elements #####################
     */
    private BaseFragment baseFragment;
    private static TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();  // app level storage
        myPrefs= PreferenceManager.getDefaultSharedPreferences(context);
        peditor = myPrefs.edit();

        if(myPrefs.getString("auth_token", "").equals("")){
            Log.i("it got to here", "hello");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finishActivity(0);
        }  else{
            //Valid user, so must have username
            String userId = myPrefs.getString("UID","Default");
            String userName = myPrefs.getString("UserName", "Default");
            String profilePic = myPrefs.getString("ProfilePic","default.png");
            long numPosts = myPrefs.getLong("numPosts",0);
            String email = myPrefs.getString("Email","Default");
            Log.i("MainActivity","UID: "+userId);
            Log.i("MainActivity","UserName: "+userName);
            Log.i("MainActivity","ProfilePic: "+profilePic);
            Log.i("MainActivity","NumPosts:"+numPosts);
            Log.i("MainActivity","Email: "+email);

            this.user = new User(userId, userName, profilePic, email, numPosts);
        }
        Log.i("im here too", "hi");
        mRef = new Firebase(getResources().getString(R.string.firebaseurl));

        //Deal with navigation to Search Fragment
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_search_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFragment = SearchFragment.newInstance(user);
                inflateAndCommitBaseFragment();
            }
        });

        Firebase userRef = mRef.child("users").child(user.getUserId());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = (String) dataSnapshot.child("username").getValue();
                String email = (String) dataSnapshot.child("email").getValue();
                long numPosts;
                if(dataSnapshot.child("numPosts").getValue() == null) {
                    numPosts = 0;
                } else {
                    numPosts = (long) dataSnapshot.child("numPosts").getValue();
                }
                user.setUsername(userName);
                user.setNumPosts(numPosts);
                user.setEmail(email);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                makeToast(firebaseError.toString());
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.i("MainActivity","Selected Tab Position: "+position);
                if(position == 0) {
                    baseFragment = HomeFeedFragment.newInstance(user);
                }else if (position == 1){
                    baseFragment = RecordFragment.newInstance(user);
                }else if (position == 2){
                    baseFragment = NotificationsFragment.newInstance(user);
                }else if (position == 3){
                    baseFragment = ProfileFragment.newInstance(user);
                }

                //Save Tab Position
                peditor.putInt("TabPosition", position);
                peditor.commit();

                inflateAndCommitBaseFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("MainActivity","Unselected Tab Position: "+tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("MainActivity","Reselected Tab Position: "+tab.getPosition());
                if(baseFragment!=null &&
                        (baseFragment.getFragmentName().equals("SettingsFragment")||
                        baseFragment.getFragmentName().equals("SearchFragment"))) {
                    int position = tab.getPosition();
                    Log.i("MainActivity","Reselected Tab Position: "+position);
                    if(position == 0) {
                        baseFragment = HomeFeedFragment.newInstance(user);
                    }else if (position == 1){
                        baseFragment = RecordFragment.newInstance(user);
                    }else if (position == 2){
                        baseFragment = NotificationsFragment.newInstance(user);
                    }else if (position == 3){
                        baseFragment = ProfileFragment.newInstance(user);
                    }
                    //Save Tab Position
                    peditor.putInt("TabPosition", position);
                    peditor.commit();

                    inflateAndCommitBaseFragment();
                }
            }
        });

        int tabPosition = myPrefs.getInt("TabPosition", 0);
        tabLayout.getTabAt(tabPosition).select();

        initiateFragment();
    }

    @Override
    public void onPause(){

        super.onPause();
        if(baseFragment==null){
            peditor.putString(CURRENTFRAGMENT, "0");
        }else {
            Log.i("MainActivity", "onPause on fragment: "+baseFragment.getFragmentName());
            peditor.putString(CURRENTFRAGMENT, baseFragment.getFragmentName());
        }
        peditor.putInt("TabPosition",tabLayout.getSelectedTabPosition());
        peditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            baseFragment = SettingsFragment.newInstance(user);
            inflateAndCommitBaseFragment();
            return true;
        } else if (id == R.id.action_logout){
            //Delete token;
            baseFragment=null;
            peditor.putString("auth_token","");
            peditor.putString("UID", "");
            peditor.putString(CURRENTFRAGMENT, "0");
            peditor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateFragment() {
        String currentFragment = myPrefs.getString(CURRENTFRAGMENT,"0");
        Log.i("MainActivity", "Attempting to inflate: "+currentFragment);
        if(currentFragment.equals("0")){
            baseFragment = HomeFeedFragment.newInstance(user);
        }else if(currentFragment.equals(HomeFeedFragment.FRAGMENTNAME)){
            baseFragment = HomeFeedFragment.newInstance(user);
        } else if(currentFragment.equals(ProfileFragment.FRAGMENTNAME)){
            baseFragment = ProfileFragment.newInstance(user);
        } else if(currentFragment.equals(RecordFragment.FRAGMENTNAME)){
            baseFragment = RecordFragment.newInstance(user);
        } else if(currentFragment.equals(NotificationsFragment.FRAGMENTNAME)){
            baseFragment = NotificationsFragment.newInstance(user);
        } else if(currentFragment.equals(SettingsFragment.FRAGMENTNAME)){
            baseFragment = SettingsFragment.newInstance(user);
        } else if(currentFragment.equals(SearchFragment.FRAGMENTNAME)){
            baseFragment = SearchFragment.newInstance(user);
        } else{
            Log.e("MainActivity", "InvalidFragmentNameFound: "+currentFragment);
        }

        Firebase userRef = mRef.child("users").child(user.getUserId());
        Map<String, Object> changes = new HashMap<String, Object>();
        userRef.updateChildren(changes);

        inflateAndCommitBaseFragment();
    }

    private void inflateAndCommitBaseFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, baseFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        peditor.putString(CURRENTFRAGMENT, baseFragment.getFragmentName());
        peditor.commit();
    }

    private void makeToast(String e) {
        Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
    }

    public static void setTabLayout(int n) {
        tabLayout.getTabAt(n).select();
    }
}
