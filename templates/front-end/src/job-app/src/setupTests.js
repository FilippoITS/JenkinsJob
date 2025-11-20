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
      },
      {
        id: 2,
        project: "Another-Project",
        startdate: "2024-02-02T08:30:00",
        enddate: "2024-02-02T11:00:00",
        status: "RUNNING",
        data: "Other sample"
      }
    ]),
  })
);
