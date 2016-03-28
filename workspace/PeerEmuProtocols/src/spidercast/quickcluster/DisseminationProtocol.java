/**
 * 
 */
package spidercast.quickcluster;

import gossip.descriptor.DescriptorAge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Linkable;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;





/**
 * @author vinaysetty
 * 
 */
public class DisseminationProtocol implements EDProtocol, ProtocolBandwidthTracker
{
  private static String PAR_RT = "routing";
  private static String PAR_F = "F";
  protected static int F = 0;
  int rtPid;
  protected NodeIDDistanceComparator distComp;
  int tid;
  public HashMap<Integer, Integer> messageDigest;
  public static HashMap<Long, HashMap<Integer, Integer>> numMessages;



  /**
   * 
   */
  public DisseminationProtocol(String prefix)
  {
    rtPid = Configuration.getPid(prefix+"."+PAR_RT);
    distComp = new NodeIDDistanceComparator();
    tid = FastConfig.getTransport(rtPid);
    F = Configuration.getInt(prefix+"."+PAR_F);
    messageDigest = new HashMap<Integer, Integer>();
    numMessages = new HashMap<Long, HashMap<Integer, Integer>>();
  }



  @Override
  public Object clone()
  {
    DisseminationProtocol dp = null;
    try
    {
      dp = (DisseminationProtocol) super.clone();
      dp.messageDigest = new HashMap<Integer, Integer>();
    }
    catch (CloneNotSupportedException e)
    {
      e.printStackTrace();
    }
    return dp;
  }



  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    Publication pub = (Publication) event;
    // System.out.println(pub.getSerialNum() + "\t" + node.getID());
    int serialNum = pub.getSerialNum();
    if (numMessages.containsKey(node.getID()))
    {
      HashMap<Integer, Integer> topicMessageMap = numMessages.get(node.getID());
      Integer count = (Integer) (topicMessageMap.get(serialNum));
      if (count!=null&&messageDigest.containsKey(serialNum))
        count = count+1;
      else
        count = new Integer(1);
      topicMessageMap.put(serialNum, count);
    }
    else
    {
      HashMap<Integer, Integer> topicMessageMap = new HashMap<Integer, Integer>();
      topicMessageMap.put(serialNum, 1);
      numMessages.put(node.getID(), topicMessageMap);
    }
    if (messageDigest.containsKey(serialNum))
      return;
    messageDigest.put(serialNum, pub.hop);
    pub.hop++;
    Node sender = pub.getSender();
    // Get an instance of selfDescriptor
     DescriptorRT selfDescriptor = (DescriptorRT) node.getDescriptor(rtPid);
    Publication fwdPub = pub; // XXX: Normally should clone, but omitting for
                              // performance
    fwdPub.setSender(node);
    // Get the routing table instance
    RoutingTableRingCast rt = (RoutingTableRingCast) node.getProtocol(rtPid);
    // Choose the closest neighbors to disseminate by sorting them
    int topic = pub.getTargetTopic();
    Set<Descriptor> ringLinks = rt.getRingNeighbors(topic);
    // Make sure the topic exists or there are some neighbors already otherwise
    // just return
    if (ringLinks==null)
      return;
    List<Descriptor> list = new ArrayList<Descriptor>(ringLinks);

    Set<Integer> selectedIds = new HashSet<Integer>();
    Transport tr = (Transport) node.getProtocol(tid);
    int fanout = F;
    long senderID = sender.getID();
    if (list.isEmpty()==false)
    {
      distComp.setReference(node);
      Collections.sort(list, distComp);
      Descriptor successor = list.get(0);
      Descriptor predecessor = list.get(list.size()-1);
      // Send the message to all neighbors except the link where it came from
      selectedIds.add((int) senderID);
      int successorID = (int) successor.getID();
      if (successorID!=(int)senderID)
      {
        tr.send(selfDescriptor, successor, pid, fwdPub.clone());
        selectedIds.add(successorID);
        fanout--;
      }
      long predecessorID = predecessor.getID();
      if (predecessorID!=successorID&&predecessorID!=senderID
          || senderID == node.getID())
      {
        tr.send(selfDescriptor, predecessor, pid, fwdPub.clone());
        selectedIds.add((int) predecessorID);
        fanout--;
      }
    }
    //If it is the source node, we give an exception and allow F+1 fanout
    if(senderID == node.getID())
      fanout++;
    Set<Descriptor> randomLinks = getRandomNeighbors(topic, rt, node);
    if (randomLinks.isEmpty())
      return;
    List<Descriptor> listRandomLinks = new ArrayList<Descriptor>(randomLinks);
    int count = 0;
    //respect fanout
    while (count<fanout)
    {
      Descriptor randLink = getNodeForTopic(listRandomLinks, selectedIds, node);
      if(randLink == null) return;
      selectedIds.add((int) randLink.getID());
      tr.send(selfDescriptor, randLink, pid, fwdPub.clone());
      count++;
    }
  }



  private Descriptor getNodeForTopic(List<Descriptor> nodes, Set<Integer> selectedIds, Node node)
  {
    DescriptorSim randomNeighbor = null;
    while (nodes.size()>0)
    {
      int index = CommonState.r.nextInt(nodes.size());
      randomNeighbor = (DescriptorSim) nodes.get(index);
      if (selectedIds.contains((int) randomNeighbor.getID())==false)
      {
        break;
      }
      else
      {
        randomNeighbor = null;
        nodes.remove(index);
      }
    }
    return randomNeighbor;
  }



  /**
   * @param topic
   * @param rt
   * @return
   */
  protected Set<Descriptor> getRandomNeighbors(int topic, RoutingTableRingCast rt, Node selfNode)
  {
    Set<Descriptor> randomLinks = rt.getRandomNeighbors(selfNode, topic);
    return randomLinks;
  }



  @Override
  public void addTotalBitsSent(int bytes)
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  public void addTotalBitsReceived(int bytes)
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  public void incrementTotalNumMessagesSent()
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  public int getTotalNumMessagesSent()
  {
    // TODO Auto-generated method stub
    return 0;
  }



  @Override
  public void incrementTotalNumMessagesReceived()
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  public int getTotalNumMessagesReceived()
  {
    // TODO Auto-generated method stub
    return 0;
  }



  @Override
  public int getTotalBitsReceived()
  {
    // TODO Auto-generated method stub
    return 0;
  }



  @Override
  public int getTotalBitsSent()
  {
    // TODO Auto-generated method stub
    return 0;
  }
}
