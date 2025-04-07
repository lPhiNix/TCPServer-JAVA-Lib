package org.phinix.lib.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for file operations such as checking existence, creating, reading, and writing files.
 */
public class FileUtil {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Checks if a file exists at the specified file path.
     *
     * @param filePath the file path
     * @return {@code true} if the file exists, {@code false} otherwise
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        logger.debug("Checking if file exists: {}", filePath);
        return file.exists();
    }

    /**
     * Creates a new file at the specified file path.
     *
     * @param filePath the file path
     */
    public static void createFile(String filePath) {
        logger.debug("Creating file: {}", filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("");
        } catch (IOException e) {
            logger.fatal("Error creating file: ", e);
        }
    }

    /**
     * Reads a file line by line and consumes each line using the provided consumer.
     *
     * @param filePath the file path
     * @param lineConsumer the consumer to process each line
     */
    public static void readFile(String filePath, Consumer<String> lineConsumer) {
        logger.debug("Reading file: {}", filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineConsumer.accept(line);
            }
        } catch (IOException e) {
            logger.error("Error reading file: ", e);
        }
    }

    /**
     * Writes a collection of items to a file, converting each item to a string using the provided converter.
     *
     * @param filePath the file path
     * @param items the collection of items to write
     * @param converter the function to convert each item to a string
     * @param <T> the type of items
     */
    public static <T> void writeFile(String filePath, Iterable<T> items, Function<T, String> converter) {
        logger.debug("Writing to file: {}", filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T item : items) {
                writer.write(converter.apply(item));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error writing to file: ", e);
        }
    }
}