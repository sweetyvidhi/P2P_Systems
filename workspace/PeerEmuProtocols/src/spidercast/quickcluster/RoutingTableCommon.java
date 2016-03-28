/*
 * Created on Jul 12, 2011 by Spyros Voulgaris
 *
 */
package spidercast.quickcluster;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Linkable;
import peeremu.core.Node;
public class RoutingTableCommon extends RoutingTable
{
  
  static int refpid;

  protected int selfPID;
  private static final String PAR_PROT = "refprotocol";
  public RoutingTableCommon(String prefix)
  {
    super(prefix);
    String protocol = Configuration.getString(prefix+"."+PAR_PROT);
    if (protocol.compareTo("all") != 0)
      refpid = Configuration.getPid(prefix+"."+PAR_PROT);
    else
      refpid = -1;
    selfPID = CommonState.getPid();
  }

  @Override
  public Set<Descriptor> getRandomNeighbors(Node selfNode, int topic)
  {
    Vector<Descriptor> neighborsFromAllProtocols = null;
    if (refpid==-1)
      neighborsFromAllProtocols = collectNeighborFromAllProtocols(selfNode, selfPID, topic);
    else
      neighborsFromAllProtocols = collectNeighbors(selfNode, topic);
    return new HashSet<Descriptor>(neighborsFromAllProtocols);
  }


  protected Vector<Descriptor> collectNeighborFromAllProtocols(Node selfNode, int pid, int topic)
  {
    // If no protocols are linked, return the view, as is.
    if (!FastConfig.hasLinkable(pid))
      return null;
    Vector<Descriptor> neighborsFromAllProtocols = new Vector<Descriptor>();
    // Then collect neighbors from linked protocols
    for (int i = 0; i<FastConfig.numLinkables(pid); i++)
    {
      int linkableID = FastConfig.getLinkable(pid, i);
      Linkable linkable = (Linkable) selfNode.getProtocol(linkableID);
      // Add linked protocol's neighbors
      for (int j = 0; j<linkable.degree(); j++)
      {
        DescriptorRT d = (DescriptorRT) linkable.getNeighbor(j);
        Set<Integer> topicSet = ((DescriptorRT)(d)).getTopicSet();
        if (topicSet.contains(topic)&&neighborsFromAllProtocols.contains(d)==false)
          try
          {
            neighborsFromAllProtocols.add((Descriptor) d.clone());
          }
          catch (CloneNotSupportedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
      }
    }
    return neighborsFromAllProtocols;
  }

  private Vector<Descriptor> collectNeighbors(Node selfNode, int topic)
  {
   
    Vector<Descriptor> neighborsFromAllProtocols = new Vector<Descriptor>();
  
      Linkable linkable = (Linkable) selfNode.getProtocol(refpid);
      // Add linked protocol's neighbors
      for (int j = 0; j<linkable.degree(); j++)
      {
        DescriptorRT d = (DescriptorRT) linkable.getNeighbor(j);
        Set<Integer> topicSet = ((DescriptorRT)(d)).getTopicSet();
        if (topicSet.contains(topic)&&neighborsFromAllProtocols.contains(d)==false)
          neighborsFromAllProtocols.add(d);
      }
    return neighborsFromAllProtocols;
  }


  public void considerNodes(Collection<Descriptor> descriptors)
  {
    //System.out.println(CommonState.getTime());
    Node node = CommonState.getNode();
//    Descriptor 
//    selfDescr = node.getDescriptor(CommonState.getPid());
    // go through all received neighbors
    for (Descriptor d: descriptors)
    {
      if (node.getID() == d.getID())
        continue;

      // loop through all topics supported by this neighbor
      Set<Integer> topics = getDescriptorTopics(d);
      for (Integer topic: topics)
      {
        // Check if I still need a neighbor for this topic
        Set<Descriptor> neighbors = neighborMap.get(topic);

        // check if I am interested in this topic
        if (neighbors==null)
          continue;

        // Now, check if we should add this neighbor.
        considerNodeForTopic(d, neighbors, node);
      }
    }
  }



  protected Set<Integer> getDescriptorTopics(Descriptor d)
  {
    return ((DescriptorRT)(d)).getTopicSet();
  }



  protected Descriptor considerNodeForTopic(Descriptor candidate, Set<Descriptor> neighbors, Node myDesc)
  {
    if (neighbors.size() < K){
      neighbors.add(candidate);
    }
    return null;
  }



  public Object getAdvertisement()
  {
    HashSet<Integer> set = new HashSet<Integer>();
    if (neighborMap==null) // Easy hack, to allow WireKOut to run.
      return set;
    for (Integer topic: neighborMap.keySet())
    {
      Set<Descriptor> neighbors = neighborMap.get(topic);
      if (neighbors.size()<1)
        set.add(topic);
    }
    return set;
  }



  @Override
  public long proximity(Descriptor a, Descriptor b)
  {
    Set setA = ((DescriptorRT)(a)).getTopicSet();
    Set setB = ((DescriptorRT)(b)).getTopicSet();
    
    if (setA.size()>setB.size()) // swap
    {
      Set set = setA;
      setA = setB;
      setB = set;
    }
    int count = 0;
    for (Object x: setA)
      if (setB.contains(x))
        count++;
    return count;
  }



}
