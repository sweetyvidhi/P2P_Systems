package thesis.one;

import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.dynamics.NodeInitializer;

public class NodeGroupInitialiser implements NodeInitializer
{
 
  @Override
  public void initialize(Node n)
  {
    
    /*AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
    long myid = n.getDescriptor(pid).getID();
    int ngroups = (int)Network.size()/ap.groupsize;
    int groupid = (int)myid%ngroups;
    for(int j=groupid;j<Network.size();j+=ngroups)
    {
      Node n1=Network.get(j); 
      if(j!=myid)
        { 
          Suspicion susp = new Suspicion();
          susp.desc = n1.getDescriptor(pid);
          susp.suspvalue=0;
          susp.esttime=0;
          susp.lastmsgtime=0;
          susp.first=true;
          susp.initval=-1;
          susp.value=-1;
          susp.state = Suspicion.State.ALIVE;
          
          ap.sp.add(susp);
              
        }*/
      
    }
}
