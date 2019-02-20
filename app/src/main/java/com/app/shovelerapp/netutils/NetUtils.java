package com.app.shovelerapp.netutils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.app.shovelerapp.R;
import com.app.shovelerapp.ShovlerApplication;
import com.app.shovelerapp.callback.AcceptJobCallback;
import com.app.shovelerapp.callback.ApproveJobCallback;
import com.app.shovelerapp.callback.AvailableJobCallback;
import com.app.shovelerapp.callback.CallGetPriceCallback;
import com.app.shovelerapp.callback.CancelJobCallback;
import com.app.shovelerapp.callback.CancelJobsCallback;
import com.app.shovelerapp.callback.CheckPromoCodeCallback;
import com.app.shovelerapp.callback.FeedbackCallback;
import com.app.shovelerapp.callback.FinishJobCallback;
import com.app.shovelerapp.callback.ForgetPassword;
import com.app.shovelerapp.callback.GetAddressFromLatLongCallback;
import com.app.shovelerapp.callback.GetBusinessPriceCallback;
import com.app.shovelerapp.callback.GetCarDetailsCallBack;
import com.app.shovelerapp.callback.GetLatLongFromAddressCallback;
import com.app.shovelerapp.callback.GetPlacesCallback;
import com.app.shovelerapp.callback.GetProfileCallback;
import com.app.shovelerapp.callback.GetPromoCodeCallback;
import com.app.shovelerapp.callback.GetRequestorJobCallback;
import com.app.shovelerapp.callback.GetShovelerJobCallback;
import com.app.shovelerapp.callback.HoldCancelJobsCallback;
import com.app.shovelerapp.callback.LoginCallback;
import com.app.shovelerapp.callback.LogoutCallback;
import com.app.shovelerapp.callback.PostJobCallback;
import com.app.shovelerapp.callback.PromocodeCallback;
import com.app.shovelerapp.callback.RelunchCallback;
import com.app.shovelerapp.callback.SaveProfileCallback;
import com.app.shovelerapp.callback.SignUpCallback;
import com.app.shovelerapp.callback.UpdateLatLngCallback;
import com.app.shovelerapp.callback.callStaticAPI;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.app.shovelerapp.R.string.address;

/**
 * Created by supriya.n on 05-07-2016.
 */
public class NetUtils {
    private SharedPrefClass prefClass;

    // Call Sign Up API
    public static void CallSignUp(String email, String pass, String fname, String lname,
                                  final SignUpCallback callback, final Context context) {
        String url = Webservices.base_url + "userreg.php/signup/";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.SignUpCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.SignUpCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.SignUpCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("emailid", email);
        params.put("password", pass);
        params.put("utype", Constants.USER_TYPE);
        params.put("searchdist", "10");
        params.put("fbstatus", "0");
        params.put("deviceid", Constants.DEVICE_ID);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallSignUp");

    }


    // Call Login API
    public static void CallLogin(String email, String pass, String fbstatus,
                                 final LoginCallback callback, final Context context) {
        String url = Webservices.base_url + "login.php/index/";
        SharedPrefClass prefClass = new SharedPrefClass(context);
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.LoginCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.LoginCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.LoginCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("emailid", email);
        params.put("password", pass);
        params.put("fbstatus", fbstatus);
        params.put("utype", Constants.USER_TYPE);
        params.put("deviceid", Constants.DEVICE_ID);
        params.put("devicetype", "Android");

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallLogin");
    }

