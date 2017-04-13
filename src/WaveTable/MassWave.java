package WaveTable;
/**
 *
 * @author MultiTool
 */
public class MassWave {// relativistic wave table
  public static int NDims = 2;
  public static int NArms = NDims * 2;
  public static class Vector {
    public double[] Loc = new double[NDims];
  }
  public static class IntensityArm {
    public Vector vect;
  }
  public static class IntensityArms {
    public IntensityArm[] arms = new IntensityArm[NArms];
  }
  /*
   then we make a grid.
   a cell has energymass, and the em has a direction.
   one neighbor reads the source's direction and em, and projects the source's directions onto its own arms.
   the em of the source is split up into its shadows on the neighbor's arms.
   but what of things with the same direction but different speeds?
   maybe we can get away with it if everything is moving at C.
  
   */
  public class Grid {

  }
}
