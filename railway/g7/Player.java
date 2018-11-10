// package railway.g7;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.TreeMap;
// import java.util.Random;
// import java.util.Hashtable;
// import java.util.HashSet;
// import java.util.Set;
// import java.lang.management.*;
// // To access data classes.
// import railway.sim.utils.*;

// public class Player implements railway.sim.Player {
//     // Random seed of 42.
//     private int seed = 42;
//     private Random rand;

//     private double budget;
//     private String name;
//     private List<BidInfo> allBids;

//     private List<Coordinates> geo;
//     private List<List<Integer>> infra;
//     private int[][] transit;
//     private List<String> townLookup;
//     private Hashtable<Integer, Double> distanceLookup = 
//               new Hashtable<Integer, Double>();

//     private WeightedGraph graph;
//     private List<RouteValue> rankedRouteValue;
//     private List<List<Integer>> bridges; 
//     private List<LinkValue> bridgeLinks = new ArrayList<>();
//     private List<List<LinkValue>> routeLinks = new ArrayList<>();
//     private Map<LinkValue, List<List<Integer>>> bridgeMap;
//     private double[][] shortestPaths;
//     private Map<LinkValue, Double> bridgeValue;
//     private Map<Double, Link> valueToBridge;
//     private Map<Link, Integer> linkToID;
//     private Map<Integer, Link> idToLink;
//     private Map<Integer, LinkInfo> idToLinkInfo;
//     private Map<String, Integer> townIDLookup;
//     private double lastBidAmount;

//     private List<BidInfo> availableBids = new ArrayList<>();
//     private Set<Integer> availableBidId = new HashSet<>();
//     private Set<Integer> ourBidId = new HashSet<>();

//     public Player() {
//         rand = new Random();
//         valueToBridge = new HashMap<Double, Link>();
//         lastBidAmount = 0.;
//     }

//     public void init(
//         String name,
//         double budget,
//         List<Coordinates> geo,
//         List<List<Integer>> infra,
//         int[][] transit,
//         List<String> townLookup,
//         List<BidInfo> allBids) {
//         this.name = name;
//         this.budget = budget;
//         this.geo = geo;
//         this.infra = infra;
//         this.transit = transit;
//         this.townLookup = townLookup;
//         this.allBids = allBids;
//         shortestPaths = new double[transit.length][transit[0].length];
//         initTownIDLookup();
//         initializeGraph();
//         initLinkTable();
//         // List<List<Integer>> links = getMostVolumePerKm();
//         // for (int i = 0; i < links.size(); i++) {
//         //     System.out.println("The %s link is: ");
//         //     for (int j = 0; j < links.get(i).size(); j++) {
//         //         System.out.print(links.get(i).get(j) + " ");
//         //     }
//         // }
//         // bridges = findBridges();
//         //System.out.println("The bridges are:");
//         // for (int i = 0; i < bridges.size(); i++) {
//         //     for (int j = 0; j < bridges.get(i).size(); j++) {
//         //         //System.out.print(bridges.get(i).get(j) + " ");
//         //     }
//         //     //System.out.println();
//         // }
//         // initializeBridgeLinks();
//         // initializeDistHash();
//         // rankedRouteValue = new ArrayList<RouteValue>();
//         // gatherAllVolumePerKm();
//         BridgeThread bridgeThread = new BridgeThread("BridgeThread");
//         bridgeThread.start();
//         // buildBridgeMap();
//         // for (Map.Entry<LinkValue, List<List<Integer>>> entry: bridgeMap.entrySet()) {
//         //     System.out.println(entry.getKey() + ", from " + townLookup.get(entry.getKey().town1) + " to " + townLookup.get(entry.getKey().town2));
//         //     System.out.println();
//         //     List<List<Integer>> nodes = entry.getValue();
//         //     for (int i = 0; i < nodes.size(); i++) {
//         //         for (int j = 0; j < nodes.get(i).size(); j++) {
//         //             System.out.print(nodes.get(i).get(j) + " ");
//         //         }
//         //         System.out.println();
//         //     }
//         // }
//         // for (Map.Entry<Double, LinkValue> entry: valueToBridge.entrySet()) {
//         //     System.out.println(entry.getValue() + ", from " + townLookup.get(entry.getValue().town1) + " to " + townLookup.get(entry.getValue().town2) + ", and its value is: " + entry.getKey());
//         // }

//         //System.out.println("The bridges are:");
//         // for (int i = 0; i < bridges.size(); i++) {
//         //     for (int j = 0; j < bridges.get(i).size(); j++) {
//         //         //System.out.print(bridges.get(i).get(j) + " ");
//         //     }
//         //     //System.out.println();
//         // }
//         // rankedRouteValue = new ArrayList<RouteValue>();
//         // gatherAllVolumePerKm();
//         // for (int i = 0; i < rankedRouteValue.size(); i++) {
//         //     //System.out.println("route number: " + i + ", volume: " + rankedRouteValue.get(i).getVolumePerKm() + ", distance: " + rankedRouteValue.get(i).getDistance());
//         // }
//         // calculateBridgeValue();

//         // initializeRouteLinks();
//     }

//     private void initializeDistHash(){
//         for (BidInfo bi: allBids){
//             distanceLookup.put(bi.id,graph.getWeight(townLookup.indexOf(bi.town1),townLookup.indexOf(bi.town2)));
//         }
//     }

//     private void initTownIDLookup() {
//         townIDLookup = new HashMap<String, Integer>();
//         for (int i = 0; i < townLookup.size(); i++) {
//             townIDLookup.put(townLookup.get(i), i);
//         }
//     }

//     private void initializeGraph() {
//         graph = new WeightedGraph(townLookup.size());
//         for (int i = 0; i < townLookup.size(); i++) {
//             graph.setLabel(townLookup.get(i));
//         }

//         for (int i = 0; i < infra.size(); i++) {
//             for (int j = 0; j < infra.get(i).size(); j++) {
//                 int source = i;
//                 int target = infra.get(i).get(j);
//                 // graph.addEdge(source, target, transit[source][target]);
//                 double distance = calcEuclideanDistance(geo.get(source), geo.get(target));
//                 graph.addEdge(source, target, distance);
//             }
//         }
//     }

//     private void getUncontestableRoute() {
//         WeightedGraph g = buildGraph();
//         for (int s = 0; s < transit.length; s++) {
//             for (int t = s + 1; t < transit[s].length; s++) {
//                 List<List<Integer>> links = getLinks(s, t);
//                 int num = Integer.MAX_VALUE;
//                 int index = 0;
//                 for (int i = 0; i < links.size(); i++) {
//                     if (links.get(i).size() == 2) {
//                         num = 2;
//                         index = i;
//                         break;
//                     }
//                     if (links.get(i).size() < num) {
//                         num = links.get(i).size();
//                         index = i;
//                     }
//                 }

//                 if (num == 2) {
//                     continue;
//                 } 

//                 int switches = links.get(index).size() - 2;
//                 double addedWeight = switches * 200 / (links.get(index).size() - 1);
//                 for (int i = 0; i < links.get(index).size() - 1; i++) {
//                     double weight = g.getWeight(links.get(index).get(i), links.get(index).get(i + 1));
//                     g.removeEdge(links.get(index).get(i), links.get(index).get(i + 1));
//                     g.addEdge(links.get(index).get(i), links.get(index).get(i + 1), weight + addedWeight);
//                 }

//                 List<List<Integer>> newLinks = getLinks(s, t);
//                 int distance = 0;
//                 for (int i = 0; i < newLinks.get(0).size() - 1; i++) {
//                     distance += g.getWeight(newLinks.get(0).get(i), newLinks.get(0).get(i + 1));
//                 }
//                 if (distance <= shortestPaths[s][t] + switches * 200) {
//                     continue;
//                 }
//             }
//         }

