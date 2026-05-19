package org.example.backend.model;

//import lombok.Builder;
//import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Client {
    @JsonProperty(value = "client_id")
    private Integer clientId;

    @JsonProperty(value = "client_name")
    @NotEmpty(message = "Поле client_name не может быть пустым")
    @Pattern( regexp = "^[а-яА-ЯёЁ\\-\\\"\\,\\.\\s]+$",
    message = "Имя клиента должно быть на русском языке")
    @Size(max = 100, message = "Длина поля clientName не может превышать 100 символов")
    private String clientName;

    @NotEmpty(message = "Поле type не может быть пустым")
    private String type;

    private LocalDate added;
    @Valid
    private List<Addresses> addresses  = new ArrayList<>();;

    public Client(Integer clientId, String clientName, String type, LocalDate added, List<Addresses> addresses) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.type = type;
        this.added = added;
        this.addresses = addresses;
    }

    public Client() {
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getAdded() {
        return added;
    }

    public void setAdded(LocalDate added) {
        this.added = added;
    }

    public List<Addresses> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Addresses> addresses) {
        this.addresses = addresses;
    }
}
