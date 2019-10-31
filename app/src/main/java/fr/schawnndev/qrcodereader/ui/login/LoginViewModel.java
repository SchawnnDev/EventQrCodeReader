package fr.schawnndev.qrcodereader.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Patterns;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.backend.tasks.Singleton;
import fr.schawnndev.qrcodereader.data.LoginRepository;
import fr.schawnndev.qrcodereader.data.Result;
import fr.schawnndev.qrcodereader.data.model.LoggedInUser;
import fr.schawnndev.qrcodereader.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private Context appContext;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(final String apiKey, final String email) {
        // can be launched in a separate asynchronous job
       // Result<LoggedInUser> result = loginRepository.login(apiKey, email);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, BackendServer.getLoginUrl(apiKey, email), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response==null || !response.has("success") || !response.getBoolean("success")){
                                loginResult.setValue(new LoginResult(R.string.login_failed));
                            }else {
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
                    }
                });

// Access the RequestQueue through your singleton class.
        Singleton.getInstance(appContext).addToRequestQueue(jsonObjectRequest);
/*
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();

        } else {

        }*/
    }

    public void loginDataChanged(String apiKey, String email) {

        if (!isApiKeyValid(apiKey)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_apikey, null));
        } else if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_email));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isApiKeyValid(String apiKey) {
        return apiKey != null && !apiKey.isEmpty();
    }

    private boolean isEmailValid(String email)
    {
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
