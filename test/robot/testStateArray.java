package robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class testStateArray {
    double[] state1 = {1,2,3,4,5,6};
    double[] state2 = {1,2,3,4,5,6};
    double[] state3 = {1,1,1,2,2,2};
    StateArray stateArray1;
    StateArray stateArray2;
    StateArray stateArray3;
    HashMap<StateArray, Double> DoublestateMap = new HashMap<StateArray, Double>();


    @BeforeEach
    void runBefore() {
        stateArray1 = new StateArray(state1);
        stateArray2 = new StateArray(state2);
        stateArray3 = new StateArray(state3);
        DoublestateMap.put(stateArray1,1.0);
        DoublestateMap.put(stateArray3,2.0);



    }

    @Test

    void testEquals() {
        assertEquals(stateArray2,stateArray1);
        assertEquals(DoublestateMap.get(stateArray2),1);
    }

}
