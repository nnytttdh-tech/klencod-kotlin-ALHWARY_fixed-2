# ALHWARY

تطبيق مراسلة Android مبني بـ Kotlin + Jetpack Compose.

## التعديلات
- إزالة المستخدمين الوهميين والبيانات التجريبية من المحادثات والحالات والمكالمات.
- تفعيل Username محلي قابل للتعديل والحفظ.
- عرض `@username` بدل Gmail في واجهات المستخدم وجهات الاتصال.
- تغيير الاسم العربي من «الحواري» إلى «الهواري».
- إصلاح GitHub Actions: المشروع Android/Gradle وليس Flutter؛ لذلك تم استبدال `flutter pub get` ببناء Gradle.

## البناء
استخدم GitHub Actions أو Android Studio ثم شغّل:

```bash
gradle :app:assembleDebug
```
