package org.ospreyphoto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class State {
    @JsonProperty
    public String kept="";

    @JsonProperty
    public String place="";

    @JsonProperty
    public int score=-1;


    public String toString(){
        return kept+","+place;//+","+score;
    }
}
