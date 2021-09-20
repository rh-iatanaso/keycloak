package org.keycloak.forms.login.freemarker.model;

import org.keycloak.common.util.RandomString;
import org.keycloak.common.util.Time;
import org.keycloak.models.utils.BackupAuthnCodesUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BackupCodesBean {

    private final List<String> codes;
    private final long generatedAt;

    public BackupCodesBean() {
        this.codes = BackupAuthnCodesUtils.generateRawCodes();
        this.generatedAt = Time.currentTimeMillis();
    }

    public List<String> getCodes() {
        return codes;
    }

    public String getBackupCodesList() {
        return String.join(",", codes);
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

}
