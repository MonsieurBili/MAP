package UI;

/**
 * Constants for UI text messages and menu options.
 * Centralizes all user-facing text for easier maintenance and internationalization.
 */
public final class UiConstants {
    
    // Main menu
    public static final String MAIN_MENU_HEADER = "Choose the option you want:";
    public static final String MENU_SEPARATOR = "----------------------------";
    public static final String OPTION_PERSON_MENU = "1. Person menu";
    public static final String OPTION_DUCK_MENU = "2. Duck menu";
    public static final String OPTION_FRIENDSHIP_MENU = "3. Friendship menu";
    public static final String OPTION_EVENTS_MENU = "4. Events menu";
    
    // Person menu
    public static final String OPTION_ADD_PERSON = "1. Add user person";
    public static final String OPTION_SHOW_PERSONS = "2. Show persons";
    public static final String OPTION_DELETE_PERSON = "3. Delete user person";
    
    // Duck menu
    public static final String OPTION_ADD_DUCK = "1. Add user duck";
    public static final String OPTION_SHOW_DUCKS = "2. Show ducks";
    public static final String OPTION_DELETE_DUCK = "3. Delete user duck";
    public static final String OPTION_ADD_FLOCK = "4. Add flock of ducks";
    public static final String OPTION_SHOW_FLOCKS = "5. Show flock of ducks";
    public static final String OPTION_ADD_DUCK_TO_FLOCK = "6. Add duck to flock";
    
    // Friendship menu
    public static final String OPTION_CREATE_FRIENDSHIP = "1. Create a friendship";
    public static final String OPTION_SHOW_FRIENDSHIPS = "2. Show friendships";
    public static final String OPTION_DELETE_FRIENDSHIP = "3. Delete a friendship";
    public static final String OPTION_CHECK_COMMUNITIES = "4. Check how many communities we have";
    public static final String OPTION_MOST_SOCIABLE = "5. Most sociable community";
    
    // Event menu
    public static final String OPTION_CREATE_EVENT = "1. Create a race event";
    public static final String OPTION_SHOW_EVENTS = "2. Show race events";
    public static final String OPTION_ADD_DUCKS_TO_EVENT = "3. Add ducks to the event";
    public static final String OPTION_START_EVENT = "4. Start an event";
    
    // Input prompts
    public static final String PROMPT_USERNAME = "Add username:";
    public static final String PROMPT_PASSWORD = "Add password:";
    public static final String PROMPT_EMAIL = "Add email:";
    public static final String PROMPT_LAST_NAME = "Add last name:";
    public static final String PROMPT_FIRST_NAME = "Add first name:";
    public static final String PROMPT_DATE_OF_BIRTH = "Add date of birth:";
    public static final String PROMPT_JOB = "Add job:";
    public static final String PROMPT_DUCK_TYPE = "Add duck type - FLYING,SWIMMING,FLYING_AND_SWIMMING";
    public static final String PROMPT_SPEED = "Add speed";
    public static final String PROMPT_RESISTANCE = "Add resistance";
    
    // Success messages
    public static final String SUCCESS_DELETION = "Deletion successful!";
    public static final String SUCCESS_ADDITION = "Addition successful!";
    public static final String SUCCESS_EVENT_CREATED = "Event created successfully!";
    
    // Error messages
    public static final String ERROR_NOT_ENOUGH_PARTICIPANTS = "You have not enough participants for this event!";
    public static final String ERROR_WRONG_DUCK_TYPE = "This duck is not a type of duck we accept in our flock!";
    public static final String ERROR_NOT_FLYING_DUCK_EVENT = "This is not an event for a FLYING DUCK";
    public static final String ERROR_NO_RACE_EVENT = "There is no race event!";
    
    // Info messages
    public static final String INFO_NO_MEMBERS = "For now we have no members";
    public static final String INFO_MEMBERS_LIST = "And we've got the members:";
    public static final String INFO_WINNERS = "Winners are:";
    
    // Prevent instantiation
    private UiConstants() {
        throw new AssertionError("UiConstants class cannot be instantiated");
    }
}
