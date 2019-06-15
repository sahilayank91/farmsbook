package com.sahil.farmsbook.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by joey on 20/3/17.
 */

public class MultipartUtility {
    private String boundary;
    private  final String LINE_FEED = "\r\n";
    private  HttpURLConnection httpConn;
    protected String charset="UTF-8";
    protected OutputStream outputStream;
    protected PrintWriter writer;
    public boolean cancel = false;
    protected boolean last_item_is_file = false;
    public static String LOG_TAG = "MultipartUtil";

    public MultipartUtility(){

    }



   public  void performMultipartServerCall(String requestURL,HashMap<String,String>form_params,HashMap<String,File> file_params) throws IOException{


        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
       setup_writer();

        for(String key:form_params.keySet()){
            addFormField(key,form_params.get(key));
        }
        for(String key:file_params.keySet()){
            addFilePart(key,file_params.get(key));
        }
        finish();

    }

    public PrintWriter setup_writer() throws IOException {
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
        return writer;
    }

    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
        last_item_is_file = false;
    }

    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while (!cancel && (bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        if (cancel) {
            writer.close();
            return;
        }

        writer.append(LINE_FEED);
        writer.flush();
        last_item_is_file = true;
    }

    public void addFileStreamPart(String fieldName, String fileName, InputStream inputStream)
            throws IOException {

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while (!cancel && (bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.flush();
        inputStream.close();
        if (cancel) {
            writer.close();
            return;
        }

        writer.append(LINE_FEED);
        writer.flush();
        last_item_is_file = true;
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        httpConn.setRequestProperty(name, value);
        //writer.append(name + ": " + value).append(LINE_FEED).flush();
        //addFormField("headers["+name+"]", value);
        //writer.append(name + ": " + value).append(LINE_FEED).flush();
    }

    public HttpURLConnection finish() throws IOException {

        if (last_item_is_file)
            writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        System.out.print(httpConn.getResponseCode());
        return httpConn;
    }

}