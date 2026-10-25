package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.ProductoResponseDTO;
import com.matiasTorres.ecommerce.exception.ProductoNoEncontradoException;
import com.matiasTorres.ecommerce.model.Producto;
import com.matiasTorres.ecommerce.repository.ProductoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService{

    private final ProductoRepo productoRepo;

    @Override
    public List<ProductoResponseDTO> findAllActivas() {
        return productoRepo.findAllByActivoTrue().stream().map(this::toDTO).toList();
    }

    @Override
    public List<ProductoResponseDTO> findByNombre(String nombre) {
        List<Producto> productos = productoRepo.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        return productos.stream().map(this::toDTO).toList();
    }

    @Override
    public ProductoResponseDTO findById(Long id) {
        Producto producto = productoRepo.findByIdProductoAndActivoTrue(id)
                .orElseThrow(()->new ProductoNoEncontradoException("Producto no encontrado"));
        return toDTO(producto);
    }

    private ProductoResponseDTO toDTO(Producto p) {
        return new ProductoResponseDTO(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getImagen());
    }

}
