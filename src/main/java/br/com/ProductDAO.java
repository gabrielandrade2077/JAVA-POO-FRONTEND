package br.com;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para gerenciar as operações CRUD de produtos.
 * Utiliza uma lista em memória para simular um banco de dados.
 */
public class ProductDAO {
    private static final List<Product> products = new ArrayList<>();
    private static int nextId = 1;

    // Create
    public void addProduct(Product product) {
        product.setId(nextId++);
        products.add(product);
    }

    // Read
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Update
    public void updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                return;
            }
        }
    }

    // Delete
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
    }
}
