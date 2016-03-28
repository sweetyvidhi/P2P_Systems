/**
 * 
 */
package spidercast.quickcluster;

import java.io.Serializable;

import peeremu.core.Descriptor;

import gossip.protocol.Message;

/**
 * @author vinaysetty
 *
 */
public class RoutingTableMessage extends Message
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  int topic;
  /**
   * 
   */
  public RoutingTableMessage()
  {
    // TODO Auto-generated constructor stub
  }
  
  /*
   * Returns size of the message in number of bits
   */
  public int getSize(){
    int size = 0;
    for(Descriptor d : this.descriptors)
    {
      DescriptorRT descRT = (DescriptorRT) d;
      size += descRT.getSize();
    }
    return size;
  }
}
