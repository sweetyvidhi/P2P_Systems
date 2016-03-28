package random;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.Network;
import peeremu.core.Node;

public class RandomTopology implements Control
{

private int pid;
private String fname;
private static final String PAR_FILE = "file";
  
  public RandomTopology(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    fname = Configuration.getString(prefix+"."+PAR_FILE);
  }
  
  @Override
  public boolean execute()
  {
    int i,j;
    try
    {
      FileWriter fstream = new FileWriter(fname);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write("graph randomtopology{ ");
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
        RandomProtocol rp = (RandomProtocol)node.getProtocol(pid);
        long myid = node.getDescriptor(pid).getID();
        Vector<Descriptor> myview =  rp.view;
        for(j=0;j<myview.size();j++)
        {
          nid = myview.get(j).getID();
          out.write(myid+"--"+nid);
          out.write("\r\n");
        }
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
