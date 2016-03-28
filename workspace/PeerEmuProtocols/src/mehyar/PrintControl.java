package mehyar;

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
  private static final String PAR_FILE = "file";
    
    public PrintControl(String prefix)
    {
      pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
      fname = Configuration.getString(prefix+"."+PAR_FILE);
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
          MehyarProtocol rp = (MehyarProtocol)node.getProtocol(pid);
          long myid = node.getDescriptor(pid).getID();
          if(myid<Network.size()/2)
          {
            rp.initval=rp.value = 0;
          }
          else
          {
            rp.initval=rp.value = 1;
          }
          System.out.println("-1 "+myid+" "+rp.value);
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
        //System.out.println("Average "+avg);
        out.write("}");
        out.close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return false;
    }
}
