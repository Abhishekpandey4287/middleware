package com.example.Social_Media.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeData {
    private int likes;
    private Boolean isLiked;
}