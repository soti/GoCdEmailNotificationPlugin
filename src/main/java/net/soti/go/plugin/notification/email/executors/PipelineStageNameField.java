package net.soti.go.plugin.notification.email.executors;

import net.soti.go.plugin.notification.email.model.Whitelist;

import org.apache.commons.lang3.StringUtils;

/**
 * User: wsim
 * Date: 2018-04-10
 */
public class PipelineStageNameField extends Field {
    public PipelineStageNameField(String key, String displayName, String defaultValue, boolean required, boolean secure, String displayOrder) {
        super(key, displayName, defaultValue, required, secure, displayOrder);
    }

    @Override
    public String doValidate(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        String[] items = input.split(",");

        for (String item : items) {
            Whitelist whitelist = Whitelist.getWhitelistItem(item);
            if (whitelist == null) {
                return this.displayName + " must be following format; 'PipelineName/StageName, PipelineName/StageName, ...' (Wildcard " +
                        "character for the beginning and the end of pipeline/stage name is allowed).";
            }
        }

        return null;
    }
}
