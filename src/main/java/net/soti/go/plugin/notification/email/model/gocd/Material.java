package net.soti.go.plugin.notification.email.model.gocd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import net.soti.go.plugin.notification.email.model.MaterialType;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class Material {
    @Expose
    @SerializedName("type")
    public MaterialType type;

    @Expose
    @SerializedName("description")
    public String description;

    private static final String SOTITFS = "SOTITFS";
    private static final String PACKAGE = "Package";
    private static final String REPOSITORY = "Repository";
    private static final String PIPELINE = "SimplePipeline";
    private static final String URL = "URL";
    private static final String PROJECT_PATH = "ProjectPath";
    private static final String PACKAGE_ENTRY_SPLITTER = " - ";
    private static final String SOURCE_CONTROL_ENTRY_SPLITTER = ", ";
    private static final String ITEM_SPLITTER = ": ";
    private static final String EQUAL_SPLITTER = "=";

    public boolean isPipeline() {
        return type.equals(PIPELINE);
    }

    public String getName() {
        String name;
        switch (type) {
            case Tfs:
                name = getDescriptionItem(description, SOURCE_CONTROL_ENTRY_SPLITTER, ITEM_SPLITTER, PROJECT_PATH);
                break;
            case Git:
                name = getDescriptionItem(description, SOURCE_CONTROL_ENTRY_SPLITTER, ITEM_SPLITTER, URL);
                name = name.substring(name.lastIndexOf('/') + 1);
                break;
            case Package:
                name = getDescriptionItem(description, PACKAGE_ENTRY_SPLITTER, ITEM_SPLITTER, PACKAGE);
                break;
            default:
                name = description;
        }
        return name;
    }

    public String getBaseUrl() {
        String result = "";
        switch (type) {
            case Tfs:
                result = getDescriptionItem(description, SOURCE_CONTROL_ENTRY_SPLITTER, ITEM_SPLITTER, URL);
                String project = getName().replace("$/", "").split("/")[0];
                result = String.format("%s/%s/%s/_versionControl/changeset", result, SOTITFS, project);
                break;
            case Git:
                String urlString = getDescriptionItem(description, SOURCE_CONTROL_ENTRY_SPLITTER, ITEM_SPLITTER, URL);
                try {
                    URL url = new URL(urlString);
                    result = urlString.replace(String.format("%s@", url.getUserInfo()), "");
                    result = String.format("%s/commit", result);
                } catch (MalformedURLException e) {
                    result = "";
                }
                break;
            case Package:
                String repository = getDescriptionItem(description, PACKAGE_ENTRY_SPLITTER, ITEM_SPLITTER, REPOSITORY);
                String repoUrl = getDescriptionItem(repository, SOURCE_CONTROL_ENTRY_SPLITTER, EQUAL_SPLITTER, "repo_url");
                String pkg = getDescriptionItem(description, PACKAGE_ENTRY_SPLITTER, ITEM_SPLITTER, PACKAGE);
                String repoId = getDescriptionItem(pkg, SOURCE_CONTROL_ENTRY_SPLITTER, EQUAL_SPLITTER, "repo_id");
                String pkgPath = getDescriptionItem(pkg, SOURCE_CONTROL_ENTRY_SPLITTER, EQUAL_SPLITTER, "package_path");
                result = String.format("%s/%s/%s", repoUrl, repoId, pkgPath);
                break;
        }

        return result;
    }

    private String getDescriptionItem(String input, String entrySplitter, String itemSplitter, String key) {
        Optional<String> value = Arrays.stream(input.trim().split(entrySplitter))
                .map(entry -> entry.split(itemSplitter))
                .filter(items -> {
                    return items.length == 2 && items[0].equalsIgnoreCase(key);
                })
                .map(items -> items[1].trim())
                .findFirst();
        return value.orElse(description).replace("[", "").replace("]", "");
    }
}