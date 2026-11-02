# ADR 003: Motor definitivo — PostgreSQL

**Estado**: Aceptada
**Fecha**: 2026-07-27
**Reemplaza a**: ADR 001

## Contexto
ADR 001 eligió MySQL por familiaridad. Al ponderar criterios de
despliegue y roadmap del proyecto, se reevaluó la decisión antes de
escribir código.

## Decisión
Usar PostgreSQL 16.

## Justificación
- Soporte nativo y gratuito en Render/Railway (plataformas de despliegue).
- JSONB y full-text search útiles para la Fase 3 (filtros en lenguaje
  natural, datos semiestructurados de portales).
- Costo de aprendizaje bajo: SQL estándar compartido con MySQL.

## Consecuencias
- (+) Despliegue más simple y económico.
- (−) Curva menor de aprendizaje en particularidades de PostgreSQL.