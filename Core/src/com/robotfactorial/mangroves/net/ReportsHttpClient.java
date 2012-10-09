/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.robotfactorial.mangroves.net;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.text.TextUtils;

import com.robotfactorial.mangroves.ImageManager;
import com.robotfactorial.mangroves.Preferences;
import com.robotfactorial.mangroves.util.ApiUtils;
import com.robotfactorial.mangroves.util.ReportsApiUtils;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author eyedol
 */
public class ReportsHttpClient extends MainHttpClient {

	private static MultipartEntity entity;

	private static String YOUTUBE_CLIENT_KEY = "AI39si729rjJcniA-JMG_VCDi3QQixlGpOOmNBzl9r-8dCxcibqtiVOm7WZIC8b4W3RUnqVgtN4vuMzEbGURiRKmfwnKOMFy4Q";
	private static String YOUTUBE_USER = "mappingthemangroves@gmail.com";
	private static String YOUTUBE_PASSWORD = "cyne-ghy-bu-xo";

	private String incidentTitle;
	private String incidentDescription;
	private String youTubeAuthToken;
	private String uploadURL, uploadToken;

	/**
	 * @param context
	 */
	private Context context;

	private ApiUtils apiUtils;

	public ReportsHttpClient(Context context) {
		super(context);
		this.context = context;
		apiUtils = new ApiUtils(context);
	}

	public int getAllReportFromWeb() {
		HttpResponse response;
		String incidents = "";

		// get the right domain to work with
		apiUtils.updateDomain();

		StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
		uriBuilder.append("/api?task=incidents");
		uriBuilder.append("&by=all");
		uriBuilder.append("&limit=" + Preferences.totalReports);
		uriBuilder.append("&resp=json");

		try {
			response = GetURL(uriBuilder.toString());

			if (response == null) {
				// Network is down
				return 100;
			}

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {

				incidents = GetText(response);
				ReportsApiUtils reportsApiUtils = new ReportsApiUtils(incidents);
				if (reportsApiUtils.saveReports(context)) {
					return 0; // return success even if geographic fails
				}

				// bad json string
				return 99;
			}
			return 100; // network down?
		} catch (SocketTimeoutException e) {
			log("SocketTimeoutException e", e);
			return 110;
		} catch (ConnectTimeoutException e) {
			log("ConnectTimeoutException", e);
			return 110;
		} catch (MalformedURLException ex) {
			log("PostFileUpload(): MalformedURLException", ex);
			// invalid URL
			return 111;
		} catch (IllegalArgumentException ex) {
			log("IllegalArgumentException", ex);
			// invalid URI
			return 120;
		} catch (IOException e) {
			log("IOException", e);
			// connection refused
			return 112;
		}

	}

