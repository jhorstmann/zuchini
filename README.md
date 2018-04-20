# Zuchini - A gherkin / cucumber implementation for java based on antlr4 and junit.

[![Build Status](https://img.shields.io/travis/jhorstmann/zuchini.svg)](https://travis-ci.org/jhorstmann/zuchini)
[![Maven Central](https://img.shields.io/maven-central/v/org.zuchini/zuchini-junit.svg)](https://search.maven.org/#artifactdetails%7Corg.zuchini%7Czuchini-junit%7C0.2.0%7Cjar)

## Why Zuchini instead of Cucumber-JVM?

The main benefit is better integration with junit and spring-framework. This allows leveraging existing features of junit like

 - [Parallel test execution](zuchini-examples/zuchini-examples-parallel/pom.xml)
 - [Automatically retrying flaky test](zuchini-examples/zuchini-examples-flaky/pom.xml)
 - [Filtering which tests to execute](zuchini-examples/zuchini-examples-filtering/pom.xml)

The spring integration brings the following improvements

 - Step definition classes are normal spring components
 - No need to repeat context configuration annotations on each step definition class
 - Special [scenario scope](zuchini-spring/src/main/java/org/zuchini/spring/ScenarioScoped.java) that allows to configure mocks and keep state per scenario 

On the technical side the goals are

 - Clean implementation by delegating features to existing frameworks like junit and spring
 - Separation between gherkin model and test execution
 - Reusable [gherkin parser with a clearly defined grammar](zuchini-model/src/main/antlr4/org/zuchini/gherkin/antlr/Gherkin.g4)

## Usage

To start using zuchini add the dependency to the pom:

```xml
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-junit</artifactId>
    <version>${version.zuchini}</version>
    <scope>test</scope>
</dependency>
```

Add a feature file containing your scenarios in `src/test/resources/features`:

```gherkin
Feature: Hello World

  Scenario: Hello World
    Given the user name is 'World'
    When the user clicks the hello button
    Then the output is 'Hello World'
```

Currently due to [a bug](https://github.com/jhorstmann/zuchini/issues/8) the feature files has to end with a newline.

Implement the steps by creating a java class containing annotated methods:

```java
public class HelloWorldSteps {
    private String name;
    private String output;

    @Given("^the user name is '([^']+)'$")
    public void userNameIs(String name) {
        this.name = name;
    }

    @When("^the user clicks the hello button$")
    public void clickTheButton() {
        this.output = "Hello " + name;
    }

    @Then("^the output is '([^']+)'")
    public void outputIs(String expectedOutput) {
        Assert.assertEquals("Output should be", expectedOutput, output);
    }
}
```

Implement a Junit runner for executing the features. The annotation parameters `featurePackages` and `stepDefinitionPackages` specify in which packages to search for features and step definitions respectively.

```java
@RunWith(Zuchini.class)
@ZuchiniOptions(
    featurePackages = {"features/helloworld"},
    stepDefinitionPackages = {"org.zuchini.junit.helloworld"})
public class HelloWorldTest {
}
```

## Reporting and test listeners

By default zuchini will report execution of scenarios without details about the individual steps. To enable reporting per step, set the `reportIndividualSteps` property in `ZuchiniOptions` to `true`.

Zuchini allows to add additional `RunListener`'s to monitor test execution, using the `listeners` property of `ZuchiniOptions`. One such `RunListener` for generating test reports in json format is included in the `zuchini-reporting` artifact. 

```xml
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-reporter</artifactId>
    <version>${version.zuchini}</version>
    <scope>test</scope>
</dependency>
```

```java
@RunWith(Zuchini.class)
@ZuchiniOptions(
    featurePackages = {"features/helloworld"},
    stepDefinitionPackages = {"org.zuchini.junit.helloworld"},
    listeners = {JsonReporter.class})
public class HelloWorldTest {
}
```

The report will be written to `zuchini-report.json` in a [format described by this json schema](zuchini-reporter/schema.json).

**Note**: The report format differs from the format used by cucumber-jvm and therefore cannot be interpreted by the usual report generation plugins.

The output file name can be overriden using a system property `zuchini.reporter.output`, for example using maven and the surefire plugin:

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <zuchini.reporter.output>${project.build.directory}/report.json</zuchini.reporter.output>
        </systemPropertyVariables>
    </cofiguration>
</plugin>
```

## Usage with spring framework

To use autowiring of spring beans in your step definition classes you need to use the `SpringZuchini` runner.

```xml
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-spring</artifactId>
    <version>${version.zuchini}</version>
    <scope>test</scope>
</dependency>
```

The spring configuration has to be specified on the junit test class, same as it would be for normal junit and spring tests.

```java
@RunWith(SpringZuchini.class)
@ZuchiniOptions(
    featurePackages = {"features/hellospring"},
    stepDefinitionPackages = "org.zuchini.spring.helloworld")
@ContextConfiguration(classes = {HelloSpringConfiguration.class, ScenarioScopeConfiguration.class})
public class CukesSpringTest {
}
```

The `ScenarioScopeConfiguration` defines a spring scope per executed scenario that you can use on your step definition classes, so that a new instance is instantiated by spring for each scenario:

```java
@Component
@ScenarioScoped
public class HelloSpringSteps {
}
```

**Note:** If you use spring-boot with `@EnableAutoConfiguration` you no longer have to add the `ScenarioScopeConfiguration` explicitly and the scenario scope is available without further configuration.

For an example for how to test REST endpoints in a spring-boot project using `MockMvc` check out the [`zuchini-examples-mockmvc` project](zuchini-examples-mockmvc/).

```java
@RunWith(SpringZuchini.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ZuchiniOptions(
    featurePackages = "features/mockmvc",
    stepDefinitionPackages = "org.zuchini.examples.mockmvc"
)
public class HelloSpringTest {
}
```

## IntelliJ integration

To enable running individual scenarios directly from the feature files in IntelliJ you have to add the `zuchini-intellij-support` dependency:

```xml
<dependency>
    <groupId>org.zuchini</groupId>
    <artifactId>zuchini-intellij-support</artifactId>
    <version>${version.zuchini}</version>
    <scope>test</scope>
</dependency>
```

This makes it look to IntelliJ like the original cucumber-jvm implementation is on the classpath and enable the cucumber plugin.

IntelliJ currently does not directly support the zuchini annotations and so will highlight all steps in the feature files as "not implemented". To avoid this you have to use the original cucumber annotations on your step definition methods. A copy of those annotations is included in the `zuchini-compat-annotations` artifact, which is a transitive dependency of the above `zuchini-intellij-support`. 