package org.phinix.lib.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@code FileUtil} utility class for file operations such as checking existence, creating, reading, and writing files.
 * <p>
 * This class provides utility methods for interacting with files, including checking if a file exists, creating a file,
 * reading files line by line, and writing collections of items to a file.
 */
public class FileUtil {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Checks if a file exists at the specified file path.
     * <p>
     * This method returns {@code true} if the file exists at the given path, or {@code false} if it doesn't.
     *
     * @param filePath the file path
     * @return {@code true} if the file exists, {@code false} otherwise
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        logger.debug("Checking if file exists: {}", filePath); // Logs the file path being checked
        return file.exists(); // Returns true if the file exists, false otherwise
    }

    /**
     * Creates a new file at the specified file path.
     * <p>
     * This method attempts to create a file at the provided file path. If the file already exists, no action is taken.
     * If an error occurs during file creation, it logs the exception as a fatal error.
     *
     * @param filePath the file path
     */
    public static void createFile(String filePath) {
        logger.debug("Creating file: {}", filePath); // Logs the file path being created
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(""); // Creates an empty file by writing an empty string
        } catch (IOException e) {
            logger.fatal("Error creating file: ", e); // Logs any fatal error that occurs during file creation
        }
    }

    /**
     * Reads a file line by line and consumes each line using the provided consumer.
     * <p>
     * This method opens the file and reads it line by line, passing each line to the specified consumer function.
     * If an error occurs while reading the file, it logs the exception as an error.
     *
     * @param filePath the file path
     * @param lineConsumer the consumer to process each line
     */
    public static void readFile(String filePath, Consumer<String> lineConsumer) {
        logger.debug("Reading file: {}", filePath); // Logs the file path being read
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineConsumer.accept(line); // Processes each line using the provided consumer
            }
        } catch (IOException e) {
            logger.error("Error reading file: ", e); // Logs any error that occurs while reading the file
        }
    }

    /**
     * Writes a collection of items to a file, converting each item to a string using the provided converter.
     * <p>
     * This method writes each item in the collection to the file, converting it to a string using the provided converter function.
     * If an error occurs while writing the file, it logs the exception as an error.
     *
     * @param filePath the file path
     * @param items the collection of items to write
     * @param converter the function to convert each item to a string
     * @param <T> the type of items
     */
    public static <T> void writeFile(String filePath, Iterable<T> items, Function<T, String> converter) {
        logger.debug("Writing to file: {}", filePath); // Logs the file path where data is being written
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T item : items) {
                writer.write(converter.apply(item)); // Converts the item to a string and writes it to the file
                writer.newLine(); // Writes a new line after each item
            }
        } catch (IOException e) {
            logger.error("Error writing to file: ", e); // Logs any error that occurs during file writing
        }
    }
}
