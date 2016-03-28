package kempe;

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

public class KempeProtocol implements EDProtocol,CDProtocol,Linkable
{
  private static final String PAR_VALUE = "value";
  private static final String PAR_VIEWLEN = "view";
  private static final String PAR_SIZE = "size";
  private static final String PAR_RANGE = "range";
  private int viewlen;
  private int size;
  private int range;
  public double value;
  public int round;
  private double sum;
  public double weight;
  public double initval;
  public double trueavg=0.0;
  public Vector<Descriptor> view;
  
  public KempeProtocol(String prefix)
  {
    //value = Configuration.getInt(prefix+"."+PAR_VALUE);
    viewlen = Configuration.getInt(prefix+"."+PAR_VIEWLEN);
    size = Configuration.getInt(prefix+"."+PAR_SIZE);
    view = new Vector<Descriptor>(viewlen);
    range = Configuration.getInt(prefix+"."+PAR_RANGE);
  }
  
  public Object clone()
  {
    KempeProtocol kp = null;
    try
    {
      kp = (KempeProtocol)super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }
    kp.view = (Vector<Descriptor>)view.clone();
    kp.round = 0;
    kp.value = (double)CommonState.r.nextInt(range);
    kp.initval = kp.value;
    kp.sum = kp.value;
    kp.weight = 1.0;
    return kp;
  }
 
  @Override
  public void nextCycle(Node node, int protocolID)
  {
    int linkableID = FastConfig.getLinkable(protocolID);
    Linkable linkable = (Linkable) node.getProtocol(linkableID);
    value = sum/weight;
    round = round+1;
    //System.out.println("---- Id "+node.getID()+" Round "+round+" value "+value);
    //System.out.println(round+" "+value);
    Descriptor mydesc = (Descriptor)node.getDescriptor(protocolID);
    int r = CommonState.r.nextInt(linkable.degree());
    Descriptor receiverdesc = (Descriptor)linkable.getNeighbor(r);
    Message m = new Message();
    m.round = round;
    m.sender = mydesc;
    m.sum = sum/2.0;
    m.weight = weight/2.0;
    int tid = FastConfig.getTransport(protocolID);
    Transport tr = (Transport)node.getProtocol(tid);
    if(round==0)
    {
      tr.send(mydesc,mydesc, protocolID, m);
    }
    else
    {
    tr.send(mydesc,receiverdesc, protocolID, m);
    }
    sum = sum/2.0;
    weight = weight/2.0;
    
    
  }

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    if(event instanceof Message)
    {
      Message m = (Message)event;
      this.sum = this.sum + m.sum;
      this.weight = this.weight + m.weight;
      
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
