package TeamTopbug_HR;

import tools.Vector2d;

import java.awt.*;
import java.awt.geom.Point2D;

public class Utils {
    public static double[][] copy2DArray(double[][] arr) {
        int n = arr.length;
        double[][] copy = new double[n][];
        for (int i = 0; i < n; i++) {
            copy[i] = new double[arr[i].length];
            System.arraycopy(arr[i], 0, copy[i], 0, arr[i].length);
        }
        return copy;
    }

    public static double min(double[][] arr) {
        double min = Double.POSITIVE_INFINITY;
        for (double[] anArr : arr) {
            for (double anAnArr : anArr) {
                if (anAnArr < min) {
                    min = anAnArr;
                }
            }
        }
        return min;
    }

    public static double max(double[][] arr) {
        double max = Double.NEGATIVE_INFINITY;
        for (double[] anArr : arr) {
            for (double anAnArr : anArr) {
                if (anAnArr > max) {
                    max = anAnArr;
                }
            }
        }
        return max;
    }

    public static Point toTileCoord(Vector2d coord) {
        return toTileCoord(coord.x, coord.y);
    }
    public static Point toTileCoord(double x, double y) {
        Point point = new Point();
        point.x = (int) Math.round(x / GameInfo.blocksize);
        point.y = (int) Math.round(y / GameInfo.blocksize);
        if (point.x<0) point.x = 0;
        if (point.y<0) point.y = 0;
        if (point.x>GameInfo.width-1) point.x = GameInfo.width-1;
        if (point.y>GameInfo.height-1) point.y = GameInfo.height-1;
        return point;
    }

}
