package chord;

import peeremu.core.DescriptorSim;
import peeremu.core.Node;

public class ChordDescriptor extends DescriptorSim
{
  int chordID;
  
  
  public ChordDescriptor(Node node,int pid)
  {
    super(node,pid);
    this.chordID = ((ChordProtocol)node.getProtocol(pid)).chordId;
  }
  
  void setChordID(int id)
  {
    chordID = id;
  }
  
  int getChordID()
  {
    return chordID;
  }
}