    public static void CallLogin(String email, String pass, String fbstatus, String fname, String lname,
                                 final LoginCallback callback, final Context context) {
        String url = Webservices.base_url + "login.php/index/";
        SharedPrefClass prefClass = new SharedPrefClass(context);
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.LoginCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.LoginCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.LoginCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("emailid", email);
        params.put("password", pass);
        params.put("fbstatus", fbstatus);
        params.put("utype", Constants.USER_TYPE);
        params.put("deviceid", Constants.DEVICE_ID);
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("devicetype", "Android");

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallLogin");
    }
    public static void CallUpdateLatLng(String uid, String lat, String lng,
                                        final UpdateLatLngCallback callback, final Context context) {
        String url = Webservices.update_latlng;
        SharedPrefClass prefClass = new SharedPrefClass(context);
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.UpdateLatLngCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.UpdateLatLngCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.UpdateLatLngCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("slat", lat);
        params.put("slng", lng);
        params.put("utype", Constants.USER_TYPE);
        params.put("deviceid", Constants.DEVICE_ID);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallLogin");
    }

    public static void CallNewUpdateLatLng(String uid, String lat, String lng, String address,
                                           final UpdateLatLngCallback callback, final Context context) {
        String url = Webservices.update_latlng;
        SharedPrefClass prefClass = new SharedPrefClass(context);
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.UpdateLatLngCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.UpdateLatLngCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.UpdateLatLngCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("slat", lat);
        params.put("slng", lng);
        params.put("address", address);
        params.put("utype", Constants.USER_TYPE);
        params.put("deviceid", Constants.DEVICE_ID);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallLogin");
    }

    // Call Forget Password
    public static void CallForget(String email,
                                  final ForgetPassword callback, final Context context) {
        String url = Webservices.base_url + "login.php/forgetPassword/";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.ForgetPasswordSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.ForgetPasswordError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.ForgetPasswordError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("emailid", email);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallForget");
    }

    // Call Get Profile
    public static void CallGetProfile(String uid,
                                      final GetProfileCallback callback, final Context context) {
        String url = Webservices.base_url + "userreg.php/userprofile/";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetProfileCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetProfileCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetProfileCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetProfile");
    }

    //get car details
    public static void CallGetCarDetails(final GetCarDetailsCallBack callback, final Context context) {
        String url = Webservices.base_url + "jobmgt.php/cardddetails";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetCarDetailsCallBackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetCarDetailsCallBackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetCarDetailsCallBackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();


        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetProfile");
    }

    //get car details
    public static void CallGetPrice(final CallGetPriceCallback callback, final Context context) {
        String url = Webservices.Get_Job_Type_Prices;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.CallGetPriceCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.CallGetPriceCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.CallGetPriceCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("zipcode", Constants.Postal_code);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetProfile");
    }

    // Call Get Profile
    public static void CallSaveProfile(String uid, String name, String lname, String address, String zipcode, String mobno, String emailid, String password, String utype, String searchdist, String fbstatus, String api_key,
                                       final SaveProfileCallback callback, final Context context) {
        String url = Webservices.base_url + "userreg.php/profile/";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);


