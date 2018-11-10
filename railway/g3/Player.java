package railway.g3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import railway.sim.Simulator;
import java.util.HashSet;

// To access data classes.
import railway.sim.utils.*;

// Thanks Sidhi!
class LinkInfo {
    public int town1;
    public int town2;

    public double distance;
}

public class Player implements railway.sim.Player {
    // Random seed of 42.
    //private int seed = 42;
    //private Random rand;

    private double budget; // amount to spend at auction

    private List<Coordinates> geo; // city locations
    private ArrayList<String> townLookup; // city names
    private List<List<Integer>> infra; // graph edges
    private int[][] transit; // travel along paths

    private double[][] revenue; // price to travel along paths
    private int[] connections; // number of connections for links

    private boolean[][] ownership; // which links we own
    private List<G3Bid> availableBids = new ArrayList<G3Bid>();

    private List<BidInfo> allBids;
    private Bid maxBid;
    private G3Bid best;
    private int bidsThisRound;

    public Player() {
        //rand = new Random();
    }

    // Initialization function.
    // name: name of current player.
    // budget: maximum amount allocated for bidding.
    // geo: Town indices and their coordinates.
    // infra: Town indices and other towns they are connected to.
    // transit: Town indices and the number of passengers between them.
    // townLookup: Town indices and names of towns.
    // allBids: list of all available bids and their minimum amount.
    public void init(
        String name,
        double budget,
        List<Coordinates> geo,
        List<List<Integer>> infra,
        int[][] transit,
        List<String> townLookup,
        List<BidInfo> allBids) {

        this.budget = budget;
        this.townLookup = (ArrayList)townLookup;
        this.infra = infra;
        this.geo = geo;
        this.transit = transit;
        this.revenue = getRevenue();
        this.connections = getConnections();
        this.bidsThisRound = 0;

        // create all single link bids
        for(BidInfo bi: allBids) {
        	int town_id1 = townLookup.indexOf(bi.town1);
        	int town_id2 = townLookup.indexOf(bi.town2);
        	G3Bid bid = new G3Bid(town_id1, town_id2, bi.id, bi.amount);
        	availableBids.add(bid);
        }

        // create all double link bids
        List<G3Bid> pairs = new ArrayList<G3Bid>();
        for(int i = 0; i < availableBids.size(); ++i) {
        	G3Bid b1 = availableBids.get(i);
        	for(int j = i+1; j < availableBids.size(); ++j) {
        		G3Bid b2 = availableBids.get(j);

        		if(isPair(b1, b2)) {
        			try {
        				G3Bid pair = new G3Bid(b1, b2);
        				pairs.add(pair);
        			} catch(Exception e) {
        				System.err.println(e);
        			}
        		}
        	}
        }
        availableBids.addAll(pairs);

        // for (double[] row: revenue) {
        // 	for (double d: row) {
        // 		System.err.print(d + " ");
        // 	}
        // 	System.err.println();
        // }

        /*System.out.println("Bids in availableBids:");
        for (G3Bid bid : availableBids) {
            printBid(bid);
        }
        System.out.println("number of links left = " + availableBids.size());*/

        // System.err.println("bids in availableBids");
        // for (G3Bid b : availableBids) {
        //     printBid(b);
        // }

        // System.err.println("testing error in number of bids");
        // for (G3Bid b : availableBids) {
        //     int t_1 = b.town_id1;
        //     int t_2 = b.town_id2;
        //     if (b.town_id3 > -1) {
        //         int t_3 = b.town_id3;
        //         if (isTricycle(t_1, t_2, t_3)) {
        //             System.out.println("CYCLE!!! : " + townLookup.get(t_1) + ", " + townLookup.get(t_2) + ", " + townLookup.get(t_3));
        //         }
        //     }
        // }
    }

    private boolean isPair(G3Bid b1, G3Bid b2) {
    	// false if one bid already represents a pair
    	if(b1.town_id3 > -1 || b2.town_id3 > -1) {
    		return false;
    	}

    	// check for exactly one town in common
    	int t1a, t1b, t2a, t2b;
  		t1a = b1.town_id1;
  		t1b = b1.town_id2;
  		t2a = b2.town_id1;
  		t2b = b2.town_id2;
    	return ((t1a==t2a) || (t1a==t2b) || (t1b==t2a) || (t1b==t2b))
    		&& (t1a!=t2a || t1b!=t2b) && (t1a!=t2b || t1b!=t2a);
    }

