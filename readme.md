# Turn Your Code into a Code to Exercise
According to psychology study, people just can remember 30% of what they hear but 60% of what they do. When I give a training to trainees, I will give them an exercise project that contains **incomplete** codes and request them to complete the code during the training. For example, this is my original code:

```java
public class HelloVM {

    public void hello(){
        hello = "hello world";
    }
}
```

Then I will create an exercise file by removing several lines, and trainees have to implement those missing code as an exercise. The exercise file looks like:

```java
public class HelloVM {
    //TODO, write a hello method to initialize "hello"
}
	
```

If I have to create these exercise files by manually removing lines, it's a burden to maintain the exercise files when the original example code changes in the future. So I need a tool to automatically produce the exercise files based on the original file. This plugin helps me do it.


# Exercise Maker Introduction
A maven plugin that **produces the corresponding exercise files based on the source project**. It removes some lines according to specific marks written in the comments to produce the exercise files. So that when we modify an example file slightly in the future, we don't have to create the corresponding exercise file again manually.

![](images/concept.png)

## Exercise mark
With this plugin, I can leave a special format comment (starting with `TODO`) like:

```java
public class HelloVM {

	private String hello = "";

	//TODO, 3, write a hello method
	public void hello(){
		hello = "hello world";
	}
}
```

Then, this plugin will remove the next 3 lines after the exercise mark which is just the method `hello()`. So the produced file is:

```java
public class HelloVM {

	private String hello = "";

	//TODO, 3, write a hello method
}
```

Then this source becomes a file for exercise. Training attendees can implement the missing code by themselves.





# Feature
* Put **exercise mark** inside a comment line of a source code (including  zul, java, css, javascript) to produce the corresponding file for the exercise by removing the specified lines below the **exercise mark**, e.g.

`<!-- TODO, 2-5, hint -->`

The plugin removes the lines starting from the 2nd to the 5th lines (included) below TODO.
* If a file doesn't contain any exercise mark, do not produce any exercise file.


# Exercise Mark Syntax
a valid exercise mark should at least contains 3 parts:
`TODO, [EXERCISE RANGE], [HINT]`

## 1. TODO
The keyword that indicates the exercise mark, must be uppercase

## 2. `[EXERCISE RANGE]`:
Specifies one of the following:
1. one number: number of lines to delete e.g. `13`
2. a range: e.g. `1-3`.
* line number starts from **1**

## 3. `[HINT]` 
* a hint is left for trainees to read when doing exercises
* it can contain a comma, the plugin just checks the first 2 parts


the plugin ignores invalid marks

# Usage
Put exercise mark inside a comment line to specify a block to be removed for exercises

## zul
`<!-- TODO, 5, add a button -->`

## java
`// TODO, 8, implement a feature`


# Plugin Usage
Check /src/test/pom.xml

* setup the repository
```xml
    <repository>
        <id>ZK CE</id>
        <name>ZK CE Repository</name>
        <url>https://mavensync.zkoss.org/maven2</url>
    </repository>
```

* configure the plugin
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.zkoss.training</groupId>
            <artifactId>exercise-maker</artifactId>
            <version>1.0.0</version>
            <!-- optional, here are default paths:
            <configuration>
                <sourceDirectory>src/main/</sourceDirectory>
                <outputDirectory>src/exercise/</outputDirectory>
            </configuration>
            -->
        </plugin>
    </plugins>
</build>
```
* The default goal is `make`
* The default phase is `process-sources` 

# Run Test Cases
Install exercise-maker into the local repository before running test cases.


# Reference
[Plugin Developers Centre](https://maven.apache.org/plugin-developers/index.html) 

# Publish
[jenkins2/Maven_update/PBFUM/](http://jenkins2/view/Maven_update/job/PBFUM/)


# Limitation
* can't remove the line before exercise mark
* can't remove non-continuous lines.
For example:
```xml
<!--
<button width=30%/>
-->
```
It can't remove the 1st and 3rd line.