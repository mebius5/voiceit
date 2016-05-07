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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ChangePhotoActivity extends AppCompatActivity {

    public static final String DEFAULT_IMAGE_PATH =
            Environment.getExternalStorageDirectory().toString()+"/temp.jpg";

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

        mRef=new Firebase(getString(R.string.firebaseurl));

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
                final String imageEncodedString = Byte64EncodeAndDecoder.encode(DEFAULT_IMAGE_PATH);

                Firebase userRef = mRef.child("users").child(userId);
                Map<String, Object> change = new HashMap<>();
                change.put("profilePicName",imageEncodedString);
                userRef.updateChildren(change);

                final Firebase postsRef = new Firebase("https://voiceit.firebaseio.com/posts");
                final Query postQuery = postsRef.orderByChild("owner/userId").startAt(userId).endAt(userId);
                postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.i("ChangePhoto", "Number of Posts for this user: "+dataSnapshot.getChildrenCount());

                        for (DataSnapshot i: dataSnapshot.getChildren()){
                            Log.i("ChangePhoto", "getPostRefPath: "+i.getRef().getPath());
                            Map<String, Object> change = new HashMap<>();
                            change.put("owner/profilePicName",imageEncodedString);
                            i.getRef().updateChildren(change);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                //Go back to Main Activity
                Intent intent = new Intent(ChangePhotoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Go back to Main Activity
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
                File f = new File(DEFAULT_IMAGE_PATH);
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize=5;

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);

                try {
                    Log.i("ChangePhotoActivity","compressing camera photo");
                    OutputStream outFile = new FileOutputStream(DEFAULT_IMAGE_PATH,false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outFile);
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

                Log.i("Gallery path:", picturePath);

                File f = new File(DEFAULT_IMAGE_PATH);

                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize=5;
                bitmap = BitmapFactory.decodeFile(picturePath, bitmapOptions);

                try {
                    Log.i("ChangePhotoActivity","compressing gallery photo");
                    OutputStream outFile = new FileOutputStream(f.getPath().toString(), false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outFile);
                    outFile.flush();
                    outFile.close();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    profilePicture.setImageBitmap(bitmap);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
