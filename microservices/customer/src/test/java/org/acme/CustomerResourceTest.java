package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;

import org.acme.CustomerResource;

@QuarkusTest
public class CustomerResourceTest {

	@Inject
	io.vertx.mutiny.mysqlclient.MySQLPool client;

	@BeforeEach
	public void setup() {
		client.query("DROP TABLE IF EXISTS Customers").execute()
			.await().indefinitely();
		client.query("CREATE TABLE Customers (id SERIAL PRIMARY KEY, name TEXT NOT NULL, FiscalNumber BIGINT UNSIGNED, location TEXT NOT NULL)").execute()
			.await().indefinitely();
		client.query("INSERT INTO Customers (name,FiscalNumber,location) VALUES ('client1','123456','Lisbon')").execute()
			.await().indefinitely();
	}

	@Test
	public void testGetCustomers() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		Multi<Customer> customers = resource.get();
		assertNotNull(customers);

		customers.subscribe().with(
			customer -> {
				assertNotNull(customer);
				assertEquals("client1", customer.name);
				assertEquals("Lisbon", customer.location);
				assertEquals(123456L, customer.FiscalNumber);
			},
			failure -> fail("Failed to retrieve customers: " + failure.getMessage())
		);
	}

	@Test
	public void testGetCustomerById() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		Uni<Response> response = resource.getSingle(1L);
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
				Customer customer = resp.readEntity(Customer.class);
				assertNotNull(customer);
				assertEquals("client1", customer.name);
				assertEquals("Lisbon", customer.location);
				assertEquals(123456L, customer.FiscalNumber);
			},
			failure -> fail("Failed to retrieve customer: " + failure.getMessage())
		);
	}

	@Test
	public void testGetNonExistentCustomer() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		Uni<Response> response = resource.getSingle(999L); // Non-existent ID
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to handle non-existent customer: " + failure.getMessage())
		);
	}

	@Test
	public void testCreateCustomer() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		Customer newCustomer = new Customer("client2", 654321L, "Porto");
		
		Uni<Response> response = resource.create(newCustomer);

		response.await().indefinitely();
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
				assertNotNull(resp.getLocation());
			},
			failure -> fail("Failed to create customer: " + failure.getMessage())
		);

		// Verify the customer was created
		Uni<Response> getResponse = resource.getSingle(2L);

		getResponse.subscribe().with(
			resp -> {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
				Customer customer = resp.readEntity(Customer.class);
				assertNotNull(customer);
				assertEquals("client2", customer.name);
				assertEquals("Porto", customer.location);
				assertEquals(654321L, customer.FiscalNumber);
			},
			failure -> fail("Failed to retrieve created customer: " + failure.getMessage())
		);
	}

	@Test
	public void testDeleteCustomer() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		
		Uni<Response> response = resource.delete(1L);

		response.await().indefinitely();
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to delete customer: " + failure.getMessage())
		);

		// Verify the customer was deleted
		Uni<Response> getResponse = resource.getSingle(1L);

		getResponse.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to verify deletion of customer: " + failure.getMessage())
		);
	}

	@Test
	public void testUpdateCustomer() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		Long id = 1L;
		String newName = "Updated Client";
		Long newFiscalNumber = 111222L;
		String newLocation = "Updated Location";

		Uni<Response> response = resource.update(id, newName, newFiscalNumber, newLocation);
		
		response.await().indefinitely();

		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to update customer: " + failure.getMessage())
		);

		// Verify the customer was updated
		Uni<Response> getResponse = resource.getSingle(id);

		response.await().indefinitely();

		getResponse.subscribe().with(
			resp -> {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
				Customer customer = resp.readEntity(Customer.class);
				assertNotNull(customer);
				assertEquals(newName, customer.name);
				assertEquals(newLocation, customer.location);
				assertEquals(newFiscalNumber, customer.FiscalNumber);
			},
			failure -> fail("Failed to verify update of customer: " + failure.getMessage())
		);
	}

	@Test
	public void testUpdateNonExistentCustomer() {
		CustomerResource resource = new CustomerResource();
		resource.client = client;
		Long id = 999L; // Non-existent ID
		String newName = "Non Existent Client";
		Long newFiscalNumber = 000000L;
		String newLocation = "Nowhere";

		Uni<Response> response = resource.update(id, newName, newFiscalNumber, newLocation);
		
		response.await().indefinitely();

		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to handle update of non-existent customer: " + failure.getMessage())
		);
	}


}
