import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;
import java.util.List;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges; 

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;
    
    // PART 1: Store original capacities and flows of edges
    private static Map<String, Pair<Integer, Integer>> ogCapacitiesAndFlows;
    
    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph){
        // TODO
        // method fields: initiate HashMaps and create id count
        edges = new HashMap<>();
        ogCapacitiesAndFlows = new HashMap<>();
        int id = 0;
        
        for (Edge edge : graph.getOriginalEdges()) {
            // forward edge
            edges.put(id + "", edge);
            // Store original capacities and flows
            ogCapacitiesAndFlows.put(id + "", new Pair<>(edge.capacity(), edge.flow()));
            edge.fromCity().addEdgeId(id + ""); // connect edge to the 'from' city
            
            // reverse edge
            id++;
            Edge reverse = new Edge(edge.toCity(), edge.fromCity(), 0, 0);
            edges.put(id + "", reverse);
            reverse.fromCity().addEdgeId(id + ""); // connect edge to the 'from' city
            
            id++;
        }
        
        //printResidualGraphData(graph);  //may help in debugging
        // END TODO
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        //TODO
        computeResidualGraph(graph); // initiate residual graph
        
        ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths = new ArrayList<>();
        while(true) {
            // get a path, if it is null; the end of the path has been reached. Else add to total path and update edge values
            Pair<ArrayList<String>, Integer> path = bfs(graph, from, to);
            if (path.getKey() == null) { break; }
            augmentationPaths.add(path);
            updateEdges(path);
        }
        
        resetCapacitiesAndFlows(); // reset any changes to edges
        // END TODO
        return augmentationPaths;
    }
    
    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer>  bfs(Graph graph, City s, City t) {
        ArrayList<String> augmentationPath = new ArrayList<String>();
        HashMap<String, String> backPointer = new HashMap<String, String>();
        // TODO
        // create a queue and add the first city to it
        Queue<City> q = new LinkedList<>();
        q.add(s);
        // set backPointers
        for (String cId : graph.getCities().keySet()){
            backPointer.put(cId, null);
        }
        
        while (!q.isEmpty()) {
            City cur = q.poll(); // get first in queue
            // for each out-edge Id
            for (String eId : cur.getEdgeIds()) {
                Edge e = getEdge(eId);
                // if next path is valid (not s, hasn't been visited, and has capacity)
                if ((!e.toCity().equals(s)) && backPointer.get(e.toCity().getId()) == null
                    && e.capacity() != 0) {
                    // put next city id & edge id into backpointers
                    backPointer.put(e.toCity().getId(), eId); 
                    // if the path leads to the sink, recreate path
                    if(backPointer.get(t.getId()) != null) {
                        return pathRecreation(augmentationPath, backPointer, t);
                    }
                    // add next city to queue
                    q.add(e.toCity());
                }
            }
        }
        
        // END TODO
        return new Pair(null,0);
    }
    
    // --------------------
    // Extra Functions:
    // --------------------
    
    /**
     * Update all edges after augementation path has been found
     */
    public static void updateEdges(Pair<ArrayList<String>, Integer> augPath) {
        // method field: Get the path of edge Id's and the bottleneck flow for the path
        ArrayList<String> path = augPath.getKey();
        Integer pathFlow = augPath.getValue();
        
        // for each edge in the path
        for (String eId : path) {
            Edge e = getEdge(eId);
            // change the current edge's flow and capacity
            e.setFlow(e.flow() + pathFlow);
            e.setCapacity(e.capacity() - pathFlow);
            
            // get the Id of the reverse edge (keeping in mind if current edge is an orginal or reverse edge)
            Integer revId; 
            if (Integer.valueOf(eId) % 2 == 0 ) { revId = Integer.valueOf(eId) + 1; }
            else { revId = Integer.valueOf(eId) - 1; }
            
            Edge reverse = edges.get(revId + "");
            // update the reverse edges capacity
            reverse.setCapacity(reverse.capacity() + pathFlow);
        }
    }
    
    /**
     * Recreate path using backpointers
     */
    public static Pair<ArrayList<String>, Integer> pathRecreation (ArrayList<String> augPath, HashMap<String, String> bPts, City t) {
        // initialise pathId & bottleneck
        String pathId = bPts.get(t.getId());
        Integer bottleneck = Integer.MAX_VALUE;
        
        // until path has reached end, add edgeId to path, and check for bottleneck
        while (pathId != null) {
            augPath.add(pathId);
            if (edges.get(pathId).capacity() < bottleneck) {
                bottleneck = edges.get(pathId).capacity();
            }
            pathId = bPts.get(edges.get(pathId).fromCity().getId()); // move to next edgeId
        }
        
        // reverse and return path with bottleneck
        Collections.reverse(augPath);
        return new Pair<ArrayList<String>, Integer>(augPath, bottleneck);
    }
    
    /**
     * Resets all changes made to the original edges
     */
    public static void resetCapacitiesAndFlows() {
        for (Map.Entry<String, Pair<Integer, Integer>> entry : ogCapacitiesAndFlows.entrySet()) {
            // get the edge id & the pair containing the edges original capacity & flow
            String edgeId = entry.getKey();
            Pair<Integer, Integer> originalValues = entry.getValue();
            
            Edge edge = getEdge(edgeId); // get edge
            // reset any changes
            edge.setCapacity(originalValues.getKey());
            edge.setFlow(originalValues.getValue());
        }
    }
}


