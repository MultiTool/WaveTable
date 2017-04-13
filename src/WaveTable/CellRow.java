package WaveTable;

import java.util.ArrayList;

/**
 *
 * @author MultiTool
 */

/* ********************************************************************************* */
public class CellRow extends ArrayList<Cell> {
  /* ********************************************************************************* */
  public CellRow(int width) {
    for (int cnt = 0; cnt < width; cnt++) {
      Cell cell = new Cell();
      this.add(cell);
    }
  }
  /* ********************************************************************************* */
  public void Assign_Box(double XLoc, double YLoc, double Width, double Height) {
    int NumCells = this.size();
    for (int cnt = 0; cnt < NumCells; cnt++) {
      Cell cell = this.get(cnt);
      cell.Assign_Box(XLoc, YLoc, Width, Height);
      XLoc += Width;
    }
  }
  /* ********************************************************************************* */
  public void ConnectRows(CellRow otherprev) {
    int NumCells = this.size();
    if (NumCells != otherprev.size()) {
      System.out.println("Row sizes do not match!!!");// would be throw but I hate catch. 
    }
    for (int cnt = 0; cnt < NumCells; cnt++) {
      Cell cell0 = otherprev.get(cnt);
      Cell cell1 = this.get(cnt);
      cell1.ConnectCross(cell0, 1, 0);// 1 is ydim, 0 is north axis
    }
  }
  /* ********************************************************************************* */
  public void ConnectCells() {// wrapped horizontal connections
    int NumCells = this.size();
    Cell cell_prev = null, cell_now = this.get(NumCells - 1);
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell_prev = cell_now;
      cell_now = this.get(cnt);
      cell_now.ConnectCross(cell_prev, 0, 0);// 0 is xdim, 0 is west axis
    }
  }
  /* ********************************************************************************* */
  public void Fill_TimeRate(double Value) {
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.TRate = Value;
//      cell.Damp = Value;
    }
  }
  /* ********************************************************************************* */
  public void Fill_TimeRate(int FromX, int ToX, double Value) {
    int NumCells = this.size();
    FromX = Math.min(NumCells, FromX);
    ToX = Math.min(NumCells, ToX);
    Cell cell;
    for (int cnt = FromX; cnt < ToX; cnt++) {
      cell = this.get(cnt);
      cell.TRate = Value;
    }
  }
  /* ********************************************************************************* */
  public void Fill_Amps(double Value) {
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.Amp = Value;
    }
  }
  /* ********************************************************************************* */
  public void Fill_Amps(int FromX, int ToX, double Value) {
    FromX = Math.min(this.size(), FromX);
    ToX = Math.min(this.size(), ToX);
    Cell cell;
    for (int cnt = FromX; cnt < ToX; cnt++) {
      cell = this.get(cnt);
      cell.Amp = Value;
//      cell.Prev_Amp = Value;
    }
  }
  /* ********************************************************************************* */
  public void Fill_Prev_Amps(double Value) {
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.Prev_Amp = Value;
    }
  }
  /* ********************************************************************************* */
  public void Rand_Amps() {
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.RandAmp();
    }
  }
  /* ********************************************************************************* */
  public void Update() {// runcycle
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.Update();
    }
  }
  /* ********************************************************************************* */
  public void Rollover() {// set up for next cycle
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.Rollover();
    }
  }
  /* ********************************************************************************* */
  public double Get_Sum() {// get sum of all cell Amps
    int NumCells = this.size();
    Cell cell;
    double Sum = 0.0;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      Sum += cell.Amp;
    }
    return Sum;
  }
  /* ********************************************************************************* */
  public void Adjust_Sum(double Amount) {// raise or lower 'water level' of medium
    int NumCells = this.size();
    Cell cell;
    Amount /= (double) NumCells;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.Adjust_Sum(Amount);
    }
  }
  /* ********************************************************************************* */
  public void Draw_Me(DrawingContext ParentDC) {// IDrawable
    int NumCells = this.size();
    Cell cell;
    for (int cnt = 0; cnt < NumCells; cnt++) {
      cell = this.get(cnt);
      cell.Draw_Me(ParentDC);
    }
  }
}
