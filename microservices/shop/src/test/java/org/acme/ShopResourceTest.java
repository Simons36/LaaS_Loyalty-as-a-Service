package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.inject.Inject;

import org.acme.ShopResource;

@QuarkusTest
public class ShopResourceTest {

	@Inject
	io.vertx.mutiny.mysqlclient.MySQLPool client;

	@BeforeEach
	public void setup() {
		client.query("DROP TABLE IF EXISTS Shops").execute()
			.await().indefinitely();
		client.query("CREATE TABLE Shops (id SERIAL PRIMARY KEY, name TEXT NOT NULL, location TEXT NOT NULL)").execute()
			.await().indefinitely();
		client.query("INSERT INTO Shops (name,location) VALUES ('ArcoCegoLisbon','Lisboa')").execute()
			.await().indefinitely();
	}

	@Test
	public void testGetShops() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Multi<Shop> shops = resource.get();
		assertNotNull(shops);

		shops.subscribe().with(
			shop -> {
				assertNotNull(shop);
				assertEquals("ArcoCegoLisbon", shop.name);
				assertEquals("Lisboa", shop.location);
			},
			failure -> fail("Failed to retrieve shops: " + failure.getMessage())
		);
	}

	@Test
	public void testGetShopById() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Uni<Response> response = resource.getSingle(1L);
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
				Shop shop = (Shop) resp.getEntity();
				assertNotNull(shop);
				assertEquals("ArcoCegoLisbon", shop.name);
				assertEquals("Lisboa", shop.location);
			},
			failure -> fail("Failed to retrieve shop by ID: " + failure.getMessage())
		);
	}

	@Test
	public void testGetNonExistentShop() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Uni<Response> response = resource.getSingle(999L); // Assuming 999 does not exist
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to retrieve non-existent shop: " + failure.getMessage())
		);
	}
	
	@Test
	public void testCreateShop() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Shop newShop = new Shop("NewShop", "NewLocation");
		
		Uni<Response> response = resource.create(newShop);

		response.await().indefinitely(); // Wait for the response to be processed
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
				assertNotNull(resp.getLocation());
			},
			failure -> fail("Failed to create shop: " + failure.getMessage())
		);

		// Verify the shop was created
		Uni<Response> getResponse = resource.getSingle(2L); // Assuming the new shop gets ID 2
		
		getResponse.await().indefinitely(); // Wait for the response to be processed

		getResponse.subscribe().with(
			resp -> {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
				Shop shop = (Shop) resp.getEntity();
				assertNotNull(shop);
				assertEquals("NewShop", shop.name);
				assertEquals("NewLocation", shop.location);
			},
			failure -> fail("Failed to retrieve created shop: " + failure.getMessage())
		);
	}

	@Test
	public void testDeleteShop() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Uni<Response> response = resource.delete(1L);

		response.await().indefinitely(); // Wait for the response to be processed
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to delete shop: " + failure.getMessage())
		);

		// Verify the shop was deleted
		Uni<Response> getResponse = resource.getSingle(1L);

		getResponse.await().indefinitely(); // Wait for the response to be processed

		getResponse.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to retrieve deleted shop: " + failure.getMessage())
		);
	}

	@Test
	public void testDeleteNonExistentShop() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Uni<Response> response = resource.delete(999L); // Assuming 999 does not exist

		response.await().indefinitely(); // Wait for the response to be processed
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to delete non-existent shop: " + failure.getMessage())
		);
	}

	@Test
	public void testUpdateShop() {
		ShopResource resource = new ShopResource();
		resource.client = client;
		Uni<Response> response = resource.update(1L, "UpdatedShop", "UpdatedLocation");

		response.await().indefinitely(); // Wait for the response to be processed
		
		response.subscribe().with(
			resp -> {
				assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());
			},
			failure -> fail("Failed to update shop: " + failure.getMessage())
		);
		
		// Verify the shop was updated
		Uni<Response> getResponse = resource.getSingle(1L);

		response.await().indefinitely(); // Wait for the response to be processed
		
		getResponse.subscribe().with(
			resp -> {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
				Shop shop = (Shop) resp.getEntity();
				assertNotNull(shop);
				assertEquals("UpdatedShop", shop.name);
				assertEquals("UpdatedLocation", shop.location);
			},
			failure -> fail("Failed to retrieve updated shop: " + failure.getMessage())
		);
	}
}
