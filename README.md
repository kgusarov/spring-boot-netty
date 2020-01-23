# spring-boot-netty

[![Build Status](https://travis-ci.org/kgusarov/spring-boot-netty.svg?branch=master)](https://travis-ci.org/kgusarov/spring-boot-netty)
[![Coverage](https://codecov.io/gh/kgusarov/spring-boot-netty/branch/master/graph/badge.svg)](https://codecov.io/gh/kgusarov/spring-boot-netty)
[![Maven Central](https://img.shields.io/maven-central/v/org.kgusarov/spring-boot-netty.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.kgusarov%22%20AND%20a:%22spring-boot-netty%22)

## Latest Release Highlights
* [Method Handles](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/MethodHandle.html) are now used to invoke handler methods.
* Updated [Netty](https://netty.io/) version

## Motivation
Both [Spring Boot](https://spring.io/projects/spring-boot) and [Netty](https://netty.io/) are 
awesome projects! So, why not use them together? Imagine, how cool it would be, if you were
able to use something similar to [Spring WebMvc](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
when working with Netty!

So, this is what this little project is aimed for. Initially, I've developed this for myself in order to be able
to use Netty in several projects along with Spring Boot. 

## HOWTO
### Basics
As, it is already mentioned, this project aims to be similar to [Spring WebMvc](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html),
so here we have concepts of controllers and handler methods, as well as filters. So, where should you start? I like to 
say, that the code is the best documentation! So, we will look into codes, and I am going to refer some tests, you might
look at! 
In order to integrate Spring Boot with Netty, you have to use `@EnableNettyServers` annotation on your configuration class.
This will turn the integration ON. However, in comparison to some other Spring Boot _Enable_ annotations, you 
WILL have to do some additional work - since we don't have a lot of default values here! First thing to do, is to add
the configuration to the Spring Context (for example, via _.yaml_ file):
```yaml
netty:
  servers:
    - name: server1
      host: localhost
      port: 40000
```   
Take a look at [this class](https://github.com/kgusarov/spring-boot-netty/blob/master/src/main/java/org/kgusarov/integration/spring/netty/configuration/SpringNettyConfigurationProperties.java)
for possible configuration properties, however for us it is time to move on! Next thing to do is to create some handlers
for your server. If you have defined at least one server in configuration, you must have some handlers for it, otherwise,
you will see `BeanCreationException` here. Below, we will explore various handlers and how they work together. 

We are going to start with `@NettyOnConnect` handler. This one allows to react on connection events. All the handlers
should be present in a class annotated with `@NettyController` annotation. So, in case we want to react on connect 
event, we should do the following:
```java
@NettyController
public class OnConnectController {   
    @NettyOnConnect(serverName = "server1", priority = 1)
    private void onConnect1() {
        System.out.println("Hello, world!");
    }

    @NettyOnConnect(serverName = "server1", priority = 2)
    ByteBuf onConnect2(final ChanneHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copyLong(new Random().nextLong()));
        return Unpooled.copyLong(new Random().nextLong());
    }
}
``` 
As a result, when conneting to the server, you should see _Hello, world!_ in server's stdout as well as receive two random 
`long` numbers. As you can see, handlers have priority (this applies for all types of them, not only connect ones). Also, 
if the method returns something, this _something_ will be sent to client. Also it is possible to inject some arguments
into the handler method. This is ensured by having [method parameter resolvers](https://github.com/kgusarov/spring-boot-netty/blob/master/src/main/java/org/kgusarov/integration/spring/netty/support/resolvers/NettyOnConnectParameterResolver.java)
. By default, it is possible to receive `ChanneHandlerContext` or `Channel` as a connection handler parameter.

Now, moving to `@NettyOnDisconnect` handler. Basic idea here is the same, create controller, mark its appropriate method, 
consider [method parameter resolvers](https://github.com/kgusarov/spring-boot-netty/blob/master/src/main/java/org/kgusarov/integration/spring/netty/support/resolvers/NettyOnDisconnectParameterResolver.java)
and react on disconnect events. However, one notable difference here is that return value of the method is ignored. So, 
you can return whatever from disconnect handler, nothing will happen with it... Disconnect handlers can resolve
`ChannelFuture` and `Channel` as method parameters by default:
```java
@NettyController
public class OnDisconnectController {   
    @NettyOnDisconnect(serverName = "server1", priority = 1)
    private void onDisconnect1() {
        System.out.println("Hello, world!");
    }

    @NettyOnDisconnect(serverName = "server1", priority = 2)
    ByteBuf onDisconnect2() {
        System.out.println("Hello, world!");
        return Unpooled.copyLong(new Random().nextLong());
    }
}
```
Having server with such a controller and disconnecting from it, you should see _Hello, world!_ in server's stdout twice.

Next type of handlers is message handlers. These handlers rely on `@NettyOnMessage` annotation. They differ from ones 
previously described due to the fact, that these handlers are also supporting message body parameter resolution, with 
a help of `@NettyMessageBody` annotation. Only one such annotation should be present on given method parameters. 
[Method parameter resolvers](https://github.com/kgusarov/spring-boot-netty/blob/master/src/main/java/org/kgusarov/integration/spring/netty/support/resolvers/NettyOnMessageParameterResolver.java)
here by default support `ChannelHandlerContext`, `Channel` and `@NettyMessageBody` annotated parameter resolution.
Again, return value will be sent back to the client. Please, find an example below:
```java
@NettyController
public class SomeController {
    @NettyOnConnect(serverName = "server1")
    private void onConnect() {
        System.out.println("Client connected");
    }
    
    @NettyOnDisconnect(serverName = "server1")
    private void onDisconnect() {
        System.out.println("Client disconnected");
    }

    @NettyOnMessage(serverName = "server1", priority = 1)
    public String onStringMessage(@NettyMessageBody final String msg) {
        return msg;
    }
    
    @NettyOnMessage(serverName = "server1", priority = 2)
    void onLongMessage1(final ChannelHandlerContext ctx, final Channel channel, @NettyMessageBody final Long msg) {
        ctx.writeAndFlush(msg + 1);
        channel.writeAndFlush(msg + 2);
    }   
    
    @NettyOnMessage(serverName = "server1", priority = 3)
    long onLongMessage2(@NettyMessageBody final long msg) {
        return msg;
    } 

    @NettyOnMessage(serverName = "server1", priority = 4)
    private void onLongMessage3() {
    }    
}
```
Study this example, or also refer to [this test](https://github.com/kgusarov/spring-boot-netty/tree/master/src/integration-test/java/org/kgusarov/integration/spring/netty/onmessage)
to see, which handlers in which order depending on which message are getting called. :eyeglasses:

### Custom Method Parameter Resolvers
As it was mentioned, when working with `@NettyController` annotated classes, it is possible to pass various parameters
to the methods. These parameters are resolved with the help of [method parameter resolvers](https://github.com/kgusarov/spring-boot-netty/tree/master/src/main/java/org/kgusarov/integration/spring/netty/support/resolvers),
so if you need to introduce additional parameters for your handler methods, just implement appropriate resolver and mark it
as a Spring `@Component`. That should do the trick:
```java
@Component
public class RNG extends Random {
    private final List<Long> generatedNumbers = Lists.newArrayList();

    @Override
    public long nextLong() {
        final long result = super.nextLong();
        generatedNumbers.add(result);
        return result;
    }

    public List<Long> getGeneratedNumbers() {
        return generatedNumbers;
    }
}

@Component
public class RandomLongOnConnectResolver implements NettyOnConnectParameterResolver {
    private final RNG rng;

    @Autowired
    public RandomLongOnConnectResolver(final RNG rng) {
        this.rng = rng;
    }

    @Override
    public Object resolve(final ChannelHandlerContext ctx) {
        return rng.nextLong();
    }

    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType);
    }
}

@Component
public class RandomLongOnDisconnectResolver implements NettyOnDisconnectParameterResolver {
    private final RNG rng;

    @Autowired
    public RandomLongOnDisconnectResolver(final RNG rng) {
        this.rng = rng;
    }

    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelFuture future) {
        return rng.nextLong();
    }
}

@Component
public class RandomLongOnMessageResolver implements NettyOnMessageParameterResolver {
    private final RNG rng;

    @Autowired
    public RandomLongOnMessageResolver(final RNG rng) {
        this.rng = rng;
    }

    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelHandlerContext ctx, final Object msg) {
        return rng.nextLong();
    }
}
```
Refer to [this test](https://github.com/kgusarov/spring-boot-netty/tree/master/src/integration-test/java/org/kgusarov/integration/spring/netty/customresolvers)
for more information.

### Filters
The last part to be discussed, are filters. There is analogy between Netty filters marked with `@NettyFilter` annotation and
servlet filters in Java Servlet Specification. Classes marked with this annotation will be added to Netty handler pipeline
before any `@NettyController` classes will do the processing. Filters should implement `@ChannelHanlder` interfaces. 
Also, filters can be stateless and stateful. Netty itself achieves this using `@ChannelHandler.Sharable` annotation. Same
rules apply here with one additional requirement - if handler is not sharable, it should be defined in a
`ConfigurableBeanFactory.SCOPE_PROTOTYPE` scope to ensure Spring will create new instances of this bean when required!
So, say, you want your server to speak with client using `String` messages. Here's an example of appropriate filters
and handlers:
```java
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 1)
public class Decoder extends ReplayingDecoder<Object> {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        final int size = in.readInt();
        final byte[] bytes = new byte[size];

        in.readBytes(bytes, 0, size);
        out.add(new String(bytes, Charset.forName("UTF-8")));
    }
}

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 2)
public class Encoder extends MessageToByteEncoder<Object> {   
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) {        
        final String s = (String) msg;
        final byte[] bytes = s.getBytes(Charset.forName("UTF-8"));

        out.writeByte(1);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}


@NettyController
@SuppressWarnings("WeakerAccess")
public class OnMessageController {    
    @NettyOnMessage(serverName = "server1", priority = 1)
    public String onMessage(@NettyMessageBody final String msg) {
        // Do your processing here...
    }
}
```
NB! Priorities for `@NettyController` and `@NettyFilter` are used only with appropriate classes, which means first
filters are sorted by their priority, then controllers by their.
It is also possible to use only filters to do all the handling! Check 
[this test](https://github.com/kgusarov/spring-boot-netty/tree/master/src/integration-test/java/org/kgusarov/integration/spring/netty/nettyfilters)
for more information

## License
This project is licensed under my personal favorite - MIT License. 
 