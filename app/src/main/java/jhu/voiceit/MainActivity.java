package jhu.voiceit;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

import jhu.voiceit.layout.HomeFeedFragment;
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        Context context = getApplicationContext();  // app level storage
        myPrefs= PreferenceManager.getDefaultSharedPreferences(context);
        peditor = myPrefs.edit();

        if(myPrefs.getString("auth_token", "")==""){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }  else{
            //TODO: Load user information such as userName from Firebase
        }

        //Deal with navigation to Search Fragment
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_search_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFragment = SearchFragment.newInstance();
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
                    baseFragment = HomeFeedFragment.newInstance(1);
                }else if (position == 1){
                    baseFragment = RecordFragment.newInstance();
                }else if (position == 2){
                    baseFragment = NotificationsFragment.newInstance();
                }else if (position == 3){
                    baseFragment = ProfileFragment.newInstance();
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
        Log.i("MainActivity", "onPause on fragment: "+baseFragment.getFragmentName());

        super.onPause();
        peditor.putString(CURRENTFRAGMENT, baseFragment.getFragmentName());
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
            baseFragment = SettingsFragment.newInstance();
            inflateAndCommitBaseFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateFragment() {
        String currentFragment = myPrefs.getString(CURRENTFRAGMENT,"0");
        if(currentFragment.equals("0")){
            baseFragment = HomeFeedFragment.newInstance(1);
        }else if(currentFragment.equals(HomeFeedFragment.FRAGMENTNAME)){
            baseFragment = HomeFeedFragment.newInstance(1);
        } else if(currentFragment.equals(ProfileFragment.FRAGMENTNAME)){
            baseFragment = ProfileFragment.newInstance();
        } else if(currentFragment.equals(RecordFragment.FRAGMENTNAME)){
            baseFragment = RecordFragment.newInstance();
        } else if(currentFragment.equals(NotificationsFragment.FRAGMENTNAME)){
            baseFragment = NotificationsFragment.newInstance();
        } else if(currentFragment.equals(SettingsFragment.FRAGMENTNAME)){
            baseFragment = SettingsFragment.newInstance();
        } else if(currentFragment.equals(SearchFragment.FRAGMENTNAME)){
            baseFragment = SearchFragment.newInstance();
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
