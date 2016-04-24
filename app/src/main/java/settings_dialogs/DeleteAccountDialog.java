package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import jhu.voiceit.LoginActivity;
import jhu.voiceit.R;
import jhu.voiceit.User;
import layout.BaseFragment;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */

public class DeleteAccountDialog{
    private AlertDialog.Builder builder;
    private View dialoglayout;

    private Firebase mRef;

    //TODO: Delete account from user node and user authentication

    public DeleteAccountDialog(Activity a, final SettingsFragment myFrag, final User user) {
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.delete_account, null);

        mRef = new Firebase("https://voiceit.firebaseio.com/users/" + user.getUserId());

        //setup dialogue box
        builder = new AlertDialog.Builder(a);
        builder.setView(dialoglayout);

        final EditText oldPassword = (EditText) dialoglayout.findViewById(R.id.currPassword);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Operation Canceled");
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String oldPass = oldPassword.getText().toString();
                mRef.removeUser(myFrag.getEmail(), oldPass, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        //TODO: Figure out how to log out after deleting account
                        logout(myFrag);
                        myFrag.makeToast("Account Deleted :'(");
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        myFrag.makeToast("Password entered incorrectly; Account not deleted.");
                    }
                });
                System.out.println("Name Changed");
            }
        });

    }

    public void logout(final BaseFragment myFrag){
        SharedPreferences myPrefs= PreferenceManager.getDefaultSharedPreferences(myFrag.getActivity());
        SharedPreferences.Editor peditor = myPrefs.edit();

        peditor.putString("auth_token","");
        peditor.putString("UID", "");
        peditor.putString("UserName","");
        peditor.putString("currentFragment", "0");
        peditor.commit();

        Intent intent = new Intent(myFrag.getActivity(), LoginActivity.class);
        myFrag.getActivity().startActivity(intent);
    }

    public void show() {
        builder.show();
    }
}

