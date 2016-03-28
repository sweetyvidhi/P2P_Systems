package mehyar2;

import java.util.Vector;

import mehyar2.Message;

import mehyar.MehyarProtocol;
import mehyar.MehyarProtocol.State;
import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Linkable;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;

public class Mehyar2Protocol implements EDProtocol,CDProtocol,Linkable
{
  private static final String PAR_VALUE = "value";
  private static final String PAR_VIEWLEN = "view";
  private static final String PAR_SIZE = "size";
  private static final String PAR_PHI = "phi";
  private static final String PAR_GAMMA = "gamma";
  private int viewlen;
  private double value;
  private double initval;
  private int size;
  private double phi;
  private double gamma;
  private int round;
  private Vector<Descriptor> view;
  private Vector<Double> x;
  public enum State
  {
    RQST,
    RESP;
  }
  private State state;
  
  public Mehyar2Protocol(String prefix)
  {
    value = Configuration.getInt(prefix+"."+PAR_VALUE);
    viewlen = Configuration.getInt(prefix+"."+PAR_VIEWLEN);
    size = Configuration.getInt(prefix+"."+PAR_SIZE);
    phi = Configuration.getDouble(prefix+"."+PAR_PHI);
    gamma = Configuration.getDouble(prefix+"."+PAR_GAMMA);
    view = new Vector<Descriptor>(viewlen);
    x = new Vector<Double>(viewlen);
  }
  
  public Object clone()
  {
    Mehyar2Protocol mp = null;
    try
    {
      mp = (Mehyar2Protocol)super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }
    mp.view = (Vector<Descriptor>)view.clone();
    mp.round = 0;
    mp.value = (double)CommonState.r.nextInt(size);
    mp.state = State.RESP;
    mp.initval = mp.value;
    mp.gamma = gamma;
    mp.phi = phi;
    for(int i=0;i<size;i++)
    {
      x.add(new Double(0));
    }
    return mp;
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

  @Override
  public void nextCycle(Node node, int protocolID)
  {
    int i;
    if(round>0)
    {
      double sum=0;
      for(i=0;i<size;i++)
      {
        sum = x.elementAt(i);
      }
      sum += initval;
      sum -= value;
      sum *= gamma;
      value += sum;
    }
    for (i=0;i<size;i++)
    {
      System.out.print(x.elementAt(i)+" ");
    }
    System.out.println(" ");
    Descriptor mydesc = node.getDescriptor(protocolID);
    System.out.println("r "+round+" "+mydesc.getID()+" "+value);
    round = round + 1;
    int r = CommonState.r.nextInt(this.degree());
    Descriptor receiverdesc = this.getNeighbor(r);
    Message msg = new Message();
    msg.type = Message.Type.RQST;
    msg.round = round;
    msg.value = value;
    msg.sender = mydesc;
    int tid = FastConfig.getTransport(protocolID);
    Transport tr = (Transport)node.getProtocol(tid);
    tr.send(mydesc,receiverdesc,protocolID,msg);    
  }

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    Descriptor mydesc = node.getDescriptor(pid);
    if(event instanceof Message)
    {
      Message m = (Message)event;
      if(m.type == Message.Type.RQST)
      {
        if(state == State.RESP)
        {
       long j = m.sender.getID();
        if(mydesc.getID() < j)
        {
          double u = phi * (value - m.value);
          double temp = x.elementAt((int)j);
          temp += u;
          x.add((int)j,temp);
          Message msg = new Message();
          msg.type = Message.Type.RESP;
          msg.round = round;
          msg.value = u;
          msg.sender = mydesc;
          int tid = FastConfig.getTransport(pid);
          Transport tr = (Transport)node.getProtocol(tid);
          tr.send(mydesc,m.sender,pid,msg);
          state = State.RQST;
        }
        }
      }
      else if(m.type == Message.Type.RESP)
      {
        long j = m.sender.getID();
        double temp = x.elementAt((int)j);
        temp -= m.value;
        x.add((int)j,temp);
        Message msg = new Message();
        msg.type = Message.Type.RESP_ACK;
        msg.round = round;
        msg.value = value;
        msg.sender = mydesc;
        int tid = FastConfig.getTransport(pid);
        Transport tr = (Transport)node.getProtocol(tid);
        tr.send(mydesc,m.sender,pid,msg);
        
      }
      else if(m.type == Message.Type.RESP_ACK)
      {
        state = State.RESP;
      }
    }
      

    
  }
}
