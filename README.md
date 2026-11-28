# Pakete – historischer MultiCarrierScan-Prototyp

**Status: früher, nicht produktionsreifer UI-Prototyp.** Dieses Repository ist nicht die freigegebene TRAKUNA-Produktivquelle.

## Was dieser Stand tatsächlich kann

- Manuelle Eingabe eines Tracking-Codes
- Anzeige einer einfachen Jetpack-Compose-Oberfläche
- Öffnen einer externen Tracking-Webseite

## Was nicht implementiert oder nicht nachgewiesen ist

- Kein integrierter Kamera-/Barcode-Scanner
- Keine offizielle Carrier-API und keine bestätigte Buchung
- Keine Paketshop-Annahme, Kundenausgabe, Retouren- oder Übergabeprozesse
- Keine Hermes-Partnerintegration
- Keine produktive Mehrbenutzer-/Synchronisierungsarchitektur
- Keine Geräte- oder Paketshop-Abnahme

Die frühe Code-Zuordnung anhand der Zeichenlänge ist nicht zuverlässig und darf nicht für operative Paketshop-Entscheidungen verwendet werden.

## Maßgebliche Weiterentwicklung

Für die aktuelle Android-Scan-Zentrale siehe [TRAKUNA](https://github.com/mouzzlii/Fuck). Für die übergreifende Integrationsarchitektur siehe [multi-carrier-gateway](https://github.com/mouzzlii/multi-carrier-gateway).

Dieses Repository bleibt als historischer Entwicklungsstand erhalten. Die tatsächlich eingebundenen App-Quellen und die kanonische Android-Manifestdatei bleiben erhalten. Drei fehlplatzierte Altdateien außerhalb der Android-Build-Struktur werden in einem separaten Maintenance-PR entfernt.

## Build

Voraussetzungen: JDK 17, Android SDK Platform 34 und Gradle 8.2.1.

```bash
gradle assembleDebug
```

Der GitHub-Actions-Workflow baut eine Debug-APK. Ein erfolgreicher Build belegt nur die Kompilierbarkeit, nicht Carrier-Integration, Scannerfunktion oder Produktionsreife.

## Rechte und Vertraulichkeit

Siehe [PROPRIETARY-NOTICE.md](PROPRIETARY-NOTICE.md). Unveröffentlichte Erfindungen, Kundendaten, Schlüssel und vertrauliche Fertigungsdetails gehören nicht in dieses öffentliche Repository.
