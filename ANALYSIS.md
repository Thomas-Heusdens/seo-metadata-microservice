# 🔍 SEO & Online Presence – Compact Checklist

---

## 1. Title

**How to fetch:** `doc.title()`
**What to check:**

* Ideal length: **45–70 characters**
* Contains main keyword
* Brand included at end
* Unique per page

---

## 2. Meta Description

**How to fetch:** `doc.select("meta[name=description]").attr("content")`
**What to check:**

* Ideal length: **120–160 characters**
* Clear explanation of page purpose
* 1–2 keywords naturally included
* Soft CTA recommended
* Unique per page

---

## 3. Canonical

**How to fetch:** `doc.select("link[rel=canonical]").attr("href")`
**What to check:**

* Exists
* Self-referential (canonical = current URL)
* No canonical chains
* Consistent with site structure

---

## 4. Keywords (Legacy)

**How to fetch:** `doc.select("meta[name=keywords]").attr("content")`
**What to check:**

* Optional (not used by Google)
* Should not be spammy

---

## 5. Open Graph Title

**How to fetch:** `doc.select("meta[property=og:title]").attr("content")`
**What to check:**

* Exists
* Clear, concise, and similar to `<title>`
* More “marketing-style” allowed

---

## 6. Open Graph Description

**How to fetch:** `doc.select("meta[property=og:description]").attr("content")`
**What to check:**

* Exists
* Short, clear, benefit-focused
* Not keyword-stuffed

---

## 7. Open Graph Image

**How to fetch:** `doc.select("meta[property=og:image]").attr("content")`
**What to check:**

* Exists
* High resolution (**1200×630 recommended**)
* Relevant and professional
* Not a tiny logo

---

## 8. H1

**How to fetch:** `doc.select("h1").text()`
**What to check:**

* Only **one** main H1
* Clear statement of the page topic
* Matches the title/intent
* Not generic (“Welcome”, “Home”)

---

## 9. Favicon

*(Optional)*
**How to fetch:**

* `doc.select("link[rel=icon]").attr("href")`
* `doc.select("link[rel=shortcut icon]").attr("href")`
  **What to check:**
* Exists
* High-quality (32×32, 64×64)
* Consistent branding

---

## 10. Hreflang (Optional future feature)

**How to fetch:** `doc.select("link[rel=alternate][hreflang]")`
**What to check:**

* Exists for multilingual sites
* Correct language codes (`en`, `fr-BE`, etc.)
* All languages cross-reference each other

---

## 11. Meta Robots

**How to fetch:** `doc.select("meta[name=robots]").attr("content")`
**What to check:**

* Should typically be `index, follow`
* Ensure no accidental `noindex`, `nofollow`

---

## 12. Viewport

**How to fetch:** `doc.select("meta[name=viewport]").attr("content")`
**What to check:**

* Should include: `width=device-width, initial-scale=1`
* Missing viewport = poor mobile usability

---

## 13. JSON-LD Structured Data

**How to fetch:** `doc.select("script[type=application/ld+json]")`
**What to check:**

* Exists
* Relevant schemas (Article, Product, FAQ, Organization, Breadcrumb, etc.)
* Proper JSON structure

---

## 14. Twitter Tags

**How to fetch:**

* Title: `doc.select("meta[name=twitter:title]").attr("content")`
* Description: `doc.select("meta[name=twitter:description]").attr("content")`
* Image: `doc.select("meta[name=twitter:image]").attr("content")`
  **What to check:**
* Exists
* Consistent with OG tags
* Prefer `summary_large_image`

---

## 16. Internal Links

**How to fetch:** `doc.select("a[href^='/']")`
**What to check:**

* Number of internal links
* Good internal linking → better SEO
* Compare coverage vs reference

---

## 17. External Links

**How to fetch:** `doc.select("a[href^='http']")`
**What to check:**

* Number of external links
* Should not rely on low-quality or broken links

---

## 18. Images & Alt Text

**How to fetch:**

* All images: `doc.select("img")`
* Alt: `doc.select("img").attr("alt")`
  **What to check:**
* Missing alt attributes
* Compare ratio of missing alt vs reference
* Accessibility + SEO

---

## 19. Page Word Count

**How to fetch:**

```java
doc.body().text().split("\\s+").length;
```

**What to check:**

* Low content = weak SEO
* Compare total words vs reference page
* Useful coaching metric

---

## 20. Apple Touch Icon

**How to fetch:** `doc.select("link[rel=apple-touch-icon]").attr("href")`
**What to check:**

* Exists
* High-resolution brand icon
* Better mobile appearance when saved to home screen