package graphv4j;

public interface Algorithm<T> {
    /**
     * invoked when "step" button is clicked. should modify the graph according to algorithm being implemented.
     * @param graph the graph to apply one step of algorithm on
     */
    void step(Graph<T> graph);

    /**
     * invoked when the "new vertex" button is clicked
     * @return should return an initialised new vertex that will be added to the graph. return null if adding vertex is not allowed
     */
    default Vertex<T> getVertex() {
        return null;
    }

    /**
     * invoked when all nodes in graph are cleared. should clean internal state of the algorithm(if any),
     * and reinitialize it according to the graph passed
     * @param graph new graph to reinitialize state off of
     */
    default void clear(Graph<T> graph) {
    }
}
