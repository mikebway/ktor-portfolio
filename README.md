# ktor-portfolio

Eventually this project may become a simple, configurable, art portfolio web site implementation built on the 
[Ktor](https://github.com/kotlin/ktor) web application framework for the [Kotlin](https://kotlinlang.org/) 
programming language.
 
That's the pipe dream intention but, as of this writing (June 25, 2017), I am just trying to understand 
how to construct and run a basic [Ktor](https://github.com/kotlin/ktor) application. The 
[Ktor documentation](https://github.com/Kotlin/ktor/wiki) is, understandably, very much in its infancy at 
this point; half of the Google search results that you are likely to get are for the *Star Wars: Knights 
of the Old Republic* game (KotOR is close to Kotr)!

### Update - July 4, 2017

Having completed the next step of returning HTML from a handler declared outside of the `Application.main()` 
function (see [Separate HTML Handlers](#html-handlers) below), I will be shelving this project for the 
time being. 

[Ktor](https://github.com/kotlin/ktor) is a promising Web application and microservice framework that,
built on the [Kotlin coroutine system](https://kotlinlang.org/docs/reference/coroutines.html), should be 
able to maximize the ability of lightweight servers to handle high traffic volumes.
Unfortunately, at this time, the Ktor documentation is not yet mature enough for an outsider to utilize 
the framework to build an application. The functionality appears to be present in the source code (e.g. to 
return static files or match path expressions) but playing Sherlock Holmes to make sense of the 
uncommented forest of Ktor's expert Kotlin syntax is not an efficient way to learn how to apply it.

I'll be monitoring [Ktor's](https://github.com/kotlin/ktor) progress and look forward to reviving this 
project once the documentation has filled out and the code base is more stable.

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
wildcarding):

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

To compile with a Maven POM you will need to add this repository definition:

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
    <jvm.target>1.8</jvm.target>
    <kotlin.version>1.1.2-5</kotlin.version>
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

and this Kotlin compiler plugin:

```
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>${kotlin.version}</version>
    <configuration>
        <jvmTarget>${jvm.target}</jvmTarget>
    </configuration>
    <executions>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Where Is The `main(...)` Function?

Great, it compiles, but how do you run it? Downloading the [Ktor](https://github.com/kotlin/ktor) project 
and studying the ['Hello World' sample code](https://github.com/Kotlin/ktor/tree/master/ktor-samples) 
provides the necessary clues. 

As of this writing, the [Ktor sample code README.md](https://github.com/Kotlin/ktor/blob/master/ktor-samples/README.md) 
instructions for running in under **IntelliJ IDEA** are out of date. To run with Netty, the class
you want to point to is `org.jetbrains.ktor.netty.DevelopmentHost`. It is this that provides the 
`main(args: Array<String>)` that you need.

But you are not done yet ...

## Configuring the Skeleton App

Finding the application entry point was just the start; it expects to have a configuration.
The `main(...)` function configures an `ApplicationHostEnvironment`
object using either command line parameters or a default `application.conf` file. The `main(...)` 
function finishes by using the environment to a `NettyApplicationHost` and starting that. For
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
<properties>
    ...
    <logback.version>1.2.1</logback.version>
    <slf4j.version>1.7.12</slf4j.version>
    ...
</properties>
 
<dependencies>
    ...
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback.version}</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jul-to-slf4j</artifactId>
        <version>${slf4j.version}</version>
    </dependency>        
    ...
<dependencies>
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

## Shading For Command Line Execution

The final step, configuring the POM to build a fat, all-in-one, jar file that will allow execution from
the command line like this:

```
java -jar ktor-portfolio-0.5-SNAPSHOT.jar
```

You just have to add the `maven-shade-plugin` to the POM and configure it with the 
`org.jetbrains.ktor.netty.DevelopmentHost` main class:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <!-- Run shade goal on package phase -->
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <!-- add Main-Class to manifest file -->
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>org.jetbrains.ktor.netty.DevelopmentHost</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## <a id="html-handlers"></a>Separate HTML Handlers

After getting the minimal skeleton to run, a natural next step is to generate HTML instead of text and 
separate the hander implemenation into its own file, away from the `Application.main(...)` function.
 
To render HTML you need to add the Ktor HTML dependency to the POM file:

```
<dependency>
    <groupId>org.jetbrains.ktor</groupId>
    <artifactId>ktor-html-builder</artifactId>
    <version>${ktor.version}</version>
</dependency>
```

To invoke an external lambda expression from the `get('/')` route, the function name (`home`) has to be declared 
as an explicit parameter inside the parenthesis:
 
```
...
import com.mikebway.ktor.handlers.home
...

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/", home)
    }
}
```

The only tricky part is to understand the declaration of the `home` lambda as a receiver of type
`PipelineInterceptor<ApplicationCall>`. For this project, `home` is defined in the file
[Home.kt](src/main/kotlin/com/mikebway/ktor/handlers/Home.kt) as follows:

```
val home: PipelineInterceptor<ApplicationCall> = {
    call.respondHtml {
        head {
            title { +"HTML Application" }
        }
        body {
            h1 { +"Sample application with HTML builders" }
            p { +"Ktor shows promise as a lightweight but sophisticated Web framework." }
        }
    }
}
```

Making sense of how this works this takes some detective effort. `PipelineInterceptor<ApplicationCall>` is
a type alias declared as follows:

```
typealias PipelineInterceptor<TSubject> = suspend PipelineContext<TSubject>.(TSubject) -> Unit
```

which means that `val home: PipelineInterceptor<ApplicationCall>` declares `home` as a lambda receiver
of the `ApplicationCall` reification of the generic `PipelineContext<TSubject>` class, in effect
a member property of the `PipelineContext<ApplicationCall>`. The `.(TSubject)` parameter declaration 
indicates that the lambda is passed an instance of `ApplicationCall` as a parameter, accessible as `it`.

The `suspend` keyword signals that the [Kotlin coroutine system](https://kotlinlang.org/docs/reference/coroutines.html)
may suspend execution of the function to switch the current thread to execute other paused activities.

The `call` in `call.respondHtml` invokes an extension member property of `PipelineContext<ApplicationCall>`
that exposes the `subject` property of `PipelineContext<TSubject>` as an explicit `ApplicationCall` object. 
Using `call` follows the pattern established by the Ktor documentation and [sample code](https://github.com/Kotlin/ktor/blob/master/ktor-samples/ktor-samples-html/src/org/jetbrains/ktor/samples/html/HtmlApplication.kt).
Exactly the same effect could be achieved if `call` were replaced by either `subject`, referring to the
actual backing property define in `PipelineContext<TSubject>`, or `it`, referring to the anonymous 
`ApplicationCall` parameter passed to the lambda. A `println` call confirmed that all three have the
same value!
 
Why so many ways to do the same thing? `call` makes sense as a self describing artifact of the Ktor DSL
but passing the same `ApplicationCall` as an `it` parameter seems entirely redundant. By the time anyone 
reads this the Ktor code may well have evolved to reduce this confusion of choices.

## Tests And Warnings

The project has zero unit tests as yet, that's barely excusable except that I wanted to be sure that
I could actually get a Ktor server running at all before I invested any effort in writing code.

The same justification applies to the build warnings thrown off by Maven. Ordinarilly I consider a
warning to be the equivalent of an error; for now, I am turning a blind eye on those until I have proved 
to myself that I can realistically use [Ktor](https://github.com/kotlin/ktor) to match at least the 
static placeholder content that I currently have at [mikebroadway.com](http://mikebroadway.com).

