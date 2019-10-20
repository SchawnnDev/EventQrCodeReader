package fr.schawnndev.qrcodereader.data;

import fr.schawnndev.qrcodereader.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String apiKey) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser = new LoggedInUser(apiKey);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
