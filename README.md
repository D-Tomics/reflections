# Reflections
  This repo abstracts basic java reflection functionality. This project is created with extensibility in mind so that any
one who wishes for extending the functionality can do so.

## Prerequisite
In order to use this you would need the following
  - java 8 or above
  - maven 3.6.3 or above

## Installation
- first clone this repository
    ```
    git clone https://github.com/D-Tomics/reflections.git
    ```
- extract the zip file to a folder
- browse to that directory via command prompt using 
    ```cd <folder location>```
- `mvn clean install`
- then add the following to your pom.xml under `<dependenices>` tag

    ```
    <groupId>com.dtomics.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>1.0</version>
    ```
## use case
- for every use case the api needs to be initialized the following way.
  ```
  Reflection reflection = new Reflection("com.example.package", classLoader);
  reflection.addReader(new ClassFileReader());
  reflection.addScanner(new AnnotatedMethodScanner());
  reflection.scan();
  ```
  During this custom scanners that implements the `com.dtomics.reflections.scanners.Scanner` interface and
  custom file readers that implements the `com.dtomics.reflections.classReaders.ClassReader` interface can be added.

- The supported readers are `ClassFileReader` and `JarFileReader`.

- The supported scanners are `AnnotatedClassScanner`, `AnnotatedExecutableScanner`,`AnnotatedFieldScanner`,`SubClassScanner`

- The elements can then be accessed via `reflection.getTypesAnnotatedWith()` and similar methods for corresponding
 elements
