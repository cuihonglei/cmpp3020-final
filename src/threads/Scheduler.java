package threads;

import entity.Event;
import entity.Room;
import exception.CapacityExceededException;
import exception.InvalidEventException;
import exception.RoomUnavailableException;
import service.Booking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Scheduler {

    private List<Room> rooms;
    private ExecutorService executor;

    public Scheduler(List<Room> rooms) {
        this.rooms = rooms;
        // Fixed thread pool size
        this.executor = Executors.newFixedThreadPool(4);
    }

    public Booking schedule(Event event)
            throws InvalidEventException, RoomUnavailableException {

        // 1. Validate event validity
        validateEvent(event);
        logs.add("[SCHEDULE] Start scheduling event: " + event.getName());

        // 2. Create a Callable task for each Room.
        List<Callable<Room>> tasks = rooms.stream()
                .map(room -> (Callable<Room>) () -> {
                    try {
                        // Can it accommodate + atomic occupancy
                        if (room.canHost(event) && room.occupy()) {
                            logs.add("[SUCCESS][" + Thread.currentThread().getName() + "] "+ "Event " + event.getName()+ " assigned to room " + room.getId());
                            return room; // Success: invokeAny will return immediately.
                        }
                    } catch (CapacityExceededException e) {
                        // This is converted to a runtime exception, allowing invokeAny to continue attempting other tasks.
                        System.out.println("Capacity check failed: " + e.getMessage() + ", code: "+e.getCode());   
                        logs.add("[CAPACITY_FAIL][" + Thread.currentThread().getName() + "] "+ "Room " + room.getId()+ " cannot host event " + event.getName());
                    }

                    // Inappropriate: An exception must be thrown; null cannot be returned.
                    throw new RoomUnavailableException("Room not suitable", 403);
                })
                .collect(Collectors.toList());

        // 3. Execute tasks in parallel and return the first successful Room.
        try {
            Room room = executor.invokeAny(tasks);
            return new Booking(event, room);

        } catch (InterruptedException | ExecutionException e) {
            // All tasks fail, or the thread is interrupted.
            throw new RoomUnavailableException(
                    "No available room can host event: " + event.getName(), 403);
        }
    }


    private void validateEvent(Event event) throws InvalidEventException {
        if (event.getAttendees() <= 0) {
            throw new InvalidEventException("Event attendee count must be greater than zero.", 404);
        }
    }


    public void shutdown() {
        executor.shutdown();
    }

    // Thread-safe log list (readable by Main)
    private final List<String> logs =
            Collections.synchronizedList(new ArrayList<>());

    // call log
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public void clearLogs() {
        logs.clear();
        logs.add("[SYSTEM] Logs cleared");
    }
}

