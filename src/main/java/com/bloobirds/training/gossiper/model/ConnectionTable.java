package com.bloobirds.training.gossiper.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionTable {

	private final Set<Connection> connections = Collections.synchronizedSet(new HashSet<>());

    public void addConnections(Collection<Connection> newConnections) {
		newConnections.forEach(newConnection -> add(newConnection));
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
