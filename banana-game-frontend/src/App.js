import React, { useState, useEffect } from "react";
import Cookies from "js-cookie";
import "./styles.css"; // Assuming you have styles

function App() {
  const debugMode = true;
  const [gameImage, setGameImage] = useState(null);
  const [solution, setSolution] = useState("");
  const [score, setScore] = useState({ player1: 0, player2: 0 });
  const [message, setMessage] = useState("");
  const [currentPlayer, setCurrentPlayer] = useState("player1");
  const [player1Name, setPlayer1Name] = useState("");
  const [player2Name, setPlayer2Name] = useState("");
  const [timeLeft, setTimeLeft] = useState(60);
  const [gameEnded, setGameEnded] = useState(false);
  const [correctSolution, setCorrectSolution] = useState(null); // Store correct solution

  // Timer for the current player's turn
  useEffect(() => {
    if (timeLeft === 0) {
      setMessage(
        `Time's up! Switching to ${
          currentPlayer === "player1" ? player2Name : player1Name
        }.`
      );
      switchPlayer();
    }

    const timer =
      timeLeft > 0 && setInterval(() => setTimeLeft(timeLeft - 1), 1000);
    return () => clearInterval(timer);
  }, [timeLeft]);

  const switchPlayer = () => {
    // Save the current player's score in cookies
    Cookies.set(
      currentPlayer === "player1" ? "score1" : "score2",
      score[currentPlayer]
    );

    // Switch to the next player when time runs out
    if (currentPlayer === "player1") {
      setCurrentPlayer("player2");
      setTimeLeft(60);
      fetchGame("player2", player2Name);
    } else {
      // End game after player2 finishes
      endGame();
    }
  };

  const endGame = () => {
    setGameEnded(true); // Mark the game as ended
    setMessage("Game Over! Check the scores below.");
  };

  const fetchGame = async (player, name) => {
    try {
      const response = await fetch(
        `http://localhost:8080/game/next?player=${name}`
      );
      const data = await response.json();

      setGameImage(`data:image/png;base64,${data.image}`);
      setCorrectSolution(data.solution); // Update the solution for the current game
      setSolution(""); // Reset the current solution input field
      setMessage(`${name}'s turn!`);
    } catch (error) {
      console.error("Error fetching game:", error);
    }
  };

  const checkSolution = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/game/check/${solution}?player=${
          currentPlayer === "player1" ? player1Name : player2Name
        }`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
        }
      );

      const result = await response.json();
      if (result.correct) {
        setMessage("Correct!");
        setScore((prevScore) => ({
          ...prevScore,
          [currentPlayer]: prevScore[currentPlayer] + 1, // Increment score for the current player
        }));

        // Fetch a new question after a correct answer
        fetchGame(
          currentPlayer,
          currentPlayer === "player1" ? player1Name : player2Name
        );
      } else {
        setMessage("Incorrect, try again.");
      }
    } catch (error) {
      console.error("Error checking solution:", error);
    }
  };

  const startGame = () => {
    if (!player1Name || !player2Name) {
      setMessage("Please enter both player names.");
      return;
    }
    setCurrentPlayer("player1");
    fetchGame("player1", player1Name);
    setTimeLeft(60); // Start with 60 seconds
  };

  return (
    <div className="App">
      <h1>Banana Game - Two Players</h1>

      {gameEnded ? (
        <div>
          <h2>Game Over!</h2>
          <p>
            {player1Name}'s score: {Cookies.get("score1") || score.player1}
          </p>
          <p>
            {player2Name}'s score: {Cookies.get("score2") || score.player2}
          </p>
          <h3>Thank you for playing!</h3>
        </div>
      ) : (
        <>
          {!gameImage && (
            <div>
              <input
                type="text"
                value={player1Name}
                onChange={(e) => setPlayer1Name(e.target.value)}
                placeholder="Enter Player 1 name"
              />
              <input
                type="text"
                value={player2Name}
                onChange={(e) => setPlayer2Name(e.target.value)}
                placeholder="Enter Player 2 name"
              />
              <button onClick={startGame}>Start Game</button>
            </div>
          )}

          {gameImage && (
            <>
              <img src={gameImage} alt="Game" />
              {debugMode && <p>Correct Answer: {correctSolution}</p>}{" "}
              {/* Show correct answer if in debug mode */}
              <div>
                <input
                  type="number"
                  value={solution}
                  onChange={(e) => setSolution(e.target.value)}
                  placeholder="Enter your solution"
                />
                <button onClick={checkSolution}>Submit</button>
              </div>
              <h2>{message}</h2>
              <h3>
                {currentPlayer === "player1" ? player1Name : player2Name}'s
                score:{" "}
                {currentPlayer === "player1" ? score.player1 : score.player2}
              </h3>
              <h3>Time Left: {timeLeft} seconds</h3>
            </>
          )}
        </>
      )}
    </div>
  );
}

export default App;
