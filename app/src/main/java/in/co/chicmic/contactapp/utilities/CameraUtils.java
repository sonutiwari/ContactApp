package in.co.chicmic.contactapp.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.co.chicmic.contactapp.R;

public class CameraUtils {

    //Get Uri Of captured Image
    public static Uri getOutputMediaFileUri(Context context) {
        File mediaStorageDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , context.getString(R.string.camera));
        //If File is not present create directory
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdir())
                Log.e(context.getString(R.string.create_directory)
                        , context.getString(R.string.directory_created) + mediaStorageDir);
        }

        String timeStamp = new SimpleDateFormat(context.getString(R.string.date_pattern),
                Locale.getDefault()).format(new Date());//Get Current timestamp
        //create image path with system mill and image format
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + context.getString(R.string.img_)
                + timeStamp + context.getString(R.string.jpg_extension));
        return Uri.fromFile(mediaFile);
    }
}