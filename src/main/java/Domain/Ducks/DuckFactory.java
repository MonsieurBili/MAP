package Domain.Ducks;


import Domain.UserFactory;

public class DuckFactory implements UserFactory<Duck> {
    private String username;
    private String email;
    private String password;
    private TipRata tipRata;
    private double viteza;
    private double rezistenta;
    private long idcard;
    private static final DuckFactory INSTANCE = new DuckFactory();

    public DuckFactory() {}

    public static DuckFactory getInstance()
    {
        return INSTANCE;
    }
    public void setData(String username,String email,String password,TipRata tipRata, double viteza, double rezistenta,long idcard)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.tipRata = tipRata;
        this.viteza = viteza;
        this.rezistenta = rezistenta;
        this.idcard = idcard;
    }
    @Override
    public Duck createUser() {
        if (tipRata == TipRata.SWIMMING)
            return new SwimmingDuck(username,email,password,tipRata,viteza, rezistenta,idcard);
        if (tipRata == TipRata.FLYING)
            return new FlyingDuck(username,email,password,tipRata,viteza,rezistenta,idcard);
        if (tipRata == TipRata.FLYING_AND_SWIMMING)
            return new FlyingAndSwimmingDuck(username,email,password,tipRata,viteza,rezistenta,idcard);
        return null;
    }
}
