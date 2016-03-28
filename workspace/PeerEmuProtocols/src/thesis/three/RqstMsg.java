package thesis.three;

public class RqstMsg extends Message
{
  public double value;
  public int confidence;
  public void display()
  {
    System.out.println(this.desc.getID()+" says RQST "+this.value);
  }
}
