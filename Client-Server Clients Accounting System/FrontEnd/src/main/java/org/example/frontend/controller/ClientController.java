package org.example.frontend.controller;

import org.example.frontend.model.Addresses;
import org.example.frontend.model.Client;
import org.example.frontend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = {"/", "/clients"})
    public String getViewPage(Model model) {
        model.addAttribute("clientsCollection", clientService.getClients());
        return "clients_page";
    }

    @GetMapping(value = "/createClient")
    public String getCreatePage() {
        return "create_page";
    }

    @PostMapping(value = "/clients")
    public String createClient(
            @RequestParam("clientName") String clientName,
            @RequestParam("type") String type,
            @RequestParam("ip") String ip,
            @RequestParam("mac") String mac,
            @RequestParam("model") String clModel,
            @RequestParam("address") String address,
            Model model
    ) {
        Client client = new Client();
        client.setClientName(clientName);
        client.setType(type);
        client.setAdded(LocalDate.now());

        Addresses clAddress = new Addresses();
        clAddress.setIp(ip);
        clAddress.setMac(mac);
        clAddress.setModel(clModel);
        clAddress.setAddress(address);
        clAddress.setClient(client);

        client.getAddresses().add(clAddress);

        clientService.create(client);
        return getViewPage(model);
    }

    @GetMapping(value = "/deleteClient")
    public String deleteClient(
            @RequestParam("clientId") String clientId,
            Model model
    ) {
        if (clientId != null && !clientId.trim().isEmpty()) {
            int clId = Integer.parseInt(clientId);
            clientService.delete(clId);
        }
        return getViewPage(model);
    }


    @GetMapping(value = "/filterClients")
    public String getFilteredViewPage(
            @RequestParam("filterType") String type,
            @RequestParam("searchText") String searchText,
            Model model
    ) {
        model.addAttribute("clientsCollection", clientService.getFilteredClients(type, searchText));
        model.addAttribute("filterType", type);
        model.addAttribute("searchText", searchText);
        return "clients_page";
    }

    @GetMapping(value = "/updateClient")
    public String updateClient(
            @RequestParam(value = "action", required = false) String action,
            @RequestParam("clientId") String clientId,
            @RequestParam(value = "currentAddressIndex", required = false) Integer currentAddressIndex,
            @RequestParam(value = "currentAddressId", required = false) Integer currentAddressId,
            Model model
    ) {
        if (currentAddressIndex == null) { currentAddressIndex = 0;}
        if (action != null) {
            if ("next".equals(action)) {
                ++currentAddressIndex;
            } else if ("previous".equals(action)) {
                --currentAddressIndex;
            }
        }

        if (clientId != null && !clientId.trim().isEmpty()) {
            int clId = Integer.parseInt(clientId);
            Client client = clientService.findClientById(clId);
            if (client != null) {
                if (client.getAddresses() != null) {
                    List<Addresses> addresses = client.getAddresses();
                    if (addresses != null) {
                        //=====поиск следующего id адреса в коллекции данного клиента
                        if (currentAddressId == null) {
                            currentAddressId = 0;
                        }
                        int result = 0;
                        for (Addresses address : addresses) {
                            if ("next".equals(action) || currentAddressId == 0) {
                                if (address.getAddressId() > currentAddressId){
                                if (result == 0 || address.getAddressId() < result)
                                    result = address.getAddressId();
                                }
                            } else if ("previous".equals(action)) {
                                if (address.getAddressId() < currentAddressId) {
                                    if (result == 0 || address.getAddressId() > result)
                                        result = address.getAddressId();
                                }
                            }
                        }
                        currentAddressId = result;
                        //====== поиск в коллекции индекса элемента по его addressId
                        int numAddr = 0;
                        for (Addresses adr : addresses) {
                            if (adr.getAddressId().equals(currentAddressId)) {
                                break;
                            }
                            if (numAddr < addresses.size() - 1)
                                ++numAddr;
                        }
                        //======
                        model.addAttribute("address", addresses.get(numAddr));
                    }
                }
                model.addAttribute("client", client);
                model.addAttribute("currentAddressIndex", currentAddressIndex);
                model.addAttribute("currentAddressId", currentAddressId);
                return "update_page";
            }
        }
        return getViewPage(model);
    }

    @PostMapping(value = "/updateClient")
    public RedirectView/*String*/ updateClientPost(
            @RequestParam("action") String action,
            @RequestParam("clientId") Integer clientId,
            @RequestParam("clientName") String clientName,
            @RequestParam("added") LocalDate added,
            @RequestParam("type") String type,
            @RequestParam("ip") String ip,
            @RequestParam("mac") String mac,
            @RequestParam("model") String clModel,
            @RequestParam("address") String address,
            @RequestParam("addressId") Integer addressId,
            Model model) {
        if ("UPDATE CLIENT".equals(action)) {
            Client client = new Client();
            client.setClientId(clientId);
            client.setClientName(clientName);
            client.setType(type);
            client.setAdded(added);
            Addresses clAddress = new Addresses();
            clAddress.setIp(ip);
            clAddress.setMac(mac);
            clAddress.setModel(clModel);
            clAddress.setAddress(address);
            clAddress.setClient(client);
            clAddress.setAddressId(addressId);
            client.getAddresses().add(clAddress);
            clientService.updateClient(client);
        }
        else if ("DELETE ADDRESS".equals(action)) {
            clientService.deleteAddress(clientId, addressId);
        }
        else if ("ADD ADDRESS".equals(action)) {
            Addresses clAddress = new Addresses();
            clAddress.setIp(ip);
            clAddress.setMac(mac);
            clAddress.setModel(clModel);
            clAddress.setAddress(address);
            clientService.addAddress(clientId, clAddress);
        }

        //return getViewPage(model);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/updateClient?clientId=" + clientId);
        return  redirectView;
    }
}