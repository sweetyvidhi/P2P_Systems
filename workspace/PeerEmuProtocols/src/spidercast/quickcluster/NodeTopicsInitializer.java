/**
 * 
 */
package spidercast.quickcluster;

import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Fallible;
import peeremu.core.Network;
import peeremu.core.Node;
import spidercast.workload.Topics;

/**
 * @author vinaysetty
 *
 */
public class NodeTopicsInitializer implements Control
{

  private static final String PAR_DATA = "data";
  private static final String PAR_PROT = "protocol";
  public static Topics subscriptionTopics = null;
  boolean isChurnEnabled = false;
  int pid;
  /**
   * 
   */
  public NodeTopicsInitializer(String prefix)
  {
    pid = Configuration.getPid(prefix + "." + PAR_PROT);
    subscriptionTopics = (Topics) Configuration.getInstance(prefix + "." + PAR_DATA);
    isChurnEnabled = Configuration.contains("include.control.churn");
  }



  /* (non-Javadoc)
   * @see peeremu.core.Control#execute()
   */
  @Override
  public boolean execute()
  {
    for(int i = 0; i < Network.size() ; ++i)
    {
      Node n = Network.get(i);
      if(isChurnEnabled)
        n.setFailState(Fallible.DOWN);
      RoutingTable rt = (RoutingTable) n.getProtocol(pid);
      Vector<Integer> topics = (Vector<Integer>) subscriptionTopics.getTopics(i);
      for(Integer topic : topics)
      {
        rt.subscribeToTopic(topic);
      }
    }
    return false;
  }
}
