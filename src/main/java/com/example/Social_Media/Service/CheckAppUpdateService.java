package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Model.AppUpdateInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("checkAppUpdate")
public class CheckAppUpdateService implements Action {

    @Autowired
    private AppUpdateService appUpdateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        AppUpdateInfo info = appUpdateService.getUpdateInfo();
        return objectMapper.writeValueAsString(info);
    }
}