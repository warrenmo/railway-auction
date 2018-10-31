
package railway.g2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

// To access data classes.
import railway.sim.utils.*;


class AvailableLinks {
    public int id;
    public String town1;
    public int town1_id;
    public String town2;
    public int town2_id;
    public double traffic;
    public double distance;
    public double originalValue;
    public double bidValue;
    public double expectedValue;
}

public class Player implements railway.sim.Player {
    // Random seed of 42.
    private int seed = 42;
    private Random rand;

    private double budget;
    private String name;
    private List<List<Integer>> infra;
    private List<String> townLookup;
    private Map<String, Integer> townRevLookup = new HashMap<>();
    private List<Coordinates> geo;
    private static int[][] transit;
    private List<AvailableLinks> availableLinks;

    private List<BidInfo> availableBids = new ArrayList<>();
    private List<BidInfo> availableBids_new = new ArrayList<>();

    private WeightedGraph weightedGraph;
    private List<String> allPaths = new ArrayList<>();

    // private static List<LinkInfo> links = new ArrayList<>();

    public Player() {
        rand = new Random();
    }

    public void init(
        String name,
        double budget,
        List<Coordinates> geo,
        List<List<Integer>> infra,
        int[][] transit,
        List<String> townLookup,
        List<BidInfo> allBids) {

        this.budget = budget;
        this.name = name;
        this.infra = infra;
        this.townLookup = townLookup;
        this.geo = geo;
        this.transit = transit;

        for(int i=0; i<townLookup.size(); i++)
            this.townRevLookup.put(townLookup.get(i), i);
    }

    public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
        // The random player bids only once in a round.
        // This checks whether we are in the same round.
        // Random player doesn't care about bids made by other players.
        // if (availableBids.size() != 0) {
        //     return null;
        // }

        initializeAvailableLinks(currentBids, allBids);
        //s is the most valuable node
        int s = 0;
        // s=getMostValuableNodeNaive();

        //get the weightedGraph given node s
        weightedGraph=initializeWeightedGraph(s,townLookup,infra);
        // weightedGraph.print();
        //implement dijkstra to the weightedGraph
        int[][] prediction = Dijkstra.dijkstra(weightedGraph, s);
        for (int n=s;n<townLookup.size();n++){
            System.out.println(weightedGraph.getLabel(s) + " to " + weightedGraph.getLabel(n));
            Dijkstra.printPath(weightedGraph, prediction, n);
            System.out.println();
        }

        // try to update the traffic infomation
        int s_id = s;
        getpaths(weightedGraph, prediction, s_id, "", allPaths);
        //add all the transit after the first link to the first link
        //go loop the infra to find the adjacent node.
        for(int i=0;i<infra.get(s_id).size();i++){
            int curAdjacentNode = infra.get(s_id).get(i);
            for(int j=1;j<allPaths.size();j++){
                int next_node = Integer.parseInt(allPaths.get(j));
                if (next_node == curAdjacentNode){
                    for(int k=2;k<transit[s_id].length;k++){
                        transit[s_id][1] += transit[s_id][k];
                    }
                    System.out.println(" tansit info: " + transit[s_id][1]);
                }
                else break;
                // transit[s_id][i]; 
            }
        }
        

        System.out.println(name + " has budget: " + budget);
        
        for(int i=0;i<infra.size();i++){
            for(int j=0;j<infra.get(i).size();j++){
                System.out.println(" infra shows as :"+i+" to "+infra.get(i).get(j));
            }
        }
        

        for (BidInfo bi : allBids) {
            System.out.println("Bid id: " + bi.id + " Bid from " + bi.town1 + " to " + bi.town2 + " made by " + bi.owner + " for: " + bi.amount);
            if (bi.owner == null) {
                // bi.amount += 1111;
                availableBids.add(bi);
            }
        }

        if (availableBids.size() == 0) {
            return null;
        }
        
        // Try to choose a link with highest updated traffic, not finished
        // for(int i=0;i<availableBids.size();i++){
        //     if (availableBids.town1 == s || availableBids.town2 == s){
        //         availableBids_new.add(availableBids.get(i));
        //     }
        //     else {
        //         continue;
        //     }
        // }
       
        Collections.sort(availableBids,new Comparator<BidInfo>(){
            public int compare(BidInfo o1, BidInfo o2) {
                if(o1.amount > o2.amount){
                    return 1;
                }
                if(o1.amount == o2.amount){
                    return 0;
                }
                return -1;
            }
        });


