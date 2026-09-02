# VoiceCoverApp - Build APK sin Android Studio

## Pasos para tener tu APK instalable en 2 minutos:

1. Crea un repo nuevo en GitHub (ej: VoiceCoverApp)
2. Sube TODO el contenido de esta carpeta al repo:
   ```
   git init
   git add .
   git commit -m "Initial VoiceCover"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/VoiceCoverApp.git
   git push -u origin main
   ```
3. Ve a la pestaña Actions en GitHub -> verás que se está ejecutando "Build APK - VoiceCover"
4. Espera 3-4 minutos. Cuando termine:
   - Entra en el run > baja hasta abajo > descarga el artifact "VoiceCover-debug-apk"
   - O ve a Releases y descarga el app-debug.apk

5. Pasa ese APK a tu móvil por WhatsApp, Drive o cable y ábrelo. Acepta "Instalar de origen desconocido".

## Qué hace este workflow
- Instala Java 17 + Android SDK
- Compila con ./gradlew assembleDebug
- Te deja el APK listo en Artifacts y en Releases

No necesitas tener Android Studio instalado en tu PC.
