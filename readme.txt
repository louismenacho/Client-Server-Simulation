Exam Day
Simulates an exam day utilizing threads and a client-server model.
Developed monitors to synchronize the threads in the context of the problem.
Client threads connect to server to participate in story.
Created a server that spawned threads to handle clients accordingly.

How to run:
1.Run server.jar
2.Run client.jar

Commands to JAR:
To launch Server: java -jar server.jar portNumber
To launch Client: java -jar client.jar hostName portNumber numStudent capacity numSeats

Example:
java -jar server.jar 1026
java -jar client.jar localhost 1026 16 12 3

A welcome message (Welcome to ExamDay) will be displayed to any client that connects.
When all methods finish executing, the client will terminate.