//     }

//     private WeightedGraph buildGraph() {
//         WeightedGraph g = new WeightedGraph(townLookup.size());
//         for (int i = 0; i < townLookup.size(); i++) {
//             g.setLabel(townLookup.get(i));
//         }

//         for (int i = 0; i < infra.size(); i++) {
//             for (int j = 0; j < infra.get(i).size(); j++) {
//                 int source = i;
//                 int target = infra.get(i).get(j);
//                 // g.addEdge(source, target, transit[source][target]);
//                 double distance = calcEuclideanDistance(geo.get(source), geo.get(target));
//                 g.addEdge(source, target, distance);
//             }
//         }

//         return g;
//     }

//     private void initLinkTable() {
//         linkToID = new HashMap<Link, Integer>();
//         idToLink = new HashMap<Integer, Link>();
//         idToLinkInfo = new HashMap<Integer, LinkInfo>();
//         for (BidInfo binfo: allBids) {
//             int i = townIDLookup.get(binfo.town1);
//             int j = townIDLookup.get(binfo.town2);
//             linkToID.put(new Link(i, j), binfo.id);
//             idToLink.put(binfo.id, new Link(i, j));
//             idToLinkInfo.put(binfo.id, new LinkInfo(new Link(i, j), binfo.amount, binfo.owner));
//             // System.out.println("owner: " + binfo.owner);
//         }
//         System.out.println("linkTable finished");
//     }


//     public void initializeBridgeLinks(){
//         for (int i=0; i < bridges.size();i++){
//             List blink = bridges.get(i);
//             int town1 = (int)blink.get(0);
//             int town2 = (int)blink.get(1);
//             BidInfo bInfo = getBidInfo(town1,town2);
//             bridgeLinks.add(new LinkValue(town1,town2,bInfo));
//         }
//         Collections.sort(bridgeLinks, Collections.reverseOrder());
//         System.out.println("initializeBridgeLinks ended");
//     }

//     public void initializeRouteLinks(){
//         for (int i=0; i < rankedRouteValue.size();i++){
//             List<List<Integer>> listOfRoutes = rankedRouteValue.get(i).routes;
//             for (int j=0; j < listOfRoutes.size();j++){
//                 List<Integer> routeInt = listOfRoutes.get(j);
//                 List<LinkValue> shortestRoute = new ArrayList<LinkValue>();
//                 for (int k=0; k < routeInt.size()-1;k++){
//                     int town1 = routeInt.get(k);
//                     int town2 = routeInt.get(k+1);
//                     BidInfo bInfo = getBidInfo(town1,town2);
//                     shortestRoute.add(new LinkValue(town1,town2, bInfo));
//                 }
//                 Collections.sort(shortestRoute, Collections.reverseOrder());
//                 routeLinks.add(shortestRoute);
//             }
//         }
//     }

//     private double calcEuclideanDistance(Coordinates a, Coordinates b) {
//         return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
//     }

//     private List<List<Integer>> getLinks(int source, int target) {
//         int[][] prev = Dijkstra.dijkstra(graph, source);
//         return Dijkstra.getPaths(graph, prev, target);
//     }

//     // private List<List<Integer>> getMostVolumePerKm() {
//     //     double max = 0;
//     //     List<List<Integer>> maxLinks = new ArrayList<List<Integer>>();
//     //     for (int i = 0; i < transit.length; i++) {
//     //         for (int j = i + 1; j < transit[i].length; j++) {
//     //             List<List<Integer>> links = getLinks(i, j);
//     //             System.out.println("transit: s=" + i + ", t=" + j);
//     //             for (int m = 0; m < links.size(); m++) {
//     //                 for (int n = 0; n < links.get(m).size(); n++) {
//     //                     System.out.print(links.get(m).get(n) + " ");
//     //                 }
//     //             }
//     //             int distance = 0;
//     //             for (int x = 0; x < links.get(0).size() - 1; x++) {
//     //                 distance += graph.getWeight(links.get(0).get(x), links.get(0).get(x + 1));
//     //             }

//     //             double volumePerKm = (double)transit[i][j] / distance;
//     //             if (volumePerKm > max) {
//     //                 max = volumePerKm;
//     //                 maxLinks = links;
//     //             }
//     //         }
//     //     }

//     //     System.out.println("most volume per km: " + max);
//     //     return maxLinks;
//     // }

//     private RouteValue getRouteValue(int source, int target) {
//         List<List<Integer>> links = getLinks(source, target);
//         int distance = 0;
//         for (int i = 0; i < links.get(0).size() - 1; i++) {
//             distance += graph.getWeight(links.get(0).get(i), links.get(0).get(i + 1));
//         }
//         double volumePerKm = (double) transit[source][target] / distance;
//         shortestPaths[source][target] = distance;
//         return this.new RouteValue(links, volumePerKm, distance);
//     }

//     private void gatherAllVolumePerKm() {
//         // double max = 0;
//         rankedRouteValue = new ArrayList<RouteValue>();
//         // List<List<Integer>> maxLinks = new ArrayList<List<Integer>>();
//         for (int i = 0; i < transit.length; i++) {
//             for (int j = i + 1; j < transit[i].length; j++) {
//                 rankedRouteValue.add(getRouteValue(i, j));
//                 // List<List<Integer>> links = getLinks(i, j);
//                 // System.out.println("transit: s=" + i + ", t=" + j);
//                 // for (int m = 0; m < links.size(); m++) {
//                 //     for (int n = 0; n < links.get(m).size(); n++) {
//                 //         System.out.print(links.get(m).get(n) + " ");
//                 //     }
//                 // }
//                 // int distance = 0;
//                 // for (int x = 0; x < links.get(0).size() - 1; x++) {
//                 //     distance += graph.getWeight(links.get(0).get(x), links.get(0).get(x + 1));
//                 // }

//                 // double volumePerKm = (double)transit[i][j] / distance;
//                 // RouteValue rv = new RouteValue(links, volume, distance);
//                 // rankedRouteValue.add(rv);
//                 // if (volumePerKm > max) {
//                 //     max = volumePerKm;
//                 //     maxLinks = links;
//                 // }
//             }
//         }
//         Collections.sort(rankedRouteValue, Collections.reverseOrder());
//     }

//     // private List<List<Integer>> findBridges() {
//     //     List<List<Integer>> bridges = new ArrayList<List<Integer>>();
//     //     for (int i = 0; i < infra.size(); i++) {
//     //         for (int j = 0; j < infra.get(i).size(); j++) {
//     //             int source = i;
//     //             int target = infra.get(i).get(j);
//     //             double weight = graph.getWeight(source, target);
//     //             graph.removeEdge(source, target);
//     //             boolean bridgeFound = false;
//     //             // System.out.println("source: " + source + ", target: " + target);
//     //             for (int s = 0; s < townLookup.size(); s++) {
//     //                 int[][] prev = Dijkstra.dijkstra(graph, s);
//     //                 for (int t = s + 1; t < prev.length; t++) {
//     //                     if (prev[t][0] == 0) {
//     //                         bridgeFound = true;
//     //                         // System.out.println("s: " + s + ", t: " + t);
//     //                         break;
//     //                     }
//     //                 }

//     //                 if (bridgeFound) {
//     //                     break;
//     //                 }
//     //             }

//     //             if (bridgeFound) {
//     //                 List<Integer> bridge = new ArrayList<Integer>();
//     //                 bridge.add(source);
//     //                 bridge.add(target);
//     //                 bridges.add(bridge);
//     //             }

//     //             graph.addEdge(source, target, weight);
//     //         }
//     //     }

//     //     return bridges; 
//     // }

