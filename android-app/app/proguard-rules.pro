# kotlinx.serialization keeps its generated serializer classes reachable via reflection.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclasseswithmembers class uk.co.andymarch.blogposter.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class uk.co.andymarch.blogposter.**$$serializer { *; }

# OkHttp/Retrofit platform reflection.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
