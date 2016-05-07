package jhu.voiceit;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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

        //Disables landscape mode
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginButton = (ImageButton) findViewById(R.id.imageButtonLogin);
        registerButton = (ImageButton) findViewById(R.id.imageButtonRegister);
        emailInput = (EditText) findViewById(R.id.registerEmail);
        emailInput.setHintTextColor(getResources().getColor(R.color.white));
        usernameInput = (EditText) findViewById(R.id.registerUsername);
        usernameInput.setHintTextColor(getResources().getColor(R.color.white));
        passwordInput = (EditText) findViewById(R.id.registerPassword);
        passwordInput.setHintTextColor(getResources().getColor(R.color.white));
        confirmPasswordInput = (EditText) findViewById(R.id.registerPasswordConfirm);
        confirmPasswordInput.setHintTextColor(getResources().getColor(R.color.white));

        confirmPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    submitRegister();
                    return true;
                }
                return false;
            }
        });

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setRegisterButtonListener() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Run some validations
                if (checkFieldsCompletion()) {
                    makeToast(getResources().getString(R.string.missing_input_register));
                } else if (!passwordsAreEqual()) {
                    makeToast(getResources().getString(R.string.passwords_match_feedback));
                } else {
                    submitRegister();
                }
            }
        });
    }

    private void submitRegister() {
        fireBase.createUser(emailInput.getText().toString(), passwordInput.getText().toString(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {
                        loginAfterRegister();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        makeToast("createUser" + firebaseError.toString());
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
                        peditor.putString("Email", emailInput.getText().toString());
                        peditor.putString("UserName", userName);
                        Log.i("LoginActivity", "SuccessAuth: UID: " + userId);
                        peditor.commit();

                        Firebase usersRef = fireBase.child("users").child(userId);
                        usersRef.setValue(new User(userId, userName, "userdefault.png", emailInput.getText().toString()));

                        movetoMain();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        makeToast("loginAfterRegister"+firebaseError.toString());
                    }
                });
    }



    public void movetoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
