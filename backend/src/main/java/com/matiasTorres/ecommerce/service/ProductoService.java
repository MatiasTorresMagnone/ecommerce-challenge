package com.matiasTorres.ecommerce.service;


import com.matiasTorres.ecommerce.dto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> findAllActivas();

    List<ProductoResponseDTO> findByNombre(String nombre);

    ProductoResponseDTO findById(Long id);
}
