package com.thundernoteapp.android.supportclasses;

import java.util.ArrayList;

public class Filtro {

	public static ArrayList<FsqVenue> gestisciFiltro (ArrayList<FsqVenue> venues , String filter){

		if (filter.equals("Food") || filter.equals("Coffee")){
			filter = "Food";
		}
		if (filter.equals("Shops")){
			filter = "Shops & Services";
		}
		if (filter.equals("Drinks")){
			filter = "Nightlife Spots";
		}
		if (filter.equals("Arts")){
			filter = "Arts & Entertainment";
		}
		if (filter.equals("Outdoors")){
			filter = "Outdoors & Recreation";
		}

		ArrayList<FsqVenue> venuesListFiltered = new ArrayList<FsqVenue>();

		for (int i = 0; i < venues.size(); i++) {

			if (venues.get(i).type.equals(filter)) {
				FsqVenue fsq = venues.remove(i);
				venuesListFiltered.add(fsq);
			}

		}

		return venuesListFiltered;

	}

}
