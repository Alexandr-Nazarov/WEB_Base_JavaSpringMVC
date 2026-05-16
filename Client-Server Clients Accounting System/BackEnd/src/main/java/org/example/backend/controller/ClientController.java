package org.example.backend.controller;

import org.example.backend.model.Client;
import org.example.backend.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
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
    public ResponseEntity<Client> createClient(@RequestBody Client client){
        return clientService.create(client)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filterClients")
    public ResponseEntity<List<Client>> searchClients(
            @RequestParam(value = "filterType", required = false) String filterType,
            @RequestParam(value = "searchText", required = false) String searchText){

            return ResponseEntity.ok(clientService.searchClients(filterType, searchText).toList());
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
        return ResponseEntity.ok(optionalClient.get());
    }

}

//01:33:10