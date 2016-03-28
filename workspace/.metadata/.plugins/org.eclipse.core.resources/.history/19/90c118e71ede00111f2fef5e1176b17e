package thesis.three;

import java.util.Vector;

import org.apache.commons.math3.distribution.NormalDistribution;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;

public class RobustnessProtocol implements EDProtocol,CDProtocol
{

  private static final String PAR_GROUPSIZE = "groupsize";
  private static final String PAR_HBEAT = "heartbeat";
  private static final String PAR_SUSPTHRESH="susp_thresh";
  private static final String PAR_MSGLOSS="msg_loss";
  private static final String PAR_SUSPFN="susp_function";
  
  public int groupsize;
  private long heartbeat;
  private double susp_threshold;
  private int susp_function;
  private double msg_loss;
  public double value;
  public double initval;
  public Vector<Suspicion> sp;
  public Vector<Suspicion> dead;
  public Vector<GroupDetails> gp;
  
  /**********/
  
  public RobustnessProtocol(String prefix)
  {
    groupsize = Configuration.getInt(prefix+"."+PAR_GROUPSIZE);
    heartbeat = Configuration.getLong(prefix+"."+PAR_HBEAT);
    susp_threshold = Configuration.getDouble(prefix+"."+PAR_SUSPTHRESH);
    msg_loss = Configuration.getDouble(prefix+"."+PAR_MSGLOSS);
    susp_function = Configuration.getInt(prefix+"."+PAR_SUSPFN);
    sp = new Vector<Suspicion>(groupsize-1);
    dead = new Vector<Suspicion>(groupsize-1);
    gp = new Vector<GroupDetails>(groupsize-1);
  }
  
