package org.keycloak.credential;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.BackupAuthnCodesCredentialModel;
import org.keycloak.models.utils.BackupAuthnCodesUtils;

import java.util.Optional;


public class BackupAuthnCodesCredentialProvider implements CredentialProvider<BackupAuthnCodesCredentialModel>, CredentialInputValidator {

    private static final Logger logger = Logger.getLogger(BackupAuthnCodesCredentialProvider.class);

    private final KeycloakSession session;

    BackupAuthnCodesCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public String getType() {
        return BackupAuthnCodesCredentialModel.TYPE;
    }

    @Override
    public CredentialModel createCredential(RealmModel realm, UserModel user, BackupAuthnCodesCredentialModel credentialModel) {
        session.userCredentialManager()
                .getStoredCredentialsByTypeStream(realm, user, getType())
                .findFirst()
                .ifPresent(model -> deleteCredential(realm, user, model.getId()));

        return session.userCredentialManager().createCredential(realm, user, credentialModel);
    }

    @Override
    public boolean deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        return session.userCredentialManager().removeStoredCredential(realm, user, credentialId);
    }

    @Override
    public BackupAuthnCodesCredentialModel getCredentialFromModel(CredentialModel model) {
        return BackupAuthnCodesCredentialModel.createFromCredentialModel(model);
    }

    @Override
    public CredentialTypeMetadata getCredentialTypeMetadata(CredentialTypeMetadataContext metadataContext) {
        CredentialTypeMetadata.CredentialTypeMetadataBuilder builder = CredentialTypeMetadata.builder()
                .type(getType())
                .category(CredentialTypeMetadata.Category.TWO_FACTOR)
                .displayName("backup-codes-display-name")
                .helpText("backup-codes-help-text")
                .iconCssClass("kcAuthenticatorBackupCodeClass")
                .removeable(true);

        UserModel user = metadataContext.getUser();

        if (user != null && !isConfiguredFor(session.getContext().getRealm(), user, getType())) {
            builder.createAction(UserModel.RequiredAction.CONFIGURE_BACKUP_CODES.name());
        }

        return builder.build(session);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return getType().equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return session.userCredentialManager()
                      .getStoredCredentialsByTypeStream(realm, user, credentialType)
                      .findAny()
                      .isPresent();
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        // TODO: Copied from elsewhere, is this even possible?
        if (!(credentialInput instanceof UserCredentialModel)) {
            logger.debug("Expected instance of UserCredentialModel");
            return false;
        }

        String rawInputBackupCode = credentialInput.getChallengeResponse();

        Optional<CredentialModel> credential = session.userCredentialManager()
                                                      .getStoredCredentialsByTypeStream(realm, user, getType())
                                                      .findFirst();

        if (!credential.isPresent()) {
            return false;
        }

        BackupAuthnCodesCredentialModel backupCodeCredentialModel = BackupAuthnCodesCredentialModel.createFromCredentialModel(credential.get());

        if (backupCodeCredentialModel.allCodesUsed()) {
            return false;
        }

        String hashedSavedBackupCode = backupCodeCredentialModel.getNextBackupCode().getEncodedHashedValue();

        if (BackupAuthnCodesUtils.verifyBackupCodeInput(rawInputBackupCode, hashedSavedBackupCode)) {
            backupCodeCredentialModel.removeBackupCode();
            session.userCredentialManager().updateCredential(realm, user, backupCodeCredentialModel);
            return true;
        }

        return false;
    }

}
