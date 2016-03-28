package spidercast.quickcluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Linkable;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.graph.Graph;
import peeremu.graph.NeighborListGraph;
import peeremu.reports.ConnectivityObserver;





public class TopicConnectivityObserver extends ConnectivityObserver
{
  /**
   * Standard constructor that reads the configuration parameters. Invoked by
   * the simulation engine.
   * 
   * @param name the configuration prefix for this class
   */
  public TopicConnectivityObserver(String name)
  {
    super(name);
  }



  public boolean execute()
  {
    Set<Integer> topicSet = getGlobalTopicSet(pid);
    int countConnected = 0;
    int countClusters = 0;
    for (Integer topic: topicSet)
    {
      g = TopicGraph(topic);
      Map clst = ga.weaklyConnectedClusters(g);
      countClusters += clst.size();
      if (clst.size()==1)
        countConnected++;
    }
    System.out.println(CommonState.getTime()+
        " connected: "+countConnected+ " / " + topicSet.size() +
        " clusters: "+countClusters);
    if (countConnected==topicSet.size())
      return true;
    else
      return false;
  }



  protected Graph TopicGraph(int topic)
  {
    NeighborListGraph nlg = new NeighborListGraph(true);
    HashMap<Node, Integer> indexes = new HashMap<Node, Integer>();

    // First, add the nodes that have that topic.
    for (int i = 0; i<Network.size(); i++)
    {
      Node node = Network.get(i);
      Linkable l = (Linkable) node.getProtocol(pid);
      RoutingTable rt = ((VicinityQC)l).routingTable;
      // If it has this topic, add the node
      if (rt.hasTopic(topic))
      {
        int index = nlg.addNode(node);
        indexes.put(node, index);
      }
    }

    // Second, add their neighbors
    for (Node node: indexes.keySet())
    {
      Linkable l = (Linkable) node.getProtocol(pid);
      RoutingTable rt = ((VicinityQC)l).routingTable;
      Set<Descriptor> neighbors = rt.getNeighborsOfTopic(topic);

      for (Descriptor peerDescr: neighbors)
      {
        Node peer = ((DescriptorSim) peerDescr).getNode();
        if (rt.hasTopic(topic))
        {
          int nodeIndex = indexes.get(node);
          int peerIndex = indexes.get(peer);
          nlg.setEdge(nodeIndex, peerIndex);
        }
      }
    }
    return nlg;
  }



  public static Set<Integer> getGlobalTopicSet(int pid)
  {
    Set<Integer> topicSet = new HashSet<Integer>();
    for (int i = 0; i<Network.size(); i++)
    {
      Node n = Network.get(i);
      Linkable l = (Linkable) n.getProtocol(pid);
      RoutingTable rt = ((VicinityQC)l).routingTable;
      topicSet.addAll(rt.getTopics());
    }
    return topicSet;
  }
}
