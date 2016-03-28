/**
 * 
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.core.Protocol;





/**
 * @author vinaysetty
 * 
 */
public class TCDistributionObserver extends TopicConnectivityObserver
{
  private static HashMap<Integer, Integer> topicPopularity = null;
  private static HashMap<Integer, Integer> histogram = null;



  public TCDistributionObserver(String name)
  {
    super(name);
  }



  @Override
  public boolean execute()
  {
    Set<Integer> topicSet = getGlobalTopicSet(pid);

    topicPopularity = getTopicPopularity();
    histogram = getTopicPopularityHistogram();
    int countConnected = 0;
    int countClusters = 0;
    HashMap<Integer, Integer> connectedHistogram = new HashMap<Integer, Integer>();
    for (Integer topic: topicSet)
    {
      g = TopicGraph(topic);
      Map clst = ga.weaklyConnectedClusters(g);
      countClusters += clst.size();
      int sizeofTopic = topicPopularity.get(topic);
      if (clst.size()==1)
      {
        updateHistogram(connectedHistogram, sizeofTopic);
        countConnected++;
      }
    }
    for (Integer size: histogram.keySet())
    {
      Integer connectedTopics = 0;
      if(connectedHistogram.containsKey(size))
        connectedTopics = connectedHistogram.get(size);
      System.out.println(size+"\t"+histogram.get(size)+"\t"+
          connectedTopics+"\t"+ (histogram.get(size) - connectedTopics));
    }
    System.out.println("\n");
    return false;
  }



  /**
   * @param disconnectedHistogram
   * @param sizeofTopic
   */
  protected void updateHistogram(
      HashMap<Integer, Integer> histogram, int sizeofTopic)
  {
    if (histogram.containsKey(sizeofTopic))
    {
      int count = histogram.get(sizeofTopic);
      histogram.put(sizeofTopic, ++count);
    }
    else
    {
      histogram.put(sizeofTopic, 1);
    }
  }



  protected static HashMap<Integer, Integer> getTopicPopularityHistogram()
  {
    HashMap<Integer, Integer> histogram = new HashMap<Integer, Integer>();
    for (Integer popularity: topicPopularity.values())
    {
      if (histogram.containsKey(popularity))
      {
        int count = histogram.get(popularity);
        histogram.put(popularity, ++count);
      }
      else
      {
        histogram.put(popularity, 1);
      }
    }
    return histogram;
  }



  /**
   * 
   */
  protected HashMap<Integer, Integer> getTopicPopularity()
  {
    HashMap<Integer, Integer> topicPopularity = new HashMap<Integer, Integer>();
    for (int i = 0; i<Network.size(); i++)
    {
      Node n = Network.get(i);
      RoutingTable rt = (RoutingTable) n.getProtocol(pid);
      for (Integer topic: rt.getTopics())
      {
        if (topicPopularity.containsKey(topic))
        {
          int count = topicPopularity.get(topic);
          topicPopularity.put(topic, ++count);
        }
        else
        {
          topicPopularity.put(topic, 1);
        }
      }
    }
    return topicPopularity;
  }
}
