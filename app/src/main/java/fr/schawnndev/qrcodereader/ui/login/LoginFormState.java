package fr.schawnndev.qrcodereader.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer apiKeyError;
    @Nullable
    private Integer emailError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer apiKeyError, @Nullable Integer emailError) {
        this.apiKeyError = apiKeyError;
        this.emailError=emailError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.apiKeyError = this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getApiKeyError() {
        return apiKeyError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
