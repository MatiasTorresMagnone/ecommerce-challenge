package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.CarritoRequestDTO;
import com.matiasTorres.ecommerce.dto.CarritoResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;

public interface CarritoService {
    CarritoResponseDTO verCarrito(Usuario usuario);

    CarritoResponseDTO agregarProducto(Usuario usuario, CarritoRequestDTO request);

    CarritoResponseDTO modificarCantidad(Usuario usuario, Long idItem, int cantidad);

    CarritoResponseDTO eliminarItem(Usuario usuario, Long idItem);
}
