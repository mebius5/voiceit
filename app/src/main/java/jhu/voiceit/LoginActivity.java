package jhu.voiceit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ImageButton login;
    private ImageButton regis;

    private String username;
    private String password;
    private EditText usrenter;
    private EditText passenter;

    private Firebase fireBase;
    private AuthData mAuthData;
    private String token;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireBase = new Firebase(getResources().getString(R.string.firebase_url));
        fireBase.addAuthStateListener(authlistener);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        token = myPrefs.getString("auth_token", "");
        if (!(token.equals(""))) {
            tokenAuthentication();
        }

        TextView title = (TextView) findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/title.otf");
        title.setTypeface(font, Typeface.BOLD);

        TextView loginlabel = (TextView) findViewById(R.id.llabel);
        TextView regislabel = (TextView) findViewById(R.id.rlabel);
        Typeface label = Typeface.createFromAsset(getAssets(), "fonts/Helvetica.otf");
        loginlabel.setTypeface(label);
        regislabel.setTypeface(label);

        usrenter = (EditText) findViewById(R.id.usrname);
        passenter = (EditText) findViewById(R.id.passwd);

        login = (ImageButton) findViewById(R.id.login);
        regis = (ImageButton) findViewById(R.id.register);

        login.setOnClickListener(loginlistener);
        regis.setOnClickListener(regislistener);
    }

    View.OnClickListener loginlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fireBase.authWithPassword(usrenter.getText().toString(), passenter.getText().toString(),
                    new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            mAuthData = authData;
                            SharedPreferences.Editor peditor = myPrefs.edit();
                            peditor.putString("auth_token", authData.getToken());
                            peditor.putString("UID", authData.getUid());
                            Log.i("LoginActivity","SuccessAuth: UID: "+authData.getUid());
                            peditor.commit();
                            movetoMain();
                        }
                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            makeToast(firebaseError.toString());
                        }
                    });
        }
    };

    View.OnClickListener regislistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*fireBase.createUser(usrenter.getText().toString(), passenter.getText().toString(),
                    new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    loginlistener.onClick(login);
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    makeToast(firebaseError.toString());
                }
            });
            */
            moveToRegister();
        }
    };

    Firebase.AuthStateListener authlistener = new Firebase.AuthStateListener() {
        @Override
        public void onAuthStateChanged(AuthData authData) {
            setAuthenticatedUser(authData);
        }
    };

    public void makeToast(String e) {
        Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* show a provider specific status text */
            String name = null;
            if (authData.getProvider().equals("password")) {
                name = authData.getUid();
            }
        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu();
    }

    public void tokenAuthentication() {
        fireBase.authWithCustomToken(token, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                mAuthData = authData;
                SharedPreferences.Editor peditor = myPrefs.edit();
                peditor.putString("auth_token", authData.getToken());
                peditor.putString("UID", authData.getUid());
                Log.i("LoginActivity","SuccessAuth: UID: "+authData.getUid());
                peditor.commit();
                movetoMain();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                makeToast(firebaseError.toString());
            }
        });
    }

    public void movetoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void moveToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
