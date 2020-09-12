package io.github.ztkmkoo.dss.core.message.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;

public class DssAuthorizationCommandRequestTest {

	@Test
	void testToString() {
		final ActorTestKit testKit = ActorTestKit.create();
        final TestProbe<DssAuthCommand> probe = testKit.createTestProbe();

        final DssAuthorizationCommandRequest request = DssAuthorizationCommandRequest
        		.builder()
        		.token("testToken")
        		.valid(probe.ref())
        		.build();
        assertFalse(request.toString().isEmpty());
	}
	
	@Test
	void getToken() {
		final ActorTestKit testKit = ActorTestKit.create();
        final TestProbe<DssAuthCommand> probe = testKit.createTestProbe();

        final DssAuthorizationCommandRequest request = DssAuthorizationCommandRequest
        		.builder()
        		.token("testToken")
        		.valid(probe.ref())
        		.build();
        assertEquals("testToken", request.getToken());
	}
}
