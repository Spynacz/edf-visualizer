# EDF visualizer
EDF Visualizer is an application designed to visualize effects of the Earliest Deadline First (EDF) scheduling algorithm. It works in a client-server architecture, where the client sends tasks to be scheduled to the server and the server sends back a list of timeframes containing scheduled tasks.

Server communicates with an SQLite database using JDBC to store information about users and their tasks. After logging in, a user sees a list of all their saved tasks, and can add or remove them, as well as schedule added tasks for a specified number of timeframes.

### Client interface
![client](https://github.com/Spynacz/edf-visualizer/assets/63506536/431499c0-9d1e-4a9d-9bf1-02404dbdce79)
