# 🔍 **SEO & Online Presence – Full Analysis Checklist (v2)**

*Aligned with `SeoAnalysisService` logic*

This checklist represents the exact validation rules applied by the backend during a website analysis. It is also the human-readable reference to understand how the SEO coaching system works.

---

# ✅ **1. Title (`<title>`)**

**How it’s extracted:**
`doc.title()`

**Checks performed:**

* ❌ Missing title → **Critical**
* ⚠️ Too short (< 45 chars)
* ⚠️ Too long (> 70 chars)
* ✅ Optimal length (45–70 chars)

**What matters:**

* Include primary keyword
* Add brand at the end

---

# ✅ **2. Meta Description**

**How it’s extracted:**
`doc.select("meta[name=description]").attr("content")`

**Checks performed:**

* ❌ Missing → **Critical**
* ⚠️ Length not between 120–160 chars
* ✅ Optimal range

**What matters:**

* Clear purpose
* Benefits + light CTA
* Not duplicated

---

# ✅ **3. Canonical URL**

**How it’s extracted:**
`doc.select("link[rel=canonical]").attr("href")`

**Checks performed:**

* ⚠️ Missing canonical tag
* ✅ Canonical exists

**What matters:**

* Avoid duplicate content
* Canonical = current URL

---

# 🟦 **4. Open Graph Tags (Social Preview)**

## 4.1 OG Title

`meta[property=og:title]`

## 4.2 OG Description

`meta[property=og:description]`

## 4.3 OG Image

`meta[property=og:image]`

**Checks performed:**

* ⚠️ Missing → Warning
* ✅ Present → OK

**What matters:**

* Proper image (1200×630)
* Clean marketing descriptions

---

# 🟦 **5. H1**

**How it’s extracted:**
`doc.select("h1").text()`

**Checks performed:**

* ⚠️ Missing H1
* ✅ H1 exists

**What matters:**

* One H1 only
* Page topic should be obvious

---

# 🟦 **6. Favicon**

**How it’s extracted:**
`link[rel=icon]`, `shortcut icon`

**Checks performed:**

* ⚠️ Missing favicon
* ✅ Favicon found

**What matters:**

* Clear branding
* 32×32 or 64×64

---

# 🔤 **7. Hreflang Tags (Multilingual Sites)**

`link[rel=alternate][hreflang]`

**Checks performed:**

* ⚠️ No hreflangs detected
* ✅ Hreflang(s) found

**What matters:**

* Cross-language linking
* Correct ISO language codes

---

# 🔒 **8. Robots Meta Tag**

`meta[name=robots]`

**Checks performed:**

* ❌ Contains `noindex` → **Critical**
* ✅ Safe robots tag

**What matters:**

* Pages must be indexable
* Avoid accidental noindex

---

# 📱 **9. Viewport Meta Tag**

`meta[name=viewport]`

**Checks performed:**

* ⚠️ Missing or not mobile-friendly
* ✅ Correct viewport tag

**What matters:**

* Mobile friendliness
* Required for responsive design

---

# 📚 **10. JSON-LD Structured Data**

`script[type=application/ld+json]`

**Checks performed:**

* ⚠️ No structured data
* ✅ One or more JSON-LD blocks

**What matters:**

* Richer Google results
* Correct schema types:

  * `Organization`
  * `Article`
  * `Product`
  * `FAQPage`
  * `BreadcrumbList`

---

# 🐦 **11. Twitter Card Tags**

### Title

`meta[name=twitter:title]`

### Description

`meta[name=twitter:description]`

### Image

`meta[name=twitter:image]`

**Checks performed:**

* ⚠️ Missing → Warning
* ✅ Exists → OK

**What matters:**

* Consistency with OG tags
* Prefer `summary_large_image`

---

# 🔗 **12. Internal Links**

**How it’s extracted:**
Count of internal links scraped

**Checks performed:**

* ⚠️ Less than five internal links
* ✅ Healthy linking (≥ 5)

**What matters:**

* Strong internal structure
* Good topic distribution

---

# 🌍 **13. External Links**

**Checks performed:**

* ⚠️ No external links found
* ✅ External links present

**Why:**

* External links boost trust
* Should link to authoritative sources

---

# 🖼 **14. Images & Missing Alt Text**

**How it’s extracted:**
`img` elements & alt attributes

**Checks performed:**

* ⚠️ Missing alt attributes detected
* ✅ All alt text present

**What matters:**

* Accessibility
* Image context for search engines

---

# ✍️ **15. Word Count**

`doc.body().text().split("\\s+")`

**Checks performed:**

* ⚠️ Low content (< 300 words)
* ✅ Healthy word count

**Why:**

* Thin content harms SEO
* Longer content improves ranking potential