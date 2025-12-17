import entity.Event;
import entity.Room;
import exception.InvalidEventException;
import exception.RoomUnavailableException;
import service.Booking;
import threads.Scheduler;

import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final List<Room> rooms = new ArrayList<>();
    private static final List<Event> events = new ArrayList<>();
    private static final List<String> logs = new ArrayList<>();

    private static Scheduler scheduler;


    

    public static void main(String[] args) {

        scheduler = new Scheduler(rooms);

        boolean running = true;

        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    addRoom();
                    break;
                case 2:
                    removeRoom();
                    break;
                case 3:
                    viewRooms();
                    break;
                case 4:
                    addEvent();
                    break;
                case 5:
                    removeEvent();
                    break;
                case 6:
                    viewEvents();
                    break;
                case 7:
                    scheduleEvent();
                    break;
                case 8:
                    scheduleEvents();
                    break;
                case 9:
                    clearAllBookings();
                    break;
                case 10:
                    viewLogs();
                    break;
                case 11:
                    loadTestData();
                    break;
                case 12:
                    running = false;
                    System.out.println("System exited. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1–11.");
            }
        }

        scheduler.shutdown();
    }

    // =========================
    // Menu
    // =========================

    private static void printMenu() {
        System.out.println("\n            Smart Campus Event Scheduler             ");
        System.out.println(" —————————————————————————————————————————————————————");
        System.out.println("| 1. Add room                                         |");
        System.out.println("| 2. Remove room                                      |");
        System.out.println("| 3. View rooms                                       |");
        System.out.println("| 4. Add event                                        |");
        System.out.println("| 5. Remove event                                     |");
        System.out.println("| 6. View events                                      |");
        System.out.println("| 7. Schedule single event                            |");
        System.out.println("| 8. Batch schedule events                            |");
        System.out.println("| 9. Clear all bookings                               |");
        System.out.println("| 10. View scheduling logs                            |");
        System.out.println("| 11. Load test data                                  |");
        System.out.println("| 12. Exit                                            |");
        System.out.println(" —————————————————————————————————————————————————————");
        System.out.print("Enter your choice: ");
    }

    private static int readMenuChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    // =========================
    // Room operations
    // =========================

    private static void addRoom() {
        System.out.println("\n--- Add Room ---");

        String id;
        while (true) {
            System.out.print("Room ID (e.g., R101): ");
            String checkId = scanner.nextLine().trim();

            // 1. Check if it matches 'R' followed by one or more digits
            if (!checkId.matches("R\\d+")) {
                System.out.println("Room ID must start with 'R' followed by a positive integer (e.g., R1).");
                continue;
            }

            // 2. Extract the number part (everything after the 'R') to check if it's > 0
            int idValue = Integer.parseInt(checkId.substring(1));
            if (idValue <= 0) {
                System.out.println("Room ID number must be greater than 0.");
                continue;
            }

            // 3. Check for uniqueness
            boolean exists = rooms.stream()
                    .anyMatch(r -> r.getId().equalsIgnoreCase(checkId));

            if (exists) {
                System.out.println("Room ID already exists.");
                continue;
            }

            id = checkId;
            break;
        }

        int capacity = readPositiveInt("Capacity (>0): ");

        System.out.print("Equipment (comma separated): ");
        String eqLine = scanner.nextLine();
        List<String> equipment;
        if (eqLine.isEmpty()) {
            equipment = Collections.emptyList(); // Creates a truly empty list
        } else {
            equipment = Arrays.asList(eqLine.split(","));
        }

        rooms.add(new Room(id, capacity, equipment));
        logs.add("Room added: " + id);

        System.out.println("Room added successfully.");
    }

    private static void removeRoom() {
        System.out.print("\nEnter room ID to remove: ");
        String id = scanner.nextLine();

        boolean removed = rooms.removeIf(r -> r.getId().equals(id));
        if (removed) {
            logs.add("Room removed: " + id);
            System.out.println("Room removed.");
        } else {
            System.out.println("Room not found.");
        }
    }

    private static void viewRooms() {
        System.out.println("\n--- Room List ---");
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
            return;
        }
        for (Room r : rooms) {
            System.out.println(
                    "Room ID: " + r.getId() +
                    ", Capacity: " + r.getCapacity() +
                    ", Equipment: " + r.getEquipment()
            );
        }
    }

    // =========================
    // Event operations
    // =========================

    private static void addEvent() {
        System.out.println("\n--- Add Event ---");

        System.out.print("Event name: ");
        String name = scanner.nextLine();

        int attendees = readPositiveInt("Attendees (>0): ");

        System.out.print("Required equipment (comma separated): ");
        String eqLine = scanner.nextLine();
        List<String> equipment;
        if (eqLine.isEmpty()) {
            equipment = Collections.emptyList(); // Creates a truly empty list
        } else {
            equipment = Arrays.asList(eqLine.split(","));
        }

        events.add(new Event(name, attendees, equipment));
        logs.add("Event added: " + name);

        System.out.println("Event added successfully.");
    }

    private static void removeEvent() {
        System.out.print("\nEnter event name to remove: ");
        String name = scanner.nextLine();

        boolean removed = events.removeIf(e -> e.getName().equals(name));
        if (removed) {
            logs.add("Event removed: " + name);
            System.out.println("Event removed.");
        } else {
            System.out.println("Event not found.");
        }
    }

    private static void viewEvents() {
        System.out.println("\n--- Event List ---");
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        for (Event e : events) {
            System.out.println(
                    "Event: " + e.getName() +
                    ", Attendees: " + e.getAttendees() +
                    ", Equipment: " + e.getRequiredEquipment()
            );
        }
    }

    // =========================
    // Scheduling
    // =========================

    private static void scheduleEvent() {
        System.out.println("\n--- Schedule Single Event ---");

        System.out.print("Event name: ");
        String name = scanner.nextLine();

        System.out.print("Attendees: ");
        int attendees = readPositiveInt("Attendees (>0): ");

        System.out.print("Required equipment (comma separated): ");
        String eqLine = scanner.nextLine();
        List<String> equipment;
        if (eqLine.isEmpty()) {
            equipment = Collections.emptyList(); // Creates a truly empty list
        } else {
            equipment = Arrays.asList(eqLine.split(","));
        }

        Event event = new Event(name, attendees, equipment);

        try {
            Booking booking = scheduler.schedule(event);
            String msg = "Scheduled: " + booking;
            System.out.println(msg);
            logs.add(msg);

        } catch (InvalidEventException e) {
            String msg = "Invalid event: " + event.getName() +
                    " - " + e.getMessage();
            System.out.println(msg);
            logs.add(msg);

        } catch (RoomUnavailableException e) {
            String msg = "Room unavailable for event: " +
                    event.getName() + " - " + e.getMessage();
            System.out.println(msg);
            logs.add(msg);
        }
    }

    private static void scheduleEvents() {
        System.out.println("\n--- Scheduling Events ---");

        if (events.isEmpty()) {
            System.out.println("No events to schedule.");
            return;
        }

        for (Event event : events) {
            try {
                Booking booking = scheduler.schedule(event);
                String msg = "Scheduled: " + booking;
                System.out.println(msg);
                logs.add(msg);

            } catch (InvalidEventException e) {
                String msg = "Invalid event: " + event.getName() +
                        " - " + e.getMessage();
                System.out.println(msg);
                logs.add(msg);

            } catch (RoomUnavailableException e) {
                String msg = "Room unavailable for event: " +
                        event.getName() + " - " + e.getMessage();
                System.out.println(msg);
                logs.add(msg);
            }
        }
    }

    // =========================
    // Cancel Scheduling
    // =========================
    private static void clearAllBookings() {
        System.out.println("\n--- Clearing All Bookings ---");

        for (Room room : rooms) {
            room.release();
        }

        System.out.println("All room bookings have been cleared.");
    }
    

    // =========================
    // Logs
    // =========================


    private static void viewLogs() {
        System.out.println("\n--- Scheduling Logs ---");
        List<String> schedulerLogs = scheduler.getLogs();
        if (schedulerLogs.isEmpty()) {
            System.out.println("No logs available.");
            return;
        }
        for (String log : schedulerLogs) {
            System.out.println(log);
        }
    }
    // =========================
    // Test Data
    // =========================

    private static void loadTestData() {
        System.out.println("\n--- Loading Test Data ---");

        rooms.clear();
        events.clear();
        //scheduler.clearLogs();

        // ===== Rooms =====
        rooms.add(new Room("R101", 30,
                Arrays.asList("projector", "whiteboard", "computer")));

        rooms.add(new Room("R102", 20,
                Arrays.asList("whiteboard", "pen")));

        rooms.add(new Room("R201", 50,
                Arrays.asList("TV", "projector", "computer", "piano")));

        // ===== Events =====
        events.add(new Event("Music Concert", 35,
                Arrays.asList("piano")));

        events.add(new Event("CS Lecture", 25,
                Arrays.asList("projector", "computer")));

        events.add(new Event("Team Meeting", 10,
                Arrays.asList("whiteboard", "pen")));

        events.add(new Event("Basketball Practice", 20,
                Arrays.asList("ball")));



        System.out.println("Test rooms loaded: " + rooms.size());
        System.out.println("Test events loaded: " + events.size());
    }
    // =========================
    // Utility
    // =========================

    private static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextInt()) {
                System.out.println("Must be a positive integer.");
                scanner.next();
                continue;
            }
            int value = scanner.nextInt();
            scanner.nextLine();
            if (value <= 0) {
                System.out.println("Must be greater than 0.");
            } else {
                return value;
            }
        }
    }
}



