package fr.schawnndev.qrcodereader.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String apiKey;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String apiKey) {
        this.apiKey = apiKey;
    }

    String getApiKey() {
        return apiKey;
    }
}
