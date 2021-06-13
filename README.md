# Monet

Google's Monet color system (Android 12 Beta 2) library.

## Usage

### Jetpack Compose

1. Generate Monet color palette

```kotlin
monetColorsOf(Color, darkTheme)
```

2. LocalMonetColors

```kotlin
ProvideMonetColors(Color, darkTheme) {
    // content
}
```
