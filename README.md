# ScrollTrack – TikTok Video Kaydırma Sayacı

TikTok'ta bir günde kaç video kaydırdığını sayan, tamamen cihazda çalışan Android uygulaması.

- Koyu tema, büyük sayaç, günlük hedef + ilerleme çubuğu
- İstatistik ekranı (bugün, dün, son 7 gün grafiği, haftalık toplam, günlük ortalama, en yoğun gün)
- İlk açılışta tanıtım ve izin rehberi
- **İnternet izni yok.** Hiçbir veri gönderilmez. Veriler yalnızca telefonda durur.
- Ekran içeriği, mesajlar, kullanıcı adları okunmaz (`canRetrieveWindowContent=false`).

---

## APK'yı bilgisayara hiçbir şey kurmadan alma (yalnızca tarayıcı)

Bu klasördeki projeyi **GitHub'ın sunucuları** derler. Sen sadece tarayıcıyı kullanırsın.
Terminal / CMD / Android Studio gerekmez. Ücretsiz bir GitHub hesabı yeterlidir.

### 1) ZIP'i aç
ZIP dosyasına sağ tıkla → **Tümünü ayıkla** (Mac: çift tıkla). Ortaya `ScrollTrack` klasörü çıkar.

### 2) GitHub hesabı aç
https://github.com adresinde ücretsiz hesap oluştur (varsa giriş yap).

### 3) Yeni depo (repository) oluştur
- Sağ üstte **+** → **New repository**
- Repository name: `ScrollTrack`
- **Public** veya **Private** seç (ikisi de çalışır)
- **Create repository** de

