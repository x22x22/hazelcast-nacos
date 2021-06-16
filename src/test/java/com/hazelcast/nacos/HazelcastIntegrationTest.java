package com.hazelcast.nacos;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HazelcastIntegrationTest {


    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws IOException {
        Hazelcast.shutdownAll();
    }

    @Test
    public void testIntegration() {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.setProperty("hazelcast.discovery.enabled", "true");

        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new NacosDiscoveryStrategyFactory());
        discoveryStrategyConfig.addProperty(NacosDiscoveryProperties.SERVER_ADDR.key(), System.getenv("NACOS_REGISTRY_SERVER_ADDR"));
        discoveryStrategyConfig.addProperty(NacosDiscoveryProperties.APPLICATION_NAME.key(), "hz-server");
        config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);

        int instance1Size = instance1.getCluster().getMembers().size();
        assertEquals(2, instance1Size);
        int instance2Size = instance2.getCluster().getMembers().size();
        assertEquals(2, instance2Size);
    }

    @SuppressWarnings({"unchecked"})
    public static void removeEnv(String envName) throws ReflectiveOperationException {
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.remove(envName);
        Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.remove(envName);
    }

    @Test
    public void testIntegration_urlNotConfigured() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Nacos ServerAddr cannot be null.");
        try {
            removeEnv("NACOS_REGISTRY_SERVER_ADDR");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.setProperty("hazelcast.discovery.enabled", "true");

        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new NacosDiscoveryStrategyFactory());
        config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);

        int instance1Size = instance1.getCluster().getMembers().size();
        assertEquals(2, instance1Size);
        int instance2Size = instance2.getCluster().getMembers().size();
        assertEquals(2, instance2Size);
    }
}
