package org.ospreyphoto.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetitionSettings  {
    @JsonProperty
    public List<String> orderedValueScores;

    @JsonProperty
    public Map<String,Integer> numberScoresAvailable;

    @JsonProperty
    public boolean randomised;

    @JsonProperty
    public String imageSrc;

}
