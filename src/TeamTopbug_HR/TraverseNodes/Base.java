package TeamTopbug_HR.TraverseNodes;


import TeamTopbug_HR.Node;
import TeamTopbug_HR.NodeAdvance;

public abstract class Base {
    protected Node        origin;
    protected NodeAdvance advance;
    protected TraverseCallback callback;

    public Base(Node origin, NodeAdvance advance, TraverseCallback callback) {
        this.origin = origin;
        this.advance = advance;
        this.callback = callback;
    }

    /**
     * Starts traversing.
     */
    abstract public void startTraverse();
}
