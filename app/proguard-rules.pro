# Keep Rhino JavaScript engine classes required by NewPipeExtractor for YouTube signature deciphering
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn org.mozilla.javascript.tools.**
-dontwarn org.schabi.newpipe.extractor.**
