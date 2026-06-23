# Referencia — `service/FirebaseService.java`

> **Archivo a crear durante la clase:** `src/main/java/com/ufide/clase4base/service/FirebaseService.java`
>
> **Cuándo:** PARTE E del lab (bonus). Solo si llegan al Firebase Storage.

## Pre-requisitos antes de crear este archivo

1. **Pom:** descomentar la dependencia `firebase-admin` en `pom.xml`.
2. **application.properties:** descomentar `firebase.bucket=...` y poner el bucket del proyecto Firebase Console.
3. **Service Account:** descargar el JSON desde Firebase Console → Project Settings → Service accounts → Generate new private key. Guardarlo como `src/main/resources/firebase-key.json`.
4. **.gitignore:** asegurarse que `firebase-key.json` esté en `.gitignore` (NUNCA subir al repo).

## Código

```java
package com.ufide.clase4base.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

/**
 * Wrapper sencillo de Firebase Storage para subir imagenes.
 *
 * Si NO existe firebase-key.json en resources/, el servicio queda inactivo
 * y la app sigue arrancando normalmente. Esto permite que los alumnos que no
 * hagan el bonus puedan correr la app sin errores.
 */
@Service
public class FirebaseService {

    @Value("${firebase.bucket:}")
    private String bucket;

    private boolean activo = false;

    @PostConstruct
    public void init() {
        try (InputStream key = new ClassPathResource("firebase-key.json").getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(key))
                    .setStorageBucket(bucket)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            activo = true;
            System.out.println("[Firebase] inicializado con bucket " + bucket);
        } catch (IOException ex) {
            System.out.println("[Firebase] NO inicializado (sin firebase-key.json). Bonus desactivado.");
        }
    }

    public boolean isActivo() {
        return activo;
    }

    /**
     * Sube el archivo a Firebase Storage y devuelve la URL publica.
     */
    public String subir(MultipartFile file) throws IOException {
        if (!activo) {
            throw new IllegalStateException("FirebaseService no esta inicializado");
        }
        String nombre = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Bucket b = StorageClient.getInstance().bucket();
        Blob blob = b.create(nombre, file.getBytes(), file.getContentType());
        // Hacer la imagen publica para que la pueda mostrar el navegador
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        return String.format("https://storage.googleapis.com/%s/%s", bucket, nombre);
    }
}
```

## Conceptos clave para mencionar en clase

- **`@PostConstruct`** corre el `init()` una sola vez al arrancar la app, después de inyectar el bean.
- **Degradación graceful:** si no hay `firebase-key.json`, el servicio marca `activo=false` y la app no rompe. El controller verifica `firebaseService.isActivo()` antes de intentar subir.
- **`UUID.randomUUID()`** evita colisiones de nombres si dos cursos suben una imagen llamada igual.
- **`Acl.User.ofAllUsers() + Acl.Role.READER`** hace la imagen pública. En producción real conviene URLs firmadas con expiración.
- **NUNCA** subir `firebase-key.json` al repo. Si se sube por error, hay que **rotar la key** en Firebase Console inmediatamente.

## Cómo probar sin Firebase real

Pueden hacer el bonus sin tener cuenta Firebase: dejar el `firebase-key.json` sin crear. La app va a loggear `[Firebase] NO inicializado` y los formularios con imagen seguirán funcionando — la imagen simplemente no se sube y `imagenUrl` queda en `null`.
