package com.bloobirds.training.gossiper.backup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
		try (FileOutputStream fileOutput = new FileOutputStream(getWriteFileName());
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
		if (!isBackupAvailable()) {
			log.warn("No backup files to read");
			return Collections.emptyList();
		}
		try (FileInputStream fileInput = new FileInputStream(getReadFileName());
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
		return !StringUtils.isEmpty(properties.getBackupDirectory());
	}

	@Override
	public boolean isBackupAvailable() {
		if (!isEnabled()) {
			return false;
		}
		return !getBackupFiles().isEmpty();
	}

	@PreDestroy
	public void clean() {
		try {
			Files.delete(Paths.get(getWriteFileName()));
			log.info("Backup file deleted");
		} catch (IOException e) {
			log.error("Error cleaning backup file", e);
		}
	}

	private String getReadFileName() {
		List<String> backupFiles = getBackupFiles();
		int selectedFileIndex = ThreadLocalRandom.current().nextInt(backupFiles.size());
		return getBackupFiles().get(selectedFileIndex);
	}

	private String getWriteFileName() {
		return Paths.get(properties.getBackupDirectory(), properties.getOwnName()).toString();
	}

	private List<String> getBackupFiles() {
		Path directory = Paths.get(properties.getBackupDirectory());
		try (Stream<Path> walk = Files.walk(directory)) {
			List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
			if (result.isEmpty()) {
				return Collections.emptyList();
			}
			return result;
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}
}
