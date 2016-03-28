/**
 * 
 */
package spidercast.quickcluster;

import peeremu.config.Configuration;
import peeremu.core.Control;

/**
 * @author vinaysetty
 *
 */
public class BandwidthObserver implements Control
{
  private int pid = -1; 
  BandwidthObserver(String prefix)
  {
    pid = Configuration.getPid(prefix+"."+PAR_PROTOCOL);
  }
  @Override
  public boolean execute()
  {
    // TODO Auto-generated method stub
    return false;
  }
}
