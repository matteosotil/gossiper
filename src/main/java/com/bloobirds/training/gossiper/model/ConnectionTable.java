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
	private final Map<String, Set<String>> candidateConnections;
	private final GossiperConfigurationProperties properties;

	public ConnectionTable(GossiperConfigurationProperties properties) {
		this.properties = properties;
		connections = Collections.synchronizedSet(new HashSet<>());
		candidateConnections = Collections.synchronizedMap(new HashMap<String, Set<String>>());
		if (properties.getSeedHostname() != null && properties.getSeedName() != null) {
			connections.addAll(Collections.singleton(new Connection(properties.getSeedName(), properties.getSeedHostname())));
		}
	}

	public void addConnections(String sourceNodeName, Collection<Connection> newConnections) {
		newConnections.forEach(newConnection -> add(sourceNodeName, newConnection));
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

	/**
	 * Adds a connection if its name is different that own name.
	 *
	 * @param sourceNodeName
	 *
	 * @param connection
	 */
	public void add(String sourceNodeName, Connection connection) {
		if (properties.getOwnName().equals(connection.getName())) {
			return;
		}
		boolean success;
		synchronized (this) {
			if (!shouldAddConnection(sourceNodeName, connection)) {
				return;
			}
			success = connections.add(connection);
		}
		if (success) {
			log.info("Node {} joined", connection.getName());
		}
	}

	private boolean shouldAddConnection(String sourceNodeName, Connection connection) {
		if (sourceNodeName.equals(connection.getName())) {
			return true;
		}
		Set<String> connectionProposals = candidateConnections.computeIfAbsent(connection.getName(),
				k -> new HashSet<String>());
		connectionProposals.add(sourceNodeName);
		if (connectionProposals.size() < properties.getTrustThreshold()) {
			return false;
		}
		candidateConnections.remove(connection.getName());
		return true;
	}
}
