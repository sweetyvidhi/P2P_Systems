package thesis.three;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class AvgControl implements Control
{

  public static final String PAR_PROTOCOL="protocol";
  public static final String PAR_PRINT="print";
  
  private int pid;
  private int print;
  public AvgControl(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    print = Configuration.getInt(prefix+"."+PAR_PRINT);
  }
  @Override
  public boolean execute()
  {
    double sum=0.0,avg=0.0;
    for(int i=0;i<Network.size();i++)
    {
      Node n = Network.get(i);
      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
 
      sum+=ap.initval;
      //System.out.println(ap.initval);
    }
    avg = sum/Network.size();
    if(print==1)
    {
      System.out.println("Average "+avg);
    }
    for(int i=0;i<Network.size();i++)
    {
      Node n = Network.get(i);
      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
      ap.trueavg = avg;
    }
    // TODO Auto-generated method stub
    return false;
  }
  
}
