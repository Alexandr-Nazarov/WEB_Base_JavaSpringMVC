package org.example.frontend.service.impl;

import org.example.frontend.model.Addresses;
import org.example.frontend.model.Client;
import org.example.frontend.service.ClientService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@Primary
public class ClientServiceImpl implements ClientService {

    @Override
    public Stream<Client> getClients() {
        return Stream.empty();
    }

    @Override
    public Optional<Client> create(Client client) {
        return Optional.empty();
    }

    @Override
    public void delete(Integer clientId) {

    }

    @Override
    public Stream<Client> getFilteredClients(String type, String searchText) {
        return Stream.empty();
    }

    @Override
    public Client findClientById(Integer clientId) {
        return null;
    }

    @Override
    public void updateClient(Client client) {

    }

    @Override
    public void deleteAddress(Integer clientId, Integer addressId) {

    }

    @Override
    public void addAddress(Integer clientId, Addresses address) {

    }
}
