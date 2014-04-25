package routing;

import java.util.List;

/**
 * The route that a message can follow.
 *
 * @author Created by ted on 4/15/14.
 */
public class Route implements Comparable<Route> {


    /**
     * List of pairs of (Connection Type, MAC)s available to send to in this order.
     * Starts with the first closest node.
     */
    private List<Pair<Integer, String>> MACList;
    
    /**
     * Default constructor does nothing
     */
    public Route() {
    }

    /**
     * Default constructor does nothing
     */
    public Route(List<Pair<Integer, String>> MACList) {
        this.MACList = MACList;
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Route)) {
            return false;
        }

        Route route = (Route) object;
        if (MACList.equals(route.getMACList())) {
            return true;
        }
        return false;
    }

    /**
     * For now, simply compare the number of hops to determine optimal routing. If same number of
     * hops, then choose the first route as more optimal.
     * @param route
     * @return
     */
    @Override
    public int compareTo(Route route) {
    	if (equals(route)) {
            return 0;
        } else if (MACList.size() < route.getMACList().size()) {
            return -1;
        }
        return 1;
    }
    
    /**
     * Merge two routes together.
     * @param route
     */
    public void merge(Route route) {
    	
    }

    //******** GETTERS ********
    //*************************

    public List<Pair<Integer, String>> getMACList() {
        return MACList;
    }
    
    /**
     * Returns the last element of the list
     * @return
     */
    public String getDestination() {
    	String destination = null;
    	if (MACList != null) {
            destination = MACList.get(MACList.size() - 1).second;
    	}
    	return destination;
    }
}
