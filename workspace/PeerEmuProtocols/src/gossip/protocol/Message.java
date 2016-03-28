/*
 * Created on Jul 23, 2010 by Spyros Voulgaris
 *
 */
package gossip.protocol;

import java.util.Vector;

import peeremu.core.Descriptor;



public class Message
{
  public enum Type
  {
    GOSSIP_REQUEST,
    GOSSIP_RESPONSE;
  }

  public Type type;
  public Descriptor sender;
  public Vector<Descriptor> descriptors;
}