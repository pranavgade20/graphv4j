# graphv4j
A java graph visualization library. Allows you to save, load and edit graphs via a GUI.

## Usage
After importing this project as a library, the following would be a basic example:
```java
public class GraphTest {
    public static void main(String[] args) {
        class MyAlgo implements Algorithm<Integer> {
            int i = 2;
            @Override
            public void step(Graph<Integer> graph) {
                graph.addVertex(new Vertex<>(i++));
            }
        }
        Graph<Integer> graph = new Graph<>();
        var a = new Vertex<>(0);
        var b = new Vertex<>(1);
        a.addEdge(b, Color.ORANGE);
        graph.addVertex(a);
        graph.addVertex(b);

        graph.setAlgorithm(new MyAlgo());
    }
}
```
