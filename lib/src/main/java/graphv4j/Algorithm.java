package graphv4j;

public interface Algorithm<T> {
    void step(Graph<T> graph);
    default Vertex<T> getVertex() {
        return null;
    }
}
