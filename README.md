# ktor-portfolio

Eventually this project may become a simple, configurable, art portfolio web site implementation built on the 
[Ktor](https://github.com/kotlin/ktor) web application framework for the [Kotlin](https://kotlinlang.org/) 
programming language.
 
That's the pipe dream intention but, as of this writing (June 25, 2017), I am just trying to understand 
how to construct and run a basic [Ktor](https://github.com/kotlin/ktor) application. The 
[Ktor documentation](https://github.com/Kotlin/ktor/wiki) is, understandably, very much in its infancy at 
this point; half of the Google search results that you are likely to get are for the *Star Wars: Knights 
of the Old Republic* game (KotOR is close to Kotr)!

## A Skeleton Application

With a bit of poking around you may eventually find that a 'Hello World' application, returning plain text 
not HTML, can be coded using [Kotr Features](https://github.com/Kotlin/ktor/wiki/Features) like this:

```
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("Hello, World!")
        }
    }
}
```

Fine, but how how do you get that to compile? Well, first you will need these imports (I am not a fan of 
wildcarding imports):

```
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.get
```

To compile with a Maven POM (assuming you already have a POM that works for compiling Kotlin) you will 
need to add this repository definition:

```
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-kotlin-ktor</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/kotlin/ktor</url>
    </repository>
</repositories>
```

and these properties / dependencies:

```
<properties>
    ...
    <kotlin.version>1.1.2-4</kotlin.version>
    <ktor.version>0.3.3</ktor.version>
    ...
</properties>

<dependencies>
    ...
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib</artifactId>
        <version>${kotlin.version}</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.ktor</groupId>
        <artifactId>ktor-core</artifactId>
        <version>${ktor.version}</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.ktor</groupId>
        <artifactId>ktor-netty</artifactId>
        <version>${ktor.version}</version>
    </dependency>
    ...
</dependencies>
```

## Where Is The `main(...)` Function?

Great, it compiles, but how do you run it? Downloading the [Ktor](https://github.com/kotlin/ktor) project 
and studying the ['Hello World' sample code](https://github.com/Kotlin/ktor/tree/master/ktor-samples) 
provides the necessary clues. 

As of this writing, the [README.md](https://github.com/Kotlin/ktor/blob/master/ktor-samples/README.md) 
instructions for running in under **IntelliJ IDEA** are out of date. To run with Netty, the class
you want to point to is `org.jetbrains.ktor.netty.DevelopmentHost`. It is this that provides the 
`main(args: Array<String>)` that you need.

But you are not done yet ...

## Configuring the Skeleton App

Finding the application entry point is just the start though, it expects to have a configuration.
The `main(...)` function instantiates an `ApplicationHostEnvironment`
object from either command line parameters or a default `application.conf` file. The `main(...)` 
function finishes by using the configuration to create a `NettyApplicationHost` and starting that. For
all this to work, you have to provide the `application.conf` file in your `resources` directory; here's
one ripped off from the [Ktor 'Hello World' example](https://github.com/Kotlin/ktor/blob/master/ktor-samples/ktor-samples-hello/resources/application.conf):

```
ktor {
    deployment {
        port = 8080
        autoreload = true
        watch = [ hello ]
    }

    application {
        modules = [ com.mikebway.ktor.WebSiteKt.main ]
    }
}
```

We also need to configure logging by adding POM dependencies for [Logback](https://logback.qos.ch/) and
a `logback.xml` file to the `resources` directory.

The [Logback](https://logback.qos.ch/) POM dependencies:

```
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>jul-to-slf4j</artifactId>
            <version>1.7.12</version>
        </dependency>        
```

The `resources/logback.xml` file:

```
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="trace">
        <appender-ref ref="STDOUT"/>
    </root>

    <logger name="io.netty" level="INFO"/>

</configuration>
```

## Running With Maven

You have enough now to run the application from your IDE through the 
`org.jetbrains.ktor.netty.DevelopmentHost` main class. To run on the command line with `mvn exec:java`
we just need to add the `exec-maven-plugin` plugin and point it to the :

```
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>

    <configuration>
        <mainClass>org.jetbrains.ktor.netty.DevelopmentHost</mainClass>
    </configuration>
</plugin>
```




