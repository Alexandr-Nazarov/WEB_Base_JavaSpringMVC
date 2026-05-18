package org.example.backend.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {

    private List<ErrorDescription> errorDescription;

    public ValidationErrorResponse(String message, List<ErrorDescription> errorDescription){
        super(message);
        this.errorDescription = errorDescription;
    }

    public List<ErrorDescription> getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(List<ErrorDescription> errorDescription) {
        this.errorDescription = errorDescription;
    }

}
