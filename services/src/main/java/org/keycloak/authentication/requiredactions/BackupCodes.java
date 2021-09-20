package org.keycloak.authentication.requiredactions;

import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.credential.BackupCodeCredentialProviderFactory;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.events.Details;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.BackupCodeCredentialModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class BackupCodes implements RequiredActionProvider {

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
        MultivaluedMap<String, String> formDataMap;
        String[] generatedCodesFromFormArray;
        Long generatedAtTime;
        BackupCodeCredentialModel bkpCodeCredentialModel;
        CredentialProvider bkpCodeCredentialProvider;

        bkpCodeCredentialProvider = reqActionContext.getSession()
                                                    .getProvider(CredentialProvider.class,
                                                                 BackupCodeCredentialProviderFactory.PROVIDER_ID);

        reqActionContext.getEvent().detail(Details.CREDENTIAL_TYPE, BackupCodeCredentialModel.TYPE);

        formDataMap = reqActionContext.getHttpRequest().getDecodedFormParameters();
        generatedCodesFromFormArray = formDataMap.getFirst("backupCodes").split(",");
        generatedAtTime = Long.parseLong(formDataMap.getFirst("generatedAt"));

        bkpCodeCredentialModel = BackupCodeCredentialModel.createFromValues(generatedCodesFromFormArray,
                                                                            generatedAtTime);


        bkpCodeCredentialProvider.createCredential(reqActionContext.getRealm(),
                                                   reqActionContext.getUser(),
                                                   bkpCodeCredentialModel);

        reqActionContext.success();
    }

    @Override
    public void close() {
    }

}
