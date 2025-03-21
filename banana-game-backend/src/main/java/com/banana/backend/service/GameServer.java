package com.banana.backend.service;

import com.banana.backend.model.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class GameServer {
    // Method to fetch game from the Banana API
    public Game getRandomGame() {
        String bananaapi = "https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes";
        String dataraw = readUrl(bananaapi);
        
        if (dataraw == null || dataraw.isEmpty()) {
            System.out.println("Error: Could not retrieve game data from the server.");
            return null; // Return null if the data is empty or invalid
        }
        
        String[] data = dataraw.split(",");
        if (data.length != 2) {
            System.out.println("Error: Malformed game data received.");
            return null; // Invalid data structure
        }

        byte[] decodeImg = Base64.getDecoder().decode(data[0]);
        ByteArrayInputStream quest = new ByteArrayInputStream(decodeImg);
        int solution = Integer.parseInt(data[1]);

        BufferedImage img = null;
        try {
            img = ImageIO.read(quest);
            return new Game(img, solution);
        } catch (IOException e) {
            System.out.println("Error processing game image: " + e.getMessage());
            return null; // Return null in case of failure to process the image
        }
    }

    // Utility method to read the URL
    private static String readUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (Exception e) {
            System.out.println("Error reading URL: " + e.getMessage());
            return null; // Return null if URL reading fails
        }
    }
}

