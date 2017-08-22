zuchini
=======

A reimplementation of gherkin and cucumber for java based on antlr4 and junit.

[![Build Status](https://img.shields.io/travis/jhorstmann/zuchini.svg)](https://travis-ci.org/jhorstmann/zuchini)
[![Maven Central](https://img.shields.io/maven-central/v/org.zuchini/zuchini-junit.svg)](https://search.maven.org/#artifactdetails%7Corg.zuchini%7Czuchini-junit%7C0.2.0%7Cjar)

## Usage

To start using zuchini just add the dependency to the pom.
````        
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
````

After this is added you can start creating feature files with the standard [Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin) syntax.
Step definitions can be added in the same way as with cucumber.

## Reporting

To enable reporting add the zuchini-reporting dependency to your project.
````        
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-reporting</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
````

Additionally you have to configure your surefire plugin to write the report to a given file:
````
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.18</version>
    <configuration>
        <systemPropertyVariables>
            <zuchini.reporter.output>${project.build.directory}/report.json</zuchini.reporter.output>
        </systemPropertyVariables>
    </cofiguration>
</plugin>
````

Now add the `JsonReporter` listener to your zuchini test case.
````
@RunWith(SpringZuchini.class)
@ZuchiniOptions(
    stepDefinitionPackages = {"de/my/company/features"},
    featurePackages = {"features"},
    listeners = JsonReporter.class)
public class ZuchiniTest {
}
````

This will produce a json report at the given location.

**ATTENTION**: This report does not conform to the cucumber standard and therefore cannot be interpreted by the usual report generation plugins! 


## Possible problems

### Intellij Idea step definitions unknown

Idea will tell you that your steps have no definition and will offer to create one for you. To get Idea to recognize your implementation
switch the annotations from the zuchini annotations to the original cucumber annotations.

To do this just add
````        
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-compat-annotations</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
````
to your dependency list. Then a version of the original `Given`, `When` and `Then` annotations are usable without having the whole cukes set in your classpath (we are trying to avoid it in the end, right ;) ).

### Spring Boot 1.5 crashes


To run zuchini tests with Spring Boot 1.5 or above just add the zuchini-spring dependency to your project.
````        
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-spring</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
````
Now annotate your zuchini test with `@SpringBootTest` instead of the old `@ContextConfiguration` annotation and you are good to go.

````
@RunWith(SpringZuchini.class)
@ZuchiniOptions(
    stepDefinitionPackages = {"de/my/project/features"},
    featurePackages = {"features"}
@SpringBootTest
public class ZuchiniTest {
}

````

If this does crash on you with some internal Spring Exceptions just exclude the spring dependencies from zuchini-spring like so:
````
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-spring</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
        </exclusion>
    </exclusions>
</dependency>
````
Most probably though this is a sign that your dependencies are not set up correctly as Spring Boot should overwrite 
the version that you are referencing here.