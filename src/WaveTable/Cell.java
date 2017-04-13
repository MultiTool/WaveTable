/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WaveTable;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author MultiTool
 */
public class Cell {
  public static final int NDims = 2;
  public static final int NAxis = 2;
  public static final int NumNbrs = NDims * NAxis;
  public double TRate = 0.01;// 0.0001;// time rate
//  public double TRate = 0.0001;// time rate
  // z component of vector potential and its first derivative
  public double prev_az, az = 0.0, dazdt, dazdt2;
  double jx, jy;// current
  public Cell[][] NbrCross;// another way
  public ArrayList<Cell> Neighbors;// deprecated
  private double damp = 1.0;
  public double Tension = 0.0, Speed = 0.0;// Speed is also used as inertia
  public double Basis;// average Amp of all my neighbors
  public double Prev_Amp = 0.0, Amp = 0.0, Next_Amp = 0.0;
  public double Damp = 1.0;
  double[] PoyntingVect = new double[NDims];
  public double XLoc = 0, YLoc = 0, Wdt = 10, Hgt = 10;// for drawing
  public double XCtr = 0.0, YCtr = 0.0;
  /* ********************************************************************************* */
  public Cell() {
    this.Neighbors = new ArrayList<Cell>();
    NbrCross = new Cell[NDims][NAxis];
    for (int dcnt = 0; dcnt < NDims; dcnt++) {
      for (int axcnt = 0; axcnt < NAxis; axcnt++) {
        NbrCross[dcnt][axcnt] = null;
      }
    }
  }
  /* ********************************************************************************* */
  public void Assign_Box(double XLoc0, double YLoc0, double Wdt0, double Hgt0) {
    this.XLoc = XLoc0;
    this.YLoc = YLoc0;
    this.Wdt = Wdt0;
    this.Hgt = Hgt0;
    this.XCtr = this.XLoc + (this.Wdt / 2.0);
    this.YCtr = this.YLoc + (this.Hgt / 2.0);
  }
  /* ********************************************************************************* */
  public double Calc_Energy_Vector(int Dim) {
    /* 
     If I understand correctly, Falstad seems to calculate the direction of energy flow (Poynting vector) by simply getting the difference between the speeds 
     of two opposite neighbors, and then multiplying by the current amp (height value) of the cell.
     By 'speed' I mean the rate of change of the altitude of the medium in each cell. 
     */
    Cell[] nbrs = this.NbrCross[Dim];
    Cell nbr0 = nbrs[0];// NAxis
    Cell nbr1 = nbrs[1];
    double mm = 3.6 * this.Amp;// what the heck is this?  negative value inverts the direction.  3.6 is a mysterious magic number. 
//    double mm = 3.6 * -this.Tension;// is this more independent of sea level? 
    double coordinate = nbr1.Speed - nbr0.Speed;// component? 
    coordinate *= mm;
    return coordinate;
  }
  /* ********************************************************************************* */
  public void Calc_Speed() {
    this.Speed = this.Amp - this.Prev_Amp;// vector toward future state
  }
  /* ********************************************************************************* */
  public void Calc_Energy_Vector() {
    for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
      this.PoyntingVect[DimCnt] = this.Calc_Energy_Vector(DimCnt);
    }
  }
  /* ********************************************************************************* */
  public void Calc_Tension(double Base) {
    this.Tension = Base - this.Amp;// pull toward base
  }
  /* ********************************************************************************* */
  public double Calc_Basis() {
    double Sum = 0;
    double NNbrs = 0;
    for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
      for (int AxCnt = 0; AxCnt < NAxis; AxCnt++) {
        if (this.NbrCross[DimCnt][AxCnt] != null) {
          Sum += this.NbrCross[DimCnt][AxCnt].Amp;
          NNbrs++;
        }
      }
    }
    //Sum += this.Amp; NNbrs++;
    this.Basis = Sum / NNbrs;// 4 neighbors in 2 dimensions
    return this.Basis;// 4 neighbors in 2 dimensions
