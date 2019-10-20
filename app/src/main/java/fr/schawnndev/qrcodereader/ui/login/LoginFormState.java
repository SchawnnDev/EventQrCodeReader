package fr.schawnndev.qrcodereader.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer apiKeyError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer apiKeyError) {
        this.apiKeyError = apiKeyError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.apiKeyError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getApiKeyError() {
        return apiKeyError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
