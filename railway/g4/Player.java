package railway.g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.lang.Math;
// To access data classes.
import railway.sim.utils.*;

public class Player implements railway.sim.Player 
{

    private double budget;

    private List<BidInfo> availableBids = new ArrayList<>();
    
    // use maps to store each player's budget and links
    private HashMap<String, String> allPlayerLinks = new HashMap<>();
    
    private List<Coordinates> geo;
    private List<String> townLookup;
    private List<List<Integer>> infra;
    private int [][] transit;
    private int total_profit = 0; 
    private int total_budget = 0;
    private String name;
    private static int NUM_STATIONS;
    private ArrayList<Integer>[] adjList; 
    private HashMap<Integer, Coordinates> station_to_coordinates = new HashMap<Integer, Coordinates>();
    
    // For use with paths
    private ArrayList<Integer> pathList = new ArrayList<>(); 
    
    private HashMap<Integer, String> ownership = new HashMap<Integer, String>();
    private HashMap<Integer, List<Path> > paths = new HashMap<Integer, List<Path> >();
    private HashMap<Integer, Integer> flow = new HashMap<Integer, Integer>();
    public Player() 
    {
    }

    public void init(
        String name,
        double budget,
        List<Coordinates> geo,
        List<List<Integer>> infra,
        int[][] transit,
        List<String> townLookup,
        List<BidInfo> allBids) 
    {
        this.name = name;
        this.townLookup = townLookup;
        this.geo = geo;
        this.budget = budget;
        this.infra = infra;
        this.transit = transit;


        this.NUM_STATIONS = geo.size();
        this.geo = geo;
        this.infra = infra;
        this.transit = transit;
        // Build a Naive List Containing all paths.
        // 1- Use DFS
        
        // With this, get the distances!
        
        // Sum up all transit links of path. Print Profit!
        
        // geography - Maps Station -. (x, y)
        // tranist maps (Station 1 -> Station 2, traffic)
        
        // Init Adjacency List
        adjList = new ArrayList[NUM_STATIONS];
        System.out.println("Number of Stations: " + NUM_STATIONS);
        for(int i = 0; i < NUM_STATIONS;i++)
        {
            adjList[i] = new ArrayList<>();
        }

        
        for (int i=0; i<infra.size(); i++){
            for (int j=0; j<infra.get(i).size();j++){
                adjList[i].add(infra.get(i).get(j));
                adjList[infra.get(i).get(j)].add(i);
            }
        }

        update_flow(); 
    }


    public void update_flow(){
        total_profit = 0;
        flow = new HashMap<Integer, Integer>();
        for (int i = 0; i< transit.length; i++){
            for (int j=0; j<transit[i].length; j++){
                if (transit[i][j]!=0){
                    findAllPaths(i,j);
                }
            }
        }

        for (int i = 0; i< transit.length; i++){
            for (int j=0; j<transit[i].length; j++){
                if (transit[i][j]!=0){
                    for (Path p : paths.get(hash(i,j))){
                        int prev = -1;
                        // System.out.printf("For Flow from %d to %d :%d\n",i,j,transit[i][j]);
                        for (int node: p.path){
                            if (prev != -1){
                                if (flow.containsKey(hash(prev, node))){
                                //    System.out.printf("Adding Flow from %d to %d : %d\n",prev,node, flow.get(hash(prev, node)) + transit[i][j]/paths.get(hash(i,j)).size());
                                    flow.put(hash(prev,node), flow.get(hash(prev, node)) + transit[i][j]/paths.get(hash(i,j)).size());
                                }
                                else{
                                //   System.out.printf("Final Flow from %d to %d : %d\n",prev,node, transit[i][j]/paths.get(hash(i,j)).size());
                                    flow.put(hash(prev, node), transit[i][j]/paths.get(hash(i,j)).size());
                                }
                            }
                            prev = node;
                        }
                    }
                }
            }
        }

    }

    // this function hashes a link to a unique integer
    private int hash(int from, int to){
        int temp = 0;
        if (from > to){
            temp = from;
            from = to;
            to = temp;
        }
        return from*2*NUM_STATIONS+to;
    }

    // this function hashes a link to a unique integer
    private int hash(String from, String to){
        int f = townLookup.indexOf(from);
        int t = townLookup.indexOf(to);
        if (f==-1 || t==-1){
            System.out.println("city not found");
        }
        return hash(f,t);
    }

