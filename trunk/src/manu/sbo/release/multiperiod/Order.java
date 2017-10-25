package manu.sbo.release.multiperiod;

import java.util.ArrayList;
import java.util.List;

public class Order {
	List<Product> products;

	public Order() {
		products = new ArrayList<Product>();
	}

	public void addProduct(long earliestStartDate,long dueDate, 
			long minReleaseInterval, int jobNum) {
		products.add(new Product(dueDate, earliestStartDate,
				minReleaseInterval, jobNum,products.size()+1));
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
