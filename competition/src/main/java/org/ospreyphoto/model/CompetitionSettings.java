package org.ospreyphoto.model;

import static org.ospreyphoto.model.Constants.FIRST;
import static org.ospreyphoto.model.Constants.HC;
import static org.ospreyphoto.model.Constants.SECOND;
import static org.ospreyphoto.model.Constants.THIRD;

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


    public static CompetitionSettings defaults(){
        var defaults = new CompetitionSettings();
        defaults.randomised = true;
        defaults.imageSrc = System.getProperty("user.home");
        defaults.orderedValueScores = List.of(FIRST, SECOND, THIRD, HC);
        defaults.numberScoresAvailable = Map.of(FIRST, 1, SECOND, 1, THIRD, 1, HC, 3);
        return defaults;
    }
}
