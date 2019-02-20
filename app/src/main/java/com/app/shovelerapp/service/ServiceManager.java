package com.app.shovelerapp.service;

import com.app.shovelerapp.callback.SignUpCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.ChatModel;
import com.app.shovelerapp.netutils.Webservices;
import com.app.shovelerapp.utils.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 12/19/2016.
 */
public class ServiceManager {
    public static void onUpdateDutyStatus(String uid,final int status,final ILoadService activity)
    {
        RequestParams req = new RequestParams();
        req.put("status",String.valueOf(status));
        req.put("uid",uid);

        HttpUtil.post(Config.updateDutyStatusUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");

                        if (response == 200) {
                            String status = localJSONObject1.getString("status");
                            Globals.g_dutyStatus = status;
                            activity.onResponse(1);
                        } else if (response == 400) {
                            activity.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void onVerifyPhone(String uid,String phone,final ILoadService activity)
    {
        Globals.g_phoneCode = "";
        Globals.g_phoneNumber = phone;
        RequestParams req = new RequestParams();
        req.put("phone", phone);
        req.put("uid",uid);

        HttpUtil.post(Config.verifyPhoneUrl, req, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("status")) {
                        int response = localJSONObject1.getInt("status");
                        if (response == 200) {
                            activity.onResponse(2);
                        } else if (response == 400) {
                            activity.onResponse(700);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void onRequestToken(final ILoadService activity) {

        HttpUtil.post(Config.requestTokenUrl, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("token")) {
                        Globals.g_clientToken = localJSONObject1.getString("token");
                        activity.onResponse(2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void onSendSms(String uid,String phone,final ILoadService activity) {
        Globals.g_phoneCode = "";
        Globals.g_phoneNumber = phone;
        RequestParams req = new RequestParams();
        req.put("phone", phone);
        req.put("uid",uid);

        HttpUtil.post(Config.sendSmsUrl, req, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("status")) {
                        int response = localJSONObject1.getInt("status");
                        if (response == 200) {
                            String status = localJSONObject1.getString("status");
                            String code = localJSONObject1.getString("code");
                            String phone = localJSONObject1.getString("phone");
                            Globals.g_phoneCode = code;
                            Globals.g_phoneNumber = phone;
                            activity.onResponse(1);
                        } else if (response == 400) {
                            activity.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void onReadDutyStatus(String uid,final ILoadService activity) {
        RequestParams req = new RequestParams();
        req.put("uid", uid);

        HttpUtil.post(Config.readDutyStatusUrl, req, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");

                        if (response == 200) {
                            String status = localJSONObject1.getString("status");
                            Globals.g_dutyStatus = status;
                            activity.onResponse(2);
                        } else if (response == 400) {
                            activity.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void updateEmailStatus(String uid,String status,final ILoadService caller)
    {
        RequestParams req = new RequestParams();
        req.put("uid",uid);
        req.put("status",status);

        HttpUtil.post(Config.updateEmailStatusUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            Globals.g_emailStatus = localJSONObject1.getString("status");
                            caller.onResponse(2);
                        } else if (response == 400) {
                            caller.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    caller.onResponse(800);
                }
            }
        });

    }
    public static void readEmailStatus(String uid,final ILoadService caller)
    {
        RequestParams req = new RequestParams();
        req.put("uid",uid);

        HttpUtil.post(Config.readEmailStatusUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            Globals.g_emailStatus = localJSONObject1.getString("status");
                            caller.onResponse(0);
                        } else if (response == 400) {
                            caller.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    caller.onResponse(800);
                }
            }
        });

    }
    public static void onSendMessage(String uid,String message,final ILoadService caller)
    {
        RequestParams req = new RequestParams();
        req.put("chat",Globals.g_currentJobID);
        req.put("uid",uid);
        req.put("message",message);

        HttpUtil.post(Config.sendMessageUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            caller.onResponse(1);
                        } else if (response == 400) {
                            caller.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    caller.onResponse(800);
                }
            }
        });
    }
    public static void onLoadChatHistory(final String uid, final ILoadService caller)
    {
        RequestParams req = new RequestParams();
        req.put("chat",Globals.g_currentJobID);
        req.put("uid",uid);
        Globals.g_lstMessages.clear();
        HttpUtil.post(Config.loadChatHistoryUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            JSONArray messageArray = localJSONObject1.getJSONArray("messages");
                            for (int i = 0;i < messageArray.length();i++)
                            {
                                JSONObject messageObj = messageArray.getJSONObject(i);
                                ChatModel cModel = new ChatModel();
                                cModel.mNo = messageObj.getString("no");
                                cModel.mMessage = messageObj.getString("message");
                                cModel.mUid = messageObj.getString("uid");
                                if (cModel.mUid.equals(uid))
                                {
                                    cModel.mFrom = "1";
                                }
                                else cModel.mFrom = "0";
                                cModel.mTime = messageObj.getString("createtime");
                                Globals.g_lstMessages.add(cModel);

                            }
                            caller.onResponse(100);
                        } else if (response == 400) {
                            caller.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    caller.onResponse(800);
                }
            }
        });
    }
    public static void onResetPassword(String email,String password, final ILoadService activity)
    {
        RequestParams req = new RequestParams();
        req.put("email",email);
        req.put("password",password);

        HttpUtil.post(Config.resetPasswordUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            activity.onResponse(1);
                        } else if (response == 400) {
                            activity.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void onGetParnoramaID(String location,final ILoadService caller,final int index)
    {
        String url = "http://maps.google.com/cbk?output=json&ll=" + location;

        HttpUtil.post(url,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    JSONObject locationObject = localJSONObject1.getJSONObject("Location");
                    String panoID = locationObject.getString("panoId");
                    Globals.g_googlePhoto =
                            "http://cbk0.google.com/cbk?output=tile&panoid=" + panoID + "&zoom=3&x=5&y=1";

                    caller.onResponse(400,index);
                } catch (Exception e) {
                    e.printStackTrace();
                    caller.onResponse(800);
                }
            }
        });
    }
    public static void onGetParnoramaID(String location,final ILoadService caller)
    {
        //http://maps.google.com/cbk?output=xml&ll=40.767712,-73.968191
        String url = "http://maps.google.com/cbk?output=json&ll=" + location;

        HttpUtil.post(url,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    JSONObject locationObject = localJSONObject1.getJSONObject("Location");
                    String panoID = locationObject.getString("panoId");
                    Globals.g_googlePhoto =
                            "http://cbk0.google.com/cbk?output=tile&panoid=" + panoID + "&zoom=3&x=5&y=1";

                    caller.onResponse(400);
                } catch (Exception e) {
                    e.printStackTrace();
                    caller.onResponse(800);
                }
            }
        });


    }
    public static void onGetPlacePicture(String url,final ILoadService activity)
    {
        Globals.g_googlePhoto = "";
        HttpUtil.post(url,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    JSONArray resultArray = localJSONObject1.getJSONArray("results");
                    if (resultArray.length() > 0)
                    {
                        JSONObject resultPlace = resultArray.getJSONObject(0);
                        JSONArray placePhotos = resultPlace.getJSONArray("photos");
                        if (placePhotos.length() > 0)
                        {
                            JSONObject photoObject = placePhotos.getJSONObject(0);
                            String photoReference = photoObject.getString("photo_reference");
                            Globals.g_googlePhoto =
                                    "https://maps.googleapis.com/maps/api/place/photo?" +
                                            "photoreference=" + photoReference+"&sensor=false&maxheight=240&maxwidth=320&key=AIzaSyDMSVwjY-Ml_OeJpzDKQEM-8fNZWJnHXY8";
                            activity.onResponse(1);
                        }
                    }
                    activity.onResponse(800);
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void getShovlerLocation(String jid,final ILoadService activity)
    {
        Globals.g_shovlerLat = "";
        Globals.g_shovlerLng = "";
        RequestParams req = new RequestParams();
        req.put("jid",jid);

        HttpUtil.post(Config.getShovlerLocationUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            Globals.g_shovlerLat = localJSONObject1.getString("lat");
                            Globals.g_shovlerLng = localJSONObject1.getString("lng");
                            activity.onResponse(1);
                        } else if (response == 400) {
                            activity.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
    public static void onUpdateLocation(String uid,double lat,double lng)
    {
        RequestParams req = new RequestParams();
        req.put("uid",uid);
        req.put("lat",String.valueOf(lat));
        req.put("lng",String.valueOf(lng));

        HttpUtil.post(Config.updateLocationUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
            }
        });
    }
    public static void onCheckNearbyShovler(Double lat, Double lng,final ILoadService activity)
    {
        RequestParams req = new RequestParams();
        req.put("lat",String.valueOf(lat));
        req.put("lng",String.valueOf(lng));

        HttpUtil.post(Config.checkNearbyUrl,req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                String s = "success";
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");

                        if (response == 200) {
                            String count = localJSONObject1.getString("count");
                            Globals.g_availableShovler = Integer.parseInt(count);
                            activity.onResponse(1);
                        } else if (response == 400) {
                            activity.onResponse(800);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.onResponse(800);
                }
            }
        });
    }
}
