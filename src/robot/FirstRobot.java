package robot;

import LUT.LUT;
import robocode.*;


import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;


public class FirstRobot extends AdvancedRobot {
    private static final int robotMoveDistance = 80;
    private static final double robotTurnDegree = 10.0;

    private Target target;
    private double oppoDist, oppoBearing;
    private boolean found = false;
    private static LUT lut;
    private double firePower;
    private int isHitByBullet = 0;
    private Learner learner;
    private static int numState=6;
    private static int numAction=5;

    double[] state;
    double [] action;
    private double immediateReward=0;
    private double accumulateReward=0;
    private boolean willFire = false;


    /**
     * run:  Fire's main run function
     */
    public void run() {
        // Set colors
        setBodyColor(Color.orange);
        setGunColor(Color.orange);
        setRadarColor(Color.red);
        setScanColor(Color.red);
        setBulletColor(Color.red);

        //TODO:Reconstruct the LUT
        lut = new LUT();
        loadData();
        learner = new Learner(lut);
        target = new Target();
        target.distance = 100000;
        //END

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        execute();
        turnRadarRightRadians(2 * Math.PI);
        state = getState();
        while (true) {
            action = robotMovement(state, lut);
            firePower = 400 / target.distance;
            if (firePower > 3)
                firePower = 3;
            radarMovement(target);
            gunMovement();
            if (willFire) {
                setFire(firePower);
            }
            //END
            execute();
            state = getState();
            //System.out.println("immediatereward is "+immediateReward);
            learner.learnOffPolicy(state,action,immediateReward);
            accumulateReward += immediateReward;
            immediateReward=0;
        }
    }


    private double[] robotMovement(double[] state, LUT lut) {

        double actionInt;
        double stateInt=learner.convertArrayStatetoInt(state);
        actionInt = lut.getBestAction(stateInt);
        System.out.println("Selected Best Action is: "+actionInt);
        double[] action = learner.convertIntActiontoArray(actionInt);

        if (action[0] > 0) {
            setAhead((action[0]) * robotMoveDistance);
        }
        if (action[1] > 0) {
            setBack((action[1]) * robotMoveDistance);
        }

        if (action[2] > 0) {
            setTurnLeft(action[2] * robotTurnDegree);
            setAhead(robotMoveDistance);
        }

        if (action[3] > 0) {
            setTurnRight(action[3] * robotTurnDegree);
            setAhead(robotMoveDistance);
        }

        if (action[4] > 0) {
            willFire=true;
        }
        return action;
    }

    //TODO improve state class
    private double[] getState() {
        int heading = State.getHeading(getHeading());
        int targetDistance = State.getTargetDistance(target.distance);
        int targetBearing = State.getTargetBearing(target.bearing);
        int HorizontalNSafe = State.HorizontalHitWall(getX(),getBattleFieldWidth());
        int VerticalNSafe = State.VerticalHitWall(getY(), getBattleFieldHeight());
        double[] state = {heading, targetDistance, targetBearing, HorizontalNSafe, VerticalNSafe,isHitByBullet};
        return state;
    }

    private void radarMovement(Target target) {
        double radarOffset;
        if (getTime() - target.currentTime > 4) {
            radarOffset = 4 * Math.PI;
        } else {

            radarOffset = getRadarHeadingRadians() - (Math.PI / 2 - Math.atan2(target.y - getY(), target.x - getX()));

            radarOffset = NormaliseBearing(radarOffset);
            if (radarOffset < 0)
                radarOffset -= Math.PI / 10;
            else
                radarOffset += Math.PI / 10;
        }
        setTurnRadarLeftRadians(radarOffset);
    }

    private void gunMovement() {
        long time;
        long nextTime;
        Point2D.Double p;
        p = new Point2D.Double(target.x, target.y);
        for (int i = 0; i < 20; i++) {
            nextTime = (int) Math.round((getrange(getX(), getY(), p.x, p.y) / (20 - (3 * firePower))));
            time = getTime() + nextTime - 10;
            p = target.guessPosition(time);
        }
        double gunOffset = getGunHeadingRadians() - (Math.PI / 2 - Math.atan2(p.y - getY(), p.x - getX()));
        setTurnGunLeftRadians(NormaliseBearing(gunOffset));
    }

