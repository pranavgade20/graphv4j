package graphv4j;

import java.awt.*;

public class GraphTest {
    public static void main(String[] args) {
        class MyAlgo implements Algorithm<Integer> {
            @Override
            public void step(Graph<Integer> graph) {
                int i = graph.vertices.stream().mapToInt(integerVertex -> integerVertex.value).sum();
                graph.addVertex(new Vertex<>(i));
                graph.vertices.forEach(a -> graph.vertices.forEach(b ->
                        a.addEdge(b, Color.ORANGE)
                ));
            }
        }
        Graph<Integer> graph = new Graph<>();
        var a = new Vertex<>(0);
        var b = new Vertex<>(1);
        a.addEdge(b);
        graph.addVertex(a);
        graph.addVertex(b);

        graph.setAlgorithm(new MyAlgo());
    }
}
