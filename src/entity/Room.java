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

    // Called after a room is successfully assigned
    public synchronized boolean occupy() {
        if (occupied) return false;
        this.occupied = true;
        return true;
    }

    // Encapsulates business validation logic:
    // checks whether this room can host the given event
    public boolean canHost(Event event) throws CapacityExceededException {

         // 1 Room is already occupied -> reject immediately
        if (occupied) {
            return false;
        }

        // 2 Capacity is insufficient -> throw exception
        if (event.getAttendees() > capacity) {
            throw new CapacityExceededException(
                    "Room " + id + " capacity (" + capacity + ") is less than event size (" + event.getAttendees() + ")", 405);
        }

        // 3 Check if the room satisfies the event's equipment requirements
        return equipment.containsAll(event.getRequiredEquipment());
    }
}
