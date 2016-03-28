/*
 * Created on May 14, 2010 by Spyros Voulgaris
 */
package spidercast.quickcluster;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Linkable;
import peeremu.core.Network;
import peeremu.util.IncrementalFreq;





public class CoverageObserver implements Control
{
  private static String PAR_PROTOCOL = "protocol";
  private int pid;



  public CoverageObserver(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
  }



  @Override
  public boolean execute()
  {
    IncrementalFreq freq = new IncrementalFreq();
    int completeNodes = 0;

    for (int i = 0; i<Network.size(); i++)
    {
      Linkable prot = (Linkable) Network.get(i).getProtocol(pid);
      RoutingTable rt = ((VicinityQC)prot).routingTable;
      int nonCovered = rt.numNonCoveredTopics();
      freq.add(nonCovered);
      if (nonCovered == 0)
        completeNodes++;
    }

    System.out.println(CommonState.getTime()+"\t"+freq);
    //System.out.print(CommonState.getTime()+"\t"+completeNodes);

    if (completeNodes==Network.size())
      return true;
    else
      return false;
  }
}
