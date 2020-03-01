package com.bloobirds.training.gossiper.model;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bloobirds.training.gossiper.GossiperConfigurationProperties;

public class ConnectionTableTest {

	private static final String OWN_NODE_NAME = "ownNodeName";
	private static final String HOST_NAME = "hostName";

	private ConnectionTable sutConnectionTable;

	@Mock
	private GossiperConfigurationProperties stubProperties;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		sutConnectionTable = new ConnectionTable(stubProperties);
		BDDMockito.given(stubProperties.getOwnName()).willReturn(OWN_NODE_NAME);
	}

	@Test
	public void shouldAddConnection() {
		Connection newConnection = new Connection("anotherNodeName", HOST_NAME);
		sutConnectionTable.add(newConnection);
		Assertions.assertThat(sutConnectionTable.getAll()).containsExactly(newConnection);
	}

	@Test
	public void shouldAddConnections() {
		Connection newConnection1 = new Connection("oneNodeName", HOST_NAME);
		Connection newConnection2 = new Connection("anotherNodeName", HOST_NAME);
		Collection<Connection> connections = Stream.of(newConnection1, newConnection2).collect(Collectors.toList());
		sutConnectionTable.addConnections(connections);
		Assertions.assertThat(sutConnectionTable.getAll()).containsExactly(newConnection1, newConnection2);
	}
}
