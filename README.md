# Web HTTP Socket Server – Task Manager with Docker

## 1. Project Overview

This project implements a Java-based socket server that receives client requests, executes different computational tasks, and returns the results.

The server uses Java's `ExecutorService` with a cached thread pool to handle multiple client requests concurrently. Each accepted client connection is assigned to a worker thread, allowing the server to process multiple requests without waiting for previous requests to finish.

The project also includes Docker support, allowing the server to be compiled and executed inside a container.

## 2. Technologies Used

- **Java:** Application development and task implementation.
- **Java Socket API:** Client-server communication.
- **ExecutorService:** Concurrent request processing.
- **Multithreading:** Handling multiple client requests simultaneously.
- **Docker:** Containerization and deployment.

## 3. Project Structure

```text
web-http-socket-server-task-manager-docker/
│
├── Dockerfile
├── README.md
│
└── src/
    ├── WebServerHttp.java
    ├── WebServerSocket.java
    │
    ├── handler/
    │   ├── RequestsHandlerHttp.java
    │   └── RequestsHandlerSocket.java
    │
    ├── singleton/
    │   ├── TaskExecutor.java
    │   └── TasksList.java
    │
    └── task/
        ├── Task.java
        ├── TaskImpl.java
        ├── TaskBitcoin.java
        ├── TaskBubbleSort.java
        ├── TaskFiboRecursive.java
        ├── TaskIncorrectParams.java
        ├── TaskNotFound.java
        └── TaskPrimeNumbers.java
```

## 4. Main Components

### WebServerSocket

The main socket server listens for incoming client connections on port `8000`.

It uses:

```java
Executors.newCachedThreadPool();
```

When a client connects, the server creates a request handler and submits it to the executor service.

### RequestsHandlerSocket

This class implements `Runnable` and handles the communication with each connected client.

It processes incoming GET and POST requests and delegates task execution to `TaskExecutor`.

The implementation also logs the worker thread name and request execution time, making concurrent execution observable.

### TaskExecutor

This component is responsible for identifying and executing the requested task.

It uses Java Reflection to instantiate the appropriate task class dynamically, based on the task name received from the client.

### TasksList

This component stores the executed tasks.

A synchronized list is used to protect shared task data when multiple worker threads access it concurrently.

### TaskPrimeNumbers

A new computational task was added to calculate the number of prime numbers between 2 and a specified upper limit.

It uses the **Sieve of Eratosthenes** algorithm and Java's `BitSet` to efficiently identify prime numbers.

The accepted input range is from 2 to 100,000,000.

## 5. Multithreading and Concurrent Execution

The server uses Java's `newCachedThreadPool()` to manage worker threads.

Its main characteristics are:

- It creates new threads when existing threads are busy and additional requests arrive.
- It reuses previously created idle threads.
- It does not impose a fixed upper limit on the number of threads.

Each request handler executes its assigned task within its worker thread.

This allows multiple computational tasks to run concurrently when several clients send requests at the same time.

The server logs include:

```text
[START] Request is being handled by: pool-1-thread-2
[END] Request handled by: pool-1-thread-2 | Time: 1279 ms
```

These messages help demonstrate which worker thread handled each request and how long the request took.

## 6. Running the Project Locally

### Requirements

- Java Development Kit (JDK 8 or later).
- Windows PowerShell or another terminal.

### Step 1: Compile the source code

Open a terminal in the project root directory and execute:

```powershell
javac -d out src\*.java src\handler\*.java src\singleton\*.java src\task\*.java
```

### Step 2: Start the socket server

```powershell
java -cp out WebServerSocket
```

The expected output is:

```text
WebServerSocket is listening on port 8000
```

### Step 3: Send a client request

Open another terminal and execute:

```powershell
curl.exe -s --http0.9 -X POST `
  -H "Content-Type: text/plain" `
  --data-binary "TaskPrimeNumbers&1000000" `
  http://localhost:8000
```

Expected result:

```text
Result: Number of prime numbers between 2 and 1000000 is: 78498; Executed in: ...
```

## 7. Testing Concurrent Requests

To test concurrent execution, multiple clients can send requests simultaneously.

The following PowerShell script sends four requests:

```powershell
$jobs = 1..4 | ForEach-Object {
    Start-Job -ArgumentList $_ -ScriptBlock {
        param($id)

        curl.exe -s --http0.9 -X POST `
          -H "Content-Type: text/plain" `
          --data-binary "TaskPrimeNumbers&100000000" `
          http://localhost:8000 2>$null
    }
}

$jobs | Wait-Job | Out-Null
$jobs | Receive-Job
$jobs | Remove-Job
```

The server logs should show requests being handled by different worker threads.

This test demonstrates concurrent request handling and thread reuse.

## 8. Running the Project with Docker

### Build the Docker image

From the project root directory, execute:

```powershell
docker build -t socket-task-manager .
```

### Run the container

```powershell
docker run --rm -p 8000:8000 --name socket-task-manager-container socket-task-manager
```

The server will start inside the Docker container and listen on port `8000`.

### Test the container from the host machine

Open another PowerShell window and execute:

```powershell
curl.exe -s --http0.9 -X POST `
  -H "Content-Type: text/plain" `
  --data-binary "TaskPrimeNumbers&1000000" `
  http://localhost:8000
```

The request is sent from the host machine to the server running inside the container through Docker's port mapping.

### View Docker images

```powershell
docker images
```

## 9. Main Results

The project was tested through the following scenarios:

1. Compiling and running the Java socket server locally.
2. Executing the newly implemented prime-number task.
3. Sending multiple requests concurrently.
4. Observing worker thread names and execution times in the server logs.
5. Building the Docker image successfully.
6. Running the server inside a Docker container.
7. Sending requests from the host machine to the container.

The tests confirmed that the server can process multiple client requests concurrently and operate successfully inside Docker.

## 10. Conclusion

This project demonstrates the practical use of Java socket programming, multithreading, and thread pool management.

By using `ExecutorService` and a cached thread pool, the server can handle multiple client connections concurrently while reusing available worker threads.

The addition of the prime-number task provides a computational workload that makes concurrent execution easier to observe and test.

Finally, Docker provides a portable environment for building and running the application.
