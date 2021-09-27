package org.keycloak.models.credential;

import org.keycloak.credential.CredentialModel;
import org.keycloak.models.credential.dto.BackupAuthnCodeRepresentation;
import org.keycloak.models.credential.dto.BackupAuthnCodesCredentialData;
import org.keycloak.models.credential.dto.BackupAuthnCodesSecretData;
import org.keycloak.models.utils.BackupAuthnCodesUtils;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupAuthnCodesCredentialModel extends CredentialModel {

    public static final String TYPE = "backup-authn-codes";

    private final BackupAuthnCodesCredentialData credentialData;
    private final BackupAuthnCodesSecretData secretData;

    private BackupAuthnCodesCredentialModel(BackupAuthnCodesCredentialData credentialData,
                                            BackupAuthnCodesSecretData secretData) {
        this.credentialData = credentialData;
        this.secretData = secretData;
    }

    public BackupAuthnCodeRepresentation getNextBackupCode() {
        return this.secretData.getCodes().get(0);
    }

    public boolean allCodesUsed() {
        return this.secretData.getCodes().isEmpty();
    }

    public void removeBackupCode() {
        try {
            this.secretData.removeNextBackupCode();
            this.credentialData.setRemainingCodes(this.secretData.getCodes().size());

            this.setSecretData(JsonSerialization.writeValueAsString(this.secretData));
            this.setCredentialData(JsonSerialization.writeValueAsString(this.credentialData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BackupAuthnCodesCredentialModel createFromValues(String[] originalGeneratedCodes, long generatedAt) {

        BackupAuthnCodesSecretData secretData = new BackupAuthnCodesSecretData(toBackupCodes(originalGeneratedCodes));

        BackupAuthnCodesCredentialData credentialData = new BackupAuthnCodesCredentialData(
                BackupAuthnCodesUtils.NUM_HASH_ITERATIONS, BackupAuthnCodesUtils.NOM_ALGORITHM_TO_HASH,
                originalGeneratedCodes.length);

        BackupAuthnCodesCredentialModel model = new BackupAuthnCodesCredentialModel(credentialData, secretData);

        try {
            model.setCredentialData(JsonSerialization.writeValueAsString(credentialData));
            model.setSecretData(JsonSerialization.writeValueAsString(secretData));
            model.setCreatedDate(generatedAt);
            model.setType(TYPE);

            return model;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<BackupAuthnCodeRepresentation> toBackupCodes(String[] rawGeneratedCodes) {
        List<BackupAuthnCodeRepresentation> backupAuthnCodeRepresentations = new ArrayList<>();

        for (int i = 0; i < rawGeneratedCodes.length; i++) {
            backupAuthnCodeRepresentations.add(new BackupAuthnCodeRepresentation(i + 1,
                                               (BackupAuthnCodesUtils.SHOULD_SAVE_RAW_BACKUP_AUTHN_CODE ? rawGeneratedCodes[i] : null),
                                               BackupAuthnCodesUtils.hashRawCode(rawGeneratedCodes[i])));
        }

        return backupAuthnCodeRepresentations;
    }

    public static BackupAuthnCodesCredentialModel createFromCredentialModel(CredentialModel credentialModel) {

        try {
            BackupAuthnCodesCredentialData credentialData = JsonSerialization.readValue(credentialModel.getCredentialData(), BackupAuthnCodesCredentialData.class);
            BackupAuthnCodesSecretData secretData = JsonSerialization.readValue(credentialModel.getSecretData(), BackupAuthnCodesSecretData.class);

            BackupAuthnCodesCredentialModel newModel = new BackupAuthnCodesCredentialModel(credentialData, secretData);
            newModel.setUserLabel(credentialModel.getUserLabel());
            newModel.setCreatedDate(credentialModel.getCreatedDate());
            newModel.setType(TYPE);
            newModel.setId(credentialModel.getId());
            newModel.setSecretData(credentialModel.getSecretData());
            newModel.setCredentialData(credentialModel.getCredentialData());

            return newModel;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
