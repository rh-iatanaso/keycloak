package org.keycloak.testsuite.forms;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.keycloak.authentication.AuthenticationFlow;
import org.keycloak.authentication.authenticators.browser.BackupAuthnCodesFormAuthenticatorFactory;
import org.keycloak.authentication.authenticators.browser.PasswordFormFactory;
import org.keycloak.authentication.authenticators.browser.UsernameFormFactory;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.BackupAuthnCodesCredentialModel;
import org.keycloak.models.utils.BackupAuthnCodesUtils;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.client.KeycloakTestingClient;
import org.keycloak.testsuite.pages.*;
import org.keycloak.testsuite.util.FlowUtil;
import org.openqa.selenium.WebDriver;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * Backup Code Authentication test
 *
 * @author <a href="mailto:vnukala@redhat.com">Venkata Nukala</a>
 */
public class BackupCodesAuthenticationTest extends AbstractTestRealmKeycloakTest {

    private static final String BROWSER_FLOW_WITH_BACKUP_CODES = "Browser with Backup Codes";

    @Drone
    protected WebDriver driver;

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginUsernameOnlyPage loginUsernameOnlyPage;

    @Page
    protected EnterBackupCodePage enterBackupCodePage;

    @Page
    protected SelectAuthenticatorPage selectAuthenticatorPage;

    @Page
    protected PasswordPage passwordPage;

    @Page
    protected LandingPage landingPage;

    @Page
    protected AuthenticationMethodSetupPage authenticationMethodSetupPage;

    @Override
    public void configureTestRealm(RealmRepresentation testRealm) {

    }

    void configureBrowserFlowWithBackupCodes(KeycloakTestingClient testingClient) {
        final String newFlowAlias = BROWSER_FLOW_WITH_BACKUP_CODES;
        testingClient.server("test").run(session -> FlowUtil.inCurrentRealm(session).copyBrowserFlow(newFlowAlias));
        testingClient.server("test").run(session -> FlowUtil.inCurrentRealm(session)
                .selectFlow(newFlowAlias)
                .inForms(forms -> forms
                        .clear()
                        .addAuthenticatorExecution(AuthenticationExecutionModel.Requirement.REQUIRED, UsernameFormFactory.PROVIDER_ID)
                        .addSubFlowExecution(AuthenticationExecutionModel.Requirement.REQUIRED, reqSubFlow -> reqSubFlow
                                // Add authenticators to this flow: 1 PASSWORD, 2 Another subflow with having only OTP as child
                                .addAuthenticatorExecution(AuthenticationExecutionModel.Requirement.ALTERNATIVE, PasswordFormFactory.PROVIDER_ID)
                                .addSubFlowExecution("backup subflow", AuthenticationFlow.BASIC_FLOW, AuthenticationExecutionModel.Requirement.ALTERNATIVE, altSubFlow -> altSubFlow
                                        .addAuthenticatorExecution(AuthenticationExecutionModel.Requirement.REQUIRED, BackupAuthnCodesFormAuthenticatorFactory.PROVIDER_ID)
                                )
                        )
                )
                .defineAsBrowserFlow()
        );

        ApiUtil.removeUserByUsername(testRealm(), "test-user@localhost");

        String userId = createUser("test", "test-user@localhost", "password", UserModel.RequiredAction.CONFIGURE_BACKUP_CODES.name());
        //ApiUtil.createUserAndResetPasswordWithAdminClient(testRealm(), user, "password");
        setRequiredActionEnabled("test", userId, UserModel.RequiredAction.CONFIGURE_BACKUP_CODES.name(), true);
    }

    // In a sub-flow with alternative credential executors, test whether backup codes are working
    @Test
    public void testBackupCodes() {
        try {
            configureBrowserFlowWithBackupCodes(testingClient);
            loginUsernameOnlyPage.open();
            loginUsernameOnlyPage.assertAttemptedUsernameAvailability(false);
            loginUsernameOnlyPage.login("test-user@localhost");
            // On the password page, username should be shown as we know the user
            passwordPage.assertCurrent();
            passwordPage.assertAttemptedUsernameAvailability(true);
            Assert.assertEquals("test-user@localhost", passwordPage.getAttemptedUsername());
            passwordPage.assertTryAnotherWayLinkAvailability(true);
            List<String> backupCodes = BackupAuthnCodesUtils.generateRawCodes();
            testingClient.server().run(session -> {
                RealmModel realm = session.realms().getRealmByName("test");
                UserModel user = session.users().getUserByUsername(realm, "test-user@localhost");
                CredentialModel backupCred = BackupAuthnCodesCredentialModel.createFromValues(
                        backupCodes.stream().toArray(String[]::new),
                        System.currentTimeMillis(),
                        null);
                session.userCredentialManager().createCredential(realm, user, backupCred);
            });
            passwordPage.clickTryAnotherWayLink();
            selectAuthenticatorPage.assertCurrent();
            Assert.assertEquals(Arrays.asList(SelectAuthenticatorPage.PASSWORD, SelectAuthenticatorPage.BACKUP_CODES), selectAuthenticatorPage.getAvailableLoginMethods());
            selectAuthenticatorPage.selectLoginMethod(SelectAuthenticatorPage.BACKUP_CODES);
            enterBackupCodePage.assertCurrent();
            enterBackupCodePage.enterBackupCode(backupCodes.get(enterBackupCodePage.getBackupCodeToEnterNumber()));
            enterBackupCodePage.clickSignInButton();
            enterBackupCodePage.assertAccountLinkAvailability(true);
        } finally {
            // Remove save backup codes to keep a clean slate after this test
            enterBackupCodePage.assertAccountLinkAvailability(true);
            enterBackupCodePage.clickAccountLink();
            landingPage.assertCurrent();
            landingPage.clickSigningInLink();
            authenticationMethodSetupPage.assertCurrent();
            authenticationMethodSetupPage.clickRemoveBackupCodesLink();
            authenticationMethodSetupPage.clickConfirmButton();
            authenticationMethodSetupPage.assertCurrent();
            // Revert copy of browser flow to original to keep clean slate after this test
            BrowserFlowTest.revertFlows(testRealm(), BROWSER_FLOW_WITH_BACKUP_CODES);
        }
    }

}
