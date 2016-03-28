/**
 * 
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.transport.Transport;





/**
 * @author vinaysetty
 * 
 */
public class PublicationSender implements Control, ProtocolBandwidthTracker
{
  private static String PAR_PROT = "protocol";
  static int rtPid;
  static int pid;
  private static String PAR_RT = "routing";
  public static HashMap<Integer, Integer> eventQueue = null;
  public static int totalHopCount = 0;
  private static String PAR_DETAIL = "detailed";
  boolean printDetails = false;
  private static int upCount = 0;
  Set<Integer> globalTopics;
  public static HashMap<Integer, Integer> ageMap = new HashMap<Integer, Integer>(Network.size());
  public static int warmupPeriod = 0;
  static boolean measureLoad = false;
  private static String PAR_LOAD = "measureload";
  HashMap<Integer, Integer> convergenceMap;

  /**
   * 
   */
  public PublicationSender(String prefix)
  {
    printDetails = (Configuration.contains(prefix+"."+PAR_DETAIL)) ? true : false;
    rtPid = Configuration.getPid(prefix+"."+PAR_RT);
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
    globalTopics = getGlobalTopicSet(rtPid);
    eventQueue = new HashMap<Integer, Integer>();
    measureLoad = (Configuration.contains(prefix+"."+PAR_LOAD)) ? true : false;
    convergenceMap = new HashMap<Integer, Integer>();
    for (Integer topic: globalTopics)
    {
      eventQueue.put(topic, 0);
    }
    for (int i = 0; i<Network.size(); i++)
    {
      Node node = Network.get(i);
      PublicationSender.ageMap.put((int) node.getID(), 0);
    }
    // globalTopics = getGlobalTopicSet(rtPid);
  }

  

  public static void incNodeAge(int id)
  {
    int newAge = (ageMap.containsKey(id)) ? ageMap.get(id)+1 : 1;
    ageMap.put((int) id, newAge);
  }

  


  public static double getHitRatio(int messageID, int topic, boolean printDetails)
  {
    int count = 0;
    int hitCount = 0;
    Integer maxHopCount = 0;
    upCount = 0;
    for (int i = 0; i<Network.size(); ++i)
    {
      Node node = Network.get(i);
      if (node.isUp()==false)
      {
        continue;
      }

      upCount++;
      RoutingTable rt = (RoutingTable) node.getProtocol(rtPid);
      DisseminationProtocol dp = (DisseminationProtocol) node.getProtocol(pid);
      if (rt.hasTopic(topic))
      {
        ++count;
        if (dp.messageDigest.containsKey(messageID))
        {
          Integer hopCount = dp.messageDigest.get(messageID);
          if (hopCount>maxHopCount)
          {
            maxHopCount = hopCount;
          }
          ++hitCount;

          dp.messageDigest.remove(messageID);
        }
        else
        {
          if (ageMap.get(node.getIndex())<warmupPeriod)
          {
            hitCount++;
          }
        }
      }
     
    }
//    if (totalredundancyFactor!=0&&measureLoad)
//      System.out.print(totalredundancyFactor/Network.size()+"\t");
//    DisseminationProtocol.numMessages.clear();
    int disconnected = count-hitCount;
    if (printDetails)
      System.out.println(CommonState.getTime()+"\t"+topic+"\t"+hitCount+"\t"+count+"\t"+disconnected+"\t"+maxHopCount);
    totalHopCount += maxHopCount;
    // This should never happen but sometimes messageID is -1 indicating some
    // topics were never published
    if (count==0||messageID==-1)
    {
      return 100;
    }
    double hitRatio = (double) (hitCount/(double) count)*100;
    // System.out.println(count);
//     if(hitRatio < 100)
//     {
//     System.out.println("broken ring "+topic+"\t");
//     }
    return hitRatio;
  }



