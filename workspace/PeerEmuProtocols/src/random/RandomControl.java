package random;

import java.util.Vector;

import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.transport.Transport;

public class RandomControl implements Control
{
  private int pid;
  private int walklen;
  private static final String PAR_WALKLEN="p";
  public RandomControl(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    walklen = Configuration.getInt(prefix+"."+PAR_WALKLEN);
  }
  
  @Override
  public boolean execute()
  {
    int i,j;
    int sender = CommonState.r.nextInt(Network.size());
    Node sendernode = Network.get(sender);
    Descriptor senderdesc = sendernode.getDescriptor(pid);
    RandomProtocol rp = (RandomProtocol)sendernode.getProtocol(pid);
    int r = CommonState.r.nextInt(rp.degree());
    Descriptor receiverdesc = rp.getNeighbor(r);
    System.out.println(sender+" is sending this message");
    String msg = "Hi from "+sender;
    Object[] message = {msg,walklen};
    int tid = FastConfig.getTransport(pid);
    Transport tr = (Transport) sendernode.getProtocol(tid);
    tr.send(senderdesc,receiverdesc,pid,message);
    
    return false;
  }
}
