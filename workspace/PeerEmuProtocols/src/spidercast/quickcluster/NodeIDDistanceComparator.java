/**
 * 
 */
package spidercast.quickcluster;

import java.util.Comparator;

import peeremu.core.Descriptor;
import peeremu.core.Node;

/**
 * @author vinaysetty
 *
 */
public class NodeIDDistanceComparator implements Comparator<Object>
{
  public Object ref;



  public void setReference(Object ref)
  {
    this.ref = (Object) ref;
  }



  @Override
  public int compare(Object a, Object b)
  {
    assert ref!=null;
    long distA = RoutingTableRingCast.getClockwiseDistance(((Node) (ref)).getID(), ((Descriptor) a).getID());
    long distB = RoutingTableRingCast.getClockwiseDistance(((Node) ref).getID(), ((Descriptor) b).getID());
    if (distA==distB)
      return 0;
    else if (distA>distB)
      return +1; // prefer A
    else
      return -1; // prefer B
  }
}
