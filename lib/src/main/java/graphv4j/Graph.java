package graphv4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Graph<T> {
    public volatile ArrayList<Vertex<T>> vertices;
    public transient volatile ReentrantLock graphLock = new ReentrantLock();
    private transient Algorithm<T> algorithm;

    transient Vertex<T> selectedVertex = null;
    transient Color foreground = Color.BLACK;

    private transient final JPanel panel;
    private transient final JFrame frame;
    private volatile int pagex, pagey;
    private transient volatile int pressx, pressy;
    public Graph() {
        vertices = new ArrayList<>();

        frame = new JFrame();
        frame.setTitle("Graph");
        frame.setSize(700, 700);
        addMenuBar();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                graphLock.lock();
                super.paintChildren(g);
                vertices.forEach(from -> {
                    from.edges.forEach((to, w) -> {
                        double angle = Math.atan((to.getLocation().y - (double)from.getLocation().y)/(to.getLocation().x - from.getLocation().x));
                        if (from.getLocation().x < to.getLocation().x) {
                            g.setColor(w.color);
                            g.drawLine(
                                    (int) (from.getLocation().x + from.getWidth()*0.5 + 20*Math.cos(angle)),
                                    (int) (from.getLocation().y + from.getHeight()*0.5 + 20*Math.sin(angle)),
                                    (int) (to.getLocation().x + to.getWidth()*0.5 - 20*Math.cos(angle)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 - 20*Math.sin(angle))
                            );
                            g.setColor(w.color);
                            g.drawLine(
                                    (int) (to.getLocation().x + to.getWidth()*0.5 - 20*Math.cos(angle)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 - 20*Math.sin(angle)),
                                    (int) (to.getLocation().x + to.getWidth()*0.5 - 20*Math.cos(angle) - 5*Math.cos(angle+Math.PI/6)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 - 20*Math.sin(angle) - 5*Math.sin(angle+Math.PI/6))
                            );
                            g.setColor(w.color);
                            g.drawLine(
                                    (int) (to.getLocation().x + to.getWidth()*0.5 - 20*Math.cos(angle)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 - 20*Math.sin(angle)),
                                    (int) (to.getLocation().x + to.getWidth()*0.5 - 20*Math.cos(angle) - 5*Math.cos(angle-Math.PI/6)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 - 20*Math.sin(angle) - 5*Math.sin(angle-Math.PI/6))
                            );
                            g.setColor(foreground);
                            g.drawString(
                                    String.valueOf(w.weight),
                                    (int) ((to.getLocation().x + to.getWidth()*0.5 - 20*Math.cos(angle))*0.9 + ((from.getLocation().x + from.getWidth()*0.5 + 20*Math.cos(angle))*0.1)),
                                    (int) ((to.getLocation().y + to.getHeight()*0.5 - 20*Math.sin(angle))*0.9 + ((from.getLocation().y + from.getHeight()*0.5 + 20*Math.sin(angle))*0.1))
                            );
                        }
                        else {
                            g.setColor(w.color);
                            g.drawLine(
                                    (int) (from.getLocation().x + from.getWidth()*0.5 - 20*Math.cos(angle)),
                                    (int) (from.getLocation().y + from.getHeight()*0.5 - 20*Math.sin(angle)),
                                    (int) (to.getLocation().x + to.getWidth()*0.5 + 20*Math.cos(angle)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 + 20*Math.sin(angle))
                            );
                            g.setColor(w.color);
                            g.drawLine(
                                    (int) (to.getLocation().x + to.getWidth()*0.5 + 20*Math.cos(angle)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 + 20*Math.sin(angle)),
                                    (int) (to.getLocation().x + to.getWidth()*0.5 + 20*Math.cos(angle) + 5*Math.cos(angle+Math.PI/6)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 + 20*Math.sin(angle) + 5*Math.sin(angle+Math.PI/6))
                            );
                            g.setColor(w.color);
                            g.drawLine(
                                    (int) (to.getLocation().x + to.getWidth()*0.5 + 20*Math.cos(angle)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 + 20*Math.sin(angle)),
                                    (int) (to.getLocation().x + to.getWidth()*0.5 + 20*Math.cos(angle) + 5*Math.cos(angle-Math.PI/6)),
                                    (int) (to.getLocation().y + to.getHeight()*0.5 + 20*Math.sin(angle) + 5*Math.sin(angle-Math.PI/6))
                            );
                            g.setColor(foreground);
                            g.drawString(
                                    String.valueOf(w.weight),
                                    (int) ((to.getLocation().x + to.getWidth()*0.5 + 20*Math.cos(angle))*0.9 + ((from.getLocation().x + from.getWidth()*0.5 - 20*Math.cos(angle))*0.1)),
                                    (int) ((to.getLocation().y + to.getHeight()*0.5 + 20*Math.sin(angle))*0.9 + ((from.getLocation().y + from.getHeight()*0.5 - 20*Math.sin(angle))*0.1))
                            );
                        }
                    });
                });
                graphLock.unlock();
            }
        };
        panel.setLayout(null);
        frame.add(panel);
        panel.setBackground(Color.LIGHT_GRAY);

        pagex = frame.getWidth()/2;
        pagey = frame.getHeight()/2;

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                pressx = mouseEvent.getXOnScreen();
                pressy = mouseEvent.getYOnScreen();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                int delx = pressx - mouseEvent.getXOnScreen();
                int dely = pressy - mouseEvent.getYOnScreen();
                pressx = mouseEvent.getXOnScreen();
                pressy = mouseEvent.getYOnScreen();
                pagex -= delx;
                pagey -= dely;
                for (Vertex<T> vertex : vertices) {
                    vertex.setLocation(vertex.getLocation().x-delx, vertex.getLocation().y-dely);
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });
    }

    public void addVertex(Vertex<T> vertex) {
        vertices.add(vertex);
        vertex.setParentGraph(this);
        this.redraw();

        frame.revalidate();
        frame.repaint();
    }

    void repaint() {
        panel.repaint();
    }

    private void redraw() {
        LinkedList<Vertex<T>> temp = new LinkedList<>(vertices);

        panel.removeAll();

        Deque<Vertex<T>> visited = new ArrayDeque<>();
        LinkedList<Vertex<T>> ordered = new LinkedList<>();
        HashSet<Vertex<T>> visitedCache = new HashSet<>();
        while (!temp.isEmpty()) {
            visited.push(temp.stream().max(Comparator.comparingInt(a -> a.edges.size())).get());
            temp.remove(visited.peek());

            while (!visited.isEmpty()) {
                Vertex<T> vertex = visited.removeFirst();
                if (!visitedCache.contains(vertex)) {
                    visitedCache.add(vertex);
                    ordered.add(vertex);
                    vertex.edges.forEach((v, w) -> {
                        visited.addLast(v);
                        temp.remove(v);
                    });
                }
            }
        }

        double x, y, c = 45;
        for (int i = 0; i < ordered.size(); i++) {
            // arranging vertices according to phyllotaxis
            if (i != 0) {
                var a = (i - 2) * 137.5;
                var r = c * Math.sqrt(i + 1);
                x = r * Math.cos(a);
                y = r * Math.sin(a);
            } else x = y = 0;

            panel.add(ordered.get(i).getPanel());
            (ordered.get(i)).setLocation((int)x + pagex, (int)y + pagey);
        }
    }

    int i = 0;
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Actions");
        JMenuItem save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        save.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save to");
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                chooser.getSelectedFile();
                try {
                    FileOutputStream w = new FileOutputStream(chooser.getSelectedFile());
                    ObjectOutputStream objStream = new ObjectOutputStream(w);
                    objStream.writeInt(pagex);
                    objStream.writeInt(pagey);
                    objStream.writeObject(vertices);
                    objStream.close();
                    w.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("No Selection");
            }
        });
        menu.add(save);
        JMenuItem open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        open.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open From");
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                chooser.getSelectedFile();
                try {
                    FileInputStream r = new FileInputStream(chooser.getSelectedFile());
                    ObjectInputStream objStream = new ObjectInputStream(r);
                    this.pagex = objStream.readInt();
                    this.pagey = objStream.readInt();
                    this.vertices = (ArrayList<Vertex<T>>) objStream.readObject();
                    panel.removeAll();
                    vertices.forEach(v -> panel.add(v.getPanel()));
                    frame.invalidate();
                    frame.repaint();
                    r.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("No Selection");
            }
        });
        menu.add(open);
        JMenuItem clear = new JMenuItem("Clear");
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        clear.addActionListener(actionEvent -> {
            graphLock.lock();
            this.vertices = new ArrayList<>();
            this.redraw();
            graphLock.unlock();
            panel.invalidate();
            panel.repaint();
        });
        menu.add(clear);
        menuBar.add(menu);

        JMenuItem addVertex = new JMenuItem("Add Vertex");
        addVertex.setAccelerator(KeyStroke.getKeyStroke('v'));
        addVertex.addActionListener(actionEvent -> {
            graphLock.lock();
            Vertex<T> vertex = algorithm.getVertex();
            if (vertex == null) JOptionPane.showMessageDialog(frame, "Adding vertices is not supported.");
            else {
                vertices.add(vertex);
                vertex.setParentGraph(this);
                panel.add(vertex.getPanel());
            }
            graphLock.unlock();
            panel.invalidate();
            panel.repaint();
        });
        menuBar.add(addVertex);
        JMenuItem next = new JMenuItem("Next");
        next.setAccelerator(KeyStroke.getKeyStroke('n'));
        next.addActionListener(actionEvent -> {
            if (this.algorithm != null) {
                graphLock.lock();
                this.algorithm.step(this);
                graphLock.unlock();
                panel.invalidate();
                panel.repaint();
            }
            else JOptionPane.showMessageDialog(frame, "Algorithm is not set!");
        });
        menuBar.add(next);
        JMenuItem play = new JMenuItem("Play");
        AtomicBoolean playing = new AtomicBoolean(false);
        play.setAccelerator(KeyStroke.getKeyStroke('p'));
        play.addActionListener(actionEvent -> {
            playing.set(!playing.get());
            if (playing.get()) {
                // Play the algorithm
                play.setText("Pause");
                (new Thread(() -> {
                    while (playing.get()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (this.algorithm != null) {
                            graphLock.lock();
                            this.algorithm.step(this);
                            graphLock.unlock();
                            panel.invalidate();
                            panel.repaint();
                        }
                        else {
                            JOptionPane.showMessageDialog(frame, "Algorithm is not set!");
                            break;
                        }
                    }
                })).start();
            } else {
                // Pause the algorithm
                play.setText("Play");
            }
        });
        menuBar.add(play);
        frame.setJMenuBar(menuBar);
    }

    public void setAlgorithm(Algorithm algorithm){
        this.algorithm = algorithm;
    }
}
