package org.ospreyphoto.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetitionSettings {
    static final Logger logger = LoggerFactory.getLogger(CompetitionSettings.class);

    @JsonProperty
    public String clubName;

    @JsonProperty
    public Map<String, Competition> competitions;

    @JsonProperty
    public List<Scoring> scoringSystems;

    public static CompetitionSettings defaults() {

        URL defaultJson = CompetitionSettings.class.getResource("defaultcomp.json");
        logger.info("Getting defautls from {}",defaultJson);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(defaultJson, CompetitionSettings.class);
        } catch (IOException e) {
            logger.error("Failed to open defaults", e);
            throw new RuntimeException(e);
        }

    }
}
