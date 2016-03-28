package thesis.one;


import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class AverageControl implements Control
{
  private int pid;
  private double thresh;
  private double x;
  private static final String PAR_THRESH = "threshold";
  private static final String PAR_X = "x";
  private boolean first=true;
  public AverageControl(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    thresh = Configuration.getDouble(prefix+"."+PAR_THRESH);
    x = Configuration.getDouble(prefix+"."+PAR_X);
  }
  @Override
  public boolean execute()
  {
    int i,flag=0,round=-1,nodesize;
    double sum=0,temp=0;
    int num = (int)((x*Network.size())/100);
    if(first)
      nodesize=Network.size();
    else
      nodesize=Network.size()-num;
    for(i=0;i<nodesize;i++)
    {
      Node n = Network.get(i);
      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
      round = ap.round;
      temp+=ap.value;
      if (ap.value >= ap.trueavg-thresh && ap.value <= ap.trueavg+thresh)
        continue;
      else
      { flag = 1;
      break;
      }

    }

    if(flag == 0)
    {
      System.out.println("Converginground "+round);
      if(first)
      {
        //System.out.println("Converginground "+round);             /* To simulate removal of x% nodes after convergence */
        
        for(i=Network.size()-1;i>Network.size()-1-num;i--)  
        {
          Node n = Network.get(i);
          n.setFailState(Node.DEAD);
        }
        for(i=0;i<Network.size()-num;i++)
        {
          Node n = Network.get(i);
          AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
          sum+=ap.initval;
        }
        for(i=0;i<Network.size()-num;i++)
        {
          Node n = Network.get(i);
          AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
          ap.trueavg = sum/(Network.size()-num);
          if(i==0)
            System.out.println("Newavg "+ap.trueavg);
        }
        
        first=false;
      }

    }
    return false;
  }
}