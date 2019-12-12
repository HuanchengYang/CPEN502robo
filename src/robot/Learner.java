package robot;

import LUT.LUT;
//import NeuralNet.NeuralNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Learner {
    public static final double LearningRate = 0.1;
    public static final double DiscountRate = 0.9;
    public static final double ExploitationRate = 1;
    private boolean initialize = true;
    private LUT lut;
    private static int typeofState = 6;
    private static int typeofAction = 5;

    //private ArrayList<NeuralNet> neuralNetworks = new ArrayList<NeuralNet>();

    private double[] currentStateArray = new double [typeofState];
    private double[] newStateArray = new double [typeofState];
    private double [] currentActionArray  = new double [typeofAction];
    private double [] newActionArray = new double[typeofAction];
    private double [] currentActionQ  = new double[Action.NumRobotActions];
    private double [] newActionQ = new double[Action.NumRobotActions];
    private double qError[][]= new double [State.NumofState][Action.NumRobotActions];


    HashMap<StateArray, Double> DoublestateMap = new HashMap<StateArray, Double>();
    HashMap<ActionArray, Double> DoubleactionMap = new HashMap<ActionArray, Double>();
    HashMap<Double, double[]> ArraystateMap = new HashMap<Double, double[]>();
    HashMap<Double, double[]> ArrayactionMap = new HashMap<Double, double[]>();




    public Learner(LUT lut)
    {
        this.lut = lut;
        initializeActionHash();
        initializeStateHash();
    }

    //Off-Policy
    public void learnOffPolicy(double newstate[], double newaction[], double immediateReward)
    {
        if (initialize) {
            initialize = false;
            currentStateArray = newstate;
            currentActionArray = newaction;
        }
        else
        {

            double oldStateInt = convertArrayStatetoInt(currentStateArray);
            double newStateInt = convertArrayStatetoInt(newstate);
            double oldActionInt = convertArrayActiontoInt(currentActionArray);
            double newActionInt = convertArrayActiontoInt(newaction);

            double oldQValue = lut.getQValue(oldStateInt,oldActionInt);
            double newQValue = (1 - LearningRate) * oldQValue + LearningRate * (immediateReward + DiscountRate * lut.getMaximumQ(newStateInt));
            lut.trainTable(oldStateInt,oldActionInt,newQValue);
            currentStateArray = newstate;
            currentActionArray = newaction;
        }
    }

    //On Policy
    public void learnOnPolicy(double newstate[], double newaction[], double immediateReward)
    {
        if (initialize) {
            initialize = false;
            currentStateArray = newstate;
            currentActionArray = newaction;
        }
        else
        {
            double oldStateInt = convertArrayStatetoInt(currentStateArray);
            double newStateInt = convertArrayStatetoInt(newstate);
            double oldActionInt = convertArrayActiontoInt(currentStateArray);
            double newActionInt = convertArrayActiontoInt(newaction);

            double oldQValue = lut.getQValue(oldStateInt,oldActionInt);
            double newQValue = (1 - LearningRate) * oldQValue + LearningRate * (immediateReward + DiscountRate * lut.getQValue(newStateInt,newActionInt));
            lut.trainTable(oldStateInt,oldActionInt,newQValue);
            currentStateArray = newstate;
            currentActionArray = newaction;
        }
    }


    private void initializeStateHash(){
        double statecount = 0;
        for (double i=0;i<State.NumHeading;i++){
            for (double j=0;j<State.NumTargetDistance;j++){
                for (double k=0;k<State.NumTargetBearing;k++){
                    for (double l=0;l<State.NumVerticalHitWall;l++){
                        for (double m=0;m<State.NumHorizontalHitWall;m++){
                            for (double n=0;n<State.NumIsHit;n++){
                                double[] testArray= new double[]{i, j, k, l, m, n};
                                StateArray testStateArray = new StateArray(testArray);
                                this.DoublestateMap.put(testStateArray,statecount);
                                this.ArraystateMap.put(statecount,testArray);
                                statecount++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void initializeActionHash(){
        double actioncount = 0;
        for (double i=0;i<Action.RobotAhead;i++){
            for (double j=0;j<Action.RobotBack;j++){
                for (double k=0;k<Action.RobotAheadTurnLeft;k++){
                    for (double l=0;l<Action.RobotAheadTurnRight;l++){
                        for (double m=0;m<Action.RobotFire;m++){
                            double[] testAction= new double[]{i, j, k, l, m};
                            ActionArray testActionArray = new ActionArray(testAction);
                            this.DoubleactionMap.put(testActionArray,actioncount);
                            this.ArrayactionMap.put(actioncount,testAction);
                            actioncount++;
                        }
                    }
                }
            }
        }
    }

    public double convertArrayStatetoInt(double[] state){
        StateArray stateArray = new StateArray(state);
        return DoublestateMap.get(stateArray);
    }

    public double[] convertIntStatetoArray(double state){
        return ArraystateMap.get(state);
    }

    public double convertArrayActiontoInt(double[] action){
        ActionArray actionArray = new ActionArray(action);
        return DoubleactionMap.get(actionArray);
    }

    public double[] convertIntActiontoArray(double action){
        return ArrayactionMap.get(action);
    }

//    public void nn_QLearn( int state, int action, double reward) {
//        //Need to make currentData Array and new Data array is set before calling this function
//        double currentStateQValue = getCurrentQValues()[action] ;
//        double [] newInputData = new double[numStateCategory];
//        newInputData = LUTNeuralNet.normalizeInputData(getNewStateArray());
//        for(NeuralNet theNet: neuralNetworks) {
//            int act = theNet.getNetID();
//            double tempOutput = theNet.outputFor(newInputData)[0];
//            double tempQValue = remappingOutputToQ(tempOutput, maxQ[act], minQ[act], upperBound, lowerBound);
//            setNewActionValue(tempOutput,theNet.getNetID());
//            setNewQValue(tempQValue,theNet.getNetID());
//        }//Update the NewActionValue and newQValues Arrays
//        int maxNewStateActionIndex = getMaxIndex(getNewActionValues());
//        double maxNewQValue = getNewQValues()[maxNewStateActionIndex];
//        double expectedQValue = currentStateQValue + learningRate*(reward + discountRate *maxNewQValue -currentStateQValue);
//        double [] expectedOutput = new double[1];
//        expectedOutput[0] = LUTNeuralNet.normalizeExpectedOutput(expectedQValue, maxQ[action], minQ[action], upperBound, lowerBound);
//        NeuralNet learningNet = neuralNetworks.get(action);
//        double [] currentInputData = LUTNeuralNet.normalizeInputData(getNewStateArray());
//        learningNet.train(currentInputData, expectedOutput);
//        if (getCurrentQValues()[action] != 0) {
//            qError[state][action] = (getNewQValues()[action] - getCurrentQValues()[action])/getCurrentQValues()[action];
//        }
//
//    }
//
//    public void setCurrentStateArray (int state) {
//        currentStateArray = State.getStateFromIndex(state);
//    }
//    public void setNewStateArray (int state) {
//        newStateArray = State.getStateFromIndex(state);
//    }
//
//    public void initializeNeuralNetworks(){
//        for(int i = 0; i < Action.NumRobotActions; i++) {
//            NeuralNet theNewNet = new NeuralNet(numInput,numHidden,numOutput,learningRate_NN,momentumRate,lowerBound,upperBound,i);
//            neuralNetworks.add(theNewNet);
//        }
//    }
//
//    public static double [] normalizeInputData(int [] states) {
//        double [] normalizedStates = new double [6];
//        for(int i = 0; i < 6; i++) {
//            switch (i) {
//                case 0:
//                    normalizedStates[0] = -1.0 + ((double)states[0])*2.0/((double)(State.NumHeading-1));
//                    break;
//                case 1:
//                    normalizedStates[1] = -1.0 + ((double)states[1])*2.0/((double)(State.NumTargetDistance-1));;
//                    break;
//                case 2:
//                    normalizedStates[2] = -1.0 + ((double)states[2])*2.0/((double)(State.NumTargetBearing-1));;
//                    break;
//                case 3:
//                    normalizedStates[3] = -1.0 + ((double)states[3])*2.0;
//                    break;
//                case 4:
//                    normalizedStates[4] = -1.0 + ((double)states[4])*2.0;
//                    break;
//                case 5:
//                    normalizedStates[5] = -1.0 + ((double)states[5])*2.0;
//                    break;
//                default:
//                    System.out.println("The data doesn't belong here.");
//            }
//        }
//        return normalizedStates;
//    }
//
//    public static double normalizeExpectedOutput(double expected, double max, double min, double upperbound, double lowerbound){
//        double normalizedExpected = 0.0;
//        double localExpected = expected;
//        if(localExpected > max) {
//            localExpected = max;
//        }else if(localExpected < min) {
//            localExpected = min;
//        }
//
//        normalizedExpected = lowerbound +(localExpected-min)*(upperbound-lowerbound)/(max - min);
//
//
//        return normalizedExpected;
//    }
//
//    public static double  remappingOutputToQ (double output, double maxQ, double minQ, double upperbound, double lowerbound) {
//        double remappedQ = 0.0;
//        double currentOutput = output;
//        if(currentOutput < -1.0) {
//            currentOutput = -1.0;
//        }else if(currentOutput > 1.0) {
//            currentOutput = 1.0;
//        }
//        remappedQ = minQ + (currentOutput-lowerbound)/(upperbound-lowerbound)*(maxQ - minQ);
//        return remappedQ;
//    }
//
//    public static double[] getColumn(double[][] array, int index) {
//        double[] column = new double[State.NumStates];
//        for(int i = 0; i< column.length; i++ ) {
//            column[i] = array[i][index];
//        }
//        return column;
//    }
//
//    public static double findMax (double []theValues) {
//        double maxQValue = theValues[0];
//        for (int i = 0; i < theValues.length; i++) {
//            if(maxQValue < theValues[i]) {
//                maxQValue = theValues[i];
//            }
//        }
//        return maxQValue;
//    }
//
//    public static double findMin (double []theValues) {
//        double minQValue = theValues[0];
//        for (int i = 0; i < theValues.length; i++) {
//            if(minQValue > theValues[i]) {
//                minQValue = theValues[i];
//            }
//        }
//        return minQValue;
//    }
//
//    public int getMaxIndex(double [] theValues) {
//        double maxQValue = theValues[0];
//        int maxIndex = 0;
//        for(int i = 0; i < theValues.length; i++) {
//            if(maxQValue < theValues[i]) {
//                maxQValue = theValues[i];
//                maxIndex = i;
//            }
//        }
//        return maxIndex;
//    }
//
//    public void setCurrentActionValue(double theValues, int theIndex) {
//        currentActionOutput[theIndex] = theValues;
//    }
//    public double [] getCurrentActionValues() {
//        return this.currentActionOutput;
//    }
//
//    public void setCurrentQValues(double [] theValues) {
//        currentActionQ = theValues;
//    }
//    public void setCurrentQValue(double theValues, int theIndex) {
//        currentActionQ[theIndex] = theValues;
//    }
//
//    public double [] getCurrentQValues() {
//        return this.currentActionQ;
//    }
//
//    public void setNewQValues(double [] theValues) {
//        newActionQ = theValues;
//    }
//    public void setNewQValue(double theValues, int theIndex) {
//        newActionQ[theIndex] = theValues;
//    }
//    public double [] getNewQValues() {
//        return this.newActionQ;
//    }
//
//    public int [] getNewStateArray(){
//        return this.newStateArray;
//    }
//
//    public void setCurrentActionValues(double [] theValues) {
//        currentActionOutput = theValues;
//    }
//
//
//    public void setNewActionValues(double [] theValues) {
//        newActionOutput = theValues;
//    }
//    public void setNewActionValue(double theValues, int theIndex) {
//        newActionOutput[theIndex] = theValues;
//    }
//    public double [] getNewActionValues() {
//        return this.newActionOutput;
//    }
//    public ArrayList<NeuralNet> getNeuralNetworks(){
//        return this.neuralNetworks;
//
//    }
//
//    public double getQError (int state, int action) {
//        return qError[state][action];
//    }
//
//    public void setQError (int state, int action, double value) {
//        qError[state][action] = value;
//    }

}
