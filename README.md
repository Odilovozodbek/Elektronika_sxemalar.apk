# Elektronika sxemalar — Android APK

ElectroTools Studio 5.2 elektronika o‘quv laboratoriyasining Android versiyasi. Ilova HTML/CSS/JavaScript fayllarini qurilmaning ichida ochadi va ishlashi uchun internet talab qilmaydi.

## Ilova imkoniyatlari

- Sxema yig‘ish, sim ulash va oddiy simulyatsiya
- LED, kuchlanish bo‘luvchi, mantiqiy elementlar va boshqa tajribalar
- Elektronika kalkulyatorlari, formulalar va ossillograf
- O‘zbekcha, inglizcha va ruscha interfeys
- Laboratoriya jurnalini qurilmada saqlash

Bu o‘quv simulyatori. Hisob-kitoblar soddalashtirilgan va haqiqiy elektr montaji uchun ko‘rsatma emas.

## APK yuklab olish

Eng so‘nggi APK: [Elektronika_sxemalar.apk](https://github.com/Odilovozodbek/Elektronika_sxemalar.apk/releases/latest/download/Elektronika_sxemalar.apk)

## Androidda o‘rnatish

1. APK faylini yuklab oling.
2. Faylni ochib o‘rnating. Telefon noma’lum manbadan o‘rnatishga ruxsat so‘rasa, shu fayl menejeri yoki brauzer uchun ruxsat bering.
3. Ilovani oching. Ilova ichidagi saqlangan sxemalar va jurnal telefonda qoladi.

## Build

GitHub Actions main branchga yangi o‘zgarish tushganda debug APKni yig‘adi, Actions artifact sifatida saqlaydi va v1.0.0 Release’ga yuklaydi.

Loyihani Android Studio/Gradle bilan ham yig‘ish mumkin:

    gradle :app:assembleDebug

Natija: app/build/outputs/apk/debug/app-debug.apk.
