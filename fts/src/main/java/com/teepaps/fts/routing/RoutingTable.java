package com.teepaps.fts.routing;

import java.io.Serializable;
import java.util.HashMap;

/**
 * TreeSet that holds all the routes  to different Peers
 * Created by ted on 4/15/14.
 */
public class RoutingTable extends HashMap<String, RoutingTableEntry>
        implements Serializable
{


    /**
     * Cost of a node that is unreachable from the current router
     */
    private static final int COST_UNREACHABLE = -1;

    /**
     * Address of router holding this routing table
     */
    public final String routerAddress;

    /**
     * Default constructor
     */
    public RoutingTable(String routerAddress) {
        super();
        put(routerAddress, new RoutingTableEntry(routerAddress, 0, routerAddress));
        this.routerAddress = routerAddress;
    }

    /**
     * Return a deep clone of the routing table
     * @param table
     * @return
     */
    public static RoutingTable deepClone(RoutingTable table) {
        RoutingTable cloneTable =  new RoutingTable(table.routerAddress);

        for (RoutingTableEntry entry : table.values()) {
            cloneTable.put(entry.getDestination(), RoutingTableEntry.deepClone(entry));
        }

        return cloneTable;
    }

    @Override
    public RoutingTableEntry put(String key, RoutingTableEntry value) {
        return super.put(key, value);
    }

    /**
     * Return the first entry in the Routing set corresponding to the MAC, which is the optimal one.
     * @return the optimal route, null otherwise
     */
    public RoutingTableEntry getOptimalRoute (String MACAddress) {
        return null;
    }

    /**
     * Add a route to the routing table
     * @param entry
     */
    public void add(RoutingTableEntry entry) {
        put(entry.getDestination(), entry);
    }

    /**
     * Set a node to unreachable, and update the other entries in the table accordingly.
     */
    public void killNode(String destination) {
        for (RoutingTableEntry entry : this.values()) {
            // If not the router itself
            if (entry.getCost() > 0) {
                entry.setCost(COST_UNREACHABLE);
            }
        }
    }

    /**
     * Merge two routing tables together to get new routes.
     * @param routingTable
     */
    public void merge(RoutingTable routingTable) {
        RoutingTableEntry entryFromSelf;

        // Get the routes for each destination
        for (RoutingTableEntry entryFromSender : routingTable.values()) {
            String destination = entryFromSender.getDestination();
            entryFromSelf = get(destination);

			/*
			 * Update algorithm: Slide 5 -
			 * http://fac-web.spsu.edu/ecet/wagner/4820Ch21.pdf
			 */

            // If node in new table is unreachable exists in the current table,
            if ((entryFromSelf != null)
                    && (entryFromSender.getCost() < 0)
                    && entryFromSelf.getNextHop().equals(routingTable.routerAddress))
            {
                // Set it to unreachable in the current table
                entryFromSelf.setCost(COST_UNREACHABLE);
            }
            else {
                // Add one hop to the hop count for each advertised destination.
                entryFromSender.increaseCost(1);

                // Change the next hop to the router sending the update message.
                entryFromSender.setNextHop(routingTable.routerAddress);

                // If the destination in the message is not in the routing
                // table, add it
                if (entryFromSelf == null) {
                    add(entryFromSender);
                }
                // Else, if the destination in the message is in the routing
                // table, then
                else {
                    // If the next-hop field in the existing routing table is
                    // the same as the router
                    // sending the update.
                    if (entryFromSelf.getNextHop().equals(routingTable.routerAddress)) {
                        // Replace the entry in the routing table with the
                        // advertised one
                        remove(entryFromSender.getDestination());
                        add(entryFromSender);
                    } else {
                        // If the advertised hop count is smaller than the one
                        // in the existing table,
                        if (entryFromSender.getCost() < entryFromSelf.getCost()) {
                            // Add it to the table
                            remove(entryFromSender.getDestination());
                            add(entryFromSender);
                        } else {

                        }
                        // Else, do nothing
                    }
                }
            }
        }
    }

    /**
     * Output routing table as a string for debugging purposes
     */
    public String toString() {
        String tableString = "";
        int i = 1;
        for (RoutingTableEntry entry : this.values()) {
            tableString += i + ") "
                    + entry.getDestination() + " -- "
                    + entry.getCost() + " -- "
                    + entry.getNextHop() + "\n";
            i++;
        }

        return tableString;
    }
}
