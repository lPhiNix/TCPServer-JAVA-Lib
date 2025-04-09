package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.service.Service;
import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.AbstractWorker;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code AbstractPersistenceDataManager} is an abstract class that provides mechanisms
 * for managing persistent data stored in a file. The data is read into a thread-safe
 * {@link ConcurrentHashMap} for in-memory manipulation and can be reloaded from the file
 * when needed.
 * <p>
 * This class includes methods for validating data lines, parsing data from the file into
 * memory, and retrieving specific lines of data. Subclasses are responsible for defining
 * what constitutes valid data by implementing the {@link #isValidDataLine(String)} method.
 * <p>
 * Use example:
 * <pre>{@code
 * public class DataManager extends AbstractPersistenceDataManager {
 *
 *     private static final String FILE_PATH = "data.txt";
 *
 *     public DataManager() {
 *         super(FILE_PATH);
 *     }
 *
 *     @Override
 *     protected boolean isValidDataLine(String line) {
 *         return true;
 *     }
 * }
 * }
 *
 * @see Service
 * @see AbstractServer
 * @see AbstractWorker
 */
public abstract class AbstractPersistenceDataManager implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final String filePath; // Path to the persistence data file
    private ConcurrentHashMap<Integer, String> data; // In-memory representation of the data

    /**
     * Constructs an {@code AbstractPersistenceDataManager} and initializes the in-memory data
     * by loading it from the specified file.
     *
     * @param filePath the path to the file containing persistent data
     */
    public AbstractPersistenceDataManager(String filePath) {
        this.filePath = filePath;
        logger.log(Level.DEBUG, "Initializing AbstractPersistenceDataManager with file path: {}", filePath);
        data = loadData(); // Load data from the file into memory
    }

    /**
     * Loads data from the persistence file into memory.
     * Validates the file's existence and then parses its content into a {@link ConcurrentHashMap}.
     *
     * @return a thread-safe map containing the parsed data
     */
    private ConcurrentHashMap<Integer, String> loadData() {
        File dataFile = new File(filePath);
        logger.log(Level.DEBUG, "Loading data from file: {}", filePath);
        checkDataFile(dataFile); // Ensure the file exists

        return parsePersistenceDataToVolatile();
    }

    /**
     * Parses the content of the persistence file into a thread-safe map.
     * Each line of the file is added to the map, indexed by its line number.
     * Lines that fail validation are skipped.
     *
     * @return a thread-safe map containing the parsed data
     */
    private ConcurrentHashMap<Integer, String> parsePersistenceDataToVolatile() {
        logger.log(Level.DEBUG, "Parsing persistence data to volatile map...");
        ConcurrentHashMap<Integer, String> data = new ConcurrentHashMap<>();
        int lineCount = 0;
        String fileLine;

        // Read the file line-by-line
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((fileLine = bufferedReader.readLine()) != null) {
                logger.log(Level.DEBUG, "Read line: {}", fileLine);
                if (!isValidDataLine(fileLine)) {
                    // Skip invalid lines and log a warning
                    logger.log(Level.WARN, "Invalid data line: {}", fileLine);
                    continue;
                }

                // Add valid lines to the map
                data.put(lineCount, fileLine);
                lineCount++;
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error while reading data file", e);
        }

        logger.log(Level.DEBUG, "Finished parsing data, total lines read: {}", lineCount);
        return data;
    }

    /**
     * Checks whether a given line of data is valid.
     * Subclasses must implement this method to define their own validation logic.
     *
     * @param line the line to validate
     * @return {@code true} if the line is valid, {@code false} otherwise
     */
    protected abstract boolean isValidDataLine(String line);

    /**
     * Ensures that the persistence file exists.
     * Throws a {@link RuntimeException} if the file does not exist.
     *
     * @param dataFile the file to check
     */
    private void checkDataFile(File dataFile) {
        if (!dataFile.exists()) {
            logger.log(Level.FATAL, "Data file does not exist: {}", filePath); // Improved log message format
            throw new RuntimeException("Data file does not exist.");
        }
        logger.log(Level.DEBUG, "Data file exists: {}", filePath);
    }

    /**
     * Retrieves a specific line of data from memory by its index.
     *
     * @param index the index of the data line
     * @return the data line at the specified index, or {@code null} if the index is invalid
     */
    protected String getDataLine(int index) {
        logger.log(Level.DEBUG, "Retrieving data line at index: {}", index);
        return data.get(index);
    }

    /**
     * Returns the total number of data lines currently loaded in memory.
     *
     * @return the number of data lines
     */
    public int getEquationAmount() {
        logger.log(Level.DEBUG, "Fetching total number of data lines: {}", data.size());
        return data.size();
    }

    /**
     * Reloads the data from the persistence file into memory.
     * This method clears the existing in-memory data and replaces it with
     * the latest content from the file.
     */
    protected void reloadData() {
        logger.log(Level.DEBUG, "Reloading data from file: {}", filePath);
        data = loadData();
    }
}
