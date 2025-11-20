// Mock della fetch per TUTTI i test

console.log("setupTests.js LOADED");


global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve([
      {
        id: 1,
        project: "Project-Test",
        startdate: "2024-01-01T10:00:00",
        enddate: "2024-01-01T12:00:00",
        status: "DONE",
        data: "Sample data"
      }
    ]),
  })
);