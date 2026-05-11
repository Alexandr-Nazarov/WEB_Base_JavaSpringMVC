package org.example.frontend.service;

import org.example.frontend.model.Addresses;
import org.example.frontend.model.Client;

import java.util.Optional;
import java.util.stream.Stream;

public interface ClientService {

    Stream<Client> getClients();

    Optional<Client> create(Client client);

    void delete(Integer clientId);

    Stream<Client> getFilteredClients(String type, String searchText);

    Client findClientById(Integer clientId);

    void updateClient(Client client);

    void deleteAddress(Integer clientId, Integer addressId);

    void addAddress(Integer clientId, Addresses address);
}
