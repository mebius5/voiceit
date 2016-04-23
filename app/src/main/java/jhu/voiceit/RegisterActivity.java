package jhu.voiceit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class RegisterActivity extends AppCompatActivity {
    private ImageButton loginButton;
    private ImageButton registerButton;
    private EditText emailInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

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
            }
        });
    }

    public void moveToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public boolean checkFieldsCompletion() {
        return true;
    }
}
