package fr.schawnndev.qrcodereader.data.model;

public class LoginArgs {

    private String email;
    private String apiKey;

    public LoginArgs(String email, String apiKey)
    {
        this.email= email;
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEmail() {
        return email;
    }
}
