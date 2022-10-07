package com.entrolabs.apsacs.Apikey;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kk on 4/10/2018.
 */


public class AuthKey {
    static SessionManager sessionManager;
    static VolleyCallback global_callback;
    static String global_url= "";
    static Map<String, String> global_params;
    static Context global_context;
    static String global_loader;
    static JSONObject global_json;

    public static void VolleyCallBack(final VolleyCallback callback, final String Volley_URL, Map<String, String> params, final Context context, String loader_index) {

        sessionManager = new SessionManager(context);
        if(loader_index.equalsIgnoreCase("show")){
            Helper.showProgressDialog(context);
        }
        global_callback =  callback;
        global_url = Volley_URL;
        global_params = params;
        global_context =  context;
        global_loader =  loader_index;

        DataChannel();
    }


    private static void DataChannel() {
        try{
            RequestQueue queue = Volley.newRequestQueue(global_context);

            JsonObjectRequest req_obj = new JsonObjectRequest(Request.Method.POST,
                    global_url, new JSONObject(global_params), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Helper.dismissProgressDialog();
                    Helper.l("response : " + response.toString());
                    try {

                        String apiresult = response.getString("result");

                        if (apiresult.equalsIgnoreCase("success")) {
                            global_callback.onSuccess(response);
                        } else {
                            if (response.getString("result").equalsIgnoreCase("failed") &&
                                    ((response.getString("error").equalsIgnoreCase("Invalid or token expired"))
                                            || (response.getString("error").equalsIgnoreCase("No key generated for user")))) {
                                RefreshToken();
                            }
                            else{
                                global_callback.onFailure(response);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        global_callback.onException(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Helper.dismissProgressDialog();
                    global_callback.onError(error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String versionName = "";
                    try {
                        PackageInfo pinfo = global_context.getPackageManager().getPackageInfo(global_context.getPackageName(), 0);
                        versionName = pinfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ver", versionName);
                    params.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));
                    params.put("Auth-Key", sessionManager.getStrVal(Helper. Apsacs_Token));
                    params.put("App-Id", global_context.getPackageName());
                    params.put("Androidid", sessionManager.getStrVal("android_id"));
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            int socketTimeout = 0;//1 min - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req_obj.setRetryPolicy(policy);
            req_obj.setShouldCache(false);
            queue.getCache().clear();
            // Adding request to request queue
            queue.add(req_obj);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void RefreshToken() {
        try{

            Map<String, String> localparams = new LinkedHashMap<String, String>();
            localparams.put("getToken", "true");
            localparams.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));

            RequestQueue queue = Volley.newRequestQueue(global_context);

            JsonObjectRequest req_obj = new JsonObjectRequest(Request.Method.POST,
                    global_url, new JSONObject(localparams), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Helper.dismissProgressDialog();
                    Helper.l("response : " + response.toString());
                    try {

                        String apiresult = response.getString("result");

                        if (apiresult.equalsIgnoreCase("success")) {
                            sessionManager.storeVal(Helper. Apsacs_Token, response.getString("token"));
                            DataChannel();
                        } else {
                            if(response.getString("error").equalsIgnoreCase("logout")){
                                global_callback.onLogout("logout");
                            }

                            else{
                                global_callback.onFailure(response);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        global_callback.onException(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Helper.dismissProgressDialog();
                    global_callback.onError(error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String versionName = "";
                    try {
                        PackageInfo pinfo = global_context.getPackageManager().getPackageInfo(global_context.getPackageName(), 0);
                        versionName = pinfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ver", versionName);
                    params.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));
                    params.put("Auth-Key",sessionManager.getStrVal(Helper. Apsacs_Token));
                    params.put("App-Id", global_context.getPackageName());
                    params.put("Androidid", sessionManager.getStrVal("android_id"));
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            int socketTimeout = 0;//1 min - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req_obj.setRetryPolicy(policy);
            req_obj.setShouldCache(false);
            queue.getCache().clear();
            // Adding request to request queue
            queue.add(req_obj);


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}

