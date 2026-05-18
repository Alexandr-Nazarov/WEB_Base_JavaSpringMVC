package org.example.frontend.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorResponse {

    private String message;

    @JsonProperty("errorDescription")
    private List<ErrorDescription> errorDescriptions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorDescription {
        @JsonProperty("errorMessage")
        private String errorMessage;

        public ErrorDescription(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    public List<String> getAllErrorMessages() {
        return errorDescriptions != null
                ? errorDescriptions.stream()
                .map(ErrorDescription::getErrorMessage)
                .toList()
                : List.of();
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDescription> getErrorDescriptions() {
        return errorDescriptions;
    }
    public void setErrorDescriptions(List<ErrorDescription> errorDescriptions) {
        this.errorDescriptions = errorDescriptions;
    }
}
