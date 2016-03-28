package random;

import peeremu.core.DescriptorSim;
import peeremu.core.Node;

public class RandomDescriptor extends DescriptorSim
{
  int descid;
  public RandomDescriptor(Node node, int pid)
  {
    super(node, pid);
    descid = ((RandomProtocol)node.getProtocol(pid)).id;
    // TODO Auto-generated constructor stub
  }
}
