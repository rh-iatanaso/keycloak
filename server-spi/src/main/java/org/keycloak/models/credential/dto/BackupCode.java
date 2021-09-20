package org.keycloak.models.credential.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BackupCode {

    private final int number;
    private final String plainTextValue;
    private final String encodedHashedValue;

    @JsonCreator
    public BackupCode(@JsonProperty("number") int number,
                      @JsonProperty("plainTextValue") String plainTextValue,
                      @JsonProperty("encodedHashedValue") String encodedHashedValue) {
        this.number = number;
        this.plainTextValue = plainTextValue;
        this.encodedHashedValue = encodedHashedValue;
    }

    public int getNumber() {
        return this.number;
    }

    public String getPlainTextValue() {
        return this.plainTextValue;
    }

    public String getEncodedHashedValue() {
        return this.encodedHashedValue;
    }

}
