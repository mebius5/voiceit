package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jhu.voiceit.R;
import jhu.voiceit.User;

/**
 * Created by sjp on 2016-04-23.
 */
public class PostSettingDialogue {

    private AlertDialog.Builder builder;
    private View dialoglayout;

    private Firebase postRef;

    private ListView menu;

    public PostSettingDialogue(Activity a, final BaseFragment myFrag, final User user, final Firebase postRef) {
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.post_setting, null);

        this.postRef = postRef;

        //setup dialogue box
        builder = new AlertDialog.Builder(a);
        builder.setView(dialoglayout);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.setNumPosts(user.getNumPosts() - 1);
                Firebase userRef = new Firebase("https://voiceit.firebaseio.com").child("users").child(user.getUserId());
                Map<String, Object> change = new HashMap<String, Object>();
                change.put("numPosts", user.getNumPosts());
                userRef.updateChildren(change);
                postRef.setValue(null);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public void show() {
        builder.show();
    }
}
