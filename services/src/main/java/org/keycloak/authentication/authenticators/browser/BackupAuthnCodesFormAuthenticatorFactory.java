package org.keycloak.authentication.authenticators.browser;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.BackupAuthnCodesCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class BackupAuthnCodesFormAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "auth-backup-code-form";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Backup Authentication Code Form";
    }

    @Override
    public String getReferenceCategory() {
        return BackupAuthnCodesCredentialModel.TYPE;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return ConfigurableAuthenticatorFactory.REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Validates a Backup Authentication Code";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new BackupAuthnCodesFormAuthenticator(keycloakSession);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

}
