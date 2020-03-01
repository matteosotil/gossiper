package com.bloobirds.training.gossiper.backup;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.bloobirds.training.gossiper.GossiperConfigurationProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(GossiperConfigurationProperties.class)
public class BackupConnectionTableScheduler {
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	private final BackupConnectionTableService backupConnectionTableService;
	private final GossiperConfigurationProperties properties;

	@PostConstruct
	public void init() {
		if (!isEnabled()) {
			return;
		}
		scheduledExecutorService.scheduleAtFixedRate(backupConnectionTableService::write, properties.getBackupTime(),
				properties.getBackupTime(), TimeUnit.SECONDS);
	}

	public boolean isEnabled() {
		return properties.getBackupTime() > 0;
	}
}