//     // private void buildBridgeMap() {
//     //     System.out.println("buildBridgeMap started");
//     //     bridgeMap = new HashMap<LinkValue, List<List<Integer>>>();
//     //     valueToBridge = new TreeMap<Double, LinkValue>(Collections.reverseOrder());
//     //     for (int i = 0; i < infra.size(); i++) {
//     //         for (int j = 0; j < infra.get(i).size(); j++) {
//     //             int source = i;
//     //             int target = infra.get(i).get(j);
//     //             double weight = graph.getWeight(source, target);
//     //             graph.removeEdge(source, target);
//     //             // boolean bridgeFound = false;
//     //             // System.out.println("source: " + source + ", target: " + target);
//     //             BidInfo bInfo = getBidInfo(source,target);
//     //             LinkValue lv = new LinkValue(source, target, bInfo);
//     //             double value = 0;
//     //             for (int s = 0; s < townLookup.size(); s++) {
//     //                 int[][] prev = Dijkstra.dijkstra(graph, s);
//     //                 for (int t = s + 1; t < prev.length; t++) {
//     //                     if (prev[t][0] != 0) {
//     //                         // bridgeFound = true;
//     //                         // System.out.println("s: " + s + ", t: " + t);
//     //                         // break;
//     //                         continue;
//     //                     }

//     //                     if (!bridgeMap.containsKey(lv)) {
//     //                         // System.out.println(lv + ", not contained in map!");
//     //                         List<List<Integer>> nodes = new ArrayList<List<Integer>>();
//     //                         // List<Integer> part1 = new ArrayList<Integer>();
//     //                         // List<Integer> part2 = new ArrayList<Integer>();
//     //                         // nodes.add(part1);
//     //                         // nodes.add(part2);
//     //                         bridgeMap.put(lv, nodes);
//     //                     }
//     //                     List<Integer> pair = new ArrayList<Integer>();
//     //                     pair.add(s);
//     //                     pair.add(t);
//     //                     List<List<Integer>> nodes = bridgeMap.get(lv);
//     //                     nodes.add(pair);
//     //                     value += shortestPaths[s][t] * transit[s][t];
//     //                     System.out.println("value is: " + value);
//     //                     // nodes.get(0).add(s);
//     //                     // nodes.get(1).add(t);
//     //                 }
//     //             }

//     //             graph.addEdge(source, target, weight);

//     //             if (!bridgeMap.containsKey(lv)) {
//     //                 continue;
//     //             }

//     //             List<List<Integer>> nodes = bridgeMap.get(lv);
//     //             System.out.println("one map built, and nodes size: " + nodes.size() + ", node size: " + nodes.get(0).size());
//     //             if (nodes.get(0).get(0) == nodes.get(nodes.size() / 2).get(0) && nodes.get(0).get(1) == nodes.get(nodes.size() / 2).get(1)) {
//     //                 // System.out.println("duplicate route, value should be halved: " + value);
//     //                 value /= 2;
//     //             }


//     //             valueToBridge.put(value, lv);
//     //         }
//     //     }
//     // }


//     private void calculateBridgeValue() {
//         for (Map.Entry<LinkValue, List<List<Integer>>> entry: bridgeMap.entrySet()) {
//             System.out.println(entry.getKey() + ", from " + townLookup.get(entry.getKey().town1) + " to " + townLookup.get(entry.getKey().town2));
//             System.out.println();
//             List<List<Integer>> nodes = entry.getValue();
//             double value = 0;
//             for (int i = 0; i < nodes.size(); i++) {
//                 for (int j = 0; j < nodes.get(i).size(); j++) {
//                     System.out.print(nodes.get(i).get(j) + " ");
//                 }
//                 int source = nodes.get(i).get(0);
//                 int target = nodes.get(i).get(1);
//                 value += shortestPaths[source][target] * transit[source][target];
//                 System.out.println();
//             }
//             if (nodes.get(0).get(0) == nodes.get(nodes.size() / 2).get(0) && nodes.get(0).get(1) == nodes.get(nodes.size() / 2).get(1)) {
//                 System.out.println("duplicate route, value should be halved: " + value);
//                 value /= 2;
//             }
//             System.out.println("the value of this link is: " + value);
//             bridgeValue.put(entry.getKey(), value);
//         }
//     }

//     // return null if owned by other; return bidInfo if not
//     public BidInfo checkOwnershipByTownID(int id1, int id2){
//         String name1 = townLookup.get(id1);
//         String name2 = townLookup.get(id2);
//         for (BidInfo bi : allBids) {
//             if (((bi.town1.equals(name1) && bi.town2.equals(name2))||(bi.town1.equals(name2) && bi.town2.equals(name1)))) {
//                 if (bi.owner!=null&& !bi.owner.equals(this.name)){
//                     return null;
//                 }
//                 else{
//                     return bi;
//                 }
//             }
//         }
//         return null;
//     }

//     public int getBidID(int id1, int id2){
//         String name1 = townLookup.get(id1);
//         String name2 = townLookup.get(id2);
//         for (BidInfo bi : allBids){
//             if ((bi.town1.equals(name1) && bi.town2.equals(name2))||(bi.town1.equals(name2) && bi.town2.equals(name1))){
//                 return bi.id;
//             }
//         }
//         return -1;
//     }

//     public LinkValue getLongestPair(RouteValue route){
//         List<LinkValue> doubleLinks= new ArrayList<LinkValue>();
//         for (int i=0; i< route.routes.get(0).size()-1;i++){
//             return null;
//         }
//         Collections.sort(doubleLinks, Collections.reverseOrder());
//         if (doubleLinks.size()>0){
//             return doubleLinks.get(0);
//         }
//         else{
//             return null;
//         }
//     }

//     public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
//         // The random player bids only once in a round.
//         // This checks whether we are in the same round.
//         // Random player doesn't care about bids made by other players.
//         // this.allBids = allBids; 

//         // System.out.println(routeLinks.size());
//         updateAvailableLinks(lastRoundMaxBid);
//         int linkID = -1;
//         double value = 0;
//         if (valueToBridge.size() == 0) {
//             System.out.println("no bridge");
//             return getRandomBid(currentBids, allBids, lastRoundMaxBid);
//         }
//         for (Map.Entry<Double, Link> entry: valueToBridge.entrySet()) {
//             Link nextBridge = entry.getValue();
//             if (linkToID.containsKey(nextBridge)) {
//                 linkID = linkToID.get(nextBridge);
//                 value = entry.getKey();
//                 valueToBridge.remove(value);
//                 break;
//             }
//         }

//         if (linkID == -1) {
//             return getRandomBid(currentBids, allBids, lastRoundMaxBid);
//         }

//         double max = 0.;
//         String bidder = "";
//         for (Bid bid: currentBids) {
//             Link link = idToLink.get(bid.id1);
//             double distance = graph.getWeight(link.town1, link.town2);
//             if (bid.amount / distance > max) {
//                 max = bid.amount / distance;
//                 bidder = bid.bidder;
//             }

//             if (bid.id2 == -1) {
//                 continue;
//             }
//             link = idToLink.get(bid.id2);
//             distance = graph.getWeight(link.town1, link.town2);
//             if (bid.amount / distance > max) {
//                 max = bid.amount / distance;
//                 bidder = bid.bidder;
//             }
//         }

//         Link bridge = idToLink.get(linkID);
//         double distance = bridge.getDistance();
//         System.out.println("max bidder: " + bidder);
//         if (bidder.equals("g7") || max * distance > value * 10) {
//             System.out.println("we are the highest price already, or the current price is too high");
//             return null;
//         }

