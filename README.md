# VolumeControlView
We’re happy to introduce you a new free regulator VolumeControlView based on our lightweight open-source visual component that doesn't require extra lines of code and can be easily integrated into your project. Visual regulator can be connected to a player or other smart house’s device making the process of controlling the level of a particular characteristic much easier.

### Demo

<img src="https://user-images.githubusercontent.com/17047537/26981465-ef1759ae-4d3d-11e7-85bd-b04338761719.gif"> <img src="https://user-images.githubusercontent.com/17047537/26981461-edd0ee7a-4d3d-11e7-8b79-5f9ce4a80552.gif"> 

## Example
To run the example project, clone the repo, and run sample.
### How to use

Just add VolumeControlView to your layout file:
```kotlin
 <com.agilie.volumecontrol.view.VolumeControlView
        android:id="@+id/controllerView"
        android:layout_width="wrap_content"
        android:layout_height="270dp">
````

The visual display of this regulator can be easily customized. One has a possibility to choose colors, the gradient style and background according to the wishes:
```kotlin
controllerView.colors = intArrayOf()
````
````xml
<declare-styleable name="VolumeControlView">
        <attr name="innerCircleColor" format="color" />
        <attr name="movableCircleColor" format="color" />
        <attr name="splineCircleColor" format="color" />
        <attr name="controllerSpace" format="float" />
        <attr name="sectorRadius" format="integer" />
        <attr name="movableCircleRadius" format="float" />
        <attr name="minShiningRadius" format="float" />
        <attr name="maxShiningRadius" format="float" />
        <attr name="shiningFrequency" format="float" />
</declare-styleable>
````
## Usage

### Gradle

Add dependency in your `build.gradle` file:
````gradle

````

### Maven
Add  dependency in your `.pom` file:
````xml
<dependency>
  <groupId>com.agilie</groupId>
  <artifactId>volume-control-view</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
````

## Requirements

VolumeControlView works on Android API 19+


## Author

This library is open-sourced by [Agilie Team](https://www.agilie.com) <info@agilie.com>

## Contributors

- [Eugene Surkov](https://github.com/ukevgen)

## Contact us
<android@agilie.com>


## License

The [MIT](LICENSE.md) License (MIT) Copyright © 2017 [Agilie Team](https://www.agilie.com)