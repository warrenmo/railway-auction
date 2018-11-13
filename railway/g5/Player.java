package railway.g5;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.lang.Math;

// To access data classes.
import railway.sim.utils.*;

public class Player implements railway.sim.Player {
 // Random seed of 42.
 private int seed = 42;
 private Random rand;

 private double budget;
 private double initBudget;

 private List < BidInfo > availableBids = new ArrayList < > ();
 private List < Integer > availableLinks = new ArrayList < > ();
 private Map < Integer, Double > minAmounts = new HashMap < Integer, Double > ();
 private Map < Integer, Double > originalMins = new HashMap < Integer, Double > ();
 private Map < String, Double > playerBudgets = new HashMap < String, Double > ();
 //we are provided last round maxBid value
 private Bid lastWinner = new Bid();
 private boolean firstRound;
 private boolean updatedRoundBudget = false;

 //Variables for choosing link
 private int bestLink;
 private double bestValue;
 private int bestAdj;
 private double bestAdjValue;
 private double railCount = 0;

 private double auctionProgress = 1;
 private List < Integer > ownedRails = new ArrayList < > ();
 private List < String > ownedCities = new ArrayList < > ();
 final static double margin = 0.9;
 private List < Integer > adjacentRails = new ArrayList < > ();
 private Map < Integer, List < String >> railCities = new HashMap < Integer, List < String >> (); // Stores cities corresponding to specific rail
 private Map < String, List < Integer >> connectedRails = new HashMap < String, List < Integer >> (); //stores rail ids connected to each city
 private Map < Integer, Double > railValues = new HashMap < Integer, Double > (); //this is the traffic/rails in metric, for min bid use minamounts
 private Map < Integer, Double > railDistance = new HashMap < Integer, Double > ();

 private List < List < Integer >> duplicateRails = new ArrayList < List < Integer >> ();


 public Player() {
  rand = new Random();
 }

 public void init(
  String name,
  double budget,
  List < Coordinates > geo,
  List < List < Integer >> infra,
  int[][] transit,
  List < String > townLookup,
  List < BidInfo > allBids) {
  this.budget = budget;
  this.initBudget = budget;

  // Initialize availableLinks
  for (int i = 0; i < allBids.size(); i++) {
   BidInfo bi = allBids.get(i);
   for (int j = i + 1; j < allBids.size(); j++) {
    BidInfo b2 = allBids.get(j);
    if (bi.town1.equals(b2.town1) && bi.town2.equals(b2.town2) && bi.id != b2.id) {
     ArrayList < Integer > pair = new ArrayList < Integer > ();
     pair.add(bi.id);
     pair.add(b2.id);
     duplicateRails.add(pair);
    }
   }

   // System.out.println("===================ID " + bi.id + " " + bi.town1 + " " + bi.town2);
   List cities = new ArrayList < String > ();
   cities.add(bi.town1);
   cities.add(bi.town2);
   railCities.put(bi.id, cities);
   if (bi.owner == null) {
    availableLinks.add(bi.id);
    minAmounts.put(bi.id, bi.amount);
    originalMins.put(bi.id, bi.amount);
    if (connectedRails.get(bi.town1) == null) {
     List newlist = new ArrayList < Integer > ();
     newlist.add(bi.id);
     connectedRails.put(bi.town1, newlist);
    } else {
     connectedRails.get(bi.town1).add(bi.id);
    }

    if (connectedRails.get(bi.town2) == null) {
     List newlist = new ArrayList < Integer > ();
     newlist.add(bi.id);
     connectedRails.put(bi.town2, newlist);
    } else {
     connectedRails.get(bi.town2).add(bi.id);
    }
   }
  }

  Map < Integer, Integer > cityTraffic = new HashMap < Integer, Integer > ();
  for (int m = 0; m < geo.size(); m++) {
   cityTraffic.put(m, 0);
   for (int n = 0; n < geo.size(); n++) {
    if (n == m) {
     for (int p = 0; p < geo.size(); p++) {
      cityTraffic.put(m, cityTraffic.get(m) + transit[n][p]);
     }
    } else {
     cityTraffic.put(m, cityTraffic.get(m) + transit[n][m]);
    }
   }
  }

  int id = 0;
  for (int i = 0; i < infra.size(); ++i) {
   for (int j = 0; j < infra.get(i).size(); ++j) {
    int irails = infra.get(i).size();
    int jrails = infra.get(infra.get(i).get(j)).size();

    //		System.out.printf("i is %d, irails starting at %d\n", i, irails);
    //		System.out.printf("j is %d, jrails starting at %d\n", j, jrails);

    for (int k = 0; k < infra.size(); ++k) {
     if (infra.get(k).contains(i)) {
      irails += 1;
      //	System.out.printf("found a %d in line %d, irails now %d\n", i, k, irails);
     }
     if (infra.get(k).contains(infra.get(i).get(j))) {
      jrails += 1;
      //	System.out.printf("found a %d in line %d, jrails now %d\n", j, k, jrails);
     }
    }
    double value = 0;
    if (irails != 0) {
     value += cityTraffic.get(i) / irails;
    }
    if (jrails != 0) {
     value += cityTraffic.get(infra.get(i).get(j)) / jrails;
    }
    Coordinates p1 = geo.get(i);
    Coordinates p2 = geo.get(infra.get(i).get(j));
    //		System.out.printf("%f, %f and %f, %f\n", p1.x, p1.y, p2.x, p1.y);
    double dist = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    value = value * dist * 10 * margin;
    this.railCount += 1;
    if (value < originalMins.get(id)) {
     railValues.put(id, originalMins.get(id) + 10);
    } else {
     railValues.put(id, value);
    }
    railDistance.put(id, dist);
    id++;
   }
  }
  int numPlayers = 8;
  if(railCount > numPlayers * 4){
    for(int linkId : railValues.keySet()){
      double restored = originalMins.get(linkId) + 10;
      railValues.put(linkId, restored);
    }
  }
 }

