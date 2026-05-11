package org.example.frontend.service.impl;

import org.example.frontend.model.Addresses;
import org.example.frontend.model.Client;
import org.example.frontend.service.ClientService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Primary
public class CustomClientServiceImpl implements ClientService {

    private List<Client> clients;

    @Override
    public Stream<Client> getClients() {
        return getCustomClients().stream();
    }

    private List<Client> getCustomClients() {
        Client cl1 = new Client(1, "AAA", "Corporate", LocalDate.of(2023, 5, 15), Arrays.asList(
                new Addresses(101, "192.168.1.10", "00:1A:2B:3C:4D:5E", "Dell XPS 13", "Москва, ул. Тверская, 15"),
                new Addresses(102, "10.0.0.25", "AA:BB:CC:DD:EE:FF", "MacBook Pro", "Москва, пр. Вернадского, 42"),
                new Addresses(103, "172.16.0.50", "11:22:33:44:55:66", "HP EliteBook", "Санкт‑Петербург, Невский пр., 105")
        ));
        Client cl2 = new Client(
                2,
                "Иванов Алексей Сергеевич",
                "Individual",
                LocalDate.of(2023, 6, 20),
                Arrays.asList(
                        new Addresses(201, "192.168.2.20", "A1:B2:C3:D4:E5:F6", "Lenovo ThinkPad", "Москва, ул. Ленина, 34, кв. 12")
                ));

        if (clients == null) {
            clients = new ArrayList<>();
            clients.addAll(Arrays.asList(cl1, cl2));
        }
        return clients;
    }

    @Override
    public Optional<Client> create(Client client) {
        return Optional.ofNullable(client).
                map(cl -> {
                    cl.setClientId(findFirstFreeId());
                    cl.getAddresses().getFirst().setAddressId(findFirstFreeAddrId());
                    clients.add(cl);
                    return cl;
                });
    }

    private Integer findFirstFreeId() {
        if (clients == null || clients.isEmpty())
            return 1;
        Set<Integer> ids = clients.stream().map(c -> c.getClientId()).collect(Collectors.toSet());
        for (int id = 1; ; ++id) {
            if (!ids.contains(id))
                return id;
        }
    }

    @Override
    public void delete(Integer clientId) {
        if (clients != null && !clients.isEmpty()) {
            clients.removeIf(cl -> cl.getClientId().equals(clientId));
        }
    }

    public Stream<Client> getFilteredClients(String type, String searchText) {
        if (clients == null) {
            getClients();
        }

        if (clients != null) {
            if (type != null && !type.isEmpty() && "All".equalsIgnoreCase(type) && searchText.isEmpty()) {
                return clients.stream();
            } else {
                return clients.stream()
                        .filter(c -> filterType(c, type))
                        .filter(c -> filterNameAndAddress(c, searchText));
                //.filter(c -> filterName(c, searchText))
                //.filter(c -> filterAddress(c, searchText));
            }
        } else {
            return Stream.empty();
        }
    }

    private boolean filterType(Client client, String type) {
        if (type == null || type.trim().isEmpty()) return true;
        return client.getType().equals(type) || "All".equalsIgnoreCase(type);
    }

    private boolean filterAddress(Client client, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;
        return client.getAddresses() != null && client.getAddresses().stream().anyMatch(address -> address.getAddress().toLowerCase().contains(searchText.toLowerCase()));
    }

    private boolean filterName(Client client, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;
        return client.getClientName() != null && client.getClientName().toLowerCase().contains(searchText.toLowerCase());
    }

    private boolean filterNameAndAddress(Client client, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;
        return filterAddress(client, searchText) || filterName(client, searchText);
    }

    @Override
    public Client findClientById(Integer clientId) {
        return clients.stream().filter(c -> c.getClientId().equals(clientId)).findFirst().orElse(null);
    }

    @Override
    public void updateClient(Client client) {
        if (clients != null) {
            for (Client cl : clients) {
                if (cl.getClientId().equals(client.getClientId())) {
                    cl.setClientName(client.getClientName());
                    cl.setType(client.getType());
                    for (Addresses address : cl.getAddresses()) {
                        if (address.getAddressId().equals(client.getAddresses().getFirst().getAddressId())) {
                            address.setAddress(client.getAddresses().getFirst().getAddress());
                            address.setIp(client.getAddresses().getFirst().getIp());
                            address.setMac(client.getAddresses().getFirst().getMac());
                            address.setModel(client.getAddresses().getFirst().getModel());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void deleteAddress(Integer clientId, Integer addressId) {
        if (clients != null) {
            for (Client cl : clients) {
                if (cl != null && cl.getClientId().equals(clientId)) {
                    if (cl.getAddresses() != null && !cl.getAddresses().isEmpty()) {
                        List<Addresses> modifAddresses = new ArrayList<>(cl.getAddresses());
                        Iterator<Addresses> iterator = modifAddresses.iterator();
                        while (iterator.hasNext()) {
                            Addresses address = iterator.next();
                            if (address != null && address.getAddressId().equals(addressId)) {
                                iterator.remove();
                                break;
                            }
                        }
                        cl.setAddresses(modifAddresses);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void addAddress(Integer clientId, Addresses address) {
        if (clients != null && address != null) {
            for (Client cl : clients) {
                if (cl != null && cl.getClientId().equals(clientId) && cl.getAddresses() != null) {
                    address.setAddressId(findFirstFreeAddrId());
                    address.setClient(cl);
                    List<Addresses> modifAddresses = new ArrayList<>(cl.getAddresses());
                    modifAddresses.add(address);
                    cl.setAddresses(modifAddresses);
                    break;
                }
            }
        }
    }

    private Integer findFirstFreeAddrId() {
        int id = 1;
        for (int i = 0; i < clients.size(); i++) {
            for (int j = 0; j < clients.get(i).getAddresses().size(); j++) {
                if (clients.get(i).getAddresses().get(j).getAddressId().equals(id)) {
                    i = 0;
                    j = 0;
                    ++id;
                }
            }
        }
        return id;
    }
}
