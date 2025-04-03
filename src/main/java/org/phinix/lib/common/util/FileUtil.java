package org.phinix.lib.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class FileUtil {
    private static final Logger logger = LogManager.getLogger();

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        logger.debug("Checking if file exists: {}", filePath);
        return file.exists();
    }

    public static void createFile(String filePath) {
        logger.debug("Creating file: {}", filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("");
        } catch (IOException e) {
            logger.fatal("Error creating file: ", e);
        }
    }

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

