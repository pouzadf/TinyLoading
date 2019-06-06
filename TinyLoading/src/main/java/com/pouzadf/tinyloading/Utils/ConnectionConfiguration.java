package com.pouzadf.tinyloading.Utils;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * This class configures each fetch request to any url. Provides timeout and headers support. No retry
 * policy have been implemented yet.
 */
public class ConnectionConfiguration {

    /*Timeout in ms*/
    private static final int timeoutConnect = 5000;

    /*Timeout in ms */
    private static final int timeoutRead = 45000;


    /**
     *
     * @param source URL source
     * @param headers Provided by user, specific headers to send with the request
     * @return If the connection succeed returns the connection, itMUST be closed by the caller.
     * @throws IOException if the connection failed or any  others IO error occurs.
     *
     */
    public static HttpURLConnection initConnection(String source, @Nullable  Map<String, String> headers) throws IOException{
        URL url = new URL(source);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeoutConnect);
        connection.setReadTimeout(timeoutRead);

        if(headers != null)
        {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        connection.connect();
        final int respcode = connection.getResponseCode();
        /*all HTTP return code that begins with 2 indicates that request succeeded*/
        if(respcode / 100 == 2)
            return connection;

        connection.disconnect();
        throw new IOException("An error occurred while performing get request on: " +
                source + "\n return code :" + respcode);
    }
}
