package eaut.myapp.appplant.model;

public class Cart {
    String id_cart;
    int id_tree;
    String name_tree;
    float price_tree;
    String image_tree;
    int quantity;


    public String getId_cart() {
        return id_cart;
    }

    public void setId_cart(String id_cart) {
        this.id_cart = id_cart;
    }

    public Cart() {
    }

    public int getId_tree() {
        return id_tree;
    }

    public void setId_tree(int id_tree) {
        this.id_tree = id_tree;
    }

    public String getName_tree() {
        return name_tree;
    }

    public void setName_tree(String name_tree) {
        this.name_tree = name_tree;
    }

    public float getPrice_tree() {
        return price_tree;
    }

    public void setPrice_tree(float price_tree) {
        this.price_tree = price_tree;
    }

    public String getImage_tree() {
        return image_tree;
    }

    public void setImage_tree(String image_tree) {
        this.image_tree = image_tree;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id_tree=" + id_tree +
                ", name_tree='" + name_tree + '\'' +
                ", price_tree=" + price_tree +
                ", image_tree='" + image_tree + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
