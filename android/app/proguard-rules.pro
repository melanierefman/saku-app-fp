# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\melan\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Keep Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Gson / Models / DTOs
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.example.saku.app.core.network.** { *; }
-keep class com.example.saku.app.core.data.** { *; }
-keep class com.example.saku.app.features.**.data.** { *; }
-keep class com.example.saku.app.features.**.dto.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Koin
-keep class org.koin.** { *; }

# Keep Coroutines
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
