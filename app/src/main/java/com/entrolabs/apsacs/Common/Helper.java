package com.entrolabs.apsacs.Common;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;


import com.entrolabs.apsacs.R;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;


public class Helper {
    public static final String DeliveryAddress = "DeliveryAddress";
    public static Dialog progress;
    public static String  Apsacs_Token = " Apsacs_Token";

    public static String  Apsacs_Role = " Apsacs_Role";
    public static String  Apsacs_Username = " Apsacs_Username";
    public static String  Apsacs_Name = " Apsacs_Name";
    public static String  Apsacs_Rolename = " Apsacs_Rolename";
    public static String  Apsacs_Mobile = " Apsacs_Mobile";
    public static String  Apsacs_soch_uid = " Apsacs_soch_uid";

    public static String  Apsacs_SubUnitId= " Apsacs_SubUnit";
    public static String  Apsacs_PHC = " Apsacs_PHC";
    public static String  Apsacs_Village = " Apsacs_Village";
    public static String  Apsacs_Volunteer = " Apsacs_Volunteer";
    public static String  Apsacs_HouseHolds = " Apsacs_HouseHolds";
    public static String  Apsacs_District = " Apsacs_District";
    public static String  Apsacs_District_uid = " Apsacs_District_uid";
    public static String  Apsacs_SubUnitName = " Apsacs_SubUnitName";



    public static String  Apsacs_status = " Apsacs_status";

    public static String ver = "1.0";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static String static_key = "85b653091942d928dc74c7184ecdfc8f";
    public static String[] perms = {Manifest.permission.INTERNET,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    public static void t(Context context, String display) {
        Toast.makeText(context, display, Toast.LENGTH_LONG).show();
    }


    public static void l(String logdisplay) {
         //Log.d("mosquito", logdisplay);
    }

    public static void showProgressDialog(Context ctx) {

        try {
            progress = new Dialog(ctx, R.style.AppTheme1);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setContentView(R.layout.progress_layout);
            //  progress.setCancelable(false);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
//                    prog_cancel_Flag = true;
                }
            });
            progress.show();
        } catch (Exception e) {
//            e.printStackTrace();
        }

    }

    public static String AppVersion(Context context){
        PackageInfo pinfo = null;
        String versionName = "";
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pinfo.versionName;
            Helper.l("versionName" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  versionName;
    }


    public static boolean isNetworkAvailable(Context context) {
        /*if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Helper.l("update_statut" + "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Helper.l("update_statut" + "" + e.getMessage());
                }
            }
        }
        Helper.l("update_statut" + "Network is unavailable : FALSE ");
        */return true;
    }

    public static String getTimeStamp() {
        String timeStamp = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String year = "" + cal.get(Calendar.YEAR);
        String month = "" + (cal.get(Calendar.MONTH) + 1);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = "" + cal.get(Calendar.DAY_OF_MONTH);
        if (day.length() == 1) {
            day = "0" + day;
        }
        String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String min = "" + cal.get(Calendar.MINUTE);
        if (min.length() == 1) {
            min = "0" + min;
        }
        String sec = "" + cal.get(Calendar.SECOND);
        if (sec.length() == 1) {
            sec = "0" + sec;
        }

        timeStamp = day + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec;

        return timeStamp;
    }

//
//    public static void showProgressDialog(Context ctx) {
//
//        try {
//            progress = new Dialog(ctx, R.style.AppTheme1);
//            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            progress.setContentView(R.layout.progress_layout);
//            //  progress.setCancelable(false);
//            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
////                    prog_cancel_Flag = true;
//                }
//            });
//            progress.show();
//        } catch (Exception e) {
////            e.printStackTrace();
//        }
//
//    }


    public static String getRandomString(final int sizeOfRandomString) {
        StringBuilder sb = new StringBuilder(sizeOfRandomString);

        try {
            // Create a secure random number generator using the SHA1PRNG algorithm
            SecureRandom srg = SecureRandom.getInstance("SHA1PRNG");

            // Get 10 random bytes
            int i = srg.nextInt(1000000);
//            s= String.valueOf(i);

            for (int j = 0; j < sizeOfRandomString; j++)
                sb.append(AB.charAt(srg.nextInt(AB.length())));


        } catch (NoSuchAlgorithmException e) {
        }
        return String.valueOf(sb);
    }

    public static String getFormattedDateStamp() {
        String timeStamp = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String year = "" + cal.get(Calendar.YEAR);
        String month = "" + (cal.get(Calendar.MONTH) + 1);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = "" + cal.get(Calendar.DAY_OF_MONTH);
        if (day.length() == 1) {
            day = "0" + day;
        }
        timeStamp = year + "-" + month + "-" + day;
        return timeStamp;
    }


    public static void dismissProgressDialog() {
        try {
            if (null != progress) {
                progress.dismiss();
                progress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

}
