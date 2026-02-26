package com.example.Social_Media.Service;

import com.example.Social_Media.Model.AppUpdateInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppUpdateService {

    private static final String REDIS_KEY = "app:android:update";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public AppUpdateInfo getUpdateInfo() {
        try {
            String json = redisTemplate.opsForValue().get(REDIS_KEY);
            if (json != null) {
                return objectMapper.readValue(json, AppUpdateInfo.class);
            }
        } catch (Exception e) {
            // fall through to default
        }
        return new AppUpdateInfo("1.0", 1, false, "A new version is available!",
                "https://play.google.com/store/apps/details?id=com.example.socialmediausingblockchain");
    }

    public void setUpdateInfo(AppUpdateInfo info) throws Exception {
        String json = objectMapper.writeValueAsString(info);
        redisTemplate.opsForValue().set(REDIS_KEY, json);
    }
}