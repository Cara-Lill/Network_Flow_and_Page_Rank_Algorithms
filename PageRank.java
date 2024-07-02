import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;

/**
* Write a description of class PageRank here.
*
* @author Cara Lill
*/
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    
    // Map of page id to rank
    private static Map<String, Double> pageRanks;
    
    // ArrayList of pairs containing a node and it's most important node
    private static ArrayList<Pair<Gnode, Gnode>> mostImportant;
    
    /**
    * build the fromLinks and toLinks 
    */
    //TODO: Build the data structure to support Page rank. Compute the fromLinks and toLinks for each node
    public static void computeLinks(Graph graph){
        // TODO
        //get edges from graph
        //for each edge, get from gnode and to gnode and add it to each nodes respective lists
        for (Edge e : graph.getOriginalEdges()) {
            Gnode toNode = e.toNode();
            Gnode fromNode = e.fromNode();
            
            toNode.addFromLinks(fromNode);
            fromNode.addToLinks(toNode);
        }
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (Gnode node : graph.getNodes().values()){
            System.out.print("\nNode: "+node.toString());
            //for each node display the in edges 
            System.out.print("\nIn links to nodes:");
            for(Gnode c:node.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to nodes:");
            //for each node display the out edges 
            for(Gnode c: node.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    /**
     * Compute the page ranks for each Gnode
     * 
     * @param graph the given graph containing nodes and links
     */
    public static void computePageRank(Graph graph){
        // TODO
        // initiate the ranks hashmap and get the number of nodes
        pageRanks = new HashMap<>();
        int nNodes = graph.getNodes().size();
        
        // set each initial page rank
        for (String nId : graph.getNodes().keySet()) {
            pageRanks.put(nId, 1.0/nNodes);
        }
        
        int count = 1; // get count
        do {
            // get the contribution of nodes with no outbounding links
            double noOutLinkShare = 0;
            for (Gnode n : graph.getNodes().values()) {
                if (n.getToLinks().isEmpty()){
                    noOutLinkShare += (dampingFactor * (pageRanks.get(n.getId()) / nNodes));
                }
            }
            
            // make a temp map for page ranks
            Map<String, Double> newPageRanks = new HashMap<>();
            for (Gnode n : graph.getNodes().values()) {
                // get a basis for each node (nodes with no outbounding links & random page jumping)
                double nRank = noOutLinkShare + (1.0 - dampingFactor)/nNodes;
                
                // compute page rank contribution from incoming links
                double neighboursShare = 0.0;
                for (Gnode nb : n.getFromLinks()) {
                    int nbOutEdgesCount = nb.getToLinks().size();
                    neighboursShare += (pageRanks.get(nb.getId()) / nbOutEdgesCount);
                }
                
                // update the rank within the temp ranks
                double newRank = nRank + dampingFactor * neighboursShare;
                newPageRanks.put(n.getId(), newRank);
            }
            
            // fully update ranks with newly computed ranks
            pageRanks = newPageRanks; 
            count++;
        } while (count <= iter); // stop when count is over number of expected iterations
        // END TODO
    }
    
    /**
     * Computes the most important neighbour node for each node
     * 
     * @param graph the given graph containing nodes and links
     */
    public static void computeMostImpneighbour(Graph graph){
        // TODO
        //computePageRank(graph); // make sure all nodes have been ranked
        mostImportant = new ArrayList<>(); // initiate the list
        
        for (Gnode n : graph.getNodes().values()) {
            // set initial values for the most important node and max importance
            Gnode mostImportantNode = null;
            double maxImportance = Double.MIN_VALUE;
            
            // if the node has inbound links
            if (!n.getFromLinks().isEmpty()) {
                // for each neighbour node
                for (Gnode nb : n.getFromLinks()) {
                    // get their importance and calculate how much the current node gets
                    double nbImportance = pageRanks.get(nb.getId());
                    double sharingImportance = nbImportance / nb.getToLinks().size();
                    // if this neighbour node contributes more then the current most important node
                    if (sharingImportance > maxImportance) {
                        // update values
                        maxImportance = sharingImportance;
                        mostImportantNode = nb;
                    }
                }
            }
            // add most important node to the important nodes arraylist
            mostImportant.add(new Pair(n, mostImportantNode));
        }
        // END TODO
    }
    
    /** Get an unmodifiable map with all the page ranks */
    public static Map<String, Double> getPageRanks() {
        return Collections.unmodifiableMap(pageRanks);
    }
    
    /** Get the number of iterations */
    public static int getIteration() {
        return iter;
    }
    
    /** Get an arraylist containing all the nodes with their most important node */
    public static ArrayList<Pair<Gnode, Gnode>> getImportantNodes() {
        return mostImportant;
    }
}
