package UI;

import Domain.*;
import Domain.Ducks.*;
import Domain.Person.Persoana;
import Domain.Person.PersonFactory;
import Exception.ValidationException;
import Service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Ui {
    ServicePerson servicePersoana;
    ServiceDuck serviceDuck;
    ServiceFriendship serviceFriendship;
    PersonFactory personFactory = new PersonFactory();
    DuckFactory duckFactory = new DuckFactory();
    ServiceStatistics serviceStatistics;
    ServiceCard serviceCard;
    ServiceRaceEvent serviceRaceEvent;
    public Ui(ServicePerson servicePersoana, ServiceDuck serviceDuck, ServiceFriendship serviceFriendship, ServiceStatistics serviceStatistics, ServiceCard serviceCard,ServiceRaceEvent serviceRaceEvent)
    {
        this.servicePersoana = servicePersoana;
        this.serviceDuck = serviceDuck;
        this.serviceFriendship = serviceFriendship;
        this.serviceStatistics = serviceStatistics;
        this.serviceCard = serviceCard;
        this.serviceRaceEvent = serviceRaceEvent;
    }
    public static void afiseazaMeniu()
    {
        System.out.println("Choose the option you want:");
        System.out.println("----------------------------");
        System.out.println("1. Person menu");
        System.out.println("2. Duck menu");
        System.out.println("3. Friendship menu");
        System.out.println("4. Events menu");
    }

    public static void afiseazaMeniuPersoana()
    {
        System.out.println("Choose the option you want:");
        System.out.println("----------------------------");
        System.out.println("1. Add user person");
        System.out.println("2. Show persons");
        System.out.println("3. Delete user person");
    }

    public static void afiseazaMeniuDuck()
    {
        System.out.println("Choose the option you want:");
        System.out.println("----------------------------");
        System.out.println("1. Add user duck");
        System.out.println("2. Show ducks");
        System.out.println("3. Delete user duck");
        System.out.println("4. Add flock of ducks");
        System.out.println("5. Show flock of ducks");
        System.out.println("6. Add duck to flock");
    }

    public static void afiseazaMeniuFriendship()
    {
        System.out.println("Choose the option you want:");
        System.out.println("----------------------------");
        System.out.println("1. Create a friendship");
        System.out.println("2. Show friendships");
        System.out.println("3. Delete a friendship");
        System.out.println("4. Check how many communites we have");
        System.out.println("5. Most sociable community");
    }

    public static void afiseazaMeniuEvent()
    {
        System.out.println("Choose the option you want:");
        System.out.println("----------------------------");
        System.out.println("1. Create a race event");
        System.out.println("2. Show race events");
        System.out.println("3. Add ducks to the event");
        System.out.println("4. Start an event");
        System.out.println("5. Subscribe to an event");
    }

    public void afiseazaCard()
    {
        Iterable<Card> allflocks = serviceCard.findAll();
        for (Card card : allflocks)
        {
            System.out.println(card.toString());
            for (Duck d : card.getMembri())
            {
                if (d.getIdCard() == card.getId())
                    System.out.println(d.toString());
            }
        }
    }

    public void adaugaRataInCard()
    {
        System.out.println("Choose the duck you want to add to a flock");
        Scanner sc = new Scanner(System.in);
        long idRata = sc.nextLong();
        System.out.println("Please add the id of flocks you want to add to");
        afiseazaCard();
        long idCard = sc.nextLong();
        Duck d = serviceDuck.findOne(idRata);
        Card card = serviceCard.findOne(idCard);
        if (!card.getTip().equals(d.getTipRata()))
        {
            System.out.println("This duck is not a type of duck we accept in our flock!");
        }
        else {
            d.setIdCard(idCard);
            serviceDuck.update(d);
            card.addMembri(d);
            serviceCard.update(card);
            System.out.println("Adaugarea reusita cu succes !");
        }
    }

    public void creeazaRaceEvent()
    {
        System.out.println("What's the name of the event:");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("What's the location of the event:");
        String location = sc.nextLine();
        System.out.println("What's the number of lanes you wish for the ducks to race on");
        int lanes = sc.nextInt();
        RaceEvent event = new RaceEvent(name,location);
        serviceRaceEvent.save(event);
        for (int i = 0; i < lanes; i++)
        {
            double cl = sc.nextDouble();
            event.addculoar(cl);
        }
        System.out.println("Event created successfully!");
    }

    void afiseazaEventuri()
    {
        Iterable<RaceEvent> listEvent = serviceRaceEvent.findAll();
        if(!listEvent.iterator().hasNext())
        {
            System.out.println("There is no race event!");
            return;
        }
        for (RaceEvent e : listEvent)
            System.out.println( e.toString());
    }

    public void addDucksToEvent()
    {
        System.out.println("What's the id of the event:");
        Scanner sc = new Scanner(System.in);
        long idEvent = sc.nextLong();
        RaceEvent event = serviceRaceEvent.findOne(idEvent);
        System.out.println("How many ducks would you like to add to the event?");
        int num = sc.nextInt();
        afiseazaRate();
        System.out.println("Enter the id of the ducks you want to add");
        for (int i=0;i<num;i++)
        {
            long choice = sc.nextLong();
            Duck d = serviceDuck.findOne(choice);
            if (d.getTipRata() == TipRata.SWIMMING) {
                SwimmingDuck dp = (SwimmingDuck)d;
                event.addParticipant(dp);
                System.out.println("Added the swimming duck!");
            }
            else
            {
                System.out.println("This is not an event for a FLYING DUCK");
            }
        }
    }

    public void afiseazaRate()
    {
        Iterable<Duck> allDucks = serviceDuck.findAll();
        for (Duck duck1 : allDucks) {
            System.out.println(duck1);
        }
    }

    public void startEvent()
    {
        afiseazaEventuri();
        System.out.println("What event would you like to start?");
        Scanner sc = new Scanner(System.in);
        Long id = sc.nextLong();
        RaceEvent event = serviceRaceEvent.findOne(id);
        if (event.getCuloare().size() > event.getParticipants().size())
        {
            System.out.println("You have not enough participants for this event!");
            return;
        }
        else
        {
            List<SwimmingDuck> winners = serviceRaceEvent.solve(event);
            System.out.println("Winners are:");
            int nr = 0;
            double besttime = 0;
            for (SwimmingDuck d : winners)
            {
                besttime += event.getCuloare().get(nr)*2/d.getViteza();
                System.out.println(d.toString());
            }
            System.out.println(besttime);
        }
    }

    public void subscribeEvent()
    {
        System.out.println("What event would you like to subscribe?");
        Scanner sc = new Scanner(System.in);
        Long id = sc.nextLong();
        RaceEvent event = serviceRaceEvent.findOne(id);
        System.out.println("Which user should subscribe?");
        Long idUser = sc.nextLong();
        Persoana user = null;
        try
        {
            user = servicePersoana.findOne(idUser);

        }
        catch (Exception e)
        {
        }
        if (user != null)
            event.subscribe(user);
    }
    public void run()
    {
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            afiseazaMeniu();
            switch (sc.nextInt())
            {
                case 1:
                    afiseazaMeniuPersoana();
                    Scanner optiunePersoana = new Scanner(System.in);
                    int optiune = optiunePersoana.nextInt();
                    switch (optiune)
                    {
                        case 1:
                            System.out.println("Add username:");
                            String username = sc.next();
                            System.out.println("Add password:");
                            String password = sc.next();
                            System.out.println("Add email:");
                            String email = sc.next();
                            System.out.println("Add last name:");
                            String nume = sc.next();
                            System.out.println("Add first name:");
                            String prenume = sc.next();
                            System.out.println("Add date of birth:");
                            String dataNasteriiString = sc.next();
                            LocalDate dataNasterii = LocalDate.parse(dataNasteriiString);
                            System.out.println("Add job:");
                            String ocupatie = sc.next();
                            personFactory.setData(username,email,password,nume,prenume,dataNasterii,ocupatie);
                            Persoana person = personFactory.createUser();
                            try {
                                servicePersoana.save(person);
                            }
                            catch (ValidationException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 2:
                            Iterable<Persoana> allpersons = servicePersoana.findAll();
                            for (Persoana persoana : allpersons) {
                                System.out.println(persoana);
                            }
                            break;
                        case 3:
                            System.out.println("Add the id of the person you wish to delete");
                            servicePersoana.delete(sc.nextLong());
                            System.out.println("Deletion succesfull!");

                    }
                    break;
                case 2:
                    afiseazaMeniuDuck();
                    Scanner optiuneDucks = new Scanner(System.in);
                    int optiuneDuck = optiuneDucks.nextInt();
                    switch (optiuneDuck)
                    {
                        case 1:
                            System.out.println("Add username:");
                            String username = sc.next();
                            System.out.println("Add password:");
                            String password = sc.next();
                            System.out.println("Add email:");
                            String email = sc.next();
                            System.out.println("Add duck type - FLYING,SWIMMING,FLYING_AND_SWIMMING");
                            String tipurlate = sc.next();
                            System.out.println("Add speed");
                            double viteza = sc.nextDouble();
                            System.out.println("Add resistance");
                            double rezistenta = sc.nextDouble();
                            TipRata tipRata;
                            if (tipurlate.equals("FLYING"))
                                tipRata = TipRata.FLYING;
                            else if (tipurlate.equals("SWIMMING"))
                                tipRata = TipRata.SWIMMING;
                            else
                                tipRata = TipRata.FLYING_AND_SWIMMING;
                            duckFactory.setData(username,email,password,tipRata,viteza,rezistenta);
                            Duck duck = duckFactory.createUser();
                            try {
                                serviceDuck.save(duck);
                            }
                            catch (ValidationException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 2:
                            afiseazaRate();
                            break;
                        case 3:
                            System.out.println("Add the id of the duck you wish to delete");
                            serviceDuck.delete(sc.nextLong());
                            System.out.println("Deletion succesfull!");
                            break;
                        case 4:
                            System.out.println("Add the name of the flock you wish to create");
                            String numeCard = sc.next();
                            System.out.println("Add the type of the ducks you wish to have in that flock : FLYING,SWIMMING,FLYING_AND_SWIMMING");
                            String tip  = sc.next();
                            if (tip.equals("FLYING")) {
                                Card card = new Card(numeCard,TipRata.FLYING);
                                serviceCard.save(card);
                            }
                            else if (tip.equals("SWIMMING")) {
                                Card card = new Card(numeCard,TipRata.SWIMMING);
                                serviceCard.save(card);
                            }
                            else {
                                Card card = new Card(numeCard,TipRata.FLYING_AND_SWIMMING);
                                serviceCard.save(card);
                            }
                            break;
                        case 5:
                            afiseazaCard();
                            break;
                        case 6:
                            adaugaRataInCard();
                            break;
                    }
                    break;
                case 3:
                    afiseazaMeniuFriendship();
                    Scanner optiuneFriendship = new Scanner(System.in);
                    int optiunefriendship = optiuneFriendship.nextInt();
                    switch (optiunefriendship) {
                        case 1:
                            Iterable<Duck> allDucks = serviceDuck.findAll();
                            for (Duck duck1 : allDucks) {
                                System.out.println(duck1);
                            }
                            Iterable<Persoana> allpersons = servicePersoana.findAll();
                            for (Persoana persoana : allpersons) {
                                System.out.println(persoana);
                            }
                            System.out.println("Add the id of the first user:");
                            long id1 = optiuneFriendship.nextInt();
                            System.out.println("Add the id of the second user:");
                            long id2 = optiuneFriendship.nextInt();
                            User user1 = serviceDuck.findOne(id1);
                            User user2 = serviceDuck.findOne(id2);
                            if (user1 == null) {
                                user1 = servicePersoana.findOne(id1);
                            }
                            if (user2 == null)
                            {
                                user2 = servicePersoana.findOne(id2);
                            }
                            Friendship friendship = new Friendship(user1,user2);
                            serviceFriendship.save(friendship);
                            break;
                        case 2:
                            Iterable<Friendship> allFriendships = serviceFriendship.findAll();
                            for (Friendship friendship1 : allFriendships) {
                                System.out.println(friendship1);
                            }
                            break;
                        case 3:
                            System.out.println("Enter the id of the friendship you want to delete");
                            long id = optiuneFriendship.nextInt();
                            serviceFriendship.delete(id);
                            break;
                        case 4:
                            System.out.println(serviceStatistics.CommunityNumber());
                            break;
                        case 5:
                            serviceStatistics.showComponentWithMaxDiameter();
                            break;
                    }
                    break;
                case 4:
                    afiseazaMeniuEvent();
                    Scanner optiuneEvent = new Scanner(System.in);
                    int optiunevent = optiuneEvent.nextInt();
                    switch (optiunevent) {
                        case 1:
                            creeazaRaceEvent();
                            break;
                        case 2:
                            afiseazaEventuri();
                            break;
                        case 3:
                            addDucksToEvent();
                            break;
                        case 4:
                            startEvent();
                            break;
                        case 5:
                            subscribeEvent();
                            break;
                    }
            }
        }
    }
}
