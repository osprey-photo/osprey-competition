package org.ospreyphoto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Competition {

    @JsonProperty
    public Scoring scoringSystem;

    @JsonProperty
    public String imageSrc;

    @JsonProperty
    public String[] competitionNames;

}
