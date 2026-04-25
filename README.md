# 💬 LAN Chat System (Java)

A multi-client **LAN-based chat application** built using Java Socket Programming.
The system supports **real-time messaging, file transfer, and user authentication** with a structured client-server architecture.

---

## 🚀 Features

* 🔐 **User Authentication**

  * Login & Registration system using MySQL
  * Retry-based authentication (no app restart needed)
  * Graceful exit handling

* 💬 **Real-Time Messaging**

  * Multiple clients can communicate simultaneously
  * Broadcast messaging across connected users

* 📁 **File Transfer**

  * Send and receive files over the network
  * Automatic file saving on receiver side

* 🧵 **Multi-threaded Server**

  * Each client handled in a separate thread
  * Supports concurrent connections efficiently

* 🖥️ **GUI Interface**

  * Built using Java Swing
  * Interactive chat window with message display

---

## 🛠️ Tech Stack

* **Java (Core + Swing)**
* **Socket Programming (TCP)**
* **Multithreading**
* **MySQL (JDBC)**

---

## 🧠 System Architecture

```
Client (GUI) 
   ↕ (Sockets)
Server (Multi-threaded)
   ↕
Database (MySQL - User Authentication)
```

* The server listens for incoming client connections.
* Each client is handled in a separate thread (`ClientHandler`).
* Authentication is performed before entering the chat system.
* Communication is handled via a custom protocol using `DataInputStream` and `DataOutputStream`.

---

## 🔄 Authentication Flow

```
Start Client
   ↓
Login / Register
   ↓
[Success] → Enter Chat
[Fail] → Retry
[Register] → Redirect to Login
[Cancel/Close] → Exit Application
```

---

## ▶️ How to Run

### 1. Setup Database

Run the following SQL:

```sql
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50)
);
```

---

### 2. Start Server

```bash
javac Server.java
java Server
```

---

### 3. Start Client

```bash
javac Client.java
java Client
```

---

## 📂 Project Structure

```
LAN-Chat-System/
│
├── src/
│   ├── Client.java
│   ├── Server.java
│   ├── ClientHandler.java
│   ├── DatabaseManager.java
│   ├── ChatClientGUI.java
│
├── screenshots/
├── README.md
└── .gitignore
```

---

## 📸 Screenshots

* Login / Register dialog
![alt text](image.png)
![alt text](image-1.png)
![alt text](image-2.png)
* Chat window
![alt text](image-3.png)
* File transfer example
![alt text](image-4.png)
![alt text](image-6.png)

---

## ⚙️ Key Implementation Details

* Custom message protocol using integer codes:

  * `TEXT`, `FILE`, `LOGIN`, `REGISTER`, etc.
* File transfer handled via byte streams
* Authentication integrated into socket lifecycle
* Client retries authentication using fresh connections
* GUI runs on separate thread to avoid blocking

---

## 📌 Future Improvements

* 👤 Online users list
* 💬 Private messaging
* 🗂️ Chat history persistence (database)
* 🔐 Password hashing (SHA-256 / bcrypt)
* 🎨 Dedicated login UI (instead of popups)

---

## 🧪 Limitations

* Passwords are stored in plain text (for simplicity in LAN environment)
* No encryption for message transfer
* Designed primarily for local network usage



