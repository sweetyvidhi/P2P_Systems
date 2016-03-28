package thesis.two;

import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Linkable;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.edsim.EDSimulator;
import peeremu.transport.Transport;

public class AggregationProtocol implements EDProtocol,CDProtocol
{
  private static final String PAR_SIZE = "size";
  private static final String PAR_GROUPSIZE = "groupsize";
  private static final String PAR_HBEAT = "heartbeat";
  private static final String PAR_CLEANUP = "cleanup";
  private static final String PAR_SUSPTHRESH="susp_thresh";
  private static final String PAR_CONFIDENCE="confidence";
  private int size;
  public int groupsize;
  private long heartbeat;
  private long cleanup;
  private double susp_threshold;
  public double value;
  private boolean first = true;
  public double initval;
  public double trueavg;
  public int round;
  public int confidence;
  public Vector<Suspicion> sp;
  public Vector<Suspicion> dead;
  public Vector<GroupDetails> gp;
  
  public AggregationProtocol(String prefix)
  {
    size = Configuration.getInt(prefix+"."+PAR_SIZE);
    groupsize = Configuration.getInt(prefix+"."+PAR_GROUPSIZE);
    heartbeat = Configuration.getLong(prefix+"."+PAR_HBEAT);
    cleanup = Configuration.getLong(prefix+"."+PAR_CLEANUP);
    susp_threshold = Configuration.getDouble(prefix+"."+PAR_SUSPTHRESH);
    confidence = Configuration.getInt(prefix+"."+PAR_CONFIDENCE);
    sp = new Vector<Suspicion>(groupsize-1);
    dead = new Vector<Suspicion>(groupsize-1);
    gp = new Vector<GroupDetails>(groupsize-1);
  }

  public Object clone()
  {
    AggregationProtocol ap = null;
    try
    {
      ap = (AggregationProtocol)super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }
    ap.sp = (Vector<Suspicion>)sp.clone();
    ap.dead = (Vector<Suspicion>)dead.clone();
    ap.gp = (Vector<GroupDetails>)gp.clone();
    ap.round = 0;
    ap.value = (double)CommonState.r.nextInt(size);
    //ap.value = (double)CommonState.r.nextInt(10);
    ap.initval = ap.value;
    return ap;
  }

