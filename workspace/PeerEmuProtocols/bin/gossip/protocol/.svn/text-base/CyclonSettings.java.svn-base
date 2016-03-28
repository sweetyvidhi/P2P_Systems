/*
 * Created on Nov 26, 2004 by Spyros Voulgaris
 *
 */
package gossip.protocol;

import gossip.comparator.DescriptorComparator;
import gossip.comparator.Random;

import java.util.Comparator;


import peeremu.config.Configuration;
import peeremu.core.CommonState;

/**
 * @author Spyros Voulgaris
 *
 */
public class CyclonSettings
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

  /**
   *  Name of class to be used for ItemSim in this protocol.
   */
  private static final String PAR_PIGGYBACK = "piggyback";

  /**
   *  Name of class to be used as a comparator for selecting a peer to gossip.
   */
  private static final String PAR_SELECT_COMPARATOR = "select";

  /**
   *  Name of class to be used as a comparator for items to send.
   */
  private static final String PAR_SEND_COMPARATOR = "send";

  /**
   *  Name of class to be used as a comparator for items to keep.
   */
  private static final String PAR_KEEP_COMPARATOR = "keep";


  /**
   *  Name of class to be used as a comparator for duplicate item conflicts.
   */
  private static final String PAR_DUPL_COMPARATOR = "duplicate";



  // Protocol specific parameters
  public int viewLen;
  public int gossipLen;
  public int pid = -1;
  public boolean piggyback;


  // Instances of ItemComparator classes
  /**
   * Comparator for selecting neighbor to gossip with.
   * The 'min' neighbor will be picked for gossiping.
   */
  public Comparator selectComparator = null;            // initialize with null
  
  /**
   * Comparator for defining the priority for neighbors to keep.
   * Typically up to cachesize 'min' neighbors will be kept.
   */
  public DescriptorComparator sendComparator = new Random(null); // initialize with random

  /**
   * Comparator for defining the priority for neighbors to send.
   * Typically up to gossiplen 'min' neighbors will be sent.
   */
  public DescriptorComparator keepComparator = new Random(null); // initialize with random

  /**
   * Comparator for selecting item to keep, in case of duplicate conflict.
   * The minimun item will be kept.
   */
  public DescriptorComparator duplComparator = new Random(null); // initialize with random




  public CyclonSettings(String prefix)
  {
    String confProperty;

    // cut off the last component of prefix
    // e.g., protocol.cyclon.settings -> protocol.cyclon
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
    piggyback = Configuration.contains(prefix+"."+PAR_PIGGYBACK);


    /*
     * Apply reflection for comparators.
     * We catch MissingParameterException to make these parameters optional.
     */

    // selectComparator
    confProperty = prefix+"."+PAR_SELECT_COMPARATOR;
    if (Configuration.contains(confProperty))
      selectComparator = (DescriptorComparator)Configuration.getInstance(confProperty);

    // sendComparator
    confProperty = prefix+"."+PAR_SEND_COMPARATOR;
    if (Configuration.contains(confProperty))
      sendComparator = (DescriptorComparator)Configuration.getInstance(confProperty);

    // keepComparator
    confProperty = prefix+"."+PAR_KEEP_COMPARATOR;
    if (Configuration.contains(confProperty))
      keepComparator = (DescriptorComparator)Configuration.getInstance(confProperty);

    // duplComparator
    confProperty = prefix+"."+PAR_DUPL_COMPARATOR;
    if (Configuration.contains(confProperty))
      duplComparator = (DescriptorComparator)Configuration.getInstance(confProperty);
  }
}
