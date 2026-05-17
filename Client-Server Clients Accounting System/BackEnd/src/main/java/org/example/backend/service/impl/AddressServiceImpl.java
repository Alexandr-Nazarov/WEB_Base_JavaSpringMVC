package org.example.backend.service.impl;

import org.example.backend.mapper.AddressEntityToAddressFunc;
import org.example.backend.model.Addresses;
import org.example.backend.model.Client;
import org.example.backend.model.entity.AddressEntity;
import org.example.backend.model.entity.ClientEntity;
import org.example.backend.repository.AddressRepository;
import org.example.backend.repository.ClientRepository;
import org.example.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final Function<AddressEntity, Addresses> addressEntityFunction = new AddressEntityToAddressFunc();

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

//    @Override
//    public Optional<Addresses> createAddress(Addresses addresses) {
//        AddressEntity  addressEntity = new AddressEntity();
//        addressEntity.setAddress(addresses.getAddress());
//        addressEntity.setAddressId(null);
//        addressEntity.setIp(addresses.getIp());
//        addressEntity.setMac(addresses.getMac());
//        addressEntity.setModel(addresses.getModel());
//       // addressEntity.setClient();
//        addressRepository.save(addressEntity);
//
//        return  Optional.ofNullable(addressEntityFunction.apply(addressEntity));
//
//    }


    @Override
    public Optional<Addresses> getAddressById(Integer addressId) {
        return addressRepository.findById(addressId)
                .map( addressEntityFunction);
    }

    @Override
    public void deleteAddress(Integer addressId) {
        addressRepository.deleteById(addressId);
    }
}