                callback.SaveProfileCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.SaveProfileCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.SaveProfileCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("fname", name);
        params.put("lname", lname);
        params.put("address", address);
        params.put("zipcode", zipcode);
        //params.put("mobno", mobno);
        params.put("emailid", emailid);
        params.put("password", password);
        params.put("utype", utype);
        params.put("searchdist", searchdist);
        params.put("fbstatus", fbstatus);
        params.put("apikey", api_key);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetProfile");
    }

    public static void getAddressSuggestions(String strText, String strLatitude, String strLongitude,
                                             final GetAddressFromLatLongCallback callback, final Context context) {

        strText = strText.replace(" ", "+");
        String mapKey = "AIzaSyDMSVwjY-Ml_OeJpzDKQEM-8fNZWJnHXY8";
//        String strUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?"
        String strUrl = "https://maps.googleapis.com/maps/api/geocode/json?"
                + "&latlng="
                + strLatitude
                + ","
                + strLongitude
                + "&radius=49000"
                + "&sensor=true&key=" + mapKey;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.GET, strUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);
                callback.getAddressFromLatLongSuccess(jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.getAddressFromLatLongError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.getAddressFromLatLongError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "GetAddressFromLatLong");

    }


    // Call Get Address Lat Long
    public static void CallGetPlacesApi(String input, String location,
                                        final GetPlacesCallback callback, final Context context) {
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";

        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
        // https://maps.googleapis.com/maps/api/place/autocomplete/json?input=&types=geocode&key=AIzaSyBD_1vHA8R79XyWl807jtzTpqJa813k2BY
        // String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&types=geocode&sensor=false&key=AIzaSyBhtWDdfv-iM6tMYNOw8fKtVCBxfepI8Fc";
//        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input + "&types=geocode&sensor=false&key=AIzaSyBD_1vHA8R79XyWl807jtzTpqJa813k2BY";
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&types=geocode&sensor=false&key=AIzaSyDpo2CLkvGzU79O2WNmv6t16b4fQaISE4o";

        String mapKey = "AIzaSyDMSVwjY-Ml_OeJpzDKQEM-8fNZWJnHXY8";
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input + "&types=geocode&sensor=false&key=" + mapKey;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetPlacesCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetPlacesCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetPlacesCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        /*params.put("input", input);
        params.put("location", location);
        //arams.put("radius", "49000");
        params.put("sensor", "false");
        params.put("types", "geocode");
        params.put("key", "AIzaSyBhtWDdfv-iM6tMYNOw8fKtVCBxfepI8Fc");*/


        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "GetAddress");
    }

    public static void CallGetLatLongFromAddressApi(String input,
                                                    final GetLatLongFromAddressCallback callback,
                                                    final Context context) {
//        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input + "&types=geocode&sensor=false&key=AIzaSyBD_1vHA8R79XyWl807jtzTpqJa813k2BY";
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&types=geocode&sensor=false&key=AIzaSyDpo2CLkvGzU79O2WNmv6t16b4fQaISE4o";

//        String url = "http://maps.google.com/maps/api/geocode/json?address="
//                + input.replace(" ", "+") + "&sensor=false";

//        String mapKey = context.getResources().getString(R.string.google_maps_key);
//        String mapKey = "AIzaSyBD_1vHA8R79XyWl807jtzTpqJa813k2BY";

        String mapKey = "AIzaSyDMSVwjY-Ml_OeJpzDKQEM-8fNZWJnHXY8";
        // New API
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="
                + input.replace(" ", "+") + "&key=" + mapKey;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);
