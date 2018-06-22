package edu.gla.kail.ac.core;

import com.google.cloud.Tuple;
import edu.gla.kail.ad.core.DialogflowAgentAuthorizationSingleton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DialogflowAgentAuthorizationSingletonTest {
    @Before
    public void setUp() {

    }
    @After
    public void cleanUp() {

    }

    @Test
    public void testInitialization () {
        Tuple<String, String> tuple = Tuple.of("TestProjectId", "TestFileLocalisation");
        DialogflowAgentAuthorizationSingleton dialogflowAgentAuthorizationSingleton = new DialogflowAgentAuthorizationSingleton(); }
}
