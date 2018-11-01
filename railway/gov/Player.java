package railway.gov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// To access data classes.
import railway.sim.utils.*;

public class Player implements railway.sim.Player {

    private double budget;

    private List<BidInfo> availableBids = new ArrayList<>();

    public Player() {
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
    }

    // Gov doesn't ever bid.
    public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids, Bid lastRoundMaxBid) {
        return null;
    }

    // Not applicable for Gov.
    public void updateBudget(Bid bid) {
    }
}
