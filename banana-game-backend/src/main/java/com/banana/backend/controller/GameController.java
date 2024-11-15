package com.banana.backend.controller;

import com.banana.backend.service.GameEngine;
import com.banana.backend.service.GameServer;
import com.banana.backend.model.Game;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class GameController {

    private Map<String, GameEngine> activeGames = new HashMap<>();
    private GameServer gameServer = new GameServer(); // Assuming you have a GameServer to provide random games

    // Fetch the next game and return the image in Base64 format along with remaining chances    
    @GetMapping("/next")
    public ResponseEntity<Map<String, Object>> nextGame(@RequestParam String player) {
        Game game = gameServer.getRandomGame(); // Always fetch a new game instance
        if (game == null) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch new game."));
        }
        GameEngine gameEngine = new GameEngine(game); // Create a new GameEngine for the new game
        activeGames.put(player, gameEngine); // Update or add the game engine for the player
        
        Map<String, Object> response = new HashMap<>();
        response.put("image", game.getImageBase64());
        response.put("solution", game.getCorrectSolution()); // Debug mode solution
        response.put("score", gameEngine.getScore());
        
        boolean debugMode = true; // Ensure debugMode is set to true for testing
        if (debugMode) {
            response.put("solution", game.getCorrectSolution());
        }
        
        return ResponseEntity.ok(response);
    }


    // Check if the solution is correct
    @PostMapping("/check/{solution}")
    public ResponseEntity<Map<String, Object>> checkSolution(@PathVariable int solution, @RequestParam String player) {
        GameEngine gameEngine = activeGames.get(player);
        
        if (gameEngine == null) {
            return ResponseEntity.badRequest().body(null); // Handle case if player game not found
        }
        
        boolean correct = gameEngine.checkSolution(solution);
        Map<String, Object> response = new HashMap<>();
        response.put("correct", correct);
        response.put("remainingChances", gameEngine.getRemainingChances()); // Optional
        response.put("score", gameEngine.getScore()); // Return updated score
        
        return ResponseEntity.ok(response);
    }
}
