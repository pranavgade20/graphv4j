package graphv4j.antcolony;

import java.util.HashSet;

public class AntSet extends HashSet<Ant> {
    @Override
    public String toString() {
        return Integer.toString(this.size());
    }
}
