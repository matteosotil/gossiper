package com.bloobirds.training.gossiper.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Connection implements Serializable {

	private static final long serialVersionUID = 3649772793236356335L;

	private final String name;
    private final String hostname;

    public Connection(@JsonProperty("name") String name, @JsonProperty("hostname") String hostname) {
        this.name = name;
        this.hostname = hostname;
    }
}
