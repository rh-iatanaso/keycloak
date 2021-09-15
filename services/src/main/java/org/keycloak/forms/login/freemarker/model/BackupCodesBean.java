package org.keycloak.forms.login.freemarker.model;

import org.keycloak.common.util.Time;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BackupCodesBean {

    private static final int NUMBER_OF_CODES = 2;

    private static final int CODE_LENGTH = 12;

    private final List<String> codes;
    private final long generatedAt;

    public BackupCodesBean() {
        this.codes = Stream.generate(this::newCode).limit(NUMBER_OF_CODES).collect(Collectors.toList());
        this.generatedAt = Time.currentTimeMillis();
    }

    // TODO: Mostly stolen from elsewhere. Is there a better way to do this?
    private String newCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW1234567890";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            if (i != 0 && i % 4 == 0) {
                sb.append('-');
            }

            char c = chars.charAt(r.nextInt(chars.length()));
            sb.append(c);
        }

        return sb.toString();
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