        BidInfo randomBid = availableBids.get(availableBids.size()-1);
        double amount = randomBid.amount;

        // Don't bid if the random bid turns out to be beyond our budget.
        if (budget - amount < 0.) {
            return null;
        }

        // Check if another player has made a bid for this link.
        for (Bid b : currentBids) {
            if (b.id1 == randomBid.id || b.id2 == randomBid.id) {
                if (budget - b.amount - 10000 < 0.) {
                    return null;
                }
                else if (amount>randomBid.amount+30000){
                    System.out.println("Stop bidding");
                    break;
                }
                else {
                    if(b.bidder.equals("g2"))
                    {
                        return null;
                    }
                    amount = b.amount + 10000;
                }

                break;
            }
        }

        Bid bid = new Bid();
        bid.amount = amount;
        bid.id1 = randomBid.id;

        return bid;
    }

    public void updateBudget(Bid bid) {
        if (bid != null) {
            budget -= bid.amount;
        }

        availableBids = new ArrayList<>();
    }

    //iterate through available links and get most valuable 2 links
    //available links will be in bidInfo
    public void initializeAvailableLinks(List<Bid> currentBids, List<BidInfo> allBids){
        availableLinks = new ArrayList<>();
        for (BidInfo bid : allBids) {
            if(bid.owner == null){
                AvailableLinks link = new AvailableLinks();
                link.id = bid.id;
                link.town1 = bid.town1;
                link.town2 = bid.town2;
                link.town1_id = townRevLookup.get(link.town1);
                link.town2_id = townRevLookup.get(link.town2);

                for (Bid currentBid : currentBids) {
                    if(currentBid.id1 == link.id || currentBid.id2 == link.id)
                        link.bidValue = currentBid.amount;
                }

                link.distance = getDistance(link.town1_id, link.town2_id);
                link.traffic = transit[link.town1_id][link.town2_id];
                link.originalValue = bid.amount;
                System.out.println(link.id + " " + link.town1 + " " + link.town2 + " " + link.originalValue + " " + link.bidValue + " " + 
                    link.distance + " " + link.traffic);
                System.out.println("Expected Value: " + (link.distance*link.traffic*10));
                //link.expectedValue = null;
                availableLinks.add(link);
            }
        }

        for(int i=0; i<availableLinks.size(); i++)
            System.out.println(availableLinks.get(i).town1 + " " + availableLinks.get(i).town2);

    }

    private WeightedGraph initializeWeightedGraph(int s,List<String> town, List<List<Integer>> infra)
    {
        List<List<Integer>> graph_infra = infra;
        List<String> graph_townLookup = town;
        double distance = 0;
        WeightedGraph t = new WeightedGraph(town.size());
        for(int i=0;i<town.size();i++){
            t.setLabel(town.get(i));
        }
        for(int i=0;i<infra.size();i++){
            for(int j=0;j<infra.get(i).size();j++){
                distance = getDistance(i, j)+200;
                t.addEdge(i, j, distance);
            }
        }
        return t;
    }

    private List<List<Integer>> getPaths(WeightedGraph G, int[][] prev, int dest) {
        List<Integer> paths = new ArrayList<>();
        List<List<Integer>> allPaths_this = new ArrayList<>();

        getpaths(G, prev, dest, paths, allPaths_this);

        return allPaths_this;
    }

    private void getpaths(WeightedGraph G, int[][] prev, int dest,
        List<Integer> path, List<List<Integer>> allPaths_this) {

        path = new ArrayList<>(path);
        path.add(0, dest);

        if (prev[dest][0] == 0) {
            allPaths_this.add(path);

            return;
        }

        for (int i=1; i<=prev[dest][0]; ++i) {
            getpaths(G, prev, prev[dest][i], path, allPaths_this);
        }
    }

    private void getpaths(WeightedGraph G, int[][] prev, int dest, String path,
        List<String> allPaths_this) {

        path = G.getLabel(dest) + " " + path;

        if (prev[dest][0] == 0) {
            allPaths_this.add(path);

            return;
        }

        for (int i=1; i<=prev[dest][0]; ++i) {
            getpaths(G, prev, prev[dest][i], path, allPaths_this);
        }
    }

    private double getDistance(int t1, int t2) {
        return Math.pow(
            Math.pow(geo.get(t1).x - geo.get(t2).x, 2) +
                Math.pow(geo.get(t1).y - geo.get(t2).y, 2),
            0.5);
    }
}