    // The bid placed for a round.
    // Returns null if they don't want to place a bid.
    // currentBids: bids being placed in this round in reverse order -
    //   most recent bid is first.
    // allBids: shows those bids available and bids owned by other players
    //   i.e. the results of previous rounds.
    public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
        this.allBids = allBids;

        // update possible bids according to max bids
        for (int i = 0; i < currentBids.size()-bidsThisRound; ++i) {
        	Bid b1 = currentBids.get(i);
        	for (int j = 0; j < availableBids.size(); ++j) {
        		Bid b2 = availableBids.get(j);
        		if(b1.id1==b2.id1 && b1.id2==b2.id2 || b1.id1==b2.id2 && b1.id2==b2.id1) {
        			if(b1.amount + 10000 > b2.amount) {
        				b2.amount = b1.amount + 10000;
        			}
        			break;
        		}
        	}
        }
        bidsThisRound = currentBids.size();

    	// get the price-per-distance of the maximum bid so far in this round
    	double maxBidAmt;
    	if (currentBids.size() > 0) {
    		maxBid = Simulator.getMaxBid(currentBids, allBids);
    		maxBidAmt = maxBid.amount/Simulator.getDistance(maxBid);
    		//System.err.println("max bid: " + maxBid.id1 + ", " + maxBid.id2 + " -- amount = " + maxBidAmt);
    	} else {
    		maxBid = null;
    		maxBidAmt = 0;
    	}

    	if(maxBid==null) {
    		// evaluate bid scores
    		for(int i = 0; i < availableBids.size(); ++i) {
				G3Bid cur = availableBids.get(i);
				evaluateBid(cur);
				//printBid(cur);
			}

    		// sort by score and return the best we can afford
    		G3Bid ret = getBestAffordableBid(availableBids);
    		if(ret!=null && ret.score >= 0) {
    			best = ret;
    			return ret;
    		}
    		return null;
    	}

    	if(maxBid.bidder.equals("g3")) {
    		// already own the winning bid
    		return null;
    	}

    	// past here means another player has the winning bid right now...

		// update all the possible bids to cost what is needed to secure them this round
		for(int i = 0; i < availableBids.size(); ++i) {
			G3Bid cur = availableBids.get(i);
			updateBid(cur, maxBid, maxBidAmt);
			//printBid(cur);
		}

		// evaluate the bid scores
		for(int i = 0; i < availableBids.size(); ++i) {
			G3Bid cur = availableBids.get(i);
			evaluateBid(cur);
			//printBid(cur);
		}

