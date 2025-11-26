package Domain.Ducks;

public class FlyingDuck extends Duck implements Zburator {
    public FlyingDuck(String username, String email, String password, TipRata tipRata, double viteza, double rezistenta,long idcard)
    {
        super(username, email, password, tipRata, viteza, rezistenta,idcard);
    }

    @Override
    public void zboara()
    {
        System.out.println("Aceasta ratusca inoata");
    }
}