  /*
   * (non-Javadoc)
   * 
   * @see peeremu.core.Control#execute()
   */
  @Override
  public boolean execute()
  {
    PublicationSender.totalHopCount = 0;
    int totalhitRatio = 0;
    globalTopics.clear();
    globalTopics = getGlobalTopicSet(rtPid);
//    incrementNodeAge();
    for (Integer topic: globalTopics)
    {
      Integer msgID = PublicationSender.sendPublication(topic);
      int oldMsgID = PublicationSender.eventQueue.get(topic);
      PublicationSender.eventQueue.put(topic, msgID);
      if (oldMsgID!=-1)
      {
        totalhitRatio += PublicationSender.getHitRatio(oldMsgID, topic, printDetails);
      }
      else
      {
        totalhitRatio += 100;
      }
    }
    double avgHopCount = (double) PublicationSender.totalHopCount/(double) globalTopics.size();
    double avgHitRatio = (double) totalhitRatio/(double) globalTopics.size();
//    if (printDetails==false && measureLoad == false)
    if(printDetails)
      System.out.println("\n");
      System.out.println(CommonState.getTime()+"\t"+avgHopCount+"\t"+avgHitRatio+"\t"+upCount + "\t");
//    else
//      System.out.println();
    if(printDetails)
      System.out.println("\n");
    if(measureLoad)
      measureLoad();
    // if (avgHitRatio==100)
    // return true;
    return false;
  }


  public void measureLoad()
  {
    double totalRedundancy = 0.0;
    int count = 0;
    for (int i = 0; i<Network.size(); ++i)
    {
      Node node = Network.get(i);
      double redundancy = 0.0;
      long id = node.getID();
      HashMap<Integer, Integer> numMsgs = DisseminationProtocol.numMessages.get(id);        
      if(numMsgs == null) continue; 
      count++;
      for(Integer messageID : numMsgs.keySet())
      {
        redundancy += numMsgs.get(messageID);
      }
      double redundancyFactor = (double) redundancy / numMsgs.size();
      totalRedundancy += redundancyFactor;
      if(printDetails)
      System.out.println(id+"\t"+redundancy+"\t"+numMsgs.size()+"\t"+redundancyFactor+"\t");
    }
    System.out.println("\n\n");
    System.out.println((double)totalRedundancy/(double)count);
    System.out.println("\n\n");
    DisseminationProtocol.numMessages.clear();
  }

  public static Node getNodeForTopic(int topic)
  {
    int count = 0;

    while (true)
    {
      int i = CommonState.r.nextInt(Network.size());
      Node node = Network.get(i);
      count++;
      if (node.isUp() && PublicationSender.ageMap.get((int)node.getID()) >= PublicationSender.warmupPeriod)
      {
        RoutingTable rt = (RoutingTable) node.getProtocol(rtPid);
        Set<Integer> topicSet = rt.getTopics();
        if (topicSet.contains(topic))
        {
          return node;
        }
      }
      if (Network.size()==count)
        return null;
    }
  }



  /**
   * @return
   */
  public static int sendPublication(int topicToPublish)
  {
    Node startNode = getNodeForTopic(topicToPublish);
    if (startNode==null)
      return -1;
    Descriptor desc = (Descriptor) startNode.getDescriptor(rtPid);
    Publication fwdPub = new Publication("blah", startNode, topicToPublish);
    // Send the selected neighbors to this peer.
    int tid = FastConfig.getTransport(pid);
    Transport tr = (Transport) startNode.getProtocol(tid);
    tr.send(desc, desc, pid, fwdPub);
    return fwdPub.getSerialNum();
  }



  public static void incrementNodeAge()
  {
    for (int i = 0; i<Network.size(); ++i)
    {
      Node node = Network.get(i);
      PublicationSender.incNodeAge(node.getIndex());
    }
  }



  public static Set<Integer> getGlobalTopicSet(int pid)
  {
    Set<Integer> topicSet = new HashSet<Integer>();
    for (int i = 0; i<Network.size(); i++)
    {
      Node n = Network.get(i);
      RoutingTable rt = (RoutingTable) n.getProtocol(pid);
      topicSet.addAll(rt.getTopics());
    }
    return topicSet;
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
