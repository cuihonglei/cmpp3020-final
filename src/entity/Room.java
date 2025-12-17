package entity;

import exception.CapacityExceededException;

import java.util.List;

public class Room {
    private String id;
    private int capacity;
    private List<String> equipment;
    private boolean occupied;

    public Room(String id, int capacity, List<String> equipment) {
        this.id = id;
        this.capacity = capacity;
        this.equipment = equipment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity=capacity;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment=equipment;
    }

    public synchronized void release() {
        occupied = false;
    }

    // Call after successful allocation
    public synchronized boolean occupy() {
        if (occupied) return false;
        this.occupied = true;
        return true;
    }

    // Encapsulate the business logic for determining whether the number of participants exceeds the room capacity
    public boolean canHost(Event event) throws CapacityExceededException {

        // Already occupied, reject directly.
        if (occupied) {
            return false;
        }

        // Insufficient capacity, throw an exception.
        if (event.getAttendees() > capacity) {
            throw new CapacityExceededException(
                    "Room " + id + " capacity (" + capacity + ") is less than event size (" + event.getAttendees() + ")", 405);
        }

        // Activity and room matching
        return equipment.containsAll(event.getRequiredEquipment());
    }
}
