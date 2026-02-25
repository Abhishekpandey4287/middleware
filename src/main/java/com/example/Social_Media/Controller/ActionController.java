package com.example.Social_Media.Controller;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.DTO.ApiResponse;
import com.example.Social_Media.Factory.ActionFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/app")
public class ActionController {

    @Autowired
    private ActionFactory actionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/process", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse process(@RequestBody String json, HttpServletRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            if (!jsonNode.has("action")) {
                apiResponse.setStatus(false);
                apiResponse.setMessage("Missing 'action' field in request");
                return apiResponse;
            }

            String actionName = jsonNode.get("action").asText();

            Action actionImpl = actionFactory.getAction(actionName);

            if (actionImpl == null) {
                apiResponse.setStatus(false);
                apiResponse.setMessage("Unknown action: " + actionName);
                return apiResponse;
            }

            String response = actionImpl.handle(
                    jsonNode.has("User") ?
                            jsonNode.get("User").toString() :
                            jsonNode.toString()
            );

            JsonNode responseNode = objectMapper.readTree(response);

            // Check if response contains an error
            if (responseNode.has("error")) {
                apiResponse.setStatus(false);
                apiResponse.setMessage(responseNode.get("error").asText());
                apiResponse.setData(responseNode);
            } else {
                apiResponse.setStatus(true);
                apiResponse.setMessage("Processed successfully");
                apiResponse.setData(responseNode);
            }

        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return apiResponse;
    }
}