package chord;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class ChordInitializer implements Control
{
  private static final String PAR_PROT="protocol";
  private static final String PAR_M="m";
  int pid;
  int m;
  
  public ChordInitializer(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
    m = Configuration.getInt(prefix+"."+PAR_M);    
  }

  @Override
  public boolean execute()
  {
    int i;
    NodeComparator nc = new NodeComparator(pid);
    for(i=0;i<Network.size();i++)
    {
      Node n = (Node)Network.get(i);
      ChordDescriptor cd = (ChordDescriptor)n.getDescriptor(pid);
     //System.out.println("chord id is "+cd.getChordID());
    }
    Network.sort(nc);
    createFingers();
    // TODO Auto-generated method stub
    return false;
  }
  public ChordDescriptor findSuccessor(int id)
  {
    int i,flag=0;
    Node node;
    //System.out.println("id "+id);
    for(i=Network.size()-1;i>=0;i--)
    {
      node = (Node) Network.get(i);
      ChordDescriptor cd = (ChordDescriptor)node.getDescriptor(pid);
      if(cd.chordID<id)
      {
        flag=1;
        break;
        
      }
    }
    if(i==Network.size()-1)
    {
      node = (Node) Network.get(0);
    }
    else if(i==0 && flag==0)
    {
      node = (Node) Network.get(Network.size()-1);
    }
    else
    {
      node = (Node) Network.get(i+1);
    }
    ChordDescriptor cd = (ChordDescriptor)node.getDescriptor(pid);
    return cd;
  }
  public void createFingers()
  {
    int id;
    for (int i = 0; i < Network.size(); i++) {
      Node node = (Node) Network.get(i);
      ChordProtocol cp = (ChordProtocol) node.getProtocol(pid);
      //System.out.println("My id is "+cp.chordId+" "+((ChordDescriptor)node.getDescriptor(pid)).chordID);
      Node succnode = (Node) Network.get((i+1)%Network.size());
      ChordDescriptor cdsucc = (ChordDescriptor)succnode.getDescriptor(pid);
      cp.successor = cdsucc;
      Node prednode = null;
      if(i==0)
      {
        prednode = (Node) Network.get(Network.size()-1);
      }
      else
        {
        prednode = (Node) Network.get(i-1);
        }
      ChordDescriptor cdpred = (ChordDescriptor)prednode.getDescriptor(pid);
      cp.predecessor = cdpred;
      for(int j=0;j<m;j++)
      {
       id = (cp.chordId+(int)Math.pow(2,j))%((int)Math.pow(2,m));
       cp.fingerTable[j] = findSuccessor(id);
      }
    }   
  }
}

