# Releases #
The releases are published to maven central. Add following to the dependencies section of your pom.xml in order to start using Komarro.

```
  <dependency>
    <groupId>com.googlecode.komarro</groupId>
    <artifactId>komarro</artifactId>
    <version>1.0</version>
    <scope>test</scope>
  </dependency>
```

# Snapshots #

Mockarro snapshots get deployed to the sonatype snapshot repository.

```
    <repositories>
        <repository>
            <id>sonatype.oss.snapshots</id>
            <name>Sonatype OSS Snapshot Repository</name>
            <url>http://oss.sonatype.org/content/repositories/snapshots</url>
            <releases>
              <enabled>false</enabled>
            </releases>
            <snapshots>
              <enabled>true</enabled>
            </snapshots>
        </repository> 
    </repositories>
```

Once the repository is added to your pom.xml's repository section, add following dependency to the dependecies section.
```
<dependency>
   <groupId>com.googlecode.mockarro</groupId>
   <artifactId>mockarro-core</artifactId>
   <version>1.0.0-SNAPSHOT</version>
   <scope>test</scope>
</dependency>
```