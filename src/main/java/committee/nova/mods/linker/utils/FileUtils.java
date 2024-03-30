package committee.nova.mods.linker.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FileUtils
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 22:35
 */
public class FileUtils {

    public FileUtils() {
    }

    public static void checkFolder(Path folder) {
        if (!folder.toFile().isDirectory()) {
            try {
                Files.createDirectories(folder);
            } catch (IOException ignored) {
            }
        }

    }
}