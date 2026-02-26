package com.example.Social_Media.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUpdateInfo {
    private String latestVersion;
    private int latestVersionCode;
    private boolean forceUpdate;
    private String updateMessage;
    private String playStoreUrl;
}