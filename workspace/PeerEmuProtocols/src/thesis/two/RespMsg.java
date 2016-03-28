package thesis.two;

public class RespMsg extends Message
{
  public double svalue;
  public double rvalue;
  
  public void display()
  {
    System.out.println(this.desc.getID()+" says RESP "+this.rvalue+" "+this.svalue);
  }
}
