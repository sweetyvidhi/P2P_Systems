package thesis.two;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class StdDeviationControl implements Control
{
  private int pid;
  private int confidence;
  private double threshold;
  private int type;
  private boolean convflag=true;
  private static final String PAR_CONFIDENCE = "confidence";
  private static final String PAR_THRESHOLD = "threshold";
  private static final String PAR_TYPE = "type";
  public StdDeviationControl(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    confidence = Configuration.getInt(prefix+"."+PAR_CONFIDENCE);
    threshold = Configuration.getDouble(prefix+"."+PAR_THRESHOLD);
    type = Configuration.getInt(prefix+"."+PAR_TYPE);
  }
  @Override
  public boolean execute()
  {
    double sum = 0.0,avg=0.0,stdev=0.0;
    int count = 0,N=0,flag=0;
    if(type==1)
    {
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        //if(ap.round > confidence)
        //{ 
          sum+=ap.initval;
          N++;
        //}
      }
      avg = sum/N;
      sum=0.0;
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
       // if(ap.round > confidence)
        //{
          sum+=Math.pow((ap.value-avg),2);
          //System.out.println(ap.round + " "+ap.value+" "+sum);
        //}
      }
      stdev=Math.sqrt(sum/N);
      System.out.println(CommonState.getTime()+ " "+stdev);
    }
    else if(type==2)
    {
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        sum+=ap.initval;
        N++;
      }
      avg = sum/N;
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        if(ap.value>(avg+threshold) || ap.value <(avg-threshold))
        {
          flag=1;
        }
      }
      if(flag==0 && convflag)
      {
        System.out.println(Network.size()+" "+CommonState.getTime()/1000);
        convflag = false;
      }
    }
    else if(type==3)
    {
      double sum1=0.0;
      for(int i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        sum+=ap.initval;
        sum1+=ap.value;
      } 
      System.out.println((CommonState.getTime()/1000) + " " + sum+ " "+sum1+" "+(sum-sum1));
    }
    // TODO Auto-generated method stub
    return false;
  }
}
