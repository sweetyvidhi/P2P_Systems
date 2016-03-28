/**
 * 
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.Set;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.Network;
import peeremu.core.Node;





/**
 * @author vinaysetty
 * 
 */
public class RingConstructionObserver implements Control
{
  int pid = -1;
  private static String PAR_PROT = "protocol";
  private static String PAR_LOAD = "measureload";
  boolean measureLoad = false;
  private static String PAR_HOPS = "measurehops";
  boolean measureHops = false;
  private static String PAR_HIT = "measurehitratio";
  boolean measureHit = false;
  private static String PAR_AGGR = "measureaggregate";
  boolean measureAggr = false;
  private static String PAR_OPT = "compareoptimal";
  boolean compareOpt = false;



  /**
   * 
   */
  public RingConstructionObserver(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
    measureLoad = (Configuration.contains(prefix+"."+PAR_LOAD)) ? true : false;
    measureHops = (Configuration.contains(prefix+"."+PAR_HOPS)) ? true : false;
    measureHit = (Configuration.contains(prefix+"."+PAR_HIT)) ? true : false;
    measureAggr = (Configuration.contains(prefix+"."+PAR_AGGR)) ? true : false;
    compareOpt = (Configuration.contains(prefix+"."+PAR_OPT)) ? true : false;
  }



  /*
   * (non-Javadoc)
   * 
   * @see peeremu.core.Control#execute()
   */
  @Override
  public boolean execute()
  {
    HashMap<Integer, Integer> ringCount = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> ringCountExpected = new HashMap<Integer, Integer>();
    int downcount = 0;
    double totalredundancyFactor = 0;
    if (compareOpt||measureLoad)
    {
      for (int i = 0; i<Network.size(); ++i)
      {
        Node node = Network.get(i);
        if (node.isUp()==false)
          downcount++;
        RoutingTable rt = (RoutingTable) node.getProtocol(pid);
        Set<Integer> topics = rt.getTopics();
        long nodeID = node.getID();
        double redundancy = 0.0;
        int numTopics = 0;
        for (Integer topic: topics)
        {
          if (compareOpt)
          {
            RoutingTable optimalRT = OptimalRoutingTableInitializer.optimalRT.get(node.getID());
            // if(topic != 660355389 || node.getID() != 614) continue;
            Set<Descriptor> set1 = rt.getNeighborsOfTopic(topic);
            Set<Descriptor> set2 = optimalRT.getNeighborsOfTopic(topic);
            int count = 0;
            int totalLinksExpected = 0;
            totalLinksExpected += set2.size();
            for (Descriptor d: set1)
            {
              if (set2.contains(d))
                count++;
              else
                System.out.print("");
            }
            if (ringCount.containsKey(topic))
            {
              int newCount = ringCount.get(topic)+count;
              ringCount.put(topic, newCount);
            }
            else
            {
              ringCount.put(topic, count);
            }
            if (ringCountExpected.containsKey(topic))
            {
              int newCount = ringCountExpected.get(topic)+totalLinksExpected;
              ringCountExpected.put(topic, newCount);
            }
            else
            {
              ringCountExpected.put(topic, totalLinksExpected);
            }
          }
          if (measureLoad)
          {
            HashMap<Integer, Integer> numMsgs = DisseminationProtocol.numMessages.get(nodeID);
            Integer messageID = PublicationSender.eventQueue.get(topic);
            if (messageID!=null)
            {
              Integer num = numMsgs.get(messageID);
              if (num!=null)
              {
                redundancy += num;
                numMsgs.remove(messageID);
                ++numTopics;
              }
            }
          }
        }
        if (measureLoad)
        {
          HashMap<Integer, Integer> numMsgs = DisseminationProtocol.numMessages.get(nodeID);
          double redundancyFactor = (redundancy>0) ? (redundancy/(double) numTopics) : 1;
          if (numMsgs!=null)
            System.out.println(nodeID+"\t"+redundancy+"\t"+numTopics+"\t"+redundancyFactor+"\t"+topics.size());
          totalredundancyFactor += redundancyFactor;
        }
      }
      if (totalredundancyFactor!=0&&measureLoad)
        System.out.print(totalredundancyFactor/Network.size()+"\t");
    }
    int totalLinksinPlace = 0;
    int totalLinksExpected = 0;
    int completeRings = 0;
    int totalPercentLinksinPlace = 0;
    PublicationSender.totalHopCount = 0;
    for (Integer topic: ringCount.keySet())
    {
      double percentComplete = 0;
      if (compareOpt)
      {
        int topicLinksinPlace = ringCount.get(topic);
        int topicLinksExpected = ringCountExpected.get(topic);
        if (topicLinksExpected==topicLinksinPlace)
        {
          completeRings++;
        }
        totalLinksExpected += topicLinksExpected;
        totalLinksinPlace += topicLinksinPlace;
        percentComplete = (topicLinksExpected==0) ? 100 : (double) topicLinksinPlace/topicLinksExpected*100;
      }

      // System.out.println( "\t" + percentComplete + "\t" + complete);
      totalPercentLinksinPlace += percentComplete;
      // if(percent < 100 && complete == true)
      // System.out.println(topic);
    }
    double avgPercentLinksinPlace = (double) totalPercentLinksinPlace/(double) ringCount.size();
    if (measureAggr)
      System.out.println(CommonState.getTime()+"\t"+completeRings+"\t"+avgPercentLinksinPlace);
    if(avgPercentLinksinPlace == 100){
      return true;
    }
    return false;
  }
}
