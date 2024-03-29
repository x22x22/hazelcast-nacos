[![pitest](https://github.com/x22x22/hazelcast-nacos/actions/workflows/pitest.yml/badge.svg)](http://x22x22.io/hazelcast-nacos/pitest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.x22x22/hazelcast-nacos/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.x22x22/hazelcast-nacos) 

# Table of Contents

- [Table of Contents](#table-of-contents)
- [Hazelcast Alibaba Nacos Discovery Plugin](#hazelcast-alibaba-nacos-discovery-plugin)
  - [Configuration](#configuration)
    - [Server XML Config](#server-xml-config)
    - [Client XML Config](#client-xml-config)
    - [Server Programmatic Config](#server-programmatic-config)
    - [Client Programmatic Config](#client-programmatic-config)
    - [Configuration via Maven](#configuration-via-maven)
  - [Compatibilities](#compatibilities)
    - [Known Issues](#known-issues)


# Hazelcast Alibaba Nacos Discovery Plugin 

**PS:** The repository is base on [hazelcast-zookeeper](https://github.com/hazelcast/hazelcast-zookeeper), thanks!

This plugin provides a service-based discovery by using nacos-client to communicate with your Nacos server. 

You can use this plugin with Discovery SPI enabled Hazelcast 3.6.1 and higher applications.

## Configuration

### Server XML Config

```xml

<hazelcast xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.hazelcast.com/schema/config
                               http://www.hazelcast.com/schema/config/hazelcast-config-4.0.xsd"
           xmlns="http://www.hazelcast.com/schema/config">

  <properties>
    <property name="hazelcast.discovery.enabled">true</property>
  </properties>

  <network>
    <join>
      <multicast enabled="false"/>
      <discovery-strategies>
        <discovery-strategy enabled="true" class="com.hazelcast.nacos.NacosDiscoveryStrategy">
          <properties>
            <property name="server_addr">{nacosServerIp}:{nacosServerPort}</property>
            <property name="application_name">{applicationName}</property>
            <!--defaults to discovery-hazelcast -->
            <property name="namespace">{namespace}</property>
            <!--Name of this Hazelcast cluster. You can have multiple distinct clusters to use the same Nacos installation.-->
            <property name="cluster_name">{clusterName}</property>
          </properties>
        </discovery-strategy>
      </discovery-strategies>
    </join>
  </network>
</hazelcast>
```

### Client XML Config

```xml

<hazelcast-client xsi:schemaLocation="http://www.hazelcast.com/schema/client-config hazelcast-client-config-4.0.xsd"
                  xmlns="http://www.hazelcast.com/schema/client-config"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <properties>
    <property name="hazelcast.discovery.enabled">true</property>
  </properties>

  <network>
    <aws enabled="false"/>
    <discovery-strategies>
      <discovery-strategy enabled="true" class="com.hazelcast.nacos.NacosDiscoveryStrategy">
        <properties>
          <property name="server_addr">{nacosServerIp}:{nacosServerPort}</property>
          <property name="application_name">{applicationName}</property>
          <!--defaults to discovery-hazelcast -->
          <property name="namespace">{namespace}</property>
          <!--Name of this Hazelcast cluster. You can have multiple distinct clusters to use the same Nacos installation.-->
          <property name="cluster_name">{clusterName}</property>
        </properties>
      </discovery-strategy>
    </discovery-strategies>
  </network>

</hazelcast-client>
```
### Server Programmatic Config

```java
Config config = new Config();
config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
config.setProperty("hazelcast.discovery.enabled", "true");

DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new NacosDiscoveryStrategyFactory())
  .addProperty(NacosDiscoveryProperties.SERVER_ADDR.key(), "{nacosServerIp}:{nacosServerPort}")
  .addProperty(NacosDiscoveryProperties.APPLICATION_NAME.key(), "{applicationName}")
  .addProperty(NacosDiscoveryProperties.NAMESPACE.key(), "{namespace}")
  .addProperty(NacosDiscoveryProperties.CLUSTER_NAME.key(), "{clusterName}")
config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

Hazelcast.newHazelcastInstance(config);
```

### Client Programmatic Config

```java
ClientConfig config = new ClientConfig();

config.setProperty("hazelcast.discovery.enabled", "true");

DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new NacosDiscoveryStrategyFactory())
  .addProperty(NacosDiscoveryProperties.SERVER_ADDR.key(), "{nacosServerIp}:{nacosServerPort}")
  .addProperty(NacosDiscoveryProperties.APPLICATION_NAME.key(), "{applicationName}")
  .addProperty(NacosDiscoveryProperties.NAMESPACE.key(), "{namespace}")
  .addProperty(NacosDiscoveryProperties.CLUSTER_NAME.key(), "{clusterName}")
config.getNetworkConfig().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

HazelcastClient.newHazelcastClient(config);

```
### Configuration via Maven

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
      
    ...  
    <dependencies>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>${nacos.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast-nacos</artifactId>
            <version>${hazelcast-nacos.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast</artifactId>
            <version>${hazelcast.version}</version>
        </dependency>
    </dependencies>
...
</project>
```

## Compatibilities

- `nacos-client-2.2.4` has been tested with Nacos 1.3.2 and Nacos 2.0

### Known Issues
There is an issue between Nacos and curator client in some versions. You may get `Received packet at server of unknown type 15` error.
