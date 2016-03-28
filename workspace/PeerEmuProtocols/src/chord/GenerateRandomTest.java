package chord;

import java.util.Random;
public class GenerateRandomTest
{
  public static void main(String args)
  {
    int m=10,i;
    long seed = 1234567890;
    Random r = new Random();
    r.setSeed(seed);
    for(i=0;i<m;i++)
    {
      System.out.println(r.nextInt());
    }
    
  }
}
