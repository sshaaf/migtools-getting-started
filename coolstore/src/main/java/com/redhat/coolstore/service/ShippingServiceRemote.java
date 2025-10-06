package com.redhat.coolstore.service;

import com.redhat.coolstore.model.ShoppingCart;

public interface ShippingServiceRemote {
    double calculateShipping(ShoppingCart sc);
    double calculateShippingInsurance(ShoppingCart sc);
}
