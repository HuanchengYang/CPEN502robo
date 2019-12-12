package robot;

import java.util.Arrays;

public class ActionArray {

    private double[] action;
    private int numAction=5;


    public ActionArray(double[] action) {
        this.action = action;
    }

    public double[] getActionArray (){
        return this.action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionArray that = (ActionArray) o;
        boolean equals = true;
        for (int i=0;i<numAction;i++){
            if(that.action[i]!=this.action[i]){
                equals=false;
                return equals;
            }
        }
        return true;


    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(action);
    }
}
