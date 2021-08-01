package graphv4j;

import java.awt.*;
import java.io.Serializable;

public class Edge implements Serializable {
    public int weight;
    public Color color;
    Edge(int weight, Color color) {
        this.weight = weight;
        this.color = color;
    }
}
