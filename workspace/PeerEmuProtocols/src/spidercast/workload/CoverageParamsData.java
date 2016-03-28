package spidercast.workload;

public class CoverageParamsData
{
  public int kr;
  public int kg;



  public CoverageParamsData(int kg, int kr)
  {
    this.kr = kr;
    this.kg = kg;
  }



  public int getCoverageToBeDone()
  {
    return kr+kg;
  }



  public int getKr()
  {
    return this.kr;
  }



  public void setKr(int kr)
  {
    this.kr = kr;
  }



  public int getKg()
  {
    return this.kg;
  }



  public void setKg(int kg)
  {
    this.kg = kg;
  }



  public void decKg()
  {
    kg--;
  }



  public void decKr()
  {
    kr--;
  }



  public void incKg()
  {
    kg++;
  }



  public void incKr()
  {
    kr++;
  }
}