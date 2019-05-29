package com.siang.wei.mybookmark.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class PermissionUtil {

    public static final int PERMISSION_REQUEST_CODE_INIT = 1;
    public static final int PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE = 2;
    public static final int PERMISSION_REQUEST_CODE_SCAN = 3;
    public static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 4;



    /**
     * 確認是否有指定的權限
     */
    public static boolean needGrantRuntimePermission(final Activity activity, final String permission, final int requestCode/*,
                                              View snackbarParentView, int expanationResId*/){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = {permission};

                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 確認是否有指定的權限
     */
    public static boolean needGrantRuntimePermission(Activity activity, String[] permissions, final int requestCode){
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        //Android為6.0以上的版本，根據官方提供的權限處理方式，顯示訊息要求使用者允許

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permissions[i]);
            }
        }

        // 如果有權限沒有取得，要求權限
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
            return true;
        } else {
            return false;
        }

    }

    /**
     * 確認是否有指定的權限(要求權限的對象是fragment)
     */
    public static boolean needGrantRuntimePermission(Fragment fragment, String[] permissions, final int requestCode){

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(fragment.getActivity(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permissions[i]);
            }
        }

        // 如果有權限沒有取得，要求權限
        if (permissionsToRequest.size() > 0) {
            fragment.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判斷是否所有要求的權限都有獲得
     * @param grantResults 從Activity的onRequestPermissionsResult取得
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
