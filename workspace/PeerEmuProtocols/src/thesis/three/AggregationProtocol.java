package thesis.three;

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

public class AggregationProtocol implements EDProtocol,CDProtocol,Linkable
{
  private static final String PAR_SIZE = "size";
  private static final String PAR_RANGE = "range";
  private static final String PAR_LAMBDA = "lambda";
  private static final String PAR_INITVALFN = "initval_fn";
  public double value;
  public double initval;
  public double trueavg;
  public int range;
  public int round;
  public int confidence;
  public double lambda;
  public int initval_fn;
  public double delay;
  private Vector<Double> prev;

  public AggregationProtocol(String prefix)
  {
    
    range = Configuration.getInt(prefix+"."+PAR_RANGE);
    lambda = Configuration.getDouble(prefix+"."+PAR_LAMBDA);
    initval_fn = Configuration.getInt(prefix+"."+PAR_INITVALFN);
    prev = new Vector<Double>();
    
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
    ap.round = 0;
    if(initval_fn==0)
    {
      ap.value = (double)(CommonState.r.nextInt(range));  //initval = given
    }
    else if(initval_fn==1)
    {
      ap.value = generate_poisson(lambda);                  //initval = Poisson
    }
    else if(initval_fn==2)
    {
      ap.value = generate_nonuniform();
    }
    ap.initval = ap.value;
    ap.confidence=0;
    ap.delay=0;
    return ap;
  }

  public double generate_nonuniform()
  {
    double decide = CommonState.r.nextDouble();
    if(decide<0.2)
    {
      return (CommonState.r.nextInt(5000));
    }
    else
    {
      return (100000 + CommonState.r.nextInt(5000));
    }
  }
  public double generate_poisson(double lambda)
  {
   double l = Math.pow(Math.E, -lambda);
   int k=1;
   double p = 1;
   do
   {
     k=k+1;
     p = p* CommonState.r.nextDouble();
   }while(p>l);
   return k-1;
  }
  /* (non-Javadoc)
   * @see peeremu.edsim.CDProtocol#nextCycle(peeremu.core.Node, int)
   */
  @Override
  public void nextCycle(Node node, int protocolID)
  {
    int linkableID = FastConfig.getLinkable(protocolID);
    Linkable linkable = (Linkable) node.getProtocol(linkableID);

    Descriptor mydesc = node.getDescriptor(protocolID);
    round = round + 1;
 
    if(linkable.degree() > 0)
    {
      Descriptor receiverdesc = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
      RqstMsg msg = new RqstMsg();
      msg.value = value;
      msg.desc = mydesc;
      msg.confidence=confidence;
      int tid = FastConfig.getTransport(protocolID);
      Transport tr = (Transport)node.getProtocol(tid);
      tr.send(mydesc,receiverdesc,protocolID,msg);
    }
  }

  @Override
  public void processEvent(Node node, int pid, Object event)
  {
    Descriptor mydesc = node.getDescriptor(pid);
    if(event instanceof RespMsg)
    {
      RespMsg m = (RespMsg) event;
      if(value == m.svalue)
      {
        value = (value + m.rvalue)/2;
      }
      else
      { delay++;
        value = value + ((m.rvalue-m.svalue)/2);
      }
      if(confidence<=m.confidence)
      {
        confidence=confidence+1;
      }
      else
      {
        confidence=m.confidence+1;
      }
    }
    else if(event instanceof RqstMsg)
    {
      RqstMsg m = (RqstMsg) event;
      RespMsg msg = new RespMsg();
      msg.desc = mydesc;
      msg.svalue = m.value;
      msg.rvalue = value;
      msg.confidence=confidence;
      int tid = FastConfig.getTransport(pid);
      Transport tr = (Transport)node.getProtocol(tid);
      tr.send(mydesc,m.desc,pid,msg);
      value = (value + m.value)/2;

      if(confidence<=m.confidence)
      {
        confidence=confidence+1;
      }
      else
      {
        confidence=m.confidence+1;
      }
    } 


  }
  public void onKill()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public int degree()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Descriptor getNeighbor(int i)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean addNeighbor(Descriptor neighbour)
  {
    // TODO Auto-generated method stub
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
  public void applyCorrection(double offset)
  {
    value = value + offset;
    confidence = 0;

  }
  public int getRound()
  {
    return round;
  }
  public double getValue()
  {
    return value;
  }
  public double getInitvalue()
  {
    return initval;
  }

}

