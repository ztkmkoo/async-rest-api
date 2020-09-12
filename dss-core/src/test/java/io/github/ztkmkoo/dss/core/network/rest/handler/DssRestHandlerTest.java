package io.github.ztkmkoo.dss.core.network.rest.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import io.github.ztkmkoo.dss.core.actor.AbstractDssActorTest;
import io.github.ztkmkoo.dss.core.message.rest.DssRestChannelHandlerCommand;
import io.github.ztkmkoo.dss.core.message.rest.DssRestChannelHandlerCommandResponse;
import io.github.ztkmkoo.dss.core.message.rest.DssRestChannelInitializerCommand;
import io.github.ztkmkoo.dss.core.message.rest.DssRestMasterActorCommand;
import io.github.ztkmkoo.dss.core.message.rest.DssRestMasterActorCommandRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * Project: dss
 * Created by: @ztkmkoo(ztkmkoo@gmail.com)
 * Date: 20. 3. 3. 오후 11:16
 */
public class DssRestHandlerTest extends AbstractDssActorTest {

    private final HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hi/hello");
    private final HttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8));

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void channelRead0() throws Exception {
        mockChannelHandlerContextChannelId(ctx, "abcedf");

        final TestProbe<DssRestMasterActorCommand> testProbe = testKit.createTestProbe();
        final DssRestHandler handler = newDssRestHandlerForTest(testProbe);
        testKit.spawn(handler.create());

        handler.channelRead0(ctx, request);

        final HttpRequest httpRequest = getDssRestHandlerFieldWithReflection(handler, "request", HttpRequest.class);
        assertEquals(HttpVersion.HTTP_1_1, httpRequest.protocolVersion());
        assertEquals(HttpMethod.GET, httpRequest.method());
        assertEquals("/hi/hello", httpRequest.uri());

        handler.channelRead0(ctx, content);
        final StringBuilder stringBuilder = getDssRestHandlerFieldWithReflection(handler, "buffer", StringBuilder.class);
        assertEquals("Hello", stringBuilder.toString());
    }

    @Test
    public void channelReadComplete() throws Exception {
        mockChannelHandlerContextChannelId(ctx, "abcedf");

        final TestProbe<DssRestMasterActorCommand> testProbe = testKit.createTestProbe();
        final DssRestHandler handler = newDssRestHandlerForTest(testProbe);
        testKit.spawn(handler.create());

        handler.channelRead0(ctx, request);
        handler.channelRead0(ctx, content);

        handler.channelReadComplete(ctx);

        testProbe.expectMessageClass(DssRestMasterActorCommandRequest.class);
    }

    @Test
    public void exceptionCaught() throws NoSuchFieldException, IllegalAccessException {

        mockChannelHandlerContextChannelId(ctx, "abcedf");

        final TestProbe<DssRestMasterActorCommand> testProbe = testKit.createTestProbe();
        final DssRestHandler handler = newDssRestHandlerForTest(testProbe);

        addChannelHandlerContextToDssRestHandlerMap(handler, ctx);

        handler.exceptionCaught(ctx, new NullPointerException());

        assertTrue(true);
    }

    @Test
    public void exceptionCaughtSkippedWhenContextRemoved() {

        mockChannelHandlerContextChannelId(ctx, "abcedf");

        final TestProbe<DssRestMasterActorCommand> testProbe = testKit.createTestProbe();
        final DssRestHandler handler = newDssRestHandlerForTest(testProbe);
        handler.exceptionCaught(ctx, new NullPointerException());

        assertTrue(true);
    }

    @Test
    public void create() throws NoSuchFieldException, IllegalAccessException {

        mockChannelHandlerContextChannelId(ctx, "abcedf");

        final ChannelFuture channelFuture = Mockito.mock(ChannelFuture.class);
        Mockito.when(ctx.writeAndFlush(Mockito.anyObject())).thenReturn(channelFuture);

        final TestProbe<DssRestMasterActorCommand> testProbe = testKit.createTestProbe();
        final DssRestHandler handler = newDssRestHandlerForTest(testProbe);

        addChannelHandlerContextToDssRestHandlerMap(handler, ctx);

        final ActorRef<DssRestChannelHandlerCommand> handlerActorRef = testKit.spawn(handler.create());
        assertNotNull(handlerActorRef);

        handlerActorRef.tell(DssRestChannelHandlerCommandResponse.builder().channelId("abcedf").build());
    }

    private static DssRestHandler newDssRestHandlerForTest(TestProbe<DssRestMasterActorCommand> testProbe) {
        final TestProbe<DssRestChannelInitializerCommand> testProbe1 = testKit.createTestProbe();
        return new DssRestHandler(testProbe1.ref(), testProbe.ref(),"test-handler");
    }

    private static void addChannelHandlerContextToDssRestHandlerMap(DssRestHandler handler, ChannelHandlerContext ctx) throws NoSuchFieldException, IllegalAccessException {

        final Map<String, ChannelHandlerContext> channelHandlerContextMap = getDssRestHandlerFieldWithReflection(handler, "channelHandlerContextMap", Map.class);
        channelHandlerContextMap.put(ctx.channel().id().asLongText(), ctx);
    }

    private static <T> T getDssRestHandlerFieldWithReflection(DssRestHandler handler, String fieldName, Class<T> tcLass) throws NoSuchFieldException, IllegalAccessException {

        final Field field = DssRestHandler.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return tcLass.cast(field.get(handler));
    }

    private static void mockChannelHandlerContextChannelId(ChannelHandlerContext ctx, String longId) {

        final Channel mockChannel = Mockito.mock(Channel.class);
        final ChannelId channelId = Mockito.mock(ChannelId.class);

        Mockito.when(ctx.channel()).thenReturn(mockChannel);
        Mockito.when(mockChannel.id()).thenReturn(channelId);
        Mockito.when(channelId.asLongText()).thenReturn(longId);
    }
}