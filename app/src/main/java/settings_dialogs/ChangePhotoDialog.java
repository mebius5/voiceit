package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import jhu.voiceit.R;
import jhu.voiceit.User;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */

public class ChangePhotoDialog{
    private AlertDialog.Builder builder;
    private View dialoglayout;

    private Firebase mRef;

    public ChangePhotoDialog(Activity a, final SettingsFragment myFrag, final User user){
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_picture, null);

        mRef=new Firebase("https://voiceit.firebaseio.com/users/"+user.getUserId());

        //Retrieves elements on the change profile picture dialog box
        final ImageView oldProfilePicture = (ImageView) dialoglayout.findViewById(R.id.prev_photo);
        final ImageButton newProfilePictureButton = (ImageButton) dialoglayout.findViewById(R.id.imageButton);

        oldProfilePicture.setImageResource(R.drawable.userdefault);
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

