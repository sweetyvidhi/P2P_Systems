/**
 * 
 */
package spidercast.quickcluster;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Linkable;
import peeremu.core.Network;
import peeremu.core.Node;





/**
 * @author vinaysetty
 * 
 */
public class DegreeObserver implements Control
{
  int pid;
  private static final String PAR_AGGR = "aggregate";
  boolean aggr = false;



  public DegreeObserver(String name)
  {
    pid = Configuration.getPid(name+"."+PAR_PROTOCOL);
    aggr = Configuration.contains(name+"."+PAR_AGGR) ? true : false;
  }



  protected double getAverageNodeDegree()
  {
    int totalDegree = 0;
    for (int i = 0; i<Network.size(); i++)
    {
      Node node = Network.get(i);
      Linkable l = (Linkable) node.getProtocol(pid);
      RoutingTable rt = (RoutingTable)(l);
      int degree = rt.degree();
      totalDegree += degree;
      if (aggr==false)
      System.out.println(rt.neighborMap.size()+"\t"+degree);
    }
    return (double) totalDegree/(double) Network.size();
  }



  @Override
  public boolean execute()
  {
    if (aggr==false)
      getAverageNodeDegree();
    else
      System.out.print(CommonState.getTime()+"\t"+getAverageNodeDegree()+"\t");
    System.out.println();
    return false;
  }
}