  @Override
  public void nextCycle(Node node, int protocolID)
  {
    int linkableID = FastConfig.getLinkable(protocolID);
    Linkable linkable = (Linkable) node.getProtocol(linkableID);

    Descriptor mydesc = node.getDescriptor(protocolID);
    long curtime = CommonState.getTime();
    //if(round > confidence)
      //System.out.println(round+" "+value);
    round = round + 1;
    
    if(linkable.degree() > 0)
    {
      Descriptor receiverdesc = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
      RqstMsg msg = new RqstMsg();
      msg.value = value;
      msg.desc = mydesc;
      int tid = FastConfig.getTransport(protocolID);
      Transport tr = (Transport)node.getProtocol(tid);
      tr.send(mydesc,receiverdesc,protocolID,msg);
    }
    
    if(first)
    {
      for(int i=0;i<sp.size();i++)
      { 
        sp.get(i).esttime = curtime+heartbeat; 
      }
      EDSimulator.add(heartbeat, new Message(Message.Type.BCAST), node, protocolID); //To send heartbeat message
      EDSimulator.add(cleanup, new Message(Message.Type.CLEANUP), node, protocolID);
    }
    first = false;
  }

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    Descriptor mydesc = node.getDescriptor(pid);
    long curtime = CommonState.getTime();
    if(event instanceof RespMsg)
    {
      RespMsg m = (RespMsg) event;
      if(value == m.svalue)
      {
        value = (value + m.rvalue)/2;
      }
      else
      {
        value = value + ((m.rvalue-m.svalue)/2);
      }
      
      for(int i=0;i<sp.size();i++)
      {
        if(sp.get(i).desc.getID() == m.desc.getID())
        {
          sp.get(i).lastmsgtime = curtime;
        }
      }

    }
    else if(event instanceof RqstMsg)
    {
      RqstMsg m = (RqstMsg) event;
      RespMsg msg = new RespMsg();
      msg.desc = mydesc;
      msg.svalue = m.value;
      msg.rvalue = value;
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport)node.getProtocol(tid);
      tr.send(mydesc,m.desc,pid,msg);
      value = (value + m.value)/2;
      for(int i=0;i<sp.size();i++)
      {
        if(sp.get(i).desc.getID() == m.desc.getID())
        {
          sp.get(i).lastmsgtime = curtime;
        }
      }
    } 
    else if(event instanceof HbeatMsg) //Arrival of a heartbeat message
    {
      HbeatMsg m = (HbeatMsg) event;
      //System.out.println("HBEAT : "+curtime+" "+node.getID()+" : "+m.desc.getID() + " = " +(curtime+heartbeat));
      // Update
      boolean f1=false,f2=false;
      for(int i=0;i<sp.size();i++)
      {
        if(sp.get(i).desc.getID() == m.desc.getID())
        {
          //System.out.println("HBEAT : "+curtime+" "+node.getID()+" : "+m.desc.getID() + " = " +sp.get(i).esttime+" "+(curtime+heartbeat));
          sp.get(i).esttime = curtime + heartbeat;
          sp.get(i).lastmsgtime = curtime;
          sp.get(i).value = m.value;
          sp.get(i).version =m.version;
          if(sp.get(i).first)
          {
            sp.get(i).initval = m.initval;
            sp.get(i).first = false;
          }
          
          f1=true;
          break;
        }
      }
      if(!f1)
      {
        Suspicion sp1 = new Suspicion();
        sp1.desc = m.desc;
        sp1.esttime = curtime + heartbeat;
        sp1.initval = m.initval;
        sp1.first = true;
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
          value = value - (dead.get(i).value - dead.get(i).initval);
          dead.get(i).state = Suspicion.State.DEAD;
        }
      }
    }
    else if(event instanceof Message)
    {
      Message m = (Message)event;
      //System.out.println("time : "+curtime+" "+node.getID()+" "+m.type+" "+m.sender.getID());
      if(m.getType() == Message.Type.BCAST) //Sending heartbeat message to others
      {
        for(int i=0;i<sp.size();i++)
        {
          if(curtime<=sp.get(i).esttime)
            sp.get(i).suspvalue = 0;
          else
            sp.get(i).suspvalue = Math.log10(curtime-sp.get(i).esttime+1);
          //System.out.println("At "+curtime+" "+node.getID()+" : "+sp.get(i).desc.getID() + " = " + sp.get(i).suspvalue+" "+sp.get(i).esttime);
          //System.out.println(sp.get(i).suspvalue+" "+curtime+" "+node.getID()+" "+sp.get(i).desc.getID());
        }
        for(int i=0;i<sp.size();i++)
        {
          HbeatMsg msg = new HbeatMsg();
          msg.initval = initval;
          msg.value = value;
          msg.desc = mydesc;
          msg.sp = sp;
          msg.version = round;
          int tid = FastConfig.getTransport(pid);
          Transport tr = (Transport)node.getProtocol(tid);
          tr.send(mydesc, sp.get(i).desc, pid, msg);
          //System.out.println("Sending heartbeat msg from "+mydesc.getID()+" to "+sp.get(i).desc.getID());
        }
        EDSimulator.add(heartbeat, new Message(Message.Type.BCAST), node, pid);
      }
      else if(m.getType() == Message.Type.CLEANUP)
      {
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
                    int tid = FastConfig.getTransport(pid);
                    Transport tr = (Transport)node.getProtocol(tid);
                    tr.send(mydesc, sp.get(j).desc, pid, deadmsg);
                  }
                }
                value = value + (sp.get(i).value - sp.get(i).initval);
                sp.get(i).state = Suspicion.State.DEADFIRST;
                dead.add(sp.get(i));
                sp.remove(i);                
              }
            }
        }
        EDSimulator.add(cleanup, new Message(Message.Type.CLEANUP), node, pid);
      }
    }

  }
  /*@Override
  public void onKill()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public int degree()
  {
    return view.size();
  }

  @Override
  public Descriptor getNeighbor(int i)
  {
    return view.elementAt(i);
  }

  @Override
  public boolean addNeighbor(Descriptor neighbour)
  {
    view.add(neighbour);
    return false;
  }

  @Override
  public boolean contains(Descriptor neighbor)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void pack()
  {
    // TODO Auto-generated method stub

  }*/

}

