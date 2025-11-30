package edu.tecnm.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

public class UploadFileHelper {

	public static String guardarArchivo(MultipartFile file, String ruta) {
	    // Validar que el archivo no esté vacío
	    if (file == null || file.isEmpty()) {
	        return null;
	    }

	    try {
	        // Limpia el nombre original y obtiene extensión
	        String original = StringUtils.cleanPath(file.getOriginalFilename());
	        String extension = "";
	        int i = original.lastIndexOf('.');
	        if (i >= 0) extension = original.substring(i).toLowerCase();

	        // Generar nombre único para evitar duplicados
	        String nombreArchivo = java.util.UUID.randomUUID().toString() + extension;

	        // Crear el directorio si no existe
	        Path uploadDir = Paths.get(ruta);
	        if (!Files.exists(uploadDir)) {
	            Files.createDirectories(uploadDir);
	        }

	        // Ruta completa del archivo
	        Path destino = uploadDir.resolve(nombreArchivo);

	        // Copiar el archivo (si existe, lo reemplaza)
	        Files.copy(file.getInputStream(), destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

	        System.out.println("[HELPER] Archivo guardado exitosamente: " + destino.toAbsolutePath());

	        // Retornar el nombre final que se guardará en la BD
	        return nombreArchivo;

	    } catch (IOException e) {
	        System.err.println("Error al guardar el archivo: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}

}