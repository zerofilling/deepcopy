# DeepCopy Utility

A robust Java utility for creating deep copies (clones) of complex Java objects.

## Overview

This library provides a powerful and flexible mechanism to create deep copies of Java objects. Unlike shallow copying, which only duplicates references, deep copying creates completely independent copies of the entire object graph, handling nested objects appropriately.

## Features

- Creates complete deep copies of Java objects with a single method call
- Handles complex object graphs with circular references
- Supports arrays, collections, maps, and custom objects
- Properly manages immutable objects (Strings, primitives, wrapper classes)
- Works with objects that don't have default constructors using Unsafe
- Preserves object relationships in the copied object graph

## Installation

### Maven

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>deepcopy</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle (Groovy)

```groovy
implementation 'org.example:deepcopy:1.0-SNAPSHOT'
```

### Gradle (Kotlin)

```kotlin
implementation("org.example:deepcopy:1.0-SNAPSHOT")
```

## Usage

```java
// Import the utility
import com.lightspeedhq.util.CopyUtils;

// Create a deep copy of any object
try {
    MyComplexObject original = new MyComplexObject();
    // ... populate original
    
    MyComplexObject copy = CopyUtils.deepCopy(original);
    
    // copy is now a completely independent clone of original
} catch (Exception e) {
    // Handle exception
}
```

## How It Works

The utility uses reflection to:

1. Create new instances of objects (using constructors or Unsafe)
2. Copy all field values recursively
3. Track already-copied objects to handle circular references
4. Special handling for arrays, collections and maps

### Special Cases

- **Immutable Objects**: Objects like Strings, Integer, etc. are not copied but shared
- **Arrays**: Elements are copied into a new array of the same type and size
- **Collections**: A new collection is created and populated with deep copies of the elements
- **Maps**: A new map is created with deep copies of both keys and values
- **Objects without Constructors**: Uses the Unsafe API to instantiate objects

## Requirements

- Java 21 or compatible JDK

## Building and Deploying

This project uses Gradle as its build tool.

### Building the Project

```shell
# Clone the repository
git clone [repository-url]

# Build the project
./gradlew build
```

### Publishing to Maven Local

To publish the library to your local Maven repository for testing or local development:

```shell
./gradlew publishToMavenLocal
```

This will make the library available for local development with the coordinates `org.example:deepcopy:1.0-SNAPSHOT`.

After publishing, you can include the library in other local projects by adding `mavenLocal()` to your repositories in build.gradle:

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}
```

### Running Tests

```shell
./gradlew test
```

## License

MIT License (or specify your preferred license)

## Contributing

Contributions are welcome. Please feel free to submit a Pull Request.

---

Developed by Your Organization