package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.ProductoRequestAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseDTO;
import com.matiasTorres.ecommerce.exception.ProductoNoEncontradoException;
import com.matiasTorres.ecommerce.exception.ProductoYaExisteException;
import com.matiasTorres.ecommerce.model.Producto;
import com.matiasTorres.ecommerce.repository.ProductoRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepo productoRepo;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto crearProducto(Long id, String nombre, String precio) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setNombre(nombre);
        p.setDescripcion("Descripción de " + nombre);
        p.setPrecio(new BigDecimal(precio));
        p.setImagen("img-" + id + ".jpg");
        p.setActivo(true);
        return p;
    }

    @Test
    @DisplayName("Lista los productos activos mapeados a DTO")
    void findAllActivas_conProductos_devuelveDTOs() {
        when(productoRepo.findAllByActivoTrue()).thenReturn(List.of(
                crearProducto(1L, "Zapatilla", "100.00"),
                crearProducto(2L, "Remera", "25.50")));

        List<ProductoResponseDTO> resultado = productoService.findAllActivas();

        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).id());
        assertEquals("Zapatilla", resultado.get(0).nombre());
        assertEquals(0, new BigDecimal("25.50").compareTo(resultado.get(1).precio()));
    }

    @Test
    @DisplayName("Sin productos activos devuelve lista vacía")
    void findAllActivas_sinProductos_devuelveListaVacia() {
        when(productoRepo.findAllByActivoTrue()).thenReturn(List.of());

        assertTrue(productoService.findAllActivas().isEmpty());
    }

    @Test
    @DisplayName("Buscar por nombre devuelve las coincidencias")
    void findByNombre_conCoincidencias_devuelveDTOs() {
        when(productoRepo.findByNombreContainingIgnoreCaseAndActivoTrue("zapa"))
                .thenReturn(List.of(crearProducto(1L, "Zapatilla", "100.00")));

        List<ProductoResponseDTO> resultado = productoService.findByNombre("zapa");

        assertEquals(1, resultado.size());
        assertEquals("Zapatilla", resultado.get(0).nombre());
        verify(productoRepo).findByNombreContainingIgnoreCaseAndActivoTrue("zapa");
    }

    @Test
    @DisplayName("Buscar por nombre sin coincidencias devuelve lista vacía")
    void findByNombre_sinCoincidencias_devuelveListaVacia() {
        when(productoRepo.findByNombreContainingIgnoreCaseAndActivoTrue("xyz"))
                .thenReturn(List.of());

        assertTrue(productoService.findByNombre("xyz").isEmpty());
    }

    @Test
    @DisplayName("Buscar por id un producto activo devuelve el DTO")
    void findById_productoActivo_devuelveDTO() {
        when(productoRepo.findByIdProductoAndActivoTrue(1L))
                .thenReturn(Optional.of(crearProducto(1L, "Zapatilla", "100.00")));

        ProductoResponseDTO resultado = productoService.findById(1L);

        assertEquals(1L, resultado.id());
        assertEquals("Zapatilla", resultado.nombre());
        assertEquals("img-1.jpg", resultado.imagen());
    }

    @Test
    @DisplayName("Buscar por id un producto inexistente o inactivo lanza excepción")
    void findById_inexistenteOInactivo_lanzaExcepcion() {
        when(productoRepo.findByIdProductoAndActivoTrue(99L))
                .thenReturn(Optional.empty());

        ProductoNoEncontradoException ex = assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.findById(99L));

        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Agregar producto guarda los datos correctos")
    void agregarProductoAdmin() {
        ProductoRequestAdminDTO dto = new ProductoRequestAdminDTO("Zapatilla", "Liviana", new BigDecimal("100.00"), false);
        when(productoRepo.existsByNombreIgnoreCase("Zapatilla")).thenReturn(false);
        when(productoRepo.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setIdProducto(1L);
            return p;
        });

        ProductoResponseAdminDTO resultado = productoService.agregarProductoAdmin(dto);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepo).save(captor.capture());
        assertEquals("Zapatilla", captor.getValue().getNombre());
        assertFalse(captor.getValue().isActivo());
        assertEquals(1L, resultado.id());
        assertFalse(resultado.activo());
    }

    @Test
    @DisplayName("Agregar un producto con nombre repetido lanza excepción y no guarda")
    void agregarProductoAdmin_nombreRepetido_lanzaExcepcionYNoGuarda() {
        ProductoRequestAdminDTO dto = new ProductoRequestAdminDTO(
                "Zapatilla", "Liviana", new BigDecimal("100.00"), true);
        when(productoRepo.existsByNombreIgnoreCase("Zapatilla")).thenReturn(true);

        assertThrows(ProductoYaExisteException.class,
                () -> productoService.agregarProductoAdmin(dto));

        verify(productoRepo, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Modificar producto actualiza correctamente")
    void modificarProductoAdmin() {
        Producto existente = crearProducto(1L, "Zapatilla", "100.00");
        ProductoRequestAdminDTO dto = new ProductoRequestAdminDTO(
                "Zapatilla Pro", "Nueva desc", new BigDecimal("150.00"), false);
        when(productoRepo.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepo.existsByNombreIgnoreCase("Zapatilla Pro")).thenReturn(false);
        when(productoRepo.save(existente)).thenReturn(existente);

        ProductoResponseAdminDTO resultado = productoService.modificarProductoAdmin(1L, dto);

        assertEquals("Zapatilla Pro", existente.getNombre());
        assertEquals("Nueva desc", existente.getDescripcion());
        assertEquals(0, new BigDecimal("150.00").compareTo(existente.getPrecio()));
        assertFalse(existente.isActivo());
        assertEquals("Zapatilla Pro", resultado.nombre());
    }

    @Test
    @DisplayName("Modificar un producto inexistente lanza excepción")
    void modificarProductoAdmin_inexistente_lanzaExcepcion() {
        ProductoRequestAdminDTO dto = new ProductoRequestAdminDTO(
                "Zapatilla", "Liviana", new BigDecimal("100.00"), true);
        when(productoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.modificarProductoAdmin(99L, dto));

        verify(productoRepo, never()).save(any(Producto.class));
    }




}