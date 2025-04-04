package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.service.Service;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPersistenceDataManager implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final String filePath;
    private ConcurrentHashMap<Integer, String> data;

    public AbstractPersistenceDataManager(String filePath) {
        this.filePath = filePath;

        data = new ConcurrentHashMap<>();
    }

    private ConcurrentHashMap<Integer, String> loadData() {
        File dataFile = new File(filePath);
        checkDataFile(dataFile);

        return parsePersistenceDataToVolatile();
    }

    private ConcurrentHashMap<Integer, String> parsePersistenceDataToVolatile() {
        ConcurrentHashMap<Integer, String> data = new ConcurrentHashMap<>();
        int lineCount = 0;
        String fileLine;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((fileLine = bufferedReader.readLine()) != null) {
                if (!isValidDataLine(fileLine)) {
                    logger.log(Level.WARN, "Invalid data line: {}", fileLine);
                    continue;
                }

                data.put(lineCount, fileLine);
                lineCount++;
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error while reading data file", e);
        }

        return data;
    }

    protected abstract boolean isValidDataLine(String line);

    private void checkDataFile(File dataFile) {
        if (!dataFile.exists()) {
            logger.log(Level.FATAL, "{} data file does not found: ", filePath);
            throw new RuntimeException("Data file does not found.");
        }
    }

    protected String getDataLine(int index) {
        return data.get(index);
    }

    protected void reloadData() {
        data = loadData();
    }
}
