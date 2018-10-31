package railway.g7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
// To access data classes.
import railway.sim.utils.*;

public class Player implements railway.sim.Player {
    // Random seed of 42.
    private int seed = 42;
    private Random rand;

    private double budget;
    private String name;
    private List<BidInfo> allBids;

    private List<Coordinates> geo;
    private List<List<Integer>> infra;
    private int[][] transit;
    private List<String> townLookup;
    private Hashtable<Integer, Double> distanceLookup = 
              new Hashtable<Integer, Double>();

    private WeightedGraph graph;
    private List<RouteValue> rankedRouteValue;
    private List<List<Integer>> bridges; 
    private List<LinkValue> bridgeLinks = new ArrayList<>();
    private List<List<LinkValue>> routeLinks = new ArrayList<>();

    private List<BidInfo> availableBids = new ArrayList<>();
    private Set<Integer> availableBidId = new HashSet<>();
    private Set<Integer> ourBidId = new HashSet<>();

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
        this.name = name;
        this.budget = budget;
        this.geo = geo;
        this.infra = infra;
        this.transit = transit;
        this.townLookup = townLookup;
        this.allBids = allBids;
        initializeGraph();
        // List<List<Integer>> links = getMostVolumePerKm();
        // for (int i = 0; i < links.size(); i++) {
        //     System.out.println("The %s link is: ");
        //     for (int j = 0; j < links.get(i).size(); j++) {
        //         System.out.print(links.get(i).get(j) + " ");
        //     }
        // }
        bridges = findBridges();
        //System.out.println("The bridges are:");
        for (int i = 0; i < bridges.size(); i++) {
            for (int j = 0; j < bridges.get(i).size(); j++) {
                //System.out.print(bridges.get(i).get(j) + " ");
            }
            //System.out.println();
        }
        initializeBridgeLinks();
        initializeDistHash();
        rankedRouteValue = new ArrayList<RouteValue>();
        gatherAllVolumePerKm();
        for (int i = 0; i < rankedRouteValue.size(); i++) {
            //System.out.println("route number: " + i + ", volume: " + rankedRouteValue.get(i).getVolumePerKm() + ", distance: " + rankedRouteValue.get(i).getDistance());
        }
        initializeRouteLinks();
    }

    private void initializeDistHash(){
    	for (BidInfo bi: allBids){
    		distanceLookup.put(bi.id,graph.getWeight(townLookup.indexOf(bi.town1),townLookup.indexOf(bi.town2)));
    	}
    }

    private void initializeGraph() {
        graph = new WeightedGraph(townLookup.size());
        for (int i = 0; i < townLookup.size(); i++) {
            graph.setLabel(townLookup.get(i));
        }

        for (int i = 0; i < infra.size(); i++) {
            for (int j = 0; j < infra.get(i).size(); j++) {
                int source = i;
                int target = infra.get(i).get(j);
                // graph.addEdge(source, target, transit[source][target]);
                double distance = calcEuclideanDistance(geo.get(source), geo.get(target));
                graph.addEdge(source, target, distance);
            }
        }
    }
    public void initializeBridgeLinks(){
    	for (int i=0; i < bridges.size();i++){
    		List blink = bridges.get(i);
    		int town1 = (int)blink.get(0);
    		int town2 = (int)blink.get(1);
    		BidInfo bInfo = getBidInfo(town1,town2);
    		bridgeLinks.add(new LinkValue(town1,town2,bInfo));
    	}
    	Collections.sort(bridgeLinks, Collections.reverseOrder());
    }

    public void initializeRouteLinks(){
    	for (int i=0; i < rankedRouteValue.size();i++){
    		List<List<Integer>> listOfRoutes = rankedRouteValue.get(i).routes;
    		for (int j=0; j < listOfRoutes.size();j++){
    			List<Integer> routeInt = listOfRoutes.get(j);
    			List<LinkValue> shortestRoute = new ArrayList<LinkValue>();
    			for (int k=0; k < routeInt.size()-1;k++){
    				int town1 = routeInt.get(k);
    				int town2 = routeInt.get(k+1);
    				BidInfo bInfo = getBidInfo(town1,town2);
    				shortestRoute.add(new LinkValue(town1,town2, bInfo));
    			}
    			Collections.sort(shortestRoute, Collections.reverseOrder());
    			routeLinks.add(shortestRoute);
    		}
    	}
    }

    private double calcEuclideanDistance(Coordinates a, Coordinates b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    private List<List<Integer>> getLinks(int source, int target) {
        int[][] prev = Dijkstra.dijkstra(graph, source);
        return Dijkstra.getPaths(graph, prev, target);
    }

    // private List<List<Integer>> getMostVolumePerKm() {
    //     double max = 0;
    //     List<List<Integer>> maxLinks = new ArrayList<List<Integer>>();
    //     for (int i = 0; i < transit.length; i++) {
    //         for (int j = i + 1; j < transit[i].length; j++) {
    //             List<List<Integer>> links = getLinks(i, j);
    //             System.out.println("transit: s=" + i + ", t=" + j);
    //             for (int m = 0; m < links.size(); m++) {
    //                 for (int n = 0; n < links.get(m).size(); n++) {
    //                     System.out.print(links.get(m).get(n) + " ");
    //                 }
    //             }
    //             int distance = 0;
    //             for (int x = 0; x < links.get(0).size() - 1; x++) {
    //                 distance += graph.getWeight(links.get(0).get(x), links.get(0).get(x + 1));
    //             }

    //             double volumePerKm = (double)transit[i][j] / distance;
    //             if (volumePerKm > max) {
    //                 max = volumePerKm;
    //                 maxLinks = links;
    //             }
    //         }
    //     }

    //     System.out.println("most volume per km: " + max);
    //     return maxLinks;
    // }

    private RouteValue getRouteValue(int source, int target) {
        List<List<Integer>> links = getLinks(source, target);
        int distance = 0;
        for (int i = 0; i < links.get(0).size() - 1; i++) {
            distance += graph.getWeight(links.get(0).get(i), links.get(0).get(i + 1));
        }
        double volumePerKm = (double) transit[source][target] / distance;
        return this.new RouteValue(links, volumePerKm, distance);
    }

    private void gatherAllVolumePerKm() {
        // double max = 0;
        List<List<Integer>> maxLinks = new ArrayList<List<Integer>>();
        for (int i = 0; i < transit.length; i++) {
            for (int j = i + 1; j < transit[i].length; j++) {
                rankedRouteValue.add(getRouteValue(i, j));
                // List<List<Integer>> links = getLinks(i, j);
                // System.out.println("transit: s=" + i + ", t=" + j);
                // for (int m = 0; m < links.size(); m++) {
                //     for (int n = 0; n < links.get(m).size(); n++) {
                //         System.out.print(links.get(m).get(n) + " ");
                //     }
                // }
                // int distance = 0;
                // for (int x = 0; x < links.get(0).size() - 1; x++) {
                //     distance += graph.getWeight(links.get(0).get(x), links.get(0).get(x + 1));
                // }

                // double volumePerKm = (double)transit[i][j] / distance;
                // RouteValue rv = new RouteValue(links, volume, distance);
                // rankedRouteValue.add(rv);
                // if (volumePerKm > max) {
                //     max = volumePerKm;
                //     maxLinks = links;
                // }
            }
        }
        Collections.sort(rankedRouteValue, Collections.reverseOrder());
    }

    private List<List<Integer>> findBridges() {
        List<List<Integer>> bridges = new ArrayList<List<Integer>>();
        for (int i = 0; i < infra.size(); i++) {
            for (int j = 0; j < infra.get(i).size(); j++) {
                int source = i;
                int target = infra.get(i).get(j);
                double weight = graph.getWeight(source, target);
                graph.removeEdge(source, target);
                boolean bridgeFound = false;
                // System.out.println("source: " + source + ", target: " + target);
                for (int s = 0; s < townLookup.size(); s++) {
                    int[][] prev = Dijkstra.dijkstra(graph, s);
                    for (int t = s + 1; t < prev.length; t++) {
                        if (prev[t][0] == 0) {
                            bridgeFound = true;
                            // System.out.println("s: " + s + ", t: " + t);
                            break;
                        }
                    }

                    if (bridgeFound) {
                        break;
                    }
                }

                if (bridgeFound) {
                    List<Integer> bridge = new ArrayList<Integer>();
                    bridge.add(source);
                    bridge.add(target);
                    bridges.add(bridge);
                }

                graph.addEdge(source, target, weight);
            }
        }

        return bridges; 
    }

    // return null if owned by other; return bidInfo if not
    public BidInfo checkOwnershipByTownID(int id1, int id2){
        String name1 = townLookup.get(id1);
        String name2 = townLookup.get(id2);
        for (BidInfo bi : allBids) {
            if (((bi.town1.equals(name1) && bi.town2.equals(name2))||(bi.town1.equals(name2) && bi.town2.equals(name1)))) {
                if (bi.owner!=null&& !bi.owner.equals(this.name)){
                    return null;
                }
                else{
                    return bi;
                }
            }
        }
        return null;
    }

    public BidInfo getBidInfo(int id1, int id2){
        String name1 = townLookup.get(id1);
        String name2 = townLookup.get(id2);
        for (BidInfo bi : allBids){
            if ((bi.town1.equals(name1) && bi.town2.equals(name2))||(bi.town1.equals(name2) && bi.town2.equals(name1))){
                return bi;
            }
        }
        return null;
    }

    public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
        // The random player bids only once in a round.
        // This checks whether we are in the same round.
        // Random player doesn't care about bids made by other players.
        // this.allBids = allBids; 

    	System.out.println(routeLinks.size());

        for (BidInfo bi : allBids) { 
            if (bi.owner == null) {
                availableBids.add(bi);
                availableBidId.add(bi.id);
            }
        } 

        if (availableBids.size()==0){
            return null;
        } 

        // RouteValue routeToBid=null; 
        BidInfo linkToBid =null; 

        // find first bridge in the list, if the bridge is already taken remove it from the list
        double bidAmount = 0;
        while(linkToBid == null && bridgeLinks.size()>0){
        	LinkValue temp = bridgeLinks.get(0);
  			boolean bidAvail = false;
  			for (BidInfo bi: availableBids){
  				if (bi.id == temp.bid.id){
  					bidAvail = true;
  					linkToBid = bi;
  					break;
  				}
  			}
  			if (!bidAvail){
        		bridgeLinks.remove(temp);
        	}
        }

        // if there's no bridge, look for the most traveled route
        if (linkToBid == null){
	        for (int i=0;i<routeLinks.size();i++){
	        	List<LinkValue> path = routeLinks.get(i);
	        	boolean full = true;
	        	for (int j=0;j< path.size();j++){
	        		LinkValue linkV = path.get(j);
	        		int bidId = linkV.bid.id;
	        		if (!availableBidId.contains(bidId) && !ourBidId.contains(bidId)){
	        			routeLinks.remove(path);
	        			i--;
	        			break;
	        		}
	        		if(availableBidId.contains(bidId)){
	        			linkToBid = linkV.bid;
	        			full = false;
	        			break;
	        		}
	        	}
	        	if (full == true){
	        		routeLinks.remove(path);
	        		i--;
	        	}
	        	else{
	        		break;
	        	}
	        }
	    }

        // If no bridge, and no most traveled route, just choose random
        if (linkToBid==null){
        	linkToBid = availableBids.get(rand.nextInt(availableBids.size()));
        }

        // if minimum amount to bid is lower than budget, return null
        bidAmount=linkToBid.amount;
        if (budget - bidAmount < 0.) {
            return null;
        }

        // find current highest bid and over bid that
        Collections.reverse(currentBids);
        double currMax = 0;
        String maxBidder = null;
        for (Bid b : currentBids) {
        	// increment 10000
        	if (b.id1 == linkToBid.id || b.id2 == linkToBid.id) {
                 if (budget - b.amount - 10000 < 0.) {
                     return null;
                 }
                 else{
                 	bidAmount = b.amount + 10000;
                 }
            }
            // find max bid
            double currDis = distanceLookup.get(b.id1);
            if (b.id2 != -1) currDis += distanceLookup.get(b.id2);
            double currVal = b.amount/currDis;
            if (currVal > currMax){
            	currMax = currVal;
            	maxBidder = b.bidder;
            }
        }  
        // increase bid to match max bit                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
        if (maxBidder!= null && !maxBidder.equals(this.name)){
        	double temp = currMax*distanceLookup.get(linkToBid.id);
        	if (temp > bidAmount && temp < budget){
        		bidAmount = temp+10000;
        	}
        }
        else if (maxBidder!=null && maxBidder.equals(this.name)){
        	return null;
        }

        Bid bid = new Bid();
        bid.amount = bidAmount;
        bid.id1 = linkToBid.id;

        return bid;
    }

    public void updateBudget(Bid bid) {
        if (bid != null) {
            budget -= bid.amount;
            ourBidId.add(bid.id1);
            if (bid.id2 != -1){
            	ourBidId.add(bid.id2);
            }
        }

        availableBids = new ArrayList<>();
        availableBidId = new HashSet<>();
    }

    public BidInfo getBidInfo(int id1, int id2, List<BidInfo> allBids){
        String name1 = townLookup.get(id1);
        String name2 = townLookup.get(id2);
        for (BidInfo bi : allBids){
            if ((bi.town1.equals(name1) && bi.town2.equals(name2))||(bi.town1.equals(name2) && bi.town2.equals(name1))){
                return bi;
            }
        }
        return null;
    }

    private class LinkValue implements Comparable<LinkValue>{
        int town1;
        int town2;
        //int townMid; // used for bidding pair of links
        double distance;
        BidInfo bid;

        public LinkValue (int id1, int id2, BidInfo bidInfo){
            town1 = id1;
            town2 = id2;
            distance = graph.getWeight(id1,id2);
            bid = bidInfo;
        }

        // public LinkValue (int id1, int id2, int id3, double dist){
        //     town1 = id1;
        //     town2 = id2;
        //     townMid = id3;
        //     distance = dist;
        // }

        @Override
        public int compareTo(LinkValue lv) {
            return (int) Math.signum(distance - lv.distance);
        }

        @Override
        public boolean equals(Object o) { 
  
            // If the object is compared with itself then return true   
            if (o == this) { 
                return true; 
            } 
  
            /* Check if o is an instance of Complex or not 
            "null instanceof [type]" also returns false */
            if (!(o instanceof LinkValue)) { 
                return false; 
            } 
          
            // typecast o to Complex so that we can compare data members  
            LinkValue lv = (LinkValue) o; 
          
            // Compare the data members and return accordingly  
            return (town1 == lv.town1 && town2 == lv.town2) || (town1 == lv.town2 && town2 == lv.town1); 
        }
    }

    private class RouteValue implements Comparable<RouteValue>{
        List<List<Integer>> routes;
        double volPerKm;
        double distance;

        public RouteValue (List<List<Integer>> r, double v, double d){
            routes = copyListofList(r);
            volPerKm = v;
            distance = d;
        }

        private List<List<Integer>> copyListofList(List<List<Integer>> list) {
            List<List<Integer>> results = new ArrayList<List<Integer>>();
            for (int i = 0; i < list.size(); i++) {
                List<Integer> result = new ArrayList<Integer>();
                for (int j = 0; j < list.get(i).size(); j++) {
                    result.add(list.get(i).get(j));
                }
                results.add(result);
            }
            return results;
        }

        // return true if link is owned by someone else

        public List<List<Integer>> getRoutes() {
            return copyListofList(routes);
        }

        public double getVolumePerKm() {
            return volPerKm;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(RouteValue rv) {
            if (volPerKm != rv.volPerKm) {
                return (int) Math.signum(volPerKm - rv.volPerKm);
            }
            else {
                return (int) Math.signum(distance - rv.distance);
            }
        }
    }
}
