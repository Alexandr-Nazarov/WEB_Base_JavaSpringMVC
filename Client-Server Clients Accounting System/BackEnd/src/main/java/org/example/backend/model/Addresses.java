package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

public class Addresses {
    @JsonProperty(value = "address_id")
    private Integer addressId;

    @NotEmpty(message = "Поле ip не может быть пустым")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
            message = "Некорректный формат ip адреса")
    // @Size(max = 25, message = "Длина поля ip не может превышать 25 символов")
    private String ip;

    @NotEmpty(message = "Поле mac не может быть пустым")
    @Pattern( regexp = "^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}$",
            message = "Некорректный формат mac адреса")
    @Size(max = 20, message = "Длина поля mac не может превышать 20 символов")
    private String mac;

    @NotEmpty(message = "Поле model не может быть пустым")
    @Size(max = 100, message = "Длина поля model не может превышать 100 символов")
    private String model;

    @NotEmpty(message = "Поле address не может быть пустым")
    @Size(max = 200, message = "Длина поля address не может превышать 200 символов")
    private String address;

    private Client client;

    public Addresses(Integer addressId, String ip, String mac, String model, String address, Client client) {
        this.addressId = addressId;
        this.ip = ip;
        this.mac = mac;
        this.model = model;
        this.address = address;
        this.client = client;
        client.getAddresses().add(this); // добавляем адрес в коллекцию клиента
    }

    public Addresses() {
    }

    public Addresses(Integer addressId, String ip, String mac, String model, String address) {
        this.addressId = addressId;
        this.ip = ip;
        this.mac = mac;
        this.model = model;
        this.address = address;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Addresses addresses = (Addresses) o;
        return Objects.equals(addressId, addresses.addressId) && Objects.equals(ip, addresses.ip) && Objects.equals(mac, addresses.mac) && Objects.equals(model, addresses.model) && Objects.equals(address, addresses.address) && Objects.equals(client, addresses.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId, ip, mac, model, address, client);
    }
}
