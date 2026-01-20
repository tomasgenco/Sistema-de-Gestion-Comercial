# Configuración de Zona Horaria para PostgreSQL

## Problema Resuelto
Las ventas después de las 21:00 hs se guardaban como el día siguiente porque PostgreSQL almacena las fechas en UTC internamente.

## Solución Implementada

### 1. Configuración en application.properties
Se agregaron dos propiedades clave:

```properties
# Inicializa cada conexión con la zona horaria de Argentina
spring.datasource.hikari.connection-init-sql=SET TIME ZONE 'America/Argentina/Buenos_Aires'

# Configura Hibernate para usar la zona horaria de Argentina
spring.jpa.properties.hibernate.jdbc.time_zone=America/Argentina/Buenos_Aires
```

### 2. Configuración en la URL de Conexión (Recomendado)
Actualiza tu archivo `.env` para incluir el parámetro `timezone` en la URL:

**Antes:**
```
DB_URL=jdbc:postgresql://host:5432/database
```

**Después:**
```
DB_URL=jdbc:postgresql://host:5432/database?timezone=America/Argentina/Buenos_Aires
```

### 3. Para Render (Producción)
En las variables de entorno de Render, asegúrate de que tu `DATABASE_URL` incluya el parámetro:

```
DATABASE_URL=jdbc:postgresql://usuario:password@host:5432/database?timezone=America/Argentina/Buenos_Aires
```

O si Render te da una URL en formato `postgres://`, necesitas:
1. Cambiar `postgres://` por `jdbc:postgresql://`
2. Agregar `?timezone=America/Argentina/Buenos_Aires` al final

### 4. Configuración Global en Java
El archivo `CrudApplication.java` ya tiene configurada la zona horaria a nivel de JVM:

```java
@PostConstruct
public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
}
```

## Verificación
Después de aplicar estos cambios:

1. Reinicia la aplicación
2. Registra una venta después de las 21:00 hs
3. Verifica en la base de datos que la fecha se guardó correctamente con UTC-3
4. Confirma que la consulta por fecha del día actual funciona correctamente

## Capas de Protección
La solución implementa múltiples capas para garantizar la zona horaria correcta:

- ✅ **JVM**: Configuración global en `CrudApplication`
- ✅ **JDBC**: Parámetro en la URL de conexión
- ✅ **HikariCP**: Inicialización de conexiones con `SET TIME ZONE`
- ✅ **Hibernate**: Configuración explícita de timezone
- ✅ **Código Java**: Uso de `ZoneId.of("America/Argentina/Buenos_Aires")` en todos los servicios

Con estas 5 capas, las fechas siempre se manejarán correctamente en UTC-3.
