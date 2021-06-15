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
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;

import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

/**
 * The type Nacos discovery properties.
 */
public final class NacosDiscoveryProperties {

    /**
     * Connection string to your Nacos server.
     * Default: There is no default, this is a required property.
     * Example: 127.0.0.1:8848
     *
     */
    public static final PropertyDefinition SERVER_ADDR = property("server_addr", STRING);

    /**
     * instance.setServiceName(applicationName)
     *
     */
    public static final PropertyDefinition APPLICATION_NAME = property("application_name", STRING);

    /**
     * Namespace in Nacos Hazelcast will use
     * Default: discovery-hazelcast
     *
     */
    public static final PropertyDefinition NAMESPACE = property("namespace", STRING);

    /**
     * Name of this Hazelcast cluster. You can have multiple distinct clusters to use the
     * same Nacos installation.
     *
     */
    public static final PropertyDefinition CLUSTER_NAME = property("cluster_name", STRING);


    private NacosDiscoveryProperties() {
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter) {
        return property(key, typeConverter, null);
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter,
                                               ValueValidator valueValidator) {
        return new SimplePropertyDefinition(key, true, typeConverter, valueValidator);
    }
}
