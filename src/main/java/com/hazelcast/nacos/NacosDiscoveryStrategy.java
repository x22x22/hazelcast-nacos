/*
 * Copyright 2020 Hazelcast Inc.
 *
 * Licensed under the Hazelcast Community License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://hazelcast.com/hazelcast-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.hazelcast.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hazelcast.cluster.Address;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Implementation for Nacos Discovery Strategy
 *
 * @author Kdump
 */
public class NacosDiscoveryStrategy extends AbstractDiscoveryStrategy {

    private static final String DEFAULT_NAMESPACE = "discovery-hazelcast";
    private static final String DEFAULT_CLUSTER_NAME = "hazelcast";

    private final DiscoveryNode thisNode;
    private final ILogger logger;

    private String applicationName;
    private String clusterName;
    private List<String> clusters = new ArrayList<>();
    private NamingService namingService;
    private Instance instance;

    public NacosDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.thisNode = discoveryNode;
        this.logger = logger;
    }

    private boolean isMember() {
        return thisNode != null;
    }

    @Override
    public void start() {
        try {
            startCuratorClient();
        } catch (NacosException e) {
            e.printStackTrace();
        }

        try {
            instance = new Instance();
            if (isMember()) {
                //register members only into nacos
                //there no need to register clients
                prepareServiceInstance();
                namingService.registerInstance(applicationName, instance);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to Nacos. ", e);
        }
    }

    private void prepareServiceInstance() {
        Address privateAddress = thisNode.getPrivateAddress();
        instance.setIp(privateAddress.getHost());
        instance.setPort(privateAddress.getPort());
        instance.setClusterName(clusterName);
        Map<String, String> instanceMeta = new HashMap<>(1);
        instanceMeta.put("cluster", clusterName);
        instance.setMetadata(instanceMeta);
    }

    private void startCuratorClient() throws NacosException {
        String serverAddr = getOrDefault(NacosDiscoveryProperties.SERVER_ADDR,
                System.getenv("NACOS_REGISTRY_SERVER_ADDR"));
        String namespace = getOrDefault(NacosDiscoveryProperties.NAMESPACE,
                Optional.ofNullable(System.getenv("NACOS_REGISTRY_NAMESPACE"))
                        .orElse(DEFAULT_NAMESPACE));

        clusterName = getOrDefault(NacosDiscoveryProperties.CLUSTER_NAME,
                Optional.ofNullable(System.getenv("NACOS_CLUSTER_NAME"))
                .orElse(DEFAULT_CLUSTER_NAME));

        applicationName = getOrDefault(NacosDiscoveryProperties.APPLICATION_NAME,
                System.getenv("HZ_APPLICATION_NAME"));
        if (applicationName == null){
            throw new IllegalStateException("applicationName cannot be null.");
        }
        clusters.add(clusterName);

        if (serverAddr == null) {
            throw new IllegalStateException("Nacos ServerAddr cannot be null.");
        }
        logger.finest(String.format("Using %s as Nacos URL, namespace is %s", serverAddr, namespace));

        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);

        namingService = NamingFactory.createNamingService(properties);
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        try {
            List<Instance> members = namingService.getAllInstances(applicationName, clusters, true);
            List<DiscoveryNode> nodes = new ArrayList<>(members.size());
            for (Instance member : members) {
                Address address = new Address(member.getIp(), member.getPort());
                nodes.add(new SimpleDiscoveryNode(address));
            }
            return nodes;
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to Nacos", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (isMember() && namingService != null) {
                namingService.deregisterInstance(applicationName, instance);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to Nacos", e);
        }
    }
}
