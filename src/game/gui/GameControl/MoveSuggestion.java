package game.gui.GameControl;

import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.weapons.factory.WeaponFactory;
import java.util.*;

public class MoveSuggestion {

    public String suggestBestMove(Lane lane, WeaponFactory weaponFactory, int curr) {
        int titansAtWall = countTitansAtWall(lane.getTitans());
        int remTitans = lane.getTitans().size() - titansAtWall;
        int titanInrange = countTitansAtRange(lane.getTitans());

        // Prioritize Proximity Trap if there are titans at the wall and enough currency
        if (titansAtWall > 0 && curr >= 75) {
            return "Proximity Trap";
        }

        // Use Wall Spread Cannon if there are many remaining titans and enough currency
        if (titanInrange > 15 && curr >= 100) {
            return "Wall Spread Cannon";
        }

        // Use Anti Titan Shell if there are a significant number of remaining titans and enough currency
        if (remTitans > 5 && curr >= 25) {
            return "Anti-Titan Shell";
        }

        // Use Long Range Spear as a default option if enough currency is available
        if (curr >= 25) {
            return "Long Range Spear";
        }

        // Skip if none of the conditions are met
        return "Skip";
    }

    private int countTitansAtRange(PriorityQueue<Titan> titans) {
    	int count = 0;
        Iterator<Titan> iterator = titans.iterator();
        while (iterator.hasNext()) {
            Titan titan = iterator.next();
            if (titan.getDistance()<=50 && titan.getDistance()>=20) {
                count++;
            }
        }
        return count;
	}

	private static int countTitansAtWall(PriorityQueue<Titan> titans) {
        int count = 0;
        Iterator<Titan> iterator = titans.iterator();
        while (iterator.hasNext()) {
            Titan titan = iterator.next();
            if (titan.hasReachedTarget()) {
                count++;
            }
        }
        return count;
    }

}
