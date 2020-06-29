# Installation

1. [Download latest plugin version](https://github.com/adevone/summer-plugin/releases/download/0.17.1/summer-plugin-0.17.1.zip)
2. Open Intellij plugins page
3. Click to gear icon
4. Select "Install Plugin from disk..."
5. Select downloaded `.zip` file
6. Restart you IDE

# Intentions

##### Implement missing properties
Click `alt+Enter` when caret on `object` that implements `viewProxy` and select `Store missing properties`
```
override val viewProxy = >>>object<<< : FeatureView {
    override var banner by state({ it::banner }, initial = null)
}
```

##### State property
Click `alt+Enter` when caret on property name in `View` interface and select `state`
```
interface FeatureView {
    var >>>banner<<<: Banner?
}
```

##### Event
Click `alt+Enter` when caret on property name in `View` interface and select `doExactlyOnce` or `doOnlyWhenAttached`
```
interface FeatureView {
    val >>>doSomething<<<: () -> Unit
}
```

##### Copy event implementation
Click `alt+Enter` when caret on property name in `View` interface and select `Copy iOS implementation` or `Copy Android implementation`
```
interface FeatureView {
    val >>>doSomething<<<: () -> Unit
}
```

