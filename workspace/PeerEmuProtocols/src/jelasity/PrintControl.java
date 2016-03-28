package jelasity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import chord.ChordDescriptor;
import chord.ChordProtocol;
import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.Network;
import peeremu.core.Node;
import random.RandomProtocol;

public class PrintControl implements Control
{
  private int pid;
  private String fname;
  private int eval;
  private static final String PAR_FILE = "file";
  private static final String PAR_EVALUATION = "evaluation";
    
    public PrintControl(String prefix)
    {
      pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
      fname = Configuration.getString(prefix+"."+PAR_FILE);
      eval = Configuration.getInt(prefix+"."+PAR_EVALUATION);
    }
    
    @Override
    public boolean execute()
    {
      int i,j;
      double sum=0.0,avg=0.0;
      try
      {
        FileWriter fstream = new FileWriter(fname);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("digraph randomtopology{ ");
        out.write("\r\n");
        out.write("nodesep=1.0 ");
        out.write("\r\n");
        for(i=0;i<Network.size();i++)
        {
          Node node = Network.get(i);
          long myid = node.getDescriptor(pid).getID();
          out.write(myid+"[label=\""+myid+"\"]");
          out.write("\r\n");    
        }
        for(i=0;i<Network.size();i++)
        {
          long nid;
          Node node = Network.get(i);
          AggregationProtocol rp = (AggregationProtocol)node.getProtocol(pid);
          long myid = node.getDescriptor(pid).getID();
          sum+=rp.initval;
          Vector<Descriptor> myview =  rp.view;
          for(j=0;j<myview.size();j++)
          {
            nid = myview.get(j).getID();
            out.write(myid+"->"+nid);
            out.write("\r\n");
          }
        }
        
        avg = sum/Network.size();
        for(i=0;i<Network.size();i++)
        {
          Node node = Network.get(i);
          AggregationProtocol rp = (AggregationProtocol)node.getProtocol(pid);
          rp.trueavg = avg;
        }
        if(eval == 1)
        {
          System.out.println("Average "+avg);
        }
        out.write("}");
        out.close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return false;
    }
}
