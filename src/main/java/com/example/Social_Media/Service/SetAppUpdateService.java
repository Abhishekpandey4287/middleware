package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Model.AppUpdateInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("setAppUpdate")
public class SetAppUpdateService implements Action {

    @Autowired
    private AppUpdateService appUpdateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        // Reusing existing UniversalTaskRequest fields:
        // title        → latestVersion (e.g. "1.1")
        // amount       → latestVersionCode (e.g. 2)
        // description  → updateMessage
        // videoUrl     → playStoreUrl
        // text         → forceUpdate ("true"/"false")

        String latestVersion  = node.has("title")       ? node.get("title").asText()            : "1.0";
        int versionCode       = node.has("amount")      ? (int) node.get("amount").asDouble()   : 1;
        String message        = node.has("description") ? node.get("description").asText()      : "Update available";
        String playStoreUrl   = node.has("videoUrl")    ? node.get("videoUrl").asText()         : "";
        boolean forceUpdate   = node.has("text") && Boolean.parseBoolean(node.get("text").asText());

        AppUpdateInfo info = new AppUpdateInfo(latestVersion, versionCode, forceUpdate, message, playStoreUrl);
        appUpdateService.setUpdateInfo(info);

        return "{\"result\": \"Update info saved successfully\"}";
    }
}