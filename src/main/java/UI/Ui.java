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

/**
 * User Interface class for the application.
 * Provides console-based interaction for managing persons, ducks, friendships, and race events.
 */
public class Ui {
    
    private final ServicePerson servicePersoana;
    private final ServiceDuck serviceDuck;
    private final ServiceFriendship serviceFriendship;
    private final PersonFactory personFactory = new PersonFactory();
    private final DuckFactory duckFactory = new DuckFactory();
    private final ServiceStatistics serviceStatistics;
    private final ServiceCard serviceCard;
    private final ServiceRaceEvent serviceRaceEvent;
    
    /**
     * Constructs a new UI with the specified services.
     *
     * @param servicePersoana    service for person management
     * @param serviceDuck        service for duck management
     * @param serviceFriendship  service for friendship management
     * @param serviceStatistics  service for statistics computation
     * @param serviceCard        service for flock/card management
     * @param serviceRaceEvent   service for race event management
     */
    public Ui(ServicePerson servicePersoana, ServiceDuck serviceDuck, ServiceFriendship serviceFriendship, 
              ServiceStatistics serviceStatistics, ServiceCard serviceCard, ServiceRaceEvent serviceRaceEvent) {
        this.servicePersoana = servicePersoana;
        this.serviceDuck = serviceDuck;
        this.serviceFriendship = serviceFriendship;
        this.serviceStatistics = serviceStatistics;
        this.serviceCard = serviceCard;
        this.serviceRaceEvent = serviceRaceEvent;
    }
    /**
     * Displays the main menu options to the user.
     */
    public static void displayMainMenu() {
        System.out.println(UiConstants.MAIN_MENU_HEADER);
        System.out.println(UiConstants.MENU_SEPARATOR);
        System.out.println(UiConstants.OPTION_PERSON_MENU);
        System.out.println(UiConstants.OPTION_DUCK_MENU);
        System.out.println(UiConstants.OPTION_FRIENDSHIP_MENU);
        System.out.println(UiConstants.OPTION_EVENTS_MENU);
    }

    /**
     * Displays the person management menu options.
     */
    public static void displayPersonMenu() {
        System.out.println(UiConstants.MAIN_MENU_HEADER);
        System.out.println(UiConstants.MENU_SEPARATOR);
        System.out.println(UiConstants.OPTION_ADD_PERSON);
        System.out.println(UiConstants.OPTION_SHOW_PERSONS);
        System.out.println(UiConstants.OPTION_DELETE_PERSON);
    }

    /**
     * Displays the duck management menu options.
     */
    public static void displayDuckMenu() {
        System.out.println(UiConstants.MAIN_MENU_HEADER);
        System.out.println(UiConstants.MENU_SEPARATOR);
        System.out.println(UiConstants.OPTION_ADD_DUCK);
        System.out.println(UiConstants.OPTION_SHOW_DUCKS);
        System.out.println(UiConstants.OPTION_DELETE_DUCK);
        System.out.println(UiConstants.OPTION_ADD_FLOCK);
        System.out.println(UiConstants.OPTION_SHOW_FLOCKS);
        System.out.println(UiConstants.OPTION_ADD_DUCK_TO_FLOCK);
    }

    /**
     * Displays the friendship management menu options.
     */
    public static void displayFriendshipMenu() {
        System.out.println(UiConstants.MAIN_MENU_HEADER);
        System.out.println(UiConstants.MENU_SEPARATOR);
        System.out.println(UiConstants.OPTION_CREATE_FRIENDSHIP);
        System.out.println(UiConstants.OPTION_SHOW_FRIENDSHIPS);
        System.out.println(UiConstants.OPTION_DELETE_FRIENDSHIP);
        System.out.println(UiConstants.OPTION_CHECK_COMMUNITIES);
        System.out.println(UiConstants.OPTION_MOST_SOCIABLE);
    }

    /**
     * Displays the event management menu options.
     */
    public static void displayEventMenu() {
        System.out.println(UiConstants.MAIN_MENU_HEADER);
        System.out.println(UiConstants.MENU_SEPARATOR);
        System.out.println(UiConstants.OPTION_CREATE_EVENT);
        System.out.println(UiConstants.OPTION_SHOW_EVENTS);
        System.out.println(UiConstants.OPTION_ADD_DUCKS_TO_EVENT);
        System.out.println(UiConstants.OPTION_START_EVENT);
    }

    /**
     * Displays all flocks (cards) and their members.
     */
    public void displayFlocks() {
        Iterable<Card> allFlocks = serviceCard.findAll();
        for (Card card : allFlocks) {
            System.out.println(card.toString());
            if (!card.getMembri().isEmpty()) {
                System.out.println(UiConstants.INFO_MEMBERS_LIST);
                for (Duck d : card.getMembri()) {
                    System.out.println(d.toString());
                }
            } else {
                System.out.println(UiConstants.INFO_NO_MEMBERS);
            }
        }
    }

