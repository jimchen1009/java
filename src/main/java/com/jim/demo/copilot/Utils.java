package com.jim.demo.copilot;

import java.util.List;

public class Utils {

    public static class Point{
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static class Grid{
        private final Point point;
        private final boolean walkable;

        public Grid(Point point, boolean walkable) {
            this.point = point;
            this.walkable = walkable;
        }

        public Point getPoint() {
            return point;
        }


    }
}
