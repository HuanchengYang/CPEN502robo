package LUT;

import robocode.Robocode;
import robocode.RobocodeFileOutputStream;
import robot.State;
import robot.Action;

import java.io.*;
import java.lang.reflect.GenericDeclaration;
import java.util.HashMap;
import java.util.Random;

public class LUT implements LUTInterface {

    private double [][] LUT;

    public LUT(){
        LUT = new double [State.NumofState][Action.NumRobotActions];
        initialiseLUT();
    }


    @Override
    public void initialiseLUT() {
        for (int stateN = 0; stateN < State.NumofState; stateN++)
            for (int actionN = 0; actionN < Action.NumRobotActions; actionN++)
                LUT[stateN][actionN] = 0.0;
    }

    @Override
    public int indexFor(double[] X) {
        return 0;
    }



    public double getMaximumQ(double state) {
        int currentState = (int)Math.round(state);
        double maxQ = Double.NEGATIVE_INFINITY;
        for(int actionN = 0; actionN < this.LUT[currentState].length; actionN++) {
            if (LUT[currentState][actionN] > maxQ)
                maxQ = this.LUT[currentState][actionN];
        }
        return maxQ;
    }

    public double getQValue(double state, double action) {
        int currentState = (int)Math.round(state);
        int currentAction = (int)Math.round(action);
        return this.LUT[currentState][currentAction];
    }

    public double getBestAction(double state) {
        int currentState = (int)Math.round(state);
        double maxQ = -500;
        double bestAction=0;
        for(int actionN = 0; actionN < this.LUT[currentState].length; actionN++) {
            if (LUT[currentState][actionN] > maxQ) {
                maxQ = this.LUT[currentState][actionN];
                System.out.println("The current maxQ is" + maxQ);
                bestAction = actionN;
            }
        }
        return bestAction;
    }

    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    @Override
    public double train(double[] X, double argValue) {
        return 0;
    }

    public void trainTable(double state, double action, double Qvalue) {
        int currentState = (int)Math.round(state);
        int currentAction = (int)Math.round(action);
        LUT[currentState][currentAction]=Qvalue;
    }

    @Override
    public void save(File argFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < LUT.length; i++)//for each row
        {
            for(int j = 0; j < LUT[i].length; j++)//for each column
            {
                builder.append(LUT[i][j]+"");//append to the output string
                if(j < LUT[i].length - 1)//if this is not the last row element
                    builder.append(",");//then add comma (if you don't like commas you can use spaces)
            }
            builder.append("\n");//append new line at the end of the row
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(argFile));
        writer.write(builder.toString());//save the string representation of the board
        writer.close();

    }
    @Override
    public void load(File argFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(argFileName));
        String line = "";
        int row = 0;
        while((line = reader.readLine()) != null)
        {
            String[] cols = line.split(","); //note that if you have used space as separator you have to split on " "
            int col = 0;
            for(String  c : cols)
            {
                LUT[row][col] = Double.parseDouble(c);
                col++;
            }
            row++;
        }
        reader.close();
    }

}
