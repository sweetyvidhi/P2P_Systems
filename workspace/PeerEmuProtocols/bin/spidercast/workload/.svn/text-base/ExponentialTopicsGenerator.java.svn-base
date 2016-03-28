/**
 * 
 */
package spidercast.workload;

import peeremu.config.Configuration;





/**
 * @author vinaysetty
 * 
 */
public class ExponentialTopicsGenerator extends SyntheticTopicsGenerator
{
  double alpha = 0.5;
  public static final String strAlpha = "alpha";
  public ExponentialTopicsGenerator(String prefix)
  {
    super(prefix);
    alpha = Configuration.getDouble(prefix+"."+strAlpha);
    init();
  }



  public void init()
  {
    interest = InterestGenerator.exponInterestMatrix(numNodes, numTopics, alpha, minTopicsPerNode, maxTopicsPerNode);
  }
}
