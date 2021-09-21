package org.keycloak.credential;

import org.keycloak.models.KeycloakSession;

public class BackupAuthnCodesCredentialProviderFactory implements CredentialProviderFactory<BackupAuthnCodesCredentialProvider> {

    public static final String PROVIDER_ID = "keycloak-backup-authn-codes";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public BackupAuthnCodesCredentialProvider create(KeycloakSession session) {
        return new BackupAuthnCodesCredentialProvider(session);
    }

}
