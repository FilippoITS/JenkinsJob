// src/setupTests.js
console.log("setupTests.js LOADED");

if (!globalThis.fetch) {
  globalThis.fetch = jest.fn(() => Promise.resolve({ json: () => Promise.resolve([

  {
          id: 1,
          project: "Project-Test",
          startdate: "2024-01-01T10:00:00",
          enddate: "2024-01-01T12:00:00",
          status: "DONE",
          data: "Sample data"
            }

        ])
    })
  );
}