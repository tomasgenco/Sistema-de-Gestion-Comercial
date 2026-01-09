# Copilot Instructions - Sistema de Gestión Comercial

## Project Overview
Spring Boot 4.0 REST API for a commercial management system (stock, sales, purchases, finances). Uses PostgreSQL, Spring Data JPA, Spring Security with JWT authentication.

**Key Path**: `src/main/java/com/ApiRestStock/CRUD/`

## Architecture Pattern: Domain-Driven Domain Separation

The codebase separates into **independent business domains** within the CRUD module:

- **`stock/`**: Product inventory management (ProductModel, IProductRepository, ProductService)
- **`ventas/`**: Sales operations (VentaModel, VentaService, DetalleVentaModel for line items)
- **`Finanzas/`**: Financial tracking:
  - `Compra/`: Purchases from suppliers (CompraModel, CompraService)
  - `ingreso/`: Income records (IngresoModel, linked to Venta or Fiado)
  - `gasto/`: Expenses (GastoModel, linked to Compra)
  - `cierreCaja/`: Daily cash reconciliation (CierreCajaModel)
- **`proveedor/`**: Supplier master data (ProveedorModel)
- **`shared/`**: Cross-cutting concerns:
  - `security/`: JWT auth (JwtService, JwtAuthFilter, SecurityConfig)
  - `service/AuthService`: User registration/login with role-based access
  - `exception/GlobalExceptionHandler`: Centralized error handling
  - `enums/RolUsuario`: ADMIN, VENDEDOR roles

## Critical Data Flows

### 1. Sales Transaction (Venta)
```
VentaController.POST /venta 
  → VentaService.registrarVenta()
    → ProductService.actualizarStockPorNombre() [restar]
    → IngresoService.registrarIngreso(TipoIngreso.VENTA)
    → VentaRepository.save()
```
**Key**: Stock decrements immediately; Ingreso record created for finance tracking.

### 2. Purchase Transaction (Compra)
```
CompraController.POST /compras
  → CompraService.registrarCompra()
    → ProductService.buscarOCrearProducto() [auto-create if SKU provided]
    → ProductService.actualizarStockPorNombre() [sumar]
    → GastoService.registrarGasto(TipoGasto.PROVEEDOR)
    → CompraRepository.save()
```
**Key**: Products auto-created via SKU; Stock increments; Gasto record created.

### 3. Daily Cash Close (Cierre Caja)
```
CierreCajaController.POST /cierre-caja
  → CierreCajaService.cerrarCaja()
    → VentaRepository.sumTotalByFechaAndMetodoPago() [for each payment method]
    → CierreCajaRepository.save()
```
**Key**: Validates no duplicate closes per date; computes cash discrepancy (efectivo_real vs teorico).

## Security & Authentication
- **Entry Point**: `AuthController` - `/auth/register`, `/auth/login`
- **Token**: JWT with BCrypt password hashing (JwtService, CustomUserDetailsService)
- **Filter Chain**: JwtAuthFilter validates token + loads user authorities before controller
- **Authorization**: 
  - VENDEDOR: Can POST `/ventas/**`, `/compras/**`
  - ADMIN: All endpoints except auth
- **Config**: [SecurityConfig.java](src/main/java/com/ApiRestStock/CRUD/shared/security/SecurityConfig.java) - stateless sessions, CSRF disabled for APIs

## Database Peculiarities
- **Timezone**: América/Argentina/Buenos Aires (embedded in HikariCP, JVM args, PostgreSQL)
- **OffsetDateTime**: Used for transactional timestamps; `.withHour(0)` patterns for date boundaries
- **Decimal**: BigDecimal for all monetary fields (precision 10,2)
- **Enums**: EnumType.STRING persisted (MetodoPago, TipoIngreso, TipoGasto, RolUsuario)

## Build & Run
- **Maven**: `mvnw clean package`, `mvnw spring-boot:run`
- **Docker**: `docker-compose up` (PostgreSQL only; Spring Boot runs on host)
- **Port**: 4000 (application.properties)
- **Environment**: Requires `.env` with `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SECRETO_JWT`

## Common Modification Patterns

### Adding a New Endpoint
1. Create Model (JPA @Entity in domain folder)
2. Create Repository (extend JpaRepository, add custom queries with @Query)
3. Create Service (business logic, @Transactional on cross-domain ops)
4. Create Controller (REST mapping, validate auth via @PreAuthorize if needed)
5. DTO layer: Use records for requests, DTOs for responses (see CompraRequest, CompraResponse)

### Linking Domains
- **One-to-Many**: Use `@JsonManagedReference` on parent, `@JsonBackReference` on child (VentaModel ↔ DetalleVentaModel)
- **One-to-One Financial Link**: Ingreso/Gasto link to Venta/Compra via unique foreign keys
- **Lazy Loading**: Use FetchType.LAZY; explicitly join if needed in queries

### Exception Handling
- **Custom Exceptions** in `Finanzas/exception/`: ProductosFaltantesException, NoFoundComprasProveedorException, CierreCajaDuplicadoException
- **Global Handler**: GlobalExceptionHandler maps domain exceptions → ApiError JSON
- **Pattern**: `throw new CustomException("detail")` → caught and mapped to HTTP status

## Key Files to Review
- [application.properties](src/main/resources/application.properties) - DB, JWT config
- [pom.xml](pom.xml) - Spring Boot 4.0, JPA, Security, JJWT 0.12.5
- [SecurityConfig.java](src/main/java/com/ApiRestStock/CRUD/shared/security/SecurityConfig.java) - Authorization matrix
- [VentaService.java](src/main/java/com/ApiRestStock/CRUD/ventas/VentaService.java) - Transaction orchestration example
- [CompraService.java](src/main/java/com/ApiRestStock/CRUD/Finanzas/Compra/CompraService.java) - Validation + auto-create pattern
- [CierreCajaService.java](src/main/java/com/ApiRestStock/CRUD/Finanzas/cierreCaja/CierreCajaService.java) - Date-based business logic

## Testing Notes
- Tests are minimal (only CrudApplicationTests.contextLoads())
- Add tests in `src/test/` for new services using @SpringBootTest, @DataJpaTest
- Mock repositories with @MockBean or in-memory H2 database for isolation
