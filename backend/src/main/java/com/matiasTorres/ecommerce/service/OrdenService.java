package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.OrdenDetalleResponseDTO;
import com.matiasTorres.ecommerce.dto.OrdenResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;

import java.util.List;

public interface OrdenService {

    List<OrdenResponseDTO> verOrdenesUsuario(Usuario usuario);

    OrdenDetalleResponseDTO verOrdenDetalle(Usuario usuario, Long idOrden);
}
