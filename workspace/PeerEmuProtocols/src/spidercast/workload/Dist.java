package spidercast.workload;

import peeremu.core.CommonState;
/*
 *  Copyright (c) 2009, University of Oslo
 *  See LICENSE.txt for details.
 */

public class Dist {
	int n;                      //Number of distribution
	double lambda;               //lambda for exponential distribution
	boolean first = true;       // Static first time flag in zip distribution
	double c = 0;               // Normalization constant in zip distribution

	public Dist(int n){
		this.n=n;
		lambda=-Math.log(0.45)/Math.ceil(n*0.1);
	}
    
        // return a number greater or equal to 0 and smaller or equal to n

	public int uniform(){
		return ((int)(CommonState.r.nextDouble()*(n+1)));  // Never use non-repeatable RNGs!
	}

        // return a number greater or equal to 0 and smaller or equal to n

	public int expon()
	{
	  int value = (int) (-1/lambda * Math.log10(1-CommonState.r.nextDouble())); // Never use non-repeatable RNGs!
	  if (value>n)
		  value=n;
	  return value;
	}

        // return a number greater or equal to 0 and smaller or equal to n

	public int zipf(double alpha)
	{
    // Never use non-repeatable RNGs!
		double z=CommonState.r.nextDouble();     // Uniform random number (0 < z < 1)
		double sum_prob;            // Sum of probabilities
		int zipf_value = 0;      // Computed exponential value to be returned

	  // Compute normalization constant on first call only
	  if (first)
	  {
	    for (int i=1; i<=n+1; i++)
	      c = c + (1.0 / Math.pow((double) i, alpha));
	    c = 1.0 / c;
	    first = false;
	  }

	  // Map z to the value
	  sum_prob = 0;
	  for (int i=1; i<=n+1; i++)
	  {
	    sum_prob = sum_prob + c / Math.pow((double) i, alpha);
	    if (sum_prob >= z)
	    {
	      zipf_value = i;
	      break;
	    }
	  }
	  // Assert that zipf_value is between 1 and N
	  assert((zipf_value >=1) && (zipf_value <= n+1));
	  return zipf_value-1;
	}
}