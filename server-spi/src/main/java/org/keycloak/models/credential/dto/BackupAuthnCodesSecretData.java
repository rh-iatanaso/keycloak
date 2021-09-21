package org.keycloak.models.credential.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BackupAuthnCodesSecretData {

    private final List<BackupAuthnCodeRepresentation> codes;

    @JsonCreator
    public BackupAuthnCodesSecretData(@JsonProperty("codes") List<BackupAuthnCodeRepresentation> codes) {
        this.codes = codes;
    }

    public List<BackupAuthnCodeRepresentation> getCodes() {
        return this.codes;
    }

    public void removeNextBackupCode() {
        this.codes.remove(0);
    }

}
