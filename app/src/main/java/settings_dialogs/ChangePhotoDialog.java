package settings_dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;

import jhu.voiceit.R;
import jhu.voiceit.User;
import layout.SettingsFragment;

/**
 * Created by smsukardi on 4/23/16.
 */

public class ChangePhotoDialog{
    private AlertDialog.Builder builder;
    private View dialoglayout;
    private AlertDialog alertDialog;

    private Firebase mRef;

    public ChangePhotoDialog(final Activity a, final SettingsFragment myFrag, final User user){
        LayoutInflater inflater = a.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.activity_change_picture, null);

        mRef=new Firebase("https://voiceit.firebaseio.com/users/"+user.getUserId());

        //Retrieves elements on the change profile picture dialog box
        ImageView oldProfilePicture = (ImageView) dialoglayout.findViewById(R.id.prev_photo);
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
                alertDialog.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                a.startActivityForResult(intent, 1);
            }

        });

        newProfileGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                a.startActivityForResult(intent, 2);
            }
        });
    }
    
    

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void show() {
        alertDialog = builder.show();
    }
}

