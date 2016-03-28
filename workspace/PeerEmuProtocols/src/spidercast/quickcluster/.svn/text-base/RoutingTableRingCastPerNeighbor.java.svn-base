/**
 * 
 */
package spidercast.quickcluster;

import gossip.protocol.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Linkable;
import peeremu.core.Node;
import peeremu.transport.Transport;





/**
 * @author vinaysetty
 * 
 */
public class RoutingTableRingCastPerNeighbor extends RoutingTableRingCast
{
  static int nummsgsReceived = 0;
  static int nummsgsSent = 0;
  protected Map<Descriptor, Integer> neighborAge = null;
  NodeIDDistanceComparator distComp = null;
  IDDistanceComparatorDirectionLess distCompUndir = null;



  /**
   * @param prefix
   */
  public RoutingTableRingCastPerNeighbor(String prefix)
  {
    super(prefix);
    neighborAge = new HashMap<Descriptor, Integer>();
    distComp = new NodeIDDistanceComparator();
    distCompUndir = new IDDistanceComparatorDirectionLess();
  }



  Descriptor getDescByID(int id)
  {
    for (Set<Descriptor> neighbors: neighborMap.values())
    {
      for (Descriptor neighbor: neighbors)
      {
        if ((int) neighbor.getID()==id)
          return neighbor;
      }
    }
    return null;
  }



  @Override
  public Object clone()
  {
    RoutingTableRingCastPerNeighbor rt = (RoutingTableRingCastPerNeighbor) super.clone();
    rt.neighborAge = new HashMap<Descriptor, Integer>();
    return rt;
  }



  @Override
  public void clearTopicsNeighbors()
  {
    super.clearTopicsNeighbors();
    neighborAge.clear();
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



  private Vector<Descriptor> collectNeighbors(Node selfNode, int topic)
  {
    Vector<Descriptor> neighborsFromProtocol = new Vector<Descriptor>();
    Linkable linkable = (Linkable) selfNode.getProtocol(refpid);
    // Add linked protocol's neighbors
    for (int j = 0; j<linkable.degree(); j++)
    {
      DescriptorRT d = (DescriptorRT) linkable.getNeighbor(j);
      Set<Integer> topicSet = ((DescriptorRT) (d)).getTopicSet();
      if (topicSet.contains(topic)&&neighborsFromProtocol.contains(d)==false)
        neighborsFromProtocol.add(d);
    }
    return neighborsFromProtocol;
  }



  /**
   * Increments age of topics while returning the topic with maximum age
   */
  private Descriptor incrementAgeofNeighbors()
  {
    int maxAge = -1;
    Descriptor maxAgeNeighbor = null;
    Set<Descriptor> allNeighbors = getAllNeighbors();
    List<Descriptor> ageDescriptors = new ArrayList<Descriptor>(neighborAge.keySet());
    for (int i = 0; i<ageDescriptors.size(); ++i)
    {
      Descriptor neighbor = ageDescriptors.get(i);
      if (allNeighbors.contains(neighbor)==false)
      {
        // neighborAge.remove(neighbor);
        continue;
      }
      int age = neighborAge.get(neighbor)+1;
      neighborAge.put(neighbor, age);
      if (age>maxAge)
      {
        maxAge = age;
        maxAgeNeighbor = neighbor;
      }
    }
    // for(Descriptor neighbor : ageDescriptors){
    // int age = neighborAge.get(neighbor) + 1;
    // if(age > maxAge)
    // {
    // maxAge = age;
    // maxAgeNeighbor = neighbor;
    // }
    // }
    return maxAgeNeighbor;
  }



  Integer getTopicToGossip(Descriptor neighborToGossip, Descriptor selfDescr)
  {
    int topic = -1;
    ArrayList<Integer> topics = new ArrayList(((DescriptorRT) neighborToGossip).getTopicSet());
    Set<Integer> myTopics = ((DescriptorRT) selfDescr).getTopicSet();
    while (true)
    {
      int index = CommonState.r.nextInt(topics.size());
      Integer newTopic = topics.get(index);
      if (myTopics.contains(newTopic))
      {
        topic = newTopic;
        break;
      }
    }
    return topic;
  }



  @Override
  public void nextCycle(Node node, int protocolID)
  {
    // Acquire fresh own descriptor
    // Need to call getDescriptor, as it may produce gradually changing profiles
    DescriptorRT selfDescr = (DescriptorRT) node.getDescriptor(protocolID);
    Vector<Descriptor> allNeighbors = collectNeighborsFromAllProtocols(node, protocolID);
    considerNodes(allNeighbors);
    Descriptor peerDescr = null;
    peerDescr = incrementAgeofNeighbors();
    if (peerDescr==null)
      return;
    // removeNeighborAge(peerDescr);
    PublicationSender.incNodeAge((int) selfDescr.getID());
    // Remove the neighbor anticipating that it will reply
    removeNeighbor(peerDescr);
    int topicToGossip = getTopicToGossip(peerDescr, selfDescr);
    Vector<Descriptor> vDesc = new Vector<Descriptor>(neighborMap.get(topicToGossip));
    selectNeighborsToSend(selfDescr, peerDescr, topicToGossip, vDesc);
    // Send the selected neighbors to this peer.
    int tid = FastConfig.getTransport(protocolID);
    Transport tr = (Transport) node.getProtocol(tid);
    RoutingTableMessage msg = new RoutingTableMessage();
    msg.type = Message.Type.GOSSIP_REQUEST;
    List<Integer> topics = new ArrayList<Integer>(selfDescr.getTopicSet());
    Set<Integer> peerTopics = ((DescriptorRT) peerDescr).getTopicSet();
//    for (int i = 0 ; i < topics.size() ; ++i)
//    {
//      if (peerTopics.contains(topics.get(i))==false)
//      {
//        selfDescr.removeTopicFromPayload(topics.get(i));
//      }
//    }
    if(selfDescr.getSize() == 0)
      System.out.print("");
    msg.sender = selfDescr;
    msg.descriptors = vDesc;
    nummsgsSent++;
    tr.send(selfDescr, peerDescr, protocolID, msg);
  }



  private void addNeighborAge(Descriptor peerDescr)
  {
    if (neighborAge.containsKey(peerDescr)==false)
      neighborAge.put(peerDescr, 0);
    else
    {
      int age = neighborAge.get(peerDescr)+1;
      neighborAge.put(peerDescr, age);
    }
  }



  //
  // private void removeNeighborAge(Descriptor peerDescr)
  // {
  // neighborAge.remove(peerDescr.getID());
  // }
  private void resetNeighborAge(Descriptor peerDescr)
  {
    neighborAge.put(peerDescr, 0);
  }



  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    init();
    Message msg = (Message) event;
    switch (msg.type)
    {
      case GOSSIP_REQUEST:
        processGossipRequest(msg.sender, node, pid, msg.descriptors);
        break;
      case GOSSIP_RESPONSE:
      {
        msg.descriptors.add(msg.sender);
        processResponse(node, pid, msg.descriptors);
      }
    }
    incrementTotalNumMessagesReceived();
    addTotalBitsReceived(((RoutingTableMessage) (msg)).getSize());
    resetNeighborAge(msg.sender);
    nummsgsReceived++;
  }