//         Bid ourBid = new Bid();
//         ourBid.id1 = linkID;
//         LinkInfo li = idToLinkInfo.get(linkID);
//         if (li.getAmount() > max * distance) {
//             if (budget < li.getAmount()) {
//                 System.out.println("budget smaller than minimum bid, budget: " + budget + ", min: " + li.getAmount());
//                 return null;
//             }
//             ourBid.amount = li.getAmount();
//             System.out.println("we bid the minimum amount: " + li.getAmount());
//         }
//         else {
//             if (budget < max * distance + 10000) {
//                 System.out.println("budget smaller than max * distance + 10000, budget: " + budget + ", bid required: " + Double.toString(max * distance + 10000));
//                 return null;
//             }
//             ourBid.amount = max * distance + 10000;
//             System.out.println("we increased the max bid by 10000: " + ourBid.amount);
//         }

//         return ourBid;

//         // for (BidInfo bi : allBids) { 
//         //    if (bi.owner == null) {
//         //         availableBids.add(bi);
//         //         availableBidId.add(bi.id);
//         //     }
//         // } 

//         // if (availableBids.size()==0){
//         //     return null;
//         // } 

//         // // RouteValue routeToBid=null; 
//         // BidInfo linkToBid =null; 

//         // // find first bridge in the list, if the bridge is already taken remove it from the list
//         // double bidAmount = 0;
//         // double maxAmount = 0;
//         // List<Double> values= new ArrayList<Double>(valueToBridge.keySet());
//         // System.out.println("keySet:"+ values.size());
//         // while(linkToBid == null && valueToBridge.size()>0){
//         //     LinkValue temp = valueToBridge.get(values.get(0));
//         //     boolean aval = false;
//         //     for (BidInfo bi: availableBids){
//         //         if (bi.id == temp.bid.id){
//         //             linkToBid = bi;
//         //             aval = true;
//         //             maxAmount = 5*values.get(0);
//         //             break;
//         //         }
//         //     }
//         //     if (!aval){
//         //         valueToBridge.remove(values.get(0));
//         //         values.remove(values.get(0));
//         //     }
//         // }

//         // // if there's no bridge, look for the most traveled route
//         // if (linkToBid == null) {
//         //     System.out.println("There's no bridges");
//         // }

//         // return linkToBid;
//         // if (linkToBid == null){
//         //     System.out.println("There's no bridge");
//         //     RouteValue routeToBid = null;
//         //     for (int i=0;i<routeLinks.size();i++){
//         //         List<LinkValue> path = routeLinks.get(0);
//         //         routeToBid = rankedRouteValue.get(0);
//         //         boolean full = true;
//         //         for (int j=0;j< path.size();j++){
//         //             LinkValue linkV = path.get(j);
//         //             int bidId = linkV.bid.id;
//         //             if (!availableBidId.contains(bidId) && !ourBidId.contains(bidId)){
//         //                 routeLinks.remove(path);
//         //                 rankedRouteValue.remove(routeToBid);
//         //                 i--;
//         //                 break;
//         //             }
//         //             if(availableBidId.contains(bidId)){
//         //                 linkToBid = linkV.bid;
//         //                 full = false;
//         //                 break;
//         //             }
//         //         }
//         //         if (full){
//         //             routeLinks.remove(path);
//         //             rankedRouteValue.remove(routeToBid);
//         //             i--;
//         //         }
//         //         else{
//         //             break;
//         //         }
//         //     }
//         //     if (linkToBid!=null){
//         //         maxAmount = linkToBid.amount;
//         //         // if (secondLinkToBid != null) {
//         //         //     amount += secondLinkValueToBid.distance * transit[secondLinkValueToBid.town1][secondLinkValueToBid.town2];
//         //         //     amount += secondLinkToBid.amount;
//         //         // }

//         //         // taking into account the entire route 
//         //         maxAmount += 10*routeToBid.volPerKm * routeToBid.distance; // the entire distance? 
//         //     }
//         // }

//         // // If no bridge, and no most traveled route, just choose random
//         // if (linkToBid==null){
//         //     linkToBid = availableBids.get(rand.nextInt(availableBids.size()));
//         // }

//         // // if minimum amount to bid is lower than budget, return null
//         // bidAmount=linkToBid.amount;
//         // if (budget - bidAmount < 0.) {
//         //     return null;
//         // }

//         // // find current highest bid and over bid that
//         // Collections.reverse(currentBids);
//         // double currMax = 0;
//         // String maxBidder = null;
//         // Set<Integer> maxLinkID = new HashSet<Integer>();
//         // for (Bid b : currentBids) {
//         //     // increment 10000
//         //     if (b.id1 == linkToBid.id || b.id2 == linkToBid.id) {
//         //          if (budget - b.amount - 10000 < 0.) {
//         //              return null;
//         //          }
//         //          else{
//         //             bidAmount = b.amount + 10000;
//         //          }
//         //     }
//         //     // find max bid
//         //     double currDis = distanceLookup.get(b.id1);
//         //     if (b.id2 != -1) currDis += distanceLookup.get(b.id2);
//         //     double currVal = b.amount/currDis;
//         //     if (currVal > currMax){
//         //         currMax = currVal;
//         //         maxLinkID.add(b.id1);
//         //         if (b.id2 != -1){
//         //             maxLinkID.add(b.id2);
//         //         }
//         //         maxBidder = b.bidder;
//         //     }
//         // }  
//         // System.out.println("MaxBidder:" +maxBidder);
//         // if (maxBidder!= null && !maxBidder.equals(this.name)){
//         //     double temp = currMax*distanceLookup.get(linkToBid.id)+1;
//         //     if (temp > maxAmount){
//         //         System.out.println("Match MaxBidder is too high");
//         //         return null;
//         //     }
//         //     if (temp > bidAmount && temp < budget){
//         //         System.out.println("Match MaxBidder");
//         //         bidAmount = temp;
//         //         if (maxLinkID.contains(linkToBid.id)){
//         //             bidAmount+=10000;
//         //         }
//         //     }
//         // }
//         // else if (maxBidder!=null && maxBidder.equals(this.name)){
//         //     return null;
//         // }

//         // Bid bid = new Bid();
//         // bid.amount = bidAmount;
//         // bid.id1 = linkToBid.id;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
//         // return bid;
//     }

//     private Bid getRandomBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
//         if (availableBids.size() != 0) {
//             return null;
//         }

//         for (BidInfo bi : allBids) {
//             if (bi.owner == null) {
//                 availableBids.add(bi);
//             }
//         }

//         if (availableBids.size() == 0) {
//             return null;
//         }

//         BidInfo randomBid = availableBids.get(rand.nextInt(availableBids.size()));
//         double amount = randomBid.amount;

//         // Don't bid if the random bid turns out to be beyond our budget.
//         if (budget - amount < 0.) {
//             return null;
//         }

//         // Check if another player has made a bid for this link.
//         for (Bid b : currentBids) {
//             if (b.id1 == randomBid.id || b.id2 == randomBid.id) {
//                 if (budget - b.amount - 10000 < 0.) {
//                     return null;
//                 }
//                 else {
//                     amount = b.amount + 10000;
//                 }

//                 break;
//             }
//         }

//         Bid bid = new Bid();
//         bid.amount = amount;
//         bid.id1 = randomBid.id;

//         return bid;
//     }

//     private void updateAvailableLinks(Bid lastRoundMaxBid) {
//         if (lastRoundMaxBid == null) {
//             return;
//         }

//         Link lastAwardedLink = idToLink.get(lastRoundMaxBid.id1);
//         linkToID.remove(lastAwardedLink);
//         if (lastRoundMaxBid.id2 == -1) {
//             return;
//         }
//         lastAwardedLink = idToLink.get(lastRoundMaxBid.id2);
//         linkToID.remove(lastAwardedLink);

