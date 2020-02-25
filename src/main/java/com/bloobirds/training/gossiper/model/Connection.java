package com.bloobirds.training.gossiper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Connection {

    private final String name;
    private final String hostname;

    public Connection(@JsonProperty("name") String name, @JsonProperty("hostname") String hostname) {
        this.name = name;
        this.hostname = hostname;
    }
}
