
# cmpp3020-final
# Project Description

## 1. Introduction
The Smart Campus Event Scheduler is a Java application designed to assign campus rooms to student events while respecting capacity, equipment and occupation status.

The system allows users to:  
- Manage rooms and events  
- Schedule events to suitable rooms  
- Validate room availability in parallel  
- Handle invalid inputs and scheduling failures  
- View detailed scheduling logs  
- Clear all bookings  

This project demonstrates core programming concepts including object-oriented design, parallel programming, and exception handling.

---

## 2. Programming Language Choice
Choose Java for this project because:  
- It provides strong support for object-oriented programming  
- It includes built-in libraries for concurrency and parallel execution (ExecutorService, CompletionService)  
- Its strict type system helps ensure robust error handling  
- Java is commonly used in enterprise and academic systems for scheduling and resource management problems  

---

## 3. System Design Overview  

### 3.1 Room  
Manage a physical campus room.  
Responsibilities:  
- Store room capacity and available equipment  
- Track occupancy status  
- Provide thread-safe methods to occupy and release a room  

### 3.2 Event  
Manage a student event.  
Responsibilities:  
- Store event name, number of attendees, and required equipment  
- Provide validated access to event data  

### 3.3 Scheduler  
Core scheduling engine of the system.  
Responsibilities:  
- Validate events  
- Assign events to rooms without conflicts  
- Perform parallel room validation  
- Record scheduling logs  
- Handle scheduling failures  

Key features:  
- Uses a fixed thread pool (ExecutorService)  
- Validates room suitability concurrently  
- Uses CompletionService to process results as they complete  

### 3.4 Booking  
Represents a successful assignment of an event to a room.  

### 3.5 Exception Handling Design  
This system uses customize exceptions to clearly represent different failure scenarios during event scheduling.  
Each exception includes a descriptive message and an error code to support clear debugging and error reporting.  

#### 3.5.1 InvalidEventException  
Purpose: Thrown when an event request contains invalid data.  
Typical Scenarios:  
- Event attendee count is zero or negative  
- Event input does not meet basic validation rules  

#### 3.5.2 CapacityExceededException  
Purpose:  
Thrown when a room does not have sufficient capacity to host an event.  
Typical Scenarios:  
- Event attendee count exceeds the room’s maximum capacity  

#### 3.5.3 RoomUnavailableException  
Purpose:  
Thrown when no available room can host a given event after all validation attempts.  
Typical Scenarios:  
- All rooms are occupied  
- No room satisfies both capacity and equipment requirements  

### 3.6 Main  
Command-line UI.  
Responsibilities:  
- Handle user input  
- Manage menus and program flow  
- Trigger add/remove rooms, add/remove events, scheduling and system operations  

---

## 4. Parallel Programming Component  
Parallel programming is implemented in the Scheduler class.  
How it works:  
- Each room is validated concurrently using a thread pool  
- Room checks run in parallel to improve efficiency  
- The first suitable available room is assigned to the event  

Technologies used:  
- ExecutorService  
- CompletionService  
- Thread-safe synchronization (synchronized methods)  

This approach simulates real-world concurrent scheduling scenarios where multiple resources are checked simultaneously.  

---

## 5. Exception Handling Strategy  
The system uses custom exceptions to handle error scenarios clearly and safely:  
Custom Exceptions:  
- InvalidEventException – triggered when event data is invalid  
- CapacityExceededException – triggered when room capacity is insufficient  
- RoomUnavailableException – triggered when no room can host an event  

Exception Handling Features:  
- Try/catch blocks in core scheduling logic  
- Meaningful error messages  
- Errors are logged and reported, prevent crashing the program  

---

## 6. How to Run the Program  
Requirements:  
- Java 8 or later  

Steps:  
1. Compile all `.java` files  
2. Run the Main class in VS Code or input `java src/Main.java` in TERMINAL  
3. Use the menu to:  
   - Load test data  
   - Add rooms and events  
   - Schedule events  
   - View logs  
   - Clear all bookings  

---

This project demonstrates a complete, well-structured scheduling system using Java, showcasing object-oriented design, concurrency, and robust error handling in a realistic application scenario.
