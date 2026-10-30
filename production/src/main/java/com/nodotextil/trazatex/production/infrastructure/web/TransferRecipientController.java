package com.nodotextil.trazatex.production.infrastructure.web;

import com.nodotextil.trazatex.production.application.dto.Actor;
import com.nodotextil.trazatex.production.application.dto.Role;
import com.nodotextil.trazatex.production.infrastructure.web.security.RequiresRole;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
class TransferRecipientController {
    record Recipient(UUID id,String businessName) { }
    private final JdbcClient jdbc;
    TransferRecipientController(JdbcClient jdbc) { this.jdbc=jdbc; }

    @GetMapping("/api/transfers/recipients")
    @RequiresRole({Role.OPERATOR,Role.COMPANY_ADMIN})
    List<Recipient> recipients(Actor actor) {
        return jdbc.sql("SELECT company_id,business_name FROM company_projection WHERE status='ACTIVE' ORDER BY business_name")
                .query((rs,n) -> new Recipient(rs.getObject("company_id",UUID.class),rs.getString("business_name"))).list();
    }
}

