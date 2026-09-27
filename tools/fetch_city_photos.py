"""Fetch free Wikipedia page photos and attribution for the offline city catalogue.

Run manually when changing City.java; normal APK builds never need network.
"""
import concurrent.futures
import html
import json
import re
import time
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "preview" / "assets"
PHOTO_DIR = ASSETS / "cities"
AGENT = "TimezoneApp/1.2 (city photos; attribution in documentation)"
API = "https://en.wikipedia.org/w/api.php"
ALIASES = {
    "austin": "Texas State Capitol",
    "orlando": "Lake Eola Park",
    "kawasaki": "Kawasaki Daishi",
    "kano": "Ancient Kano City Walls",
    "daejeon": "Daejeon Station",
    "delhi": "New Delhi",
    "newyork": "New York City",
    "phoenix": "Phoenix, Arizona",
    "darwin": "Darwin, Northern Territory",
    "hongkong": "Hong Kong Island",
    "macau": "Macau Tower",
    "singapore": "Gardens by the Bay",
    "bali": "Tanah Lot",
    "chatham": "Waitangi, Chatham Islands",
    "hochiminh": "Ho Chi Minh City",
    "stjohns": "St. John's, Newfoundland and Labrador",
    "washington": "Washington, D.C.",
    "taipei": "Taipei",
    "bogota": "Bogotá",
    "reykjavik": "Reykjavík",
}
PHOTO_REFRESH = {"macau", "bali", "chatham"}


def fetch(url, attempts=6):
    for attempt in range(attempts):
        try:
            request = urllib.request.Request(url, headers={"User-Agent": AGENT})
            with urllib.request.urlopen(request, timeout=30) as response:
                return response.read()
        except Exception as error:
            if attempt == attempts - 1:
                raise
            time.sleep(20 * (attempt + 1) if getattr(error, "code", None) == 429 else 2 * (attempt + 1))


def api(params):
    time.sleep(1.5)
    url = API + "?" + urllib.parse.urlencode(params)
    return json.loads(fetch(url))


def plain(value):
    value = re.sub(r"<[^>]+>", "", value or "")
    return html.unescape(value).strip()


def chunks(values, size):
    for start in range(0, len(values), size):
        yield values[start : start + size]


