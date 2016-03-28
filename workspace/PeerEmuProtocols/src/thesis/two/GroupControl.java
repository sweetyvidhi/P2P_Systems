package thesis.two;


import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.Network;
import peeremu.core.Node;

public class GroupControl implements Control
{
  private int pid;
  private boolean first=true;
  private int maxsize;
  private int maxnodes;
  private static final String PAR_MAXSIZE="maxsize";
  private static final String PAR_MAXNODES="maxnodes";
  private Vector v = new Vector();
  public GroupControl(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
    maxsize = Configuration.getInt(prefix+"."+PAR_MAXSIZE);
    maxnodes = Configuration.getInt(prefix+"."+PAR_MAXNODES);
  }
  @Override
  public boolean execute()
  {
    int i,j,k,ngroups=0;
    double sum=0.0;
    double sum1=0.0;
    long curtime = CommonState.getTime();
//    for(i=0;i<Network.size();i++)
//    {
//      Node n = Network.get(i);
//      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
//      System.out.println(curtime+" : "+n.getID() + " " + ap.value);
//      sum1+=ap.value;
//    }
//    System.out.println("Sum "+sum1);
    if(first)
    {
//    for(i=0;i<maxgroups;i++)
//    {
//      Vector t = new Vector();
//      v.add(t);
//    }
    for(i=0;i<Network.size();i++)
    {
      Node n = Network.get(i);
      long myid = n.getDescriptor(pid).getID();
      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
      ngroups = (int)Network.size()/ap.groupsize;
      if(ngroups==0)
        ngroups=1;
      int groupid = (int)myid%ngroups;
      // System.out.println("My id "+myid+" Group : "+groupid+" Initval "+ap.initval); 
      for(j=groupid;j<Network.size();j+=ngroups)
      {
        Node n1=Network.get(j); 
        if(j!=i)
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
            GroupDetails det = new GroupDetails();
            det.desc = n1.getDescriptor(pid);
            det.susp = new Vector<Suspicion>();
            ap.gp.add(det);
            //System.out.print(j+" ");
          }
        
      }
      sum+=ap.initval;
    }
    for(i=0;i<ngroups;i++)
    {
      Vector t = new Vector();
      for(j=i;j<Network.size();j+=ngroups)
      {
        Node n1 = Network.get(j);
        t.add(n1);
      }
      v.add(t);
    }
    double avg = sum/Network.size();
    for(i=0;i<Network.size();i++)
    {
      Node n = Network.get(i);
      AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
      ap.trueavg = avg;
    }
    System.out.println("Average "+avg);         //Experiment Failure1
    first = false;
    }
    else
    {
      //Cleanup
      for(i=0;i<v.size();i++)
      {
        Vector descvec = (Vector)v.get(i);
        for(j=0;j<descvec.size();j++)
        {
          if(!((Node)descvec.get(j)).isUp())
          {
            descvec.remove(j);
          }
        }
      }
      //Regrouping 
      for(i=0;i<Network.size();i++)
      {
        Node n = Network.get(i);
        AggregationProtocol ap = (AggregationProtocol)n.getProtocol(pid);
        int vflag=0;
        if(n.isUp() && ap.sp.size()==0)      //Node which is not grouped yet
        {
          
          //Find a suitable group
          for(j=0;j<v.size();j++)
          {
            if(((Vector)v.get(j)).size()<maxsize) //Add the group members to ap.sp
            { 
              Vector temp = (Vector)v.get(j); 
              vflag=1;
              for(k=0;k<temp.size();k++)
              {
                Node n1 = (Node)temp.get(k);
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
                GroupDetails det = new GroupDetails();
                det.desc = n1.getDescriptor(pid);
                det.susp = new Vector<Suspicion>();
                ap.gp.add(det);
                AggregationProtocol ap1 = (AggregationProtocol)n1.getProtocol(pid);
                susp = new Suspicion();
                susp.desc = n.getDescriptor(pid);
                susp.suspvalue=0;
                susp.esttime=0;
                susp.lastmsgtime=0;
                susp.first=true;
                susp.initval=-1;
                susp.value=-1;
                susp.state = Suspicion.State.ALIVE;
                ap1.sp.add(susp);
              }
              ((Vector)v.get(j)).add(n);
              break;
            }
          }
          if(vflag==0)
          {
            Vector t = new Vector();
            t.add(n);
            v.add(t);
            //System.out.println("Added new group!");
          }
        }
      }
    }
      //System.out.println("New .. ");
      for(i=0;i<v.size();i++)
      {
        Vector temp = (Vector)v.get(i);
        //System.out.println("Group "+i+" : ");
        for(j=0;j<temp.size();j++)
        {
          //System.out.print(((Node)temp.get(j)).getID()+" ");
        }
      }
    
    
    return false;
  }
  
}
