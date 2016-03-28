/*
 * Created on Jul 11, 2011 by Spyros Voulgaris
 */
package spidercast.quickcluster;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Descriptor;
import peeremu.core.DescriptorSim;
import peeremu.core.Linkable;
import peeremu.core.Node;





public class RoutingTableHash extends RoutingTablePriority
{
  static MessageDigest md;

  public RoutingTableHash(String prefix)
  {
    super(prefix);
    try
    {
      md = MessageDigest.getInstance("SHA-256");
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }

  }




  @Override
  protected Descriptor considerNodeForTopic(Descriptor candidate, Set<Descriptor> neighbors, Node myNode)
  {
    if (neighbors.size()<K){
      neighbors.add(candidate);
      return null;
    }
    else
      return applyHashFunction(candidate, neighbors, myNode);
  }



  /**
   * @param candidate
   * @param neighbors
   */
  protected Descriptor applyHashFunction(Descriptor candidate, Set<Descriptor> neighbors, Node myNode)
  {
    long myId = CommonState.getNode().getID();
    int candidateHash = getHashCode(myId, candidate.getID());
    int minHash = Integer.MAX_VALUE;
    Descriptor minDescriptor = null;
    boolean alreadyExists = false;
    for (Descriptor neighbor: neighbors)
    {
      if (candidate.equals(neighbor))
      {
        alreadyExists = true;
        break;
      }
      int neighborHash = getHashCode(myId, neighbor.getID());
      if (neighborHash<minHash)
      {
        minHash = neighborHash;
        minDescriptor = neighbor;
      }
    }
    if (!alreadyExists&&candidateHash>minHash)
    {
      neighbors.remove(minDescriptor);
      neighbors.add(candidate);
      return minDescriptor;
    }
    return null;
  }



  protected int getHashCode(long id1, long id2)
  {
    String id = ""+id1+id2;
    byte[] hashArray = md.digest(id.getBytes());
    int hashCode = hashArray[0]|hashArray[1]<<8;
    return hashCode;
  }
}