    private double getDistance(Coordinates a, Coordinates b)
    {
        return Math.pow(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2), 0.5);
    }


    private double getDistance(int a, int b)
    {
        return Math.pow(Math.pow(geo.get(a).x - geo.get(b).x, 2) + Math.pow(geo.get(a).y - geo.get(b).y, 2), 0.5);
    }

    private double[] getDistance(List<Integer> lst)
    {
        int prev = lst.get(0);
        String prev_owner = "";
        double min_dist = 0.0;
        double max_dist = 0.0;
        for (int i=1; i<lst.size();i++){
            min_dist += getDistance(prev, lst.get(i));
            if (ownership.containsKey(hash(prev,i))){
                if (prev_owner.equals(ownership.get(hash(prev,i))) == false){
                    max_dist += 200;
                }
                prev_owner = ownership.get(hash(prev,i));
            }
            else{
                max_dist+=200;
            }
            prev = lst.get(i);
        }
        double[] temp = new double[2];
        temp[0]=max_dist;
        temp[1]=min_dist;
        return temp;
    }

    public Bid getBid(List<Bid> currentBids, List<BidInfo> availableBids, Bid lastRoundMaxBid) 
    {
        List<BidInfo> avail_Bids = new ArrayList<BidInfo>();
        List<Integer> amounts = new ArrayList<Integer>();

        // this update the flow every iteration
        update_flow();

        for (int i=0; i<availableBids.size();i++)
        {
            BidInfo bi = availableBids.get(i);
            if (bi.owner == null) 
            {
                avail_Bids.add(bi);
                amounts.add((int)(bi.amount+10000));
                // 
                int from = townLookup.indexOf(bi.town1);
                int to = townLookup.indexOf(bi.town2);     
                total_profit += flow.get(hash(from, to))*20*getDistance(from,to) - bi.amount-10000;
            }
            else
            {
                ownership.put(hash(bi.town1,bi.town2),bi.owner);
                amounts.add(-1);
            }
        }

        if (avail_Bids.size() == 0) 
        {
            return null;
        }
        
        double maxb = 0;
        Coordinates s = new Coordinates(0,0);
        Coordinates d = new Coordinates(0,0);
        String maxbn = "";
        // Check if another player has made a bid for this link.
        for (int i=0; i<currentBids.size();i++)
        {
            Bid b = currentBids.get(i);

            if (b.id2 == -1)
            {

                if (townLookup.indexOf(availableBids.get(b.id1).town1) == -1 || townLookup.indexOf(availableBids.get(b.id1).town2) == -1)
                {
                   System.out.println("Town not found");
                   continue;
                }
                s = geo.get(townLookup.indexOf(availableBids.get(b.id1).town1));
                d = geo.get(townLookup.indexOf(availableBids.get(b.id1).town2));

                double dist = getDistance(s,d);

                if (b.amount/dist > maxb)
                {
                   maxb = b.amount / dist;
                   maxbn = b.bidder;
                }
                if ((int)b.amount + 10000 > amounts.get(b.id1))
                {
                    amounts.set(b.id1,(int)b.amount + 10000);
                }
            }
            else
            {
                if (townLookup.indexOf(availableBids.get(b.id1).town1) == -1 || townLookup.indexOf(availableBids.get(b.id1).town2)==-1 || townLookup.indexOf(availableBids.get(b.id2).town1)==-1 || townLookup.indexOf(availableBids.get(b.id1).town2) == -1){
                   System.out.println("Town not found");
                   continue;
                }
                s = geo.get(townLookup.indexOf(availableBids.get(b.id1).town1));
                Coordinates t = geo.get(townLookup.indexOf(availableBids.get(b.id1).town2));
                Coordinates k = geo.get(townLookup.indexOf(availableBids.get(b.id2).town1));
                d = geo.get(townLookup.indexOf(availableBids.get(b.id2).town2));
                double dist1 = getDistance(s,t);
                double dist2 = getDistance(k,d);
                
                double dist = dist1+dist2;
                if (b.amount/dist > maxb)
                {
                   maxb = b.amount / dist;
                   maxbn = b.bidder;
                }

                if ((int)(dist1/dist*b.amount + 10000) > amounts.get(b.id1))
                {
                    amounts.set(b.id1,(int)(dist1/dist*b.amount) + 10000);
                }
                if ((int)(dist2/dist*b.amount + 10000) > amounts.get(b.id2))
                {
                    amounts.set(b.id2,(int)(dist2/dist*b.amount) + 10000);
                }
            }
        }
        
        if (maxbn.equals(name))
        {
            return null;
        }

        double profit = 0;
        Bid b = new Bid();
        
        for (int i=0; i<availableBids.size();i++)
        {
            BidInfo bi = availableBids.get(i);

            if (townLookup.indexOf(bi.town1) == -1 || townLookup.indexOf(bi.town2) == -1)
            {
               System.out.println("Town not found");
               continue;
            }
            int from = townLookup.indexOf(bi.town1);
            int to = townLookup.indexOf(bi.town2);
            if (amounts.get(i)==-1)
            {
                continue;
            }
            double win_bid = (double)(maxb*getDistance(geo.get(from),geo.get(to))+1);
            double winning_price = Math.max(amounts.get(i),win_bid);
            double revenue = 0;
            if (flow.containsKey(hash(from, to))){
                revenue = flow.get(hash(from, to))*20*getDistance(from,to);
            }
            /*
            System.out.printf("revenue for %s to %s :%f\n",bi.town1, bi.town2, revenue);
            System.out.printf("price : %f\n",winning_price);
            System.out.printf("Profit : %f\n",revenue-winning_price);
            System.out.printf("total profit left this round: %d\n",total_profit);
            */
            
            if (winning_price < revenue)
            {
                if (profit < revenue - winning_price)
                {
                    if (winning_price > budget)
                    {
                        continue;
                    }
                    if (stop_criterian(winning_price, revenue,total_profit)==0)
                    {
                        continue;
                    }
                    b.id1 = i;
                    b.amount = winning_price;
                    profit = revenue - winning_price;
                    //System.out.println(bi.town1+ " to "+bi.town2 + " : "+String.valueOf(profit));
                }
                if (winning_price+b.amount > budget)
                {
                    continue;
                }
                if (stop_criterian(winning_price, revenue, total_profit)==0)
                {
                    continue;
                }
                if (b.id1 == -1 || b.id1 == i)
                {
                    continue;
                }
                if (bi.town1 == availableBids.get(b.id1).town1 || bi.town1 == availableBids.get(b.id1).town2 || bi.town2 == availableBids.get(b.id1).town1 || bi.town2 == availableBids.get(b.id1).town2){
                    b.id2 = i;
                    b.amount += winning_price;
                    profit += revenue - winning_price;
                }
            }    
        }
        if (b.id1 == -1)
        {
            //System.out.println("Not worth it");
            //System.out.println(this.budget);
            
            return null;
        }
        return b;
    }


    public void updateBudget(Bid bid) 
    {
        if (bid != null) 
        {
            budget -= bid.amount;
        }
        availableBids = new ArrayList<>();
    }
    
    // determine if we want to keep bidding, 0 if not, 1 if yes
    public int stop_criterian(double cost, double revenue, double total_profit)
    {
        if (8*(revenue - cost)*this.total_budget < cost*total_profit)
        {
            return 0;
        }
        return 1;
    }
    /*
     *  Method, get cost of a whole link:
     *  Example, if you have A -> B -> C
     *  A -> B has profit of $10,000
     *  A -> C has profit of $10,000
     *  B -> C has profot of $10,000
     *  Profit(A, B) = 10,000
     *  Profit(A, C) = 30,000
     *  
     *  Input: Two train stations.
     */
    
    // Using the Path Link and Transit Adjacency List, Get the Profit!
    public int profit_of_links()
    {
        int profit = 0;
        // [0, 1, 2, 5]
        // get profit of (0, 1) + (1, 2) + (2, 5)
        for(int i = 0; i < pathList.size(); i++)
        {
            profit += transit[i][i+1];
        }
        return profit;
    }
    
    


    public void findAllPaths(int s, int d){
        List<Integer> nodes = new ArrayList<Integer>();
        List<Double> dist = new ArrayList<Double>();
        List<Integer> isVisited = new ArrayList<Integer>();
        HashMap<Integer, Integer > parents = new HashMap<Integer, Integer>();

        nodes.add(s);
        dist.add(0.0);
        double min_dist = Double.POSITIVE_INFINITY;
        while (nodes.size()!=0){
            int cur = nodes.remove(0);
            double td = dist.remove(0);
            if (isVisited.contains(cur)){
                continue;
            }
            if (td > min_dist){
                continue;
            }
            if (d == cur){
        
                List<Integer> route = new ArrayList<Integer>();
                int node = cur;
                while (node != s){
                    route.add(node);
                    node = parents.get(node);
                }
                route.add(node);
                double max_dist = td + 200*(route.size()-2);
                if (paths.containsKey(hash(s, d))){
                    paths.get(hash(s, d)).add(new Path(s, d, route, max_dist, td));
                }
                else{
                    List<Path> lst = new ArrayList<Path>();
                    lst.add(new Path(s, d, route, max_dist, td));
                    paths.put(hash(s, d),lst);
                }
                min_dist = Math.min(td,min_dist);
            }
            for (Integer i:adjList[cur]){
                if (!parents.containsKey(i)){
                    parents.put(i,cur);
                }
                nodes.add(i);
                
                dist.add(td+getDistance(cur,i));
            }

            isVisited.add(cur);

            for (int i=0;i<nodes.size();i++){
                for (int j=i; j<nodes.size();j++){
                    if (dist.get(i)>dist.get(j)){
                        int temp = nodes.get(i);
                        nodes.set(i,nodes.get(j));
                        nodes.set(j,temp);
                        double dis = dist.get(i);
                        dist.set(i,dist.get(j));
                        dist.set(j,dis);   
                    }
                }
            }
        }
    }
    
    /*
     *  Purpose: Find your starting point
     *  
     *  If graph has tail find a pair such that 
     *  degree of 1 is one station and highest degree in other station.
     */
    public void start()
    {
    	// Look for a station with degree 1.
    	Integer tail = null;
    	// If you find it, Great! Find a good neighbor with high order degree and spawn from there
    	if(tail == null)
    	{
    		
    	}
    	// Otherwise, you are in a checkerboard like map most likely so tails are liability!
    	else
    	{
    		
    	}
    }
}
