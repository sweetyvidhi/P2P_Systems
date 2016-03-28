package thesis.one;

public class RqstMsg extends Message
{
  public double value;
  public void display()
  {
    System.out.println(this.desc.getID()+" says RQST "+this.value);
  }
}
