/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.models.utils;

import org.keycloak.common.util.Base64;
import org.keycloak.crypto.Algorithm;
import org.keycloak.crypto.JavaAlgorithm;
import org.keycloak.jose.jws.crypto.HashUtils;
import org.keycloak.utils.StringUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:anascime@redhat.com">Andre Nascimento</a>
 */
public class BackupAuthnCodesUtils {

    public static final int NUM_HASH_ITERATIONS = 1;
    public static final String NOM_ALGORITHM_TO_HASH = Algorithm.RS256;

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
}
