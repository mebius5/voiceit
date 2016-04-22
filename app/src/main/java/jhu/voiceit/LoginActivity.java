package jhu.voiceit;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView title = (TextView) findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/title.otf");
        title.setTypeface(font, Typeface.BOLD);

        TextView loginlabel = (TextView) findViewById(R.id.llabel);
        TextView regislabel = (TextView) findViewById(R.id.rlabel);
        Typeface label = Typeface.createFromAsset(getAssets(), "fonts/Helvetica.otf");
        loginlabel.setTypeface(label);
        regislabel.setTypeface(label);

        //ImageButton
    }



}
