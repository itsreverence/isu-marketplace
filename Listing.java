import java.util.UUID;

/**
 * Listing class to represent a listing in the application
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 */
public class Listing {
    private UUID id; // ID of the listing
    private UUID userId; // ID of the user who created the listing
    private String title; // Title of the listing
    private String description; // Description of the listing
    private float price; // Price of the listing

    /**
     * Constructor for the Listing class
     * 
     * @param id          The ID of the listing
     * @param userId      The ID of the user who created the listing
     * @param title       The title of the listing
     * @param description The description of the listing
     * @param price       The price of the listing
     */
    public Listing(UUID id, UUID userId, String title, String description, float price) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    /**
     * Getter for the ID of the listing
     * 
     * @return The ID of the listing
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Getter for the ID of the user who created the listing
     * 
     * @return The ID of the user who created the listing
     */
    public UUID getUserId() {
        return this.userId;
    }

    /**
     * Getter for the title of the listing
     * 
     * @return The title of the listing
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for the description of the listing
     * 
     * @return The description of the listing
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Getter for the price of the listing
     * 
     * @return The price of the listing
     */
    public float getPrice() {
        return this.price;
    }

    /**
     * Setter for the title of the listing
     * 
     * @param title The new title of the listing
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setter for the description of the listing
     * 
     * @param description The new description of the listing
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the price of the listing
     * 
     * @param price The new price of the listing
     */
    public void setPrice(float price) {
        this.price = price;
    }
}
