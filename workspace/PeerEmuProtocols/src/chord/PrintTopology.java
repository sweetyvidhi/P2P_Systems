package chord;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class PrintTopology implements Control
{
  private static final String PAR_PROT="protocol";
  int pid;
  
  public PrintTopology(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
  }
  @Override
  public boolean execute()
  {
    Node n;
    int size=Network.size();
    System.out.println("The nodes are : ");
    for(int i=0;i<size;i++)
    {
      n = Network.get(i);
      ChordProtocol cp = (ChordProtocol)n.getProtocol(pid);
      System.out.print(cp.chordId+" ");
    }
    for(int i=0;i<size;i++)
    {
      n = Network.get(i);
      ChordProtocol cp = (ChordProtocol)n.getProtocol(pid);
      System.out.println("My ID : "+cp.chordId);
      System.out.println("Successor : "+((ChordDescriptor)cp.successor).chordID);
      System.out.println("Predecessor : "+((ChordDescriptor)cp.predecessor).chordID);
      System.out.println("Finger table : ");
      for(int j=0;j<cp.m;j++)
      {
        System.out.println(j+" "+((ChordDescriptor)cp.fingerTable[j]).chordID);
      } 
    }
    return false;
  }
}
