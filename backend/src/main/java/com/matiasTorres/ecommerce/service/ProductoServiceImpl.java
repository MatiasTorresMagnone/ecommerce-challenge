package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.ProductoRequestAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseDTO;
import com.matiasTorres.ecommerce.exception.ProductoNoEncontradoException;
import com.matiasTorres.ecommerce.exception.ProductoYaExisteException;
import com.matiasTorres.ecommerce.model.Producto;
import com.matiasTorres.ecommerce.repository.ProductoRepo;
import com.matiasTorres.ecommerce.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService{

    private final ProductoRepo productoRepo;
    private final StorageService storageService;

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

    @Override
    public List<ProductoResponseAdminDTO> findAll() {
        return productoRepo.findAll().stream().map(this::toAdminDTO).toList();
    }

    @Override
    @Transactional
    public ProductoResponseAdminDTO agregarProductoAdmin(ProductoRequestAdminDTO producto) {
        if (productoRepo.existsByNombreIgnoreCase(producto.nombre())) {
            throw new ProductoYaExisteException("Ya existe un producto con ese nombre");
        }
        Producto productoNuevo = new Producto();
        productoNuevo.setNombre(producto.nombre());
        productoNuevo.setDescripcion(producto.descripcion());
        productoNuevo.setPrecio(producto.precio());
        productoNuevo.setActivo(producto.activo());

        return toAdminDTO(productoRepo.save(productoNuevo));
    }

    @Override
    @Transactional
    public ProductoResponseAdminDTO modificarProductoAdmin(Long id, ProductoRequestAdminDTO productoModificado) {
        Producto producto = productoRepo.findById(id)
                .orElseThrow(()->new ProductoNoEncontradoException("Producto no encontrado"));

        if (!productoModificado.nombre().equalsIgnoreCase(producto.getNombre()) && productoRepo.existsByNombreIgnoreCase(productoModificado.nombre())) {
            throw new ProductoYaExisteException("Ya existe un producto con ese nombre");
        }

        producto.setNombre(productoModificado.nombre());
        producto.setDescripcion(productoModificado.descripcion());
        producto.setPrecio(productoModificado.precio());
        producto.setActivo(productoModificado.activo());

        return toAdminDTO(productoRepo.save(producto));
    }

    @Override
    @Transactional
    public ProductoResponseAdminDTO subirImagen(Long id, MultipartFile archivo) {
        Producto producto = productoRepo.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        String anterior = producto.getImagen();
        producto.setImagen(storageService.guardar(archivo));
        Producto guardado = productoRepo.save(producto);

        storageService.eliminar(anterior);
        return toAdminDTO(guardado);
    }


    private ProductoResponseDTO toDTO(Producto p) {
        return new ProductoResponseDTO(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getImagen());
    }

    private ProductoResponseAdminDTO toAdminDTO(Producto p) {
        return new ProductoResponseAdminDTO(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getImagen(),
                p.isActivo()
        );
    }



}
