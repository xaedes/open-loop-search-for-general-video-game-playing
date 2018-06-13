package TeamTopbug_HR;

import java.util.LinkedList;
import java.util.List;

public class NodePool {
    private static List<Node> available = new LinkedList<Node>();

    public static Node get() {
        Node node;
        if (available.size() > 0) {
            node = available.remove(0);
        } else {
            node = new Node();
        }
        node.use();

        return node;
    }

    public static void releaseNode(Node node) {
        if (node.children != null) {
            for (Node n : node.children) {
                if (n != null) {
                    releaseNode(n);
                }
            }
        }
        node.release();
        available.add(node);
    }
}