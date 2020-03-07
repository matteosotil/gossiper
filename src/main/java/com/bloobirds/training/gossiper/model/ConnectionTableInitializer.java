package com.bloobirds.training.gossiper.model;

import java.util.Collections;
import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.bloobirds.training.gossiper.GossiperConfigurationProperties;
import com.bloobirds.training.gossiper.backup.BackupConnectionTableService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({ GossiperConfigurationProperties.class })
public class ConnectionTableInitializer {

	private final GossiperConfigurationProperties properties;
	private final BackupConnectionTableService backupConnectionTable;
	private final ConnectionTable connectionTable;

	@PostConstruct
	public void init() {
		connectionTable.add(new Connection(properties.getOwnName(), properties.getHostName()));
		if (properties.getSeedHostname() != null && properties.getSeedName() != null) {
			connectionTable.addConnections(
					Collections.singleton(new Connection(properties.getSeedName(), properties.getSeedHostname())));
		}
		connectionTable.addConnections(backupConnectionTable.read());
	}
}
