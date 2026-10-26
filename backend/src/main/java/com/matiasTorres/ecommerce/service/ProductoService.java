package com.matiasTorres.ecommerce.service;


import com.matiasTorres.ecommerce.dto.ProductoRequestAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> findAllActivas();

    List<ProductoResponseDTO> findByNombre(String nombre);

    ProductoResponseDTO findById(Long id);

    List<ProductoResponseAdminDTO> findAll();

    ProductoResponseAdminDTO agregarProductoAdmin(ProductoRequestAdminDTO producto);

    ProductoResponseAdminDTO modificarProductoAdmin(Long id, ProductoRequestAdminDTO productoModificado);

    ProductoResponseAdminDTO subirImagen(Long id, MultipartFile archivo);
}
