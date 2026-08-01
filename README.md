# ForgeFlow

**ForgeFlow** is an enterprise desktop platform built with **JavaFX** and **Spring Boot** that enables teams to design workflows, schedule distributed jobs, analyze Java projects, and synchronize files across multiple systems.

It combines four major modules into a single developer productivity platform.

---

## Problem Statement

Modern development teams rely on multiple disconnected tools for workflow automation, task scheduling, project analysis, and file synchronization.

ForgeFlow unifies these capabilities into one extensible platform.

---

# Modules

## 1. ProcessFlow Studio

A visual workflow designer.

Users can drag and drop components to build automation workflows without writing code.

Example

```
Receive File
      ↓
Validate
      ↓
Compile
      ↓
Deploy
      ↓
Send Email
```

### Features

- Drag-and-drop editor
- Unlimited workflow creation
- Custom workflow blocks
- Nested workflows
- Save templates
- Version history
- Workflow validation
- Execution logs
- Real-time execution
- Import/Export JSON
- Workflow simulator

---

## 2. Distributed Task Scheduler

A cluster-aware scheduler.

Multiple Spring Boot instances cooperate to execute scheduled jobs.

### Features

- Cron scheduling
- One-time jobs
- Recurring jobs
- Retry policies
- Distributed locking
- Leader election
- Worker registration
- Worker heartbeat
- Failure detection
- Job priorities
- Job queues
- Execution history
- Dead Letter Queue
- Manual retry
- Pause / Resume
- Metrics Dashboard

Supported jobs

- HTTP Requests
- Shell Commands
- Java Methods
- Workflow Execution
- Email
- File Sync
- Custom Plugins

---

## 3. Code Dependency Explorer

Analyzes Java projects.

Upload any Maven or Gradle project.

ForgeFlow builds

- Dependency graph
- Package graph
- Circular dependency detector
- Dead code detector
- Architecture visualization
- Complexity report

Metrics

- LOC
- Cyclomatic Complexity
- Fan In
- Fan Out
- Package Coupling
- Class Coupling
- Layer Violations

Output

- HTML Report
- PDF Report
- JSON
- Graph Images

---

## 4. LocalSync

Enterprise LAN synchronization.

Synchronizes folders between multiple computers.

### Features

- Folder synchronization
- Delta Sync
- Conflict Detection
- Version History
- File Encryption
- Compression
- LAN Discovery
- Resume Interrupted Transfer
- Folder Permissions
- Audit Logs

---

# High Level Architecture

```
                  JavaFX Desktop

     ┌─────────────────────────────────┐
     │ Dashboard                       │
     │ Workflow Designer               │
     │ Scheduler                       │
     │ Dependency Explorer             │
     │ LocalSync                       │
     └─────────────────────────────────┘
                  │
                  │ REST
                  │
         Spring Boot Backend
                  │
     ┌────────────┴────────────┐
     │                         │
 PostgreSQL              Redis Cache
     │                         │
     └────────────┬────────────┘
                  │
             Worker Nodes
                  │
      Execute Distributed Jobs
```

---

# Technology Stack

## Frontend

- JavaFX
- FXML
- CSS
- ControlsFX

---

## Backend

- Java 21
- Spring Boot 3
- Spring MVC
- Spring Security
- Spring Scheduler
- Spring WebSocket
- Spring Data JPA

---

## Database

- PostgreSQL

---

## Cache

- Redis

---

## Messaging

- RabbitMQ

---

## Build

- Maven

---

## Authentication

- JWT
- BCrypt

---

## Reports

- JasperReports
- Apache POI

---

## File Storage

- Local Storage
- MinIO (Optional)

---

## Monitoring

- Micrometer
- Spring Boot Actuator

---

## Testing

- JUnit 5
- Mockito
- Testcontainers

---

# User Roles

Administrator

Can

- Create workflows
- Manage workers
- Schedule jobs
- Configure sync
- View reports
- Manage users

Developer

Can

- Analyze projects
- Execute workflows
- Schedule personal jobs
- Sync folders

Viewer

Read-only access.

---

# Functional Requirements

## Authentication

- Login
- Logout
- JWT Authentication
- Remember Me

---

## Dashboard

- Recent Jobs
- Running Jobs
- Failed Jobs
- Worker Status
- CPU Usage
- Memory Usage

---

## Workflow Engine

- Drag Nodes
- Connect Nodes
- Save
- Execute
- Validate
- Export

---

## Scheduler

- Create Job
- Delete Job
- Retry Job
- Pause Job
- Resume Job

---

## Dependency Explorer

- Upload Project
- Parse Source Code
- Build Graph
- Generate Report

---

## LocalSync

- Connect Devices
- Share Folder
- Sync
- Resolve Conflict
- Restore Versions

---

# Non Functional Requirements

- Multi-threaded
- Secure
- Responsive UI
- Modular Architecture
- Extensible Plugin System
- Cross Platform
- High Availability
- Fault Tolerant

---

# Database Tables

users

roles

permissions

workflows

workflow_nodes

workflow_edges

jobs

job_history

worker_nodes

worker_logs

sync_devices

sync_jobs

files

file_versions

dependency_projects

dependency_classes

dependency_edges

reports

audit_logs

notifications

---

# Design Patterns Used

Factory

Strategy

Builder

Observer

Singleton

Command

Chain of Responsibility

Repository

MVC

State

Decorator

Adapter

Facade

---

# Workflow

User Login

↓

Dashboard

↓

Create Workflow

↓

Save Workflow

↓

Schedule Workflow

↓

Leader Assigns Job

↓

Worker Executes

↓

Result Stored

↓

Notification Sent

↓

Dashboard Updated

---

# Future Enhancements

- Kubernetes Worker Discovery
- AI Workflow Suggestions
- LLM Code Analysis
- Cloud Synchronization
- Mobile Companion App
- GitHub Integration
- Jenkins Integration
- Docker Execution
- Plugin Marketplace

---

# Folder Structure

```
forgeflow/

├── frontend/

│ ├── dashboard/

│ ├── workflow/

│ ├── scheduler/

│ ├── dependency/

│ ├── sync/

│ └── auth/

├── backend/

│ ├── controller/

│ ├── service/

│ ├── repository/

│ ├── security/

│ ├── scheduler/

│ ├── websocket/

│ ├── workflow/

│ ├── dependency/

│ ├── sync/

│ └── worker/

├── shared/

├── docs/

├── database/

├── docker/

└── scripts/

```

---

# Skills Demonstrated

- Java
- Spring Boot
- JavaFX
- Distributed Systems
- Concurrency
- Scheduling
- WebSockets
- REST APIs
- Authentication
- File Systems
- Graph Algorithms
- Static Code Analysis
- Multi-threading
- Software Architecture
- Design Patterns
- Database Design
- Enterprise Application Development