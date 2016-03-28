package mehyar;

import java.util.Vector;

import jelasity.Message;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Linkable;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;

public class MehyarProtocol implements EDProtocol,CDProtocol,Linkable
{
  private static final String PAR_VALUE = "value";
  private static final String PAR_VIEWLEN = "view";
  private static final String PAR_SIZE = "size";
  private static final String PAR_STEP = "stepparam";
  private int viewlen;
  private int size;
  public double value;
  public double initval;
  private double stepparam;
  public enum State
  {
    RQST,
    RESP;
  }
  private State state;
  public double getValue()
  {
    return value;
  }

  public void setValue(double value)
  {
    this.value = value;
  }

  private int round;
  public Vector<Descriptor> view;
  
  public MehyarProtocol(String prefix)
  {
    value = Configuration.getInt(prefix+"."+PAR_VALUE);
    viewlen = Configuration.getInt(prefix+"."+PAR_VIEWLEN);
    size = Configuration.getInt(prefix+"."+PAR_SIZE);
    stepparam = Configuration.getDouble(prefix+"."+PAR_STEP);
    view = new Vector<Descriptor>(viewlen); 
    
  }
  
  public Object clone()
  {
    MehyarProtocol ap = null;
    try
    {
      ap = (MehyarProtocol)super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }
    ap.view = (Vector<Descriptor>)view.clone();
    ap.round = 0;
    ap.value = (double)CommonState.r.nextInt(size);
    ap.state = State.RESP;
    ap.initval = ap.value;
    ap.stepparam = stepparam;
    return ap;
  }

  @Override
  public void nextCycle(Node node, int protocolID)
  {
      Descriptor mydesc = node.getDescriptor(protocolID);
      System.out.println(round+" "+mydesc.getID()+" "+value);
      round = round + 1;
      if(mydesc.getID()!=size-1)
      {
      int r;
      Descriptor receiverdesc;
      do
      {
      r = CommonState.r.nextInt(this.degree());
      receiverdesc = this.getNeighbor(r);
      }while(receiverdesc.getID()<mydesc.getID());
      
      Message msg = new Message();
      msg.type = Message.Type.RQST;
      msg.round = round;
      msg.value = value;
      msg.sender = mydesc;
      int tid = FastConfig.getTransport(protocolID);
      Transport tr = (Transport)node.getProtocol(tid);
      tr.send(mydesc,receiverdesc,protocolID,msg);
      state = State.RQST;
      }
    
  }
  
  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    if(event instanceof Message)
    {
      Message m = (Message)event;
      if(m.type == Message.Type.RESP)
      {
          value = value - m.value;
          state = State.RESP;
      }
      else if(m.type == Message.Type.RQST)
      {
          Descriptor mydesc = node.getDescriptor(pid);
          Message msg = new Message();
          if(state == State.RQST)
          {
            msg.type = Message.Type.NACK;
            msg.round = round;
            msg.value = value;
            msg.sender = mydesc;
            int tid = FastConfig.getTransport(pid);
            Transport tr = (Transport)node.getProtocol(tid);
            tr.send(mydesc,m.sender,pid,msg);
          }
          else
          {
            msg.type = Message.Type.RESP;
            double diff = stepparam * (m.value - value);
            value = value + diff;
            msg.round = round;
            msg.value = diff;
            msg.sender = mydesc;
            int tid = FastConfig.getTransport(pid);
            Transport tr = (Transport)node.getProtocol(tid);
            tr.send(mydesc,m.sender,pid,msg);
        }
          
      }
      else if(m.type == Message.Type.NACK)
      {
        state = State.RESP;
      }
          
    }
    
  }
  @Override
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
    
  }
}
