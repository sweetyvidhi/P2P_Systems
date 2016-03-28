/*
 * Created on Aug 25, 2007 by Spyros Voulgaris
 *
 */
package gossip.descriptor;

import gossip.vicinity.TargetOverlay;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Node;

public class DescriptorSimAge extends DescriptorSim implements DescriptorAge, Comparable<Descriptor>
{
  protected int age;

  public DescriptorSimAge(Node node, int pid)
  {
    super(node, pid);
    resetAge();
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
  
//  public String toString()
//  {
//    String s = String.format("%c%d", (char)(node.getID()+65), age);
//    if (s.length()==2)
//      s += " ";
//    return s;
//  }

  public String toString()
  {
    String s = String.format("%d-%d-%d", getID(),
        TargetOverlay.distance(0, (int)getID()),
        age);
    return s;
  }

  @Override
  public int compareTo(Descriptor arg0)
  {
    DescriptorSimAge other = (DescriptorSimAge)arg0;
    
    long a = node.getID();
    long b = other.getID();
    return (a==b) ? 0 : (a<b ? -1 : 1);
  }

  @Override
  public boolean equals(Object arg0)
  {
    if (arg0 instanceof Long)
      return getID() == ((Long)arg0).longValue();
    else
      return super.equals(arg0);
  }
}
