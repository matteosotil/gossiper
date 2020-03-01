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
	private final Map<String, Integer> candidateConnections;
	private final GossiperConfigurationProperties properties;

	public ConnectionTable(GossiperConfigurationProperties properties) {
		this.properties = properties;
		connections = Collections.synchronizedSet(new HashSet<>());
		candidateConnections = Collections.synchronizedMap(new HashMap<String, Integer>());
		if (properties.getSeedHostname() != null && properties.getSeedName() != null) {
			connections.addAll(Collections.singleton(new Connection(properties.getSeedName(), properties.getSeedHostname())));
		}
	}

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

	/**
	 * Adds a connection if it was not present and trust threshold is reached.
	 *
	 * @param connection
	 */
	public void add(Connection connection) {
		if (properties.getOwnName().equals(connection.getName())) {
			return;
		}
		boolean success;
		synchronized (this) {
			if (!shouldAddConnection(connection)) {
				return;
			}
			success = connections.add(connection);
		}
		if (success) {
			log.info("Node {} joined", connection.getName());
		}
	}

	private boolean shouldAddConnection(Connection connection) {
		Integer connectionProposals = candidateConnections.computeIfAbsent(connection.getName(), k -> 0);
		connectionProposals += 1;
		if (connectionProposals < properties.getTrustThreshold()) {
			candidateConnections.put(connection.getName(), connectionProposals);
			return false;
		}
		candidateConnections.remove(connection.getName());
		return true;
	}
}
