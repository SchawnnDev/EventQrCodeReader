package fr.schawnndev.qrcodereader.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import fr.schawnndev.qrcodereader.data.LoginRepository;
import fr.schawnndev.qrcodereader.data.Result;
import fr.schawnndev.qrcodereader.data.model.LoggedInUser;
import fr.schawnndev.qrcodereader.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String apiKey, String email) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(apiKey, email);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getApiKey(), data.getEmail())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
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

}
