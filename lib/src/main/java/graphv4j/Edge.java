package graphv4j;

import java.awt.*;
import java.io.Serializable;

public class Edge<E> implements Serializable {
    public E value;
    public Color color;
    public Edge(E value, Color color) {
        this.value = value;
        this.color = color;
    }
    public Edge(E value) {
        this(value, Color.BLACK);
    }
}
