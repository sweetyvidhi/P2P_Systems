package jelasity;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;

public class AggregationInit implements Control
{
  private static final String PAR_PROT="protocol";
  private static int pid;
  
  public AggregationInit(String prefix)
  {
    pid = Configuration.getInt(prefix+"."+PAR_PROT);
  }
  @Override
  public boolean execute()
  {
    int i;
    for(i=0;i<Network.size();i++)
    {
      Node n = (Node)Network.get(i);
      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
      ap.setValue(i);
    }
    return false;
  }
}
