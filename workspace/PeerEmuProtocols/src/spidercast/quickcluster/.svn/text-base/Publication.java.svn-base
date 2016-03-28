/**
 * 
 */
package spidercast.quickcluster;

import peeremu.core.Descriptor;
import peeremu.core.Node;

/**
 * @author vinaysetty
 *
 */
public class Publication implements Cloneable  //XXX: Be careful: Shallow cloning in place!!
{
  private Object message;
  private Node sender;
  public static int serialNumCounter = 0;
  private int serialNum;
  private int targetTopic;
  public int hop = 0;



  public Publication(Object msg, Node sender, int topic)
  {
    serialNum = serialNumCounter++;
    this.message = msg;
    this.targetTopic = topic;
    this.sender = sender;
  }
  
  public void setSender(Node s)
  {
    this.sender = s;
  }
  /**
   * @return the message
   */
  public Object getMessage()
  {
    return message;
  }
  /**
   * @return the sender
   */
  public Node getSender()
  {
    return sender;
  }
  /**
   * @return the serialNum
   */
  public int getSerialNum()
  {
    return serialNum;
  }
  /**
   * @return the targetTopic
   */
  public int getTargetTopic()
  {
    return targetTopic;
  }

  public Object clone()
  {
    Object obj = null;
    try
    {
      obj = super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      e.printStackTrace();
    }
    return obj;
  }
}
