package com.bloobirds.training.gossiper.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.bloobirds.training.gossiper.GossiperConfigurationProperties;
import com.bloobirds.training.gossiper.model.Connection;
import com.bloobirds.training.gossiper.model.ConnectionTable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reads and writes connection table from file.
 * 
 * @author MatteoSotil
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(GossiperConfigurationProperties.class)
public class BackupConnectionTableServiceToFile implements BackupConnectionTableService {
	private final GossiperConfigurationProperties properties;
	private final ConnectionTable connectionTable;

	/**
	 * Backups connection table to file defined in properties (backupFile).
	 * 
	 * @see com.bloobirds.training.gossiper.backup.BackupConnectionTableService#write()
	 */
	@Override
	public void write() {
		if (!isEnabled()) {
			return;
		}
		List<Connection> connections = connectionTable.getAll();
		if (connections.isEmpty()) {
			return;
		}
		try (FileOutputStream fileOutput = new FileOutputStream(new File(properties.getBackupFile()));
				ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {
			objectOutput.writeObject(new ArrayList<Connection>(connections));
		} catch (IOException e) {
			log.error("Error writing connection table to file", e);
		}
	}

	/**
	 * Reads backup connection table from file defined in properties (backupFile).
	 * 
	 * @see com.bloobirds.training.gossiper.backup.BackupConnectionTableService#read()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Connection> read() {
		if (!isEnabled()) {
			return Collections.emptyList();
		}
		try (FileInputStream fileInput = new FileInputStream(new File(properties.getBackupFile()));
				ObjectInputStream objectInput = new ObjectInputStream(fileInput)) {
			return (List<Connection>) objectInput.readObject();
		} catch (Exception e) {
			log.error("Error reading connection table from file", e);
			return Collections.emptyList();
		}
	}

	/**
	 * It is enabled if backupFile is defined in properties.
	 * 
	 * @see com.bloobirds.training.gossiper.backup.BackupConnectionTableService#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return properties.getBackupFile() != null;
	}

	@Override
	public void clean() {
		if (!isBackupReady()) {
			return;
		}
		try {
			Files.delete(Paths.get(properties.getBackupFile()));
		} catch (IOException e) {
			log.error("Error deleting file", e);
		}
	}

	@Override
	public boolean isBackupReady() {
		if (!isEnabled()) {
			return false;
		}
		return Paths.get(properties.getBackupFile()).toFile().exists();
	}
}
