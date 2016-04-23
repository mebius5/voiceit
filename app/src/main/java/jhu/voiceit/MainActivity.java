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

import com.firebase.client.Firebase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();  // app level storage
        myPrefs= PreferenceManager.getDefaultSharedPreferences(context);
        peditor = myPrefs.edit();

        if(myPrefs.getString("auth_token", "").equals("")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }  else{
            //Valid user, so must have username
            String userId = myPrefs.getString("UID","Default");
            String userName = myPrefs.getString("UserName", "Default");
            String profilePic = myPrefs.getString("ProfilePic","Default");
            Log.i("MainActivity","UID: "+userId);
            Log.i("MainActivity","UserName: "+userName);
            Log.i("MainActivity","ProfilePic: "+profilePic);

            this.user = new User(userId, userName, profilePic);
        }

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
                inflateAndCommitBaseFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("MainActivity","Unselected Tab Position: "+tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("MainActivity","Reselected Tab Position: "+tab.getPosition());
            }
        });

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
        String currentFragment = myPrefs.getString(CURRENTFRAGMENT,"");
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

}
