package com.app.shovelerapp.netutils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class MultipartRequestArray extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private final Listener<String> mListener;


    // private final File file;
    ArrayList<File> files;
    private final HashMap<String, String> params;

    public MultipartRequestArray(String url, Listener<String> listener,
                                 ErrorListener errorListener, ArrayList<File> files,
                                 HashMap<String, String> params) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        this.files = files;
        this.params = params;
        buildMultipartEntity();
    }

    @SuppressWarnings("deprecation")
    private void buildMultipartEntity() {


        try {
            for (String key : params.keySet()) {
                entity.addPart(key, new StringBody(params.get(key)));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
        for (int i = 0; i < files.size(); i++) {
            entity.addPart("file" + (i + 1), new FileBody(files.get(i)));
            Log.v("Key", "file" + (i + 1));
        }

    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    /**
     * copied from Android StringRequest class
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
