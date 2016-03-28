/**
 * 
 */
package spidercast.quickcluster;

import java.util.HashMap;
import java.util.Set;

import peeremu.core.Node;





/**
 * @author vinaysetty
 * 
 */
public class DescriptorQC extends DescriptorRT
{
 
  public DescriptorQC(Node node, int pid)
  {
    super(node, pid);

    assert rtPid != -1;

    RoutingTable rt = (RoutingTable) node.getProtocol(rtPid);
    payload = rt.getAdvertisement();
  }

  @Override
  public Set<Integer> getTopicSet(){
    @SuppressWarnings("unchecked")
    HashMap<Integer, Integer> topicMap = (HashMap<Integer, Integer>) payload;
    return topicMap.keySet();
  }
  
  public HashMap<Integer, Integer> getPayload(){
    return (HashMap<Integer, Integer>) payload;
  }
  public void removeTopicFromPayload(Integer topic)
  {
    ((HashMap<Integer, Integer>)payload).remove(topic);
  }
  
  public int getSize()
  {
    return ((HashMap<Integer, Integer>) payload).size()*Integer.SIZE;
  }
}
