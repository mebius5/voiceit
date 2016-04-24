package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import jhu.voiceit.R;
import jhu.voiceit.User;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */
public class ChangePasswordDialog{
    private AlertDialog.Builder builder;
    private View dialoglayout;

    private Firebase mRef;

    //TODO: Change password on firebase
    public ChangePasswordDialog(Activity a, final SettingsFragment myFrag, final User user) {
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_password, null);

        mRef=new Firebase("https://voiceit.firebaseio.com/users/"+user.getUserId());

        //setup dialogue box
        builder = new AlertDialog.Builder(a);
        builder.setView(dialoglayout);

        final EditText prevPassword = (EditText) dialoglayout.findViewById(R.id.prevPass);
        final EditText newPassword  = (EditText) dialoglayout.findViewById(R.id.newPass);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Operation Canceled");
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String oldPass = prevPassword.getText().toString();
                String newPass = newPassword.getText().toString();

                System.out.println(myFrag.getEmail());


                mRef.changePassword(myFrag.getEmail(), oldPass, newPass, new Firebase.ResultHandler() {
                    @Override

                    public void onSuccess() {
                        myFrag.makeToast("Your password has been successfully changed!");
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        myFrag.makeToast("An error has occurred. Your password has not been changed.");
                    }
                });
            }
        });
    }

    public void show() {
        builder.show();
    }
}