	/**
	 * Upload files to server 0 - success, 1 - missing parameter, 2 - invalid
	 * parameter, 3 - post failed, 5 - access denied, 6 - access limited, 7 - no
	 * data, 8 - api disabled, 9 - no task found, 10 - json is wrong
	 */
	public boolean PostFileUpload(String URL, HashMap<String, String> params)
			throws IOException {
		log("PostFileUpload(): upload file to server.");

		apiUtils.updateDomain();
		entity = new MultipartEntity();
		// Dipo Fix
		try {
			// wrap try around because this constructor can throw Error
			final HttpPost httpost = new HttpPost(URL);

			if (params != null) {
				incidentTitle = params.get("incident_title");
				incidentDescription = params.get("incident_description");

				entity.addPart("task", new StringBody(params.get("task")));
				entity.addPart(
						"incident_title",
						new StringBody(incidentTitle, Charset
								.forName("UTF-8")));
				entity.addPart("incident_description",
						new StringBody(incidentDescription,
								Charset.forName("UTF-8")));
				entity.addPart("incident_date",
						new StringBody(params.get("incident_date")));
				entity.addPart("incident_hour",
						new StringBody(params.get("incident_hour")));
				entity.addPart("incident_minute",
						new StringBody(params.get("incident_minute")));
				entity.addPart("incident_ampm",
						new StringBody(params.get("incident_ampm")));
				entity.addPart("incident_category",
						new StringBody(params.get("incident_category")));
				entity.addPart("latitude",
						new StringBody(params.get("latitude")));
				entity.addPart("longitude",
						new StringBody(params.get("longitude")));
				entity.addPart(
						"location_name",
						new StringBody(params.get("location_name"), Charset
								.forName("UTF-8")));
				entity.addPart(
						"person_first",
						new StringBody(params.get("person_first"), Charset
								.forName("UTF-8")));
				entity.addPart(
						"person_last",
						new StringBody(params.get("person_last"), Charset
								.forName("UTF-8")));
				entity.addPart(
						"person_email",
						new StringBody(params.get("person_email"), Charset
								.forName("UTF-8")));

				if (params.get("filename") != null) {

					if (!TextUtils.isEmpty(params.get("filename"))) {
						String filenames[] = params.get("filename").split(",");
						log("filenames "
								+ ImageManager.getPhotoPath(context,
										filenames[0]));
						for (String filename : filenames) {
							if (ImageManager
									.getPhotoPath(context, filename) != null) {
								File file = new File(ImageManager.getPhotoPath(
										context, filename));
								if (file.exists()) {
									entity.addPart("incident_photo[]",
											new FileBody(file));
								}
							}
						}
					}
				}

				if (params.get("videofilenames") != null) {
					if (!TextUtils.isEmpty(params.get("videofilenames"))) {
						String filenames[] = params.get("videofilenames").split(",");
						for (String filename : filenames) {
							if (ImageManager.getPhotoPath(context, filename) != null) {
								File file = new File(ImageManager.getPhotoPath(context, filename));
								String url = uploadVideo(file);
								if (!url.isEmpty()) {
									entity.addPart("incident_video", new StringBody(url));
								}
							}
						}
					}
				}

				// NEED THIS NOW TO FIX ERROR 417
				httpost.getParams().setBooleanParameter(
						"http.protocol.expect-continue", false);
				httpost.setEntity(entity);

				HttpResponse response = httpClient.execute(httpost);
				Preferences.httpRunning = false;

				HttpEntity respEntity = response.getEntity();
				if (respEntity != null) {
					InputStream serverInput = respEntity.getContent();
					int status = ApiUtils
							.extractPayloadJSON(GetText(serverInput));
					if (status == 0) {
						return true;
					}
					return false;
				}
			}

		} catch (MalformedURLException ex) {
			log("PostFileUpload(): MalformedURLException", ex);

			return false;
			// fall through and return false
		} catch (IllegalArgumentException ex) {
			log("IllegalArgumentException", ex);
			// invalid URI
			return false;
		} catch (ConnectTimeoutException ex) {
			//connection timeout
			log("ConnectionTimeoutException");
			return false;
		} catch (IOException e) {
			log("IOException", e);
			// timeout
			return false;
		}
		return false;
	}

	private String uploadVideo(File file) {
		String videoURL = "";

		if (youTubeAuthToken.isEmpty()) {
			youTubeAuthToken = youTubeAuth();
		}

		getUploadToken();
		if (!uploadToken.isEmpty() && !uploadURL.isEmpty()) {
			Map<String, AbstractContentBody> parts = new HashMap<String, AbstractContentBody>();
			try {
				parts.put("token", new StringBody(uploadToken, Charset.forName("UTF-8")));
				parts.put("file", new FileBody(file, "video/mp4"));

				String response = sendMultipartPost(uploadURL, new HashMap<String, String>(), parts, file.getAbsolutePath());
				if (!response.isEmpty()) {
					videoURL = String.format("http://www.youtube.com/watch?v=%s", response.split("id=")[1]);
				}
			} catch (UnsupportedEncodingException e) {
				log("UnsupportedEncodingException", e);
			}
		}

		return videoURL;
	}

	private String youTubeAuth() {
		String authToken = "";

		try {
			URL authURL = new URL("https://www.google.com/accounts/ClientLogin");
			String body, response;
			Map<String, String> headers = new HashMap<String, String>();

			headers.put("Content-Type", "application/x-www-form-urlencoded");
			body = String.format("Email=%s&Passwd=%s&source=Ushahidi&service=youtube", YOUTUBE_USER, YOUTUBE_PASSWORD);

			response = sendPostCall(authURL, headers, body);
			if (!response.isEmpty()) {
				String[] tokens = response.split("\n");
				if (tokens.length >= 3) {
					String authSet = tokens[tokens.length - 1];
					if (authSet.startsWith("Auth=")) {
						authToken = authSet.replace("Auth=", "");
					}
				}
			}
		} catch (MalformedURLException e) {
			Log.e("Mangroves", "youTubeAuth - Malformed URL: " + e.getMessage());
		}

		return authToken;
	}