		// sort by score and return the best we can afford
		G3Bid ret = getBestAffordableBid(availableBids);
		if(ret!=null && ret.score >= 0) {
			best = ret;
			return ret;
		}
		return null;
    }

    private void updateBid(G3Bid bid, Bid highest, double maxBidAmt) {
    	double a = bid.amount;
    	double dist = Simulator.getDistance(bid);
    	double b = Math.floor(1.0 + dist*maxBidAmt);
    	bid.amount = Math.max(a,b);
    }

    private void evaluateBid(G3Bid bid) {
    	// really basic right now -- just computes profit from direct traffic
    	double revenue = 0;
    	if (bid.id2 == -1) {
    		// single link
    		revenue += this.revenue[bid.town_id1][bid.town_id2];
    		revenue += this.revenue[bid.town_id2][bid.town_id1];
    	} else {
    		// pair link
    		revenue += this.revenue[bid.town_id1][bid.town_id2];
    		revenue += this.revenue[bid.town_id2][bid.town_id1];
    		revenue += this.revenue[bid.town_id2][bid.town_id3];
    		revenue += this.revenue[bid.town_id3][bid.town_id2];
    		revenue += this.revenue[bid.town_id1][bid.town_id3];
    		revenue += this.revenue[bid.town_id3][bid.town_id1];
    	}

    	bid.score = revenue - bid.amount;
    }

    private G3Bid getBestAffordableBid(List<G3Bid> availableBids) {
    	Collections.sort(availableBids);
    	for(G3Bid bid: availableBids) {
    		if(bid.amount < this.budget) {
    			return bid;
    		}
    	}
    	return null; // can't afford any
    }

    // Thanks Sidhi!
    private double[][] getRevenue() {
        final int n = geo.size();

        // Create the graph.
        WeightedGraph g = new WeightedGraph(n);

        for (int i=0; i<n; ++i) {
            g.setLabel(townLookup.get(i));
        }

        for (int i=0; i<infra.size(); ++i) {
            for (int j=0; j<infra.get(i).size(); ++j) {
                g.addEdge(i, infra.get(i).get(j), getDistance(i, infra.get(i).get(j)));
            }
        }

        //g.print();

        // Compute the revenue between any two nodes.
        double[][] revenue = new double[n][n];

        for (int i=0; i<n; ++i) {
            int[][] prev = Dijkstra.dijkstra(g, i);
            for (int j=i+1; j<n; ++j) {
                List<List<Integer>> allPaths = Dijkstra.getPaths(g, prev, j);

                double cost = 0;
                for (int k=0; k<allPaths.get(0).size()-1; ++k) {
                    cost += getDistance(allPaths.get(0).get(k), allPaths.get(0).get(k+1));
                }

                revenue[i][j] = cost * transit[i][j] * 10;
            }
        }

        return revenue;
    }

    // Thanks Sidhi!
    private double getDistance(int t1, int t2) {
        return Math.pow(
            Math.pow(geo.get(t1).x - geo.get(t2).x, 2) +
                Math.pow(geo.get(t1).y - geo.get(t2).y, 2),
            0.5);
    }

    private int[] getConnections() {
        int[] connections = new int[geo.size()];
        for(int i = 0; i < connections.length; ++i) {
            List<Integer> row = infra.get(i);
            connections[i] += row.size();
            for(int j = 0; j < row.size(); ++j) {
                ++connections[row.get(j)];
            }
        }

        return connections;
    }

    private void printBid(G3Bid b) {
    	int t_1 = b.town_id1;
        int t_2 = b.town_id2;
        if (b.town_id3 > -1) {
            int t_3 = b.town_id3;
            System.err.println(b.id1 + "," + b.id2 + ": " + townLookup.get(t_1) + " to " + townLookup.get(t_2) + " to " + townLookup.get(t_3) + " for " + b.amount);
        } else {
            System.err.println(b.id1 + "," + b.id2 + ": " + townLookup.get(t_1) + " to " + townLookup.get(t_2) + " for " + b.amount);
        }
    }

    // Indicates to the player whether they have won the previous bid of link/pair of links.
    // A null bid indicates that they did not win their bid.
    public void updateBudget(Bid bid) {

    	availableBids = removeOwned(availableBids, bid);

        if (bid != null) {
            budget -= bid.amount;
        }

        // reset all the bids at the end of the round
        for(int i = 0; i < availableBids.size(); ++i) {
        	G3Bid cur = availableBids.get(i);
        	cur.amount = cur.min_bid;
        }

        bidsThisRound = 0;
    }

    private List<G3Bid> removeOwned(List<G3Bid> available, Bid latest) {
    	List<G3Bid> ret = new ArrayList<G3Bid>(available.size());

    	if(latest != null) {
	    	for (G3Bid bid: available) {
	    		if(!sameLink(bid, latest) && !sameTowns(bid, this.best)) {
	    			ret.add(bid);
	    		}
	    	}
    	} else {
    		for (G3Bid bid: available) {
	    		if(!sameLink(bid, maxBid)) {
	    			ret.add(bid);
	    		}
	    	}
    	}

    	return ret;
    }

    private boolean sameLink(G3Bid bid, Bid owned) {
    	int b1l1, b1l2, b2l1, b2l2;
    	b1l1 = bid.id1;
    	b1l2 = bid.id2;
    	b2l1 = owned.id1;
    	b2l2 = owned.id2;

    	if(b1l1==b2l1 || b1l1==b2l2 || b1l2==b2l1) {
    		return true;
    	}

    	if(b1l2==-1 || b2l2==-1) {
    		return false;
    	}

    	return (b1l2==b2l2);
    }

    private boolean sameTowns(G3Bid bid, G3Bid owned) {
    	int b1t1, b1t2, b1t3, b2t1, b2t2, b2t3;
    	b1t1 = bid.town_id1;
    	b1t2 = bid.town_id2;
    	b1t3 = bid.town_id3;
    	b2t1 = owned.town_id1;
    	b2t2 = owned.town_id2;
    	b2t3 = owned.town_id3;

    	if(b1t1==b2t1 && b1t2==b2t2 || b1t2==b2t1 && b1t2==b2t1) {
    		return true;
    	}

    	if(b1t1==b2t2 && b1t2==b2t3 || b1t1==b2t3 && b1t2==b2t2 ||
    	   b1t2==b2t1 && b1t3==b2t2 || b1t3==b2t1 && b1t2==b2t2) {
    		return true;
    	}

    	if(b1t3==-1 || b2t3==-1) {
    		return false;
    	}

    	return (b1t2==b2t2 && b1t3==b2t3 || b1t3==b2t2 && b1t2==b2t3);
    }
}