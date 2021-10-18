<#import "template.ftl" as layout>
<@layout.registrationLayout; section>

<#if section = "header">
    ${msg("recovery-code-config-header")}
<#elseif section = "form">
    <!-- warning -->
    <div class="pf-c-alert pf-m-warning ${properties.kcRecoveryCodesWarning}" aria-label="Warning alert">
        <div class="pf-c-alert__icon">
            <i class="pficon-warning-triangle-o" aria-hidden="true"></i>
        </div>
        <h4 class="pf-c-alert__title">
            <span class="pf-screen-reader">Warning alert:</span>
            ${msg("recovery-code-config-warning-title")}
        </h4>
        <div class="pf-c-alert__description">
            <p>${msg("recovery-code-config-warning-message")}</p>
        </div>
    </div>

    <ol id="kc-recovery-codes-list" class="${properties.kcRecoveryCodesList!}">
        <#list recoveryAuthnCodesConfigBean.generatedRecoveryAuthnCodesList as code>
            <li><span>${code?counter}:</span> ${code[0..3]}-${code[4..7]}-${code[8..]}</li>
        </#list>
    </ol>

    <!-- actions -->
    <div class="${properties.kcRecoveryCodesActions}">
        <button id="printRecoveryCodes" class="pf-c-button pf-m-link" type="button">
            <i class="pficon-print"></i> ${msg("recovery-codes-print")}
        </button>
        <button id="downloadRecoveryCodes" class="pf-c-button pf-m-link" type="button">
            <i class="pficon-save"></i> ${msg("recovery-codes-download")}
        </button>
        <button id="copyRecoveryCodes" class="pf-c-button pf-m-link" type="button">
            <i class="pficon-blueprint"></i> ${msg("recovery-codes-copy")}
            <div class="pf-c-tooltip pf-m-top" role="tooltip" aria-describedby="codes-copied">
                <div class="pf-c-tooltip__arrow"></div>
                <div class="pf-c-tooltip__content" id="codes-copied">${msg("recovery-codes-copied")}</div>
            </div>
        </button>
    </div>

    <!-- confirmation checkbox -->
    <div class="${properties.kcCheckClass} ${properties.kcRecoveryCodesConfirmation}">
        <input class="${properties.kcCheckInputClass}" type="checkbox" id="kcRecoveryCodesConfirmationCheck" name="kcRecoveryCodesConfirmationCheck" 
        onchange="document.getElementById('saveRecoveryAuthnCodesBtn').disabled = !this.checked;"
        />
        <label class="${properties.kcCheckLabelClass}" for="kcRecoveryCodesConfirmationCheck">${msg("recovery-codes-confirmation-message")}</label>
    </div>

    <form action="${url.loginAction}" class="${properties.kcFormClass!}" id="kc-recovery-codes-settings-form" method="post">
        <input type="hidden" name="generatedRecoveryAuthnCodes" value="${recoveryAuthnCodesConfigBean.generatedRecoveryAuthnCodesAsString}" />
        <input type="hidden" name="generatedAt" value="${recoveryAuthnCodesConfigBean.generatedAt?c}" />
        <input type="hidden" name="userLabel" value="${msg("recovery-code-config-user-label")}" />

        <#if isAppInitiatedAction??>
            <input type="submit"
            class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
            id="saveRecoveryAuthnCodesBtn" value="${msg("recovery-codes-action-complete")}"
            disabled
            />
            <button type="submit"
                class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!} ${properties.kcButtonLargeClass!}"
                id="cancelRecoveryAuthnCodesBtn" name="cancel-aia" value="true" />${msg("recovery-codes-action-cancel")}
            </button>
        <#else>
            <input type="submit"
            class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
            id="saveRecoveryAuthnCodesBtn" value="${msg("recovery-codes-action-complete")}"
            disabled
            />
        </#if>
    </form>

    <script>
        /* copy recovery codes  */
        function copyRecoveryCodes() {
            var tmpTextarea = document.createElement("textarea");
            var codes = document.getElementById("kc-recovery-codes-list").getElementsByTagName("li");
            for (i = 0; i < codes.length; i++) {
                tmpTextarea.value = tmpTextarea.value + codes[i].innerText + "\n";
            }
            document.body.appendChild(tmpTextarea);
            tmpTextarea.select();
            document.execCommand("copy");
            document.body.removeChild(tmpTextarea);
        }

        document.getElementById("copyRecoveryCodes").addEventListener('click', function () {
            copyRecoveryCodes();
            setTimeout(function() {
                console.log("copied");
            }, 1500);
        }

        /* download recovery codes  */
        function parseRecoveryCodeList() {
            var recoveryCodes = document.querySelectorAll(".kc-recovery-codes-list li");
            var recoveryCodeList = "";

            for (var i = 0; i < recoveryCodes.length; i++) {
                var recoveryCodeLiElement = recoveryCodes[i].innerText;
                recoveryCodeList += recoveryCodeLiElement + "\r\n";
            }

            return recoveryCodeList;
        }

        function buildFileContent() {
            var recoveryCodeList = parseRecoveryCodeList();
            var dt = new Date();
            var options = {
                month: 'long',
                day: 'numeric',
                year: 'numeric',
                hour: 'numeric',
                minute: 'numeric',
                timeZoneName: 'short'
            };

            return fileBodyContent =
                "${msg("recovery-codes-download-file-header")}\n\n" +
                recoveryCodeList + "\n" +
                "${msg("recovery-codes-download-file-description")}\n\n" +
                "${msg("recovery-codes-download-file-date")} " + dt.toLocaleString('en-US', options);
        }

        function setUpDownloadLinkAndDownload(filename, text) {
            var el = document.createElement('a');
            el.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
            el.setAttribute('download', filename);
            el.style.display = 'none';
            document.body.appendChild(el);
            el.click();
            document.body.removeChild(el);
        }

        function downloadRecoveryCodes() {
            setUpDownloadLinkAndDownload('kc-download-recovery-codes.txt', buildFileContent());
        }

        var downloadButton = document.getElementById("downloadRecoveryCodes");
        downloadButton && downloadButton.addEventListener("click", downloadRecoveryCodes);
    </script>
</#if>
</@layout.registrationLayout>
