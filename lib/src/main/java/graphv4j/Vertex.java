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
import java.util.HashMap;

public class Vertex<T> implements Serializable {
    T value;
    protected volatile HashMap<Vertex<T>, Edge> edges;

    private JTextPane valueText;
    private JPanel panel;
    private JPanel container;
    private transient volatile int screenX, screenY;
    final static Color background = new Color(Color.LIGHT_GRAY.getColorSpace(), Color.LIGHT_GRAY.getComponents(null), 0f);
    public Color vertexColor = Color.BLACK;
    public Vertex(T value) {
        this.value = value;
        this.edges = new HashMap<>();

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setSize(50, 50);
        panel.setBackground(background);

        container = new JPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                valueText.setText(value.toString());
                super.paintChildren(g);
                g.setColor(vertexColor);
                g.drawOval(getLocation().x + 5, getLocation().y + 5, 40, 40);
            }
        };
        container.setSize(50, 50);

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

    public void addEdge(Vertex<T> vertex, Edge edge) {
        if (vertex == this) return;
        edges.put(vertex, edge);
    }

    public void addEdge(Vertex<T> vertex, Color color) {
        addEdge(vertex, new Edge(1, color));
    }

    public void addEdge(Vertex<T> vertex, int weight) {
        addEdge(vertex, new Edge(weight, Color.black));
    }

    public void addEdge(Vertex<T> vertex) {
        addEdge(vertex, new Edge(1, Color.BLACK));
    }

    public void setValue(T value) {
        this.value = value;
        valueText.setText(value.toString());
    }

    public T getValue() {
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

    public HashMap<Vertex<T>, Edge> getEdges() {
        return edges;
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
