/**
 * 
 */
package spidercast.workload;

import peeremu.config.Configuration;

/**
 * @author vinaysetty
 * 
 */
public class ZipfTopicsGenerator extends SyntheticTopicsGenerator
{
  double alpha = 0.5;
  public static final String strAlpha = "alpha";
  public ZipfTopicsGenerator(String prefix)
  {
    super(prefix);
    alpha = Configuration.getDouble(prefix+"."+strAlpha);
    init();
  }



  public void init()
  {
    // TODO Find a way to assign all parameters here
    interest = InterestGenerator.zipfInterestMatrix(numNodes, numTopics, alpha, minTopicsPerNode, maxTopicsPerNode, 1);
  }
}
