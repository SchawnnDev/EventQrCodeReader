package fr.schawnndev.qrcodereader.data;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String apiKey, String email) {

        try {

            if(BackendServer.Login(apiKey, email))
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
