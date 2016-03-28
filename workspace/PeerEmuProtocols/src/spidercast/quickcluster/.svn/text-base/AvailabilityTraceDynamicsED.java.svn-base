/**
 * 
 */
package spidercast.quickcluster;

import gossip.protocol.CyclonED;
import gossip.vicinity.VicinityED;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.config.IllegalParameterException;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Fallible;
import peeremu.core.Linkable;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.core.Scheduler;
import peeremu.dynamics.NodeInitializer;
import peeremu.util.IncrementalStats;
import peeremu.util.RandPermutation;





/**
 * @author vinaysetty
 * 
 */
public class AvailabilityTraceDynamicsED implements Control
{
  /**
   * The file containing the session data. Currently based on the Saroiu format.
   * 
   * @config
   */
  private static final String PAR_FILE = "file";
  /**
   * The number of time units in which a second is subdivided.
   * 
   * @config
   */
  private static final String PAR_TICKS_PER_SEC = "ticks_per_sec";
  private static final String PAR_PROT = "protocol";
  // ---------------------------------------------------------------------
  // Fields
  // ---------------------------------------------------------------------
  /** Name of the file containing the King measurements. */
  private String filename;
  /** Cycle length */
  private int step;
  /** Number of time units contained in one second */
  private int ticks_per_sec;
  /**
   * Events related to nodes; if the node is currently active, it is
   */
  private Integer[][] events;
  /** The prefix of this control */
  private final String prefix;
  private static int cycle = 0;
  private boolean firstExecution = true;
  private int rtPID;
  private static final String PAR_INIT = "init";
  /** node initializers to apply on the newly added nodes */
  protected final NodeInitializer[] inits;
  private int cyclonPID;
  private int vicinityPID;
  private int k;
  private static String PAR_WARMUP = "warmup";



