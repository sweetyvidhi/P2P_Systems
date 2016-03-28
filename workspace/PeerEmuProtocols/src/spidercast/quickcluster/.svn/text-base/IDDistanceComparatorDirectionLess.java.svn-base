/**
 * 
 */
package spidercast.quickcluster;

import peeremu.core.Descriptor;
import peeremu.core.Node;

/**
 * @author vinaysetty
 *
 */
public class IDDistanceComparatorDirectionLess extends IdDistanceComparator
{
  @Override
  public int compare(Object a, Object b)
  {
    assert ref!=null;
    long distA = RoutingTableRingCast.getDirectionlessDistance(((Node) ref).getID(), ((Descriptor) a).getID());
    long distB = RoutingTableRingCast.getDirectionlessDistance(((Node) ref).getID(), ((Descriptor) b).getID());
    if (distA==distB)
      return 0;
    else if (distA>distB)
      return +1; // prefer A
    else
      return -1; // prefer B
  }
}
