# language: es
Característica: Cierre automático de negociación
  Como sistema del CRM
  quiero cerrar automáticamente una negociación y marcar el inmueble como vendido
  cuando se cumplen los tres hitos de cierre (escritura firmada, pago recibido, entrega realizada)

  Escenario: Los tres hitos se cumplen y el negocio se cierra
    Dado una negociación en estado "ESPERANDO_ESCRITURA" con escritura firmada y pago recibido
    Cuando se marca la entrega como realizada
    Entonces la negociación queda en estado "NEGOCIO_CERRADO"
    Y el inmueble queda marcado con estado de captación "VENDIDO"

  Escenario: Falta un hito y el negocio no se cierra
    Dado una negociación en estado "NEGOCIACION_INICIADA" sin ningun hito cumplido
    Cuando se marca la escritura como firmada
    Entonces la negociación permanece en estado "NEGOCIACION_INICIADA"
    Y el inmueble no se marca como vendido

  Escenario: Un negocio ya cerrado no se vuelve a cerrar
    Dado una negociación en estado "NEGOCIO_CERRADO" con los tres hitos cumplidos
    Cuando se marca el pago como recibido nuevamente
    Entonces el inmueble no se marca como vendido otra vez
