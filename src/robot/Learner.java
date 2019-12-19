package robot;

import LUT.LUT;
import NeuralNetwork.BackPropagationLUT;
//import NeuralNet.NeuralNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Learner {
    public static final double LearningRate = 0.1;
    public static final double DiscountRate = 0.2;
    private boolean initialize = true;
    private LUT lut;
    private static int typeofState = 6;
    private static int typeofAction = 5;

    //private ArrayList<NeuralNet> neuralNetworks = new ArrayList<NeuralNet>();

    private double[] currentStateArray = new double[typeofState];
    private double[] newStateArray = new double[typeofState];
    private double[] currentActionArray = new double[typeofAction];
    private double[] newActionArray = new double[typeofAction];
    private double[] currentActionQ = new double[Action.NumRobotActions];
    private double[] newActionQ = new double[Action.NumRobotActions];
    private double qError[][] = new double[State.NumofState][Action.NumRobotActions];


    HashMap<StateArray, Double> DoublestateMap = new HashMap<StateArray, Double>();
    HashMap<ActionArray, Double> DoubleactionMap = new HashMap<ActionArray, Double>();
    HashMap<Double, double[]> ArraystateMap = new HashMap<Double, double[]>();
    HashMap<Double, double[]> ArrayactionMap = new HashMap<Double, double[]>();

    public BackPropagationLUT BPLUT1;


    public Learner(LUT lut) {
        this.lut = lut;
        initializeActionHash();
        initializeStateHash();

    }

    public Learner() {
        BPLUT1 = new BackPropagationLUT();

        initializeActionHash();
        initializeStateHash();
    }

    //Off-Policy
    public void learnOffPolicy(double newstate[], double newaction[], double immediateReward) {
        if (initialize) {
            initialize = false;
            currentStateArray = newstate;
            currentActionArray = newaction;
        } else {

            double oldStateInt = convertArrayStatetoInt(currentStateArray);
            double newStateInt = convertArrayStatetoInt(newstate);
            double oldActionInt = convertArrayActiontoInt(currentActionArray);
            double newActionInt = convertArrayActiontoInt(newaction);

            double oldQValue = lut.getQValue(oldStateInt, oldActionInt);
            double newQValue = (1 - LearningRate) * oldQValue + LearningRate * (immediateReward + DiscountRate * lut.getMaximumQ(newStateInt));
            lut.trainTable(oldStateInt, oldActionInt, newQValue);
            currentStateArray = newstate;
            currentActionArray = newaction;
        }
    }

    //On Policy
    public void learnOnPolicy(double newstate[], double newaction[], double immediateReward) {
        if (initialize) {
            initialize = false;
            currentStateArray = newstate;
            currentActionArray = newaction;
        } else {
            double oldStateInt = convertArrayStatetoInt(currentStateArray);
            double newStateInt = convertArrayStatetoInt(newstate);
            double oldActionInt = convertArrayActiontoInt(currentStateArray);
            double newActionInt = convertArrayActiontoInt(newaction);

            double oldQValue = lut.getQValue(oldStateInt, oldActionInt);
            double newQValue = (1 - LearningRate) * oldQValue + LearningRate * (immediateReward + DiscountRate * lut.getQValue(newStateInt, newActionInt));
            lut.trainTable(oldStateInt, oldActionInt, newQValue);
            currentStateArray = newstate;
            currentActionArray = newaction;
        }
    }


    private void initializeStateHash() {
        double statecount = 0;
        for (double i = 0; i < State.NumHeading; i++) {
            for (double j = 0; j < State.NumTargetDistance; j++) {
                for (double k = 0; k < State.NumTargetBearing; k++) {
                    for (double l = 0; l < State.NumVerticalHitWall; l++) {
                        for (double m = 0; m < State.NumHorizontalHitWall; m++) {
                            for (double n = 0; n < State.NumIsHit; n++) {
                                double[] testArray = new double[]{i, j, k, l, m, n};
                                StateArray testStateArray = new StateArray(testArray);
                                this.DoublestateMap.put(testStateArray, statecount);
                                this.ArraystateMap.put(statecount, testArray);
                                statecount++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void initializeActionHash() {
        double actioncount = 0;
        for (double i = 0; i < Action.RobotAhead; i++) {
            for (double j = 0; j < Action.RobotBack; j++) {
                for (double k = 0; k < Action.RobotAheadTurnLeft; k++) {
                    for (double l = 0; l < Action.RobotAheadTurnRight; l++) {
                        for (double m = 0; m < Action.RobotFire; m++) {
                            double[] testAction = new double[]{i, j, k, l, m};
                            ActionArray testActionArray = new ActionArray(testAction);
                            this.DoubleactionMap.put(testActionArray, actioncount);
                            this.ArrayactionMap.put(actioncount, testAction);
                            actioncount++;
                        }
                    }
                }
            }
        }
    }

    public double convertArrayStatetoInt(double[] state) {
        StateArray stateArray = new StateArray(state);
        return DoublestateMap.get(stateArray);
    }

    public double[] convertIntStatetoArray(double state) {
        return ArraystateMap.get(state);
    }

    public double convertArrayActiontoInt(double[] action) {
        ActionArray actionArray = new ActionArray(action);
        return DoubleactionMap.get(actionArray);
    }

    public double[] convertIntActiontoArray(double action) {
        return ArrayactionMap.get(action);
    }




    public double[] normalizeInput(double[] state, double[] action) {
        double[] normalizedstate = normalizestate(state);
        double[] normalizedaction = normalizeaction(action);
        double[] normalizedInput = new double[normalizedstate.length + normalizedaction.length];
        for (int i = 0; i < normalizedInput.length; i++) {
            if (i < normalizedstate.length) {
                normalizedInput[i] = normalizedstate[i];
            } else {
                normalizedInput[i] = normalizedaction[i - normalizedstate.length];
            }
        }
        return normalizedInput;
    }


    public static double[] normalizestate(double[] states) {
        double[] normalizedStates = new double[6];
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    normalizedStates[0] = -1.0 + states[0] * 2.0 / ((double) (State.NumHeading - 1));
                    break;
                case 1:
                    normalizedStates[1] = -1.0 + states[1] * 2.0 / ((double) (State.NumTargetDistance - 1));
                    ;
                    break;
                case 2:
                    normalizedStates[2] = -1.0 + states[2] * 2.0 / ((double) (State.NumTargetBearing - 1));
                    ;
                    break;
                case 3:
                    normalizedStates[3] = -1.0 + states[3] * 2.0;
                    break;
                case 4:
                    normalizedStates[4] = -1.0 + states[4] * 2.0;
                    break;
                case 5:
                    normalizedStates[5] = -1.0 + states[5] * 2.0;
                    break;
                default:
                    System.out.println("The data doesn't belong here.");
            }
        }
        return normalizedStates;
    }


    public static double[] normalizeaction(double[] actions) {
        double[] normalizedActions = new double[5];
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    normalizedActions[0] = -1.0 + actions[0] * 2.0 / ((double) (Action.RobotAhead - 1));
                    break;
                case 1:
                    normalizedActions[1] = -1.0 + actions[1] * 2.0 / ((double) (Action.RobotBack - 1));
                    break;
                case 2:
                    normalizedActions[2] = -1.0 + actions[2] * 2.0 / ((double) (Action.RobotAheadTurnLeft - 1));
                    break;
                case 3:
                    normalizedActions[3] = -1.0 + actions[3] * 2.0 / ((double) (Action.RobotAheadTurnRight - 1));
                    break;
                case 4:
                    normalizedActions[4] = -1.0 + actions[4] * 2.0;
                    break;
                default:
                    System.out.println("The data doesn't belong here.");
            }
        }
        return normalizedActions;
    }

    public void onlineLearning(double[] state, double[] action, double immediateReward) {
        if (initialize) {
            initialize = false;
            currentStateArray = state;
            currentActionArray = action;
        } else {
            double[] normalizedOldInput = normalizeInput(currentStateArray, currentActionArray);
            double oldQValue = BPLUT1.outputFor(normalizedOldInput);
            double newQValue = (1 - LearningRate) * oldQValue + LearningRate * (immediateReward + DiscountRate * getMaxQ(state));
            //lut.trainTable(oldStateInt,oldActionInt,newQValue);
            BPLUT1.updateValueAndRetrain(currentStateArray, currentActionArray, newQValue);
            currentStateArray = state;
            currentActionArray = action;
        }

    }

    public double[] getBestAction(double[] state) {
        double[] rawaction = new double[5];
        double maxQ = -1;
        double[] outputaction = new double[5];
        for (double i = 0; i <= 5; i = i + 1) {
            for (double j = 0; j <= 5; j = j + 1) {
                for (double k = 0; k <= 5; k = k + 1) {
                    for (double l = 0; l <= 5; l = l + 1) {
                        for (double m = 0; m <= 1; m++) {
                            rawaction[0] = i;
                            rawaction[1] = j;
                            rawaction[2] = k;
                            rawaction[3] = l;
                            rawaction[4] = m;
                            double[] stateandaction = new double[11];
                            stateandaction = normalizeInput(state, rawaction);
                            double Qvalue = BPLUT1.outputFor(stateandaction);
                            if (Qvalue > maxQ) {
                                maxQ = Qvalue;
                                outputaction = rawaction;
                            }
                        }
                    }
                }
            }
        }
        return outputaction;
    }

    public double getMaxQ(double[] state) {
        double[] rawaction = new double[5];
        double maxQ = -1;
        double[] outputaction = new double[5];
        for (double i = 0; i <= 5; i = i +1) {
            for (double j = 0; j <= 5; j = j + 1) {
                for (double k = 0; k <= 5; k = k + 1) {
                    for (double l = 0; l <= 5; l = l + 1) {
                        for (double m = 0; m <= 1; m++) {
                            rawaction[0] = i;
                            rawaction[1] = j;
                            rawaction[2] = k;
                            rawaction[3] = l;
                            rawaction[4] = m;
                            double[] stateandaction = new double[11];
                            stateandaction = normalizeInput(state, rawaction);
                            double Qvalue = BPLUT1.outputFor(stateandaction);
                            if (Qvalue > maxQ) {
                                maxQ = Qvalue;
                            }
                        }
                    }
                }
            }
        }
        return maxQ*10;
    }

    public double[] getRandomAction() {
        double[] action=new double[5];
        action[0]=Math.random()*5;
        action[1]=Math.random()*5;
        action[2]=Math.random()*5;
        action[3]=Math.random()*5;
        action[4]=Math.round(Math.random());
        return action;
    }
}
