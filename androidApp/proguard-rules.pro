# Crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Enums / sealed classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Room generated implementations
-keep class **_Impl { *; }

# Kotlinx serialization
-keepclassmembers class ** {
    *** Companion;
}
-keepclassmembers class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

# Parcelable (just in case)
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}