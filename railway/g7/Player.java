package railway.g7;

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
    private String name;
    private int[][] transit;
    private List<String> townLookup;

    private List<BidInfo> availableBids = new ArrayList<>();

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
        this.transit = transit;
        this.townLookup = townLookup;
        this.name = name;
    }

    public Bid getBid(List<Bid> currentBids, List<BidInfo> allBids) {
        // The random player bids only once in a round.
        // This checks whether we are in the same round.
        // Random player doesn't care about bids made by other players.
        if (availableBids.size() != 0) {
            return null;
        }
        List<BidInfo> ourBids = new ArrayList<>();
        List<String> ourTown = new ArrayList<>();;

        for (BidInfo bi : allBids) {
            if (bi.owner == null) {
                availableBids.add(bi);
            }
            // this is not working for some reason
            else if (bi.owner == name){
                ourTown.add(bi.town1);
                ourTown.add(bi.town2);
            }
        }

        if (availableBids.size() == 0) {
            return null;
        }


        double max = 0;
        double maxOwn = 0;
        BidInfo longBid = null;
        BidInfo ownBid = null;

        // find link with highest transit
        for (BidInfo bInfo : availableBids){
            int townid1 = townLookup.indexOf(bInfo.town1);
            int townid2 = townLookup.indexOf(bInfo.town2);
            if ((ourTown.contains(bInfo.town1) || ourTown.contains(bInfo.town2)) 
                && transit[townid1][townid2] > maxOwn){
                maxOwn = transit[townid1][townid2];
                ownBid = bInfo;
            }
            if (transit[townid1][townid2] > max){
                max = transit[townid1][townid2];
                longBid=bInfo;
            }
        }
        BidInfo randomBid;
        if (ownBid != null){
            randomBid = ownBid;
        }
        else if (longBid != null){
            randomBid = longBid;
        }
        else{
            randomBid = availableBids.get(rand.nextInt(availableBids.size()));
        }
        double amount = randomBid.amount;

        // Don't bid if the random bid turns out to be beyond our budget.
        if (budget - amount < 0.) {
            return null;
        }

        // Check if another player has made a bid for this link.
        for (Bid b : currentBids) {
            if (b.id1 == randomBid.id || b.id2 == randomBid.id) {
                if (budget - b.amount - 10 < 0.) {
                    return null;
                }
                else {
                    amount = b.amount + 10;
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
}