  protected void processGossipRequest(Descriptor sender, Node selfNode, int pid, Vector<Descriptor> received)
  {
    DescriptorRT selfDescr = (DescriptorRT) selfNode.getDescriptor(pid);
    if(selfDescr.getID() == 140 && sender.getID() == 751)
      System.out.print("");
    Set<Descriptor> listNeighbors = this.getAllNeighbors();
    // include self descriptor in case your neighbor does not know about you
    listNeighbors.add(selfDescr);
    List<Descriptor> list = new ArrayList<Descriptor>(listNeighbors);
    Collections.shuffle(list, CommonState.r);
    distComp.setReference(((DescriptorSim) sender).getNode());
    Collections.sort(list, distComp);
    int topicToGossip = getTopicToGossip(sender, selfDescr);
    Vector<Descriptor> vDesc = new Vector<Descriptor>(neighborMap.get(topicToGossip));
    selectNeighborsToSend(selfDescr, sender, topicToGossip, vDesc);
    // Insert the selected neighbors to my view
    received.add(sender);
    considerNodes(received);
    List<Integer> topics = new ArrayList<Integer>(selfDescr.getTopicSet());
    Set<Integer> peerTopics = ((DescriptorRT) sender).getTopicSet();
//    for (int i = 0 ; i < topics.size() ; ++i)
//    {
//      if (peerTopics.contains(topics.get(i))==false)
//      {
//        selfDescr.removeTopicFromPayload(topics.get(i));
//      }
//    }
    // Send the selected neighbors to this peer.
    int tid = FastConfig.getTransport(pid);
    Transport tr = (Transport) selfNode.getProtocol(tid);
    RoutingTableMessage msg = new RoutingTableMessage();
    msg.type = Message.Type.GOSSIP_RESPONSE;
    msg.sender = selfDescr;
    msg.descriptors = vDesc;
    nummsgsSent++;
    tr.send(selfDescr, sender, pid, msg);
  }



  @Override
  protected Descriptor applyHashFunction(Descriptor candidate, Set<Descriptor> neighbors, Node myDesc)
  {
    Long candidateDistance = getClockwiseDistance(myDesc.getID(), candidate.getID());
    // Sort the neighbors according to ascending order of clockwise distance
    // from selfID to neighborID in the ID ring assuming largest ID possible is
    // Long.MAX_VALUE
    List<Descriptor> list = new ArrayList<Descriptor>(neighbors);
    Collections.shuffle(list, CommonState.r);
    ((NodeIDDistanceComparator) distComp).setReference(myDesc);
    Collections.sort(list, distComp);
    // Check the k/2th succesor and k/2th predecessor distance because they will
    // be the worst succesors and predecessors
    // TODO For odd number K there will be one neighbor always left out so for
    // the time being considering that neighbor as a successor.
    Descriptor worstSuccessor = list.get(((int) Math.ceil((double) list.size()/2))-1);
    Descriptor worstPredecessor = list.get(list.size()/2);
    Long worstPredecessorDist = getClockwiseDistance(myDesc.getID(), worstPredecessor.getID());
    Long worstSuccesorDist = getClockwiseDistance(myDesc.getID(), worstSuccessor.getID());
    // If the Candidate is is nearer to selfID than worstSuccessor then we
    // replace worstSuccessor by Candidate
    // And similarly worstPredecessor
    if (candidate.equals(worstSuccessor)==false&&candidateDistance<worstSuccesorDist)
    {
      neighbors.remove(worstSuccessor);
      neighbors.add(candidate);
      addNeighborAge(candidate);
      return worstSuccessor;
    }
    else if (candidate.equals(worstPredecessor)==false&&candidateDistance>worstPredecessorDist)
    {
      neighbors.remove(worstPredecessor);
      neighbors.add(candidate);
      addNeighborAge(candidate);
      return worstPredecessor;
    }
    return null;
  }



  @Override
  protected Descriptor considerNodeForTopic(Descriptor candidate, Set<Descriptor> neighbors, Node myNode)
  {
    if (neighbors.size()<K)
    {
      neighbors.add(candidate);
      addNeighborAge(candidate);
      return null;
    }
    else
      return applyHashFunction(candidate, neighbors, myNode);
  }
}
