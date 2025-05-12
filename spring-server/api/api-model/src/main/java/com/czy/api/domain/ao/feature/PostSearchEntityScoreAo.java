package com.czy.api.domain.ao.feature;


import lombok.Data;

@Data
public class PostSearchEntityScoreAo {
    private String entityLabel;
    private String entityName;
    private Double score;
}
