package com.example.Social_Media.Factory;

import com.example.Social_Media.Action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ActionFactory {
    private final Map<String, Action> actionMap;

    @Autowired
    public ActionFactory(ApplicationContext applicationContext) {
        this.actionMap = applicationContext.getBeansOfType(Action.class);
    }

    public Action getAction(String actionName) {
        return actionMap.get(actionName);
    }
}