    double NormaliseBearing(double ang) {
        if (ang > Math.PI)
            ang -= 2 * Math.PI;
        if (ang < -Math.PI)
            ang += 2 * Math.PI;
        return ang;
    }


    public double getrange(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double h = Math.sqrt(xo * xo + yo * yo);
        return h;
    }


    public void onBulletHit(BulletHitEvent e) {
        if (target.name.equals(e.getName())) {

            double change = e.getBullet().getPower() * 5;
            immediateReward += change;
        }
    }


    public void onBulletMissed(BulletMissedEvent e) {
        double change = -e.getBullet().getPower();
        immediateReward += change;
    }

    public void onHitByBullet(HitByBulletEvent e) {
        if (target.name.equals(e.getName())) {
            double power = e.getBullet().getPower();
            double change = -5 * power;
            immediateReward += change;
        }
        isHitByBullet = 1;
    }

    public void onHitRobot(HitRobotEvent e) {
        if (target.name.equals(e.getName())) {
            double change = -6.0;
            immediateReward += change;
        }
    }

    public void onHitWall(HitWallEvent e) {
        double change = -5;
        immediateReward += change;
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        found = true;
        oppoDist = e.getDistance();
        oppoBearing = e.getBearing();
        if ((e.getDistance() < target.distance) || (target.name.equals(e.getName()))) {
            double absbearing_rad = (getHeadingRadians() + e.getBearingRadians()) % (2 * Math.PI);
            target.name = e.getName();
            double h = NormaliseBearing(e.getHeadingRadians() - target.heading);
            h = h / (getTime() - target.currentTime);
            target.changehead = h;
            target.x = getX() + Math.sin(absbearing_rad) * e.getDistance();
            target.y = getY() + Math.cos(absbearing_rad) * e.getDistance();
            target.bearing = e.getBearingRadians();
            target.heading = e.getHeadingRadians();
            target.currentTime = getTime();
            target.speed = e.getVelocity();
            target.distance = e.getDistance();
            target.energy = e.getEnergy();
        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if (e.getName().equals(target.name))
            target.distance = 10000;
    }

    public void onWin(WinEvent event) {
        accumulateReward +=100;
        saveData();
        System.out.println("Win!");
        int winningFlag=1;

        PrintStream w = null;
        try {
            w = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true));
            w.println(accumulateReward +" \t"+getRoundNum()+" \t"+winningFlag+" \t"+Learner.ExploitationRate);
            if (w.checkError())
                System.out.println("Could not save the data!");
            w.close();
        }
        catch (IOException e) {
            System.out.println("IOException trying to write: " + e);
        }
        finally {
            try {
                if (w != null)
                    w.close();
            }
            catch (Exception e) {
                System.out.println("Exception trying to close writer: " + e);
            }
        }
    }

    public void onDeath(DeathEvent event) {
        accumulateReward -=100;
        saveData();
        System.out.println("Lose!");
        int losingFlag=0;
//        PrintStream w = null;
//        try {
//            w = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true));
//            w.println(accumulateReward +" \t"+getRoundNum()+" \t"+losingFlag+" \t"+Learner.ExploitationRate);
//            if (w.checkError())
//                System.out.println("Could not save the data!");
//            w.close();
//        }
//        catch (IOException e) {
//            System.out.println("IOException trying to write: " + e);
//        }
//        finally {
//            try {
//                if (w != null)
//                    w.close();
//            }
//            catch (Exception e) {
//                System.out.println("Exception trying to close writer: " + e);
//            }
//        }
    }

    public void loadData() {
        try {
            File file = getDataFile("lut4.txt");
            lut.load(file);
        } catch (Exception e) {
        }
    }

    public void saveData() {
        try {
            File file = getDataFile("lut4.txt");
            lut.save(file);
        } catch (Exception e) {
            out.println("Exception trying to write: " + e);
        }
    }
}
