package com.banana.backend.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

public class Game {
    private BufferedImage image;  // Assuming this holds the image data
    private int correctSolution;  // Correct solution for the game

    public Game(BufferedImage image, int correctSolution) {
        this.image = image;
        this.correctSolution = correctSolution;
    }

    // Method to return the image as Base64 string
    public String getImageBase64() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);  // Change "png" if the image has a different format
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to return the correct solution for the game
    public int getCorrectSolution() {
        return correctSolution;
    }

    // Other methods and attributes of the Game class
}


