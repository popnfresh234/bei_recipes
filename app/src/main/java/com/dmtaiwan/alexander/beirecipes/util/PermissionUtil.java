package com.dmtaiwan.alexander.beirecipes.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dmtaiwan.alexander.beirecipes.R;

/**
 * Created by Alexander on 10/26/2016.
 */

public class PermissionUtil {

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int REQUEST_STORAGE = 0;

    public static boolean verifyPermissions(int[] grantResults) {
        //At least one result must be checked
        if (grantResults.length < 1) {
            return false;
        }

        //Verify that each permission was granted, else return false;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestExternalStoragePermissions(final AppCompatActivity activity, View view) {
        QuickLog.i("Requesting Storage");
        //Begin permission request
        //This happens if request has NOT been granted and need to ask again
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Provide rationale to user for permission request
            Snackbar.make(view, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permission_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_STORAGE);
                        }
                    })
                    .show();
        }else{
            //Contact permissions not granted, ask directly, this happens by default
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_STORAGE);
        }
    }
}
