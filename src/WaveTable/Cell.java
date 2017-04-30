/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WaveTable;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

/**
 *
 * @author MultiTool
 */
public class Cell {
  public static final int NDims = 2;
  public static final int NAxis = 2;
  public static final int NumNbrs = NDims * NAxis;
//  public double TRate = 0.9;// time rate
//  public double TRate = 0.5;// time rate
//  public double TRate = 0.1;// time rate
  public double TRate = 0.05;// time rate
//  public double TRate = 0.01;// time rate, normal
//  public double TRate = 0.001;// time rate
//  public double TRate = 0.0001;// time rate
//  public double TRate = 0.00001;// time rate
  public boolean IsWall = false;// eventually we will make this a separate class and override the related methods
  // z component of vector potential and its first derivative
  public double prev_az, az = 0.0, dazdt, dazdt2;
  double jx, jy;// current
  public Cell[][] NbrCross;// another way
  private double damp = 1.0;
  public double Tension = 0.0, Speed = 0.0;// Speed is also used as inertia
  public double Basis = 0.0;// average Amp of all my neighbors
  private double Prev_Amp = 0.0, Amp = 0.0, Next_Amp = 0.0;
  public double Damp = 1.0; //0.99;
  double[] PoyntingVect_Prev = new double[NDims], PoyntingVect = new double[NDims];
  private double EnergyMag_Prev, EnergyMag;
  public double XLoc = 0, YLoc = 0, Wdt = 10, Hgt = 10;// for drawing
  public double XCtr = 0.0, YCtr = 0.0;
  private double MinAmp = Double.POSITIVE_INFINITY, MaxAmp = Double.NEGATIVE_INFINITY;
  double AvgAmp = 0.0, NumRuns = 0.0, SumAmp = 0;
  /* ********************************************************************************* */
  public Cell() {
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
    double Magic_Number3 = 10000;//3.6;
    double Magic_Number = 4.814120606102579;
    double coordinate = 0.0;
    Cell[] nbrs = this.NbrCross[Dim];
    Cell nbr0 = nbrs[0];// NAxis
    Cell nbr1 = nbrs[1];
    if (nbr0 != null && nbr1 != null) {
      double Curve = Amp - ((nbr1.Get_Amp() + nbr0.Get_Amp()) / 2.0);// positive (middle up) or negative (middle down) curve
      coordinate = nbr1.Speed - nbr0.Speed;// if energy ls flowing from nbr0 to nbr1, return value will be positive
      if (false) {
        coordinate *= (Magic_Number3 * Amp);// Falstad way. Vector direction is good unless average sea level is far from 0. 
      } else if (true) {
//      coordinate *= Math.signum(Curve) * Math.sqrt(Math.sqrt(Math.abs(Curve))) / Magic_Number;// who knows. emperically comes close to true, but not in vector direction
//      coordinate *= Math.pow(Curve, 0.00125);
        coordinate *= Math.signum(Curve);// this works well for vector direction but not for magnitude
      } else {
        coordinate *= Curve;
      }
    }
    return coordinate;
  }
  /* ********************************************************************************* */
  public double Contrast_Energy_Vectors(double[] Vect0, double[] Vect1) {
    // Assuming both vectors have been normalized to length 1
    double DotProd = 0.0;// first get the dot product
    for (int cnt = 0; cnt < NDims; cnt++) {
      DotProd += Vect0[cnt] * Vect1[cnt];
    }
    double Radius = 1.0;
    double Metric = Radius - DotProd;// if DotProd is 1, becomes 0.  If DotProd is -1, becomes 2.  
    Metric /= 2.0;// normalize to range 0.0 to 1.0.  This gives cartoon relativistic velocity ratio
    return Metric;
  }
  /* ********************************************************************************* */
  public void Set_Prev_Amp(double Prev_Amp0) {
    double sqrT = Math.sqrt(this.TRate);// empirically this seems to work, but why?
    this.Prev_Amp = Prev_Amp0 * sqrT;
//    this.Prev_Amp = Prev_Amp0;
  }
  /* ********************************************************************************* */
  public double Get_Amp() {
//    if (this.IsWall) {
//      return 0.0;
//    } else {
    return this.Amp;
//    }
  }
  /* ********************************************************************************* */
  public void Set_Amp(double Amp0) {
//    if (this.IsWall) {
//      this.Amp = 0.0;
//    } else {
    double sqrT = Math.sqrt(this.TRate);// empirically this seems to work, but why?
    this.Amp = Amp0 * sqrT;
//    }
    this.Check_Amp();
  }
  /* ********************************************************************************* */
  public void Set_WallnessX(boolean IsWall0) {
    this.IsWall = IsWall0;
    if (this.IsWall) {
      this.Damp = 0.0001;
//    this.TRate = 0.0;
//      this.DisconnectAll();
    }
  }
  /* ********************************************************************************* */
  public void Set_Wallness(double Damp0) {
    if (Damp0 < 1.0) {
      this.IsWall = true;
      if (Double.isInfinite(Damp0)) {
        System.out.println();
      }
      this.Damp = Damp0;
//    this.TRate = 0.0;
//      this.DisconnectAll();
    } else {
      this.IsWall = false;
    }
  }
  /* ********************************************************************************* */
  public void Check_Amp() {
    if (this.MaxAmp < this.Amp) {
      this.MaxAmp = this.Amp;
    }
    if (this.MinAmp > this.Amp) {
      this.MinAmp = this.Amp;
    }
  }
  /* ********************************************************************************* */
  public void Calc_Energy_Vector() {
    double SumSq = 0.0;
    double Mag;
    for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
      Mag = this.Calc_Energy_Vector(DimCnt);
      this.PoyntingVect[DimCnt] = Mag;
      SumSq += Mag * Mag;
    }
    this.EnergyMag = Math.sqrt(SumSq);
  }
  /* ********************************************************************************* */
  public double Calc_Basis() {
    double Sum = 0;
    double NNbrs = 0;
    for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
      for (int AxCnt = 0; AxCnt < NAxis; AxCnt++) {
        if (this.NbrCross[DimCnt][AxCnt] != null) {
          Sum += this.NbrCross[DimCnt][AxCnt].Get_Amp();
          NNbrs++;
        }
      }
    }
    if (NNbrs > 0) {
      //Sum += this.Amp; NNbrs++;
      this.Basis = Sum / NNbrs;// 4 neighbors in 2 dimensions
    }
    return this.Basis;// 4 neighbors in 2 dimensions
