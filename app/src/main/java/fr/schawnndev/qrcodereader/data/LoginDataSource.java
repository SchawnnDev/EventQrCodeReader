package fr.schawnndev.qrcodereader.data;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.backend.tasks.LoginTask;
import fr.schawnndev.qrcodereader.data.model.LoggedInUser;
import fr.schawnndev.qrcodereader.data.model.LoginArgs;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String apiKey, String email) {

        try {
            if( new LoginTask().execute(new LoginArgs(email, apiKey)).get())
            {
                return new Result.Success<>(new LoggedInUser(apiKey, email));
            } else {
                return new Result.Error(new IOException("Login informations are not valid!"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
