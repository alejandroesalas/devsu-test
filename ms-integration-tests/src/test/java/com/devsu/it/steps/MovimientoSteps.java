package com.devsu.it.steps;

import com.devsu.it.config.ContainerConfig;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class MovimientoSteps {

    private String cuentasUrl;
    private Response response;

    @Before
    public void setup() {
        cuentasUrl = "http://localhost:" + ContainerConfig.MS_CUENTAS.getMappedPort(8082);
    }

    @Given("la cuenta {string} tiene saldo 0")
    public void laCuentaTieneSaldo(String numeroCuenta) {
        given()
            .baseUri(cuentasUrl)
        .when()
            .get("/cuentas/" + numeroCuenta)
        .then()
            .statusCode(200);
    }

    @When("se intenta retirar {int} de la cuenta {string}")
    public void seIntentaRetirar(int valor, String numeroCuenta) {
        response = given()
            .baseUri(cuentasUrl)
            .contentType("application/json")
            .body(String.format("{\"numeroCuenta\": \"%s\", \"valor\": -%d}", numeroCuenta, valor))
        .when()
            .post("/movimientos");
    }

    @Then("la respuesta es {int} con mensaje {string}")
    public void laRespuestaEs(int statusCode, String mensaje) {
        response.then()
            .statusCode(statusCode)
            .body(containsString(mensaje));
    }
}
