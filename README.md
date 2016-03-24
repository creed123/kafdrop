# Kafdrop

Kafdrop is a UI for monitoring Apache Kafka clusters. The tool displays information such as brokers, topics, partitions, and even lets you view messages. It is a light weight application that runs on Spring Boot and requires very little configuration.

## Requirements

* Java 8
* Kafka (0.8.1 or 0.8.2 is known to work)
* Zookeeper (3.4.5 or later)

## Building

After cloning the repository, building should just be a matter of running a standard Maven build:

```
$ mvn clean package
```

## Running

The build process creates an executable JAR file.  

```
java -jar ./target/kafdrop-<version>.jar --zookeeper.connect=<host>:<port>,<host>:<port>,...
```

Then open a browser and navigate to http://localhost:9000. The port can be overridden by adding the following config:

```
    --server.port=<port>
```
