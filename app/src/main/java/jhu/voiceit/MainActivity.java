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

import layout.BaseFragment;
import layout.ProfileFragment;
import layout.RecordFragment;
import layout.SearchFragment;
import layout.SettingsFragment;

public class MainActivity extends AppCompatActivity{

    private final String CURRENTFRAGMENT = "currentFragment";

    private SharedPreferences myPrefs;
    private SharedPreferences.Editor peditor;
    private BaseFragment baseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /***
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
         ****/

        Context context = getApplicationContext();  // app level storage
        myPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        peditor = myPrefs.edit();

        //Deal with navigation to Search Fragment
        toolbar.setNavigationIcon(R.drawable.ic_search_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFragment = SearchFragment.newInstance();
            }
        });


        initiateFragment();
    }

    @Override
    public void onPause(){
        Log.i("MainActivity", "onPause");

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateFragment() {
        if(myPrefs.getString(CURRENTFRAGMENT,"0").equals("0")){
            baseFragment = RecordFragment.newInstance(null,null);
        } else if(myPrefs.getString(CURRENTFRAGMENT,"0").equals(ProfileFragment.FRAGMENTNAME)){
            baseFragment = ProfileFragment.newInstance();
        } else if(myPrefs.getString(CURRENTFRAGMENT,"0").equals(RecordFragment.FRAGMENTNAME)){
            baseFragment = RecordFragment.newInstance(null,null);
        } else if(myPrefs.getString(CURRENTFRAGMENT,"0").equals(SettingsFragment.FRAGMENTNAME)){
            baseFragment = SettingsFragment.newInstance();
        } else if(myPrefs.getString(CURRENTFRAGMENT,"0").equals(SearchFragment.FRAGMENTNAME)){
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
