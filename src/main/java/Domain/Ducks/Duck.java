package Domain.Ducks;

import Domain.User;

/**
 * Abstract base class for all duck types in the system.
 * Extends User to provide duck-specific attributes like type, speed, and resistance.
 */
public abstract class Duck extends User {
    
    private TipRata tipRata;
    private double viteza;
    private double rezistenta;

    /**
     * Constructs a new Duck with the specified attributes.
     *
     * @param username   the username for this duck
     * @param email      the email address for this duck
     * @param password   the password for this duck
     * @param tipRata    the type of duck (FLYING, SWIMMING, or FLYING_AND_SWIMMING)
     * @param viteza     the speed of the duck
     * @param rezistenta the resistance/endurance of the duck
     */
    public Duck(String username, String email, String password, TipRata tipRata, double viteza, double rezistenta) {
        super(username, email, password);
        this.tipRata = tipRata;
        this.viteza = viteza;
        this.rezistenta = rezistenta;
    }

    /**
     * Gets the type of this duck.
     *
     * @return the duck type
     */
    public TipRata getTipRata() {
        return tipRata;
    }

    /**
     * Gets the speed of this duck.
     *
     * @return the speed value
     */
    public double getViteza() {
        return viteza;
    }

    /**
     * Gets the resistance/endurance of this duck.
     *
     * @return the resistance value
     */
    public double getRezistenta() {
        return rezistenta;
    }

    @Override
    public String toString() {
        return "Duck{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", tipRata=" + tipRata +
                ", viteza=" + viteza +
                ", rezistenta=" + rezistenta +
                '}';
    }
}
