package robot;

public class State {

    public static final int NumHeading = 4;
    public static final int NumTargetDistance = 10;
    public static final int NumTargetBearing = 4;
    public static final int NumHorizontalHitWall = 2;
    public static final int NumVerticalHitWall = 2;
    public static final int NumIsHit = 2;
    public static final int NumofState = NumHeading*NumTargetDistance*NumTargetBearing*NumHorizontalHitWall*NumVerticalHitWall*NumIsHit;


    public static double getHeading(double heading)
    {
        double angle = 360 / NumHeading;
        double newHeading = heading + angle / 2;
        if (newHeading > 360.0)
            newHeading -= 360.0;
        return (newHeading / angle);
    }

    public static double getTargetDistance(double value)
    {
        //int distance = (int)(value / (1000/NumTargetDistance));
        double distance = (value / (1000/NumTargetDistance));
        if (distance > NumTargetDistance - 1)
            distance = NumTargetDistance - 1;
        return distance;
    }

    public static double getTargetBearing(double bearing)
    {
        double PIx2 = Math.PI * 2;
        if (bearing < 0)
            bearing = PIx2 + bearing;
        double angle = PIx2 / NumTargetBearing;
        double newBearing = bearing + angle / 2;
        if (newBearing > PIx2)
            newBearing -= PIx2;
        return (newBearing / angle);
    }

    public static int HorizontalHitWall(double robotX, double BattleFieldX)  {
        int distanceToCenterH;
        if (robotX > 50 && robotX < BattleFieldX-50 ) {
            distanceToCenterH = 0;	// Safe
        } else {
            distanceToCenterH = 1;	// unSafe, too close to wall
        }
        return distanceToCenterH;
    }

    public static int VerticalHitWall(double robotY, double BattleFieldY)  {
        int distanceToCenterV;
        if (robotY > 50 && robotY < BattleFieldY-50 ) {
            distanceToCenterV = 0;	// Safe
        } else {
            distanceToCenterV = 1;	// unSafe, too close to wall
        }
        return distanceToCenterV;
    }
}