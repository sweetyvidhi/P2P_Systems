package thesis.three;

public class RespMsg extends Message
{
  public double svalue;
  public double rvalue;
  public int confidence;
  public void display()
  {
    System.out.println(this.desc.getID()+" says RESP "+this.rvalue+" "+this.svalue);
  }
}
