/**
 * @author Umaima Aslam
 * @version 2025-04-04
 */


public class Flashcard {
	private String term;
	private String definition;
	private int priority;
	private static int flashcardCount = 0;
	private int id;
	
	// CONSTRUCTOR ---  

	// creates a flashcard
    // uses passed in values to initialize class variables
	public Flashcard(String term, String definition, int priority) {
		// id is initialized to flashcardCount
        // flashcardCount is incremented
        id = flashcardCount;
        flashcardCount++;
		
        // initialize the term, definition, and priority variables;
		this.term = term;
		this.definition = definition;
		this.priority = priority;
	}
	
	// GET METHODS ---
	
	// returns term
    public String getTerm() {
        return term;
    }

    // returns definition
    public String getDefinition() {
        return definition;
    }

    // returns priority
    public int getPriority() {
        return priority;
    }

    // returns id
    public int getId() {
        return id;
    }
    
    // UPDATE METHODS ---
    
 	// updates term
    public void updateTerm(String term) {
        this.term = term;
    }

    // updates definition
    public void updateDefinition(String definition) {
        this.definition = definition;
    }

    // updates id
    public void updatePriority(int priority) {
        this.priority = priority;
    }
    
    // OVERRIDE METHODS ---
    
    // displays flashcard in the following format:
    // Term: xxx
    // Definition: xxx
    // Priority: xxx
    public String toString() {
		return String.format("Term: %s\nDefinition: %s\nPriority: %s", term, definition, priority);
	}
 
}