//         if (lastRoundMaxBid.bidder.equals("g7")) {
//             budget -= lastRoundMaxBid.amount;
//         }
//     }

//     public void updateBudget(Bid bid) {
//         if (bid != null) {
//             budget -= bid.amount;
//             ourBidId.add(bid.id1);
//             if (bid.id2 != -1){
//                 ourBidId.add(bid.id2);
//             }
//         }

//         availableBids = new ArrayList<>();
//         availableBidId = new HashSet<>();
//     }

//     public BidInfo getBidInfo(int id1, int id2){
//         String name1 = townLookup.get(id1);
//         String name2 = townLookup.get(id2);
//         for (BidInfo bi : allBids){
//             if ((bi.town1.equals(name1) && bi.town2.equals(name2))||(bi.town1.equals(name2) && bi.town2.equals(name1))){
//                 return bi;
//             }
//         }
//         return null;
//     }

//     private class LinkValue implements Comparable<LinkValue>{
//         int town1;
//         int town2;
//         //int townMid; // used for bidding pair of links
//         double distance;
//         BidInfo bid;

//         public LinkValue (int id1, int id2, BidInfo bidInfo){
//             town1 = id1;
//             town2 = id2;
//             distance = graph.getWeight(id1,id2);
//             bid = bidInfo;
//         }

//         // public LinkValue (int id1, int id2, int id3, double dist){
//         //     town1 = id1;
//         //     town2 = id2;
//         //     townMid = id3;
//         //     distance = dist;
//         // }

//         @Override
//         public int compareTo(LinkValue lv) {
//             return (int) Math.signum(distance - lv.distance);
//         }

//         @Override
//         public boolean equals(Object o) { 
  
//             // If the object is compared with itself then return true   
//             if (o == this) { 
//                 return true; 
//             } 
  
//             /* Check if o is an instance of Complex or not 
//             "null instanceof [type]" also returns false */
//             if (!(o instanceof LinkValue)) { 
//                 return false; 
//             } 
          
//             // typecast o to Complex so that we can compare data members  
//             LinkValue lv = (LinkValue) o; 
          
//             // Compare the data members and return accordingly  
//             return (town1 == lv.town1 && town2 == lv.town2) || (town1 == lv.town2 && town2 == lv.town1); 
//         }

//         @Override
//         public int hashCode() {
//             String s = "";
//             if (town1 < town2) {
//                 s = Integer.toString(town1) + Integer.toString(town2);
                
//             }
//             else {
//                 s = Integer.toString(town2) + Integer.toString(town1);
//             }
//             return s.hashCode();
//         }

//         @Override
//         public String toString() {
//             return new String("This link is from " + town1 + " to " + town2);
//         }
//     }

//     private class Link {
//         private int town1;
//         private int town2;

//         public Link(int id1, int id2){
//             if (id1 < id2) {
//                 town1 = id1;
//                 town2 = id2;
//             }
//             else {
//                 town1 = id2;
//                 town2 = id1;
//             }
//         }

//         public double getDistance() {
//             return graph.getWeight(town1, town2);
//         }

//         @Override
//         public boolean equals(Object o) { 
  
//             // If the object is compared with itself then return true   
//             if (o == this) { 
//                 return true; 
//             } 
  
//             /* Check if o is an instance of Complex or not 
//             "null instanceof [type]" also returns false */
//             if (!(o instanceof Link)) { 
//                 return false; 
//             } 
          
//             // typecast o to Complex so that we can compare data members  
//             Link lv = (Link) o; 
          
//             // Compare the data members and return accordingly  
//             return (town1 == lv.town1 && town2 == lv.town2) || (town1 == lv.town2 && town2 == lv.town1); 
//         }

//         @Override
//         public int hashCode() {
//             String s = "";
//             if (town1 < town2) {
//                 s = Integer.toString(town1) + Integer.toString(town2);
                
//             }
//             else {
//                 s = Integer.toString(town2) + Integer.toString(town1);
//             }
//             return s.hashCode();
//         }

//         @Override
//         public String toString() {
//             return new String("This link is from " + town1 + " to " + town2);
//         }
//     }

//     private class LinkInfo {
//         private Link link;
//         private double amount;
//         private String owner;

//         public LinkInfo(Link l, double a, String o){
//             link = new Link(l.town1, l.town2);
//             amount = a;
//             owner = o;
//         }

//         public Link getLink() {
//             return new Link(link.town1, link.town2);
//         }

//         public void setAmount(double a) {
//             amount = a;
//         }

//         public double getAmount() {
//             return amount;
//         }

//         public void setOwner(String o) {
//             owner = o;
//         }

//         public String getOwner() {
//             return owner;
//         }

//         @Override
//         public boolean equals(Object o) { 
  
//             // If the object is compared with itself then return true   
//             if (o == this) { 
//                 return true; 
//             } 
  
//             /* Check if o is an instance of Complex or not 
//             "null instanceof [type]" also returns false */
//             if (!(o instanceof LinkInfo)) { 
//                 return false; 
//             } 
          
//             // typecast o to Complex so that we can compare data members  
//             LinkInfo lv = (LinkInfo) o; 
          
//             // Compare the data members and return accordingly  
//             return link.equals(lv.link) && amount == lv.amount && owner.equals(lv.owner); 
//         }

//         @Override
//         public int hashCode() {
//             String s = link.toString() + Double.toString(amount) + owner;
//             return s.hashCode();
//         }

//         @Override
//         public String toString() {
//             return link.toString() + ", the amount is: " + Double.toString(amount) + ", and the owner is: " + owner;
//         }
//     }

//     private class RouteValue implements Comparable<RouteValue>{
//         List<List<Integer>> routes;
//         double volPerKm;
//         double distance;

//         public RouteValue (List<List<Integer>> r, double v, double d){
//             routes = copyListofList(r);
//             volPerKm = v;
//             distance = d;
//         }

//         private List<List<Integer>> copyListofList(List<List<Integer>> list) {
//             List<List<Integer>> results = new ArrayList<List<Integer>>();
//             for (int i = 0; i < list.size(); i++) {
//                 List<Integer> result = new ArrayList<Integer>();
//                 for (int j = 0; j < list.get(i).size(); j++) {
//                     result.add(list.get(i).get(j));
//                 }
//                 results.add(result);
//             }
//             return results;
//         }

//         // return true if link is owned by someone else

//         public List<List<Integer>> getRoutes() {
//             return copyListofList(routes);
//         }

//         public double getVolumePerKm() {
//             return volPerKm;
//         }

//         public double getDistance() {
//             return distance;
//         }

//         @Override
//         public int compareTo(RouteValue rv) {
//             if (volPerKm != rv.volPerKm) {
//                 return (int) Math.signum(volPerKm - rv.volPerKm);
//             }
//             else {
//                 return (int) Math.signum(distance - rv.distance);
//             }
//         }
//     }

//     private class BridgeThread implements Runnable {
//         private Thread t;
//         private String name;

//         BridgeThread(String name) {
//             this.name = name;
//         }

//         public void run() {
//             // initializeGraph();
//             // rankedRouteValue = new ArrayList<RouteValue>();
//             // gatherAllVolumePerKm();
//             // initLinkTable();
//             bridges = findBridges();
//             // initializeBridgeLinks();
//             initializeDistHash();
//             rankedRouteValue = new ArrayList<RouteValue>();
//             gatherAllVolumePerKm();
//             buildBridgeMap();
//             initializeRouteLinks();
//         }

//         public void start () {
//             if (t == null) {
//                 t = new Thread (this, name);
//                 t.start ();
//             }
//         }

