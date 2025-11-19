package Domain.Ducks;

public class SwimmingDuck extends Duck implements Inotator {

    SwimmingDuck(String username, String email, String password, TipRata tipRata, double viteza, double rezistenta)
    {
        super(username, email, password, tipRata, viteza, rezistenta);
    }

    @Override
    public void inoata()
    {
        System.out.println("Aceasta ratusca inoata");
    }
}
