package game.gui.GameControl;

import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.weapons.Weapon;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

public class GameState {
    PriorityQueue<Lane> lanes;
    ArrayList<Titan> titansInLanes;
    int currentResources;

    public GameState(PriorityQueue<Lane> lanes, ArrayList<Titan> titansInLanes,int currentRecources) {
        this.titansInLanes = titansInLanes;
        this.lanes = lanes;
        this.currentResources = currentRecources;
        
    }
}
