package org.keycloak.authentication.requiredactions;

import org.keycloak.Config;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.credential.BackupAuthnCodesCredentialProviderFactory;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.events.Details;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.BackupAuthnCodesCredentialModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class BackupAuthnCodesAction implements RequiredActionProvider, RequiredActionFactory {

    public static final String PROVIDER_ID = UserModel.RequiredAction.CONFIGURE_BACKUP_CODES.name();
    private static final BackupAuthnCodesAction INSTANCE = new BackupAuthnCodesAction();

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayText() {
        return "Backup Authentication Codes";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public boolean isOneTimeAction() {
        return true;
    }


    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        Response challenge = context.form()
                                    .createResponse(UserModel.RequiredAction.CONFIGURE_BACKUP_CODES);
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext reqActionContext) {
        CredentialProvider bkpCodeCredentialProvider = reqActionContext
                .getSession()
                .getProvider(
                        CredentialProvider.class,
                        BackupAuthnCodesCredentialProviderFactory.PROVIDER_ID);

        reqActionContext.getEvent().detail(Details.CREDENTIAL_TYPE, BackupAuthnCodesCredentialModel.TYPE);

        MultivaluedMap<String,String> formDataMap = reqActionContext.getHttpRequest().getDecodedFormParameters();
        // TODO Validation iatanaso
        String[] generatedCodesFromFormArray = formDataMap.getFirst("backupCodes").split(",");
        // TODO Validation iatanaso
        Long generatedAtTime = Long.parseLong(formDataMap.getFirst("generatedAt"));
        // TODO Validation iatanaso
        String generatedUserLabel = formDataMap.getFirst("userLabel");
        BackupAuthnCodesCredentialModel bkpCodeCredentialModel = BackupAuthnCodesCredentialModel.createFromValues(
                generatedCodesFromFormArray,
                generatedAtTime,
                generatedUserLabel);
        bkpCodeCredentialProvider.createCredential(reqActionContext.getRealm(),
                                                   reqActionContext.getUser(),
                                                   bkpCodeCredentialModel);

        reqActionContext.success();
    }

    @Override
    public void close() {
    }

}
