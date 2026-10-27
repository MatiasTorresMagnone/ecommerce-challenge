package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.CheckoutResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;

public interface CheckoutService {
    CheckoutResponseDTO iniciarCheckout(Usuario usuario);
}
