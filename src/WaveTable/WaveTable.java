package WaveTable;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author MultiTool
 */
public class WaveTable {
  /**
   * @param args the command line arguments
   */
  /* ********************************************************************************* */
  public static void main(String[] args) {
    Test_Poynting();
    if (true) {
      MainGui mg = new MainGui();
      mg.Init();
    }
    // https://www.crowdsupply.com/pinoccio/mesh-sensor-network
  }
  /* ********************************************************************************* */
  public double StaticSum = 0.0;
  public int GenCnt = 0;
  public ArrayList<CellRow> Rows;
  double Angle = 0.0;
  /* ********************************************************************************* */
  public WaveTable() {
//    this.Init(1, 1);
//    this.Init(2, 2);
//    this.Init(4, 4);
//    this.Init(11, 11);
//    this.Init(12, 12);
//    this.Init(16, 16);
    this.Init(32, 32);
//    this.Init(64, 64);
  }
  /* ********************************************************************************* */
  public void Init(int NumCols, int NumRows) {
    this.Rows = new ArrayList<CellRow>();
    double XOrg = 10, YOrg = 10;
    double CellWidth = 6, CellHeight = 6;
    CellWidth = CellHeight = 20;
    CellWidth = CellHeight = 30;
    for (int RowCnt = 0; RowCnt < NumRows; RowCnt++) {
      CellRow crow = new CellRow(NumCols);
      crow.Assign_Box(XOrg, YOrg + (RowCnt * CellHeight), CellWidth, CellHeight);
      this.Rows.add(crow);
    }
    int CasiAltura = NumRows - 1;
    CellRow crow_prev, crow = this.Rows.get(CasiAltura);
    for (int ycnt = 0; ycnt < NumRows; ycnt++) {
      crow_prev = crow;
      crow = this.Rows.get(ycnt);
      crow.ConnectCells();// connect and wrap horizontally
      crow.ConnectRows(crow_prev);// connect and wrap vertically
    }
    if (true) {
//      this.Alter_Time();
      if (false) {
        this.RandAmps();
      } else {
        this.Fill_Amps();
      }
    } else {
      // deaden borders
      double slowtime = 0.001;// 0.00000001;
      crow = this.Rows.get(0);
      crow.Fill_TimeRate(slowtime);// horizontal wall
      for (int ycnt = 0; ycnt < (NumRows); ycnt++) {// vertical wall
        crow = this.Rows.get(ycnt);
        crow.Fill_TimeRate(0, 1, slowtime);
      }
    }
    this.StaticSum = this.Get_Sum();
    this.Adjust_Sum(-this.StaticSum);
    this.StaticSum = this.Get_Sum();
    this.GenCnt = 0;
  }
  /* ********************************************************************************* */
  public void Alter_Time() {
    double slowtime = 0.0001;// 0.00000001;
    CellRow crow = this.Rows.get(0);
    int NumCols = this.Rows.get(0).size(), NumRows = this.Rows.size();
    int MinCol = NumCols / 4, MaxCol = (NumCols * 3) / 4;
    int MinRow = NumRows / 4, MaxRow = (NumRows * 3) / 4;
//      crow.Fill_TimeRate(slowtime);
    if (false) {
      for (int ycnt = MinRow; ycnt < MaxRow; ycnt++) {
        crow = this.Rows.get(ycnt);
        crow.Fill_TimeRate(MinCol, MaxCol, slowtime);
      }
    } else {
      MinCol = MaxCol = NumCols / 2;// or do triangle
      for (int ycnt = MinRow; ycnt < MaxRow; ycnt++) {
        crow = this.Rows.get(ycnt);
        crow.Fill_TimeRate(MinCol, MaxCol, slowtime);
        MinCol--;
        MaxCol++;
      }
    }
  }
  /* ********************************************************************************* */
  public void RandAmps() {
    int NumRows = this.Rows.size();
    CellRow crow;
    for (int cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      crow.Rand_Amps();
    }
  }
  /* ********************************************************************************* */
  public void Fill_Amps() {
    int NumRows = this.Rows.size();
    CellRow crow;
    // On a 32x32 grid:
    // 1 has Sum:0.0 drift
    // 3 has Sum:-320.00000000000057 drift
    // 4 has Sum:0.0 drift
    // 5 has Sum:-127.99999999999648 drift
    // 6 has Sum:63.99999999999244 drift
    // 7 has Sum:-64.00000000000136 drift
    // 8 has Sum:-2.7284841053187847E-11 drift (practically 0)
    // 9 has Sum:-1.48929757415317E-11 drift (practically 0)
    // 10 has Sum:64.00000000000045 drift 
    // 13 has Sum:128.00000000001046 drift 
    // 14 has Sum:128.00000000000477 drift 
    // 15 has Sum:-1.0800249583553523E-11 drift (practically 0)
    // 16 has Sum:1.0800249583553523E-11 drift (practically 0)
    // 32 has Sum:3.780797896979493E-11 drift (practically 0)
    // 
    int wlen = 4;
    int half = wlen / 2;
    int cnt = 0;
    for (cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      if (cnt % wlen < half) {
        crow.Fill_Amps(1.0);
      } else {
        crow.Fill_Amps(-1.0);
      }
    }
  }
  /* ********************************************************************************* */
  public double Get_Sum() {// get sum of all row Amps
    int NumRows = this.Rows.size();
    double Sum = 0.0;
    CellRow crow;
    for (int cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      Sum += crow.Get_Sum();
    }
    return Sum;
  }
  /* ********************************************************************************* */
  public void Adjust_Sum(double Amount) {// raise or lower 'water level' of medium
    int NumRows = this.Rows.size();
    CellRow crow;
    Amount /= (double) NumRows;
    for (int cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      crow.Adjust_Sum(Amount);
    }
  }
  /* ********************************************************************************* */
  public void Update() {// runcycle
    int NumRows = this.Rows.size();
    CellRow crow;

    if (false) {// wave maker
//      crow = this.Rows.get(this.Rows.size() / 2);
      crow = this.Rows.get(2);
      crow.Fill_Amps(10, 20, Math.sin(Angle) * 30);
      Angle += 0.01;
    }
    for (int cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      crow.Update();
    }
    // this is a hack that we shouldn't have to do. 
    // for some reason the total 'sea level' in our wave tank slowly drifts either up or down to infinity.
    // this re-sets the level
    double Sum = this.Get_Sum();
    this.Adjust_Sum(this.StaticSum - Sum);
    if (this.GenCnt % 100 == 0) {
      System.out.println("GenCnt:" + this.GenCnt + ", Sum:" + Sum);
    }
    this.GenCnt++;
  }
  /* ********************************************************************************* */
  public void Rollover() {// set up for next cycle
    int NumRows = this.Rows.size();
    CellRow crow;
    for (int cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      crow.Rollover();
    }
  }
  /* ********************************************************************************* */
  public void Draw_Me(DrawingContext ParentDC) {// IDrawable
    int NumRows = this.Rows.size();
    CellRow crow;
    for (int cnt = 0; cnt < NumRows; cnt++) {
      crow = this.Rows.get(cnt);
      crow.Draw_Me(ParentDC);
    }
  }
  /* ********************************************************************************* */
  public void TestNDex() {
    int[] dimlims = {1, 2};
    /*
     Figure out a way to index N dimensions on up so it scales easily
     for dimcnt = ndims down to 0{
     . 
     }
    
     */
  }
  /* ********************************************************************************* */
  public void updateEMWave1(Graphics realg) {// http://www.falstad.com/emwave1/
//    if (winSize == null || winSize.width == 0) {
//      // this works around some weird bug in IE which causes the
//      // applet to not show up properly the first time after
//      // a reboot.
//      handleResize();
//      return;
//    }
//    double tadd = 0;
//    if (!stoppedCheck.getState()) {
//      int val = 5; // 5; //speedBar.getValue();
//      tadd = val * .05;
//    }
//    int i, j;
//
//    boolean stopFunc = dragging;
//    if (stoppedCheck.getState()) {
//      stopFunc = true;
//    }
//    double speedValue = speedBar.getValue() / 2.;
//    if (stopFunc) {
//      lastTime = 0;
//    } else {
//      if (lastTime == 0) {
//        lastTime = System.currentTimeMillis();
//      }
//      if (speedValue * (System.currentTimeMillis() - lastTime) < 1000) {
//        stopFunc = true;
//      }
//    }
//    if (!stopFunc) {
//      int iter;
//      int mxx = gridSizeX - 1;
//      int mxy = gridSizeY - 1;
//      for (iter = 1;; iter++) {
//        doSources(tadd, false);
//        setup.doStep();
//        double tadd2 = tadd * tadd;
//        double forcecoef = 1;
////        int curMedium = 0;
//        OscElement oew, oee, oen, oes, oe;
//        double previ, nexti, prevj, nextj, basis, a, b, o;
//        for (j = 1; j != mxy; j++) {
//          int gi = j * gw + 1;
//          int giEnd = gi + mxx - 1;
//          oe = grid[gi - 1];
//          oee = grid[gi];
//          for (; gi != giEnd; gi++) {
//            oew = oe;
//            oe = oee;
//            oee = grid[gi + 1];
//            if (oe.conductor) {
//              continue;
//            }
//
//            oen = grid[gi - gw];
//            oes = grid[gi + gw];
//
//            if (oe.boundary) {
//              double az = oe.az;
//              previ = oew.az - az;
//              if (oew.conductor) {
//                previ = (oee.conductor) ? 0 : oee.az - az;
//              }
//              nexti = oee.az - az;
//              if (oee.conductor) {
//                nexti = (oew.conductor) ? 0 : oew.az - az;
//              }
//              prevj = oen.az - az;
//              if (oen.conductor) {
//                prevj = (oes.conductor) ? 0 : oes.az - az;
//              }
//              nextj = oes.az - az;
//              if (oes.conductor) {
//                nextj = (oen.conductor) ? 0 : oen.az - az;
//              }
//              basis = (nexti + previ + nextj + prevj) * .25;
//
//              double jj = oes.jx - oen.jx + oew.jy - oee.jy;
//              a = basis + jj;
//            } else {
//              // easy way
//              previ = oew.az;
//              nexti = oee.az;
//              prevj = oen.az;
//              nextj = oes.az;
//              basis = (nexti + previ + nextj + prevj) * .25;
//              a = oes.jx - oen.jx + oew.jy - oee.jy - (oe.az - basis);
//            }
//            o = oe.dazdt;
//            oe.dazdt = (oe.dazdt * oe.damp) + a * forcecoef;
//            oe.dazdt2 = oe.dazdt - o;
//          }
//        }
//        for (j = 1; j != mxy; j++) {
//          int gi = j * gw + 1;
//          int giEnd = gi - 1 + mxx;
//          for (; gi != giEnd; gi++) {
//            oe = grid[gi];
//            oe.az += oe.dazdt * tadd2;
//          }
//        }
//        t += tadd;
//        filterGrid();
//        long tm = System.currentTimeMillis();
//        /*System.out.println(tm-lastTime);
//         System.out.println(speedValue*1000/(tm-lastTime));*/
//        if (tm - lastTime > 200
//          || iter * 1000 >= speedValue * (tm - lastTime)) {
//          lastTime = tm;
//          break;
//        }
//      }
//    }
//
//    renderGrid();
//
////    int intf = (gridSizeY / 2 - windowOffsetY) * winSize.height / windowHeight;
//    for (i = 0; i < sourceCount; i++) {
//      OscSource src = sources[i];
//      int xx = src.getScreenX();
//      int yy = src.getScreenY();
//      int col = 0xFFFFFFFF;
//      if (sourceType == SRC_ANTENNA && (i % 2) == 0) {
//        col = 0xFFFFFF00;
//      }
//      plotSource(i, xx, yy, col);
//    }
//
//    if (imageSource != null) {
//      imageSource.newPixels();
//    }
//
//    realg.drawImage(dbimage, 0, 0, this);
//    if (!stoppedCheck.getState()) {
//      cv.repaint(pause);
//    }
  }
  public static void Test_Poynting() {
    /* 
     If I understand correctly, Falstad seems to calculate the direction of energy flow (Poynting vector) by simply getting the difference between the speeds 
     of two opposite neighbors, and then multiplying by the current amp (height value) of the cell.
     By 'speed' I mean the rate of change of the altitude of the medium in each cell. 
     */
    double Magic_Number = 3.6;
    double coordinate;
    double Angle = 0.0;
    double Amp_Prev = 0.0, Amp = 0.0, Amp_Next = 0.0;
    double Speed_Prev = 0.0, Speed = 0.0, Speed_Next = 0.0;
    double Curve = 0.0;
    double AngleInc = 0.1;
    for (int cnt = 0; cnt < 1000; cnt++) {
      Amp_Prev = Amp;
      Amp = Amp_Next;
      Amp_Next = Math.sin(Angle);
      Curve = Amp - (Amp_Prev + Amp_Next) / 2.0;
      Speed_Prev = Speed;
      Speed = Speed_Next;
      Speed_Next = Amp_Next - Amp;

//      double mm = Magic_Number * Amp;// 3.6 is a mysterious magic number. 
      double mm = Magic_Number * Curve;// 3.6 is a mysterious magic number. 
//      coordinate = Speed_Next - Speed_Prev;
      coordinate = Speed_Prev - Speed_Next;// reverse direction. this is right minus left, if wave is moving left to right
//      coordinate *= mm * (10000 / 2) / 1.793609607
//      coordinate *= Curve * (10000 / 2) / 0.4982248908;
      coordinate *= Curve * (10000);// * (1 / Math.sqrt(AngleInc));

//      System.out.println("Amp:" + Amp + ", coordinate:" + coordinate);
      System.out.println("" + Angle + ", " + Amp + ", " + coordinate);
      Angle += AngleInc;
    }
    System.out.println("Done");
    System.out.println("Ratio:" + 2.0 / (Math.PI));
    System.out.println("Ratio:" + (2 * Math.PI) / 4.0);
  }
}
