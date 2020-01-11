# Installation

1. [Download latest plugin version](https://github.com/adevone/summer-plugin/releases/download/0.8.12/summer-plugin-0.8.12.zip)
2. Open Intellij plugins page
3. Click to gear icon
4. Select "Install Plugin from disk..."
5. Select downloaded `.zip` file
6. Restart you IDE

# Intentions

##### Store missing properties
Click `alt+Enter` when caret on `object` that implements `viewStateProxy` and select `Store missing properties`
```
override val viewStateProxy = >>>object<<< : Feature.State {
    override var banner by store({ it::banner }, initial = null)
}
```

##### Store property
Click `alt+Enter` when caret on property name in `State` interface and select `Store property`
```
object Feature {
    interface State {
        var >>>banner<<<: Banner?
    }
}
```
