package com.bloobirds.training.gossiper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GossiperResponse {

    private String name;
    private String port;
    private List<Connection> connections;

}
