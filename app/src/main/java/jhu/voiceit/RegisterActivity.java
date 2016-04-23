package jhu.voiceit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ImageButton loginButton;
    private ImageButton registerButton;
    private EditText emailInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private Firebase fireBase;
    private AuthData mAuthData;
    private String token;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginButton = (ImageButton) findViewById(R.id.imageButtonLogin);
        registerButton = (ImageButton) findViewById(R.id.imageButtonRegister);
        emailInput = (EditText) findViewById(R.id.registerEmail);
        usernameInput = (EditText) findViewById(R.id.registerUsername);
        passwordInput = (EditText) findViewById(R.id.registerPassword);
        confirmPasswordInput = (EditText) findViewById(R.id.registerPasswordConfirm);

        setLoginButtonListener();
        setRegisterButtonListener();

        fireBase = new Firebase(getResources().getString(R.string.firebaseurl));
        fireBase.addAuthStateListener(authlistener);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }

    public void setLoginButtonListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLogin();
            }
        });
    }

    public void setRegisterButtonListener() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Run some validations
                if(checkFieldsCompletion()) {
                    makeToast(getResources().getString(R.string.missing_input_register));
                } else if(!passwordsAreEqual()) {
                    makeToast(getResources().getString(R.string.passwords_match_feedback));
                } else {
                    fireBase.createUser(emailInput.getText().toString(), passwordInput.getText().toString(),
                            new Firebase.ValueResultHandler<Map<String, Object>>() {
                                @Override
                                public void onSuccess(Map<String, Object> stringObjectMap) {
                                    loginAfterRegister();
                                }
                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    makeToast("createUser"+firebaseError.toString());
                                }
                            });
                }
            }
        });
    }

    Firebase.AuthStateListener authlistener = new Firebase.AuthStateListener() {
        @Override
        public void onAuthStateChanged(AuthData authData) {
            setAuthenticatedUser(authData);
        }
    };

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

    public void moveToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public boolean checkFieldsCompletion() {
        return emailInput.getText().toString().equals("") || usernameInput.getText().toString().equals("");
    }

    public boolean passwordsAreEqual() {
        return passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString());
    }

    public void makeToast(String e) {
        Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
    }

    public void loginAfterRegister() {
        fireBase.authWithPassword(emailInput.getText().toString(), passwordInput.getText().toString(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        mAuthData = authData;
                        SharedPreferences.Editor peditor = myPrefs.edit();
                        String userId = authData.getUid();
                        String userName = usernameInput.getText().toString();
                        //For storing in Shared Preferences
                        peditor.putString("auth_token", authData.getToken());
                        peditor.putString("UID", authData.getUid());
                        peditor.putString("UserName", userName);
                        Log.i("LoginActivity","SuccessAuth: UID: "+userId);
                        peditor.commit();

                        Firebase usersRef = fireBase.child("users").child(userId);
                        Map<String, Object> userInfo = new HashMap<String, Object>();
                        userInfo.put("username", userName);
                        userInfo.put("profilePicName", "userdefault.png");
                        usersRef.setValue(userInfo);

                        movetoMain();
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        makeToast("loginAfterRegister"+firebaseError.toString());
                    }
                });
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
}
