[Summer framework](https://github.com/adevone/summer) plugin. [Plugin page](https://plugins.jetbrains.com/plugin/15616-summer-framework)  
Adds convenience intentions to generate `viewProxy` and events platform implementations.

# Intentions

##### Implement missing properties
Click `alt+Enter` when the caret on `object` that implements `viewProxy` and select `Store missing properties`
```kotlin
override val viewProxy: FeatureView = >>>object<<< : FeatureView {
    override var banner by state({ it::banner }, initial = null)
}
```

##### State property
Click `alt+Enter` when the caret on property name in `View` interface and select `state`
```kotlin
interface FeatureView {
    var >>>banner<<<: Banner?
}
```

##### Event
Click `alt+Enter` when caret on property name in `View` interface and select `doExactlyOnce` or `doOnlyWhenAttached`
```kotlin
interface FeatureView {
    val >>>doSomething<<<: () -> Unit
}
```

##### Copy event implementation
Click `alt+Enter` when caret on property name in `View` interface and select `Copy iOS implementation` or `Copy Android implementation`
```kotlin
interface FeatureView {
    val >>>doSomething<<<: () -> Unit
}
```

# Manual installation

1. [Download the latest plugin version](https://github.com/adevone/summer-plugin/releases)
2. Open Intellij plugins page
3. Click to gear icon
4. Select "Install Plugin from disk..."
5. Select downloaded `.zip` file
6. Restart your IDE
