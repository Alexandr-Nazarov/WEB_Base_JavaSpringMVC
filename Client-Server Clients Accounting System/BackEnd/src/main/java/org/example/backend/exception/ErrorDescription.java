package org.example.backend.exception;

public class ErrorDescription {
    private String fieldName;
    private String errorMessage;

    ErrorDescription(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
