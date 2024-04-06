package me.kyren223;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.function.UnaryOperator;

public class Main {
    public static final String PATH = "test.txt";
    public static final int LINES = 20;
    public static final int COLUMNS = 50;
    public static final String INITIAL_CONTENT = generateInitialContent(LINES, COLUMNS);
    public static final int SLEEP_DURATION_MILLIS = 10;
    public static final int DURATION_IN_SECONDS = 10;
    public static final int TIMES_TO_UPDATE = DURATION_IN_SECONDS * 1000 / SLEEP_DURATION_MILLIS;
    private static Random random;
    private static int score = 0;
    
    public static void main(String[] args) {
        Path path = Paths.get(PATH);
        try {
            createFileIfNotExists();
            Files.writeString(path, INITIAL_CONTENT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        UnaryOperator<String> updateFunction = currentString -> {
            if (currentString.contains("X")) return currentString;
            score++;
            return generateInitialContent(LINES, COLUMNS);
        };
        
        try {
            monitorFileForUpdates(updateFunction);
            Files.writeString(path, "Game over! Your score is: " + score);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    private static String generateInitialContent(int lines, int columns) {
        if (random == null) {
            random = new Random();
        }
        int randomX = random.nextInt(columns);
        int randomY = random.nextInt(lines);
        
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < columns; x++) {
                sb.append((x == randomX && y == randomY) ? "X" : " ");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private static void createFileIfNotExists() throws IOException {
        Path filePath = Paths.get(PATH);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }
    
    private static void monitorFileForUpdates(UnaryOperator<String> updateFunction)
            throws IOException, InterruptedException {
        Path filePath = Paths.get(PATH);
        String currentString = Files.readString(filePath);
        
        boolean started = false;
        
        for (int i = 0; i < TIMES_TO_UPDATE; i += started ? 1 : 0) {
            Thread.sleep(SLEEP_DURATION_MILLIS);
            
            String newString = Files.readString(filePath);
            if (!currentString.equals(newString)) {
                if (!started) started = true;
                currentString = updateFunction.apply(newString);
                Files.writeString(filePath, currentString);
            }
        }
    }}