import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import App from './App';

// Mock della funzione globale fetch
beforeEach(() => {
  global.fetch = jest.fn();
});

afterEach(() => {
  jest.clearAllMocks();
});

describe('App Component', () => {

  // TEST 1: Verifica lo stato di caricamento (copre "if (loading)")
  test('mostra il messaggio di caricamento inizialmente', () => {
    // Simuliamo una fetch che non risponde subito
    global.fetch.mockImplementationOnce(() => new Promise(() => {}));

    render(<App />);

    // Verifica che "Caricamento..." sia nel documento
    expect(screen.getByText(/Caricamento.../i)).toBeInTheDocument();
  });

  // TEST 2: Verifica rendering dati e header (copre "const headers", map, fetch success)
  test('renderizza la tabella con i dati dopo il fetch', async () => {
    const mockData = [
      { id: 1, lavoro: "Frontend Dev" }
    ];

    global.fetch.mockResolvedValueOnce({
      json: async () => mockData,
    });

    render(<App />);

    // Aspetta che il caricamento sparisca
    await waitFor(() => expect(screen.queryByText(/Caricamento.../i)).not.toBeInTheDocument());

    // Verifica che gli header siano stati creati (Object.keys)
    expect(screen.getByText("id")).toBeInTheDocument();
    expect(screen.getByText("lavoro")).toBeInTheDocument();

    // Verifica i dati
    expect(screen.getByText("Frontend Dev")).toBeInTheDocument();
  });

  // TEST 3: Verifica stili pari/dispari e interazioni mouse (Copre trStyle e onMouseEnter/Leave)
  test('applica stili corretti alle righe e gestisce mouse hover', async () => {
    // Creiamo due elementi per testare pari (idx 0) e dispari (idx 1)
    const mockData = [
      { id: 1, name: "Row 1" }, // Pari (#f9f9f9)
      { id: 2, name: "Row 2" }  // Dispari (#fff)
    ];

    global.fetch.mockResolvedValueOnce({
      json: async () => mockData,
    });

    render(<App />);

    await waitFor(() => expect(screen.queryByText(/Caricamento.../i)).not.toBeInTheDocument());

    const rows = screen.getAllByRole('row');
    // rows[0] è l'header, quindi i dati sono rows[1] e rows[2]
    const row1 = rows[1];
    const row2 = rows[2];

    // CHECK COPERTURA LOGICA (idx % 2 === 0)
    expect(row1).toHaveStyle('background-color: #f9f9f9'); // 0 è pari
    expect(row2).toHaveStyle('background-color: #fff');    // 1 è dispari

    // CHECK COPERTURA EVENTI (onMouseEnter / onMouseLeave)

    // Simula Mouse Enter su riga 1
    fireEvent.mouseEnter(row1);
    expect(row1).toHaveStyle('background-color: #d0e7f9');

    // Simula Mouse Leave su riga 1 (deve tornare al colore originale pari)
    fireEvent.mouseLeave(row1);
    expect(row1).toHaveStyle('background-color: #f9f9f9');

    // Simula Mouse Enter/Leave su riga 2 per sicurezza
    fireEvent.mouseEnter(row2);
    expect(row2).toHaveStyle('background-color: #d0e7f9');
    fireEvent.mouseLeave(row2);
    expect(row2).toHaveStyle('background-color: #fff'); // torna bianco
  });

  // TEST 4: Gestione Errore Fetch (Copre il blocco .catch)
  test('gestisce errore nel fetch e rimuove loading', async () => {
    // Spia il console.error per evitare che sporchi il log del test
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    global.fetch.mockRejectedValueOnce(new Error("Errore API"));

    render(<App />);

    // Aspetta che loading sparisca anche in caso di errore
    await waitFor(() => expect(screen.queryByText(/Caricamento.../i)).not.toBeInTheDocument());

    // Verifica che l'errore sia stato loggato
    expect(consoleSpy).toHaveBeenCalledWith("Errore nel fetch:", expect.any(Error));

    // Verifica che il titolo sia comunque visibile (l'app non è crashata)
    expect(screen.getByText("Lista lavori da Flask")).toBeInTheDocument();

    consoleSpy.mockRestore();
  });

  // TEST 5: Dati vuoti (Copre il caso items.length > 0 ternario su headers)
  test('gestisce array vuoto senza crashare', async () => {
    global.fetch.mockResolvedValueOnce({
      json: async () => [],
    });

    render(<App />);
    await waitFor(() => expect(screen.queryByText(/Caricamento.../i)).not.toBeInTheDocument());

    // La tabella c'è ma non ha header (perché items.length è 0)
    const table = screen.getByRole('table');
    expect(table).toBeInTheDocument();
  });
});