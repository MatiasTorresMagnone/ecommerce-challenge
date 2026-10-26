package com.matiasTorres.ecommerce.storage;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageService {
    String guardar(MultipartFile archivo);
    Resource cargar(String nombre);
    void eliminar(String nombre);
}
