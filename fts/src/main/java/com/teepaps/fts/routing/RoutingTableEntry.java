package com.teepaps.fts.routing;

import java.io.Serializable;

/**
 * The route that a message can follow.
 *
 * @author Created by ted on 4/15/14.
 */
public class RoutingTableEntry implements Serializable {

    /**
     * Address of destination to reach
     */
    private String destination;

    /**
     * Cost, in number of hops, to reach the destination
     */
    private int cost;

    /**
     * Address of the next hop towards the destination
     */
    private String nextHop;

    public RoutingTableEntry(String destination, int cost, String nextHop) {
        this.destination = destination;
        this.cost = cost;
        this.nextHop = nextHop;
    }

    /**
     * Return a deep clone of this entry.
     * @param entry
     * @return
     */
    public static RoutingTableEntry deepClone(RoutingTableEntry entry) {
        return new RoutingTableEntry(entry.getDestination(), entry.getCost(), entry.getNextHop());
    }

    /**
     * Increase the cost to reach the destination.
     * @param addedCost
     * @return reference to self for chaining.
     */
    public RoutingTableEntry increaseCost(int addedCost) {
        cost += addedCost;
        return this;
    }

    //******** GETTERS ********
    //*************************

    public String getDestination() {
        return destination;
    }

    public String getNextHop() {
        return nextHop;
    }

    public int getCost() {
        return cost;
    }

    //******** SETTERS ********
    //*************************

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }


}
