package Domain.Ducks;


import Domain.User;

public abstract class Duck extends User {
    private TipRata tipRata;
    private double viteza;
    private double rezistenta;
     private Long idCard;

    public Duck(String username,String email,String password,TipRata tipRata,double viteza,double rezistenta){
        super(username,email,password);
        this.tipRata = tipRata;
        this.viteza = viteza;
        this.rezistenta = rezistenta;
    }

    public Long getIdCard()
    {
        return  idCard;
    }

    public void setIdCard(Long idCard)
    {
        this.idCard = idCard;
    }

    public TipRata getTipRata() {
        return tipRata;
    }

    public double getViteza() {
        return viteza;
    }

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
