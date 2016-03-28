/**
 * 
 */
package spidercast.workload;

/**
 * @author vinaysetty
 *
 */
public class UniformTopicsGenerator extends SyntheticTopicsGenerator
{
  
  public UniformTopicsGenerator(String prefix)
  {
    super(prefix);
    init();
  }

  public void init()
  {
    interest = InterestGenerator.uniformInterestMatrix(
        numNodes, numTopics, minTopicsPerNode, maxTopicsPerNode);
  }
}