	private void getUploadToken() {
		String category = "Nonprofit";
		String keywords = "ushahidi";
		StringBuilder xml = new StringBuilder();
		Map<String, String> headers = new HashMap<String, String>();

		try {
			URL tokenURL = new URL("https://gdata.youtube.com/action/GetUploadToken");
			xml.append("<?xml version=\"1.0\"?>");
			xml.append("<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:yt=\"http://gdata.youtube.com/schemas/2007\">");
			xml.append("<media:group>");
			xml.append(String.format("<media:title type=\"plain\">%s</media:title>", incidentTitle));
			xml.append(String.format("<media:description type=\"plain\">%s</media:description>", incidentDescription));
			xml.append(String.format("<media:category scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\">%s</media:category>", category));
			xml.append(String.format("<media:keywords>%s</media:keywords>", keywords));
			xml.append("</media:group>");
			xml.append("</entry>");

			headers.put("Authorization", String.format("GoogleLogin auth=\"%s\"", youTubeAuthToken));
			headers.put("GData-Version", "2");
			headers.put("X-GData-Key", String.format("key=%s", YOUTUBE_CLIENT_KEY));
			headers.put("Content-Type", "application/atom+xml");
			headers.put("Content-Length", String.format("%d", xml.length()));

			String response = sendPostCall(tokenURL, headers, xml.toString());
			if (!response.isEmpty()) {
				uploadURL = response.split("</url>")[0].split("<url>")[1];
				uploadToken = response.split("</token>")[0].split("<token>")[1];
			}

		} catch (MalformedURLException e) {
			Log.e("Mangroves", "getUploadToken - Malformed URL: " + e.getMessage());
		}
	}

	private static String sendPostCall(URL url, Map<String, String> headers, String body) {
		HttpURLConnection conn = null;
		OutputStream out;
		InputStream in;
		Writer writer;
		String response = "";

		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			for (String header : headers.keySet()) {
				conn.setRequestProperty(header, headers.get(header));
			}
			conn.setDoOutput(true);
			conn.setFixedLengthStreamingMode(body.length());
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);

			if (body !=null && !body.equals("")) {
				out = conn.getOutputStream();
				writer = new OutputStreamWriter(out);
				writer.write(body);
				writer.flush();
			}

			in = conn.getInputStream();
			response = readStream(in);
		} catch (MalformedURLException e) {
			Log.e("Mangroves", "sendPostCall - Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			String errorMessage = "";
			try {
				in = conn.getErrorStream();
				errorMessage = readStream(in);
			} catch (IOException ex) {
				Log.e("Mangroves", "sendPostCall - Error reading request error stream: " + ex.getMessage());
			}

			response = errorMessage;
			Log.e("Mangroves", "sendPostCall - IO Exception: " + e.getMessage() + " --> " + errorMessage);
		} finally {
			if (conn != null)
				conn.disconnect();
		}

		return response;
	}

	private static String sendMultipartPost(String url, Map<String, String> headers, Map<String, AbstractContentBody> parts, String filepath) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpEntity responseEntity = null;
		MultipartEntity reqEntity;

		try {
			HttpPost httpPost = new HttpPost(url);

			File file = new File(filepath);

			for (String header : headers.keySet()) {
				httpPost.setHeader(header, headers.get(header));
			}

			reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (String part : parts.keySet()) {
				reqEntity.addPart(part, parts.get(part));
			}

			httpPost.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(httpPost);
			responseEntity = response.getEntity();

			if (responseEntity != null) {
				responseEntity.consumeContent();
			}
		} catch (IOException e) {
			Log.e("Mangroves", "sendMultipartPost - IO Exception: " + e.getMessage());
		} catch (Exception e) {
			Log.e("Mangroves", "sendMultipartPost - Exception: " + e.getMessage());
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		if (responseEntity != null)
			return responseEntity.toString();
		else
			return "";
	}

	private static String readStream(InputStream in) throws IOException {
		if (in != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];

			try {
				int n;
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}

				return writer.toString();
			} finally {
				in.close();
			}
		} else {
			return "";
		}
	}
}