 public boolean bidEquals(Bid bid1, Bid bid2) {
  if (bid1 == null || bid2 == null) {
   if (bid1 == null && bid2 == null) {
    return true;
   } else {
    return false;
   }
  }

  boolean result = true;

  if (bid1.id1 != bid2.id1) {
   result = false;
  }

  if (bid1.id2 != bid2.id2) {
   result = false;
  }

  if (bid1.amount != bid2.amount) {
   result = false;
  }
  if (bid1.bidder != bid2.bidder) {
   result = false;
  }
  return result;
 }

 public Bid getBid(List < Bid > currentBids, List < BidInfo > allBids, Bid lastRoundMaxBid) {

  // BOOK KEEPING AND ROUND CHANGES //

  // Initialize all player budgets at the very start of the Auction
  if (lastRoundMaxBid == null && !playerBudgets.containsKey("g5")) {
   for (int i = 1; i < 9; i++) {
    String player = "g" + Integer.toString(i);
    playerBudgets.put(player, initBudget);
   }
  }

  if (!bidEquals(lastWinner, lastRoundMaxBid)) {
   // Entered a new round, make necessary updates
   // System.out.println("Making updates!");
   this.lastWinner = lastRoundMaxBid;

   if (lastWinner != null) {

    //	  System.out.printf("last bid was %f for distance %f + %f\n", lastRoundMaxBid.amount, railDistance.get(lastRoundMaxBid.id1), railDistance.get(lastRoundMaxBid.id2));
    // Remove purchased link
    if (availableLinks.contains(lastWinner.id1)) {
     availableLinks.remove(Integer.valueOf(lastWinner.id1));
    }
    if (lastWinner.id2 != -1) {
     availableLinks.remove(Integer.valueOf(lastWinner.id2));
    }
    // Restore original minimum prices
    // THIS IS NOT A DEEP COPY AND WILL ONLY WORK ONCE          this.minAmounts = this.originalMins;
    for (Integer k: minAmounts.keySet()) {
     minAmounts.put(k, originalMins.get(k));
    }
    // Update playerBudgets to reflect last winner
    if (!playerBudgets.containsKey(lastRoundMaxBid.bidder)) {
     playerBudgets.put(lastRoundMaxBid.bidder, initBudget);
    }

    if (!updatedRoundBudget) {
     double oppBudget = playerBudgets.get(lastRoundMaxBid.bidder);
     playerBudgets.put(lastRoundMaxBid.bidder, oppBudget - lastRoundMaxBid.amount);
     //System.out.println("===================== " + lastRoundMaxBid.id1 + " " + lastRoundMaxBid.bidder);

     // Update current progress of auction (average current budget / init budget)
     double totalBudget = 0;
     for (int i = 1; i < 9; i++) {
       String player = "g" + Integer.toString(i);
       totalBudget += playerBudgets.get(player);
     }
     double avgBudget = totalBudget / 8;
     auctionProgress = avgBudget / initBudget;
     // System.out.println("================== " + auctionProgress);

     if (lastRoundMaxBid.bidder.equals("g5")) {
      List < String > newCities = railCities.get(lastRoundMaxBid.id1);
      //System.out.println(newCities);
      if (!ownedCities.contains(newCities.get(0))) {
       ownedCities.add(newCities.get(0));
      }
      if (!ownedCities.contains(newCities.get(1))) {
       ownedCities.add(newCities.get(1));
      }

      // Consider newly aquired rails/cities
      ownedRails.add(lastRoundMaxBid.id1);

      // Remove newly aquired adjacent rail from list of available adjacent rails
      if (adjacentRails.contains(lastRoundMaxBid.id1)) {
       adjacentRails.remove(Integer.valueOf(lastRoundMaxBid.id1));
      }

      // Add new adjacent rails to list based on our newly aquired rail
      for (String city: ownedCities) {
       List < Integer > cityRails = connectedRails.get(city);
       for (int rail: cityRails) {
        if (!adjacentRails.contains(rail) && !ownedRails.contains(rail)) {
         adjacentRails.add(rail);
        }
       }
      }
      // Remove processed cities from ownedCities list
      ownedCities.clear();

     } else {
	if (adjacentRails.contains(lastRoundMaxBid.id1)) {
	  adjacentRails.remove(Integer.valueOf(lastRoundMaxBid.id1));
	}
     }
     updatedRoundBudget = true;
    }


    if (lastWinner.bidder.equals("g5")) {
     for (List < Integer > pair: duplicateRails) {
      if (pair.contains(lastWinner.id1) || pair.contains(lastWinner.id2)) {
       for (Integer rail: pair) {
        if (availableLinks.contains(rail)) {
         availableLinks.remove(rail);
        }
       }
      }
     }
    }
   }

   for (int i = availableLinks.size() - 1; i >= 0; i--) {
    int linkId = availableLinks.get(i);
    if (originalMins.get(linkId) > this.budget) {
     availableLinks.remove(i);
    }

   }
   if(adjacentRails.size() > 0){
     // Find the most valuable adjacent link for us
     this.bestAdj = -1;
     this.bestAdjValue = 0;
     for (int linkId: adjacentRails){
       double unitValue = railValues.get(linkId) / railDistance.get(linkId);
       if (unitValue > this.bestValue) {
        this.bestAdj = linkId;
        this.bestAdjValue = unitValue;
       }
     }
   }

   // Find the most valuable link for us
   this.bestLink = -1;
   this.bestValue = 0;
   for (int linkId: availableLinks) {
    System.out.printf("link: %s-%s, min bid: %f, our value: %f\n", railCities.get(linkId).get(0), railCities.get(linkId).get(1), minAmounts.get(linkId), railValues.get(linkId));
    double unitValue = railValues.get(linkId) / railDistance.get(linkId);
    if (unitValue > this.bestValue) {
     this.bestLink = linkId;
     this.bestValue = unitValue;
    }
   }
  }

  // MAIN BIDDING STRATEGY //

  // Search through bids to find current winner
  Bid curMax = new Bid();
  curMax.amount = 0.0;
  curMax.bidder = "None";
  double unitPrice = 0.0;

  for (Bid pastBid: currentBids) {
   // Update minimum bid amounts
   if (pastBid.amount >= minAmounts.get(pastBid.id1) && !(pastBid.id2 >= 0)) {
    minAmounts.put(pastBid.id1, pastBid.amount + 10000);
   }

   double bidPrice = pastBid.amount / railDistance.get(pastBid.id1);
   if (pastBid.id2 != -1) {
    double dist = railDistance.get(pastBid.id1) + railDistance.get(pastBid.id2);
    bidPrice = pastBid.amount / dist;
   }
   // Update max
   if (bidPrice > unitPrice) {
    curMax = pastBid;
    unitPrice = bidPrice;
   }
   // Only iterate until our latest bid
   String player = pastBid.bidder;
   if (player.equals("g5")) {
    break;
   }
  }

  // If we have the winning bid, return null
  if (curMax.bidder.equals("g5") || bestLink == -1) {
   return null;
  } else { // If we aren't winning, increment the bid on our most valuable link

  // First look at adjacentRails
  if (bestAdj != -1){
    double maxAmount = railValues.get(this.bestAdj);
    double maxUnit = maxAmount / railDistance.get(this.bestLink);
    if (maxUnit > unitPrice) {
     double amount = unitPrice * railDistance.get(this.bestLink) + 1;
     if (amount < minAmounts.get(this.bestLink)) {
      amount = minAmounts.get(this.bestLink); //increment
     }
     if (amount < this.budget && amount <= maxAmount) {
      Bid ourBid = new Bid();
      ourBid.id1 = this.bestLink;
      ourBid.amount = amount;
      //System.out.println("Bidding on adjacent rail");
      return ourBid;
     }
    }
  }

   double maxAmount = railValues.get(this.bestLink);
   double maxUnit = maxAmount / railDistance.get(this.bestLink);
   if (maxUnit > unitPrice) {
    double amount = unitPrice * railDistance.get(this.bestLink) + 1;
    if (amount < minAmounts.get(this.bestLink)) {
     amount = minAmounts.get(this.bestLink); //increment
    }
    if (amount < this.budget && amount <= maxAmount) {
     Bid ourBid = new Bid();
     ourBid.id1 = this.bestLink;
     ourBid.amount = amount;
     //System.out.printf("\nMin bid %f for %d\n", originalMins.get(ourBid.id1), ourBid.id1);
     //System.out.printf("Our value: %f\n", railValues.get(ourBid.id1));
     //System.out.printf("Our bid: %f for distance %f\n\n", ourBid.amount, railDistance.get(ourBid.id1));
     return ourBid;
    } else {
     return null;
    }
   }
   //System.out.printf("maxUnit: %f, unitPrice: %f\n", maxUnit, unitPrice);
  }
  // If we don't want to increment, drop out
  return null;
 }

 public void updateBudget(Bid bid) {
  //System.out.println("Rails in our potential network =========== " + adjacentRails);
  //System.out.println("We own ======" + ownedRails);
  updatedRoundBudget = false;
  if (bid != null) {
   budget -= bid.amount;
  }

  availableBids = new ArrayList < > ();
 }
}
