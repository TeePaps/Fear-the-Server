package com.teepaps.fts.routing;

import android.util.Pair;

import java.util.Comparator;
import java.util.List;

/**
 * The route that a message can follow.
 *
 * @author Created by ted on 4/15/14.
 */
public class Route {

    /**
     * List of pairs of (Connection Type, MAC)s available to send to
     */
    private List<Pair<Integer, String>> MACList;

    /**
     * Default constructor does nothing
     */
    public Route() {

    }

    /**
     * Used so we can call sort on the list of Routes to find the best one.
     * <a href="http://www.vogella.com/tutorials/JavaCollections/article.html#collectionssort">Example from Vogella</a>
     */
    public static class RouteComparable implements Comparator<Route> {
        @Override
        public int compare(Route route, Route route2) {
            return 0;
        }
    }
}
