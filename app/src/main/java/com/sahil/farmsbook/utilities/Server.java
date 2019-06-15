package com.sahil.farmsbook.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Sahil on 8/10/18.
 */

public class Server {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient client = new OkHttpClient();


    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public static String  performServerCall(String requestURL,
                                            HashMap<String, String> DataParams, String method) throws IOException {
        System.out.println(requestURL);
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            if(method.equals("POST")){
                conn.setDoInput(true);
                conn.setDoOutput(true);
            }


            if(DataParams.size()!=0){
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getRequestDataString(DataParams));

                writer.flush();
                writer.close();
                os.close();}

            int responseCode=conn.getResponseCode();
            System.out.println(responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                System.out.println(response);
            }
            else {
                response="";

            }
        } catch (Exception e) {

            e.printStackTrace();
            throw (new IOException());
        }

        return response;
    }
    public static String  performServerCall(String requestURL,
                                            String method) throws IOException {
        System.out.println(requestURL);
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(40000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            if(method.equals("POST")){
                conn.setDoInput(true);
                conn.setDoOutput(true);
            }



            int responseCode=conn.getResponseCode();
            System.out.println(responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {

            e.printStackTrace();
            throw (new IOException());
        }

        return response;
    }

    private static String getRequestDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String multipartRequest(String urlTo, Map<String, String> params, HashMap<String, File> filepath, String filefield, String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 8 * 1024 * 1024;


        try {


            URL url = new URL(urlTo);
            System.out.println(url.toString());
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; accept-charset=UTF-8; boundary=" + boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());



            if(filepath.size()>0) {
                String[] q = filepath.get(filefield).getAbsolutePath().split("/");
                int idx = q.length - 1;
                File file = new File(filepath.get(filefield).getAbsolutePath());
                FileInputStream fileInputStream = new FileInputStream(file);
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                fileInputStream.close();
            }
            // Upload POST Data
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain; accept-charset=UTF-8" + lineEnd);
                outputStream.writeBytes(lineEnd);

                outputStream.write(value.getBytes("UTF-8"));
                outputStream.writeBytes(lineEnd);

            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            System.out.println(connection.getResponseCode());
            if (200 != connection.getResponseCode()) {
                return null;
            }

            inputStream = connection.getInputStream();


            result = convertStreamToString(inputStream);


            inputStream.close();
            outputStream.flush();
            outputStream.close();


        } catch (Exception e) {
          e.printStackTrace();
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
