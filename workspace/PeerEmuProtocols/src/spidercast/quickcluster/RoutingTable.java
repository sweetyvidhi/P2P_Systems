/*
 * Created on Jun 29, 2011 by Spyros Voulgaris
 */
package spidercast.quickcluster;

import gossip.comparator.DescriptorComparator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Linkable;
import peeremu.core.Node;
import peeremu.core.Protocol;





public abstract class RoutingTable  implements Protocol, Linkable , ProtocolBandwidthTracker
{


  public int totalBitsSent ;
  public int totalBitsReceived;
  public int totalNumMessagesSent ;
  public int totalNumMessagesReceived ;
  /**
   * A Set of neighbors per topic.
   */
  protected Map<Integer, Set<Descriptor>> neighborMap = null;
  /**
   * This is filled up in degree()
   */
  protected Vector<Descriptor> neighborVector = null;
  /**
   * A descriptor to myself, for convenience (assigned once and for good)
   */
//  Descriptor selfDescr;
  /**
   * Used to define the comparator used for Vicinity's greedy clustering
   */
  protected static DescriptorComparator proxCmp;
  /**
   * Parameter k to be specified in the configuration file with the routing
   * protocol
   * */
  private static String PAR_K = "K";
  protected static int K = -1;

  public RoutingTable(String prefix)
  {
    DescriptorRT.setRoutingTablePid(CommonState.getPid());
    neighborVector = new Vector<Descriptor>();
    K = Configuration.getInt(prefix+"."+PAR_K);
    neighborMap = new HashMap<Integer, Set<Descriptor>>(100, 1.0f);
  }



  @Override
  public Object clone()
  {
    RoutingTable rt = null;
    try
    {
      rt = (RoutingTable) super.clone();
      rt.neighborMap = new HashMap<Integer, Set<Descriptor>>();
      rt.neighborVector = new Vector<Descriptor>();
      rt.totalBitsReceived = 0;
      rt.totalBitsSent = 0;
      rt.totalNumMessagesReceived = 0;
      rt.totalNumMessagesSent = 0;
    }
    catch (CloneNotSupportedException e)
    {
      e.printStackTrace();
    }
    return rt;
  }


  public void removeDeadNeighbors()
  {
    Set<Descriptor> allneighbors = getAllNeighbors();
    for (Descriptor neighbor: allneighbors)
    {
      if (((DescriptorSim) (neighbor)).getNode().isUp()==false)
        removeNeighbor(neighbor);
    }
  }

  public void removeNeighbor(Descriptor peer)
  {
    for (Set<Descriptor> neighbors: neighborMap.values())
    {
      if (neighbors.contains(peer))
      {
        neighbors.remove(peer);
      }
    }
  }
  /**
   * Called by the Vicinity protocol to feed the Routing Table with candidate
   * neighbors.
   * 
   * @param descriptors
   */
  public abstract void considerNodes(Collection<Descriptor> descriptors);



  /**
   * Prepares the data to be included when creating your descriptor.
   * 
   * @return
   */
  public abstract Object getAdvertisement();



  /**
   * Compares two nodes and returns a value denoting how close they should be.
   * The higher the return value, the closer the nodes.
   * 
   * @param a
   * @param b
   * @return
   */
  public abstract long proximity(Descriptor a, Descriptor b);



  public void subscribeToTopic(Integer topic)
  {
    neighborMap.put(topic, new HashSet<Descriptor>());
  }

  public void unSubscribeToTopic(Integer topic)
  {
    neighborMap.remove(topic);
  }
  

  public int numNonCoveredTopics()
  {
    int count = 0;
    for (Set<Descriptor> neighbors: neighborMap.values())
      if (neighbors.size()<K)
        count++;
    return count;
  }



  public Set<Descriptor> getNeighborsOfTopic(int topic)
  {
    return neighborMap.get(topic);
  }



  @Override
  public void onKill()
  {
    //assert false; // not to be called
  }
  public abstract Set<Descriptor> getRandomNeighbors(Node selfNode, int topic);





  @Override
  public int degree()
  {
    Set<Descriptor> allNeighbors = getAllNeighbors();
    return allNeighbors.size();
  }



  public Set<Descriptor> getAllNeighbors()
  {
    Set<Descriptor> allNeighbors = new HashSet<Descriptor>();
    for (Set<Descriptor> neighbors: neighborMap.values())
      allNeighbors.addAll(neighbors);
    neighborVector.clear();
    neighborVector.addAll(allNeighbors);
    return allNeighbors;
  }



  @Override
  public Descriptor getNeighbor(int i)
  {
    return neighborVector.elementAt(i);
  }



  @Override
  public boolean addNeighbor(Descriptor neighbour)
  {
    assert false; // not to be called
    return false;
  }



  @Override
  public boolean contains(Descriptor neighbor)
  {
    for (Set<Descriptor> neighbors: neighborMap.values())
      if (neighbors.contains(neighbor))
        return true;
    return false;
  }

  public boolean containsForTopic(Descriptor neighbor, int topic)
  {
    if(neighborMap.containsKey(topic) == false) return false;
    return neighborMap.get(topic).contains(neighbor);
  }

  @Override
  public void pack()
  {
    assert false; // not to be called
  }



  public DescriptorComparator getProxCmp()
  {
    if (proxCmp==null)
      proxCmp = new Proximity();
    return proxCmp;
  }

  public class Proximity implements DescriptorComparator
  {
    Descriptor ref = null;



    @Override
    public void setReference(Descriptor ref)
    {
      this.ref = (Descriptor) ref;
    }



    @Override
    public int compare(Descriptor a, Descriptor b)
    {
      assert ref!=null;
      Descriptor descrA = (Descriptor) a;
      Descriptor descrB = (Descriptor) b;
      // find the size of the topic intersections
      long scoreA = proximity(ref, descrA);
      long scoreB = proximity(ref, descrB);
      if (scoreA==scoreB)
        return 0;
      else if (scoreA>scoreB)
        return -1; // prefer A
      else
        return +1; // prefer B
    }
  }



  public boolean hasTopic(Integer topic)
  {
    return this.neighborMap.containsKey(topic);
  }



  public Set<Integer> getTopics()
  {
    return neighborMap.keySet();
  }
  
  public void clearTopics()
  {
    neighborMap.clear();
  }
  
  public void clearTopicsNeighbors()
  {
    for(Set<Descriptor> neighbors : neighborMap.values())
    {
      neighbors.clear();
    }
  }
  
  public void addTotalBitsSent(int bytes)
  {
    totalBitsSent += bytes;
  }
  
  public void addTotalBitsReceived(int bytes)
  {
    totalBitsReceived += bytes;
  }



  /**
   * @param totalNumMessagesSent the totalNumMessagesSent to set
   */
  public void incrementTotalNumMessagesSent()
  {
    this.totalNumMessagesSent++;
  }
  /**
   * @param totalNumMessagesReceived the totalNumMessagesReceived to set
   */
   public void incrementTotalNumMessagesReceived()
  {
    this.totalNumMessagesReceived++;
  }


  /**
   * @return the totalNumMessagesSent
   */
  public int getTotalNumMessagesSent()
  {
    return totalNumMessagesSent;
  }



  /**
   * @return the totalNumMessagesReceived
   */
  public int getTotalNumMessagesReceived()
  {
    return totalNumMessagesReceived;
  }
  

  public int getTotalBitsReceived()
  {
    return totalBitsReceived;
  }
  

  public int getTotalBitsSent(){
    return totalBitsSent;
  }
 
}
