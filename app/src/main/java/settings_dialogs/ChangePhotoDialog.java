package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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
        final ImageButton newProfileCamera = (ImageButton) dialoglayout.findViewById(R.id.photofromcamera);
        final ImageButton newProfileGallery = (ImageButton) dialoglayout.findViewById(R.id.photofromgallery);

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

        newProfileCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(myFrag.getActivity().getPackageManager()) != null) {
                    myFrag.getActivity().startActivityForResult(takePictureIntent, 1);
                }

            }
            protected void OnActivityResult (int requestCode, int resultCode, Intent data) {
                if (requestCode == 1) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                    String imagefile = encodeToBase64(thumbnail, Bitmap.CompressFormat.PNG, 100);

                    //Push changes to firebase
                    Map<String, Object> changes = new HashMap<String, Object>();
                    changes.put("profilePicName", imagefile);
                    mRef.updateChildren(changes);
                    oldProfilePicture.setImageBitmap(thumbnail);
                }
            }
        });

        newProfileGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                myFrag.getActivity().startActivityForResult(i, 1);
            }

            protected void OnActivityResult (int requestCode, int resultCode, Intent data) {
                if (requestCode == 1) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                    String imagefile = encodeToBase64(thumbnail, Bitmap.CompressFormat.PNG, 100);

                    //Push changes to firebase
                    Map<String, Object> changes = new HashMap<String, Object>();
                    changes.put("profilePicName", imagefile);
                    mRef.updateChildren(changes);
                    oldProfilePicture.setImageBitmap(thumbnail);
                }
            }
        });

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void show() {
        builder.show();
    }
}

