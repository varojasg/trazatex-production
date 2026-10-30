package com.nodotextil.trazatex.production.application.port.out;

public interface EvidenceStoragePort {

    /**
     * @throws com.nodotextil.trazatex.production.application.exception.StorageNotAvailableException no se intentó (sin configurar, circuito abierto)
     * @throws com.nodotextil.trazatex.production.application.exception.StorageTransientException    respondió mal: reintentar luego
     * @throws com.nodotextil.trazatex.production.application.exception.StoragePermanentException    rechazó el archivo: no reintentar
     */
    UploadedImage upload(String publicId, String filename, String contentType, byte[] content);

    default void delete(String publicId) {
        throw new com.nodotextil.trazatex.production.application.exception.StorageNotAvailableException("El almacén no permite eliminar imágenes.");
    }

    record UploadedImage(String url) {
    }
}
