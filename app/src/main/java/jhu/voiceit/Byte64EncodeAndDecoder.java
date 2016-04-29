package jhu.voiceit;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by GradyXiao on 4/28/16.
 */
public class Byte64EncodeAndDecoder {
    public static String encode(String defaultFilePath){
        try {
            File file = new File(defaultFilePath);
            byte[] bytes = FileUtils.readFileToByteArray(file);

            String encoded = Base64.encodeToString(bytes, 0);

            //For Debugging
            //Log.i("~~~~~~~~ Encoded: ", encoded);
            return encoded;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void decode(String defaultOutputFileName, String encodedString){
        try
        {
            byte[] decoded = Base64.decode(encodedString, 0);

            //For Debugging
            //Log.i("~~~~~~~~ Decoded: ", Arrays.toString(decoded));

            File outputFile = new File(defaultOutputFileName);
            FileOutputStream os = new FileOutputStream(outputFile, false);
            os.write(decoded);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