//    return Sum / (double) (NumNbrs);// 4 neighbors in 2 dimensions
  }
  /* ********************************************************************************* */
  public void Calc_Tension(double Base) {
    this.Tension = Base - this.Amp;// pull toward avg amp of neighbors
  }
  /* ********************************************************************************* */
  public void Calc_Speed() {
    this.Speed = this.Amp - this.Prev_Amp;// vector toward future state
  }
  /* ********************************************************************************* */
  public void Update() {// getting close to runcycle
    // look for better alg:
    // https://www.gamedev.net/resources/_/technical/graphics-programming-and-theory/the-water-effect-explained-r915
//    if (this.IsWall) {
//      this.Next_Amp = this.Amp = this.Prev_Amp = 0.0;
//      this.Speed = 0.0;
//    } else {
    double basis = this.Calc_Basis();
    this.Calc_Tension(basis);
    this.Calc_Speed();
    if (this.IsWall) {
//      this.Tension = 0.0;
//      this.TRate = this.TRate * 2.0 / 2.0;
//      this.Next_Amp = basis;
//      this.Damp = 0.0; this.TRate = 1.0;
    } else {
    }
    this.Next_Amp = this.Amp + ((this.Speed * this.Damp) + (this.Tension * TRate));
    this.Calc_Energy_Vector();
//    }

    // statistics
    this.SumAmp += this.Amp;
    this.NumRuns++;
    this.AvgAmp = this.SumAmp / this.NumRuns;
  }
  /* ********************************************************************************* */
  public void Rollover() {// set up for next cycle
    this.Prev_Amp = this.Amp;
    this.Amp = this.Next_Amp;
    this.EnergyMag_Prev = this.EnergyMag;

    // swap energy vectors.  dubious. 
    double[] TempVect = this.PoyntingVect_Prev;
    this.PoyntingVect_Prev = this.PoyntingVect;
    this.PoyntingVect = TempVect;

    this.Check_Amp();
  }
  /* ********************************************************************************* */
  public void Adjust_Sum_All(double Amount) {// raise or lower 'water level' of medium
    this.Prev_Amp += Amount;// does this help or hurt? 
    this.Adjust_Sum(Amount);
  }
  /* ********************************************************************************* */
  public void Adjust_Sum(double Amount) {// raise or lower 'water level' of medium
    this.Amp += Amount;
    this.Next_Amp += Amount;
    this.Check_Amp();
  }
  /* ********************************************************************************* */
  void Add_To_All(double Amount) {
    this.Prev_Amp += Amount;// does this help or hurt? 
    this.Amp += Amount;
    this.Next_Amp += Amount;
    this.Check_Amp();
  }
  /* ********************************************************************************* */
  public void ConnectCross(Cell other, int Dim, int Axis) {
    this.NbrCross[Dim][Axis] = other;
    Axis = (1 - Axis);// 1-1 = 0.  1-0 = 1.
    other.NbrCross[Dim][Axis] = this;// connect along same dimension, but opposite axis (eg your south connects to my north).
  }
  /* ********************************************************************************* */
  public void DisconnectCross(int Dim, int Axis) {
    Cell other = this.NbrCross[Dim][Axis];
    if (other != null) {
      this.NbrCross[Dim][Axis] = null;
      Axis = (1 - Axis);// 1-1 = 0.  1-0 = 1.
      other.NbrCross[Dim][Axis] = null;// connect along same dimension, but opposite axis (eg your south connects to my north).
    }
  }
  /* ********************************************************************************* */
  public void DisconnectAll() {
    for (int dcnt = 0; dcnt < NDims; dcnt++) {
      for (int axcnt = 0; axcnt < NAxis; axcnt++) {
        this.DisconnectCross(dcnt, axcnt);
      }
    }
  }
  /* ********************************************************************************* */
  void RandAmp() {
    this.Amp = 1.0 * (Globals.RandomGenerator.nextDouble() * 2.0 - 1.0);// range -1.0 to 1.0
    this.Check_Amp();
  }
  /* ********************************************************************************* */
  public void Draw_Me(DrawingContext ParentDC) {// IDrawable
    double what = this.Amp;// / 8.0;// + 2;
    Color col = Globals.ToNegPos(what);

    // draw cell background and outline
    ParentDC.gr.setColor(col);
    ParentDC.gr.fillRect((int) this.XLoc, (int) this.YLoc, (int) this.Wdt, (int) this.Hgt);
    ParentDC.gr.setColor(Color.black);
    ParentDC.gr.drawRect((int) this.XLoc, (int) this.YLoc, (int) this.Wdt, (int) this.Hgt);

    ParentDC.gr.setColor(Color.green);
    String AmpTxt;
    if (false) {
      AmpTxt = String.format("%.2f", this.Amp);
      ParentDC.gr.setFont(new Font("TimesRoman", Font.PLAIN, 10));
      ParentDC.gr.drawString(AmpTxt, (int) (this.XLoc), (int) (this.YLoc));
    }
    if (false) {
      ParentDC.gr.setFont(new Font("TimesRoman", Font.PLAIN, 10));

      AmpTxt = String.format("%.2f", this.AvgAmp);
      ParentDC.gr.drawString(AmpTxt, (int) (this.XLoc), (int) (this.YCtr + 4));
      if (false) {
        AmpTxt = String.format("%.2f", this.MaxAmp);
        ParentDC.gr.drawString(AmpTxt, (int) (this.XLoc), (int) (this.YLoc + 10));
        AmpTxt = String.format("%.2f", this.MinAmp);
        ParentDC.gr.drawString(AmpTxt, (int) (this.XLoc), (int) (this.YLoc + this.Hgt));
      }
    }
    if (false) {
      double hypot = this.EnergyMag;
      if (Math.abs(hypot) > 0.00001) {// normalize length
        for (int DimCnt = 0; DimCnt < NDims; DimCnt++) {
          this.PoyntingVect[DimCnt] /= hypot;
        }
        double len = this.Wdt / 2.0;
        ParentDC.gr.drawLine((int) (this.XCtr), (int) (this.YCtr), (int) (this.XCtr + this.PoyntingVect[0] * len), (int) (this.YCtr + this.PoyntingVect[1] * len));
      }
      ParentDC.gr.fillOval((int) (this.XCtr - 2), (int) (this.YCtr - 2), (int) (4), (int) (4));
    }
    if (this.IsWall) {
      ParentDC.gr.setColor(Color.green);
      ParentDC.gr.fillOval((int) (this.XCtr - 1), (int) (this.YCtr - 1), (int) (2), (int) (2));
    }
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
