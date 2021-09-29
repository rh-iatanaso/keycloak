package org.keycloak.testsuite.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SetupBackupCodesPage extends LanguageComboboxAwarePage {

    @FindBy(id = "kc-backup-codes-list")
    private WebElement backupCodesList;

    @FindBy(id = "saveBackupCodesBtn")
    private WebElement saveBackupCodesButton;

    public void clickSaveBackupCodesButton() {
        saveBackupCodesButton.click();
    }

    public List<String> getBackupCodes() {
        String backupCodesText =  backupCodesList.getText();
        List<String> backupCodesList = new ArrayList<>();
        Scanner scanner = new Scanner(backupCodesText);
        while (scanner.hasNextLine()) {
            backupCodesList.add(scanner.nextLine());
        }
        scanner.close();
        return backupCodesList;
    }

    @Override
    public boolean isCurrent() {

        // Check the backup code text box and label available
        try {
            driver.findElement(By.id("kc-backup-codes-list"));
            driver.findElement(By.id("saveBackupCodesBtn"));
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
