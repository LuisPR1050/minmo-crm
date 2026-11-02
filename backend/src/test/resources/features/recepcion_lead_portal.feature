# language: es
Característica: Recepción de contactos desde portales inmobiliarios
  Como sistema del CRM
  quiero validar los datos de un contacto recibido por webhook de un portal
  para no crear clientes con datos incompletos o inválidos

  Esquema del escenario: Datos obligatorios faltantes son rechazados
    Dado un contacto de portal con nombre "<nombre>" y telefono "<telefono>"
    Cuando se recibe el contacto
    Entonces la recepción es rechazada con un error que menciona "<motivo>"

    Ejemplos:
      | nombre       | telefono     | motivo   |
      |              | 3001234567   | nombre   |
      | Maria Perez  |              | telefono |

  Escenario: Un correo con formato invalido es rechazado
    Dado un contacto de portal con nombre "Maria Perez", telefono "3001234567" y correo "no-es-un-correo"
    Cuando se recibe el contacto
    Entonces la recepción es rechazada con un error que menciona "correo"

  Escenario: Un contacto con datos validos se registra como cliente
    Dado un contacto de portal con nombre "Maria Perez", telefono "3001234567" y correo "maria@example.com"
    Cuando se recibe el contacto
    Entonces se crea un cliente con nombre "Maria Perez"
