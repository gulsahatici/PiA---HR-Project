# 🎯 TalentPath ATS - Aday Takip Sistemi

Bu proje, insan kaynakları (İK) ekiplerinin aday başvuru süreçlerini daha verimli yönetmelerini sağlayan bir **Aday Takip Sistemi (ATS)**’dir. Sistem, aday başvurularını listeler, filtreler, sıralar ve adaylara işe alım süreciyle ilgili **karar e-postaları** gönderilmesine olanak tanır.

Proje; Java 24, Spring Boot, PostgreSQL ve Jakarta Mail kullanılarak geliştirilmiştir.

- 👥 **Takım Projesi:** Bu projede ana görevim backend development rolüydü. Ancak süreç içerisinde analiz ve test süreçlerinde de yer aldım.
---

## 🔧 Özellikler

- 📋 **Aday Başvuru Formu:** Aday bilgileri (isim, mail, şehir, bölüm vb.) ve özgeçmiş (CV) yüklenerek başvuru yapılır.
- 🔎 **Filtreleme ve Sıralama:** Dashboard üzerinde;
  - Gelişmiş **AND/OR filtreleme** sistemi ile İK havuzunun genişletilmesi
- 📬 **Mail Gönderimi:**
  - Kabul veya red durumlarında otomatik e-posta gönderimi
  - Gmail SMTP üzerinden yapılandırıldı
- 📄 **CV İndirme:** Adaylara ait yüklenen PDF dosyaları sistem üzerinden indirilebilir


---

## 🖥️ Kullanılan Teknolojiler

| Teknoloji       | Açıklama                           |
|----------------|------------------------------------|
| Java 24        | Proje dili                         |
| Spring Boot    | Backend framework                  |
| PostgreSQL     | Veritabanı                         |
| Jakarta Mail   | SMTP ile e-posta gönderimi         |
| REST API       | Frontend ile entegrasyon           |
| Postman        | API testleri                       |
| Gradle         | Proje yönetimi ve bağımlılıklar |

---

## 🔗 Örnek API Kullanımı

### ✅ POST `/api/email/decision`

```json
{
  "candidateEmail": "aday@example.com",
  "decision": "accept"
}

