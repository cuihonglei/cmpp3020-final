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
        // ⭐ 新增：记录开始调度
        logs.add("[SCHEDULE] Start scheduling event: " + event.getName());

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
                    // ⭐ 新增：失败写入日志
                    logs.add("[CAPACITY_FAIL][" + Thread.currentThread().getName() + "] "+ "Room " + room.getId()+ " cannot host event " + event.getName());
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
                    // ⭐ 新增：成功写入日志
                    logs.add("[SUCCESS][" + Thread.currentThread().getName() + "] "+ "Event " + event.getName()+ " assigned to room " + room.getId());
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

    // ✅ 线程安全日志列表（可被 Main 读取）
    private final List<String> logs =
            Collections.synchronizedList(new ArrayList<>());

    // =========================
    // 日志接口（给 Main 调用）
    // =========================
    public List<String> getLogs() {
        return new ArrayList<>(logs); 
    }

    public void clearLogs() {
        logs.clear();
        logs.add("[SYSTEM] Logs cleared");
    }
    // =========================

    public void shutdown() {
        executor.shutdown();
    }
}


