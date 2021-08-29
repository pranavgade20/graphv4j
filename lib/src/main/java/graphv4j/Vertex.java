package graphv4j;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;

public class Vertex<V, E> implements Serializable {
    V value;
    public volatile HashMap<Vertex<V, E>, Edge<E>> edges;
    private Graph<V, E> parentGraph = null;

    private JTextPane valueText;
    private JPanel panel;
    private JPanel container;
    private transient volatile int screenX, screenY;
    final static Color background = new Color(Color.LIGHT_GRAY.getColorSpace(), Color.LIGHT_GRAY.getComponents(null), 0f);
    private Color vertexColor = Color.LIGHT_GRAY;
    public Vertex(V value) {
        this.value = value;
        this.edges = new HashMap<>();

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setSize(41, 41);
        panel.setBackground(background);

        container = new JPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                valueText.setText(value.toString());
                super.paintChildren(g);
                g.setColor(vertexColor);
                g.fillOval(getLocation().x, getLocation().y, 40, 40);
                g.setColor(Color.BLACK);
                g.drawOval(getLocation().x, getLocation().y, 40, 40);
            }
        };
        container.setSize(41, 41);

        container.setLayout(new GridBagLayout());
        container.setBackground(background);
        GridBagConstraints c = new GridBagConstraints();

        valueText = new JTextPane();
        valueText.setText(value.toString());
        valueText.setEditable(false);
        valueText.setBackground(background);
        StyledDocument d = valueText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(center, 18);
        d.setParagraphAttributes(0, d.getLength(), center, false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        container.add(valueText, c);

        panel.add(container);
        panel.setVisible(true);

        addMouseListener(container);
        addMouseListener(panel);
        addMouseListener(valueText);
    }

    public void setValue(V value) {
        this.value = value;
        valueText.setText(value.toString());
    }

    public void taint(Color color) {
        vertexColor = color;
    }

    public Color getTaint() {
        return vertexColor;
    }

    public V getValue() {
        return value;
    }

    public Point getLocation() {
        return panel.getLocation();
    }

    public void setLocation(int x, int y) {
        panel.setLocation(x, y);
    }

    public int getWidth() {
        return panel.getWidth();
    }

    public int getHeight() {
        return panel.getHeight();
    }

    public JPanel getPanel() {
        return panel;
    }

    public HashMap<Vertex<V, E>, Edge<E>> getEdges() {
        return edges;
    }

    public Graph<V, E> getParentGraph() {
        return parentGraph;
    }

    void setParentGraph(Graph<V, E> parentGraph) {
        this.parentGraph = parentGraph;
    }

    private void addMouseListener(Component component) {
        component.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                int deltaX = mouseEvent.getXOnScreen() - screenX;
                int deltaY = mouseEvent.getYOnScreen() - screenY;
                screenX = mouseEvent.getXOnScreen();
                screenY = mouseEvent.getYOnScreen();

                panel.setLocation(panel.getLocation().x + deltaX, panel.getLocation().y + deltaY);

                panel.getParent().repaint();
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    parentGraph.changeTaint(Vertex.this);
                    return;
                }
                getParentGraph().graphLock.lock();
                if (getParentGraph().selectedVertex == null)
                    getParentGraph().selectedVertex = Vertex.this;
                else {
                    if (getParentGraph().addEdge(getParentGraph().selectedVertex, Vertex.this)) {
                        getParentGraph().history.push(new AbstractMap.SimpleEntry<>(getParentGraph().selectedVertex, Vertex.this));
                    } else {
                        JOptionPane.showMessageDialog(
                                Vertex.this.panel,
                                "Couldn't add edge between these vertices.",
                                "Adding edge failed!",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    getParentGraph().selectedVertex = null;
                }
                getParentGraph().graphLock.unlock();
                getParentGraph().repaint();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                screenX = mouseEvent.getXOnScreen();
                screenY = mouseEvent.getYOnScreen();
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
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        container.setBackground(background);
        valueText.setBackground(background);
        panel.setBackground(background);

        addMouseListener(container);
        addMouseListener(panel);
        addMouseListener(valueText);

    }
}
