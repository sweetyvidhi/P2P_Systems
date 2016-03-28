/*
 * Created on Jun 29, 2011 by Spyros Voulgaris
 */
package spidercast.quickcluster;

import gossip.protocol.Message;
import gossip.vicinity.VicinityED;

import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Node;





public class VicinityQC extends VicinityED implements ProtocolBandwidthTracker 
{
  public int totalBitsSent ;
  public int totalBitsReceived;
  public int totalNumMessagesSent ;
  public int totalNumMessagesReceived ;
  private static final String PAR_ROUTING = "routing";
  RoutingTable routingTable;
  boolean useHashFunction;
  static int routingTablePid;


  public VicinityQC(String prefix)
  {
    super(prefix);
    // useHashFunction = Configuration.getBoolean(prefix+"."+PAR_HASH_FUNCTION);
    routingTablePid = Configuration.getPid(prefix+"."+PAR_ROUTING);

    int pid = CommonState.getPid();
    VicinitySettingsQC vicSettings = (VicinitySettingsQC)FastConfig.getSettings(pid);

    Node currentNode = CommonState.getNode();
    RoutingTable rt = (RoutingTable) currentNode.getProtocol(routingTablePid);
    vicSettings.setProxCmp(rt.getProxCmp());
  }

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    Message msg = (Message) event;
    int size = ((DescriptorRT)(msg.sender)).getSize();
    for(Descriptor d : msg.descriptors)
    {
      DescriptorRT descRT = (DescriptorRT) d;
      size += descRT.getSize();
    }
    this.incrementTotalNumMessagesReceived();
    this.addTotalBitsReceived(size);
    super.processEvent(node, pid, event);
  }
  
  protected void insertReceived(Descriptor selfDescr, Vector<Descriptor> received)
  {
    routingTable.considerNodes(received);
    super.insertReceived(selfDescr, received);
  }



  public Object clone()
  {
    VicinityQC vic = (VicinityQC) super.clone();
    vic.routingTable = (RoutingTable) CommonState.getNode().getProtocol(routingTablePid);
    vic.totalBitsReceived = 0;
    vic.totalBitsSent = 0;
    vic.totalNumMessagesReceived = 0;
    vic.totalNumMessagesSent = 0;
    return vic;
  }



  protected void addAllNeighbors(Descriptor selfDescr, Vector<Descriptor> neighborsFromAllProtocols)
  {
    super.addAllNeighbors(selfDescr, neighborsFromAllProtocols);
    routingTable.considerNodes(neighborsFromAllProtocols);
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
