package com.bloobirds.training.gossiper.model;

import com.bloobirds.training.gossiper.GossiperConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@EnableConfigurationProperties({GossiperConfigurationProperties.class})
public class ConnectionTable {

    private final Set<Connection> connections;
    private final GossiperConfigurationProperties properties;

    public ConnectionTable(GossiperConfigurationProperties properties) {
        this.properties = properties;
        connections = Collections.synchronizedSet(new HashSet<>());
        if (properties.getSeedHostname() != null && properties.getSeedName() != null) {
            connections.addAll(Collections.singleton(new Connection(properties.getSeedName(), properties.getSeedHostname())));
        }
    }

    public void addConnections(Collection<Connection> newConnections) {
        newConnections.forEach(newConnection -> {
            if (properties.getOwnName().equals(newConnection.getName())) {
                return;
            }
			add(newConnection);
        });
    }

    public void remove(Connection connection) {
        boolean success = connections.remove(connection);
        if (success) {
            log.info("Node {} left", connection.getName());
        }
    }

    public List<Connection> get(int n) {
        List<Connection> copy = new ArrayList<>(connections);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(copy.size(), n));
    }

    public List<Connection> getAll() {
        return new ArrayList<>(connections);
    }

	public void add(Connection newConnection) {
		boolean success = connections.add(newConnection);
		if (success) {
			log.info("Node {} joined", newConnection.getName());
		}
	}
}
