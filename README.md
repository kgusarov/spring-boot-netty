# spring-boot-netty

## Build and Code Coverage Status
[![Build Status](https://travis-ci.org/kgusarov/spring-boot-netty.svg?branch=master)](https://travis-ci.org/kgusarov/spring-boot-netty)
[![Coverage](https://codecov.io/gh/kgusarov/spring-boot-netty/branch/master/graph/badge.svg)](https://codecov.io/gh/kgusarov/spring-boot-netty)

## Motivation
Both [Spring Boot](https://spring.io/projects/spring-boot) and [Netty](https://netty.io/) are 
awesome projects! So, why not use them together? Imagine, how cool it would be, if you were
able to use something similar to [Spring WebMvc](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
when working with Netty!
So, this is what this little project is aimed for. Initially, I've developed this for myself in order to be able
to use Netty in several projects along with Spring Boot. 

## HOWTO
### Very first steps
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