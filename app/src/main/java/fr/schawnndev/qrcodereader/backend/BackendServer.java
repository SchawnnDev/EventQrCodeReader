package fr.schawnndev.qrcodereader.backend;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class BackendServer {
    public static String getLoginUrl(String apiKey, String email)
    {
        String args = apiKey + "§§" + email;
        String encodedArgs = Base64.encodeToString(args.getBytes(),Base64.NO_WRAP | Base64.URL_SAFE);
        return getUrl("login", encodedArgs);
    }

    public static String getScanUrl(String apiKey, String email, String qrCode)
    {
        String args = apiKey + "§§" + email;
        String encodedArgs = Base64.encodeToString(args.getBytes(),Base64.NO_WRAP | Base64.URL_SAFE);
        return getUrl("login", encodedArgs);
    }

    public static String getUrl(String action, String args)
    {
        return "http://api.schawnndev.fr/" + action + "?id=" + args;
       // return "https://jsonplaceholder.typicode.com/todos/1";
    }

}
