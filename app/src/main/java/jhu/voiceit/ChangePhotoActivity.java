package jhu.voiceit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChangePhotoActivity extends AppCompatActivity {

    ImageView profilePicture;
    ImageButton newProfileCamera;
    ImageButton newProfileGallery;

    Button saveButton;
    Button cancelButton;

    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_picture);

        final Intent intentFromMain = getIntent();
        final String userId = intentFromMain.getStringExtra("userId");
        Log.i("ChangePhotoActivity:","userId from intent: "+userId);

        mRef=new Firebase("https://voiceit.firebaseio.com/users/"+userId);

        //Retrieves elements on the change profile picture dialog box
        profilePicture = (ImageView) findViewById(R.id.prev_photo);
        newProfileCamera = (ImageButton) findViewById(R.id.photofromcamera);
        newProfileGallery = (ImageButton) findViewById(R.id.photofromgallery);
        saveButton = (Button) findViewById(R.id.button_save);
        cancelButton = (Button) findViewById(R.id.button_cancel);

        profilePicture.setImageResource(R.drawable.userdefault);

        newProfileCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            }
        });

        newProfileGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePhotoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePhotoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString()+"/temp.jpg");
                Log.i("Camera temp path:",f.getPath().toString());
                try {

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    OutputStream outFile = new FileOutputStream(f.getPath().toString(),false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    profilePicture.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.i("Gallery path:", picturePath);
                profilePicture.setImageBitmap(thumbnail);
            }
        }
    }
}
