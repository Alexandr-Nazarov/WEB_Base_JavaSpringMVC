package org.example.backend.service;


import org.example.backend.model.Client;

import java.util.Optional;
import java.util.stream.Stream;

public interface ClientService {

    Stream<Client> getClients();

    Optional<Client> getClientById(Integer clientId);

    Optional<Client> create(Client client);

    Stream<Client> searchClients (String filterType , String searchText);

    void deleteClient(Integer clientId);
}
