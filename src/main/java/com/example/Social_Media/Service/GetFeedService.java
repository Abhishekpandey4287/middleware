package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Repository.ContentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getFeed")
public class GetFeedService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        // Get latest content, paginated
        List<Content> feed = contentRepository.findAll(
                PageRequest.of(0, 20, Sort.by("id").descending())
        ).getContent();

        return objectMapper.writeValueAsString(feed);
    }
}
