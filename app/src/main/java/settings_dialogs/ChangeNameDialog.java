package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import jhu.voiceit.R;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */
public class ChangeNameDialog {
    private AlertDialog.Builder builder;
    private View dialoglayout;

    private Firebase mRef;

    public ChangeNameDialog (Activity a, final SettingsFragment myFrag, final String userId, final String username){
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_name, null);

        mRef=new Firebase("https://voiceit.firebaseio.com/users/"+userId);

        //setup dialogue box
        builder = new AlertDialog.Builder(a);
        builder.setView(dialoglayout);

        final TextView prevUsername = (TextView) dialoglayout.findViewById(R.id.previousUsername);
        final EditText newUsername  = (EditText) dialoglayout.findViewById(R.id.newUsername);

        prevUsername.setText(username);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Operation Canceled");
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newName = newUsername.getText().toString();
                if (newName.equals("")) {
                    myFrag.makeToast("You must input a new username! Username unchanged.");
                } else {
                    System.out.println("Name Changed");
                    Map<String, Object> changes = new HashMap<String, Object>();
                    changes.put("username", newName);
                    mRef.updateChildren(changes);

                    myFrag.makeToast("Your username was successfully changed.");
                }
            }
        });

    }
    public void show() {
        builder.show();
    }

}
