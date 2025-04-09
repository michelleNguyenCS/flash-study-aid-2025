/**
 * @author Abigail Louie
 * @version 2025-04-09
 */


/* IMPORT CLASSES */ 
import java.util.Date;

public class Note {
	private String title;
	private String description;
	private String text;
	private int wordCount;
	private static int noteCount = 0;
	private int id;
	private Date dateCreated;
	private Date dateAccessed;
	
	public Note(String title, String description, String text) {
		this.title = title;
		this.description = description;
		this.text = text;
		updateWordCount();
		this.dateCreated = new Date();
		this.dateAccessed = dateCreated;
		id = noteCount;
		noteCount++;
	}
	
	public Note(String title, String description, String text, Date dateCreated, Date dateAccessed) {
		this.title = title;
		this.description = description;
		this.text = text;
		updateWordCount();
		id = noteCount;
		noteCount++;
		this.dateCreated = dateCreated;
		this.dateAccessed = dateAccessed;
	}
	
	//GET METHODS
	public String getTitle() { 
		return title; 
	}
	public String getDescription() { 
		return description; 
	}
	public String getText() { 
		return text; 
	}
	public int getWordCount () {
		return wordCount;
	}
	public int getId() { 
		return id; 
	}
	public Date getDateCreated() { 
		return dateCreated; 
	}
	public Date getDateAccessed() { 
		return dateAccessed; 
	}
	
	// UPDATE METHODS
	public void updateTitle(String title) { 
		this.title = title; 
	}
	public void updateDescription(String description) { 
		this.description = description; 
	}
	public void updateText(String text) { 
		this.text = text; 
		updateWordCount();
	}
	public void updateWordCount () {
		String newText = text.replaceAll("[^a-zA-Z0-9\\s]", " "); //Remove all characters that are not letters, numbers, or white space
 		String[] words = newText.trim().split("\\s+"); //Find all words in the text
 		wordCount = words.length; // Count all words in the text
	}
	public void updateDateAccessed () {
		this.dateAccessed = new Date();
	}
	
	// OVERRIDE METHODS
	public String toString() {
		return String.format("Title:\t\t%s\n"
				+ "Description:\t%s\n"
				+ "Text:\t\t\n%s"
				+ "Date Created:\t%s\n"
				+ "Date Accessed:\t%s", 
				title, description, text, dateCreated, dateAccessed);
	}
}