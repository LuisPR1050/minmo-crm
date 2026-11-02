# language: es
Característica: Registro de pagos y calculo de saldo pendiente
  Como asesor inmobiliario
  quiero que el sistema calcule el saldo pendiente al registrar un pago
  para llevar el control financiero de cada negociación

  Escenario: El saldo pendiente descuenta los pagos previos y el nuevo pago
    Dado una negociación con precio final acordado de 100000000 y pagos previos por 30000000
    Cuando se registra un recibo de pago por 20000000
    Entonces el saldo pendiente del recibo es 50000000

  Escenario: Sin precio final acordado no se puede calcular el saldo pendiente
    Dado una negociación sin precio final acordado
    Cuando se registra un recibo de pago por 20000000
    Entonces el saldo pendiente del recibo es nulo
