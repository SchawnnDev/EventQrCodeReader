package fr.schawnndev.qrcodereader.backend.tasks;

import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.data.model.LoginArgs;

public class LoginTask extends AsyncTask<LoginArgs, Void, Boolean> {

    private Exception exception;

    protected Boolean doInBackground(LoginArgs... loginArgs) {

        String args = loginArgs[0].getApiKey() + "§§" + loginArgs[0].getEmail();
        String encodedArgs = Base64.encodeToString(args.getBytes(),Base64.NO_WRAP | Base64.URL_SAFE);
        String url = BackendServer.getUrl("login", encodedArgs);

        try {
            JSONObject jsonObject = BackendServer.jsonGetRequest(url);
            if(jsonObject != null) System.out.println(jsonObject.toString());
            return jsonObject != null && jsonObject.getBoolean("success");
        } catch (IOException e) {
            e.printStackTrace();
            this.exception = e;
        } catch (JSONException e) {
            e.printStackTrace();
            this.exception = e;
        }
        return false;
    }

    protected void onPostExecute(Boolean success) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}