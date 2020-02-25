package edu.ub.bda.gossiper.endpoint;

import edu.ub.bda.gossiper.model.Connection;
import edu.ub.bda.gossiper.model.ConnectionTable;
import edu.ub.bda.gossiper.GossiperConfigurationProperties;
import edu.ub.bda.gossiper.model.GossiperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import java.util.Collections;

@RequiredArgsConstructor
@RestController
@EnableConfigurationProperties(GossiperConfigurationProperties.class)
public class PingController {

    private final GossiperConfigurationProperties properties;
    private final ConnectionTable connectionTable;

    @JsonPost("/ping")
    public @ResponseBody
    GossiperResponse answer(@RequestBody GossiperResponse newConnections, ServletRequest servletRequest) {
        connectionTable.addConnections(newConnections.getConnections());
        connectionTable.addConnections(Collections.singletonList(new Connection(newConnections.getName(), servletRequest.getServerName() + ":" + newConnections.getPort())));
        return new GossiperResponse(properties.getOwnName(), properties.getPort(),  connectionTable.getAll());
    }

}
