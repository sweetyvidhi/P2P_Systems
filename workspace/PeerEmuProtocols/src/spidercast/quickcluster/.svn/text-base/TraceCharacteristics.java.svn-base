/**
 * 
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import peeremu.config.Configuration;
import peeremu.core.Control;
import peeremu.core.Network;
import peeremu.core.Node;





/**
 * @author vinaysetty
 * 
 */
public class TraceCharacteristics implements Control
{
  int rtPid;



  public static void printStats(int pid)
  {
    HashMap<Integer, Integer> topicSize = new HashMap<Integer, Integer>();
    for (int i = 0; i<Network.size(); i++)
    {
      Node n = Network.get(i);
      RoutingTable rt = (RoutingTable) n.getProtocol(pid);
      Set<Integer> topics = rt.getTopics();
      System.out.println(topics.size());
      for (Integer topic: topics)
      {
        if (topicSize.containsKey(topic)==false)
        {
          topicSize.put(topic, 1);
        }
        else
        {
          int newSize = topicSize.get(topic)+1;
          topicSize.put(topic, newSize);
        }
      }
    }
    System.out.println("Topic Size\n");
    for (Integer size: topicSize.values())
      System.out.println(size);
  }



  public TraceCharacteristics(String prefix)
  {
    rtPid = Configuration.getPid(prefix+"."+"routing");
  }



  @Override
  public boolean execute()
  {
    printStats(rtPid);
    return true;
  }
}
