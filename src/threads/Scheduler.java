package threads;

import entity.Event;
import entity.Room;
import exception.CapacityExceededException;
import exception.InvalidEventException;
import exception.RoomUnavailableException;
import service.Booking;

import java.util.List;
import java.util.concurrent.*;

public class Scheduler {

    private List<Room> rooms;
    private ExecutorService executor;

    public Scheduler(List<Room> rooms) {
        this.rooms = rooms;
        // 固定大小线程池（标准教学 & 工程实践）
        this.executor = Executors.newFixedThreadPool(4);
    }

    public Booking schedule(Event event) throws InvalidEventException, RoomUnavailableException {

        validateEvent(event);

        CompletionService<Room> completionService = new ExecutorCompletionService<>(executor);

        //并行提交房间验证任务
        for (Room room : rooms) {
            completionService.submit(() -> {
                try {
                    if (room.canHost(event)) {
                        return room;
                    }
                } catch (CapacityExceededException e) {
                    System.out.println("Capacity check failed: " + e.getMessage() + ", code: "+e.getCode());
                }
                return null;
            });
        }

        // 按任务完成顺序获取结果
        for (int i = 0; i < rooms.size(); i++) {
            try {
                Future<Room> future = completionService.take();
                Room room = future.get();
                if (room != null && room.occupy()) {
                    return new Booking(event, room);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Validation error: " + e.getMessage());
            }
        }

        throw new RoomUnavailableException("No available room can host event: " + event.getName(), 403);
    }

    private void validateEvent(Event event) throws InvalidEventException {
        if (event.getAttendees() <= 0) {
            throw new InvalidEventException("Event attendee count must be greater than zero.", 404);
        }
    }


    public void shutdown() {
        executor.shutdown();
    }
}

