package com.banana.backend.controller;

import com.banana.backend.service.GameEngine;
import com.banana.backend.model.Game;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    // Initialize the Game Engine
    GameEngine gameEngine = new GameEngine("Player1");

    // Endpoint to retrieve the next game image
    @GetMapping("/next")
    public Game getNextGame() {
        return gameEngine.nextGame();
    }

    // Endpoint to submit a solution
    @PostMapping("/submit")
    public boolean checkSolution(@RequestParam int solution) {
        return gameEngine.checkSolution(solution);
    }

    // Endpoint to retrieve the current score
    @GetMapping("/score")
    public int getScore() {
        return gameEngine.getScore();
    }
}
