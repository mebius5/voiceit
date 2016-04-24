package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import jhu.voiceit.R;
import jhu.voiceit.User;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */
public class ChangeNameDialog {
    private AlertDialog.Builder builder;
    private View dialoglayout;

    private Firebase mRef;

    public ChangeNameDialog (Activity a, final SettingsFragment myFrag, final User user){
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_name, null);

        mRef=new Firebase("https://voiceit.firebaseio.com/users/"+user.getUserId());

        //setup dialogue box
        builder = new AlertDialog.Builder(a);
        builder.setView(dialoglayout);

        final TextView prevUsername = (TextView) dialoglayout.findViewById(R.id.previousUsername);
        final EditText newUsername  = (EditText) dialoglayout.findViewById(R.id.newUsername);

        prevUsername.setText(user.getUsername());

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

                    //Change name of user object
                    user.setUsername(newName);

                    //Push changes to firebase
                    Map<String, Object> changes = new HashMap<String, Object>();
                    changes.put("username", newName);
                    mRef.updateChildren(changes);

                    //Push changes to SharedPreferences
                    SharedPreferences myPrefs= PreferenceManager.getDefaultSharedPreferences(myFrag.getActivity());
                    SharedPreferences.Editor peditor = myPrefs.edit();
                    peditor.putString("UserName", newName);
                    myFrag.makeToast("Your username was successfully changed.");
                    myFrag.updateName();
                }
            }
        });

    }
    public void show() {
        builder.show();
    }

}
