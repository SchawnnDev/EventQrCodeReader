package fr.schawnndev.qrcodereader.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String apiKey;
    private String email;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String apiKey, String email) {
        this.apiKey = apiKey;
        this.email = email;
    }

    String getApiKey() {
        return apiKey;
    }

    String getEmail() { return email; }
}
