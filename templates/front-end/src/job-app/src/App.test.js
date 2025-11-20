// src/App.test.js
import { render, screen, waitFor } from '@testing-library/react';
import App from './App';

// Mock della fetch API
global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve([{ job: "Sviluppatore", company: "ABC" }, { job: "Designer", company: "XYZ" }]),
  })
);

describe('App Component', () => {
  it('renders loading state initially', () => {
    render(<App />);
    expect(screen.getByText(/Caricamento.../)).toBeInTheDocument();
  });

  it('renders data after fetch', async () => {
    render(<App />);
    await waitFor(() => expect(screen.getByText("Sviluppatore")).toBeInTheDocument());
    expect(screen.getByText("Designer")).toBeInTheDocument();
  });

  it('renders the table headers', async () => {
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText("job")).toBeInTheDocument();
      expect(screen.getByText("company")).toBeInTheDocument();
    });
  });
});
