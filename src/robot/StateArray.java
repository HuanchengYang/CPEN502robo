package robot;

import java.util.Arrays;

public class StateArray {

    private double[] state;
    private int numState=6;


    public StateArray(double[] state) {
        this.state = state;
    }

    public double[] getActionArray (){
        return this.state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateArray that = (StateArray) o;
        boolean equals = true;
        for (int i=0;i<numState;i++){
            if(((StateArray) o).state[i]!=this.state[i]){
                equals=false;
                return equals;
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(state);
    }
}
