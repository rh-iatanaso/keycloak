package org.keycloak.forms.login.freemarker.model;

import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.BackupAuthnCodesCredentialModel;

public class BackupAuthnCodeInputLoginBean {

    private final int codeNumber;

    public BackupAuthnCodeInputLoginBean(KeycloakSession session, RealmModel realm, UserModel user) {
        CredentialModel credentialModel = session.userCredentialManager()
                                                 .getStoredCredentialsByTypeStream(realm,
                                                                                   user,
                                                                                   BackupAuthnCodesCredentialModel.TYPE)
                                                 .findFirst().get();

        BackupAuthnCodesCredentialModel backupCodeCredentialModel = BackupAuthnCodesCredentialModel.createFromCredentialModel(credentialModel);

        this.codeNumber = backupCodeCredentialModel.getNextBackupCode().getNumber();
    }

    public int getCodeNumber() {
        return this.codeNumber;
    }

}