### 4) Dosyaları yükle
- Açılan sayfada **uploading an existing file** bağlantısına tıkla
- `ScrollTrack` klasörünün **İÇİNDEKİ her şeyi** (klasörün kendisini değil) tarayıcı penceresine sürükle-bırak:
  `app`, `.github`, `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `README.md`, ...
- Yükleme bitince en alttaki **Commit changes** düğmesine bas

> **Gizli klasör sorunu:** `.github` klasörü bazı bilgisayarlarda gizli olabilir (özellikle Mac).
> Yüklemeden sonra deponun ana sayfasında `.github` klasörünü görmüyorsan:
> 1. **Add file → Create new file** de.
> 2. Dosya adı kutusuna aynen şunu yaz: `.github/workflows/build.yml`
>    (`/` yazdıkça klasörler otomatik oluşur)
> 3. Projedeki `build-workflow.yml` dosyasının **tüm içeriğini** kopyalayıp büyük kutuya yapıştır.
> 4. **Commit changes** de.

### 5) Derlemeyi başlat / bekle
- Deponun üstündeki **Actions** sekmesine gir.
- (İlk kez soruyorsa **I understand my workflows, enable them** de.)
- **APK Olustur** adlı iş otomatik başlar. Başlamadıysa soldan **APK Olustur** → **Run workflow** → yeşil **Run workflow**.
- Yaklaşık 4–8 dakika sürer. Yeşil tik ✅ çıkınca bitmiştir.

### 6) APK'yı indir
- Biten işe (yeşil tikli satıra) tıkla.
- Sayfanın en altında **Artifacts** bölümünde **ScrollTrack-debug-apk** görünür. Ona tıkla, ZIP iner.
- ZIP'i aç: içinden **app-debug.apk** çıkar. (Derleme yolu: `app/build/outputs/apk/debug/app-debug.apk`)

Kırmızı ❌ çıkarsa: işe tıkla, hata satırının ekran görüntüsünü bana gönder, düzelteyim.

---

## Telefona kurma

1. `app-debug.apk` dosyasını telefona aktar (USB, Drive, e-posta, WhatsApp "kendime" vb.).
2. Telefonda dosyaya dokun. "Bilinmeyen kaynaklardan yükleme" izni isterse, açılan ayarda **izin ver** de.
3. **Yükle** → **Aç**.
4. Play Protect "tanınmayan uygulama" uyarısı verirse **Yine de yükle** de. (Uygulama Play Store'da değil, kendi derlediğin debug APK'dır.)

## Erişilebilirlik iznini açma

Uygulama ilk açılışta seni adım adım yönlendirir. Kısaca:

**Ayarlar → Erişilebilirlik → Yüklü uygulamalar (İndirilen uygulamalar) → ScrollTrack → AÇ → İzin ver**

**Android 13 ve üstünde anahtar gri ise** (APK ile kurulan uygulamalarda normaldir):
**Ayarlar → Uygulamalar → ScrollTrack → sağ üst ⋮ → Kısıtlanmış ayarlara izin ver**, sonra erişilebilirlik iznini tekrar aç.

İzin açılınca uygulamaya dönersen otomatik olarak sayaç ekranı açılır.

---

## Sayım nasıl çalışır? (Önemli)

Android, TikTok'un "yeni video başladı" bilgisini uygulamalara vermez. ScrollTrack, TikTok içinden
gelen **kaydırma sinyallerini** dinler. Bir kaydırma onlarca sinyal üretir; bu yüzden:

- Sinyaller **450 ms** boyunca kesilirse hareket bitmiş sayılır ve sayaç **bir kez** artar.
- Sinyaller hiç kesilmese bile bir hareket en fazla 1,5 sn sayılır.
- Yalnızca `com.zhiliaoapp.musically` ve `com.ss.android.ugc.trill` paketleri dinlenir; TikTok açık değilken sayaç artmaz.

**Sonuç yaklaşıktır.** Olası sapmalar:
- Yorumlar, profil ya da arama listesinde kaydırırsan o hareketler de sayılabilir.
- Çok hızlı (yarım saniyeden kısa aralıkla) kaydırırsan iki video tek sayılabilir.
- Videoyu yarıda bırakıp geri bıraktığında hareket sayılmış olabilir.

Hassasiyeti ayarlamak istersen `ScrollAccessibilityService.kt` içindeki `QUIET_MS` değerini değiştir
(küçültürsen daha hızlı kaydırmaları ayırır ama çift sayım riski artar).

## Gerçek cihazda test edilmesi gerekenler

Bu özellikler emülatörde değil, TikTok yüklü gerçek telefonda denenmelidir:
1. Erişilebilirlik izni açıldıktan sonra TikTok'ta 10 video kaydır, sayacı karşılaştır.
2. TikTok kapalıyken / başka uygulamadayken sayaç artmıyor mu?
3. Uygulamayı kapatıp açınca veri duruyor mu?
4. Gece yarısından sonra "Bugün" 0'dan başlıyor, "Dün" doğru mu?
5. Erişilebilirlik iznini kapatınca ana ekranda sarı uyarı çıkıyor mu?

## Bilinen sınırlar

- Servis durdurulduğunda uygulama **bildirim göndermez**; uyarı, uygulamayı bir sonraki açışında ana ekranda görünür.
- Bazı üreticiler (Xiaomi, Huawei, Samsung vb.) pil tasarrufu için erişilebilirlik hizmetlerini kapatabilir.
  Gerekirse: Ayarlar → Pil → ScrollTrack → "Kısıtlama yok".
- TikTok Lite gibi farklı paket adlı sürümler dinlenmez (istenirse `ScrollAccessibilityService.kt` ve `accessibility_service_config.xml` içine eklenir).

## Teknik özet

| Öğe | Değer |
|---|---|
| Dil | Kotlin, XML arayüz (View sistemi) |
| Java | 17 |
| Android Gradle Plugin | 8.5.2 |
| Gradle | 8.7 (GitHub Actions kurar) |
| Kotlin | 1.9.24 |
| compileSdk / targetSdk | 34 |
| minSdk | 26 (Android 8.0) |
| Veri | SharedPreferences, cihazda |
