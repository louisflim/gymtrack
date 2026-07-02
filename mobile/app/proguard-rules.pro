# Retrofit / Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class edu.cit.lim.gymtrack.mobile.data.model.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
