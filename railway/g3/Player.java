package railway.g3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// To access data classes.
import railway.sim.utils.*;

public class Player implements railway.sim.Player {
    // Random seed of 42.
    private int seed = 42;
    private Random rand;

    private double budget;

    public Player() {
        rand = new Random();
    }
    
    public void init(
        String name,
        double budget,
        List<Coordinates> geo,
        List<List<Integer>> infra,
        int[][] transit,
        List<String> townLookup) {

        this.budget = budget;
    }

    public Bid getBid(List<BidInfo> allBids) {
        List<BidInfo> availableBids = new ArrayList<>();

        for (BidInfo bi : allBids) {
            if (bi.owner == null) {
                availableBids.add(bi);
            }
        }

        if (availableBids.size() == 0) {
            return null;
        }

        BidInfo randomBid = availableBids.get(rand.nextInt(availableBids.size()));

        // Don't bid if the random bid turns out to be beyond our budget.
        if (budget - randomBid.amount <= 0.) {
            return null;
        }

        Bid bid = new Bid();
        bid.amount = randomBid.amount;
        bid.id1 = randomBid.id; 

        return bid;
    }
    
    public void updateBudget(double amount) {
        budget -= amount;
    }
}
