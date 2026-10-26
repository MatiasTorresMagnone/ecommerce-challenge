package com.matiasTorres.ecommerce.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.matiasTorres.ecommerce.exception.ArchivoInvalidoException;
import com.matiasTorres.ecommerce.exception.ImagenNoEncontradaException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private static final long MAX_BYTES = 2 * 1024 * 1024;
    private static final Map<String, String> TIPOS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final Path directorio;

    public LocalStorageService(@Value("${app.storage.dir:./uploads}") String dir) {
        this.directorio = Path.of(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(directorio);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear el directorio de imágenes", e);
        }
    }

    @Override
    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoInvalidoException("El archivo está vacío");
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new ArchivoInvalidoException("El archivo supera el máximo de 2 MB");
        }
        String extension = TIPOS.get(archivo.getContentType());
        if (extension == null) {
            throw new ArchivoInvalidoException("Tipo no permitido. Usá JPEG, PNG o WEBP");
        }

        String nombre = UUID.randomUUID() + extension;
        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, directorio.resolve(nombre));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo", e);
        }
        return nombre;
    }

    @Override
    public Resource cargar(String nombre) {
        Path ruta = directorio.resolve(nombre).normalize();
        if (!ruta.startsWith(directorio) || !Files.isReadable(ruta)) {
            throw new ImagenNoEncontradaException("Imagen no encontrada");
        }
        return new FileSystemResource(ruta);
    }

    @Override
    public void eliminar(String nombre) {
        if (nombre == null) return;
        Path ruta = directorio.resolve(nombre).normalize();
        if (!ruta.startsWith(directorio)) return;
        try {
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
        }
    }
}