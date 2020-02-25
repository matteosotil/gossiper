# Gossiper

At [Bloobirds](https://bloobirds.com), Java, Spring Framework and distributed systems are two core technologies which the development team 
deals with every day.

As part of our constant training we've built a challenge on those technologies: Gossiper.

## What does it do?

Start a first node at port `9001` called `batman`:
```shell script
 java -jar target/gossiper-1.0.0-SNAPSHOT.jar \
    --server.port=9001 \
    --gossiper.ownName=batman \
    --gossiper.port=9001
```

Start a second node at port `9002` called `robin`, using `batman` as seed

```shell script
 java -jar target/gossiper-1.0.0-SNAPSHOT.jar \
    --server.port=9002 \
    --gossiper.ownName=batman \
    --gossiper.port=9002 \
    --gossiper.seedName=robin \
    --gossiper.seedHostname=127.0.0.1:9001
```

Once the `robin` node is up, `batman` will display the message `Node robin joined`.

More nodes can be added using any node as seed, and they will be discovered by the other nodes.

## How does it work?

When a node starts it uses the seed config to send a ping to join the party. Afterwards each
node at regular intervals picks `N` nodes at random from its `ConnectionTable` and sends them a message that includes 
the entire connection table held by the node, if the connection is ok the node is held in the table
otherwise the node is removed from the table. When a node is pinged with a connection table
it merges the incoming table with its own table, possibly adding new nodes.

This pattern is called `Gossip protocol` and is used by databases and other systems to
share data, discover nodes and even process data. The promise of the method is to keep
the communications constant and linear w.r.t. to the amount of nodes. That's why there are
two constant parameters `pingTime` (the ping intervals) and `pingAmount` (the amount of nodes to ping). 
So given we have `N` nodes and `pingAmount` is `K` and `pingTime` is `T` then we have
guarantees that each `T` units of time there are `NK` communications.

## Install and notes

Java 8 or later, maven 3 (remember, `mvn clean package` to clean and build sources including jar file).

[Lombok](https://projectlombok.org/) is a dependency to remove boilerplate code, so maybe your code editor 
finds some lombok generated methods missing. There are config instructions for all major
code editors [here](https://projectlombok.org/setup/overview).


## The challenge

There is a bug, and there are improvements. :D

### The bug

Suppose we have three nodes `batman`, `robin` and `joker`. All three are well connected among 
themselves. Then `joker` leaves the party. Then batman will start to print some weird messages:
```
Node joker left
Node joker joined
Node joker left
Node joker joined
Node joker left
Node joker joined
Node joker left
Node joker joined
```
So naturally there is a problem here. Looks like `batman` node removes `joker` from the table when 
it tries to ping it, but later gets from `robin` a table connection that still includes `joker` so 
`batman` adds again `joker` to its table. At the same time `robin` pinged `joker` that was missing
so `robin` removed `joker` from its table, but later `robin` gets the ping from `batman`
that includes `joker` because `robin` told so.

Would you solve it?

### The seed

When starting a node it is required a node seed (hostname, port and name). Wouldn't be great if
just a hostname would be required and nodes within that hostname found? Also would make sense
to add a "cluster name" or something similar provided on each node, so nodes of same cluster name
would join together. Therefore being able of having more than one cluster in the same network
and no clashes.

### Sayonara, baby

If a node goes down, memory wiped, connection table lost, it may be unable to rejoin
the party. Maybe if the restart is done by the original command and the original seed
is alive, then it will be able. Wouldn't be great if the connection table is persisted 
to file from time to time and used at startup to join the cluster?

### That boat will dock somewhere

Java is cool, but it will end-up as a docker container running somewhere. Making a docker
image for a java jar is kind of easy. Notice that at `target/gossiper-1.0.0-SNAPSHOT.jar` 
there is all the code bundled, it is called a fat jar. Anyhow, there are easy ways to 
write a dockerfile for java... and there are better ways. Which would you end up with?

### End of toy story

Yes, it simple and cool, but useless. The next step would be to send data to any node
and see that data being replicated to other nodes, and being able to query that data from
anywhere. Quite aggressive a full replication... so it would be even better (and way more
difficult) to being able to control the replication level. 