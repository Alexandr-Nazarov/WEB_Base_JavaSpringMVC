package org.example.frontend.service.impl;

import org.example.frontend.exception.ExternalApiException;
import org.example.frontend.model.Addresses;
import org.example.frontend.model.Client;
import org.example.frontend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Primary
public class ClientServiceImpl implements ClientService {

    private final RestClient restClient;

    @Autowired
    public ClientServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Stream<Client> getClients() {
        List<Client> clients = restClient.get()
                .uri("/clients")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .body(new ParameterizedTypeReference<List<Client>>() {
                });

        return clients != null ? clients.stream() : Stream.empty();
    }

    @Override
    public Optional<Client> create(Client client) {
        Client created = restClient.post()
                .uri("/clients")
                .body(client)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 409 ||  res.getStatusCode().value() == 422) { return;}
                    handleErrorResponse(req, res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .body(Client.class);
        return Optional.ofNullable(created);
    }

    @Override
    public void delete(Integer clientId) {
        restClient.delete()
                .uri("/clients/{id}", clientId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .toBodilessEntity();
    }

    @Override
    public Stream<Client> getFilteredClients(String type, String searchText) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/clients/filterClients");

        if (type != null  && !type.equals("All")) {
            builder.queryParam("filterType", type);
        }
        if (searchText != null) {
            builder.queryParam("searchText", searchText);
        }

        List<Client> clients = restClient.get()
                .uri(builder.toUriString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .body(new ParameterizedTypeReference<List<Client>>() {
                });

        return clients != null ? clients.stream() : Stream.empty();
    }

    @Override
    public Client findClientById(Integer clientId) {
        return restClient.get()
                .uri("/clients/{id}", clientId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .body(Client.class);
    }

    @Override
    public void updateClient(Client client) {
        restClient.put()
                .uri("/clients/updateClient", client)
                .body(client)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .toBodilessEntity();
    }

    @Override
    public void deleteAddress(Integer clientId, Integer addressId) {
      //  UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/clients/deleteAddress/{id}");
//        if (clientId != null) {
//            builder.queryParam("clientId", clientId);
//        }
//        if (addressId != null) {
//            builder.queryParam("addressId", clientId);
//        }

        restClient.delete()
                .uri("/clients/deleteAddress/{id}", addressId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .toBodilessEntity();
    }

    @Override
    public void addAddress(Integer clientId, Addresses address) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/clients/addAddress");

        if (clientId != null) {
            builder.queryParam("clientId", clientId);
        }

        Addresses created = restClient.post()
                .uri(builder.toUriString()/*"/clients/addAddress"*/)
                .body(address)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 409 ||  res.getStatusCode().value() == 422) { return;}
                    handleErrorResponse(req, res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                .body(Addresses.class);

//        Client client = findClientById(clientId);
//        client.getAddresses().add(created);
//        updateClient(client);
    }

    private void handleErrorResponse(HttpRequest request, ClientHttpResponse  response){
        HttpStatusCode code = null;
        try{
            code = response.getStatusCode();
            String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
            String prefix = response.getStatusCode().is4xxClientError() ? "Client error" : "Server error";
            throw new ExternalApiException(
                    prefix = ": " + response.getStatusCode(),
                    response.getStatusCode(),
                    body);

        } catch (IOException e) {
            throw new ExternalApiException(
                    "Failed to read error response",
                    code,
                    null,
                    e
            );
        }
    }
}