//                Toast.makeText(context, ""+jsonObject,Toast.LENGTH_LONG).show();
                callback.GetLatLongFromAddressCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetLatLongFromAddressCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetLatLongFromAddressCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        /*params.put("input", input);
        params.put("location", location);
        //arams.put("radius", "49000");
        params.put("sensor", "false");
        params.put("types", "geocode");
        params.put("key", "AIzaSyBhtWDdfv-iM6tMYNOw8fKtVCBxfepI8Fc");*/


        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "GetLatLong");
    }


    // Call Get
    public static void CallGetRequestorJob(String uid,
                                           final GetRequestorJobCallback callback, final Context context) {
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";

        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
        //String url = "http://carshovel.com/api/jobmgt.php/rjobprofile/?uid=" + uid;
        //Log.v("URL", "" + url);
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&types=geocode&sensor=false&key=AIzaSyDpo2CLkvGzU79O2WNmv6t16b4fQaISE4o";
        String url = Webservices.base_url + "jobmgt.php/rjobprofile/?uid=" + uid;


        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetRequestorJobCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetRequestorJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetRequestorJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetRequestorJob");
    }

    //Get Shoveler job

    public static void CallGetShovelerJob(String uid,
                                          final GetShovelerJobCallback callback, final Context context) {
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";

        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
        String url = Webservices.Job_Profile_shoveler + "?uid=" + uid;
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&types=geocode&sensor=false&key=AIzaSyDpo2CLkvGzU79O2WNmv6t16b4fQaISE4o";


        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetShovelerJobCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetShovelerJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetShovelerJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        // params.put("uid", uid);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetRequestorJob");
    }

    // Call Get Profile
    public static void CallGetBusinessPrice(String sizeofwork,
                                            final GetBusinessPriceCallback callback, final Context context) {
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";

        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
        String url = "http://carshovel.com/api/jobmgt.php/getbusinesscost/?sizeofwork=" + sizeofwork + "&zipcode=" + Constants.Postal_code;
        Log.v("URL", "" + url);
        //String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&types=geocode&sensor=false&key=AIzaSyDpo2CLkvGzU79O2WNmv6t16b4fQaISE4o";


        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetBusinessPriceCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetBusinessPriceCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetBusinessPriceCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("sizeofwork", sizeofwork);


        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetProfile");
    }

    public static void CallAddJob(HashMap<String, String> map, File file,
                                  final PostJobCallback callback, final Context context) {

        String url = Webservices.Reg_Job_Business;

        Log.v("parameters", "" + map);
        Log.v("file", "" + file);

        MultipartRequest mr = new MultipartRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("TAG", "Success " + response);
                        callback.PostJobCallbackSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.PostJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.PostJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }

        }, file, map);

        mr.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(mr);
    }

    public static void CallAddJob(HashMap<String, String> map, final PostJobCallback callback,
                                  final Context context) {

        String url = Webservices.Reg_Job_Business;
        Log.v("parameters", "" + map);

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.PostJobCallbackSuccess(jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.PostJobCallbackSuccess(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.PostJobCallbackSuccess(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

//        request.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setParams(map);

        Volley.newRequestQueue(context).add(request);
    }

    // Call Get
    public static void CallGetAvailableJob(String uid, String filter, String ad, String lat, String lng,
                                           final AvailableJobCallback callback, final Context context) {

        String url = Webservices.jobs;
//uid=0&filter=0&ad=0&slat=18.5204300&slng=73.8567440
        lat="41.78";
        lng = "123.42";
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.AvailableJobCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.AvailableJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.AvailableJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("uid", "0");
        params.put("uid", uid);
        params.put("filter", filter);
        params.put("ad", ad);
        params.put("slat", lat);
        params.put("slng", lng);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    // Call AcceptJob
    public static void CallAcceptJob(String jid, String ajuid,String time,
                                     final AcceptJobCallback callback, final Context context) {

        String url = Webservices.accept_job;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.AcceptJobCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.AcceptJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.AcceptJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("jid", jid);
        params.put("ajuid", ajuid);
        params.put("esttime",time);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    //Cancel job
    // Call AcceptJob
    public static void CallCancelJob(String jid, String ajuid, String reason,
                                     final CancelJobCallback callback, final Context context) {

        String url = Webservices.cancel_job;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.CancelJobCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.CancelJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.CancelJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("jid", jid);
        params.put("ajuid", ajuid);
        params.put("reason", reason);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    //Call terms
    public static void CallTerms(final callStaticAPI callback, final Context context) {

        String url = Webservices.terms;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.callStaticAPISuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    public static void CallStatic(String CMSId, final callStaticAPI callback, final Context context) {

        String url = Webservices.about;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.callStaticAPISuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("CMSId", CMSId);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    public static void CallFAQ(final callStaticAPI callback, final Context context) {

        String url = Webservices.faq;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.callStaticAPISuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    public static void CallPrivacy(String feedback, final callStaticAPI callback, final Context context) {

        String url = Webservices.privacy;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.callStaticAPISuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.callStaticAPIError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetAvailableJob");
    }

    public static void CallFinishJob(HashMap<String, String> map, ArrayList<File> file, final FinishJobCallback callback, final Context context) {

        String url = Webservices.finish_job;

        Log.v("parameters", "" + map);
        Log.v("file", "" + file);


        MultipartRequestArray mr = new MultipartRequestArray(url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.v("TAG", "Success " + response);
                        callback.CallGetPriceCallbackSuccess(response.toString());
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.CallGetPriceCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.CallGetPriceCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }

        }, file, map);
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        mr.setRetryPolicy(policy);
        Volley.newRequestQueue(context).add(mr);
    }

    public static void CallFeedback(String uid, String feedback, final FeedbackCallback callback, final Context context) {

        String url = Webservices.feedback;


        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.FeedbackCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.FeedbackCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.FeedbackCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("feedback", feedback);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallFeedback");
    }


    public static void CallRelaunch(String jid, String uid, final RelunchCallback callback, final Context context) {

        String url = Webservices.relunch;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.RelunchCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.RelunchCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.RelunchCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("jid", jid);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallFeedback");
    }

    public static void CallCancelJob(String jid, String uid, final CancelJobsCallback callback, final Context context) {

        String url = Webservices.cancels_job;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.CancelJobsCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.CancelJobsCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.CancelJobsCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("jid", jid);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallFeedback");
    }

    public static void CallHoldCancelJob(String jid, String uid, String status, final HoldCancelJobsCallback callback, final Context context) {

        String url = Webservices.hold_cancels_job;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.HoldCancelJobsCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.HoldCancelJobsCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.HoldCancelJobsCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("status", status);
        params.put("jid", jid);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallHoldCancelJob");
    }

    public static void CallAddPromocode(String pcode, String uid, final PromocodeCallback callback, final Context context) {

        String url = Webservices.add_promocode;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.PromocodeCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.PromocodeCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.PromocodeCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("pcode", pcode);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallFeedback");
    }

    public static void CallGetPromocode(final GetPromoCodeCallback callback, final Context context) {

        String url = Webservices.get_promocode;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.GetPromoCodeCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.GetPromoCodeCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.GetPromoCodeCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallFeedback");
    }

    public static void CallCheckPromocode(String pcode, String uid, final CheckPromoCodeCallback callback, final Context context) {

        String url = Webservices.check_promocode;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.CheckPromoCodeCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.CheckPromoCodeCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.CheckPromoCodeCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("pcode", pcode);
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallFeedback");
    }

    public static void CallApproveJob(String jid, String uid, String tipamt, String rating, final ApproveJobCallback callback, final Context context) {

        String url = Webservices.approve_job + "?uid=" + uid + "&jid=" + jid + "&tipamt=" + tipamt + "&rating=" + rating + "&anyissue=" + "";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.ApproveJobCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.ApproveJobCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.ApproveJobCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
      /*  params.put("uid", uid);
        params.put("jid", jid);
        params.put("tipamt", tipamt);
        params.put("rating", rating);
        params.put("anyissue", "");*/
        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallApproveJob");
    }


    // Call Get Profile
    public static void CallSaveProfile(String uid, String cardnumber, String month, String year, String cvc,
                                       final SaveProfileCallback callback, final Context context) {
        String url = Webservices.base_url + "userreg.php/profile/";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.SaveProfileCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.SaveProfileCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.SaveProfileCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("cardnumber", cardnumber);
        params.put("month", month);
        params.put("year", year);
        params.put("cvc", cvc);
        //params.put("mobno", mobno);
        request.setParams(params);

        ShovlerApplication.getInstance().addToRequestQueue(request, "CallGetProfile");
    }

    public static void CallLogOut(String uid, final LogoutCallback callback, final Context context) {

        String url = Webservices.logout;

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(
                Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("TAG", "Success " + jsonObject);

                callback.LogoutCallbackSuccess(jsonObject.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TAG", "ERROR " + volleyError.toString());
                if (volleyError instanceof TimeoutError
                        || volleyError instanceof NoConnectionError) {
                    callback.LogoutCallbackError(context
                            .getResources().getString(
                                    R.string.network_error));
                } else {
                    callback.LogoutCallbackError(context
                            .getResources().getString(
                                    R.string.wents_wrong));
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);

        request.setParams(params);
        ShovlerApplication.getInstance().addToRequestQueue(request, "CallHoldCancelJob");
    }

}
