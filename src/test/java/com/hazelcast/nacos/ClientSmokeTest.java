package com.hazelcast.nacos;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(HazelcastSerialClassRunner.class)
public class ClientSmokeTest extends HazelcastTestSupport {

    //use an unusual port so clients won't guess it without Nacos
    private static final int HAZELCAST_BASE_PORT = 9999;
    private static final int CLUSTER_SIZE = 2;

    private final HazelcastInstance[] instances = new HazelcastInstance[CLUSTER_SIZE];

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws IOException {
        HazelcastClient.shutdownAll();
        Hazelcast.shutdownAll();
    }

    @Test
    public void testClientCanConnectionToCluster() {
        String serverAddr = "10.22.0.137:31548";
        String applicationName = "edsp-server-adm";
        startCluster(serverAddr, applicationName);

        ClientConfig clientConfig = createClientConfig(serverAddr, applicationName);

        //throws an exception when it cannot connect to a cluster
        HazelcastClient.newHazelcastClient(clientConfig);
    }

    private ClientConfig createClientConfig(String serverAddr, String applicationName) {
        DiscoveryStrategyConfig discoveryStrategyConfig = createDiscoveryConfig(serverAddr, applicationName);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty("hazelcast.discovery.enabled", "true");
        clientConfig.getNetworkConfig().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
        return clientConfig;
    }

    private void startCluster(String serverAddr, String applicationName) {
        Config config = createMemberConfig(serverAddr, applicationName);
        for (int i = 0; i < CLUSTER_SIZE; i++) {
            instances[i] = Hazelcast.newHazelcastInstance(config);
        }
    }

    private Config createMemberConfig(String serverAddr, String applicationName) {
        Config config = new Config();
        config.getNetworkConfig().setPort(HAZELCAST_BASE_PORT);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.setProperty("hazelcast.discovery.enabled", "true");

        DiscoveryStrategyConfig discoveryStrategyConfig = createDiscoveryConfig(serverAddr, applicationName);
        config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
        return config;
    }

    private DiscoveryStrategyConfig createDiscoveryConfig(String serverAddr, String applicationName) {
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new NacosDiscoveryStrategyFactory());
        discoveryStrategyConfig.addProperty(NacosDiscoveryProperties.SERVER_ADDR.key(), serverAddr);
        discoveryStrategyConfig.addProperty(NacosDiscoveryProperties.APPLICATION_NAME.key(), applicationName);
        return discoveryStrategyConfig;
    }
}
