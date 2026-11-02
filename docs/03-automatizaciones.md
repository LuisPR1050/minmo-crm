# Inventario de automatizaciones por módulo

Cada automatización se clasifica en tres tipos:
- **Determinista**: lógica de negocio en Java puro, sin IA.
- **Programada**: tareas que se ejecutan solas en el tiempo (scheduler).
- **IA generativa**: requiere llamar a una API de inteligencia artificial.

## Captación
- Determinista: código único del inmueble, pipeline de estados,
  historial de cambios, checklist de estado jurídico, matching
  cliente-propiedad por criterios.
- Programada: agenda de visitas de captación.
- IA: precio sugerido por comparables, descripción y copy
  automáticos, filtro en lenguaje natural.

## Clientes
- Determinista: pipeline de leads, historial cronológico de
  interacciones, registro de visitas y objeciones.
- Programada: recordatorios de visita (24h/2h), alertas de seguimiento.
- IA: recomendación de inmuebles, análisis de objeciones.

## Negociación
- Determinista: proyección de gastos de escrituración, historial,
  cierre automático del negocio, actualización de estado de la
  propiedad a vendida.
- Programada: alertas sobre fechas críticas.
- IA: (ninguna en el alcance actual)

## Documentación
- Determinista: generación de PDF por plantilla con autocompletado,
  numeración consecutiva, versionado, saldos y pagos parciales,
  otrosí que hereda datos de la promesa.
- Programada: recordatorios de vencimiento, alerta de pago atrasado.
- IA: redacción de cláusulas especiales, detección de inconsistencias.

## Marketing
- Determinista: historial de publicaciones.
- Programada: alerta de publicación antigua sin contactos.
- IA: adaptación de contenido por portal, hashtags, generación de
  videos, análisis de fotos.