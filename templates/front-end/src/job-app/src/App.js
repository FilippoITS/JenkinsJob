import { useEffect, useState } from "react";

function App() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/job")  // Assicurati che l'URL del servizio sia corretto
      .then((response) => response.json())
      .then((data) => {
        setItems(data); // I dati dovrebbero essere un array di oggetti
        setLoading(false);
      })
      .catch((error) => {
        console.error("Errore nel fetch:", error);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Caricamento...</p>;

    const headers = items.length > 0 ? Object.keys(items[0]) : [];

     return (
    <div style={{ fontFamily: "sans-serif", padding: "20px" }}>
      <h1>Lista lavori da Flask</h1>
      <table border="1" cellPadding="8" style={{ borderCollapse: "collapse" }}>
        <thead>
          <tr>
            {headers.map((key) => (
              <th key={key}>{key}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items.map((item, idx) => (
            <tr key={idx}>
              {headers.map((key) => (
                <td key={key}>{item[key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
