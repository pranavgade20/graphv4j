package graphv4j.antcolony;

import graphv4j.Algorithm;
import graphv4j.Edge;
import graphv4j.Graph;
import graphv4j.Vertex;

import javax.swing.*;
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

    Graph<AntSet, Integer> graph;
    void setup() {
        graph = new Graph<>();
        var startSet = new AntSet();
        for (int i = 0; i < 500; i++) {
            startSet.add(new Ant());
        }
        var origin = new Vertex<AntSet, Integer>(startSet);
        origin.taint(Color.GREEN);
        graph.addVertex(origin);
        var target = new Vertex<AntSet, Integer>(new AntSet());
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
                    v.edges.put(w, new Edge<>(weight));
                    w.edges.put(v, new Edge<>(weight));
                }
            });
        });

        graph.setAlgorithm(new AntColonyOptimisationAlgorithm(origin, target));
    }
}

class AntColonyOptimisationAlgorithm implements Algorithm<AntSet, Integer> {
    Vertex<AntSet, Integer> home, target;
    Random rand;
    AntColonyOptimisationAlgorithm(Vertex<AntSet, Integer> home, Vertex<AntSet, Integer> target) {
        this.home = home;
        this.target = target;
        rand = new Random(42);
    }

    @Override
    public Vertex<AntSet, Integer> getVertex(Graph<AntSet, Integer> graph) {
        return new Vertex<>(new AntSet());
    }

    @Override
    public boolean step(Graph<AntSet, Integer> graph) {
        graph.vertices.forEach(v -> v.getValue().forEach(ant -> ant.moved = false));
        graph.vertices.forEach(v -> v.getEdges().values().forEach(e -> {
            Color c = e.color;
            e.color = new Color((int) (c.getRed()*0.97), (int) (c.getGreen()*0.97), (int) (c.getBlue()*0.9));
        }));
        for (Vertex<AntSet, Integer> vertex : graph.vertices) {
            //ants moving towards food
            LinkedList<Map.Entry<Vertex<AntSet, Integer>, Double>> vertexWeightsToFood = new LinkedList<>();
            double totalWeightToFood = 0;
            for (Map.Entry<Vertex<AntSet, Integer>, Edge<Integer>> entry : vertex.getEdges().entrySet()) {
                Vertex<AntSet, Integer> v = entry.getKey();
                Edge<Integer> e = entry.getValue();

                totalWeightToFood += e.value *(260+(5*e.color.getRed())-e.color.getGreen());
                vertexWeightsToFood.add(new AbstractMap.SimpleEntry<>(v, totalWeightToFood));
            }

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
            LinkedList<Map.Entry<Vertex<AntSet, Integer>, Double>> vertexWeightsToHome = new LinkedList<>();
            double totalWeightToHome = 0;
            for (Map.Entry<Vertex<AntSet, Integer>, Edge<Integer>> entry : vertex.getEdges().entrySet()) {
                Vertex<AntSet, Integer> v = entry.getKey();
                Edge<Integer> e = entry.getValue();

                totalWeightToHome += e.value *(1+e.color.getGreen());
                vertexWeightsToHome.add(new AbstractMap.SimpleEntry<>(v, totalWeightToHome));
            }

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
        return true;
    }

    @Override
    public void clear(Graph<AntSet, Integer> graph) {
        var startSet = new AntSet();
        for (int i = 0; i < 500; i++) {
            startSet.add(new Ant());
        }
        var origin = new Vertex<AntSet, Integer>(startSet);
        origin.taint(Color.GREEN);
        graph.addVertex(origin);
        var target = new Vertex<AntSet, Integer>(new AntSet());
        target.taint(Color.ORANGE);

        this.home = origin;
        this.target = target;

        graph.addVertex(origin);
        graph.addVertex(target);
    }

    @Override
    public boolean addEdge(Vertex<AntSet, Integer> a, Vertex<AntSet, Integer> b) {
        String weight = JOptionPane.showInputDialog(
                "Vertex weight:",
                JOptionPane.QUESTION_MESSAGE
        );
        if (weight == null) {
            return false;
        }
        if (weight.matches("\\d+")) {
            try {
                a.edges.put(b, new Edge<>(Integer.parseInt(weight)));
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}