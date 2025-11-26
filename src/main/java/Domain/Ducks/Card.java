package Domain.Ducks;

import Domain.Entity;

import java.util.ArrayList;
import java.util.List;
public class Card extends Entity<Long>
{
    private String numeCard;
    private List<Duck> membri;
    private TipRata tip;


    public Card(String numeCard,TipRata tip)
    {
        this.numeCard = numeCard;
        this.membri = new ArrayList<Duck>();
        this.tip = tip;
    }

    public String getNumeCard(){
        return numeCard;
    }

    public void setNumeCard(String numeCard){
        this.numeCard = numeCard;
    }

    public List<Duck> getMembri(){
        return membri;
    }

    public void addMembri(Duck membri){
        this.membri.add(membri);
    }

    public TipRata getTip(){
        return tip;
    }
    public double getPerformantaMedieViteza()
    {
        double viteza = 0;
        for (Duck d:membri)
            viteza+=d.getViteza();
        return viteza/membri.size();
    }

    public double getPerformantaMedieRezistenta()
    {
        double rezistenta = 0;
        for (Duck d:membri)
            rezistenta+=d.getRezistenta();
        return rezistenta/membri.size();
    }
    @Override
    public String toString()
    {
        return getId() +". Aceste este cardul " + numeCard +" si in acest card avem " + tip;
    }
}
