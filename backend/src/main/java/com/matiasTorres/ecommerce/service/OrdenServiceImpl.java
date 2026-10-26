package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.OrdenDetalleResponseDTO;
import com.matiasTorres.ecommerce.dto.OrdenResponseDTO;
import com.matiasTorres.ecommerce.dto.ProductoOrdenDTO;
import com.matiasTorres.ecommerce.exception.OrdenNoEncontradaException;
import com.matiasTorres.ecommerce.model.Orden;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.repository.OrdenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService{

    private final OrdenRepo ordenRepo;

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> verOrdenesUsuario(Usuario usuario) {
        return ordenRepo.findByUsuarioOrderByFechaCreacionDesc(usuario).stream().map(orden-> new OrdenResponseDTO(
                orden.getIdOrden(),
                orden.getTotal(),
                orden.getEstado(),
                orden.getFechaCreacion()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenDetalleResponseDTO verOrdenDetalle(Usuario usuario, Long idOrden) {
        Orden orden = ordenRepo.findByIdOrdenAndUsuario(idOrden, usuario)
                .orElseThrow(() -> new OrdenNoEncontradaException("Orden no encontrada"));

        List<ProductoOrdenDTO> productos = orden.getDetalles().stream()
                .map(d -> new ProductoOrdenDTO(
                        d.getProducto().getIdProducto(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();

        return new OrdenDetalleResponseDTO(
                orden.getIdOrden(), orden.getEstado(),
                orden.getFechaCreacion(), orden.getTotal(), productos);
    }


}
