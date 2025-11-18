import { useEffect, useState } from "react";

function JobTable({ status }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`/job/${status}`)
      .then((response) => response.json())
      .then((data) => {
        setItems(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Errore nel fetch:", error);
        setLoading(false);
      });
  }, [status]);

  if (loading) return <p style={{ textAlign: "center" }}>Caricamento...</p>;

  const headers = items.length > 0 ? Object.keys(items[0]) : [];

  const containerStyle = {
    fontFamily: "Arial, sans-serif",
    padding: "20px",
    backgroundColor: "#f2f7f9",
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
    alignItems: "center"
  };

  const tableStyle = {
    borderCollapse: "collapse",
    width: "80%",
    maxWidth: "1000px",
    backgroundColor: "#fff",
    boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
    borderRadius: "8px",
    overflow: "hidden"
  };

  const thStyle = {
    backgroundColor: "#4CAF50",
    color: "white",
    padding: "12px 15px",
    textTransform: "uppercase"
  };

  const tdStyle = {
    padding: "12px 15px"
  };

  const trStyle = (idx) => ({
    backgroundColor: idx % 2 === 0 ? "#f9f9f9" : "#fff",
    cursor: "default",
    transition: "background-color 0.3s"
  });

  return (
    <div style={containerStyle}>
      <h1 style={{ textAlign: "center", marginBottom: "20px" }}>
        {status.toUpperCase()} Jobs
      </h1>
      <table style={tableStyle}>
        <thead>
          <tr>
            {headers.map((key) => (
              <th key={key} style={thStyle}>{key}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items.map((item, idx) => (
            <tr
              key={idx}
              style={trStyle(idx)}
              onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#d0e7f9"}
              onMouseLeave={(e) =>
                (e.currentTarget.style.backgroundColor =
                  idx % 2 === 0 ? "#f9f9f9" : "#fff")
              }
            >
              {headers.map((key) => (
                <td key={key} style={tdStyle}>{item[key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default JobTable;