  // ---------------------------------------------------------------------
  // Initialization
  // ---------------------------------------------------------------------
  public AvailabilityTraceDynamicsED(String prefix)
  {
    this.prefix = prefix;
    filename = Configuration.getString(prefix+"."+PAR_FILE);
    step = Configuration.getInt(prefix+"."+Scheduler.PAR_STEP);
    ticks_per_sec = Configuration.getInt(prefix+"."+PAR_TICKS_PER_SEC);
    rtPID = Configuration.getPid(prefix+"."+PAR_PROT);
    cyclonPID = Configuration.getPid(prefix+".cyclon");
    PublicationSender.warmupPeriod = Configuration.getInt(prefix+"."+PAR_WARMUP);
    vicinityPID = Configuration.getPid(prefix+".vicinity");
    k = Configuration.getInt(prefix+".k");
    Object[] tmp = Configuration.getInstanceArray(prefix+"."+PAR_INIT);
    inits = new NodeInitializer[tmp.length];
    for (int i = 0; i<tmp.length; ++i)
    {
      // System.out.println("Inits " + tmp[i]);
      inits[i] = (NodeInitializer) tmp[i];
    }
    BufferedReader in = null;
    try
    {
      in = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e)
    {
      throw new IllegalParameterException(prefix+"."+PAR_FILE, filename+" does not exist");
    }
    String line = null;
    // Skip initial lines
    ArrayList<long[]> traces = new ArrayList<long[]>();
    IncrementalStats sessions = new IncrementalStats();
    IncrementalStats length = new IncrementalStats();
    int zn = 0;
    long max = 0;
    try
    {
      while ((line = in.readLine())!=null)
      {
        StringTokenizer tok = new StringTokenizer(line);
        tok.nextToken();
        int n = Integer.parseInt(tok.nextToken());
        long[] trace = new long[n*2];
        for (int i = 0; i<n; i++)
        {
          long start = (long) (Double.parseDouble(tok.nextToken())*ticks_per_sec);
          long stop = (long) (Double.parseDouble(tok.nextToken())*ticks_per_sec);
          trace[i*2] = start;
          trace[i*2+1] = stop;
          if (stop>max)
            max = stop;
          long diff = stop-start;
          length.add(diff);
        }
        if (n>0)
        {
          sessions.add(n);
          traces.add(trace);
        }
        else
          zn++;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace(System.err);
      System.exit(0);
    }
    System.err.println("ZERO "+zn+" SESSIONS "+sessions+" LENGTH "+length);
    /*
     * Assign traces to nodes randomly: node i is assigned trace
     * assignedTraces[i]
     */
    int size = Network.size();
    int[] assignedTraces = new int[size];
    RandPermutation rp = new RandPermutation(CommonState.r);
    rp.reset(traces.size());
    for (int i = 0; i<assignedTraces.length; i++)
    {
      if (rp.hasNext()==false)
        rp.reset(traces.size());
      assignedTraces[i] = rp.next();
    }
    // From a trace array to an array of cycles
    ArrayList<Integer>[] cycles = new ArrayList[(int) (max/step+1)];
    for (int i = 0; i<cycles.length; i++)
      cycles[i] = new ArrayList<Integer>();
    for (int i = 0; i<size; i++)
    {
      long[] trace = traces.get(assignedTraces[i]);
      for (int j = 0; j<trace.length; j++)
        cycles[(int) (trace[j]/step)].add(i);
    }
    // From ArrayList[] to Node[][]
    events = new Integer[cycles.length][];
    for (int i = 0; i<cycles.length; i++)
      if (cycles[i].size()!=0)
      {
        events[i] = cycles[i].toArray(new Integer[cycles[i].size()]);
        int sizeofchurn = events[i].length;
        // if(sizeofchurn > 10){
        // System.out.println(i + "\t" + sizeofchurn);
        // }
      }
  }

  private static int netSize = 0;



  void removeDeadNodes(Vector<Descriptor> view)
  {
    for (int i = 0; i<view.size(); ++i)
    {
      if (((DescriptorSim) (view.get(i))).getNode().isUp()==false)
      {
        view.remove(i--);
      }
    }
  }



  protected Node restartNode(Node node)
  {
    CyclonED cyclon = (CyclonED) node.getProtocol(cyclonPID);
    VicinityED vicinity = (VicinityED) node.getProtocol(vicinityPID);
    RoutingTable rp = (RoutingTable) node.getProtocol(rtPID);
    cyclon.view.clear();
//    vicinity.view.clear();
    removeDeadNodes(vicinity.view);
    rp.removeDeadNeighbors();
    // rp.clearTopicsNeighbors();
    initializeCyclon(node);
    for (int j = 0; j<inits.length; ++j)
    {
      inits[j].initialize(node);
    }
    rp.considerNodes(cyclon.view);
    rp.considerNodes(vicinity.view);
    return node;
  }



  /**
   * Takes {@value #PAR_DEGREE} random samples with replacement from the nodes
   * of the overlay network. No loop edges are added.
   */
  public void initializeCyclon(Node n)
  {
    if (Network.size()==0)
      return;
    Linkable linkable = (Linkable) n.getProtocol(cyclonPID);
    int count = 0;
    while (count<Network.size()&&linkable.degree()<k)
    {
      int r = CommonState.r.nextInt(Network.size()-1);
      if (r>=n.getIndex())
        r++;
      Node randNode = Network.get(r);
      count++;
      if (randNode.isUp()==false)
        continue;
      linkable.addNeighbor(randNode.getDescriptor(cyclonPID));
    }
  }



  /*
   * (non-Javadoc)
   * 
   * @see peeremu.core.Control#execute()
   */
  @Override
  public boolean execute()
  {
    // The first time it's executed, set all nodes to DOWN
    if (firstExecution)
    {
      for (int i = 0; i<Network.size(); i++)
      {
        Node node = Network.get(i);
        node.setFailState(Fallible.DOWN);
        PublicationSender.ageMap.put(node.getIndex(), 0);
      }
      netSize = 0;
    }

//    for (int i = 0; i<Network.size(); i++)
//    {
//      Node node = Network.get(i);
//      PublicationSender.incNodeAge((int) node.getID());
//    }

    int down = 0;
    int up = 0;
    if (events[cycle]!=null)
    {
      int size = events[cycle].length;
      for (int i = 0; i<size; i++)
      {
        int id = events[cycle][i];
        Node node = Network.get(id);
        if (node!=null&&node.isUp())
        {
          node.setFailState(Fallible.DOWN);
          down++;
        }
        else
        {
          node.setFailState(Fallible.OK);
          restartNode(node);
          up++;
        }
        PublicationSender.ageMap.put(id, 0);
      }
      netSize += up-down;
    }

//    System.out.println("\t"+up+"\t"+down+"\t"+netSize+"\t");

    firstExecution = false;
    cycle = (cycle+1)%events.length;
    return false;
  }
}
