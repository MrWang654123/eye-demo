##---------------配置章节结构 ----------
#---------------基础混淆配置 -----------
#---------------主工程混淆配置 -----------
#---------------子工程混淆配置 -----------
#---------------第三方jar混淆配置 -----------


##---------------Begin: 基础混淆配置 ----------
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames#包明不混合大小写
-dontskipnonpubliclibraryclasses#不去忽略非公共的库类
-verbose#混淆时记录日志
-dump class_files.txt #apk 包内所有 class 的内部结构
-printseeds seeds.txt #未混淆的类和成员
-printusage unused.txt #列出从 apk 中删除的代码
-printmapping mapping.txt #原始代码与混淆的代码映射

-dontoptimize#不优化输入的类文件
-dontpreverify#不预校验
-ignorewarnings


-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
#以上默认配置来自${sdk.dir}/tools/proguard/proguard-android.txt#


#-optimizationpasses 5 #指定代码的压缩级别
#-repackageclasses ''
#-allowaccessmodification


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepattributes Signature
#May be the whole magic line would work, i.e.:
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

#-keep class * extends java.lang.annotation.Annotation { *; }
#-keep class **.R$* {*;}

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Dialog
-keep public class * extends android.view
# support design
-dontwarn android.support.**
-dontwarn android.support.design.**
-keep class android.support.** { *; }
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
##---------------End: 基础混淆配置 ------------


##---------------Begin: 主工程混淆配置 ----------
#aidl.jar
-keep class android.content.pm.** { *; }
-keep public class * extends org.litepal.crud.DataSupport {*;}
-keep public class * extends com.tm.me.dao.base.BaseDataSupport {*;}
-keep class * implements java.io.Serializable  { *; }

-keep public class * {
    public protected *;
}

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep class com.cheersmind.smartapp.main.ioc** { *; }
-keep class com.cheersmind.smartapp.main.util** { *; }
##---------------End: 主工程混淆配置 ------------

##---------------Begin: 第三方jar混淆配置 ----------
#不混淆第三方jar包

#jackson-annotations-2.3.0.jar
#jackson-core-2.3.2.jar
#jackson-databind-2.3.2.jar
-dontwarn com.fasterxml.jackson.**
-keep class  com.fasterxml.jackson.** { *;}

#MiniLibrary litepal-1.2.0.jar
-dontwarn org.litepal.**
-keep class org.litepal.** { *;}

# MiniLibrary gson-2.3.1.jar
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}
##---------------End: 第三方jar混淆配置 ------------