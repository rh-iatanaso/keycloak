package org.keycloak.models.credential;

import org.keycloak.credential.CredentialModel;
import org.keycloak.models.credential.dto.BackupCodeCredentialData;
import org.keycloak.models.credential.dto.BackupCodeSecretData;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackupCodeCredentialModel extends CredentialModel {

    public static final String TYPE = "backup-code";

    private final BackupCodeCredentialData credentialData;
    private final BackupCodeSecretData secretData;

    private BackupCodeCredentialModel(BackupCodeCredentialData credentialData, BackupCodeSecretData secretData) {
        this.credentialData = credentialData;
        this.secretData = secretData;
    }

    public List<Integer> getRemainingCodeNumbers() {
        return this.secretData.remainingCodeNumbers();
    }

    public String getCode(int number) {
        return this.secretData.getCode(number);
    }

    public boolean hasCodes() {
        return this.secretData.hasCodes();
    }

    public void removeBackupCode(int number) {
        try {
            this.secretData.removeCode(number);

            this.setSecretData(JsonSerialization.writeValueAsString(this.secretData));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BackupCodeCredentialModel createFromValues(String[] codes, long generatedAt, int hashIterations, String algorithm) {
        BackupCodeSecretData secretData = new BackupCodeSecretData(asMap(codes));
        BackupCodeCredentialData credentialData = new BackupCodeCredentialData(hashIterations, algorithm);

        BackupCodeCredentialModel model = new BackupCodeCredentialModel(credentialData, secretData);

        try {
            model.setCredentialData(JsonSerialization.writeValueAsString(credentialData));
            model.setSecretData(JsonSerialization.writeValueAsString(secretData));
            model.setCreatedDate(generatedAt);
            model.setType(TYPE);

            return model;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> asMap(String[] codes) {
        Map<String, String> codeMap = new HashMap<>();

        for(int i = 0; i < codes.length; i++) {
            codeMap.put(String.valueOf(i + 1), codes[i]);
        }
        return codeMap;
    }

    public static BackupCodeCredentialModel createFromCredentialModel(CredentialModel credentialModel) {
        try {
            BackupCodeCredentialData credentialData = JsonSerialization.readValue(credentialModel.getCredentialData(), BackupCodeCredentialData.class);
            BackupCodeSecretData secretData = JsonSerialization.readValue(credentialModel.getSecretData(), BackupCodeSecretData.class);

            BackupCodeCredentialModel newModel = new BackupCodeCredentialModel(credentialData, secretData);
            newModel.setUserLabel(credentialModel.getUserLabel());
            newModel.setCreatedDate(credentialModel.getCreatedDate());
            newModel.setType(TYPE);
            newModel.setId(credentialModel.getId());
            newModel.setSecretData(credentialModel.getSecretData());
            newModel.setCredentialData(credentialModel.getCredentialData());

            return newModel;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
