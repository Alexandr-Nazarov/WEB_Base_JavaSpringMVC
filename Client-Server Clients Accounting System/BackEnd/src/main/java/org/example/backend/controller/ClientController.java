package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.model.Addresses;
import org.example.backend.model.Client;
import org.example.backend.model.entity.ClientEntity;
import org.example.backend.service.AddressService;
import org.example.backend.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final ClientService clientService;
    private final AddressService addressService;

    @Autowired
    public ClientController(ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getClients(){
        log.info("Получен запрос getClients");
        return ResponseEntity.ok(clientService.getClients().toList());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Client> getClientById(@PathVariable("id") Integer id){
        log.info("Получен запрос getClientById: {}", id);
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Client> createClient(@RequestBody @Valid Client client){
        log.info("Получен запрос createClient: {}", client.getClientName());
        return clientService.create(client)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filterClients")
    public ResponseEntity<List<Client>> searchClients(
            @RequestParam(value = "filterType", required = false) String filterType,
            @RequestParam(value = "searchText", required = false) String searchText){

        String decodedSearchText = (searchText != null ? URLDecoder.decode(searchText, StandardCharsets.UTF_8) : null);

        return ResponseEntity.ok(clientService.searchClients(filterType, decodedSearchText).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClient(
            @PathVariable(value = "id", required = true) Integer clientId
    ){
        log.info("Получен запрос deleteClient: {}", clientId);

        Optional<Client> optionalClient = clientService.getClientById(clientId);
        if (optionalClient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        clientService.deleteClient(clientId);
        // return ResponseEntity.ok(optionalClient.get());
        log.info("Клиент ID {} удалён", clientId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateClient")
    public ResponseEntity<Client> updateClient(@RequestBody Client client){
        log.info("Получен запрос updateClient");
        return clientService.updateClient(client)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping( "/addAddress")
    public ResponseEntity<Client> createAddress(
            @RequestBody Addresses address,
            @RequestParam(value = "clientId", required = false) Integer clientId){
        log.info("Получен запрос createAddress");
                  Optional<Client> client =  clientService.getClientById(clientId);
                  if (client.isPresent()) {
                      client.get().getAddresses().add(address);
                      return clientService.updateClient(client.get())
                              .map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
                  }
                  return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deleteAddress/{addressId}")
    public ResponseEntity<Object> deleteAddress(
            @PathVariable(value = "addressId", required = true) Integer addressId
    ){
        log.info("Получен запрос deleteAddress: {}", addressId);

        Optional<Addresses> optionalAddress = addressService.getAddressById(addressId);
        if (optionalAddress.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        addressService.deleteAddress(addressId);
        log.info("Адрес ID {} удалён", addressId);
        return ResponseEntity.noContent().build();
    }
}

