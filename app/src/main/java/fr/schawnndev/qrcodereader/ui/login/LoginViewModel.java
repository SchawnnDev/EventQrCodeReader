package fr.schawnndev.qrcodereader.ui.login;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.backend.tasks.Singleton;
import fr.schawnndev.qrcodereader.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private Context appContext;

    LoginViewModel() {
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(final String apiKey, final String email) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, BackendServer.getLoginUrl(apiKey, email), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || !response.has("success") || !response.getBoolean("success")) {
                                loginResult.setValue(new LoginResult(R.string.login_failed));
                            } else {
                                loginResult.setValue(new LoginResult(new LoggedInUserView(apiKey, email)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginResult.setValue(new LoginResult(R.string.login_failed));

                        for (StackTraceElement e :
                                error.getStackTrace()) {
                            Log.e("[StackTrace]", e.toString());
                        }
                    }
                });

        Singleton.getInstance(appContext).addToRequestQueue(jsonObjectRequest);

    }

    public void loginDataChanged(String apiKey, String email) {

        if (!isApiKeyValid(apiKey)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_apikey, null));
            return;
        }
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_email));
            return;
        }

        loginFormState.setValue(new LoginFormState(true));

    }

    // A placeholder username validation check
    private boolean isApiKeyValid(String apiKey) {
        return apiKey != null && !apiKey.isEmpty();
    }

    private boolean isEmailValid(String email) {
        if (email == null || email.isEmpty())
            return false;

        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }
}
