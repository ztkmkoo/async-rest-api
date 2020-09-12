package io.github.ztkmkoo.dss.core.actor.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import io.github.ztkmkoo.dss.core.actor.AbstractDssActorTest;
import io.github.ztkmkoo.dss.core.message.security.DssAuthCommand;
import io.github.ztkmkoo.dss.core.message.security.DssAuthenticationCommandRequest;
import io.github.ztkmkoo.dss.core.message.security.DssAuthenticationCommandResponse;
import io.github.ztkmkoo.dss.core.message.security.DssAuthorizationCommandRequest;
import io.github.ztkmkoo.dss.core.message.security.DssAuthorizationCommandResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DssRestAuthActorTest /*extends AbstractDssActorTest*/ {

	 private final ActorTestKit testKit = ActorTestKit.create();
	 private final TestProbe<DssAuthCommand> probe = testKit.createTestProbe();
	 private final ActorRef<DssAuthCommand> restAuthActorRef = testKit.spawn(DssRestAuthActor.create(testUserList()), "rest-auth");
	 private String testToken;
	 
	 private static Map<String, String> testUserList() {
		 final Map<String, String> userList = new HashMap<>();
		 userList.put("testID", "testPassword");
		 
		 return userList;
	 }
	 
	 @BeforeAll
	 public void prepareTestData() {

	 }
	 
	 @Test
	 @Order(1)
	 void handlingDssAuthenticationCommandRequest() {
		 restAuthActorRef.tell(DssAuthenticationCommandRequest
				 .builder()
				 .userID("testID")
				 .userPassword("testPassword")
				 .token(probe.ref())
				 .build());
		 
		 final DssAuthenticationCommandResponse response = probe.expectMessageClass(DssAuthenticationCommandResponse.class);
		 testToken = response.getToken();
		 
		 assertTrue(true);
		 assertNotNull(response.getToken());
	 }
	 
	 @Test
	 @Order(2)
	 void handlingDssAuthorizationCommandRequest() {			 
		 restAuthActorRef.tell(DssAuthorizationCommandRequest
				 .builder()
				 .token(testToken)
				 .valid(probe.ref())
				 .build());
		 
		 final DssAuthorizationCommandResponse response = probe.expectMessageClass(DssAuthorizationCommandResponse.class);
		 
		 assertTrue(true);
		 assertNotNull(response);
	     assertEquals("true", response.getValid());
	 }
}