//    return Sum / (double) (NumNbrs);// 4 neighbors in 2 dimensions
  }
  /* ********************************************************************************* */
  public void Update() {// getting close to runcycle
    double basis = this.Calc_Basis();
    this.Calc_Tension(basis);
    this.Calc_Speed();
    this.Next_Amp = this.Amp + ((this.Speed * this.Damp) + (this.Tension * TRate));
    this.Calc_Energy_Vector();
  }
  /* ********************************************************************************* */
  public void Rollover() {// set up for next cycle
    this.Prev_Amp = this.Amp;
    this.Amp = this.Next_Amp;
  }
  /* ********************************************************************************* */
  public void Adjust_Sum(double Amount) {// raise or lower 'water level' of medium
    this.Amp += Amount;
    this.Next_Amp += Amount;
  }
  /* ********************************************************************************* */
  public void Connect(Cell other) {// deprecated
    this.Neighbors.add(other);
    other.Neighbors.add(this);
  }
  /* ********************************************************************************* */
  public void ConnectCross(Cell other, int Dim, int Axis) {
    this.NbrCross[Dim][Axis] = other;
    Axis = (1 - Axis);// 1-1 = 0.  1-0 = 1.
    other.NbrCross[Dim][Axis] = this;// connect along same dimension, but opposite axis (eg your south connects to my north).
  }
  /* ********************************************************************************* */
  void RandAmp() {
    this.Amp = 1.0 * (Globals.RandomGenerator.nextDouble() * 2.0 - 1.0);// range -1.0 to 1.0
  }
  /* ********************************************************************************* */
  public void Draw_Me(DrawingContext ParentDC) {// IDrawable
    double what = this.Amp;// / 8.0;// + 2;
    Color col = Globals.ToNegPos(what);
    ParentDC.gr.setColor(col);
    ParentDC.gr.fillRect((int) this.XLoc, (int) this.YLoc, (int) this.Wdt, (int) this.Hgt);
    ParentDC.gr.setColor(Color.black);
    ParentDC.gr.drawRect((int) this.XLoc, (int) this.YLoc, (int) this.Wdt, (int) this.Hgt);
    if (false) {
      ParentDC.gr.setColor(Color.green);
      Cell nbr;
      for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
        for (int AxCnt = 0; AxCnt < NAxis; AxCnt++) {
          nbr = this.NbrCross[DimCnt][AxCnt];
          double jitter0 = Globals.RandomGenerator.nextDouble() * 5;
          double jitter1 = Globals.RandomGenerator.nextDouble() * 5;
          double jitter2 = Globals.RandomGenerator.nextDouble() * 5;
          double jitter3 = Globals.RandomGenerator.nextDouble() * 5;
          ParentDC.gr.drawLine((int) (this.XLoc + jitter0), (int) (this.YLoc + jitter1), (int) (nbr.XLoc + jitter2), (int) (nbr.YLoc + jitter3));
        }
      }
    }

    // normalize length
    double SumSq = 0;
    double mag = 0;
    for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
      mag = this.PoyntingVect[DimCnt];
      SumSq += mag * mag;
    }
    ParentDC.gr.setColor(Color.green);
    double hypot = Math.sqrt(SumSq);
    if (Math.abs(hypot) > 0.1) {
      for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
        this.PoyntingVect[DimCnt] /= hypot;
      }
      double len = 7;
      ParentDC.gr.drawLine((int) (this.XCtr), (int) (this.YCtr), (int) (this.XCtr + this.PoyntingVect[0] * len), (int) (this.YCtr + this.PoyntingVect[1] * len));
    }
    ParentDC.gr.fillOval((int) (this.XCtr - 2), (int) (this.YCtr - 2), (int) (4), (int) (4));
  }
  /* ********************************************************************************* */
  public void React() {// http://www.falstad.com/emwave1/
    double NumNbrs = NDims * NAxis;
    double Sum = 0.0;
    double SumAz = 0.0, SumCurrent = 0.0;
    double basis;
    for (int dcnt = 0; dcnt < NDims; dcnt++) {
      for (int cnt = 0; cnt < NAxis; cnt++) {
        Cell other = this.NbrCross[dcnt][cnt];
        Sum += other.Amp;
        SumAz += other.az;
        //SumCurrent+=
      }
    }
    double forcecoef = 1.0;
    Cell oes, oen, oew, oee, oe = this;
    oew = this.NbrCross[0][0];
    oee = this.NbrCross[0][1];
    oen = this.NbrCross[1][0];
    oes = this.NbrCross[1][1];
    basis = (SumAz) / NumNbrs;// * .25;// average of all neighbor's az
    double a = (oes.jx - oen.jx) + (oew.jy - oee.jy) - (oe.az - basis);
    double o = oe.dazdt;
    oe.dazdt = (oe.dazdt * oe.damp) + (a * forcecoef);
    oe.dazdt2 = oe.dazdt - o;// delta dazdt
  }
}
/*

 so the question is 

 // easy way
 previ = oew.az;
 nexti = oee.az;
 prevj = oen.az;
 nextj = oes.az;
 basis = (nexti + previ + nextj + prevj) * .25;// average of all neighbor's az
 a = oes.jx - oen.jx + oew.jy - oee.jy - (oe.az - basis);
 }
 o = oe.dazdt;
 oe.dazdt = (oe.dazdt * oe.damp) + a * forcecoef;
 oe.dazdt2 = oe.dazdt - o;
 */
