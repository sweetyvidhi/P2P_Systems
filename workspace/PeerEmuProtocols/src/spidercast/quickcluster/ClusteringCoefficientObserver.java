/**
 * 
 */
package spidercast.quickcluster;

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
public class ClusteringCoefficientObserver implements Control
{
  Set<Integer> topicSet;
  private static String PAR_PROT = "protocol";
  int pid;
  private double ringCompleteness = 0.0;


  public ClusteringCoefficientObserver(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
    topicSet = PublicationSender.getGlobalTopicSet(pid);
  }



  /*
   * (non-Javadoc)
   * 
   * @see peeremu.core.Control#execute()
   */
  @Override
  public boolean execute()
  {
    double avgCC = 0;
    double avgCompleteness = 0;
    for (Integer topic: topicSet)
    {
      avgCC += getClusteringCoefficient(topic);
      avgCompleteness += ringCompleteness;
    }
    System.out.println(CommonState.getTime() + "\t" + avgCC/topicSet.size() + "\t" + avgCompleteness/topicSet.size());
    if(avgCompleteness == 100) return true;
    return false;
  }



  double getClusteringCoefficient(int topic)
  {
    double cc = 0.0;
    int numNodes = 0;
    ringCompleteness = 0.0;
    int ringCount = 0;
    int optimalCount = 0;
    for (int i = 0; i<Network.size(); ++i)
    {
      double localcc = 0.0;
      
      Node node = Network.get(i);
      RoutingTable rt = (RoutingTable) node.getProtocol(pid);
//      RoutingTable optimalRT = OptimalRoutingTableInitializer.optimalRT.get(node.getID());
      if (rt.hasTopic(topic)==false)
        continue;
      Set<Descriptor> neighbors = rt.getNeighborsOfTopic(topic);
      Set<Descriptor> randNeighbors = rt.getRandomNeighbors(node, topic);
//      Set<Descriptor> optimalNeighbors = optimalRT.getNeighborsOfTopic(topic);
//      for (Descriptor d: optimalNeighbors)
//      {
//        if(neighbors.contains(d))
//        {
//          ringCount++;
//        }
//      }
//      optimalCount += optimalNeighbors.size();
      int f = 0;
      for (Descriptor d: randNeighbors)
      {
        if (neighbors.contains(d)==false)
        {
          neighbors.add(d);
          f++;
          if (f==1)
            break;
        }
      }
      numNodes++;
      for (Descriptor neighbora: neighbors)
      {
        Node neighborNode = Network.getByID((int) neighbora.getID());
        RoutingTable rta = (RoutingTable) neighborNode.getProtocol(pid);
        for (Descriptor neighborb: neighbors)
        {
          if (neighborb.getID()==neighbora.getID())
            continue;
          if (rta.containsForTopic(neighborb, topic))
            localcc++;
        }
      }
      int size = neighbors.size();
      if (size>1)
        cc = cc+((double) localcc/((double) (size*(size-1))));
    }
    double avgCC = (numNodes>0) ? cc/(double) numNodes : 0;
    ringCompleteness = (optimalCount==0)? 100 : (double)ringCount/(double)optimalCount * 100;
    System.out.println(topic+"\t"+numNodes+"\t"+avgCC + "\t" + ringCompleteness);
    return avgCC;
  }
}
