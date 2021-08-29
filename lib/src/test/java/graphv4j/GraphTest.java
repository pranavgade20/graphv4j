package graphv4j;

import java.awt.*;

public class GraphTest {
    public static void main(String[] args) {
        class MyAlgo implements Algorithm<Integer, Integer> {
            @Override
            public boolean step(Graph<Integer, Integer> graph) {
                int i = graph.vertices.stream().mapToInt(integerVertex -> integerVertex.value).sum();
                graph.addVertex(new Vertex<>(i));
                graph.vertices.forEach(a -> graph.vertices.forEach(b ->
                        a.edges.put(b, new Edge<>(1, Color.ORANGE))
                ));
                return true;
            }
        }
        Graph<Integer, Integer> graph = new Graph<>();
        var a = new Vertex<Integer, Integer>(0);
        var b = new Vertex<Integer, Integer>(1);
        a.edges.put(b, new Edge<>(1, Color.ORANGE));
        graph.addVertex(a);
        graph.addVertex(b);

        graph.setAlgorithm(new MyAlgo());
    }
}
