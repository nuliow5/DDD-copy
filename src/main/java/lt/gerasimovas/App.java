package lt.gerasimovas;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class App {
    public static void main(String[] args) throws IOException {
// Path to the text file containing license plates
        String licensePlatesFilePath = "C:\\DDD_copy\\listFiles.txt";

        // Source folder containing DDD files
        String sourceFolder = "C:\\DDD_copy\\all";

        // Destination folder where files will be copied
        String destinationFolder = "C:\\DDD_copy\\done";

        try {
            // Read license plates from the text file
            List<String> licensePlates = readLicensePlatesFromFile(licensePlatesFilePath);

            // Call the copyFiles method
            copyMatchingFiles(licensePlates, sourceFolder, destinationFolder);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static List<String> readLicensePlatesFromFile(String filePath) throws IOException {
        List<String> licensePlates = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Trim whitespace and add non-empty lines to the list
                line = line.trim();
                if (!line.isEmpty()) {
                    licensePlates.add(line);
                }
            }
        }

        return licensePlates;
    }

    public static void copyMatchingFiles(List<String> licensePlates, String sourceFolder, String destinationFolder) throws IOException {
        File sourceDir = new File(sourceFolder);

        // Ensure the source folder exists
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("Source folder does not exist or is not a directory: " + sourceFolder);
        }

        File destinationDir = new File(destinationFolder);

        // Create destination folder if it does not exist
        if (!destinationDir.exists()) {
            if (!destinationDir.mkdirs()) {
                throw new IOException("Failed to create destination folder: " + destinationFolder);
            }
        }

        // Track found plates to identify missing ones later
        Set<String> foundPlates = new HashSet<>();

        // Iterate over all files in the source folder
        File[] files = sourceDir.listFiles();
        if (files == null) {
            throw new IOException("Failed to list files in the source folder: " + sourceFolder);
        }

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();

                // Check if the file name contains any of the license plates
                for (String plate : licensePlates) {
                    if (fileName.contains(plate)) {
                        // Copy the file to the destination folder
                        Path sourcePath = file.toPath();
                        Path destinationPath = new File(destinationDir, fileName).toPath();
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Copied: " + fileName);

                        // Mark this plate as found
                        foundPlates.add(plate);
                    }
                }
            }
        }

        // Identify plates that were not found
        List<String> missingPlates = new ArrayList<>();
        for (String plate : licensePlates) {
            if (!foundPlates.contains(plate)) {
                missingPlates.add(plate);
            }
        }

        // Print missing plates
        if (!missingPlates.isEmpty()) {
            System.out.println("The following license plates were not found:");
            for (String plate : missingPlates) {
                System.out.println(plate);
            }
        } else {
            System.out.println("All license plates were found.");
        }

        System.out.println("File copying completed.");
    }

}



