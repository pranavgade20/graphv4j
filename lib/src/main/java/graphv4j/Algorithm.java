package graphv4j;

public interface Algorithm<V, E> {
    /**
     * invoked when "step" button is clicked. should modify the graph according to algorithm being implemented.
     * @param graph the graph to apply one step of algorithm on
     */
    void step(Graph<V, E> graph);

    /**
     * invoked when the "new vertex" button is clicked
     * @return should return an initialised new vertex that will be added to the graph. return null if adding vertex is not allowed
     */
    default Vertex<V, E> getVertex(Graph<V, E> frame) {
        return null;
    }

    /**
     * invoked when all nodes in graph are cleared. should clean internal state of the algorithm(if any),
     * and reinitialize it according to the graph passed
     * @param graph new graph to reinitialize state off of
     */
    default void clear(Graph<V, E> graph) {
    }

    /**
     * creates an edge between vertex a and b. returns true if edge could be added, false otherwise
     * @param a edge FROM this vertex
     * @param b edge TO this vertex
     * @return was adding an edge successful
     */
    default boolean addEdge(Vertex<V, E> a, Vertex<V, E> b) {
        return false;
    }

    /**
     * change the taint/type of the vertex, typically done cyclically. The taint is Color.LIGHT_GRAY
     * by default.
     * @param vertex the vertex to change
     */
    default void changeTaint(Vertex<V, E> vertex) {
    }
}
