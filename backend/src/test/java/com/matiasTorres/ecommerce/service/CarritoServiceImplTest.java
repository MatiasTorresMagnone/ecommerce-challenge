package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.CarritoRequestDTO;
import com.matiasTorres.ecommerce.dto.CarritoResponseDTO;
import com.matiasTorres.ecommerce.exception.ItemNoEncontradoException;
import com.matiasTorres.ecommerce.exception.ProductoNoEncontradoException;
import com.matiasTorres.ecommerce.model.Carrito;
import com.matiasTorres.ecommerce.model.CarritoItem;
import com.matiasTorres.ecommerce.model.Producto;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.repository.CarritoItemRepo;
import com.matiasTorres.ecommerce.repository.CarritoRepo;
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
public class CarritoServiceImplTest {

    @Mock
    private CarritoRepo carritoRepo;

    @Mock
    private CarritoItemRepo carritoItemRepo;

    @Mock
    private ProductoRepo productoRepo;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario crearUsuario() {
        Usuario u = new Usuario();
        u.setIdUsuario(1L);
        u.setEmail("matias@test.com");
        return u;
    }

    private Usuario crearOtroUsuario() {
        Usuario u = new Usuario();
        u.setIdUsuario(2L);
        u.setEmail("otro@test.com");
        return u;
    }

    private Carrito crearCarrito(Usuario usuario) {
        Carrito c = new Carrito();
        c.setIdCarrito(10L);
        c.setUsuario(usuario);
        return c;
    }

