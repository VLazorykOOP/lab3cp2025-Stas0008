import java.util.ArrayList;
import java.util.List;

interface ProductPrototype {
    ProductPrototype clone();
}

abstract class Product implements ProductPrototype, Cloneable {
    protected String name;
    protected double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public ProductPrototype clone() {
        try {
            return (ProductPrototype) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed", e);
        }
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}

class Smartphone extends Product {
    private String model;

    public Smartphone(String name, double price, String model) {
        super(name, price);
        this.model = model;
    }

    @Override
    public ProductPrototype clone() {
        try {
            Smartphone cloned = (Smartphone) super.clone();
            cloned.model = this.model; 
            return cloned;
        } catch (Exception e) {
            throw new RuntimeException("Cloning failed", e); 
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", Model: " + model;
    }
}

interface Component {
    void add(Component component);
    void remove(Component component);
    double getTotalPrice();
    String getDetails();
}

class Category implements Component {
    private String name;
    private List<Component> components = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    @Override
    public void add(Component component) {
        components.add(component);
    }

    @Override
    public void remove(Component component) {
        components.remove(component);
    }

    @Override
    public double getTotalPrice() {
        return components.stream().mapToDouble(Component::getTotalPrice).sum();
    }

    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder(name + " contains:\n");
        for (Component c : components) {
            sb.append(c.getDetails()).append("\n");
        }
        return sb.toString();
    }
}

class ProductComponent implements Component {
    private Product product;

    public ProductComponent(Product product) {
        this.product = product;
    }

    @Override
    public void add(Component component) {
        throw new UnsupportedOperationException("Cannot add to a leaf");
    }

    @Override
    public void remove(Component component) {
        throw new UnsupportedOperationException("Cannot remove from a leaf");
    }

    @Override
    public double getTotalPrice() {
        return product.getPrice();
    }

    @Override
    public String getDetails() {
        return "  - " + product.toString();
    }
}

abstract class OrderProcessor {
    public final void processOrder(List<Component> items) {
        validateOrder(items);
        calculateTotal(items);
        applyDiscount();
        finalizeOrder();
    }

    protected abstract void validateOrder(List<Component> items);
    protected abstract void applyDiscount();
    protected void calculateTotal(List<Component> items) {
        double total = items.stream().mapToDouble(Component::getTotalPrice).sum();
        System.out.println("Total price: $" + total);
    }
    protected void finalizeOrder() {
        System.out.println("Order finalized.");
    }
}

class RetailOrderProcessor extends OrderProcessor {
    @Override
    protected void validateOrder(List<Component> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order cannot be empty");
        }
        System.out.println("Validating retail order...");
    }

    @Override
    protected void applyDiscount() {
        System.out.println("Applying 5% retail discount.");
    }
}

class WholesaleOrderProcessor extends OrderProcessor {
    @Override
    protected void validateOrder(List<Component> items) {
        if (items.size() < 3) {
            throw new IllegalArgumentException("Wholesale order must contain at least 3 items");
        }
        System.out.println("Validating wholesale order...");
    }

    @Override
    protected void applyDiscount() {
        System.out.println("Applying 15% wholesale discount.");
    }
}

public class Main {
    public static void main(String[] args) {
        // Використання Prototype
        Smartphone phone1 = new Smartphone("iPhone 13", 999.99, "A14");
        Smartphone phone2 = (Smartphone) phone1.clone();
        phone2.setPrice(949.99);
        System.out.println("Original: " + phone1);
        System.out.println("Cloned: " + phone2);

        // Використання Composite
        Category electronics = new Category("Electronics");
        Category phones = new Category("Phones");
        phones.add(new ProductComponent(phone1));
        phones.add(new ProductComponent(phone2));
        electronics.add(phones);
        electronics.add(new ProductComponent(new Smartphone("Samsung S21", 799.99, "Exynos")));
        System.out.println(electronics.getDetails());
        System.out.println("Total price of Electronics: $" + electronics.getTotalPrice());

        // Використання Template Method
        List<Component> orderItems = new ArrayList<>();
        orderItems.add(new ProductComponent(phone1));
        orderItems.add(new ProductComponent(phone2));

        RetailOrderProcessor retail = new RetailOrderProcessor();
        retail.processOrder(orderItems);

        WholesaleOrderProcessor wholesale = new WholesaleOrderProcessor();
        orderItems.add(new ProductComponent(new Smartphone("Google Pixel", 699.99, "Tensor")));
        wholesale.processOrder(orderItems);
    }
}