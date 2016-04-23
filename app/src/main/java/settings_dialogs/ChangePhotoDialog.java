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

public class ChangePhotoDialog{
    private AlertDialog.Builder builder;
    private View dialoglayout;

    public ChangePhotoDialog(Activity a, final SettingsFragment myFrag){
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_picture, null);

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

