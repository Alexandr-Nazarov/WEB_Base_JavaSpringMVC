package org.example.frontend.exception;


import org.springframework.http.HttpStatusCode;

public class ExternalApiException extends RuntimeException {
    private HttpStatusCode statusCode;
    private String responseBody;

    public ExternalApiException(String message, HttpStatusCode statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ExternalApiException(String message, HttpStatusCode statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
}