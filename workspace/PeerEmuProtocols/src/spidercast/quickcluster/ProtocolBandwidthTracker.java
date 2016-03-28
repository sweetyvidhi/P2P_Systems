/**
 * 
 */
package spidercast.quickcluster;

/**
 * @author vinaysetty
 *
 */
public interface ProtocolBandwidthTracker extends Cloneable
{
  void addTotalBitsSent(int bytes);
  
   void addTotalBitsReceived(int bytes);


  /**
   * @param totalNumMessagesSent the totalNumMessagesSent to set
   */
   void incrementTotalNumMessagesSent();



  /**
   * @return the totalNumMessagesSent
   */
  public int getTotalNumMessagesSent();



  /**
   * @param totalNumMessagesReceived the totalNumMessagesReceived to set
   */
   void incrementTotalNumMessagesReceived();



  /**
   * @return the totalNumMessagesReceived
   */
  public int getTotalNumMessagesReceived();
  

  public int getTotalBitsReceived();
  

  public int getTotalBitsSent();
}