    private Producto crearProducto(Long id, String nombre, String precio) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setNombre(nombre);
        p.setDescripcion("Desc " + nombre);
        p.setPrecio(new BigDecimal(precio));
        p.setImagen("img-" + id + ".jpg");
        p.setActivo(true);
        return p;
    }

    private CarritoItem crearItem(Long id, Carrito carrito, Producto producto, int cantidad) {
        CarritoItem i = new CarritoItem();
        i.setIdCarritoItem(id);
        i.setCarrito(carrito);
        i.setProducto(producto);
        i.setCantidad(cantidad);
        return i;
    }

    @Test
    @DisplayName("Ver carrito sin carrito creado devuelve lista vacía y total 0")
    void verCarrito_sinCarrito_devuelveVacioYTotalCero() {
        Usuario usuario = crearUsuario();
        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.empty());

        CarritoResponseDTO resultado = carritoService.verCarrito(usuario);

        assertTrue(resultado.items().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado.total()));
        verify(carritoItemRepo, never()).findByCarritoOrderByIdCarritoItemAsc(any());
    }

    @Test
    @DisplayName("Ver carrito con ítems, calcula subtotales y total")
    void verCarrito_conItems_calculaSubtotalesYTotal() {
        Usuario usuario = crearUsuario();
        Carrito carrito = crearCarrito(usuario);
        Producto zapatilla = crearProducto(1L, "Zapatilla", "100.00");
        Producto remera = crearProducto(2L, "Remera", "25.50");

        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepo.findByCarritoOrderByIdCarritoItemAsc(carrito)).thenReturn(List.of(
                crearItem(100L, carrito, zapatilla, 2),
                crearItem(101L, carrito, remera, 3)));

        CarritoResponseDTO resultado = carritoService.verCarrito(usuario);
        assertEquals(2, resultado.items().size());
        assertEquals(0, new BigDecimal("200.00").compareTo(resultado.items().get(0).subtotal()));
        assertEquals(0, new BigDecimal("76.50").compareTo(resultado.items().get(1).subtotal()));
        assertEquals(0, new BigDecimal("276.50").compareTo(resultado.total()));
    }

    @Test
    @DisplayName("Agregar producto sin carrito previo(crea el carrito)")
    void agregarProducto_carritoInexistente_creaCarrito() {
        Usuario usuario = crearUsuario();
        Carrito nuevo = crearCarrito(usuario);
        Producto producto = crearProducto(1L, "Zapatilla", "100.00");

        // primera llamada (agregar): no existe; luego verCarrito lo encuentra
        when(carritoRepo.findByUsuario(usuario))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(nuevo));
        when(carritoRepo.save(any(Carrito.class))).thenReturn(nuevo);
        when(productoRepo.findByIdProductoAndActivoTrue(1L)).thenReturn(Optional.of(producto));
        when(carritoItemRepo.findByCarritoAndProducto(nuevo, producto)).thenReturn(Optional.empty());
        when(carritoItemRepo.findByCarritoOrderByIdCarritoItemAsc(nuevo)).thenReturn(List.of());

        carritoService.agregarProducto(usuario, new CarritoRequestDTO(1L, 2));

        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepo).save(captor.capture());
        assertEquals(usuario, captor.getValue().getUsuario());
    }

    @Test
    @DisplayName("Agregar un producto nuevo con cantidad")
    void agregarProducto_productoNuevo_ConCantidad() {
        Usuario usuario = crearUsuario();
        Carrito carrito = crearCarrito(usuario);
        Producto producto = crearProducto(1L, "Zapatilla", "100.00");

        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(productoRepo.findByIdProductoAndActivoTrue(1L)).thenReturn(Optional.of(producto));
        when(carritoItemRepo.findByCarritoAndProducto(carrito, producto)).thenReturn(Optional.empty());
        when(carritoItemRepo.findByCarritoOrderByIdCarritoItemAsc(carrito)).thenReturn(List.of());

        carritoService.agregarProducto(usuario, new CarritoRequestDTO(1L, 2));

        ArgumentCaptor<CarritoItem> captor = ArgumentCaptor.forClass(CarritoItem.class);
        verify(carritoItemRepo).save(captor.capture());
        CarritoItem guardado = captor.getValue();
        assertEquals(2, guardado.getCantidad());
        assertEquals(producto, guardado.getProducto());
        assertEquals(carrito, guardado.getCarrito());
    }

    @Test
    @DisplayName("Agregar un producto que ya está en el carrito")
    void agregarProducto_productoExistente_sumaCantidad() {
        Usuario usuario = crearUsuario();
        Carrito carrito = crearCarrito(usuario);
        Producto producto = crearProducto(1L, "Zapatilla", "100.00");
        CarritoItem existente = crearItem(100L, carrito, producto, 2);

        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(productoRepo.findByIdProductoAndActivoTrue(1L)).thenReturn(Optional.of(producto));
        when(carritoItemRepo.findByCarritoAndProducto(carrito, producto)).thenReturn(Optional.of(existente));
        when(carritoItemRepo.findByCarritoOrderByIdCarritoItemAsc(carrito)).thenReturn(List.of(existente));

        carritoService.agregarProducto(usuario, new CarritoRequestDTO(1L, 3));

        assertEquals(5, existente.getCantidad());
        verify(carritoItemRepo).save(existente);
    }

    @Test
    @DisplayName("Agregar un producto inexistente o inactivo lanza excepción y no guarda")
    void agregarProducto_productoInexistenteOInactivo_lanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Carrito carrito = crearCarrito(usuario);

        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(productoRepo.findByIdProductoAndActivoTrue(99L)).thenReturn(Optional.empty());

        ProductoNoEncontradoException ex = assertThrows(
                ProductoNoEncontradoException.class,
                () -> carritoService.agregarProducto(usuario, new CarritoRequestDTO(99L, 1)));

        assertEquals("Producto no encontrado", ex.getMessage());
        verify(carritoItemRepo, never()).save(any(CarritoItem.class));
    }

    @Test
    @DisplayName("Modificar cantidad de un ítem propio reemplaza el valor")
    void modificarCantidad_itemPropio_reemplazaCantidad() {
        Usuario usuario = crearUsuario();
        Carrito carrito = crearCarrito(usuario);
        Producto producto = crearProducto(1L, "Zapatilla", "100.00");
        CarritoItem item = crearItem(100L, carrito, producto, 2);

        when(carritoItemRepo.findById(100L)).thenReturn(Optional.of(item));
        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepo.findByCarritoOrderByIdCarritoItemAsc(carrito)).thenReturn(List.of(item));

        CarritoResponseDTO resultado = carritoService.modificarCantidad(usuario, 100L, 5);

        assertEquals(5, item.getCantidad());
        verify(carritoItemRepo).save(item);
        assertEquals(0, new BigDecimal("500.00").compareTo(resultado.total()));
    }

    @Test
    @DisplayName("Modificar un ítem de otro usuario lanza excepción y no guarda")
    void modificarCantidad_itemAjeno_lanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Carrito carritoAjeno = crearCarrito(crearOtroUsuario());
        CarritoItem itemAjeno = crearItem(100L, carritoAjeno, crearProducto(1L, "Zapatilla", "100.00"), 2);

        when(carritoItemRepo.findById(100L)).thenReturn(Optional.of(itemAjeno));

        assertThrows(ItemNoEncontradoException.class,
                () -> carritoService.modificarCantidad(usuario, 100L, 5));

        assertEquals(2, itemAjeno.getCantidad());
        verify(carritoItemRepo, never()).save(any(CarritoItem.class));
    }

    @Test
    @DisplayName("Eliminar un ítem propio lo borra y devuelve el carrito actualizado")
    void eliminarItem_itemPropio_borraItem() {
        Usuario usuario = crearUsuario();
        Carrito carrito = crearCarrito(usuario);
        CarritoItem item = crearItem(100L, carrito, crearProducto(1L, "Zapatilla", "100.00"), 2);

        when(carritoItemRepo.findById(100L)).thenReturn(Optional.of(item));
        when(carritoRepo.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepo.findByCarritoOrderByIdCarritoItemAsc(carrito)).thenReturn(List.of());

        CarritoResponseDTO resultado = carritoService.eliminarItem(usuario, 100L);

        verify(carritoItemRepo).delete(item);
        assertTrue(resultado.items().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado.total()));
    }

    @Test
    @DisplayName("Eliminar un ítem de otro usuario lanza excepción y no borra nada")
    void eliminarItem_itemAjeno_lanzaExcepcionYNoBorra() {
        Usuario usuario = crearUsuario();
        Carrito carritoAjeno = crearCarrito(crearOtroUsuario());
        CarritoItem itemAjeno = crearItem(100L, carritoAjeno, crearProducto(1L, "Zapatilla", "100.00"), 2);

        when(carritoItemRepo.findById(100L)).thenReturn(Optional.of(itemAjeno));

        assertThrows(ItemNoEncontradoException.class,
                () -> carritoService.eliminarItem(usuario, 100L));

        verify(carritoItemRepo, never()).delete(any(CarritoItem.class));
    }

}
