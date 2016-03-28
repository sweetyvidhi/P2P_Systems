package chord;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;
import peeremu.core.Linkable;
import peeremu.core.Protocol;

public class ChordProtocol implements CDProtocol, EDProtocol
{
  private static final String PAR_M = "m";
  public int chordId;
  public ChordDescriptor predecessor = null;
  public ChordDescriptor successor = null;
  public ChordDescriptor[] fingerTable;
  public int m;
  public int next = 0;
  public int pid;
  
  public ChordProtocol(String prefix)
  {
      m = Configuration.getInt(prefix+"."+PAR_M);
  }
  
  public Object clone()
  {
    ChordProtocol cp = null;
    try
    {
      cp = (ChordProtocol)super.clone();
    }
    catch(CloneNotSupportedException e){e.printStackTrace();}
    cp.chordId = CommonState.r.nextInt((int)Math.pow(2,m));
    System.out.println("...."+cp.chordId);
    cp.fingerTable = new ChordDescriptor[m];    
    return cp;
  }

  @Override
  public void nextCycle(Node node, int protocolID)
  {
    pid=protocolID;
    System.out.println(this.chordId+" stabilizing");
    stabilize(node);
    System.out.println(this.chordId+" fixing");
    fix_fingers(node);
  }
  public boolean checkPos(int tid,int aid, int bid)
  {
    if(bid<aid)
    {
      if(tid>aid || tid < bid)
        return true;
      else
        return false;
    }
    if (tid>aid && tid < bid)
      return true;
    else 
      return false;
  }
  
  public boolean checkPosition(int tid,int aid, int bid)
  {
    if(bid<aid)
    {
      if(tid>aid || tid <= bid)
        return true;
      else
        return false;
    }
    if (tid>aid && tid <= bid)
      return true;
    else 
      return false;
  }
  
  public void stabilize(Node n)
  {
    /* Message to successor asking about the predecessor */
    int tid = FastConfig.getTransport(pid);
    Transport tr = (Transport)n.getProtocol(tid);
    PredecessorQueryMessage message = new PredecessorQueryMessage();
    ChordDescriptor mydesc = (ChordDescriptor) n.getDescriptor(pid);
    message.sender = mydesc;
    tr.send(mydesc, successor, pid, message);
  }
  
  public void notify(ChordDescriptor n)
  {
    if(predecessor==null || checkPos(n.chordID,predecessor.chordID,this.chordId)) 
    {
        predecessor = n;
    }
  }
  
  public void fix_fingers(Node n)
  {
   next  = next + 1;
   if(next >= m)
   {
     next = 0;
   }
    int id =  this.chordId + (int)Math.pow(2, next);
    ChordDescriptor cd = find(n,id);
    if(cd==null)
    {
      ChordDescriptor mydesc = (ChordDescriptor)n.getDescriptor(pid);
      ChordDescriptor closest = closest_preceding_node(mydesc,id,pid);
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport)n.getProtocol(tid);
      IdSuccessorQueryMessage message = new IdSuccessorQueryMessage();
      message.id = id;
      message.sender = mydesc;
      message.msgType = IdSuccessorQueryMessage.Type.FIX_FINGERS;
      tr.send(mydesc,closest,pid,message);
    }
    else
    {
      fingerTable[next] = cd;
    }
  }
  
  public ChordDescriptor find(Node n,int id)
  {
    if(checkPosition(id,this.chordId,successor.chordID))
    {
      return successor;
    }
    else
      return null;
  }
  public void find_successor(Node n, Object event, int pid)
  {
    ChordDescriptor sender = ((IdSuccessorQueryMessage)event).sender;
    int id = ((IdSuccessorQueryMessage)event).id;
    ChordDescriptor mydesc = (ChordDescriptor)n.getDescriptor(pid);
    System.out.println(this.chordId+" receivied id query "+id+"succ id "+this.successor.chordID);
    if (checkPosition(id,this.chordId,successor.chordID))
    {
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport)n.getProtocol(tid);
      IdSuccessorResponseMessage message = new IdSuccessorResponseMessage();
      message.id = id;
      message.sender = mydesc;
      message.successor = successor;
      message.msgType = ((IdSuccessorQueryMessage)event).msgType;
      tr.send(mydesc, sender, pid, message);
      
    }
    else
    {
      
      ChordDescriptor closest = closest_preceding_node(mydesc,id,pid);
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport)n.getProtocol(tid);
      IdSuccessorQueryMessage message = new IdSuccessorQueryMessage();
      message.id = id;
      message.sender = sender;
      message.msgType = ((IdSuccessorQueryMessage)event).msgType;
      tr.send(mydesc,closest,pid,message);
    }
    
  }
  
  public ChordDescriptor closest_preceding_node(ChordDescriptor mydesc,int id,int pid)
  {
    for(int i=m-1; i>=0; i--)
    { 
      if(checkPos(fingerTable[i].chordID,this.chordId,id))
      {
        return fingerTable[i];
      }
    }
    return mydesc;
  }



 

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    if (event instanceof NotifyMessage)
    {
      System.out.println(this.chordId+" receivied notify");
      notify(((NotifyMessage)event).sender);
    }
    else if (event instanceof PredecessorQueryMessage)
    {
      System.out.println(this.chordId+" receivied pred query");
      ChordDescriptor mydesc = (ChordDescriptor)node.getDescriptor(pid);
      ChordDescriptor receiverdesc = ((PredecessorQueryMessage)event).sender;
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport) node.getProtocol(tid);
      PredecessorResponseMessage message = new PredecessorResponseMessage();
      message.sender = mydesc;
      message.predecessor = this.predecessor;
      tr.send(mydesc,receiverdesc,pid,message);
      
    }
    else if(event instanceof PredecessorResponseMessage)
    {
      System.out.println(this.chordId+" receivied pred response");
      ChordDescriptor pred_succ = ((PredecessorResponseMessage)event).predecessor;
      if (checkPos(pred_succ.chordID,this.chordId,successor.chordID))
      {
        successor = pred_succ;
      }
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport)node.getProtocol(tid);
      NotifyMessage message = new NotifyMessage();
      ChordDescriptor mydesc = (ChordDescriptor) node.getDescriptor(pid);
      message.sender = mydesc;
      tr.send(mydesc, successor, pid, message);
    }
    else if(event instanceof IdSuccessorQueryMessage)
    {
      find_successor(node,event,pid);
    }
    else if(event instanceof IdSuccessorResponseMessage)
    {
      System.out.println(this.chordId+" receivied id response");
      ChordDescriptor succ = ((IdSuccessorResponseMessage)event).successor; 
      int id = ((IdSuccessorResponseMessage)event).id;
      if(((IdSuccessorResponseMessage)event).msgType == IdSuccessorQueryMessage.Type.FIX_FINGERS)
      { fingerTable[next] = succ;
      }
      else
        System.out.println("The successor of id "+id+" is "+succ.chordID);
    }
    else
      return;
  }
}
