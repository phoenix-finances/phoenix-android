# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/de76/dev/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# https://stackoverflow.com/questions/13670223/compile-with-android-proguard-error-cant-find-reference
-dontwarn butterknife.internal.**
-dontwarn com.**

-keep class android.** {*;}
-keep class com.** {*;}
-keep class org.greenrobot.** {*;}
-keep public class com.ornoma.fianance46.NewTransactionActivity {
    void handle*(***);
}

#-keepattributes *Subscribe*
#
#-keep class android.support.v4.app.** { *; }

#
#-keep interface android.support.v4.app.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
