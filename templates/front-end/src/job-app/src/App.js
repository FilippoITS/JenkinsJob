import { useNavigate } from "react-router-dom";

function App() {
  const navigate = useNavigate();

  const containerStyle = {
    fontFamily: "Arial, sans-serif",
    backgroundColor: "#f2f7f9",
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    gap: "40px"
  };

  const titleStyle = {
    fontSize: "2.5rem",
    color: "#333",
    textAlign: "center"
  };

  const buttonsContainer = {
    display: "flex",
    gap: "20px",
    flexWrap: "wrap",
    justifyContent: "center"
  };

  const buttonStyle = {
    padding: "30px 50px",
    fontSize: "1.5rem",
    border: "none",
    borderRadius: "12px",
    cursor: "pointer",
    color: "white",
    transition: "transform 0.2s",
    minWidth: "150px"
  };

  const buttonHover = {
    transform: "scale(1.05)"
  };

  return (
    <div style={containerStyle}>
      <h1 style={titleStyle}>Job-app Work Lists</h1>
      <div style={buttonsContainer}>
        <button
          style={{ ...buttonStyle, backgroundColor: "#FFA500" }}
          onClick={() => navigate("/pending")}
        >
          Pending
        </button>
        <button
          style={{ ...buttonStyle, backgroundColor: "#3498DB" }}
          onClick={() => navigate("/running")}
        >
          Running
        </button>
        <button
          style={{ ...buttonStyle, backgroundColor: "#2ECC71" }}
          onClick={() => navigate("/ok")}
        >
          OK
        </button>
        <button
          style={{ ...buttonStyle, backgroundColor: "#E74C3C" }}
          onClick={() => navigate("/errore")}
        >
          Errore
        </button>
      </div>
    </div>
  );
}

export default App;
