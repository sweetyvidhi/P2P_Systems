package spidercast.quickcluster;

import gossip.descriptor.DescriptorSimAge;

import java.util.HashSet;
import java.util.Set;

import peeremu.core.Node;

public class DescriptorRT extends DescriptorSimAge
{
  
  protected int age;
  protected Object payload;
  protected static int rtPid = -1;
  /**
   * To make sure that older descriptors (of a given node) are replaced by
   * newer ones, rather than lingering forever in the network.
   */
  private static int versionKeeper = 0;
  private int version;

  /**
   * Topics which the node is interested in
   * 
   * @param node
   * @param pid
   */
  
  
  public DescriptorRT(Node node, int pid)
  {
    super(node, pid);
    version = versionKeeper++;
    resetAge();
    RoutingTable rt = (RoutingTable) node.getProtocol(rtPid);
    payload = new HashSet<Integer>(rt.getTopics());
  }

  public int getSize()
  {
    return ((Set<Integer>) payload).size()*Integer.SIZE;
  }
  public Set<Integer> getTopicSet(){
    return (Set<Integer>) payload;
  }
  public void removeTopicFromPayload(Integer topic)
  {
    ((Set<Integer>) payload).remove(topic);
  }
  public static void setRoutingTablePid(int pid)
  {
    rtPid = pid;
  }



  public int getAge()
  {
    return age;
  }



  public void incAge()
  {
    age++;
  }



  public void resetAge()
  {
    age = 0;
  }



  public int getVersion()
  {
    return version;
  }



  public String toString()
  {
    return ""+getID()+"-"+getAge()+"-"+(getNode().isUp() ? "U" : "D");
  }
}
