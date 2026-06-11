# Heroes Arena ProGuard Rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.heroesarena.models.** { *; }
-keep class com.heroesarena.engine.** { *; }
-dontwarn javax.annotation.**
-dontwarn okhttp3.**
-dontwarn okio.**
