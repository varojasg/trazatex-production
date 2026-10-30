package com.nodotextil.trazatex.production.application.dto;

public enum Role {
    LICENSE_OWNER,
    COMPANY_ADMIN,
    OPERATOR;

    /** Solo el Dueño de Licencia no pertenece a una empresa. */
    public boolean belongsToCompany() {
        return this != LICENSE_OWNER;
    }
}
