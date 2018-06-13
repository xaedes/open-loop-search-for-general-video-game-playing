package TeamTopbug_HR;


public class FloydWarshall {
//    http://de.wikipedia.org/wiki/Algorithmus_von_Floyd_und_Warshall
//    Angenommen der Graph ist gegeben durch seine Gewichtsmatrix w.
//    w[i,j] ist das Gewicht der Kante von i nach j, falls eine solche Kante existiert. Falls es keine Kante von i nach j gibt ist w[i,j] unendlich.
//    Dann kann man die Matrix d der kuerzesten Distanzen durch folgendes Verfahren bestimmen:
//
//    Algorithmus von Floyd
//
//            (1) Fuer alle i,j  : d[i,j] = w[i,j]
//            (2) Fuer k = 1 bis n
//            (3)   Fuer alle Paare i,j
//            (4)     d[i,j] = min (d[i,j],d[i,k] + d[k,j])

    public static double[][] distances(double[][] weights) {
        double[][] w = weights;
        if (w.length == 0) {
            return new double[0][0];
        }
        int n = w.length;
        assert w[0].length == n;
        for (int k = 0; k < n; ++k) {
            for (int i = 0; i < n; ++i) {
                if (w[i][k] == Double.POSITIVE_INFINITY)
                    continue;
                for (int j = 0; j < n; ++j) {
                    double over_k = w[i][k] + w[k][j];
                    if (over_k < w[i][j]) {
                        w[i][j] = over_k;
                    }
                }
            }
        }
        return w;
    }

    public static double[][] distancesOnCopy(double[][] weights) {
        double[][] w = weights;
        if (w.length == 0) {
            return new double[0][0];
        }
        return distances(Utils.copy2DArray(weights));
    }
}

