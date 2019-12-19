package BP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class testBackPropagation {

    private BackPropagation BP1;
    private double[][] X= {{-1,-1},{-1,1},{1,-1},{1,1}};
    private double[] Y={1,-1,-1,1};
    private double[][] X2= {{0,0},{0,1},{1,0},{1,1}};
    private double[] Y2={1,0,0,1};

    @BeforeEach
    void runBefore() {
        BP1 = new BackPropagation();
        BP1.initializeWeights();
    }

    @Test
    void testConstructor() {
        BP1.value[0][0]=X[0][0];
        BP1.value[0][1]=X[0][1];
       // assertEquals(BP1.value[0][2],1);
    }

    @Test
    void testOutput(){
        double[] X={1,1};
        double result = BP1.outputFor(X);
        System.out.println(result);
    }

    @Test
    void testOutputError(){
        double[] X={1,1};
        double result = BP1.outputFor(X);
        double error=BP1.outputError(-1,result);
        System.out.println(error);
    }

    @Test
    void testHiddenError(){
        double[] X={1,1};
        double result = BP1.outputFor(X);
        double error=BP1.outputError(-1,result);
        BP1.hiddenErrors();
        BP1.weightUpdate(1);
    }

    @Test
    void testTrain() throws IOException {
        double[] X={1,1};
        double error=BP1.train(X,-1);
        System.out.println(error);
    }

    @Test
    void testTrainModel(){ //Only use with bipolar representation
        int round = BP1.trainModel(X,Y);
        System.out.println(round);

    }

    @Test
    void testTrainModel2(){ //Only use with binary representation
        int round = BP1.trainModel(X2,Y2);
        System.out.println(round);

    }


}
