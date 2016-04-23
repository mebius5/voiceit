package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import jhu.voiceit.R;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */

public class DeleteAccountDialog{
    private AlertDialog.Builder builder;
    private View dialoglayout;

    public DeleteAccountDialog(Activity a, final SettingsFragment myFrag){
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.delete_account, null);

        //setup dialogue box
        builder = new AlertDialog.Builder(a);
        builder.setView(dialoglayout);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Operation Canceled");
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Name Changed");
            }
        });

    }
    public void show() {
        builder.show();
    }
}

