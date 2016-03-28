package random;

import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.FastConfig;
import peeremu.core.Node;
import peeremu.edsim.CDProtocol;
import peeremu.edsim.EDProtocol;
import peeremu.transport.Transport;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.Linkable;

public class RandomProtocol implements EDProtocol, CDProtocol, Linkable
{
  int id;
  private static final String PAR_VIEWLEN = "view";
  private static final String PAR_SIZE = "size";
  private static int viewLen;
  private static int size;
  public Vector<Descriptor> view;
  
  public RandomProtocol(String prefix)
  {
    viewLen = Configuration.getInt(prefix+"."+PAR_VIEWLEN);
    view = new Vector<Descriptor>(viewLen);
    size = Configuration.getInt(prefix+"."+PAR_SIZE);
  }
  
  public Object clone()
  {
    RandomProtocol rp = null;
    try
    {
      rp = (RandomProtocol)super.clone();
    }
    catch(CloneNotSupportedException e){e.printStackTrace();}
    rp.view = (Vector<Descriptor>)view.clone();
    rp.id = CommonState.r.nextInt(this.size);
    
    return rp;
  }

  @Override
  public void onKill()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int degree()
  {
    // TODO Auto-generated method stub
    return view.size();
  }

  @Override
  public Descriptor getNeighbor(int i)
  {
    // TODO Auto-generated method stub
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
  public void processEvent(Node node, int pid, Object event)
  { 
    RandomProtocol rp = (RandomProtocol)node.getProtocol(pid);
    Descriptor mydesc = node.getDescriptor(pid);
    Object[] message = (Object[]) event;
    int walklen = ((Integer)message[1]).intValue();
    walklen=walklen-1;
    if(walklen == 0)
    {
    System.out.println(mydesc.getID()+" got the message "+message[0]);
    }
    else
    {
      int r = CommonState.r.nextInt(rp.degree());
      Descriptor receiverdesc = rp.getNeighbor(r);
      message[1]=walklen;
      System.out.println(mydesc.getID()+" is forwarding the message ");
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport) node.getProtocol(tid);
      tr.send(mydesc,receiverdesc,pid,message);
      
    }
    
    
  }

  @Override
  public void nextCycle(Node node, int protocolID)
  {
      System.out.println("My Id is "+node.getDescriptor(protocolID).getID());
      RandomProtocol rp = (RandomProtocol)node.getProtocol(protocolID);
      System.out.println("My random id is "+rp.id);
      Vector<Descriptor> view = rp.view;
      System.out.println("My neighbours are : ");
      for(int i=0;i<view.size();i++)
      {
        System.out.print(view.get(i).getID());
      }
    
  }
  

}
