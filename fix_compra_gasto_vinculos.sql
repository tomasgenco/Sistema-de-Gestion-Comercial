-- Script para vincular gastos de tipo PROVEEDOR con sus compras correspondientes
-- Este script busca gastos sin compra_id y los vincula basándose en la fecha, total y proveedor

-- Verificar gastos sin compra_id (tipo PROVEEDOR)
SELECT 
    g.gasto_id,
    g.fecha,
    g.total,
    g.nombre_proveedor,
    g.compra_id as compra_actual
FROM gasto g
WHERE g.tipo = 'PROVEEDOR'
AND g.compra_id IS NULL;

-- Actualizar gastos vinculándolos con sus compras correspondientes
-- Basándose en: mismo proveedor, mismo total, y fecha cercana (mismo día)
UPDATE gasto g
SET compra_id = (
    SELECT c.compra_id
    FROM compra c
    INNER JOIN proveedor p ON c.proveedor_id = p.proveedor_id
    WHERE p.nombre_empresa = g.nombre_proveedor
    AND c.total = g.total
    AND DATE(c.fecha_hora) = DATE(g.fecha)
    LIMIT 1
)
WHERE g.tipo = 'PROVEEDOR'
AND g.compra_id IS NULL
AND EXISTS (
    SELECT 1
    FROM compra c
    INNER JOIN proveedor p ON c.proveedor_id = p.proveedor_id
    WHERE p.nombre_empresa = g.nombre_proveedor
    AND c.total = g.total
    AND DATE(c.fecha_hora) = DATE(g.fecha)
);

-- Verificar cuántos quedaron sin vincular
SELECT COUNT(*) as gastos_sin_vincular
FROM gasto g
WHERE g.tipo = 'PROVEEDOR'
AND g.compra_id IS NULL;

-- Ver el resultado después de la actualización
SELECT 
    g.gasto_id,
    g.fecha,
    g.total,
    g.nombre_proveedor,
    g.compra_id,
    c.metodo_pago
FROM gasto g
LEFT JOIN compra c ON g.compra_id = c.compra_id
WHERE g.tipo = 'PROVEEDOR'
ORDER BY g.fecha DESC
LIMIT 20;