//         // private void initLinkTable() {
//         //     linkToID = new HashMap<Link, Integer>();
//         //     idToLink = new HashMap<Integer, Link>();
//         //     idToLinkInfo = new HashMap<Integer, LinkInfo>();
//         //     for (BidInfo binfo: allBids) {
//         //         int i = townIDLookup.get(binfo.town1);
//         //         int j = townIDLookup.get(binfo.town2);
//         //         linkToID.put(new Link(i, j), binfo.id);
//         //         idToLink.put(binfo.id, new Link(i, j));
//         //         idToLinkInfo.put(binfo.id, new LinkInfo(new Link(i, j), binfo.amount, binfo.owner));
//         //     }
//         //     System.out.println("linkTable finished");
//         // }

//         private List<List<Integer>> findBridges() {
//             List<List<Integer>> bridges = new ArrayList<List<Integer>>();
//             for (int i = 0; i < infra.size(); i++) {
//                 for (int j = 0; j < infra.get(i).size(); j++) {
//                     int source = i;
//                     int target = infra.get(i).get(j);
//                     double weight = graph.getWeight(source, target);
//                     graph.removeEdge(source, target);
//                     boolean bridgeFound = false;
//                 // System.out.println("source: " + source + ", target: " + target);
//                     for (int s = 0; s < townLookup.size(); s++) {
//                         int[][] prev = Dijkstra.dijkstra(graph, s);
//                         for (int t = s + 1; t < prev.length; t++) {
//                             if (prev[t][0] == 0) {
//                                 bridgeFound = true;
//                             // System.out.println("s: " + s + ", t: " + t);
//                                 break;
//                             }
//                         }

//                         if (bridgeFound) {
//                             break;
//                         }
//                     }

//                     if (bridgeFound) {
//                         List<Integer> bridge = new ArrayList<Integer>();
//                         bridge.add(source);
//                         bridge.add(target);
//                         bridges.add(bridge);
//                     }

//                     graph.addEdge(source, target, weight);
//                 }
//             }

//             System.out.println("find bridges ended");
//             return bridges; 
//         }

//         private void buildBridgeMap() {
//             System.out.println(Thread.currentThread().getId());
//             System.out.println(ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId()));
//             System.out.println("buildBridgeMap started");
//             valueToBridge = new TreeMap<Double, Link>(Collections.reverseOrder());
//             for (int i = 0; i < infra.size(); i++) {
//                 for (int j = 0; j < infra.get(i).size(); j++) {
//                     int source = i;
//                     int target = infra.get(i).get(j);
//                     double weight = graph.getWeight(source, target);
//                     graph.removeEdge(source, target);
//                     Link lv = new Link(source, target);
//                     double value = 0;
//                     List<List<Integer>> nodes = new ArrayList<List<Integer>>();
//                     for (int s = 0; s < townLookup.size(); s++) {
//                         int[][] prev = Dijkstra.dijkstra(graph, s);
//                         for (int t = s + 1; t < prev.length; t++) {
//                             if (prev[t][0] != 0) {
//                                 continue;
//                             }

//                             List<Integer> pair = new ArrayList<Integer>();
//                             pair.add(s);
//                             pair.add(t);
//                             nodes.add(pair);
//                             value += shortestPaths[s][t] * transit[s][t];
//                         }
//                     }

//                     graph.addEdge(source, target, weight);
//                     if (nodes.size() == 0) {
//                         continue;
//                     }

//                     if (nodes.get(0).get(0) == nodes.get(nodes.size() / 2).get(0) && nodes.get(0).get(1) == nodes.get(nodes.size() / 2).get(1)) {
//                         value /= 2;
//                     }


//                     valueToBridge.put(value, lv);
//                     System.out.println(ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId()));
//                 }
//             }
//             System.out.println("buildBridgeMap ended");
//             System.out.println(ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId()));
//         }
//     }

// }

