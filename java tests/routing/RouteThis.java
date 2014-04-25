package routing;

import java.util.ArrayList;
import java.util.List;

public class RouteThis {

	public static void main(String[] args) {
		RoutingTable table = new RoutingTable();
		
		List<Pair<Integer, String>> macList = new ArrayList<Pair<Integer, String>>();
		macList.add(Pair.create(1, "3"));
		macList.add(Pair.create(1, "1"));
		macList.add(Pair.create(1, "4"));
		table.add(new Route(macList));
		
		List<Pair<Integer, String>> macList2 = new ArrayList<Pair<Integer, String>>();
		macList2.add(Pair.create(1, "3"));
		macList2.add(Pair.create(1, "1"));
		macList2.add(Pair.create(1, "5"));
		table.add(new Route(macList2));

        List<Pair<Integer, String>> macList3 = new ArrayList<Pair<Integer, String>>();
		macList3.add(Pair.create(1, "3"));
		macList3.add(Pair.create(1, "1"));
		macList3.add(Pair.create(1, "4"));
		table.add(new Route(macList3));
		
        List<Pair<Integer, String>> macList4 = new ArrayList<Pair<Integer, String>>();
		macList4.add(Pair.create(1, "3"));
		macList4.add(Pair.create(1, "1"));
		macList4.add(Pair.create(1, "1"));
		macList4.add(Pair.create(1, "4"));
		System.out.println("Contains? " + macList4.contains(Pair.create(1, "8")));
		table.add(new Route(macList4));
			
        List<Pair<Integer, String>> macList5 = new ArrayList<Pair<Integer, String>>();
		macList5.add(Pair.create(1, "2"));
		macList5.add(Pair.create(1, "4"));
		table.add(new Route(macList5));	

		System.out.println(table.toString());
		System.out.println(table.getOptimalRoute("4").getMACList().toString());
	}

}
