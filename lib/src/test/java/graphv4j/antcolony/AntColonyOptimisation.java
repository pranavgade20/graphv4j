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
        for (int i = 0; i < 500; i++) {
            startSet.add(new Ant());
        }
        var origin = new Vertex<>(startSet);
        origin.taint(Color.GREEN);
        graph.addVertex(origin);
        var target = new Vertex<>(new AntSet());
        target.taint(Color.ORANGE);

        var rand = new Random(42);
        for (int i = 0; i < 20; i++) {
            graph.addVertex(new Vertex<>(new AntSet()));
        }
        graph.addVertex(target);

        graph.vertices.forEach(v -> {
            graph.vertices.forEach(w -> {
                if (rand.nextInt(100) < 6) {
                    int weight = rand.nextInt(10)+1;
                    v.addEdge(w, weight);
                    w.addEdge(v, weight);
                }
            });
        });

        graph.setAlgorithm(new AntColonyOptimisationAlgorithm(origin, target));
    }
}

class AntColonyOptimisationAlgorithm implements Algorithm<AntSet> {
    Vertex<AntSet> home, target;
    Random rand;
    AntColonyOptimisationAlgorithm(Vertex<AntSet> home, Vertex<AntSet> target) {
        this.home = home;
        this.target = target;
        rand = new Random(42);
    }

    @Override
    public Vertex<AntSet> getVertex() {
        return new Vertex<>(new AntSet());
    }

    @Override
    public void step(Graph<AntSet> graph) {
        graph.vertices.forEach(v -> v.getValue().forEach(ant -> ant.moved = false));
        graph.vertices.forEach(v -> v.getEdges().values().forEach(e -> {
            Color c = e.color;
            e.color = new Color((int) (c.getRed()*0.97), (int) (c.getGreen()*0.97), (int) (c.getBlue()*0.9));
        }));
        for (Vertex<AntSet> vertex : graph.vertices) {
            //ants moving towards food
            LinkedList<Map.Entry<Vertex<AntSet>, Integer>> vertexWeightsToFood = new LinkedList<>();
            for (Map.Entry<Vertex<AntSet>, Edge> entry : vertex.getEdges().entrySet()) {
                Vertex<AntSet> v = entry.getKey();
                Edge e = entry.getValue();

                vertexWeightsToFood.add(new AbstractMap.SimpleEntry<>(v, e.weight*(260+(5*e.color.getRed())-e.color.getGreen())));
            }
            double totalWeightToFood = vertexWeightsToFood.stream().mapToInt(Map.Entry::getValue).sum();

            double totalAntsToMoveToFood = vertex.getValue().stream().filter(a -> !a.moved && a.searching).count();
            int movedAnts = 0;
            var it = vertex.getValue().iterator();
            var weights = vertexWeightsToFood.iterator();
            if (!weights.hasNext()) continue;
            var weight = weights.next();
            while (it.hasNext()) {
                var ant = it.next();
                if (ant.moved || !ant.searching) continue;

                while ((weight.getValue()/totalWeightToFood) < (movedAnts/totalAntsToMoveToFood) && weights.hasNext()) {
                    weight = weights.next();
                }

                ant.moved = true;
                if (weight.getKey() != ant.prev || rand.nextInt(100) < 10) {
                    ant.prev = vertex;
                    weight.getKey().getValue().add(ant);
                    it.remove();
                    if (weight.getKey() == target) {
                        ant.searching = false;
                        ant.prev = null;
                    }
                    Color c = vertex.getEdges().get(weight.getKey()).color;
                    vertex.getEdges().get(weight.getKey()).color = new Color(c.getRed(), Math.min(c.getGreen() + 10, 255), c.getBlue());
                }
                movedAnts++;
            }

            // ants moving towards home
            LinkedList<Map.Entry<Vertex<AntSet>, Integer>> vertexWeightsToHome = new LinkedList<>();
            for (Map.Entry<Vertex<AntSet>, Edge> entry : vertex.getEdges().entrySet()) {
                Vertex<AntSet> v = entry.getKey();
                Edge e = entry.getValue();

                vertexWeightsToHome.add(new AbstractMap.SimpleEntry<>(v, e.weight*(1+e.color.getGreen())));
            }
            double totalWeightToHome = vertexWeightsToHome.stream().mapToInt(Map.Entry::getValue).sum();

            double totalAntsToMoveToHome = vertex.getValue().stream().filter(a -> !a.moved && !a.searching).count();
            movedAnts = 0;
            it = vertex.getValue().iterator();
            weights = vertexWeightsToHome.iterator();
            if (!weights.hasNext()) continue;
            weight = weights.next();
            while (it.hasNext()) {
                var ant = it.next();
                if (ant.moved || ant.searching) continue;

                while ((weight.getValue()/totalWeightToHome) < (movedAnts/totalAntsToMoveToHome) && weights.hasNext()) {
                    weight = weights.next();
                }

                ant.moved = true;
                if (weight.getKey() != ant.prev || rand.nextInt(100) < 5) {
                    ant.prev = vertex;
                    weight.getKey().getValue().add(ant);
                    it.remove();
                    if (weight.getKey() == home) {
                        ant.searching = true;
                        ant.prev = null;
                    }
                    Color c = vertex.getEdges().get(weight.getKey()).color;
                    vertex.getEdges().get(weight.getKey()).color = new Color(Math.min(c.getRed() + 20, 255), c.getGreen(), c.getBlue());
                }
                movedAnts++;
            }
        }
    }

    @Override
    public void clear(Graph<AntSet> graph) {
        var startSet = new AntSet();
        for (int i = 0; i < 500; i++) {
            startSet.add(new Ant());
        }
        var origin = new Vertex<>(startSet);
        origin.taint(Color.GREEN);
        graph.addVertex(origin);
        var target = new Vertex<>(new AntSet());
        target.taint(Color.ORANGE);

        this.home = origin;
        this.target = target;

        graph.addVertex(origin);
        graph.addVertex(target);
    }
}