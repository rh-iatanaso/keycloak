package org.keycloak.models.utils;

import org.keycloak.common.util.Base64;
import org.keycloak.common.util.RandomString;
import org.keycloak.crypto.Algorithm;
import org.keycloak.crypto.JavaAlgorithm;
import org.keycloak.jose.jws.crypto.HashUtils;
import org.keycloak.utils.StringUtil;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BackupAuthnCodesUtils {

    public static final int QUANTITY_OF_BACKUP_AUTHN_CODES_TO_GENERATE = 15;
    private static final RandomString randomString = new RandomString(
            4,
            new SecureRandom(),
            RandomString.upper+RandomString.digits);
    public static final boolean SHOULD_SAVE_RAW_BACKUP_AUTHN_CODE = false;

    public static final String NOM_ALGORITHM_TO_HASH = Algorithm.RS512;
    public static final int NUM_HASH_ITERATIONS = 1;

    public static final String NAM_TEMPLATE_LOGIN_INPUT_BACKUP_AUTHN_CODE  = "login-input-backup-authn-code.ftl";
    public static final String NAM_TEMPLATE_LOGIN_CONFIG_BACKUP_AUTHN_CODE = "login-config-backup-authn-codes.ftl";
    public static final String BACKUP_AUTHN_CODES_INPUT_DEFAULT_ERROR_MESSAGE = "backup-codes-error-invalid";
    public static final String FIELD_BACKUP_CODE = "backupCode";

    public static String hashRawCode(String rawGeneratedCode) {
        String hashedCode = null;

        if (StringUtil.isNotBlank(rawGeneratedCode)) {

            byte[] rawCodeHashedAsBytes = HashUtils.hash(JavaAlgorithm.getJavaAlgorithmForHash(NOM_ALGORITHM_TO_HASH),
                                                         rawGeneratedCode.getBytes(StandardCharsets.UTF_8));

            if (rawCodeHashedAsBytes != null && rawCodeHashedAsBytes.length > 0) {
                hashedCode = Base64.encodeBytes(rawCodeHashedAsBytes);
            }
        }

        return hashedCode;
    }

    public static boolean verifyBackupCodeInput(String rawInputBackupCode, String hashedSavedBackupCode) {

        String hashedInputBackupCode = hashRawCode(rawInputBackupCode);

        return (hashedInputBackupCode.equals(hashedSavedBackupCode));
    }

    public static List<String> generateRawCodes() {
        return Stream.generate(BackupAuthnCodesUtils::newCode)
                     .limit(QUANTITY_OF_BACKUP_AUTHN_CODES_TO_GENERATE)
                     .collect(Collectors.toList());
    }

    private static String newCode() {
        return String.join("-",
                           randomString.nextString(),
                           randomString.nextString(),
                           randomString.nextString());
    }

}
