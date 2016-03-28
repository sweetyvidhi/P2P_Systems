package jelasity;

import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Linkable;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;

public class AggregationProtocol implements EDProtocol,CDProtocol,Linkable
{
  private static final String PAR_VALUE = "value";
  private static final String PAR_VIEWLEN = "view";
  private static final String PAR_SIZE = "size";
  private static final String PAR_RANGE = "range";
  private int viewlen;
  private int size;
  private int range;
  public double value;
  public double initval;
  public double trueavg;
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

  public int round;
  public Vector<Descriptor> view;
  
  public AggregationProtocol(String prefix)
  {
    //value = Configuration.getInt(prefix+"."+PAR_VALUE);
    viewlen = Configuration.getInt(prefix+"."+PAR_VIEWLEN);
    size = Configuration.getInt(prefix+"."+PAR_SIZE);
    view = new Vector<Descriptor>(viewlen);
    range = Configuration.getInt(prefix+"."+PAR_RANGE);    
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
    ap.view = (Vector<Descriptor>)view.clone();
    ap.round = 0;
    ap.value = (double)CommonState.r.nextInt(range);
    ap.state = State.RESP;
    ap.initval = ap.value;
    return ap;
  }

  @Override
  public void nextCycle(Node node, int protocolID)
  {
      int linkableID = FastConfig.getLinkable(protocolID);
      Linkable linkable = (Linkable) node.getProtocol(linkableID);
      Descriptor mydesc = node.getDescriptor(protocolID);
      //System.out.println(round+" "+value);
      round = round + 1;
      int r = CommonState.r.nextInt(linkable.degree());
      Descriptor receiverdesc = linkable.getNeighbor(r);
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
  
  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    if(event instanceof Message)
    {
      Message m = (Message)event;
      if(m.type == Message.Type.RESP)
      {
          value = (value + m.value)/2;
          state = State.RESP;
      }
      else if(m.type == Message.Type.RQST)
      {
          Descriptor mydesc = node.getDescriptor(pid);
          Message msg = new Message();
          if(state == State.RQST)
          {
            msg.type = Message.Type.NACK;
          }
          else
          {
            msg.type = Message.Type.RESP;
          }
          msg.round = round;
          msg.value = value;
          msg.sender = mydesc;
          int tid = FastConfig.getTransport(pid);
          Transport tr = (Transport)node.getProtocol(tid);
          tr.send(mydesc,m.sender,pid,msg);
          if(state == State.RQST)
          {
            
           
          }
          else
          {
          value = (value + m.value)/2;
          
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
