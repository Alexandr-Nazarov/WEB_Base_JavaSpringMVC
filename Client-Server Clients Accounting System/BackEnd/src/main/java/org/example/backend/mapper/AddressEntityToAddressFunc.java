package org.example.backend.mapper;

import org.example.backend.model.Addresses;
import org.example.backend.model.Client;
import org.example.backend.model.entity.AddressEntity;
import org.example.backend.model.entity.ClientEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AddressEntityToAddressFunc implements Function<AddressEntity, Addresses> {

    @Override
    public Addresses apply(AddressEntity addressEntity) {
        Addresses address = new Addresses();
        address.setAddressId(addressEntity.getAddressId());
        address.setIp(addressEntity.getIp());
        address.setMac(addressEntity.getMac());
        address.setModel(addressEntity.getModel());
        address.setAddress(addressEntity.getAddress());
        return address;
    }
}
