/**
 * 
 */
package spidercast.quickcluster;

import java.util.Random;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.Fallible;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.dynamics.NodeInitializer;





/**
 * @author vinaysetty
 * 
 */
public class NodeSubscriptionInitializer implements NodeInitializer
{
  // --------------------------------------------------------------------------
  // Parameters
  // --------------------------------------------------------------------------
  /**
   * The protocol to operate on.
   * 
   * @config
   */
  private static final String PAR_PROT = "protocol";
  // --------------------------------------------------------------------------
  // Fields
  // --------------------------------------------------------------------------
  /**
   * The protocol we want to initialize
   */
  private final int pid;



  /**
   * 
   */
  public NodeSubscriptionInitializer(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROT);
  }



  /*
   * (non-Javadoc)
   * 
   * @see peeremu.dynamics.NodeInitializer#initialize(peeremu.core.Node)
   */
  @Override
  public void initialize(Node n)
  {
    RoutingTable rt = (RoutingTable) n.getProtocol(pid);
    Random rand = new Random(0); // ALWAYS give a constant seed to Random!! Else
                                 // experiments are not repeatable.
    int index = rand.nextInt(1050);
    Vector<Integer> topics = (Vector<Integer>) NodeTopicsInitializer.subscriptionTopics.getTopics(index);
    for (Integer topic: topics)
    {
      rt.subscribeToTopic(topic);
    }
  }
}
