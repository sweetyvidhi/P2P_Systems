/*
 * Created on Jul 12, 2011 by Spyros Voulgaris
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;





public class RoutingTablePriority extends RoutingTableCommon
{
  
  
  private int rtPid;



  public RoutingTablePriority(String prefix)
  {
    super(prefix);
    rtPid = CommonState.getPid();
  }



  protected Set<Integer> getDescriptorTopics(Descriptor d)
  {
    Set<Integer> topics = ((DescriptorRT)(d)).getTopicSet();
    return topics;
  }



  public Object getAdvertisement()
  {
    HashMap<Integer, Integer> topicCoverage = new HashMap<Integer, Integer>();

    if (neighborMap==null) // Easy hack, to allow WireKOut to run.
      return topicCoverage;

    for (Integer topic: neighborMap.keySet())
    {
      Set<Descriptor> neighbors = neighborMap.get(topic);
//      if(neighbors.size() < K)
        topicCoverage.put(topic, neighbors.size());
    }

    return topicCoverage;
  }


  @Override
  public long proximity(Descriptor a, Descriptor b)
  {
    @SuppressWarnings("unchecked")
    HashMap<Integer, Integer> ref =  ((DescriptorQC)(a)).getPayload();
    @SuppressWarnings("unchecked")
    HashMap<Integer, Integer> candidate = ((DescriptorQC)(b)).getPayload();

    int refNumTopics = ref.size();
    RoutingTableRingCastPerNeighbor rt = (RoutingTableRingCastPerNeighbor) ((DescriptorSim) a).getNode().getProtocol(rtPid);
    if(rt.contains(b))
      return 0;
    long gain = 0;
    Set<Integer> keySet = ref.keySet();
    Set<Integer> candidateSet = candidate.keySet();
    if(candidateSet.size() < keySet.size())
    {
      keySet = candidate.keySet();
      candidateSet = ref.keySet();
    }
    
    for (Integer topic: keySet)
    {
      if (candidateSet.contains(topic))
      {
        int numTopicNeighbors = ref.get(topic);
        //Give preference to uncovered topics normalized by total number of topics neighbor has
        double nodegain = Math.pow(refNumTopics, (K - numTopicNeighbors))/*/(candidate.size()  * idDistance)*/;
        gain += nodegain;
      }
    }
    return gain;
  }
}
