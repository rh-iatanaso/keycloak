package org.keycloak.testsuite.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.testsuite.util.DroneUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Signing In Page with required action "Enter Backup Code for authentication"
 *
 * @author <a href="mailto:vnukala@redhat.com">Venkata Nukala</a>
 */
public class EnterBackupCodePage extends LanguageComboboxAwarePage {

    @FindBy(xpath = "//label[@for='backupCode']")
    private WebElement backupCodeLabel;

    @FindBy(id = "backupCode")
    private WebElement backupCodeTextField;

    @FindBy(id = "kc-login")
    private WebElement signInButton;

    public int getBackupCodeToEnterNumber() {
        String [] backupCodeLabelParts = backupCodeLabel.getText().split("#");
        return Integer.valueOf(backupCodeLabelParts[1]) - 1; // Backup code 1 is at element 0 in the list
    }

    public void enterBackupCode(String backupCode) {
        backupCodeTextField.sendKeys(backupCode);
    }

    public void clickSignInButton() {
        signInButton.click();
    }

    @Override
    public boolean isCurrent() {

        // Check the backup code text box and label available
        try {
            driver.findElement(By.id("backupCode"));
            driver.findElement(By.xpath("//label[@for='backupCode']"));
        } catch (NoSuchElementException nfe) {
            return false;
        }

        return true;
    }

    @Override
    public void open() throws Exception {
        throw new UnsupportedOperationException();
    }
}
