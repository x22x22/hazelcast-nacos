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

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Factory class for NacosDiscoveryStrategy
 */
public class NacosDiscoveryStrategyFactory implements DiscoveryStrategyFactory {

    private static final Collection<PropertyDefinition> PROPERTY_DEFINITIONS;

    static {
        List<PropertyDefinition> propertyDefinitions = new ArrayList<>();
        propertyDefinitions.add(NacosDiscoveryProperties.SERVER_ADDR);
        propertyDefinitions.add(NacosDiscoveryProperties.NAMESPACE);
        propertyDefinitions.add(NacosDiscoveryProperties.CLUSTER_NAME);
        propertyDefinitions.add(NacosDiscoveryProperties.APPLICATION_NAME);
        propertyDefinitions.add(NacosDiscoveryProperties.USERNAME);
        propertyDefinitions.add(NacosDiscoveryProperties.PASSWORD);
        propertyDefinitions.add(NacosDiscoveryProperties.ACCESS_KEY);
        propertyDefinitions.add(NacosDiscoveryProperties.SECRET_KEY);
        PROPERTY_DEFINITIONS = Collections.unmodifiableCollection(propertyDefinitions);
    }

    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return NacosDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger,
                                                  Map<String, Comparable> properties) {
        return new NacosDiscoveryStrategy(discoveryNode, logger, properties);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        return PROPERTY_DEFINITIONS;
    }
}
