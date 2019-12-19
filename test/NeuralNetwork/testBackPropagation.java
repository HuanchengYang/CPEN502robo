package NeuralNetwork;
import LUT.LUT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class testBackPropagation {

    private BackPropagationLUT BPLUT1;
    private double[][] X= {{0.5,0.4,0.7,0.3,-0.2,1,-1,1,1,-1,1},{0.3,0.2,0.9,0.7,-0.2,1,1,-1,-1,1,1}};
    private double[] Y={0.6,-0.4};
    private double[][] X2= {{0,0},{0,1},{1,0},{1,1}};
    private double[] Y2={1,0,0,1};

    @BeforeEach
    void runBefore() throws IOException {
        LUT lut = new LUT();
        lut.initialiseLUT();
        BPLUT1 = new BackPropagationLUT();
        BPLUT1.initializeWeights();
        //File f=new File("weightlist3.txt");
        //BPLUT1.load(f);
    }

    @Test
    void testConstructor() {
        BPLUT1.value[0][0]=X[0][0];
        BPLUT1.value[0][1]=X[0][1];
       // assertEquals(BP1.value[0][2],1);
    }

    @Test
    void testOutput(){
        double[] X={1,1};
        double result = BPLUT1.outputFor(X);
        System.out.println(result);
    }

    @Test
    void testOutputError(){
        double[] X={1,1};
        double result = BPLUT1.outputFor(X);
        double error=BPLUT1.outputError(-1,result);
        System.out.println(error);
    }

    @Test
    void testHiddenError(){

        double[] X={1,1};
        double result = BPLUT1.outputFor(X);
        double error=BPLUT1.outputError(-1,result);
        BPLUT1.hiddenErrors();
        BPLUT1.weightUpdate(1);
    }

    @Test
    void testTrain(){
        double[] X={1,1};
        double error=BPLUT1.train(X,-1);
        System.out.println(error);
    }

    @Test
    void testTrainModel() throws IOException { //Only use with bipolar representation
        int round = BPLUT1.trainModel(X,Y);
        System.out.println(round);
        BPLUT1.save(new File("weightlist.txt"));
    }

    @Test
    void testTrainModelwithLUT() throws IOException { //Only use with binary representation
        LUT lut2 = new LUT();
        lut2.load(new File("lut6.txt"));
        BackPropagationLUT BPLUT2 = new BackPropagationLUT();
        BPLUT2.load(new File("weightlist3.txt"));
        BPLUT2.initializeTraining(lut2);
        BPLUT2.save(new File("weightlist3.txt"));
    }

    @Test
    void testTrainModelwithLUT2() throws IOException { //Only use with binary representation
        LUT lut2 = new LUT();
        lut2.load(new File("lut6.txt"));
        BackPropagationLUT BPLUT3 = new BackPropagationLUT();
        BPLUT3.load(new File("weightlist4.txt"));
        BPLUT3.initializeTraining(lut2);
        BPLUT3.save(new File("weightlist4.txt"));
    }

    @Test
    void testTrainModelwithLUT3() throws IOException { //Only use with binary representation
        LUT lut2 = new LUT();
        lut2.load(new File("lut6.txt"));
        BackPropagationLUT BPLUT3 = new BackPropagationLUT();
        BPLUT3.initializeWeights();
        BPLUT3.initializeTraining(lut2);
        BPLUT3.save(new File("weightlist5.txt"));
    }
}
