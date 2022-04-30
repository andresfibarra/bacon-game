import java.util.*;

/**
 * Graph Library with methods used in Kevin Bacon game
 * @param <V>
 * @param <E>
 * @author - Andres Ibarra, Spring 2021, CS10 Problem Set 4
 */
public class GraphLibrary <V,E>{

    /**
     *
     * @param g - graph of
     * @param source - source/ root node
     * @param <V>
     * @param <E>
     * @return - shortest path tree from the source
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
        Graph pathTree = new AdjacencyMapGraph<V,E>();
        if (g != null && g.numVertices() > 0) {
            pathTree.insertVertex(source);
            Set<V> visited = new HashSet<V>();
            visited.add(source);
            Queue<V> queue = new LinkedList<V>();
            queue.add(source);
            while(!queue.isEmpty()) {
                V u = queue.remove();
                for (V v: g.outNeighbors(u)) {
                    if (!visited.contains(v)) {
                        visited.add(v);
                        queue.add(v);
                        pathTree.insertVertex(v);
                        pathTree.insertDirected(v, u, pathTree.getLabel(v, u)); //add edge from new vertex towards source
                    }
                }
            }
        }

        return pathTree;
    }

    /**
     * Create a list keeping track of the path from the root of the tree to a specified vertex
     * @param tree - shortestPathTree
     * @param v - the vertex that is the end of the path
     * @param <V>
     * @param <E>
     * @return - List with objects of type V containing the path from the root to vertex v
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
        if (tree.numVertices() == 0 || !tree.hasVertex(v)) {
            return new ArrayList<V>();
        }

        ArrayList<V> path = new ArrayList<V>();
        V current = v;  //start at end
        path.add(current); //add source
        while(tree.outDegree(current) != 0) {
            current = tree.outNeighbors(current).iterator().next();
            path.add(0, current);
        }
        return path;
    }

    /**
     * Given a graph and a subgraph (here shortest path tree), determine which vertices are in the graph but not
     * the subgraph (here, not reached by BFS).
     * @param graph - graph of all actors
     * @param subgraph - shortest path tree
     * @param <V>
     * @param <E>
     * @return - Set containing vertices found in graph but not in subgraph
     */
    public static <V, E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        Set missing = new HashSet<V>();
        for (V v: graph.vertices()) {
            if (!subgraph.hasVertex(v)) {
                missing.add(v);
            }
        }
        return missing;
    }

    /**
     * Find the average distance-from-root in a shortest path tree
     * @param tree - shortestPathTree
     * @param root - root node of shortestPathTree
     * @param <V>
     * @param <E>
     * @return - double representing average distance-from-root
     */
    public static <V, E> double averageSeparation(Graph<V,E> tree, V root) {
        if (tree != null & tree.numVertices()>0) {
            double totalDistance = calcTotalSeparation(tree, root, 0);
            return totalDistance / (tree.numVertices()-1);
        }
        else {
            System.out.println("The provided tree is empty or only has one vertex");
            return 0;
        }
    }

    /**
     * Helper function using recursion for averageSeparation
     * @param tree - shortestPathTree
     * @param vertex - the vertex being examined
     * @param distanceFromRoot - current distance from the room
     * @param <V>
     * @param <E>
     * @return - the total degrees of separation from the root
     */
    private static <V,E> double calcTotalSeparation (Graph<V,E> tree, V vertex, double distanceFromRoot) {
        double distance = distanceFromRoot;
        for (V neighbor: tree.inNeighbors(vertex)) {
            distance += calcTotalSeparation(tree, neighbor, distanceFromRoot+1);
        }
        return distance;
    }

    /**
     * Create a map mapping vertex -> its average separation
     * @param universe - the big graph
     * @param <V>
     * @param <E>
     * @return - HashMap of vertex -> its average separation
     */
    public static <V, E> Map<V, Double> avSepMap(Graph<V,E> universe) {
        HashMap<V, Double> sepMap = new HashMap<>();
        for (V vertex: universe.vertices()) {
            double avSep = averageSeparation(bfs(universe, vertex), vertex);
            sepMap.put(vertex, avSep);
        }
        return sepMap;
    }

}