    /**
     * Adds a duck to a flock.
     * Prompts the user for duck and flock IDs and validates the duck type.
     */
    public void addDuckToFlock() {
        System.out.println("Choose the duck you want to add to a flock");
        Scanner sc = new Scanner(System.in);
        long duckId = sc.nextLong();
        
        System.out.println("Please add the id of flocks you want to add to");
        displayFlocks();
        
        long cardId = sc.nextLong();
        Duck duck = serviceDuck.findOne(duckId);
        Card card = serviceCard.findOne(cardId);
        
        if (duck == null || card == null) {
            System.out.println("Invalid duck or flock ID");
            return;
        }
        
        if (!card.getTip().equals(duck.getTipRata())) {
            System.out.println(UiConstants.ERROR_WRONG_DUCK_TYPE);
        } else {
            card.addMembri(duck);
            System.out.println(UiConstants.SUCCESS_ADDITION);
        }
    }

    /**
     * Creates a new race event with user-specified parameters.
     */
    public void createRaceEvent() {
        System.out.println("What's the name of the event:");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        
        System.out.println("What's the location of the event:");
        String location = sc.nextLine();
        
        System.out.println("What's the number of lanes you wish for the ducks to race on");
        int lanes = sc.nextInt();
        
        RaceEvent event = new RaceEvent(name, location);
        serviceRaceEvent.save(event);
        
        for (int i = 0; i < lanes; i++) {
            double laneLength = sc.nextDouble();
            event.addculoar(laneLength);
        }
        
        System.out.println(UiConstants.SUCCESS_EVENT_CREATED);
    }

    /**
     * Displays all race events.
     */
    void displayEvents() {
        Iterable<RaceEvent> listEvent = serviceRaceEvent.findAll();
        if (!listEvent.iterator().hasNext()) {
            System.out.println(UiConstants.ERROR_NO_RACE_EVENT);
            return;
        }
        for (RaceEvent e : listEvent) {
            System.out.println(e.toString());
        }
    }

    /**
     * Adds ducks to a race event as participants.
     */
    public void addDucksToEvent() {
        System.out.println("What's the id of the event:");
        Scanner sc = new Scanner(System.in);
        long eventId = sc.nextLong();
        RaceEvent event = serviceRaceEvent.findOne(eventId);
        
        if (event == null) {
            System.out.println("Event not found");
            return;
        }
        
        System.out.println("How many ducks would you like to add to the event?");
        int num = sc.nextInt();
        displayDucks();
        
        System.out.println("Enter the id of the ducks you want to add");
        for (int i = 0; i < num; i++) {
            long duckId = sc.nextLong();
            Duck duck = serviceDuck.findOne(duckId);
            
            if (duck == null) {
                System.out.println("Duck not found: " + duckId);
                continue;
            }
            
            if (duck.getTipRata() == TipRata.SWIMMING) {
                SwimmingDuck swimmingDuck = (SwimmingDuck) duck;
                event.addParticipant(swimmingDuck);
                System.out.println("Added the swimming duck!");
            } else {
                System.out.println(UiConstants.ERROR_NOT_FLYING_DUCK_EVENT);
            }
        }
    }

    /**
     * Displays all ducks in the system.
     */
    public void displayDucks() {
        Iterable<Duck> allDucks = serviceDuck.findAll();
        for (Duck duck : allDucks) {
            System.out.println(duck);
        }
    }

    /**
     * Starts a race event and displays the winners.
     */
    public void startEvent() {
        displayEvents();
        System.out.println("What event would you like to start?");
        Scanner sc = new Scanner(System.in);
        Long id = sc.nextLong();
        RaceEvent event = serviceRaceEvent.findOne(id);
        
        if (event == null) {
            System.out.println("Event not found");
            return;
        }
        
        if (event.getCuloare().size() > event.getParticipants().size()) {
            System.out.println(UiConstants.ERROR_NOT_ENOUGH_PARTICIPANTS);
            return;
        }
        
        List<SwimmingDuck> winners = serviceRaceEvent.solve(event);
        System.out.println(UiConstants.INFO_WINNERS);
        
        double totalTime = 0;
        int laneIndex = 0;
        for (SwimmingDuck duck : winners) {
            totalTime += event.getCuloare().get(laneIndex) * 2 / duck.getViteza();
            System.out.println(duck.toString());
            laneIndex++;
        }
        System.out.println("Total time: " + totalTime);
    }
    /**
     * Main UI loop that displays menus and handles user input.
     */
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            displayMainMenu();
            switch (sc.nextInt()) {
                case 1:
                    displayPersonMenu();
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
                    displayDuckMenu();
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
                            displayDucks();
                            break;
                        case 3:
                            System.out.println("Add the id of the duck you wish to delete");
                            serviceDuck.delete(sc.nextLong());
                            System.out.println(UiConstants.SUCCESS_DELETION);
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
                            displayFlocks();
                            break;
                        case 6:
                            addDuckToFlock();
                            break;
                    }
                    break;
                case 3:
                    displayFriendshipMenu();
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
                case 4:
                    displayEventMenu();
                    Scanner optiuneEvent = new Scanner(System.in);
                    int optiunevent = optiuneEvent.nextInt();
                    switch (optiunevent) {
                        case 1:
                            createRaceEvent();
                            break;
                        case 2:
                            displayEvents();
                            break;
                        case 3:
                            addDucksToEvent();
                            break;
                        case 4:
                            startEvent();
                    }
            }
        }
    }
}
