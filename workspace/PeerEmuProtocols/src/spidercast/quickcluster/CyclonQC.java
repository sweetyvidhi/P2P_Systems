/**
 * 
 */
package spidercast.quickcluster;

import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Node;
import gossip.protocol.CyclonED;
import gossip.protocol.Message;

/**
 * @author vinaysetty
 *
 */
public class CyclonQC extends CyclonED implements ProtocolBandwidthTracker
{


  public int totalBitsSent ;
  public int totalBitsReceived;
  public int totalNumMessagesSent ;
  public int totalNumMessagesReceived ;
  private static final String PAR_ROUTING = "routing";
  RoutingTable routingTable;
  boolean useHashFunction;
  static int routingTablePid;
  public CyclonQC(String prefix)
  {
    super(prefix);
    // useHashFunction = Configuration.getBoolean(prefix+"."+PAR_HASH_FUNCTION);
    routingTablePid = Configuration.getPid(prefix+"."+PAR_ROUTING);

    int pid = CommonState.getPid();

    Node currentNode = CommonState.getNode();
    routingTable = (RoutingTable) currentNode.getProtocol(routingTablePid);
  }
  
  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    Message msg = (Message) event;
    int size = (msg.descriptors.size() + 1) * Integer.SIZE;
    this.incrementTotalNumMessagesReceived();
    this.addTotalBitsReceived(size);
    super.processEvent(node, pid, event);
  }
  
  public Object clone()
  {
    CyclonQC cyc = (CyclonQC) super.clone();
    cyc.totalBitsReceived = 0;
    cyc.totalBitsSent = 0;
    cyc.totalNumMessagesReceived = 0;
    cyc.totalNumMessagesSent = 0; 
    cyc.routingTable = (RoutingTable) CommonState.getNode().getProtocol(routingTablePid);
    return cyc;
  }
  @Override
  protected void insertReceived(Vector<Descriptor> received, Vector<Descriptor> sent, Descriptor self)
  {
    routingTable.considerNodes(received);
    routingTable.considerNodes(sent);
    super.insertReceived(received, sent, self);
  }

  
  public void addTotalBitsSent(int bytes)
  {
    totalBitsSent += bytes;
  }
  
  public void addTotalBitsReceived(int bytes)
  {
    totalBitsReceived += bytes;
  }



  /**
   * @param totalNumMessagesSent the totalNumMessagesSent to set
   */
  public void incrementTotalNumMessagesSent()
  {
    this.totalNumMessagesSent++;
  }



  /**
   * @return the totalNumMessagesSent
   */
  public int getTotalNumMessagesSent()
  {
    return totalNumMessagesSent;
  }



  /**
   * @param totalNumMessagesReceived the totalNumMessagesReceived to set
   */
   public void incrementTotalNumMessagesReceived()
  {
    this.totalNumMessagesReceived++;
  }



  /**
   * @return the totalNumMessagesReceived
   */
  public int getTotalNumMessagesReceived()
  {
    return totalNumMessagesReceived;
  }
  

  public int getTotalBitsReceived()
  {
    return totalBitsReceived;
  }
  

  public int getTotalBitsSent(){
    return totalBitsSent;
  }
}
