package LUT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class testLUT {


    private LUT testLUT;
    private int[] ceiling = {4,4,4,3,1,1,1};
    private int[] floor = {0,0,0,0,0,0,0};
    private int argNumInputs=7;

    @BeforeEach
    void runBefore() {
        //testLUT = new LUT(argNumInputs,floor,ceiling);

    }

    @Test

    void testInitializeLUT() {
        //int size = testLUT.sizeofQTable();
      //  assertEquals(size,5*5*5*4*2*2*2);
    }

    @Test
    void testIndexX() {
        //double[] X={3,3,3,2,0,0,0};
        //int index=testLUT.indexFor(X);
        //assertEquals(index,3+3*5+3*5*5+2*5*5*5);
    }

    @Test
    void testInitialTraining() {

    }
}