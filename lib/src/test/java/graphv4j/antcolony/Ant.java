package graphv4j.antcolony;

import graphv4j.Vertex;

import java.io.Serializable;

public class Ant implements Serializable {
    boolean searching = true; // searching for food if true; has food and searching home if false
    boolean moved = true; // was moved during this turn if true
}
