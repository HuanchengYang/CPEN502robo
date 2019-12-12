package robot;

import java.awt.geom.*;

class Target
{
  String name;
  public double bearing;
  public double heading;
  public long currentTime=0;
  public double speed;
  public double x, y;
  public double distance;
  public double changehead;
  public double energy;

  public Point2D.Double guessPosition(long when)
  {
    double diff = when - currentTime;
    double newY, newX;

    if (Math.abs(changehead) > 0.00001)
    {
      double radius = speed/changehead;
      double tothead = diff * changehead;
      newY = y + (Math.sin(heading + tothead) * radius) - (Math.sin(heading) * radius);
      newX = x + (Math.cos(heading) * radius) - (Math.cos(heading + tothead) * radius);
    }
    else {
      newY = y + Math.cos(heading) * speed * diff;
      newX = x + Math.sin(heading) * speed * diff;
    }
    return new Point2D.Double(newX, newY);
  }

}