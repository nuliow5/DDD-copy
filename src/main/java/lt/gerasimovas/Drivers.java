package lt.gerasimovas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Drivers {
    public static void main(String[] args) throws IOException {
        // Path to the text file containing driver names
        String driverNamesFilePath = "C:\\DDD_copy\\listFiles.txt";

        // Source folder containing DDD files
        String sourceFolder = "C:\\DDD_copy\\all";

        // Destination folder where files will be copied
        String destinationFolder = "C:\\DDD_copy\\done";

        try {
            // Read driver names from the text file
            List<String> driverNames = readDriverNamesFromFile(driverNamesFilePath);

            // Call the copyFiles method
            copyMatchingFiles(driverNames, sourceFolder, destinationFolder);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static List<String> readDriverNamesFromFile(String filePath) throws IOException {
        List<String> driverNames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    driverNames.add(line);
                }
            }
        }

        return driverNames;
    }

    public static void copyMatchingFiles(List<String> driverNames, String sourceFolder, String destinationFolder) throws IOException {
        File sourceDir = new File(sourceFolder);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("Source folder does not exist or is not a directory: " + sourceFolder);
        }

        File destinationDir = new File(destinationFolder);

        if (!destinationDir.exists() && !destinationDir.mkdirs()) {
            throw new IOException("Failed to create destination folder: " + destinationFolder);
        }

        Set<String> foundDrivers = new HashSet<>();

        File[] files = sourceDir.listFiles();
        if (files == null) {
            throw new IOException("Failed to list files in the source folder: " + sourceFolder);
        }

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName().toUpperCase().replaceAll("[^A-Z0-9_]", "");

                for (String driver : driverNames) {
                    String normalizedDriver = normalizeString(driver)
                            .toUpperCase()
                            .replace(" ", "")
                            .replace("UULU", "") // Ignore 'Uulu' and similar suffixes
                            .replace("ULU", "")
                            .replace("OV", "")
                            .replace("EV", "");

                    if (fileName.contains(normalizedDriver)) {
                        Path sourcePath = file.toPath();
                        Path destinationPath = new File(destinationDir, file.getName()).toPath();
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Copied: " + file.getName());
                        foundDrivers.add(driver);
                    }
                }
            }
        }

        List<String> missingDrivers = new ArrayList<>();
        for (String driver : driverNames) {
            if (!foundDrivers.contains(driver)) {
                missingDrivers.add(driver);
            }
        }

        if (!missingDrivers.isEmpty()) {
            System.out.println("The following drivers were not found:");
            for (String driver : missingDrivers) {
                System.out.println(driver);
            }
        } else {
            System.out.println("All drivers were found.");
        }

        System.out.println("File copying completed.");
    }

    public static String normalizeString(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^A-Z0-9_]", "");
    }
}