package railway.g7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Random;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.lang.management.*;
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

    private WeightedGraph graph;
    private List<RouteValue> rankedRouteValue;
    // private List<List<Integer>> bridges; 
    // private double[][] shortestPaths;
    private RouteValue[][] routeTable;
    private Map<Link, Double> linkToValue;
    private Map<Double, Link> valueToLink;
    private Set<Link> bridges;
    // private Map<Double, Link> valueToBridge;
    private Map<Link, Integer> linkToID;
    private Map<Integer, Link> idToLink;
    private Map<Integer, LinkInfo> idToLinkInfo;
    private Map<String, Integer> townIDLookup;

    private List<BidInfo> availableBids = new ArrayList<>();
    private Set<Integer> availableBidId = new HashSet<>();
    private Set<Integer> ourBidId = new HashSet<>();

    public Player() {
        rand = new Random();
        linkToValue = new HashMap<Link, Double>();
        valueToLink = new TreeMap<Double, Link>(Collections.reverseOrder());
        bridges = new HashSet<Link>();
        // valueToBridge = new TreeMap<Double, Link>(Collections.reverseOrder());
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
        // shortestPaths = new double[transit.length][transit[0].length];
        routeTable = new RouteValue[transit.length][transit[0].length];
        initTownIDLookup();
        initializeGraph();
        initLinkTable();
        BridgeThread bridgeThread = new BridgeThread("BridgeThread");
        bridgeThread.start();
    }

    private void initTownIDLookup() {
        townIDLookup = new HashMap<String, Integer>();
        for (int i = 0; i < townLookup.size(); i++) {
            townIDLookup.put(townLookup.get(i), i);
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
                Link link = new Link(source, target);
                linkToValue.put(link, 0.);
                // graph.addEdge(source, target, transit[source][target]);
                double distance = calcEuclideanDistance(geo.get(source), geo.get(target));
                graph.addEdge(source, target, distance);
            }
        }
    }

    private void getUncontestableRoute() {
        // System.out.println("getUncontestableRoute started");
        WeightedGraph g = buildGraph();
        for (int s = 0; s < transit.length; s++) {
            for (int t = s + 1; t < transit[s].length; t++) {
                // System.out.println("source: " + s + ", target: " + t);
                List<List<Integer>> links = getLinks(g, s, t);
                int num = Integer.MAX_VALUE;
                int index = 0;
                for (int i = 0; i < links.size(); i++) {
                    // System.out.println("links.get(i).size(): " + links.get(i).size() + ", index is: " + i);
                    if (links.get(i).size() == 2) {
                        num = 2;
                        index = i;
                        break;
                    }
                    if (links.get(i).size() < num) {
                        num = links.get(i).size();
                        index = i;
                    }
                }

                if (num == 2) {
                    continue;
                } 

                int switches = links.get(index).size() - 2;
                // System.out.println("switches: " + switches);
                // System.out.println("***************links.get(index).size(): " + links.get(index).size());
                // double addedWeight = switches * 200 / (links.get(index).size() - 1);
                List<Double> weights = new ArrayList<Double>();
                for (int i = 0; i < links.get(index).size() - 1; i++) {
                    int source = links.get(index).get(i);
                    int target = links.get(index).get(i + 1);
                    if (bridges.contains(new Link(source, target))) {
                        continue;
                    }
                    double weight = g.getWeight(source, target);
                    weights.add(weight);
                    g.removeEdge(source, target);
                    g.removeEdge(target, source);
                    // System.out.println("edge removed: " + new Link(source, target));
                    // g.addEdge(links.get(index).get(i), links.get(index).get(i + 1), weight + addedWeight);
                }
                // g.print();

                List<List<Integer>> newLinks = getLinks(g, s, t);
                int distance = 0;
                for (int i = 0; i < newLinks.get(0).size() - 1; i++) {
                    // System.out.println(new Link(newLinks.get(0).get(i), newLinks.get(0).get(i + 1)));
                    distance += g.getWeight(newLinks.get(0).get(i), newLinks.get(0).get(i + 1));
                }
                // if (distance <= shortestPaths[s][t] + switches * 200) {
                //     continue;
                // }

                int n = 0;
                for (int i = 0; i < links.get(index).size() - 1; i++) {
                    int source = links.get(index).get(i);
                    int target = links.get(index).get(i + 1);
                    // double weight = g.getWeight(source, target);
                    // System.out.println(new Link(source, target) + ", initial weight: " + weight + ", offset: " + addedWeight);
                    // g.removeEdge(source, target);
                    if (bridges.contains(new Link(source, target))) {
                        continue;
                    }
                    g.addEdge(source, target, weights.get(n));
                    n++;
                }
                // System.out.println("new distance: " + distance + ", old distance: " + routeTable[s][t].getDistance());
                if (distance - 0.1 <= routeTable[s][t].getDistance() + switches * 200) {
                    continue;
                }

                links = routeTable[s][t].getRoutes();
                if (links.size() == 0) {
                    continue;
                }

                // update all values in our table, at the same time, revert the distance of edges
                for (int i = 0; i < links.get(0).size() - 1; i++) {
                    int source = links.get(0).get(i);
                    int target = links.get(0).get(i + 1);
                    // double weight = g.getWeight(source, target);
                    // g.removeEdge(source, target);
                    // g.addEdge(source, target, weight - addedWeight);
                    Link link = new Link(source, target);
                    if (bridges.contains(link)) {
                        // System.out.println("bridge not updated: " + link.toString());
                        continue;
                    }
                    double originalValue = linkToValue.get(link);
                    double increment = routeTable[source][target].getDistance() * transit[source][target];
                    linkToValue.put(link, originalValue + increment);
                    if (valueToLink.containsKey(originalValue)) {
                        // do not update the value of the bridges, since their values are accurate, updating is double counting
                        // continue;
                        valueToLink.remove(originalValue);
                        // System.out.println(link);
                        // System.out.println("###################value updated!!!");
                        
                    }
                    valueToLink.put(originalValue + increment, link);
                }
            }
        }
        // System.out.println("getUncontestableRoute ended");
    }

    private WeightedGraph buildGraph() {
        WeightedGraph g = new WeightedGraph(townLookup.size());
        for (int i = 0; i < townLookup.size(); i++) {
            g.setLabel(townLookup.get(i));
        }

        for (int i = 0; i < infra.size(); i++) {
            for (int j = 0; j < infra.get(i).size(); j++) {
                int source = i;
                int target = infra.get(i).get(j);
                // g.addEdge(source, target, transit[source][target]);
                double distance = calcEuclideanDistance(geo.get(source), geo.get(target));
                g.addEdge(source, target, distance);
            }
        }

        return g;
    }

    private void initLinkTable() {
        linkToID = new HashMap<Link, Integer>();
        idToLink = new HashMap<Integer, Link>();
        idToLinkInfo = new HashMap<Integer, LinkInfo>();
        for (BidInfo binfo: allBids) {
            int i = townIDLookup.get(binfo.town1);
            int j = townIDLookup.get(binfo.town2);
            linkToID.put(new Link(i, j), binfo.id);
            idToLink.put(binfo.id, new Link(i, j));
            idToLinkInfo.put(binfo.id, new LinkInfo(new Link(i, j), binfo.amount, binfo.owner));
            // System.out.println("owner: " + binfo.owner);
        }
        // System.out.println("linkTable finished");
    }

    private double calcEuclideanDistance(Coordinates a, Coordinates b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    private List<List<Integer>> getLinks(WeightedGraph g, int source, int target) {
        int[][] prev = Dijkstra.dijkstra(g, source);
        return Dijkstra.getPaths(g, prev, target);
    }

    private RouteValue getRouteValue(int source, int target) {
        List<List<Integer>> links = getLinks(graph, source, target);
        int distance = 0;
        for (int i = 0; i < links.get(0).size() - 1; i++) {
            distance += graph.getWeight(links.get(0).get(i), links.get(0).get(i + 1));
        }
        double volumePerKm = (double) transit[source][target] / distance;
        // shortestPaths[source][target] = distance;
        routeTable[source][target] = new RouteValue(links, volumePerKm, distance);
        routeTable[target][source] = new RouteValue(links, volumePerKm, distance);
        return this.new RouteValue(links, volumePerKm, distance);
    }

    private void gatherAllVolumePerKm() {
        // double max = 0;
        rankedRouteValue = new ArrayList<RouteValue>();
        // List<List<Integer>> maxLinks = new ArrayList<List<Integer>>();
        for (int i = 0; i < transit.length; i++) {
            for (int j = i + 1; j < transit[i].length; j++) {
                rankedRouteValue.add(getRouteValue(i, j));
            }
        }
        Collections.sort(rankedRouteValue, Collections.reverseOrder());
    }

    public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
        // The random player bids only once in a round.
        // This checks whether we are in the same round.
        // Random player doesn't care about bids made by other players.
        // this.allBids = allBids; 

        // System.out.println(routeLinks.size());
        updateAvailableLinks(lastRoundMaxBid);
        int linkID = -1;
        double value = 0;
        if (valueToLink.size() == 0) {
            // System.out.println("no bridge");
            return getRandomBid(currentBids, allBids, lastRoundMaxBid);
        }
        for (Map.Entry<Double, Link> entry: valueToLink.entrySet()) {
            Link nextBridge = entry.getValue();
            if (linkToID.containsKey(nextBridge)) {
                linkID = linkToID.get(nextBridge);
                value = entry.getKey();
                // !!!!!!!!!!!!!!!!!!!!potentially dangerous, since we don't know if our bid is going to be the awarded
                // valueToLink.remove(value);
                break;
            }
        }

        if (linkID == -1) {
            return getRandomBid(currentBids, allBids, lastRoundMaxBid);
        }

        double max = 0.;
        String bidder = "";
        for (Bid bid: currentBids) {
            Link link = idToLink.get(bid.id1);
            double distance = graph.getWeight(link.town1, link.town2);
            if (bid.amount / distance > max) {
                max = bid.amount / distance;
                bidder = bid.bidder;
            }

            if (bid.id2 == -1) {
                continue;
            }
            link = idToLink.get(bid.id2);
            distance = graph.getWeight(link.town1, link.town2);
            if (bid.amount / distance > max) {
                max = bid.amount / distance;
                bidder = bid.bidder;
            }
        }

        Link bridge = idToLink.get(linkID);
        double distance = bridge.getDistance();
        // System.out.println("max bidder: " + bidder);
        if (bidder.equals("g7") || max * distance > value * 5) {
            // System.out.println("we are the highest price already, or the current price is too high");
            return null;
        }

        Bid ourBid = new Bid();
        ourBid.id1 = linkID;
        LinkInfo li = idToLinkInfo.get(linkID);
        if (li.getAmount() - 0.005 > max * distance) {
            if (budget < li.getAmount()) {
                // System.out.println("budget smaller than minimum bid, budget: " + budget + ", min: " + li.getAmount());
                return null;
            }
            ourBid.amount = li.getAmount();
            // System.out.println("we bid the minimum amount: " + li.getAmount());
        }
        else {
            if (budget < max * distance + 10000) {
                // System.out.println("budget smaller than max * distance + 10000, budget: " + budget + ", bid required: " + Double.toString(max * distance + 10000));
                return null;
            }
            ourBid.amount = max * distance + 10000 + 0.005;
            // System.out.println("we increased the max bid by 10000: " + ourBid.amount);
        }

        return ourBid;
    }

    private Bid getRandomBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
        if (availableBids.size() != 0) {
            return null;
        }

        for (BidInfo bi : allBids) {
            if (bi.owner == null) {
                availableBids.add(bi);
            }
        }

        if (availableBids.size() == 0) {
            return null;
        }

        BidInfo randomBid = availableBids.get(rand.nextInt(availableBids.size()));
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
                else {
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

    private void updateAvailableLinks(Bid lastRoundMaxBid) {
        if (lastRoundMaxBid == null) {
            return;
        }

        Link lastAwardedLink = idToLink.get(lastRoundMaxBid.id1);
        // System.out.println(lastAwardedLink);
        linkToID.remove(lastAwardedLink);
        double value = linkToValue.get(lastAwardedLink);
        if (valueToLink.containsKey(value)) {
            valueToLink.remove(value);
            // System.out.println(lastAwardedLink.toString() + ", has been removed from valueToLink");
        }
        if (lastRoundMaxBid.id2 == -1) {
            return;
        }
        lastAwardedLink = idToLink.get(lastRoundMaxBid.id2);
        linkToID.remove(lastAwardedLink);
        value = linkToValue.get(lastAwardedLink);
        valueToLink.remove(value);

        if (lastRoundMaxBid.bidder.equals("g7")) {
            budget -= lastRoundMaxBid.amount;
        }
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

        @Override
        public int hashCode() {
            String s = "";
            if (town1 < town2) {
                s = Integer.toString(town1) + Integer.toString(town2);
                
            }
            else {
                s = Integer.toString(town2) + Integer.toString(town1);
            }
            return s.hashCode();
        }

        @Override
        public String toString() {
            return new String("This link is from " + town1 + " to " + town2);
        }
    }

    private class Link {
        private int town1;
        private int town2;

        public Link(int id1, int id2){
            if (id1 < id2) {
                town1 = id1;
                town2 = id2;
            }
            else {
                town1 = id2;
                town2 = id1;
            }
        }

        public double getDistance() {
            return graph.getWeight(town1, town2);
        }

        @Override
        public boolean equals(Object o) { 
  
            // If the object is compared with itself then return true   
            if (o == this) { 
                return true; 
            } 
  
            /* Check if o is an instance of Complex or not 
            "null instanceof [type]" also returns false */
            if (!(o instanceof Link)) { 
                return false; 
            } 
          
            // typecast o to Complex so that we can compare data members  
            Link lv = (Link) o; 
          
            // Compare the data members and return accordingly  
            return (town1 == lv.town1 && town2 == lv.town2) || (town1 == lv.town2 && town2 == lv.town1); 
        }

        @Override
        public int hashCode() {
            String s = "";
            if (town1 < town2) {
                s = Integer.toString(town1) + Integer.toString(town2);
                
            }
            else {
                s = Integer.toString(town2) + Integer.toString(town1);
            }
            return s.hashCode();
        }

        @Override
        public String toString() {
            return new String("This link is from " + town1 + " to " + town2);
        }
    }

    private class LinkInfo {
        private Link link;
        private double amount;
        private String owner;

        public LinkInfo(Link l, double a, String o){
            link = new Link(l.town1, l.town2);
            amount = a;
            owner = o;
        }

        public Link getLink() {
            return new Link(link.town1, link.town2);
        }

        public void setAmount(double a) {
            amount = a;
        }

        public double getAmount() {
            return amount;
        }

        public void setOwner(String o) {
            owner = o;
        }

        public String getOwner() {
            return owner;
        }

        @Override
        public boolean equals(Object o) { 
  
            // If the object is compared with itself then return true   
            if (o == this) { 
                return true; 
            } 
  
            /* Check if o is an instance of Complex or not 
            "null instanceof [type]" also returns false */
            if (!(o instanceof LinkInfo)) { 
                return false; 
            } 
          
            // typecast o to Complex so that we can compare data members  
            LinkInfo lv = (LinkInfo) o; 
          
            // Compare the data members and return accordingly  
            return link.equals(lv.link) && amount == lv.amount && owner.equals(lv.owner); 
        }

        @Override
        public int hashCode() {
            String s = link.toString() + Double.toString(amount) + owner;
            return s.hashCode();
        }

        @Override
        public String toString() {
            return link.toString() + ", the amount is: " + Double.toString(amount) + ", and the owner is: " + owner;
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

    private class BridgeThread implements Runnable {
        private Thread t;
        private String name;

        BridgeThread(String name) {
            this.name = name;
        }

        public void run() {
            rankedRouteValue = new ArrayList<RouteValue>();
            gatherAllVolumePerKm();
            buildBridgeMap();
            getUncontestableRoute();
        }

        public void start () {
            if (t == null) {
                t = new Thread (this, name);
                t.start ();
            }
        }

        // private List<List<Integer>> findBridges() {
        //     List<List<Integer>> bridges = new ArrayList<List<Integer>>();
        //     for (int i = 0; i < infra.size(); i++) {
        //         for (int j = 0; j < infra.get(i).size(); j++) {
        //             int source = i;
        //             int target = infra.get(i).get(j);
        //             double weight = graph.getWeight(source, target);
        //             graph.removeEdge(source, target);
        //             boolean bridgeFound = false;
        //         // System.out.println("source: " + source + ", target: " + target);
        //             for (int s = 0; s < townLookup.size(); s++) {
        //                 int[][] prev = Dijkstra.dijkstra(graph, s);
        //                 for (int t = s + 1; t < prev.length; t++) {
        //                     if (prev[t][0] == 0) {
        //                         bridgeFound = true;
        //                     // System.out.println("s: " + s + ", t: " + t);
        //                         break;
        //                     }
        //                 }

        //                 if (bridgeFound) {
        //                     break;
        //                 }
        //             }

        //             if (bridgeFound) {
        //                 List<Integer> bridge = new ArrayList<Integer>();
        //                 bridge.add(source);
        //                 bridge.add(target);
        //                 bridges.add(bridge);
        //             }

        //             graph.addEdge(source, target, weight);
        //         }
        //     }

        //     System.out.println("find bridges ended");
        //     return bridges; 
        // }

        private void buildBridgeMap() {
            // System.out.println(Thread.currentThread().getId());
            // System.out.println(ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId()));
            // System.out.println("buildBridgeMap started");
            // valueToBridge = new TreeMap<Double, Link>(Collections.reverseOrder());
            for (int i = 0; i < infra.size(); i++) {
                for (int j = 0; j < infra.get(i).size(); j++) {
                    int source = i;
                    int target = infra.get(i).get(j);
                    double weight = graph.getWeight(source, target);
                    graph.removeEdge(source, target);
                    graph.removeEdge(target, source);
                    Link lv = new Link(source, target);
                    double value = 0;
                    List<List<Integer>> nodes = new ArrayList<List<Integer>>();
                    for (int s = 0; s < townLookup.size(); s++) {
                        int[][] prev = Dijkstra.dijkstra(graph, s);
                        for (int t = s + 1; t < prev.length; t++) {
                            if (prev[t][0] != 0) {
                                continue;
                            }

                            List<Integer> pair = new ArrayList<Integer>();
                            pair.add(s);
                            pair.add(t);
                            nodes.add(pair);
                            // value += shortestPaths[s][t] * transit[s][t];
                            value += routeTable[s][t].getDistance() * transit[s][t];
                        }
                    }

                    graph.addEdge(source, target, weight);
                    if (nodes.size() == 0) {
                        continue;
                    }

                    if (nodes.get(0).get(0) == nodes.get(nodes.size() / 2).get(0) && nodes.get(0).get(1) == nodes.get(nodes.size() / 2).get(1)) {
                        value /= 2;
                    }


                    if (bridges.contains(lv)) {
                        continue;
                    }
                    // valueToBridge.put(value, lv);
                    linkToValue.put(lv, value);
                    valueToLink.put(value, lv);
                    bridges.add(lv);
                    // System.out.println(ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId()));
                }
            }
            // System.out.println("buildBridgeMap ended");
            // System.out.println(ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId()));
        }
    }

}