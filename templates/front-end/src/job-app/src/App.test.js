import { render, screen, waitFor } from '@testing-library/react';
import App from './App';

describe('App Component', () => {
  it('renders loading state initially', () => {
    render(<App />);
    expect(screen.getByText(/Caricamento.../)).toBeInTheDocument();
  });

  it('renders data after fetch', async () => {
    render(<App />);
    // Aspetta che venga mostrato "Project-Test", che è nel mock
    await waitFor(() => expect(screen.getByText("Project-Test")).toBeInTheDocument());
    expect(screen.getByText("DONE")).toBeInTheDocument();
  });

  it('renders the table headers', async () => {
    render(<App />);
    await waitFor(() => {
      // Usa le chiavi dell'oggetto mock come intestazioni
      expect(screen.getByText("id")).toBeInTheDocument();
      expect(screen.getByText("project")).toBeInTheDocument();
      expect(screen.getByText("startdate")).toBeInTheDocument();
      expect(screen.getByText("enddate")).toBeInTheDocument();
      expect(screen.getByText("status")).toBeInTheDocument();
      expect(screen.getByText("data")).toBeInTheDocument();
    });
  });
});
