package com.example.repo2;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final AtomicLong sequence = new AtomicLong(0);
	private final Map<Long, Product> products = new ConcurrentHashMap<>();

	@GetMapping
	public Collection<Product> list() {
		return products.values();
	}

	@GetMapping("/{id}")
	public Product get(@PathVariable Long id) {
		Product product = products.get(id);
		if (product == null) {
			throw new ResponseStatusException(NOT_FOUND, "Product not found");
		}
		return product;
	}

	@PostMapping
	public ResponseEntity<Product> create(@RequestBody Product request) {
		long id = sequence.incrementAndGet();
		Product created = new Product(id, request.name());
		products.put(id, created);
		return ResponseEntity.created(URI.create("/api/products/" + id)).body(created);
	}

	@PutMapping("/{id}")
	public Product update(@PathVariable Long id, @RequestBody Product request) {
		if (!products.containsKey(id)) {
			throw new ResponseStatusException(NOT_FOUND, "Product not found");
		}
		Product updated = new Product(id, request.name());
		products.put(id, updated);
		return updated;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (products.remove(id) == null) {
			throw new ResponseStatusException(NOT_FOUND, "Product not found");
		}
		return ResponseEntity.noContent().build();
	}
}
