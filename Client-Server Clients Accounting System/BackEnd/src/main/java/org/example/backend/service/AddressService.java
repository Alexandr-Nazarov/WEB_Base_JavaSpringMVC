package org.example.backend.service;

import org.example.backend.model.Addresses;

import java.util.Optional;

public interface AddressService {

    Optional<Addresses> getAddressById(Integer addressId);

    void deleteAddress(Integer addressId);
}
