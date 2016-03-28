/**
 * 
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

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
public class OptimalRoutingTableInitializer implements Control
{
  int pid = -1;
  int vicpid = -1;
  private static String PAR_PROT = "protocol";
  private static String PAR_VIC = "vicinity";
  private static String PAR_CLEAR = "keepoptimalrt";
  public static HashMap<Long, RoutingTable> optimalRT = null;
  boolean keepOptimalRt = false;


  /**
   * 
   */
  public OptimalRoutingTableInitializer(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
    vicpid = Configuration.getPid(prefix+"."+PAR_VIC);
    keepOptimalRt = Configuration.contains(prefix+"."+PAR_CLEAR);
    optimalRT = new HashMap<Long, RoutingTable>(Network.size());
  }



  /*
   * (non-Javadoc)
   * 
   * @see peeremu.core.Control#execute()
   */
  @Override
  public boolean execute()
  {
    HashSet<Descriptor> allNodes = new HashSet<Descriptor>();
    Vector<Descriptor> vdescs = new Vector<Descriptor>();
    for (int i = 0; i<Network.size(); ++i)
    {
      Node node = Network.get(i);
      allNodes.add(node.getDescriptor(pid));
      vdescs.add(node.getDescriptor(vicpid));
    }
    for (int i = 0; i<Network.size(); ++i)
    {
      Node node = Network.get(i);
      RoutingTable rt = (RoutingTable) node.getProtocol(pid);
      VicinityQC vic = (VicinityQC) node.getProtocol(vicpid);
      CommonState.setNode(node);
      CommonState.setPid(pid);
      rt.considerNodes(allNodes);
      vic.insertReceived(node.getDescriptor(vicpid), vdescs);
      RoutingTable clone = (RoutingTable) rt.clone();
      for (Integer topic: rt.neighborMap.keySet())
        clone.neighborMap.put(topic, (Set<Descriptor>) ((HashSet<Descriptor>) rt.neighborMap.get(topic)).clone());
      optimalRT.put(node.getID(), clone);
      if(keepOptimalRt == false)
        rt.clearTopicsNeighbors();
    }
    return false;
  }
}
