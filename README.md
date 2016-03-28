#A Robust Protocol for Aggregation in Dynamic Peer-to-Peer Networks

Aggregation in peer-to-peer networks provides a global view of system properties,
such as average computational load and total free disk space, which can be used for
administering the system. Among the various aggregation protocols, gossip-based
aggregation protocols provide faster aggregation of the node values in a scalable
and adaptive manner. This is particularly important in peer-to-peer networks which
are essentially dynamic, due to node failures, node churn, communication delays
and communication failures. These dynamics affect the correctness of the system
aggregate, which is a function of the individual node values (node contributions).


We first propose a protocol hierarchy for gossip-based aggregation that provides
the capability to perform accurate estimation of the system aggregate, even in the
presence of node failures. In this protocol hierarchy, we develop a robustness protocol
for aggregation in dynamic peer-to-peer networks that ensures the correctness of
the system aggregate amidst node failures. Our protocol detects node failures and
performs the necessary cleanup operations. The evaluation of our protocol shows
that the error of the converged aggregate from the system aggregate is very low.
We observe that the failure detection time is independent of the network size and
the number of nodes that fail. Additionally, we investigate the factors affecting the
accuracy of a node's estimate of the system aggregate (node estimate) and introduce
a metric called the 'confidence value' which gives an indication of this accuracy at
runtime. Hence, our robustness protocol enables accurate computation of system
aggregate even in the presence of node failures.