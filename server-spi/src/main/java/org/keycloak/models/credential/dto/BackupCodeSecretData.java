package org.keycloak.models.credential.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BackupCodeSecretData {

    private final Map<String, String> codes;

    @JsonCreator
    public BackupCodeSecretData(@JsonProperty("codes") Map<String, String> codes) {
        this.codes = codes;
    }

    public Map<String, String> getCodes() {
        return this.codes;
    }

    public List<Integer> remainingCodeNumbers() {
        return this.codes.keySet().stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public boolean hasCodes() {
        return !codes.isEmpty();
    }

    public String getCode(int number) {
        return codes.get(String.valueOf(number));
    }

    public void removeCode(int number) {
        this.codes.remove(String.valueOf(number));
    }

}
