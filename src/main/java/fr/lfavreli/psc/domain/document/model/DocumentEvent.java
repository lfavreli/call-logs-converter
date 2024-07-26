package fr.lfavreli.psc.domain.document.model;

import java.util.UUID;

public record DocumentEvent(UUID documentId, DocumentStatus status) {

    // Canonical constructor to ensure non-null status
    public DocumentEvent(UUID documentId, DocumentStatus status) {
        this.documentId = documentId;
        this.status = status != null ? status : DocumentStatus.IN_PROGRESS;
    }

    // Compact constructor to set default value for status
    public DocumentEvent(UUID documentId) {
        this(documentId, DocumentStatus.IN_PROGRESS);
    }

}
