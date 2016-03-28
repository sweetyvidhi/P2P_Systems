/*
 * Created on Dec 7, 2004 by Spyros Voulgaris
 *
 */
package gossip.vicinity;

import java.util.Comparator;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;


public abstract class VicinitySettings
{
  /**
   *  View size of the protocol.
   */
  private static final String PAR_VIEWLEN= "view";

  /**
   * Number of items to gossip in each cycle.
   * 
   * It can trivially be 0, but then it makes sense only if this protocol's
   * view is fed exclusively by its underlying protocol.
   */
  private static final String PAR_GOSSIPLEN = "gossip";



  // Protocol specific parameters
  public int viewLen;
  public int gossipLen;
  public int pid = -1;

  public Comparator<Descriptor> duplCmp;

  /** 
   * Specifies whether the view of underlying linkables
   * should be considered to send links to other peers.
   */
  private static final String PAR_SEND_LINKABLE_ITEMS = "sendLinkableItems";


  // Protocol specific parameters
  boolean sendLinkableItems;


  public VicinitySettings(String prefix)
  {
    // cut off the last component of prefix
    // e.g., protocol.vicinity.settings -> protocol.vicinity
    prefix = prefix.substring(0, prefix.lastIndexOf("."));

    /**
     * Default value is -1, so that the check "if (gossipLen==0)"
     * doesn't block a generalized nextCycle() for protocol instances
     * for which this parameter is irrelevant (like in VicinityCoverage).
     */
    gossipLen = Configuration.getInt(prefix+"."+PAR_GOSSIPLEN, -1);

    /**
     * Default value is 0, so that the 'items' array is (trivially)
     * initialized to a vector of size 0, instead of raising an
     * exception for protocols for which this parameter is
     * irrelevant (like in VicinityCoverage).
     */
    viewLen = Configuration.getInt(prefix+"."+PAR_VIEWLEN, 0);

    pid = CommonState.getPid();
    sendLinkableItems = Configuration.getBoolean(prefix+"."+PAR_SEND_LINKABLE_ITEMS);

    // If 'sendLinkableItems' is set, make sure I am linked to some protocol.
    if (sendLinkableItems && !FastConfig.hasLinkable(pid))
    {
      sendLinkableItems = false;
      System.err.println(
          "Warning: Setting 'sendLinkableItems' to false, due to lack of linkables");
    }
  }
  

  /**
   * Selects 'howmany' descriptors most proximal to 'ref' out of 'pool',
   * excluding 'ref' itself and any descriptors in 'exclude'.
   * 
   * @param ref Descriptor distances are computed w.r.t. this reference descriptor
   * @param pool Pool of descriptors to select from
   * @param exclude Descriptors to exclude from the selection
   * @param howmany Maximum number of descriptors to return
   * @return selected descriptors, all cloned
   */
  public abstract Vector<Descriptor> selectProximal(Descriptor ref, Vector<Descriptor> pool, Vector<Descriptor> exclude, int howmany);

  /**
   * 
   * @param pool Pool of descriptors to select from
   * @param remove If true, remove the selected descriptor from the pool
   * @return selected descriptor
   */
  public abstract Descriptor selectToGossip(Vector<Descriptor> pool, boolean remove);
}
