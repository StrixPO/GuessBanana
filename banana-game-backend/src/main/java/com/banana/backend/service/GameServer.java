package com.banana.backend.service;

import com.banana.backend.model.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class GameServer {
    // Fetch a random game from the Banana API
    public Game getRandomGame() {
        String apiUrl = "https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes";
        try {
            // Fetch game data as a CSV string
            String dataRaw = fetchGameData(apiUrl);

            // Parse CSV into an image and a solution
            String[] data = dataRaw.split(",");
            byte[] decodedImage = Base64.getDecoder().decode(data[0]);
            int solution = Integer.parseInt(data[1]);

            // Convert byte data into a BufferedImage
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedImage));

            return new Game(image, solution);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper function to read data from the URL
    private String fetchGameData(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
