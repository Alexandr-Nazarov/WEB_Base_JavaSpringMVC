package org.example.backend.service.impl;

import org.example.backend.mapper.ClientEntityToClientFunc;
import org.example.backend.model.Addresses;
import org.example.backend.model.Client;
import org.example.backend.model.entity.AddressEntity;
import org.example.backend.model.entity.ClientEntity;
import org.example.backend.repository.ClientRepository;
import org.example.backend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final Function<ClientEntity, Client> clientEntityListFunction = new ClientEntityToClientFunc();

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Stream<Client> getClients(){
        return clientRepository.findAll().stream().map(clientEntityListFunction);
    }

    @Override
    public Optional<Client> getClientById(Integer clientId){
        return clientRepository.findById(clientId)
                .map(clientEntityListFunction);
    }

    @Override
    public Optional<Client> create(Client client) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setClientId(null/*client.getClientId()*/);
        clientEntity.setClientName(client.getClientName());
        clientEntity.setAdded(client.getAdded());
        clientEntity.setType(client.getType());

        Set<AddressEntity> addressEntities = convertAddressesToEntities(client.getAddresses(), clientEntity);
        clientEntity.setAddressEntities(addressEntities);

        clientRepository.save(clientEntity);

        return  Optional.ofNullable(clientEntityListFunction.apply(clientEntity));

    }

    private Set<AddressEntity> convertAddressesToEntities(List<Addresses> addresses, ClientEntity clientEntity) {
        if (addresses == null || addresses.isEmpty()) {
            return new HashSet<>();
        }
        return addresses.stream()
                .map(address -> convertToAddressEntity(address, clientEntity))
                .collect(Collectors.toSet());
    }

    private AddressEntity convertToAddressEntity(Addresses address, ClientEntity clientEntity) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddressId(address.getAddressId());
        addressEntity.setIp(address.getIp());
        addressEntity.setMac(address.getMac());
        addressEntity.setModel(address.getModel());
        addressEntity.setAddress(address.getAddress());
        addressEntity.setClient(clientEntity);

        clientEntity.getAddressEntities().add(addressEntity);
        return addressEntity;
    }

    @Override
    public Stream<Client> searchClients(String filterType, String searchText) {
        return clientRepository.filterByTypeAndText(filterType, searchText).stream().map(clientEntityListFunction);
    }

    @Override
    public void deleteClient(Integer clientId) {
        clientRepository.deleteById(clientId);
    }
}
