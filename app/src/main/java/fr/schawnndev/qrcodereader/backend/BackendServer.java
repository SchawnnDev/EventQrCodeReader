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

public class BackendServer {

    public static boolean Login(String apiKey, String email)
    {
        String args = apiKey + "§§" + email;
        String encodedArgs = Base64.encodeToString(args.getBytes(),Base64.NO_WRAP | Base64.URL_SAFE);
        String url = getUrl("login", encodedArgs);

        try {
            JSONObject jsonObject = readJsonFromUrl(url);
            return jsonObject != null && jsonObject.getBoolean("success");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String getUrl(String action, String args)
    {
        return "http://localhost:7473/api/" + action + "/" + args;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

}
