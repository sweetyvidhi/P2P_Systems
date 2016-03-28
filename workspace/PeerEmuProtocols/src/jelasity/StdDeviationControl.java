package jelasity;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class StdDeviationControl implements Control
{
  private int pid;
  private int confidence=0;
  private int type;
  private double stddevprev=0.0;
  
  
  
  private static final String PAR_TYPE = "type";
  
  
  public StdDeviationControl(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    type = Configuration.getInt(prefix+"."+PAR_TYPE);
   
  }
  @Override
  public boolean execute()
  {
    
    
    if(type==1||type==2)
    {
      double sum = 0.0,avg=0.0,stdev=0.0;
      int N=0;
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        if(ap.round > confidence)
        { 
          sum+=ap.value;
          N++;
        }
      }
      avg = sum/N;
      sum=0.0;
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        if(ap.round > confidence)
        {
          sum+=Math.pow((ap.value-avg),2);  
        }
      }
      if(type==1)
      {
          stdev=Math.sqrt(sum/N);         //Standard Deviation
          System.out.println(CommonState.getTime()/1000+" "+stdev+" "+N);
      }
      else if(type==2)
      {
        stdev = sum/N;                    //Convergence Ratio
        System.out.println(CommonState.getTime()/1000+" "+(stdev/stddevprev)+" "+N);
      }
      stddevprev=stdev;
    }
   
    // TODO Auto-generated method stub
    return false;
  }
}
