package org.keycloak.credential;

public class CredentialMetadata {
    LocalizedMessage infoMessage;
    LocalizedMessage warningMessageTitle;
    LocalizedMessage warningMessageDescription;
    CredentialModel credentialModel;

    public CredentialModel getCredentialModel() {
        return credentialModel;
    }

    public void setCredentialModel(CredentialModel credentialModel) {
        this.credentialModel = credentialModel;
    }

    public LocalizedMessage getInfoMessage() {
        return infoMessage;
    }

    public LocalizedMessage getWarningMessageTitle() {
        return warningMessageTitle;
    }

    public LocalizedMessage getWarningMessageDescription() {
        return warningMessageDescription;
    }

    public void setWarningMessageTitle(String key, String... parameters) {
        LocalizedMessage message = new LocalizedMessage();
        message.key = key;
        message.parameters = parameters;
        this.warningMessageTitle = message;
    }

    public void setWarningMessageDescription(String key, String... parameters) {
        LocalizedMessage message = new LocalizedMessage();
        message.key = key;
        message.parameters = parameters;
        this.warningMessageDescription = message;
    }

    public void setInfoMessage(String key, String... parameters) {
        LocalizedMessage message = new LocalizedMessage();
        message.key = key;
        message.parameters = parameters;
        this.infoMessage = message;
    }

    class LocalizedMessage {
        String key;
        Object[] parameters; // Parameters of localized message. Something similar to class `FormMessage` from `keycloak-services` module

        public String getKey() {
            return key;
        }

        public Object[] getParameters() {
            return parameters;
        }
    }

}
