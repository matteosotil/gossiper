package com.bloobirds.training.gossiper.backup;

import java.util.List;

import com.bloobirds.training.gossiper.model.Connection;

/**
 * Backup service for connection table.
 * 
 * @author MatteoSotil
 *
 */
public interface BackupConnectionTableService {

	/**
	 * Backups connection table.
	 */
	void write();

	/**
	 * Reads backup connection table.
	 * 
	 * @return
	 */
	List<Connection> read();

	/**
	 * Checks if backup is enabled for this node.
	 * 
	 * @return
	 */
	boolean isEnabled();

	/**
	 * Checks if there is some backup available
	 * 
	 * @return
	 */
	boolean isBackupAvailable();

}