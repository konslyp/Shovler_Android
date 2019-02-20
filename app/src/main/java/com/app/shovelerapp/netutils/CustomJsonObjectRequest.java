package com.app.shovelerapp.netutils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CustomJsonObjectRequest extends JsonObjectRequest {
	private Map<String, String> params;
	private Map<String, String> headers;

	private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded";

	public CustomJsonObjectRequest(int method, String url,
								   Response.Listener<JSONObject> listener,
								   Response.ErrorListener errorListener) {
		super(method, url, null, listener, errorListener);
		this.params = new HashMap<String, String>();
		this.headers = new HashMap<String, String>();
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Log.d("PARAMS", this.params.toString());
		return this.params;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Log.d("HEADER", this.headers.toString());
		return this.headers;
	}

	@Override
	public String getBodyContentType() {
		Log.d("CONTENT", PROTOCOL_CONTENT_TYPE);
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		Log.d("BODY", this.params.toString());
		if (params != null && params.size() > 0) {
			return encodeParameters(params, getParamsEncoding());
		}
		return null;
	}

	/**
	 * Converts <code>params</code> into an application/x-www-form-urlencoded
	 * encoded string.
	 */
	private byte[] encodeParameters(Map<String, String> params,
			String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(URLEncoder.encode(entry.getKey(),
						paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(),
						paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: "
					+ paramsEncoding, uee);
		}
	}
}
