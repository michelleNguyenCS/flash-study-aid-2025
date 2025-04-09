/**
 * @author Michelle Nguyen
 * @version 2025-04-07
 */


/* IMPORT STATEMENTS */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class Database {
	/* VARIABLES */
	private static ArrayList<StudySet> sets;
	private static ArrayList<Note> notes;
	
	/* GET METHODS*/
	public static ArrayList<StudySet> getSets() { return sets; }
	public static ArrayList<Note> getNotes() { return notes; }
	
	
	/* STUDYSET METHODS */
	
	
	/**
	 * Loads set data from the database
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("deprecation")
	public static void loadSets() throws FileNotFoundException {
		sets = new ArrayList<>();
		File setData = new File("study_set_database");
		Scanner in = new Scanner(setData);
		
		String title = "";
		String description = "";
		ArrayList<Flashcard> flashcards = new ArrayList<>();
		Date dateCreated;
		Date dateAccessed;
	
		while (in.hasNextLine()) {
			String line = in.nextLine();
			
			// Indicates new set
			if (line.equals("Title;Description;Flashcards;DateCreated;DateAccessed")) {
				// Get details of set
				title = in.nextLine();
				description = in.nextLine();
				
				line = in.nextLine();	// Skip "FlashcardStart:"
				line = in.nextLine();	// First term
				
				// Get all flashcards
				while (!line.equals("FlashcardEnd:")) {
					// Get details of a flashcard
					String term = line;
					String definition = in.nextLine();
					int priority = Integer.valueOf(in.nextLine());
					
					// Create flashcard, and add to set
					Flashcard flashcard = new Flashcard(term, definition, priority);
					flashcards.add(flashcard);
			
					line = in.nextLine();
				}
				
				// Get dates
				dateCreated = new Date(in.nextLine());
				dateAccessed = new Date(in.nextLine());
				
				// Create set, and add to database
				StudySet set = new StudySet(title, description, flashcards, dateCreated, dateAccessed);
				sets.add(set);
				
				// Reset variable
				flashcards = new ArrayList<>();
			}
		}
		in.close();
		
	}
	
	
	/**
	 * Updates the set database
	 * @throws FileNotFoundException
	 */
	public static void updateSetDatabase() throws FileNotFoundException {
		File setData = new File("study_set_database");
		PrintWriter writer = new PrintWriter(setData);
		for (StudySet s : sets) {
			writer.print("Title;Description;Flashcards;DateCreated;DateAccessed\n");
			writer.print(s.getTitle() + "\n");
			writer.print(s.getDescription() + "\n");
			writer.print("FlashcardStart:\n");
			for (Flashcard f : s.getFlashcards()) {
				writer.print(f.getTerm() + "\n");
				writer.print(f.getDefinition() + "\n");
				writer.print(f.getPriority() + "\n");
			}
			writer.print("FlashcardEnd:\n");
			writer.print(s.getDateCreated() + "\n");
			writer.print(s.getDateAccessed() + "\n");
			writer.print("\n");
		}
		writer.close();
	}
	
	
	/**
	 * Checks if a flashcard is valid, ie. has a term and definition
	 * @param term the term to check
	 * @param definition the definition to check
	 * @return "Valid" if both params are non-empty, "Missing .." otherwise
	 */
	public static String validFlashcard(String term, String definition) {
		if (term.isBlank()) {
			return "Missing Term";
		}
		else if (definition.isBlank()) {
			return "Missing Definition";
		}
		else {
			return "Valid";
		}
	}
	
	
	// For study set creation, editing flashcard
	public static void editFlashcard(Flashcard flashcard, String term, String definition, int priority) {
		if (!term.equals(flashcard.getTerm())) { flashcard.updateTerm(term); }
		if (!definition.equals(flashcard.getDefinition())) { flashcard.updateDefinition(definition); }
		if (priority != flashcard.getPriority()) { flashcard.updatePriority(priority); }
	}
	
	
	/**
	 * Adds a set to the database if valid, ie. title and 0 < #flashcards
	 * @param title the title of the new set
	 * @param description the description of the new set
	 * @param flashcards the set of flashcards for the new set
	 * @return "Successful" if all params are valid
	 * @throws FileNotFoundException 
	 */
	public static String addSet(String title, String description, ArrayList<Flashcard> flashcards) throws FileNotFoundException {
		if (title.isBlank()) { 
			return "Missing Title"; 
		}
		else if (flashcards.isEmpty()) { 
			return "Need at least 1 flashcard"; 
		}
		else {
			StudySet set = new StudySet(title, description, flashcards);
			sets.add(set);
			updateSetDatabase();
			return "Successful";
		}
	}
	
	
	/**
	 * Edits a selected set
	 * @param set the set to edit
	 * @param title the title
	 * @param description the description
	 * @param flashcards the set of flashcards
	 * @return "Successful" if edits are valid, else, error messages
	 * @throws FileNotFoundException
	 */
	public static String editSet(StudySet set, String title, String description, ArrayList<Flashcard> flashcards) throws FileNotFoundException {
		if (title.isBlank()) { 
			return "Missing Title"; 
		}
		else if (flashcards.isEmpty()) {
			return "Need at least 1 flashcard";
		}
		else {
			if (!title.equals(set.getTitle())) { set.updateTitle(title); }
			if (!description.equals(set.getDescription())) { set.updateDescription(description); }
			updateSetDatabase();
			return "Successful";
		}
	}
	
	
	/**
	 * Removes a set from the database
	 * @param set the set to delete
	 * @return "Successful"
	 * @throws FileNotFoundException
	 */
	public static String deleteSet(StudySet set) throws FileNotFoundException {
		sets.remove(set);
		updateSetDatabase();
		return "Successful";
	}
	
	
	/* NOTE METHODS */
	
	
	/**
	 * Loads note data from database
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("deprecation")
	public static void loadNotes() throws FileNotFoundException {
		notes = new ArrayList<>();
		File noteData = new File("note_database");
		Scanner in = new Scanner(noteData);
		
		String title = "";
		String description = "";
		Date dateCreated;
		Date dateAccessed;
		
		while (in.hasNextLine()) {
			String line = in.nextLine();
			
			// Indicates new note
			if (line.equals("Title;Description;Text;DateCreated;DateAccessed")) {
				// Get details of note
				title = in.nextLine();
				description = in.nextLine();
				
				line = in.nextLine();	// Skip "TextStart:"
				line = in.nextLine();	// First line of text
				
				String text = "";
				
				// Get text
				while (!line.equals("TextEnd:")) {
					text += line + "\n";
					line = in.nextLine();
				}
				
				// Remove the last \n
				text = text.substring(0, text.length() - 1);
				
				// Get dates
				dateCreated = new Date(in.nextLine());
				dateAccessed = new Date(in.nextLine());
				
				// Create note, and add to database
				Note note = new Note(title, description, text, dateCreated, dateAccessed);
				notes.add(note);
				
				// Reset variables
				description = "";
			}
		}
		in.close();
	}
	
	
	/**
	 * Updates the note database
	 * @throws FileNotFoundException
	 */
	public static void updateNoteDatabase() throws FileNotFoundException {
		File noteData = new File("note_database");
		PrintWriter writer = new PrintWriter(noteData);
		for (Note n : notes) {
			writer.print("Title;Description;Text;DateCreated;DateAccessed\n");
			writer.print(n.getTitle() + "\n");
			writer.print(n.getDescription() + "\n");
			writer.print("TextStart:\n" + n.getText() + "\nTextEnd:\n");
			writer.print(n.getDateCreated() + "\n");
			writer.print(n.getDateAccessed() + "\n");
			writer.print("\n");
		}
		writer.close();
	}
	
	
	/**
	 * Adds a note to the database if valid, ie. title
	 * @param title the title of the new note
	 * @param description the description of the new note
	 * @param text the text of the new note
	 * @return "Successful" if title is non-empty, "Missing Title" otherwise
	 * @throws FileNotFoundException
	 */
	public static String addNote(String title, String description, String text) throws FileNotFoundException {
		if (title.isBlank()) {
			return "Missing Title";
		}
		else {
			Note note = new Note(title, description, text);
			notes.add(note);
			updateNoteDatabase();
			return "Successful";
		}
	}
	
	
	/**
	 * Edits a note in the database if edits are valid, ie. non-empty title
	 * @param note the note to edit
	 * @param title the new title of the note
	 * @param description the new description of the note
	 * @param text the new text of the note
	 * @return "Successful" if title is non-empty, "Missing Title" otherwise
	 * @throws FileNotFoundException
	 */
	public static String editNote(Note note, String title, String description, String text) throws FileNotFoundException {
		if (title.isBlank()) {
			return "Missing Title";
		}
		else {
			// Only call update methods if value is DIFFERENT from the original value
			if (!title.equals(note.getTitle())) note.updateTitle(title);
			if (!description.equals(note.getDescription())) note.updateDescription(description);
			if (!text.equals(note.getText())) note.updateText(text);
			updateNoteDatabase();
			return "Successful";
		}
	}
	
	
	/**
	 * Removes a note from the database
	 * @param note the note to delete
	 * @return "Successful"
	 * @throws FileNotFoundException
	 */
	public static String deleteNote(Note note) throws FileNotFoundException {
		notes.remove(note);
		updateNoteDatabase();
		return "Successful";
	}
	
	
	/* PRINT METHODS FOR DEBUGGING PURPOSES */
	
	
	public static void printNotes() {
		for (Note n : notes) {
			System.out.println(n);
			System.out.println();	// Add space between notes
		}
	}

}