def main():
    source = (ROOT / "app/src/com/cantonadong/timezone/City.java").read_text(encoding="utf-8")
    cities = re.findall(r'add\("([a-z]+)","([^"]+)","([^"]+)"', source)
    for group in re.finditer(r'^\s*group\("[^"]+","[^"]+","[^"]+",(.+)\);$', source, re.MULTILINE):
        cities.extend(tuple(entry.split("|", 2)) for entry in re.findall(r'"([^"\n]+)"', group.group(1)))
    assert len(cities) >= 80, "city catalogue not found"
    pages = {city_id: ALIASES.get(city_id, en) for city_id, _, en in cities}
    selected = {}
    for group in chunks(list(pages.values()), 45):
        data = api({"action": "query", "format": "json", "formatversion": "2", "redirects": "1", "prop": "pageimages", "piprop": "name|thumbnail", "pithumbsize": "192", "pilicense": "free", "titles": "|".join(group)})
        normalized = {item["from"]: item["to"] for item in data.get("query", {}).get("normalized", [])}
        redirects = {item["from"]: item["to"] for item in data.get("query", {}).get("redirects", [])}
        by_title = {item["title"]: item for item in data["query"]["pages"]}
        for city_id, title in pages.items():
            resolved = redirects.get(normalized.get(title, title), normalized.get(title, title))
            page = by_title.get(resolved)
            if page and "thumbnail" in page and "pageimage" in page:
                selected[city_id] = {"title": page["title"], "file": page["pageimage"], "url": page["thumbnail"]["source"]}
    missing = sorted(set(pages) - set(selected))
    if missing:
        raise SystemExit("No free page image for: " + ", ".join(missing))

    metadata = {}
    files = ["File:" + selected[city_id]["file"] for city_id in pages]
    for group in chunks(files, 40):
        data = api({"action": "query", "format": "json", "formatversion": "2", "prop": "imageinfo", "iiprop": "extmetadata|url|user", "titles": "|".join(group)})
        for item in data["query"]["pages"]:
            if item.get("imageinfo"):
                metadata[item["title"].removeprefix("File:").replace(" ", "_")] = item["imageinfo"][0]
    missing_licenses = sorted({item["file"] for item in selected.values() if item["file"] not in metadata or not metadata[item["file"]].get("extmetadata", {}).get("LicenseShortName", {}).get("value")})
    if missing_licenses:
        raise SystemExit("Missing license metadata: " + ", ".join(missing_licenses))

    PHOTO_DIR.mkdir(parents=True, exist_ok=True)
    def save(item):
        city_id, record = item
        extension = Path(urllib.parse.urlparse(record["url"]).path).suffix.lower()
        if extension not in {".jpg", ".jpeg", ".png", ".webp", ".gif"}:
            # Wikimedia thumbnails of SVG, TIFF and other formats are PNG.
            extension = ".png"
        path = PHOTO_DIR / (city_id + extension)
        if path.exists() and city_id not in PHOTO_REFRESH:
            return city_id, "cities/" + path.name
        data = fetch(record["url"])
        if not data.startswith((b"\xff\xd8\xff", b"\x89PNG", b"RIFF", b"GIF8")):
            raise ValueError(f"Unexpected image format for {city_id}: {record['url']}")
        path.write_bytes(data)
        return city_id, "cities/" + path.name

    with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
        photos = dict(pool.map(save, selected.items()))
    if len(photos) != len(cities):
        raise SystemExit(f"Only {len(photos)} of {len(cities)} city photos saved")
    for city_id, _zh, _en in cities:
        keep = ASSETS / photos[city_id]
        for old in PHOTO_DIR.glob(city_id + ".*"):
            if old != keep:
                old.unlink()

    (ASSETS / "photo-map.js").write_text("window.CITY_PHOTOS = " + json.dumps(photos, ensure_ascii=False, sort_keys=True) + ";\n", encoding="utf-8")
    lines = ["# 城市照片来源", "", "全部图片来自维基百科条目中标记为免费许可的页面图片，文件来源及许可信息如下。图片随 APK 离线提供；点击链接可查看原图、作者和许可。", "", "| 城市 | 图片作者 | 许可 | 来源 |", "| --- | --- | --- | --- |"]
    for city_id, zh, en in cities:
        item = selected[city_id]
        info = metadata[item["file"]]
        ext = info["extmetadata"]
        artist = plain(ext.get("Artist", {}).get("value") or info.get("user", ""))
        license_name = plain(ext["LicenseShortName"]["value"])
        file_url = info.get("descriptionurl") or "https://commons.wikimedia.org/wiki/File:" + urllib.parse.quote(item["file"].replace(" ", "_"))
        lines.append(f"| {zh} / {en} | {artist.replace('|', '/')} | {license_name.replace('|', '/')} | [原图]({file_url}) |")
    (ROOT / "docs/照片来源.md").write_text("\n".join(lines) + "\n", encoding="utf-8")
    credit_lines = ["City photo credits / 城市照片来源", ""]
    html_rows = []
    for city_id, zh, en in cities:
        item = selected[city_id]
        info = metadata[item["file"]]
        ext = info["extmetadata"]
        artist = plain(ext.get("Artist", {}).get("value") or info.get("user", ""))
        license_name = plain(ext["LicenseShortName"]["value"])
        file_url = info.get("descriptionurl") or "https://commons.wikimedia.org/wiki/File:" + urllib.parse.quote(item["file"].replace(" ", "_"))
        credit_lines.extend([f"{zh} / {en} — {artist} — {license_name}", file_url, ""])
        html_rows.append(f"<li><strong>{html.escape(zh)} / {html.escape(en)}</strong><br>{html.escape(artist)} · {html.escape(license_name)}<br><a href=\"{html.escape(file_url, quote=True)}\">{html.escape(item['file'].replace('_', ' '))}</a></li>")
    (ASSETS / "photo-credits.txt").write_text("\n".join(credit_lines), encoding="utf-8")
    (ASSETS / "photo-credits.html").write_text("<!doctype html><html lang=\"zh-CN\"><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>城市照片来源 / Photo credits</title><style>body{font-family:system-ui,sans-serif;margin:24px auto;padding:0 20px;max-width:680px;line-height:1.6;color:#172033}li{margin:16px 0;overflow-wrap:anywhere}a{color:#086bff}</style><h1>城市照片来源 / Photo credits</h1><p>图片来自维基百科中标记为免费许可的照片。点击文件名查看完整许可信息。 / Photos from Wikipedia pages marked with free licenses. Tap a filename for full license details.</p><ol>" + "".join(html_rows) + "</ol></html>", encoding="utf-8")
    print(f"Saved {len(photos)} photos and attribution records")


if __name__ == "__main__":
    main()
