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
        logger.log(Level.DEBUG, "Initializing AbstractPersistenceDataManager with file path: {}", filePath);
        data = loadData();
    }

    private ConcurrentHashMap<Integer, String> loadData() {
        File dataFile = new File(filePath);
        logger.log(Level.DEBUG, "Loading data from file: {}", filePath);
        checkDataFile(dataFile);

        return parsePersistenceDataToVolatile();
    }

    private ConcurrentHashMap<Integer, String> parsePersistenceDataToVolatile() {
        logger.log(Level.DEBUG, "Parsing persistence data to volatile map...");
        ConcurrentHashMap<Integer, String> data = new ConcurrentHashMap<>();
        int lineCount = 0;
        String fileLine;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((fileLine = bufferedReader.readLine()) != null) {
                logger.log(Level.DEBUG, "Read line: {}", fileLine);
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

        logger.log(Level.DEBUG, "Finished parsing data, total lines read: {}", lineCount);
        return data;
    }

    protected abstract boolean isValidDataLine(String line);

    private void checkDataFile(File dataFile) {
        if (!dataFile.exists()) {
            logger.log(Level.FATAL, "{} data file does not found: ", filePath);
            throw new RuntimeException("Data file does not found.");
        }
        logger.log(Level.DEBUG, "Data file exists: {}", filePath);
    }

    protected String getDataLine(int index) {
        return data.get(index);
    }

    public int getEquationAmount() {
        return data.size();
    }

    protected void reloadData() {
        data = loadData();
    }
}
