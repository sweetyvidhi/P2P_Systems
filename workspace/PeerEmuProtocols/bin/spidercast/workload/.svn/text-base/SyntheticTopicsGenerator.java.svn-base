/**
 * 
 */
package spidercast.workload;

import java.util.HashMap;
import java.util.Vector;

import peeremu.config.Configuration;





/**
 * @author vinaysetty
 * 
 */
public class SyntheticTopicsGenerator implements Topics
{
  protected int numNodes;
  protected int numTopics;
  protected int minTopicsPerNode;
  protected int maxTopicsPerNode;
  
  protected boolean[][] interest = null;
  private static String PAR_NUM_TOPICS = "NUM_TOPICS";
  private static String PAR_NUM_NODES = "NODES";
  private static String PAR_MIN_TOPICS = "minTopicsPerNode";
  private static String PAR_MAX_TOPICS = "maxTopicsPerNode";



  SyntheticTopicsGenerator(String prefix)
  {
    
    numTopics = Configuration.getInt(prefix+"."+PAR_NUM_TOPICS);
    
    numNodes = Configuration.getInt(PAR_NUM_NODES);
    
    minTopicsPerNode = Configuration.getInt(prefix+"."+PAR_MIN_TOPICS);
    maxTopicsPerNode = Configuration.getInt(prefix+"."+PAR_MAX_TOPICS);
  }



  /*
   * (non-Javadoc)
   * 
   * @see workload.Topics#getTopics()
   */
  @Override
  public Object getTopics(int nodeIndex)
  {
//    System.out.print("set of topics:");
    Vector<Integer> topics = new Vector<Integer>();
    for (int i = 0; i<interest[nodeIndex].length; ++i)
    {
      boolean chooseTopic = interest[nodeIndex][i];
      if (chooseTopic)
      {
        topics.add(i);
      }
    }
    return topics;
  }
}
