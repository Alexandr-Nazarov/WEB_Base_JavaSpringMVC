package org.example.backend.mapper;

import org.example.backend.model.Addresses;
import org.example.backend.model.Client;
import org.example.backend.model.entity.AddressEntity;
import org.example.backend.model.entity.ClientEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ClientEntityToClientFunc implements Function<ClientEntity, Client> {

    @Override
    public Client apply(ClientEntity clientEntity) {
        //List<Client> clients = new LinkedList<>();
        Set<AddressEntity> addressEntities = clientEntity.getAddressEntities();

        Client client = new Client();
        client.setClientId(clientEntity.getClientId());
        client.setClientName(clientEntity.getClientName());
        client.setType(clientEntity.getType());
        client.setAdded(clientEntity.getAdded());

        List<Addresses> addresses = convertAddresses(addressEntities, client);
        client.setAddresses(addresses);
       // clients.add(client);
        return client;
    }

    private List<Addresses> convertAddresses(Set<AddressEntity> addressEntities, Client client) {
        if (addressEntities == null || addressEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return addressEntities.stream()
                .map(addressEntity ->convertToAddress(addressEntity, client) )
                .collect(Collectors.toList());
    }

    private Addresses convertToAddress(AddressEntity addressEntity, Client client) {
        Addresses address = new Addresses();
        address.setAddressId(addressEntity.getAddressId());
        address.setIp(addressEntity.getIp());
        address.setMac(addressEntity.getMac());
        address.setModel(addressEntity.getModel());
        address.setAddress(addressEntity.getAddress());
       // address.setClient(client);
        return address;
    }
}

