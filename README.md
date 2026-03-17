# Car Collector HW (Android)

Aplicativo Android para revisar los lotes de Hot Wheels publicados en:

- https://t-hunted.blogspot.com/p/lotes-hot-wheels.html

## Qué hace

- Descarga y lista los lotes detectados en la página de referencia.
- Permite buscar por nombre de lote.
- Al tocar un lote, abre la página en una vista integrada (`WebView`) para revisar imágenes y contenido.

## Dónde están las vistas

Las vistas están en XML dentro de `app/src/main/res/layout`:

- `activity_main.xml`: lista de lotes + buscador.
- `item_lot.xml`: tarjeta individual de cada lote.
- `activity_lot_detail.xml`: `WebView` para el detalle.

La lógica Kotlin está en `app/src/main/java/com/carcollector`.

## Requisitos

- Android Studio Jellyfish o superior.
- JDK 17.
- SDK de Android 34.

## Ejecutar

1. Abre la carpeta en Android Studio.
2. Sincroniza Gradle.
3. Ejecuta en emulador o dispositivo físico.
