package routing;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * HashMap that holds all the routes to different Peers
 * String key - destination address
 * TreeSet<Route> - An inorder set of all routes to that destination
 * Created by ted on 4/15/14.
 */
public class RoutingTable extends HashMap<String, TreeSet<Route>> {

    /**
     * Default constructor
     */
    public RoutingTable() {
        super();
    }

    @Override
    public TreeSet<Route> put(String key, TreeSet<Route> value) {
        return super.put(key, value);
    }

    /**
     * Return the first entry in the Routing set corresponding to the MAC, which is the optimal one.
     * @return the optimal route, null otherwise
     */
    public Route getOptimalRoute (String MACAddress) {
        if (size() > 0) {
            TreeSet<Route> routeSet = get(MACAddress);
            if (routeSet.size() > 0) {
                return get(MACAddress).first();
            }
        }
        return null;
    }
    
    /**
     * Add a route to the routing table
     * @param key
     * @param route
     */
    public void add(Route route) {
    	TreeSet<Route> routeSet = get(route.getDestination());
    	if (routeSet != null) { 
    		routeSet.add(route);
    	}
    	else {
    		routeSet = new TreeSet<Route>();
    		routeSet.add(route);
    		this.put(route.getDestination(), routeSet);
    	}
    }
    
    /** 
     * Either add a new route to the routing table or update an existing entry.
     * @param route
     */
    private void addOrUpdate(Route route) {
    	
    }
    
    /**
     * Merge two routing tables together to get new routes.
     * @param routingTable
     */
    public void merge(RoutingTable routingTable) {
        // Get the routes for each destination
    	for (Map.Entry<String, TreeSet<Route>> entry : routingTable.entrySet()) {
    		for (Route route : routingTable.get(entry.getKey())) {
    			addOrUpdate(route);
    		}
    	}
    }

    /**
     * Output routing table as a string for debugging purposes
     */
    public String toString() {
    	String tableString = "";
    	int i = 1;
    	for (Map.Entry<String, TreeSet<Route>> set : this.entrySet()) {
    		int j = 1;
    		for (Route route : set.getValue()) {
    			tableString += "Dest #" + i + " -- Route #" + j + ": " + route.getMACList().toString() + "\n";
    			j++;
    		}
    		i++;
    	}

    	return tableString;
    }
}
