# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ronak\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontobfuscate
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**

#-keep class com.kosalgeek.** { *; }
#-dontwarn com.kosalgeek.**
#
#-keep class com.squareup.** { *; }
#-dontwarn com.squareup.**
#
#-keep class org.codehaus.** { *; }
#-dontwarn org.codehaus.**
#
#-keep class java.nio.** { *; }
#-dontwarn java.nio.**

-keep class **$$ViewBinder { *; }
#
#-keep class com.kosalgeek.** { *; }
#-dontwarn com.kosalgeek.**
#
#-keep class com.squareup.** { *; }
#-dontwarn com.squareup.**
#
#-keep class org.codehaus.** { *; }
#-dontwarn org.codehaus.**
#
#-keep class java.nio.** { *; }
#-dontwarn java.nio.**

#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
##    @com.kosalgeek.* <fields>;
##    @com.squareup.* <fields>;
##    @org.codehaus.* <fields>;
##    @java.nio.* <fields>;
#
#}
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
##    @com.kosalgeek.* <methods>;
##    @com.squareup.* <methods>;
##    @org.codehaus.* <methods>;
##    @java.nio.* <methods>;
#}
#-keepclassmembers class * implements android.os.Parcelable {
#    static ** CREATOR;
#}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
    @com.kosalgeek.* <methods>;
    @com.squareup.* <methods>;
    @org.codehaus.* <methods>;
    @java.nio.* <methods>;
}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

-keep class com.kosalgeek.android.photoutil.** { *; }
-dontwarn com.kosalgeek.android.**
-keep class com.kosalgeek.genasync12.** { *; }
-dontwarn com.kosalgeek.genasync12.**