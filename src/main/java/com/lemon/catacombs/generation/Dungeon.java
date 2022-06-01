package com.lemon.catacombs.generation;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.ConcurrentSet;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Handler;
import com.lemon.catacombs.generation.decoration.Room;
import com.lemon.catacombs.objects.rooms.Door;
import com.lemon.catacombs.objects.rooms.Wall;
import com.lemon.catacombs.objects.endless.CheckeredBackground;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dungeon {
    private final Set<Point> map;
    private final Set<Point> hallwayMap;
    private final Set<Room> rooms;
    private final ConcurrentSet<Hallway> hallways;
    private final int minRooms;
    private final int tileSize;
    private final int minRoomSize;
    private final int maxRoomSize;
    private int roomCount;
    private Point start, end;

    public Dungeon(int minRooms, int tileSize, int minRoomSize, int maxRoomSize) {
        this.minRooms = minRooms;
        this.tileSize = tileSize;
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;

        map = new HashSet<>();
        hallwayMap = new HashSet<>();
        hallways = new ConcurrentSet<>();
        rooms = new HashSet<>();
    }

    public boolean empty(int x, int y) {
        return !map.contains(new Point(x, y));
    }

    public boolean hallway(int x, int y) {
        return hallwayMap.contains(new Point(x, y));
    }

    public void tick() {
        for (Hallway hallway : hallways) {
            if (!hallway.extend()) {
                int[] directions = hallway.getTo();
                int j = Utils.intRange(0, directions.length);
                boolean ended = false;
                for (int i = 0; i < directions.length; i++) {
                    if (endHallway(hallway, directions[(i + j) % directions.length])) {
                        ended = true;
                        break;
                    }
                }
                if (ended) {
                    Hallway head = hallway;
                    while (head != null) {
                        map.add(head.getPosition());
                        hallwayMap.add(head.getPosition());
                        head = head.getParent();
                    }
                } else {
                    Hallway parent = hallway;
                    while (parent.getParent() != null) {
                        parent = parent.getParent();
                    }
                    Room room = parent.getRoom();
                    DoorMarker door = parent.getDoor();
                    assert room != null;
                    room.removeDoor(door);
                }
            } else {
                expandHallway(hallway);
            }
            hallways.delete(hallway);
        }
        hallways.commit();
    }

    public boolean endHallway(Hallway hallway, int direction) {
        int w = (int) (Utils.normal(minRoomSize, maxRoomSize));
        int h = (int) (Utils.normal(minRoomSize, maxRoomSize));
        while (w >= minRoomSize && h >= minRoomSize) {
            Point offset = getRoomOffset(direction, w, h);
            Point pos = hallway.getPosition();
            pos.translate(offset.x, offset.y);
            Room room = buildRoom(pos, w, h, direction, 1);
            if (room != null) {
                offset = getDoorOffset(direction, 1);
                pos = hallway.getPosition();
                pos.translate(offset.x, offset.y);
                DoorMarker door = new DoorMarker(pos.x, pos.y, Direction.flip(direction));
                room.addDoor(door);
                return true;
            }
            w--;
            h--;
        }
        return false;
    }

    public Room buildRoom(Point pos, int w, int h, int direction, int minHallwayCount) {
        for (int x = pos.x - 1; x <= pos.x + w; x++) {
            for (int y = pos.y - 1; y <= pos.y + h; y++) {
                if (!empty(x, y)) {
                    return null;
                }
            }
        }
        for (int x = pos.x; x < pos.x + w; x++) {
            for (int y = pos.y; y < pos.y + h; y++) {
                map.add(new Point(x, y));
            }
        }

        roomCount++;
        Room room = new Room(pos.x, pos.y, w, h);
        rooms.add(room);

        int[] directions;
        if (direction == -1) {
            directions = new int[] {
                    Direction.UP,
                    Direction.DOWN,
                    Direction.LEFT,
                    Direction.RIGHT
            };
        } else {
            directions = Direction.toFrom(direction);
        }

        double hallwayChance = roomCount / (4.0 * minRooms);
        if (roomCount < minRooms || Math.random() > hallwayChance) {
            int index = Utils.intRange(0, directions.length);
            int span = Utils.intRange(minHallwayCount, directions.length);
            for (int i = index; i <= index + span; i++) {
                int dir = directions[i % directions.length];
                Point offset = getHallwayOffset(dir, w, h, 1);
                Point hallwayPos = new Point(pos);
                hallwayPos.translate(offset.x, offset.y);

                Point noOffset = getHallwayOffset(dir, w, h, 0);
                Point noHallwayPos = new Point(pos);
                noHallwayPos.translate(noOffset.x, noOffset.y);

                DoorMarker door = new DoorMarker(noHallwayPos.x, noHallwayPos.y, dir);
                room.addDoor(door);

                hallways.add(new Hallway(hallwayPos.x, hallwayPos.y, Direction.flip(dir), room, door));
            }
        }
        return room;

    }

    public Point getRoomOffset(int direction, int w, int h) {
        switch (direction) {
            case Direction.LEFT:
                return new Point(-w, 0);
            case Direction.UP:
                return new Point(0, -h);
            case Direction.RIGHT:
                return new Point(1, 0);
            case Direction.DOWN:
                return new Point(0, 1);
            default:
                return new Point(0, 0);
        }
    }

    public Point getHallwayOffset(int direction, int w, int h, int offset) {
        switch (direction) {
            case Direction.RIGHT:
                return new Point(w - 1 + offset, h / 2);
            case Direction.DOWN:
                return new Point(w / 2, h - 1 + offset);
            case Direction.LEFT:
                return new Point(-offset, h / 2);
            case Direction.UP:
                return new Point(w / 2, -offset);
            default:
                return new Point(0, 0);
        }
    }

    public Point getDoorOffset(int direction, int offset) {
        switch (direction) {
            case Direction.RIGHT:
                return new Point(offset, 0);
            case Direction.DOWN:
                return new Point(0, offset);
            case Direction.LEFT:
                return new Point(-offset, 0);
            case Direction.UP:
                return new Point(0, -offset);
            default:
                return new Point(0, 0);
        }
    }

    public void expandHallway(Hallway hallway) {
        hallways.add(hallway.forward());
    }

    public int openings(Point pos) {
        int openings = 0;
        if (empty(pos.x + 1, pos.y)) {
            openings |= Direction.RIGHT;
        }
        if (empty(pos.x - 1, pos.y)) {
            openings |= Direction.LEFT;
        }
        if (empty(pos.x, pos.y + 1)) {
            openings |= Direction.DOWN;
        }
        if (empty(pos.x, pos.y - 1)) {
            openings |= Direction.UP;
        }
        return openings;
    }

    public void build(double wallThickness) {
        int thickness = (int) (wallThickness * tileSize);
        Handler world = Game.getInstance().getWorld();
        Set<Point> tiles = new HashSet<>();
        for (Point point : map) {
            Point p = new Point(point.x * tileSize, point.y * tileSize);
            tiles.add(p);
            if (!hallwayMap.contains(point)) continue;
            // Build hallway walls
            int openings = openings(point);
            boolean leftOpen = (openings & Direction.LEFT) == 0;
            boolean rightOpen = (openings & Direction.RIGHT) == 0;
            boolean upOpen = (openings & Direction.UP) == 0;
            boolean downOpen = (openings & Direction.DOWN) == 0;
            // Corners

            // Sides
            if (!upOpen) {
                world.addObject(new Wall(p.x, p.y, tileSize, thickness));
            }
            if (!downOpen) {
                world.addObject(new Wall(p.x, p.y + tileSize - thickness, tileSize, thickness));
            }
            if (!leftOpen) {
                world.addObject(new Wall(p.x, p.y, thickness, tileSize));
            }
            if (!rightOpen) {
                world.addObject(new Wall(p.x + tileSize - thickness, p.y, thickness, tileSize));
            }
        }
        for (Room room : rooms) {
            room.decorate(tileSize, thickness);
            Map<Point, DoorMarker> doors = new HashMap<>();
            for (DoorMarker door : room.getDoors()) {
                Point p = new Point(door.getX() * tileSize, door.getY() * tileSize);
                doors.put(p, door);
            }
            // Build horizontal walls
            for (int i = 0; i < room.getW(); i++) {
                Point p = new Point(room.getX() * tileSize + i * tileSize, room.getY() * tileSize);
                Point p2 = new Point(p.x, p.y + (room.getH() - 1) * tileSize);

                Wall wall = new Wall(p.x, p.y - thickness, tileSize, thickness);
                Wall wall2 = new Wall(p2.x, p2.y + tileSize, tileSize, thickness);

                if (!doors.containsKey(p) || doors.get(p).getDirection() != Direction.UP) {
                    world.addObject(wall);
                } else {
                    Door door = new Door(p.x, p.y - thickness, tileSize, thickness, true);
                    world.addObject(door);
                }
                if (!doors.containsKey(p2) || doors.get(p2).getDirection() != Direction.DOWN) {
                    world.addObject(wall2);
                } else {
                    Door door = new Door(p2.x, p2.y + tileSize, tileSize, thickness, true);
                    world.addObject(door);
                }
            }
            // Build vertical walls
            for (int i = 0; i < room.getH(); i++) {
                Point p = new Point(room.getX() * tileSize, room.getY() * tileSize + i * tileSize);
                Point p2 = new Point(p.x + (room.getW() - 1) * tileSize, p.y);

                Wall wall = new Wall(p.x - thickness, p.y, thickness, tileSize);
                Wall wall2 = new Wall(p2.x + tileSize, p2.y, thickness, tileSize);

                if (!doors.containsKey(p) || doors.get(p).getDirection() != Direction.LEFT) {
                    world.addObject(wall);
                } else {
                    Door door = new Door(p.x - thickness, p.y, thickness, tileSize, false);
                    world.addObject(door);
                }
                if (!doors.containsKey(p2) || doors.get(p2).getDirection() != Direction.RIGHT) {
                    world.addObject(wall2);
                } else {
                    Door door = new Door(p2.x + tileSize, p2.y, thickness, tileSize, false);
                    world.addObject(door);
                }
            }
            // Add corners
            Point p = new Point(room.getX() * tileSize, room.getY() * tileSize);
            Point p2 = new Point(p.x + room.getW() * tileSize, p.y + room.getH() * tileSize);
            world.addObject(new Wall(p.x - thickness, p.y - thickness, thickness, thickness));
            world.addObject(new Wall(p2.x, p2.y, thickness, thickness));
            world.addObject(new Wall(p2.x, p.y - thickness, thickness, thickness));
            world.addObject(new Wall(p.x - thickness, p2.y, thickness, thickness));
        }
        CheckeredBackground background = new CheckeredBackground(tiles);
        background.setTileSize(tileSize);
        world.addObject(background);
    }

    public void init() {
        buildRoom(new Point(0, 0), maxRoomSize, maxRoomSize, -1, 4);
        hallways.commit();
    }

    public void generate() {
        init();
        while (!hallways.isEmpty()) {
            tick();
        }
        getStart();
        getEnd();
    }

    public Point randomCenter() {
        Room[] roomCenters = rooms.toArray(new Room[0]);
        int index = (int) (Math.random() * roomCenters.length);
        Point center = roomCenters[index].getCenter();
        center.x *= tileSize;
        center.y *= tileSize;
        return center;
    }

    public Point getStart() {
        if (start == null) {
            start = randomCenter();
        }
        return start;
    }

    public Point getEnd() {
        if (end == null) {
            end = randomCenter();
        }
        return end;
    }
}