  public Object clone()
  {
    RobustnessProtocol rp = null;
    try
    {
      rp = (RobustnessProtocol)super.clone();
    }
    catch(CloneNotSupportedException e)
    {
      e.printStackTrace();
    }
    rp.sp = (Vector<Suspicion>)sp.clone();
    rp.dead = (Vector<Suspicion>)dead.clone();
    rp.gp = (Vector<GroupDetails>)gp.clone();
    return rp;
  }
  @Override
  public void nextCycle(Node node, int protocolID)
  {
    // Calculate suspicion values
    long curtime = CommonState.getTime();
    Descriptor mydesc = node.getDescriptor(protocolID);
    int linkableID = FastConfig.getLinkable(protocolID);
    AggregationProtocol ap = (AggregationProtocol) node.getProtocol(linkableID);

    for(int i=0;i<sp.size();i++)
    {
      int j=1,flag=0,nm=0;
      double tk=0,s=0,del=0;
      if(!sp.get(i).first && !sp.get(i).second)
      {
        NormalDistribution nd = new NormalDistribution(sp.get(i).mean,Math.sqrt(sp.get(i).variance));
      do
      {
        tk=sp.get(i).lastmsgtime+(sp.get(i).mean*j);
        if(tk<=curtime)
        {
          del=(curtime-tk);
          if(susp_function==0)
          {
          //s+=(curtime-tk)/(curtime+tk);         //Delay function
            s+=(del)/(del+2);
          }
          else if(susp_function==1)
          {
          s+=nd.cumulativeProbability(curtime-tk);//Normal Distribution
          }
          nm++;
          
        }
        else
        {
          flag=1;
        }
        j++;
      }while(flag==0);
      sp.get(i).suspvalue = s;
      //System.out.println(mydesc.getID()+" "+sp.get(i).desc.getID()+" "+curtime+" "+nm+" "+s);
      }
      else
        sp.get(i).suspvalue=0;
    }
    
    
   // Send heartbeat messages with suspicion values
    for(int i=0;i<sp.size();i++)
    {
      HbeatMsg msg = new HbeatMsg();
      msg.initval = ap.getInitvalue();
      msg.value = ap.getValue();
      msg.desc = mydesc;
      msg.sp = sp;
      msg.version = ap.getRound();
      int tid = FastConfig.getTransport(protocolID);
      Transport tr = (Transport)node.getProtocol(tid);
      tr.send(mydesc, sp.get(i).desc, protocolID, msg);
      
    }  
    //Cleanup
    boolean cleanflag = true;
    for(int i=0;i<sp.size();i++)
    {
      //System.out.println(mydesc.getID() + " - " + sp.get(i).desc.getID()+"("+sp.get(i).suspvalue+")");
      cleanflag = true;
      if(sp.get(i).suspvalue>susp_threshold)  //Check if the local suspicion value of any node exceeds threshold
      {
        for(int j=0;j<gp.size()&&cleanflag;j++)        
        {
          for(int k=0;k<sp.size()&&cleanflag;k++)
          {
            if(sp.get(k).desc.getID() == gp.get(j).desc.getID() && sp.get(k).state == Suspicion.State.ALIVE && sp.get(k).suspvalue < susp_threshold)
                                              //Checking if this node's vote is to be considered
            {
                Vector <Suspicion> vec = ((GroupDetails)gp.get(j)).susp;
                for(int l=0;l<vec.size()&&cleanflag;l++)
                {
                  if(vec.get(l).desc.getID() == sp.get(i).desc.getID() && vec.get(l).suspvalue < susp_threshold)
                  {
                    cleanflag=false;
                  }
                }
            }
          }
        }
          if(cleanflag)
          {
            //System.out.println("At "+curtime+" "+node.getID()+" says "+sp.get(i).desc.getID()+" is dead");
            DeadMsg deadmsg = new DeadMsg();
            deadmsg.desc = mydesc;
            deadmsg.deadnode = sp.get(i).desc;
            for(int j=0;j<sp.size();j++)
            {
              if(j!=i)
              {
                int tid = FastConfig.getTransport(protocolID);
                Transport tr = (Transport)node.getProtocol(tid);
                tr.send(mydesc, sp.get(j).desc, protocolID, deadmsg);
              }
            }
            ap.applyCorrection(sp.get(i).value - sp.get(i).initval);
            sp.get(i).state = Suspicion.State.DEADFIRST;
            dead.add(sp.get(i));
            sp.remove(i);                
          }
        }
    }
    
    
  }

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    long curtime = CommonState.getTime();
    Descriptor mydesc = node.getDescriptor(pid);
    int linkableID = FastConfig.getLinkable(pid);
    AggregationProtocol ap = (AggregationProtocol) node.getProtocol(linkableID);
    if(event instanceof HbeatMsg) //Arrival of a heartbeat message
    {
      double decide = CommonState.r.nextDouble();
      if(decide<(1-(msg_loss/100)))
      {
      HbeatMsg m = (HbeatMsg) event;
      
      // Update
      boolean f1=false,f2=false;
      double remove=0;
      for(int i=0;i<sp.size();i++)
      {
        if(sp.get(i).desc.getID() == m.desc.getID())
        {
          if(!sp.get(i).first)
          {
            if(sp.get(i).second)
            {
              sp.get(i).second = false;
            }
      
          
          /**************************/
          if(sp.get(i).window.size()==8)
          {
            remove=sp.get(i).window.remove();
          }
          sp.get(i).window.add(new Double(curtime-sp.get(i).lastmsgtime));
          sp.get(i).sum=sp.get(i).sum-remove+(curtime-sp.get(i).lastmsgtime);
          sp.get(i).sum_variance=sp.get(i).sum_variance+Math.pow((curtime-sp.get(i).lastmsgtime),2)-Math.pow(remove, 2);
          sp.get(i).num+=1;
          sp.get(i).mean = sp.get(i).sum/sp.get(i).window.size();
          sp.get(i).variance = sp.get(i).sum_variance/sp.get(i).window.size(); 
          
          /*************************/
          sp.get(i).lastmsgtime = curtime;
          sp.get(i).value = m.value;
          sp.get(i).version =m.version;
          }
          else
          {
            sp.get(i).initval = m.initval;
            sp.get(i).first = false;
            sp.get(i).lastmsgtime = curtime;
            sp.get(i).value = m.value;
          }
          f1=true;
          break;
        }
      }
      if(!f1)
      {
        Suspicion sp1 = new Suspicion();
        sp1.desc = m.desc;
        sp1.initval = m.initval;
        sp1.first = true;
        /**************/
        
        sp1.num=1;
 
        /**************/
        sp1.lastmsgtime = curtime;
        sp1.state = Suspicion.State.ALIVE;
        sp1.suspvalue = 0;
        sp1.value = m.value;
        sp1.version = m.version;
        sp.add(sp1);
      }
      for(int i=0;i<gp.size();i++)
      {
        if(gp.get(i).desc.getID() == m.desc.getID())
        {
          gp.get(i).susp = m.sp;
          f2=true;
          break;
        }
      }
      if(!f2)
      {
        GroupDetails det = new GroupDetails();
        det.desc = m.desc;
        det.susp = m.sp;
        gp.add(det);
      }
      }
      
    }
    else if(event instanceof DeadMsg)
    {
      DeadMsg m = (DeadMsg) event;
      for(int i=0;i<sp.size();i++)
      {
        if(sp.get(i).desc.getID() == m.deadnode.getID())
        {
          sp.get(i).state = Suspicion.State.DEAD;
          dead.add(sp.get(i));
          sp.remove(i);
          break;
        }
      }
      for(int i=0;i<dead.size();i++)
      {
        if(dead.get(i).desc.getID() == m.deadnode.getID() && dead.get(i).state == Suspicion.State.DEADFIRST && m.desc.getID() < mydesc.getID())
        {
          ap.applyCorrection(dead.get(i).initval-dead.get(i).value);
          dead.get(i).state = Suspicion.State.DEAD;
        }
      }
    }
    
  }
}
