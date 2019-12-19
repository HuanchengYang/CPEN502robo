package NeuralNetwork;


import LUT.LUT;
import robot.Action;
import robot.Learner;
import robot.State;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BackPropagationLUT extends NeuralNetLUT implements NeuralNetInterface {
    final int numLayer = 1;
    final int argNumOutputs = 1;
    final int biasConstant = 1;


    double [][][] weight;
    double [][] error;
    double [][] value;
    double [][][] weightChange;
    double [][][] newweight;

    ArrayList<ArrayList<Double>> trainingInput= new ArrayList<ArrayList<Double>>();
    ArrayList<Double> trainingOutput = new ArrayList<Double>();
    double [][] arrayTrainingInput = new double[][]{};
    double [] arrayTrainingOutput = new double[]{};
    double [] normalizedarrayTrainingOutput;





    public BackPropagationLUT() {
        this.argNumInputs = 11;
        this.argNumHidden = 80;
        weight = new double[numLayer+1][][]; //one extra layer for output
        weight[0]= new double[argNumHidden][argNumInputs+biasConstant];
        weight[1]= new double[argNumOutputs][argNumHidden+biasConstant];
        newweight = new double[numLayer+1][][]; //one extra layer for output
        newweight[0]= new double[argNumHidden][argNumInputs+biasConstant];
        newweight[1]= new double[argNumOutputs][argNumHidden+biasConstant];
        weightChange = new double[numLayer+1][][]; //one extra layer for output
        weightChange[0]= new double[argNumHidden][argNumInputs+biasConstant];
        weightChange[1]= new double[argNumOutputs][argNumHidden+biasConstant];
        error = new double[numLayer+1][];
        error[0] = new double[argNumHidden];
        error[1] = new double[argNumOutputs];
        value = new double[numLayer+2][];
        value[0] = new double[argNumInputs+biasConstant];
        value[1] = new double[argNumHidden+biasConstant];
        value[2] = new double[argNumOutputs];
        value[0][argNumInputs]=1; //Initialize Bias
        value[1][argNumHidden]=1; //Initialize Bias
    }




    @Override
    /*
      Return a bipolar sigmoid of the input X
      @param x The input
     * @return f(x) = 2 / (1+e(-x)) - 1
     */
    public double sigmoid(double x) {
        double fx = 2.0 / (1.0+Math.exp(-x)) -1.0;
        return fx;
    }

    public double devsigmoid(double y) {
        return 0.5*(1.0-Math.pow(y,2.0));
    }

/*
      Return a binary sigmoid of the input X
      @param x The input
     * @return f(x) = 2 / (1+e(-x)) - 1
     */

//    public double sigmoid(double x){
//        return 1.0/(1.0+Math.exp(-x));
//    }
//
//    public double devsigmoid(double y) {
//        double dfx = y*(1.0-y);
//        return dfx;
//    }

    public double[] findMaxOutput(double[] state){
        double [] normalizedInput = new double[argNumInputs];
        for (int i=0;i<normalizedInput.length;i++){
            if (i<state.length){
                normalizedInput[i]=state[i];
            } else {
                normalizedInput[i]=0;
            }
        }
        double maxQ = -500;
        double[] selectedAction = new double[4];
        for (double j=-1;j<=1;j=j+0.5){
            for (double k=-1;k<=1;k=k+0.5){
                for (double l=-1;l<=1;l=l+0.5){
                    for (double m=-1;m<=1;m=m+2){
                        normalizedInput[state.length]=j;
                        normalizedInput[state.length+1]=k;
                        normalizedInput[state.length+2]=l;
                        normalizedInput[state.length+3]=m;
                        double expectedQ=this.outputFor(normalizedInput);
                        if (expectedQ>maxQ){
                            maxQ=expectedQ;
                            selectedAction = new double[]{j, k, l, m};
                        }
                    }
                }
            }
        }
        return selectedAction;
    }


    public double findMaxQ(double[] state){
        double [] normalizedInput = new double[argNumInputs];
        for (int i=0;i<normalizedInput.length;i++){
            if (i<state.length){
                normalizedInput[i]=state[i];
            } else {
                normalizedInput[i]=0;
            }
        }
        double maxQ = -500;
        for (double j=-1;j<=1;j=j+0.5){
            for (double k=-1;k<=1;k=k+0.5){
                for (double l=-1;l<=1;l=l+0.5){
                    for (double m=-1;m<=1;m=m+2){
                        normalizedInput[state.length]=j;
                        normalizedInput[state.length+1]=k;
                        normalizedInput[state.length+2]=l;
                        normalizedInput[state.length+3]=m;
                        double expectedQ=this.outputFor(normalizedInput);
                        if (expectedQ>maxQ){
                            maxQ=expectedQ;
                        }
                    }
                }
            }
        }
        return maxQ;
    }


    /**
     * This method implements a general sigmoid with asymptotes bounded by (a,b)
     * @param x The input
     * @return f(x) = b_minus_a / (1 + e(-x)) - minus_a
     */



    @Override
    public double customSigmoid(double x) {
        double fx;
        fx = (argB-argA) / (1+Math.exp(-x)) + argA;
        return fx;
    }



    /**
     * Initialize the weights to random values.
     * For say 2 inputs, the input vector is [0] & [1]. We add [2] for the bias.
     * Like wise for hidden units. For say 2 hidden units which are stored in an array.
     * [0] & [1] are the hidden & [2] the bias.
     * We also initialise the last weight change arrays. This is to implement the alpha term.
     */

    @Override
    public void initializeWeights() {
        for (int k=0;k<weight.length;k++) {
            for (int j = 0; j < weight[k].length; j++) {
                for (int i = 0; i < weight[k][j].length; i++) {
                    weight[k][j][i] = Math.random() - 0.5;
                }
            }
        }

        for (int k=0;k<newweight.length;k++) {
            for (int j = 0; j < newweight[k].length; j++) {
                for (int i = 0; i < newweight[k][j].length; i++) {
                    newweight[k][j][i] = weight[k][j][i];
                }
            }
        }

        for (int k=0;k<weightChange.length;k++) {
            for (int j = 0; j < weightChange[k].length; j++) {
                Arrays.fill(weightChange[k][j], 0);
            }
        }
    }

    /**
     54 * Initialize the weights to 0.
     55 */

    @Override
    public void zeroWeights() {
        for (int k=0;k<weight.length;k++) {
            for (int j = 0; j < weight[k].length; j++) {
                Arrays.fill(weight[k][j], 0);
            }
        }

    }

    public double outputError(double actualY,double predictY){
        double Oerror = (actualY-predictY)*devsigmoid(predictY);
        this.error[numLayer][argNumOutputs-1]=Oerror;
        return Oerror;
    }

    //Compute all errors in hidden layer neuron
    public void hiddenErrors(){
        for (int i=0;i<argNumHidden;i++){
            hiddenError(value[1][i],i,0);
        }
    }

    private void hiddenError(double predictY,int index,int layer){
        double Zerror;
        double sumPreError = 0;
//        for (int i=0;i<error[layer+1].length;i++) {
//            sumPreError += error[layer+1][i] * weight[layer+1][argNumOutputs-1][index];
//        }
        Zerror=devsigmoid(predictY)*error[numLayer][argNumOutputs-1]*weight[1][0][index];
        this.error[layer][index]=Zerror;
    }

    public void weightUpdate(int weightLayer){

            for (int j = weight[weightLayer].length-1; j >=0; j--) {
                for (int i = weight[weightLayer][j].length-1; i >=0; i--) {
                    newweight[weightLayer][j][i] = weight[weightLayer][j][i]+argMomentumTerm*weightChange[weightLayer][j][i]+argLearningRate*error[weightLayer][j]*value[weightLayer][i];
                    weightChange[weightLayer][j][i] = newweight[weightLayer][j][i]-weight[weightLayer][j][i];
                    weight[weightLayer][j][i]=newweight[weightLayer][j][i];
                }
            }

    }



    @Override
    public double outputFor(double[] X) {
        double output=0;
        for (int m=0;m<X.length;m++){
            value[0][m]=X[m];
        }
        for (int i=0;i<argNumHidden;i++){
            for (int j=0;j<=argNumInputs;j++){
                output+=value[0][j]*weight[0][i][j];
            }
            value[1][i]=sigmoid(output);
            output=0;
        }
        output=0;
        for (int k=0;k<=argNumHidden;k++){
            output+=value[1][k]*weight[1][0][k];
        }
        value[2][0]=sigmoid(output);
        return value[2][0];
    }

    @Override
    public double train(double[] X, double argValue) {
        double predictY=outputFor(X);
        double error=Math.pow((predictY-argValue),2);

        outputError(argValue, predictY);
        weightUpdate(1);
        hiddenErrors();
        weightUpdate(0);
        error=(predictY-argValue)*(predictY-argValue);
        //System.out.println("Error is"+error);
        return error;
    }

    public void initializeTraining(LUT lut){
        Learner newlearn = new Learner(lut);
        for (int i=0;i< State.NumofState;i++){
            for (int j=0;j< Action.NumRobotActions;j++){
                if (lut.getQValue(i,j)!=0.0){
                    double[] state = newlearn.convertIntStatetoArray(i);
                    double[] action= newlearn.convertIntActiontoArray(j);
                    double[] normalizeInput = newlearn.normalizeInput(state,action);
                    ArrayList<Double> NetworkInput = convertArrayToArrayList(normalizeInput);
                    trainingInput.add(NetworkInput);
                    trainingOutput.add(lut.getQValue(i,j));
                }
            }
        }
        arrayTrainingInput = convert2DArrayListTo2DArray(trainingInput);
        arrayTrainingOutput = convertArrayListToArray(trainingOutput);
        normalizedarrayTrainingOutput=normalizeOutput(arrayTrainingOutput);


        trainModel(arrayTrainingInput,normalizedarrayTrainingOutput);
    }

    public void updateValueAndRetrain(double[] state, double[] action, double newQvalue){
        double[] normalizeInput = normalizeInput(state,action);
        double normalizedQvalue = normalizeQ(newQvalue);

        ArrayList<Double> NewInput = convertArrayToArrayList(normalizeInput);
        int n=10;
        if (trainingOutput.size()<n) {
            trainingInput.add(NewInput);
            trainingOutput.add(normalizedQvalue);
        } else {
            trainingOutput.remove(0);
            trainingInput.remove(0);
            trainingInput.add(NewInput);
            trainingOutput.add(normalizedQvalue);
        }
        arrayTrainingInput = convert2DArrayListTo2DArray(trainingInput);
        arrayTrainingOutput = convertArrayListToArray(trainingOutput);
        normalizedarrayTrainingOutput=normalizeOutput(arrayTrainingOutput);

        for (int i=0;i<arrayTrainingOutput.length;i++){
            train(arrayTrainingInput[i],normalizedarrayTrainingOutput[i]);
        }
    }

    private double normalizeQ(double newQvalue) {
        double normalizedQ=newQvalue/10;
        if (normalizedQ>1){
            normalizedQ=1;
        }
        if (normalizedQ<-1){
            normalizedQ=-1;
        }
        return normalizedQ;
    }

    public double[] normalizeInput (double[] state, double[] action){
        double[] normalizedstate = normalizestate(state);
        double[] normalizedaction = normalizeaction(action);
        double [] normalizedInput = new double[normalizedstate.length+normalizedaction.length];
        for (int i=0;i<normalizedInput.length;i++){
            if (i<normalizedstate.length){
                normalizedInput[i]=normalizedstate[i];
            } else {
                normalizedInput[i]=normalizedaction[i-normalizedstate.length];
            }
        }
        return normalizedInput;
    }

    public double[] normalizeOutput (double[] input){
        double[] output = new double[input.length];
        for (int i=0;i<input.length;i++){
            output[i] = input[i]/10;
            if (output[i]<-1){
                output[i]=-1;
            }
            if (output[i]>1){
                output[i]=1;
            }
        }
        return output;
    }

    public static double [] normalizestate(double [] states) {
        double [] normalizedStates = new double [6];
        for(int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    normalizedStates[0] = -1.0 + states[0] *2.0/((double)(State.NumHeading-1));
                    break;
                case 1:
                    normalizedStates[1] = -1.0 + states[1] *2.0/((double)(State.NumTargetDistance-1));;
                    break;
                case 2:
                    normalizedStates[2] = -1.0 + states[2] *2.0/((double)(State.NumTargetBearing-1));;
                    break;
                case 3:
                    normalizedStates[3] = -1.0 + states[3] *2.0;
                    break;
                case 4:
                    normalizedStates[4] = -1.0 + states[4] *2.0;
                    break;
                case 5:
                    normalizedStates[5] = -1.0 + states[5] *2.0;
                    break;
                default:
                    System.out.println("The data doesn't belong here.");
            }
        }
        return normalizedStates;
    }


    public static double [] normalizeaction(double [] actions) {
        double [] normalizedActions = new double [5];
        for(int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    normalizedActions[0] = -1.0 + actions[0] *2.0/((double)(Action.RobotAhead-1));
                    break;
                case 1:
                    normalizedActions[1] = -1.0 + actions[1] *2.0/((double)(Action.RobotBack-1));
                    break;
                case 2:
                    normalizedActions[2] = -1.0 + actions[2] *2.0/((double)(Action.RobotAheadTurnLeft-1));
                    break;
                case 3:
                    normalizedActions[3] = -1.0 + actions[3] *2.0/((double)(Action.RobotAheadTurnRight-1));
                    break;
                case 4:
                    normalizedActions[4] = -1.0 + actions[4] *2.0;
                    break;
                default:
                    System.out.println("The data doesn't belong here.");
            }
        }
        return normalizedActions;
    }



    private boolean deepEquals(double[] array1,double[] array2){
        if (array1.length !=array2.length){
            return false;
        }
        for (int i=0;i<array1.length;i++){
            if (array1[i]!=array2[i]){
                return false;
            }
        }
        return true;
    }

    private ArrayList<Double> convertArrayToArrayList(double[] input) {
        ArrayList<Double> output= new ArrayList<Double>();
        for (int i=0;i<input.length;i++){
            output.add(input[i]);
        }
        return output;
    }

    private double[] convertArrayListToArray(ArrayList<Double> input) {
        double[] output= new double[input.size()];
        for (int i=0;i<output.length;i++){
            output[i]= input.get(i);
        }
        return output;
    }

    private double[][] convert2DArrayListTo2DArray(ArrayList<ArrayList<Double>> input) {
        double[][] output= new double[input.size()][11];
        for (int i=0;i<input.size();i++){
            for (int j=0;j<input.get(i).size();j++){
                output[i][j]=input.get(i).get(j);
            }
        }
        return output;
    }

    public int trainModel(double[][] X,double []y) {
        double result;
        double totalerror=1;
        double[] error=new double[y.length];
        Arrays.fill(error,1.0);
        int round=0;
        int i=0;
        while (Math.abs(totalerror)>=1) {
            result = outputFor(X[i%y.length]);
            outputError(y[i%y.length], result);
            weightUpdate(1);
            hiddenErrors();
            weightUpdate(0);
            error[i%X.length]=(result-y[i%y.length])*(result-y[i%y.length]);
            totalerror = calculatetotalerror(error);
            i=i+1;
            round+=1;
            System.out.print(round+",");
            System.out.println(totalerror);
            if (round>50000){
                break;
            }
        }
        return round;
    }

    private double calculatetotalerror(double[] error){
        double totalerror=0;
        for (int i=0;i<error.length;i++){
            totalerror=totalerror+error[i];
        }
        totalerror=Math.sqrt(totalerror);
        return totalerror;
    }

    @Override
    public void save(File argFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < weight.length; i++)//for each row
        {
            for(int j = 0; j < weight[i].length; j++)//for each column
            {
                for(int k=0;k<weight[i][j].length;k++) {
                    builder.append(i+",");
                    builder.append(j+",");
                    builder.append(k+",");
                    builder.append(weight[i][j][k] + "");//append to the output string
                    builder.append("\n");//append remark a at the end of the row
                }
            }

        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(argFile));
        writer.write(builder.toString());//save the string representation of the board
        writer.close();

    }

    @Override
    public void load(File argFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(argFile));
        String line = "";
        while((line = reader.readLine()) != null)
        {
            String[] cols = line.split(","); //note that if you have used space as separator you have to split on " "
            int i=Integer.parseInt(cols[0]);
            int j=Integer.parseInt(cols[1]);
            int k=Integer.parseInt(cols[2]);
            double w=Double.parseDouble(cols[3]);
            weight[i][j][k]=w;
        }
        reader.close();
    }

}
