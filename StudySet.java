/**
 * @author Umaima Aslam
 * @version 2025-04-08
 */


/* IMPORT CLASSES */
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;


public class StudySet {
	private String title;
	private String description;
	private ArrayList<Flashcard> flashcards;
	private static int setCount = 0;
	private int id;
	private Date dateCreated;
	private Date dateAccessed;

	// CONSTRUCTORS ---

	// constructor 1: create a study set
	// uses passed in values to initialize class variables
	public StudySet(String title, String description, ArrayList<Flashcard> flashcards) {
		// id is initialized to setCount
		// setCount is incremented
		id = setCount;
		setCount++;

		// initialize the title, description, and flashcards variables
		this.title = title;
		this.description = description;
		this.flashcards = flashcards;

		// "new Date() creates a Date object that represents 
		// the current date and time at the moment of instantiation" 
		this.dateCreated = new Date();  
		this.dateAccessed = new Date();  
	}

	// constructor 2: has the ability to initialize dateCreated and dateAccessed variables
	public StudySet(String title, String description, ArrayList<Flashcard> flashcards, Date dateCreated, Date dateAccessed) {
		// id is initialized to setCount
		// setCount is incremented 
		id = setCount;
		setCount++;

		// initialize the title, description, flashcards, dateCreated, and dateAccessed variables
		this.title = title;
		this.description = description;
		this.flashcards = flashcards;
		this.dateCreated = dateCreated;
		this.dateAccessed = dateAccessed;
	}

	// GET METHODS ---

	// returns title
	public String getTitle() {
		return title;
	}

	// returns description
	public String getDescription() {
		return description;
	}

	// returns the flashcards arraylist
	public ArrayList<Flashcard> getFlashcards() {
		return flashcards;
	}

	// returns a flashcard in the flashcards arraylist from the given flashcard id
	public Flashcard getFlashcard(int id) {
		// loop through the flashcards in the arraylist
		// if a flashcard has an id that matches the passed in id, return it
		// otherwise return null
		for (int i = 0; i < flashcards.size(); i++) {
			if ((flashcards.get(i)).getId() == id ) {
				return flashcards.get(i);
			}
		}

		return null;
	}

	// returns the size of the flashcards arraylist (# of flashcards in the studyset)
	public int getSize() {
		return (flashcards.size());
	}

	// returns id
	public int getId() {
		return id;
	}

	// returns dateCreated
	public Date getDateCreated() {
		return dateCreated;
	}

	// returns dateAccessed
	public Date getDateAccessed() {
		return dateAccessed;
	}

	// UPDATE METHODS --- 

	// updates title 
	public void updateTitle(String title) {
		this.title = title;
		// call updateDateAccessed
		//updateDateAccessed();
	}

	// updates Description 
	public void updateDescription(String description) {
		this.description = description; 
		// call updateDateAccessed
		//updateDateAccessed();
	}

	// adds a flashcard to the flashcards arraylist 
	public void addFlashcard(Flashcard flashcard) {
		// add passed in flashcard to flashcards arraylist;
		flashcards.add(flashcard);
		// call updateDateAccessed
		//updateDateAccessed();
	}

	// removes a flashcard from the flashcards arraylist
	public void removeFlashcard(Flashcard flashcard) {
		for (int i = 0; i < flashcards.size(); i++) {
			if ((flashcards.get(i)).getId() == flashcard.getId()) {
				flashcards.remove(i);
				// call updateDateAccessed
				//updateDateAccessed();

			}
		}

	}

	// updates term, definition, or priority of a passed in flashcard
	// only updates variables that are provided (user may not want to update all 3 variables)
	public void updateFlashcard(Flashcard flashcard, String term, String definition, int priority) {
		// if a term is provided (no null/empty string) update it
		if (term != null && !term.isEmpty()) {
			flashcard.updateTerm(term);
		}
		// if a definition is provided (no null/empty string) update it
		if (definition != null && !definition.isEmpty()) {
			flashcard.updateDefinition(definition);
		}
		// [?] we can assign -1 or some other invalid priority value to indicate that the 
		// user doesn't want to change the priority 
		// if a priority is provided, update it
		if (priority != -1){
			flashcard.updatePriority(priority);
		}

		// call updateDateAccessed
		//updateDateAccessed();

	}

	// updates dateAccessed
	public void updateDateAccessed(){
		this.dateAccessed = new Date();
	}

	// METHODS ---

	// generates a random re-ordering of the flashcards arraylist ("shuffles" the cards)
	// preserves priority of the cards (shuffles within each priority)
	public ArrayList<Flashcard> shuffleFlashcards(){
		// create an array list to store each priority of flashcard (priority 1, 2, and 3)
		ArrayList<Flashcard> P1Cards = new ArrayList<>();
		ArrayList<Flashcard> P2Cards = new ArrayList<>();
		ArrayList<Flashcard> P3Cards = new ArrayList<>();

		// go through the flashcards arraylist and add flashcards into 
		// the appropriate priority arraylist
		for (int i = 0; i < flashcards.size(); i++) {
			Flashcard curr = flashcards.get(i);
			int currPriority = curr.getPriority();
			if (currPriority == 1){
				P1Cards.add(curr);
			} else if (currPriority == 2) {
				P2Cards.add(curr);
			} else {
				P3Cards.add(curr);
			}
		}

		// use Collections.shuffle to generate a random order for each of the priority lists
		// (Collections.shuffle sorts in place)
		Collections.shuffle(P1Cards);
		Collections.shuffle(P2Cards);
		Collections.shuffle(P3Cards);

		// Create a new arraylist to merge and hold each of the 3 shuffled priority lists 
		ArrayList<Flashcard> shuffled = new ArrayList<>();
		// use addAll to merge
		shuffled.addAll(P1Cards);
		shuffled.addAll(P2Cards);
		shuffled.addAll(P3Cards);

		return shuffled;
	}

	// OVERRIDE METHODS ---

	public String toString() {
		return String.format("ID:%d\nTitle: %s\nDescription:%s\nFlashcards:\n%s\n", id, title, description, flashcards.toString());
	}

}
