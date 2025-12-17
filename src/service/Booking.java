package service;

import entity.Event;
import entity.Room;

public class Booking {
    private Event event;
    private Room room;

    public Booking(Event event, Room room) {
        this.event = event;
        this.room = room;
    }

    @Override
    public String toString() {
        return "Event '" + event.getName() + "' is assigned to room " + room.getId();
    }
}

