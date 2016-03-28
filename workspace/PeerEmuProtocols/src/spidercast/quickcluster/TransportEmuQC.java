/**
 * 
 */
package spidercast.quickcluster;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import gossip.protocol.Message;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.transport.TransportEmu;





/**
 * @author vinaysetty
 * 
 */
public class TransportEmuQC extends TransportEmu
{
  public TransportEmuQC(String prefix)
  {
    super(prefix);
  }



  @Override
  public void send(Descriptor src, Descriptor dest, int pid, Object payload)
  {
    if (payload instanceof Message)
    {
      DescriptorSim dsim = (DescriptorSim) src;
      ProtocolBandwidthTracker pbt = (ProtocolBandwidthTracker) dsim.getNode().getProtocol(pid);
      Message msg = (Message) payload;
      int size = 0;
      for (Descriptor d: msg.descriptors)
      {
        DescriptorRT descRT = (DescriptorRT) d;
        size += descRT.getSize();
      }
      pbt.addTotalBitsSent(size);
      pbt.incrementTotalNumMessagesSent();
    }
    super.send(src, dest, pid, payload);
  }
}