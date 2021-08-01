package graphv4j.antcolony;

import graphv4j.Algorithm;
import graphv4j.Edge;
import graphv4j.Graph;
import graphv4j.Vertex;

import java.awt.*;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class AntColonyOptimisation {
    public static void main(String[] args) {
        var acd = new AntColonyOptimisation();

        acd.setup();
    }

    Graph<AntSet> graph;
    void setup() {
        graph = new Graph<>();
        var startSet = new AntSet();
        for (int i = 0; i < 100; i++) {
            startSet.add(new Ant());
        }
        var origin = new Vertex<>(startSet);
        graph.addVertex(origin);

        var rand = new Random(42);
        for (int i = 0; i < 20; i++) {
            graph.addVertex(new Vertex<>(new AntSet()));
        }

        graph.vertices.forEach(v -> {
            graph.vertices.forEach(w -> {
                if (rand.nextInt(100) < 7) {
                    int weight = rand.nextInt(10)+1;
                    v.addEdge(w, weight);
                    w.addEdge(v, weight);
                }
            });
        });


        graph.setAlgorithm(new AntColonyOptimisationAlgorithm(origin, origin));
    }
}

class AntColonyOptimisationAlgorithm implements Algorithm<AntSet> {
    Vertex<AntSet> home, target;
    AntColonyOptimisationAlgorithm(Vertex<AntSet> home, Vertex<AntSet> target) {
        this.home = home;
        this.target = target;
    }

    @Override
    public void step(Graph<AntSet> graph) {
        graph.vertices.forEach(v -> v.getValue().forEach(ant -> ant.moved = false));
        graph.vertices.forEach(v -> v.getEdges().values().forEach(e -> {
            Color c = e.color;
            e.color = new Color((int) (c.getRed()*0.9), (int) (c.getGreen()*0.9), (int) (c.getBlue()*0.9));
        }));
        for (Vertex<AntSet> vertex : graph.vertices) {
            LinkedList<Map.Entry<Vertex<AntSet>, Integer>> vertexWeights = new LinkedList<>();
            int totalWeight = 0;
            for (Map.Entry<Vertex<AntSet>, Edge> entry : vertex.getEdges().entrySet()) {
                Vertex<AntSet> v = entry.getKey();
                Edge e = entry.getValue();
                totalWeight += e.weight;
                vertexWeights.add(new AbstractMap.SimpleEntry<>(v, totalWeight));
            }

            double totalAntsToMove = vertex.getValue().stream().filter(a -> !a.moved).count();
            int movedAnts = 0;
            var it = vertex.getValue().iterator();
            var weights = vertexWeights.iterator();
            if (!weights.hasNext()) continue;
            var weight = weights.next();
            while (it.hasNext()) {
                var ant = it.next();
                if (ant.moved) continue;

                while ((weight.getValue()/(double)totalWeight) < (movedAnts/totalAntsToMove) && weights.hasNext()) {
                    weight = weights.next();
                }

                ant.moved = true;
                movedAnts++;
                weight.getKey().getValue().add(ant);
                it.remove();
                Color c = vertex.getEdges().get(weight.getKey()).color;
                vertex.getEdges().get(weight.getKey()).color = new Color(c.getRed(), Math.min(c.getGreen() + 10, 255), c.getBlue());
            }
        }
    }
}