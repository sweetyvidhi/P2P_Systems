/**
 * 
 */
package spidercast.quickcluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
public class OptimalRingConstruction implements Control
{
  Set<Integer> coveredNodes;
  boolean firstTime = true;
  private static String PAR_PROT = "protocol";
  private static String PAR_VIC = "vicinity";
  private static String PAR_CYC = "cyclon";
  int vicpid = -1;
  int cycpid = -1;
  int pid = -1;
  int numRoundsforNodes[] = new int[Network.size()];
  int numRoundsforTopics[] = null;
  HashMap<Integer, Integer> numNodesPerTopic;
  HashMap<Integer, Boolean> completeTopics;
  List<Integer> globalTopics;



  public OptimalRingConstruction(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
    if(Configuration.contains(prefix+"."+PAR_VIC))
      vicpid = Configuration.getPid(prefix+"."+PAR_VIC);
    cycpid = Configuration.getPid(prefix+"."+PAR_CYC);
    coveredNodes = new HashSet<Integer>();
    completeTopics = new HashMap<Integer, Boolean>();
    globalTopics = new ArrayList(PublicationSender.getGlobalTopicSet(pid));
    numRoundsforTopics = new int[globalTopics.size()];
    numNodesPerTopic = new HashMap<Integer, Integer>();
    for (Integer topic: globalTopics)
    {
      completeTopics.put(topic, true);
      numNodesPerTopic.put(topic, 0);
    }
  }



  @Override
  public boolean execute()
  {
    boolean allNodesConverged = true;
    int linksinPlace = 0;
    int expectedLinks = 0;
    for (int i = 0; i<Network.size(); ++i)
    {
      if (coveredNodes.contains(i)){
        linksinPlace++;
        expectedLinks++;
        continue;
      }
      Node node = Network.get(i);
      RoutingTable rt = (RoutingTable) node.getProtocol(pid);
      Set<Integer> topics = rt.getTopics();
      RoutingTable optimalRT = OptimalRoutingTableInitializer.optimalRT.get(node.getID());
      boolean thisNodeCovered = true;
      for (Integer topic: topics)
      {
        Set<Descriptor> optimalSet = optimalRT.getNeighborsOfTopic(topic);
        Set<Descriptor> mySet = rt.getNeighborsOfTopic(topic);
        expectedLinks += optimalSet.size();
        if (optimalSet.size()!=mySet.size()||optimalSet.containsAll(mySet)==false)
        {
         for(Descriptor optTopic : optimalSet)
         {
           if(mySet.contains(optTopic))
             linksinPlace++;
         }
          thisNodeCovered = false;
          completeTopics.put(topic, false);
          if (firstTime)
          {
            int nodesperTopics = numNodesPerTopic.get(topic)+1;
            numNodesPerTopic.put(topic, nodesperTopics);
          }
        }
        else{
          linksinPlace += optimalSet.size();
        }
      }
      if (thisNodeCovered)
      {
        coveredNodes.add(i);
      }
        numRoundsforNodes[i] = numRoundsforNodes[i]+1;
      allNodesConverged = allNodesConverged&&thisNodeCovered;
    }
    int index = 0;
    for (Integer topic: globalTopics)
    {
      if (completeTopics.get(topic)==false)
      {
        numRoundsforTopics[index] = numRoundsforTopics[index]+1;
      }
      index++;
    }
    firstTime = false;
    if (allNodesConverged)
    {
      int numComplereRings = 0;
      for(Boolean complete : completeTopics.values())
        if(complete)
          numComplereRings++;
      System.out.println(CommonState.getTime()+"\t"+coveredNodes.size() + "\t" + numComplereRings);
      System.out.println("\n");
      long totalbitssent = 0;
      long totalbitsreceived = 0;
      for (int i = 0; i<Network.size(); ++i)
      {
        RoutingTable optimalRT = OptimalRoutingTableInitializer.optimalRT.get((long) i);
        int numTopics = optimalRT.getTopics().size();
        RoutingTable rt = (RoutingTable) Network.get(i).getProtocol(pid);
        CyclonQC cyc = (CyclonQC) Network.get(i).getProtocol(cycpid);
        VicinityQC vic = null;
        if(vicpid != -1)
          vic = (VicinityQC) Network.get(i).getProtocol(vicpid);
        totalbitssent += rt.getTotalBitsSent() /*+ vic.getTotalBitsSent() */+cyc.getTotalBitsSent();
        if(vic != null)
          totalbitssent += vic.getTotalBitsSent();
        totalbitsreceived += rt.getTotalBitsReceived() /*+ vic.getTotalBitsReceived()*/ + cyc.getTotalBitsReceived();
        if(vic != null)
          totalbitsreceived += vic.getTotalBitsReceived();
        String result = i+"\t"+numTopics+"\t"+(numRoundsforNodes[i]+1)+ "\t" + CommonState.getTime() + "\t"
           +"\t"+rt.getTotalNumMessagesSent()+"\t"+rt.getTotalBitsSent()+"\t"+rt.getTotalNumMessagesReceived()+"\t"+rt.getTotalBitsReceived()
//           +"\t"+vic.getTotalNumMessagesSent()+"\t"+vic.getTotalBitsSent()+"\t"+vic.getTotalNumMessagesReceived()+"\t"+vic.getTotalBitsReceived()
           +"\t"+cyc.getTotalNumMessagesSent()+"\t"+cyc.getTotalBitsSent()+"\t"+cyc.getTotalNumMessagesReceived()+"\t"+cyc.getTotalBitsReceived();
        if(vic != null)
          result = result + "\t"+vic.getTotalNumMessagesSent()+"\t"+vic.getTotalBitsSent()+"\t"+vic.getTotalNumMessagesReceived()+"\t"+vic.getTotalBitsReceived();
        System.out.println(result);
      }
      double kbpssent = ((double)totalbitssent/(Network.size()*8*1024*(CommonState.getTime()/1000)));
      double kbpsrecvd = ((double)totalbitsreceived / (Network.size()*8*1024*(CommonState.getTime()/1000)));
      System.out.println("\n\n" + kbpssent +"\t"+  kbpsrecvd + "\t" + RoutingTableRingCastPerNeighbor.nummsgsReceived  + "\t" + RoutingTableRingCastPerNeighbor.nummsgsSent);
      
      System.out.println("\n");
      int ind = 0;
      for (Integer topic: globalTopics)
      {
        System.out.println(topic+"\t"+numNodesPerTopic.get(topic)+"\t"+(numRoundsforTopics[ind]+1));
        ind++;
      }
       return true;
    }
    int numComplereRings = 0;
    for(Boolean complete : completeTopics.values())
      if(complete)
        numComplereRings++;
    System.out.println(CommonState.getTime()+"\t"+coveredNodes.size() + "\t" + numComplereRings + "\t" + ((double)linksinPlace/(double)expectedLinks)*100);
    for (Integer topic: globalTopics)
    {
      completeTopics.put(topic, true);
    }
    return false;
  }
  
}
