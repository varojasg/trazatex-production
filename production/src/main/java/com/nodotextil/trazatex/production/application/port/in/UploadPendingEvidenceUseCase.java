package com.nodotextil.trazatex.production.application.port.in;

public interface UploadPendingEvidenceUseCase {

    /** @return cuántas evidencias se procesaron (subidas o no) en esta pasada */
    int uploadDue();
}
