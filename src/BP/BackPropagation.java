package BP;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BackPropagation extends NeuralNet implements NeuralNetInterface {
    final int numLayer = 1;
    final int argNumOutputs = 1;
    final int biasConstant = 1;


    double [][][] weight;
    double [][] error;
    double [][] value;
    double [][][] weightChange;
    double [][][] newweight;




    public BackPropagation () {
        this.argNumInputs = 2;
        this.argNumHidden = 4;
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

        return error;
    }

    public int trainModel(double[][] X,double []y) {
        double result;
        double totalerror=1;
        double[] error={1,1,1,1};
        int round=0;
        int i=0;
        while (Math.abs(totalerror)>=0.05) {
            result = outputFor(X[i%4]);
            outputError(y[i%4], result);
            weightUpdate(1);
            hiddenErrors();
            weightUpdate(0);
            error[i%4]=(result-y[i%4])*(result-y[i%4]);
            totalerror=(error[0]+error[1]+error[2]+error[3])*1/2;
            i=i+1;
            round+=1;
            System.out.print(round+",");
            System.out.println(totalerror);
            if (round>10000){
                break;
            }
        }
        return round;
    }

    @Override
    public void save(File argFile) {

    }

    @Override
    public void load(File argFile) throws IOException {

    }

}
