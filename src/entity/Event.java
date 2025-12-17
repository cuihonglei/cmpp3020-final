package entity;

import java.util.List;

public class Event {
    private String name;
    private int attendees;
    private List<String> requiredEquipment;

    public Event(String name, int attendees, List<String> requiredEquipment) {
        this.name = name;
        this.attendees = attendees;
        this.requiredEquipment = requiredEquipment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees=attendees;
    }

    public List<String> getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(List<String> requiredEquipment) {
        this.requiredEquipment=requiredEquipment;
    }
}

