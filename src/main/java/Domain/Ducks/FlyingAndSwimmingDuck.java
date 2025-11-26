package Domain.Ducks;

public class FlyingAndSwimmingDuck extends Duck implements Inotator, Zburator {
    FlyingAndSwimmingDuck(String username, String email, String password, TipRata tipRata, double viteza, double rezistenta,long idcard)
    {
        super(username, email, password, tipRata, viteza, rezistenta,idcard);
    }

    @Override
    public void zboara()
    {
        System.out.println("FlyingAndSwimmingDuck");
    }

    @Override
    public void inoata()
    {
        System.out.println("FlyingAndSwimmingDuck");
    }
}
