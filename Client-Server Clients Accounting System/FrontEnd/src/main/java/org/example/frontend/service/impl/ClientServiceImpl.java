package org.example.frontend.service.impl;

import org.example.frontend.controller.ClientController;
import org.example.frontend.exception.ExternalApiException;
//import org.example.frontend.exception.FieldError;
import org.example.frontend.exception.ValidationErrorResponse;
import org.example.frontend.exception.ValidationException;
import org.example.frontend.model.Addresses;
import org.example.frontend.model.Client;
import org.example.frontend.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

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
    private final View error;
    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    public ClientServiceImpl(RestClient restClient, View error) {
        this.restClient = restClient;
        this.error = error;
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
        try {
            Client created = restClient.post()
                    .uri("/clients")
                    .body(client)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        int statusCode = res.getStatusCode().value();
                        log.warn("⚠️ Клиентская ошибка при создании: HTTP {}", statusCode);
                        if (statusCode == 400) {
                            try {
                                String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                                log.warn("📄 Тело ошибки валидации: {}", body);
                                ValidationErrorResponse validationError = new ObjectMapper()
                                        .readValue(body, ValidationErrorResponse.class);
                                // Выбрасываем предметное исключение с деталями валидации
                                throw new ValidationException(
                                        validationError.getMessage(),
                                        validationError.getAllErrorMessages()
                                );
                            } catch (IOException | JacksonException e) {
                                log.error("❌ Не удалось распарсить ошибку валидации", e);
                                throw new ValidationException(
                                        "Ошибка валидации",
                                        List.of("Не удалось прочитать детали ошибки")
                                );
                            }
                        }
                        // Для 409 (Conflict) и 422 (Unprocessable) — не выбрасываем исключение,
                        // позволяем методу завершиться и вернуть Optional.empty()
                        if (res.getStatusCode().value() == 409 || res.getStatusCode().value() == 422) {
                            log.info("ℹ️ Статус {}: пользователь не создан (возможно, уже существует)", statusCode);
                            return;
                        }
                        handleErrorResponse(req, res);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleErrorResponse)
                    .body(Client.class);
            return Optional.ofNullable(created);
        } catch (ValidationException | ExternalApiException e) {
            // Предметные исключения пробрасываем дальше — они уже залогированы
            throw e;
        } catch (Exception e) {
            // Неожиданные ошибки логируем и оборачиваем
            log.error("💥 Неожиданная ошибка при создании пользователя", e);
            throw new ExternalApiException(
                    "Внутренняя ошибка клиента",
                    null,
                    null,
                    e
            );
        }
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
