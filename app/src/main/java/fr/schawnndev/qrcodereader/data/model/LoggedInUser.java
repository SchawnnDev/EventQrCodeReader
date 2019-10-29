package fr.schawnndev.qrcodereader.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String apiKey;
    private String email;

    public LoggedInUser(String apiKey, String email) {
        this.apiKey = apiKey;
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEmail() { return email; }
}
