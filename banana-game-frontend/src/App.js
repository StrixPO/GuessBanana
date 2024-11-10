import React, { useState, useEffect } from "react";

function App() {
  const [gameImage, setGameImage] = useState(null);
  const [solution, setSolution] = useState("");
  const [score, setScore] = useState(0);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(""); // To handle errors

  // Fetch the game image when the component mounts
  useEffect(() => {
    fetchGame();
  }, []);

  const fetchGame = async () => {
    try {
      const response = await fetch("http://localhost:8080/game/next");
      if (!response.ok) throw new Error("Failed to load game from server");

      const data = await response.json();
      setGameImage(`data:image/png;base64,${data.image}`);
      setError(""); // Clear previous errors
    } catch (error) {
      setError("Error fetching game. Please try again later.");
      console.error("Error fetching game:", error);
    }
  };

  const checkSolution = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/game/check/${solution}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
        }
      );

      if (!response.ok) throw new Error("Failed to check solution");

      const result = await response.json();
      if (result.correct) {
        setMessage("Correct!");
        setScore(result.score);
        fetchGame(); // Fetch the next game
      } else {
        setMessage("Incorrect, try again.");
      }
    } catch (error) {
      setError("Error checking solution. Please try again.");
      console.error("Error checking solution:", error);
    }
  };

  return (
    <div className="App">
      <h1>Banana Game</h1>
      {error && <div style={{ color: "red" }}>{error}</div>}
      {gameImage ? <img src={gameImage} alt="Game" /> : <p>Loading game...</p>}
      <div>
        <input
          type="number"
          value={solution}
          onChange={(e) => setSolution(e.target.value)}
          placeholder="Enter your solution"
        />
        <button onClick={checkSolution}>Submit</button>
      </div>
      <div>
        <h2>{message}</h2>
        <h3>Score: {score}</h3>
      </div>
    </div>
  );
}

export default App;
