# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.sun.msv.reader.GrammarReaderController**
-dontwarn com.sun.msv.reader.util.IgnoreController**
-dontwarn javax.xml.stream.XMLEventFactory**
-dontwarn javax.xml.stream.XMLInputFactory**
-dontwarn javax.xml.stream.XMLOutputFactory**
-dontwarn javax.xml.stream.XMLResolver**
-dontwarn javax.xml.stream.util.XMLEventAllocator**
-dontwarn org.conscrypt.Conscrypt$Version**
-dontwarn org.conscrypt.Conscrypt**
-dontwarn org.conscrypt.ConscryptHostnameVerifier**
-dontwarn org.openjsse.javax.net.ssl.SSLParameters**
-dontwarn org.openjsse.javax.net.ssl.SSLSocket**
-dontwarn org.openjsse.net.ssl.OpenJSSE**
-dontwarn com.yalantis.ucrop**
-keep class com.google.gson.reflect.TypeToken**
-keep class * extends com.google.gson.reflect.TypeToken**
-keep public class * implements java.lang.reflect.Type
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }


