package TeamTopbug_HR;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class BFSDistances {
    static class BFSNode extends Point {
        public int distance;

        public BFSNode(int x, int y, int distance) {
            super(x, y);
            this.distance = distance;
        }
    }

    static public int[][] distances(boolean[][] walkable, Point start) {
        assert walkable.length == GameInfo.width;
        assert walkable[0].length == GameInfo.height;
        int[][] d = new int[GameInfo.width][GameInfo.height];
        boolean[][] visited = new boolean[GameInfo.width][GameInfo.height];

        Queue<BFSNode> queue = new LinkedList<BFSNode>();
        visited[start.x][start.y] = true;
        queue.add(new BFSNode(start.x, start.y, 0));
        while (!queue.isEmpty()) {
            BFSNode current = queue.remove();
            d[current.x][current.y] = current.distance;
            if (current.x > 0) {
                int i = current.x - 1;
                int j = current.y;
                if (walkable[i][j] && !visited[i][j]) {
                    visited[i][j] = true;
                    queue.add(new BFSNode(i, j, current.distance + 1));
                }
            }
            if (current.x < GameInfo.width - 1) {
                int i = current.x + 1;
                int j = current.y;
                if (walkable[i][j] && !visited[i][j]) {
                    visited[i][j] = true;
                    queue.add(new BFSNode(i, j, current.distance + 1));
                }
            }
            if (current.y > 0) {
                int i = current.x;
                int j = current.y - 1;
                if (walkable[i][j] && !visited[i][j]) {
                    visited[i][j] = true;
                    queue.add(new BFSNode(i, j, current.distance + 1));
                }
            }
            if (current.y < GameInfo.height - 1) {
                int i = current.x;
                int j = current.y + 1;
                if (walkable[i][j] && !visited[i][j]) {
                    visited[i][j] = true;
                    queue.add(new BFSNode(i, j, current.distance + 1));
                }
            }
        }
        return d;
    }
}
