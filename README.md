# 🚚 Cold Chain Fleet Tracking System

A Java-based Cold Chain Fleet Tracking and Monitoring System designed to monitor shipment temperatures, track GPS locations, detect temperature violations, and maintain compliance audit records.

## 📌 Project Overview

The Cold Chain Fleet Tracking System helps monitor temperature-sensitive shipments during transportation. It tracks real-time-style sensor telemetry, checks whether shipment temperatures remain within the permitted range, and identifies potential cold chain violations.

When a shipment exceeds its permitted temperature range for more than 15 minutes, the system automatically marks the shipment as **COMPROMISED**, calculates a penalty, and stores the violation details in the database.

It also calculates the distance between the shipment's current GPS location and its destination to generate urgent warehouse alerts.

## ✨ Key Features

- **Shipment Management:** Retrieve shipment information, including origin, destination, temperature limits, and shipment status.
- **Temperature Monitoring:** Monitor shipment temperatures using sensor telemetry data.
- **Temperature Breach Detection:** Detect temperatures outside the permitted range.
- **15-Minute Compliance Rule:** Identify continuous temperature violations exceeding 15 minutes.
- **Automatic Status Update:** Automatically update compromised shipments to `COMPROMISED`.
- **Penalty Calculation:** Calculate penalties based on the duration of the temperature violation beyond the 15-minute threshold.
- **GPS Location Tracking:** Track shipment coordinates using latitude and longitude.
- **Distance Calculation:** Calculate the distance between the current shipment location and its destination.
- **Warehouse Alerts:** Generate urgent alerts when compromised shipments are within 25 km of their destination.
- **Audit Logging:** Store temperature breach details, peak temperature, duration, and penalty information.
- **Database Integration:** Store shipment, telemetry, and audit records using MySQL.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Core application development |
| JDBC | Database connectivity |
| MySQL | Data storage and management |
| MySQL Spatial Functions | GPS location and distance calculations |
| Eclipse IDE | Development environment |
| OOP Concepts | Object-oriented application design |

## 📂 Project Structure

```text
Cold_Chain_Fleet/
│
├── src/
│   └── com/
│       └── coldchain/
│           ├── Main.java
│           ├── ComplianceAuditor.java
│           ├── DatabaseConnection.java
│           ├── Shipment.java
│           ├── TelemetryPacket.java
│           └── AuditBreachLog.java
│
└── README.md
```

## ⚙️ How It Works

1. The application retrieves shipment information from the MySQL database.
2. Sensor telemetry provides temperature, GPS coordinates, and recorded timestamps.
3. The system compares the received temperature against the shipment's permitted temperature range.
4. If the temperature exceeds the permitted range, the system starts tracking the breach duration.
5. If the continuous breach exceeds 15 minutes:
   - The shipment status changes to `COMPROMISED`.
   - The peak temperature is recorded.
   - The penalty is calculated.
   - An audit breach record is stored in MySQL.
6. The system calculates the distance to the destination.
7. If a compromised shipment is within 25 km of its destination, an urgent warehouse alert is generated.
8. Finally, the application displays shipment status, telemetry count, and audit logs.

## 🗄️ Database Configuration

**Database Name:** `cold_chain_db`

The application uses three database tables:

- `shipments` – Stores shipment information and temperature limits.
- `sensor_telemetry` – Stores sensor temperature and GPS tracking data.
- `audit_breach_logs` – Stores temperature violation and penalty details.

Configure your MySQL database connection in `DatabaseConnection.java`.

Update the database username and password according to your MySQL configuration. Create the required tables and insert a shipment record with ID `1` before running the simulation.

The project uses MySQL spatial functions, so use a compatible MySQL version with spatial support.

## ▶️ How to Run the Project

**Prerequisites**

- Java JDK 21
- Eclipse IDE
- MySQL Server
- MySQL Connector/J

**Execution Steps**

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/Cold-Chain-Fleet.git
   ```

2. Open Eclipse IDE.
3. Import the project as an existing Java project.
4. Configure the MySQL Connector/J library in the project build path.
5. Create the `cold_chain_db` database and required tables.
6. Configure the database credentials in `DatabaseConnection.java`.
7. Ensure a valid shipment with ID `1` exists in the database.
8. Open `Main.java`.
9. Right-click and select **Run As → Java Application**.

## 🧪 Sample Simulation

The project demonstrates a shipment temperature monitoring scenario:

| Time | Temperature | Condition |
|---|---:|---|
| 10:00 AM | 4.5°C | Normal |
| 10:05 AM | 9.2°C | Breach Started |
| 10:15 AM | 10.5°C | Breach Continued |
| 10:25 AM | 11.0°C | Violation Detected |
| 10:35 AM | 5.0°C | Temperature Normal |

The simulation demonstrates how a continuous temperature breach exceeding 15 minutes triggers a compliance violation.

## 🎯 Project Objective

The main objective of this project is to improve cold chain transportation monitoring by identifying temperature violations, calculating financial penalties, tracking shipment locations, and maintaining compliance records.

## 👨‍💻 Developer

**Yuvaraj P**

Java Full Stack Developer

**Technologies:** Java | JDBC | MySQL | OOP | SQL

---

⭐ If you find this project interesting, feel free to explore the repository!
