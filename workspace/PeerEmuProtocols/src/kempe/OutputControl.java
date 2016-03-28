package kempe;


import java.util.Collections;
import java.util.Vector;

import jelasity.AggregationProtocol;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;


public class OutputControl implements Control
{
  private int pid;
  private static final String PAR_EVALUATION = "evaluation";
  private static final String PAR_E = "e";
  private static final String PAR_NODES = "nodes";
  private int eval;
  private double e;
  private int nodes;
    
    public OutputControl(String prefix)
    {
      pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
      eval = Configuration.getInt(prefix+"."+PAR_EVALUATION);
      e = Configuration.getDouble(prefix+"."+PAR_E);
      nodes = Configuration.getInt(prefix+"."+PAR_NODES);
    }
    
    @Override
    public boolean execute()
    {
      int i,j;
      if(eval == 1)                                                 // Evaluation 1 : Rounds vs value of nodes
      {
        double sum=0.0,average=0.0;
        for(i=0;i<Network.size();i++)
        {
          Node node = Network.get(i);
          KempeProtocol rp = (KempeProtocol)node.getProtocol(pid);
          System.out.println(rp.round + " "+rp.value+" "+rp.weight);              
          //sum+=rp.value;                                          
        } 
        //average=sum/Network.size();
        //System.out.println(sum + " "+average);
      }
      else if(eval == 2)                                           //Evaluation 2 : Min and max error
      {
        Vector <Double> error = new Vector<Double>();
        Double d;
        int round=0;
        for(i=0;i<Network.size();i++)
        {
          Node node = Network.get(i);
          KempeProtocol rp = (KempeProtocol)node.getProtocol(pid);
          d=(rp.value-rp.trueavg)/rp.trueavg;                       
          error.add(d);                                             
          round = rp.round;                                         
        }
        Object max = Collections.max(error);                        
        Object min = Collections.min(error);                          
        System.out.println(round + " "+max);                         
        System.out.println(round + " "+min);                        
      }
      else if(eval == 3 || eval==4)
      {
        int round=0;
        double c=0;
        for(i=0;i<Network.size();i++)
        {
          Node node = Network.get(i);
          KempeProtocol rp = (KempeProtocol)node.getProtocol(pid);
          round = rp.round;
          double d=(rp.value-rp.trueavg)/rp.trueavg;
          if (Math.abs(d)<e)
          {
            c++;
          }
        }
        if(eval==3)
        {
          System.out.println(round+" "+" "+(double)(c/nodes)*100);
        }
        else if(eval==4)
        {
          if(c==nodes)
          {
            System.out.println(round);
            return true;
          }
        }
      }
      return false;
    }
}
