Feature: Movimientos bancarios

  Scenario: Retiro rechazado por saldo insuficiente
    Given la cuenta "495878" tiene saldo 0
    When se intenta retirar 9999 de la cuenta "495878"
    Then la respuesta es 400 con mensaje "Saldo no disponible"
