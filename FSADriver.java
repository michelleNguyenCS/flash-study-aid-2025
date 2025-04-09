/**
 * @author Michelle Nguyen
 * @version 2025-04-07
 */


/* IMPORT STATEMENTS */
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.Tab;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;


public class FSADriver extends Application {
	/* VARIABLES */
	private Scene scene;
	private TabPane tabPane;
	private Tab studySetTab;
	private Tab noteTab;
	private VBox studySetVBox;
	private VBox noteVBox;
	

	/**
	 * Launches the application
	 */
	public void start(Stage stage) throws Exception {
		// Load information from database
		Database.loadSets();
		Database.loadNotes();
		
		// Setup the GUI
		initializeUI();		
		stage.setTitle("FlashStudyAid");
		stage.getIcons().add(new Image("file:Icon.PNG"));	// App Icon
		stage.setScene(scene);
		stage.show();
	}
	
	
	/**
	 * Create the tabs
	 */
	private void initializeUI() {
		// Initial content box
		VBox vbox = new VBox();
		
		// Create tabs
		tabPane = new TabPane();
		studySetTab = new Tab("Study Sets");
		noteTab = new Tab("Notes");
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		// Set content for tabs to home
		setNoteHome();
		setStudyHome();
		
		tabPane.getTabs().addAll(studySetTab, noteTab);
		vbox.getChildren().add(tabPane);
		
		// Create scene and add CSS
		scene = new Scene(vbox, 1000, 600);
		String css = this.getClass().getResource("style.css").toExternalForm();
		scene.getStylesheets().add(css);
	}
	
	
	/**
	 * Create a pop-up error warning
	 * @param msg is the content to display on the warning
	 */
	private void getErrorAlert(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(msg);
		alert.show();
	}
	/**
	 * Create a pop-up confirmation message
	 * @param msg is the content to display on the confirmation
	 * @return an optional containing the action the user took
	 */
	private Optional<ButtonType> getConfirmationAlert(String msg) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText(msg);
		Optional<ButtonType> result = alert.showAndWait();
		return result;
	}
	
	
	/**
	 * Loads data from the database and inserts it into the StudySet table
	 * @param table the StudySet table
	 */
	private void loadSets(TableView<StudySet> table) {
		table.getItems().clear();
		for (StudySet set : Database.getSets()) {
			table.getItems().add(set);
		}
	}
	
	
	/**
	 * Update the access date of a set
	 * @param set the set
	 * @throws FileNotFoundException
	 */
	private void updateSetDateAccess(StudySet set) throws FileNotFoundException {
		set.updateDateAccessed();
		Database.updateSetDatabase();
	}
	
	
	/**
	 * Sets the StudySet tab content to home
	 */
	@SuppressWarnings("unchecked")
	private void setStudyHome() {
		studySetVBox = new VBox(20);
		studySetVBox.setAlignment(Pos.CENTER);
		studySetTab.setContent(studySetVBox);
		
		// Create table
		TableView<StudySet> studySetTable = new TableView<StudySet>();
		
		TableColumn<StudySet, Integer> studySetIDCol = new TableColumn<StudySet, Integer>("ID");
		studySetIDCol.setCellValueFactory(new PropertyValueFactory<StudySet, Integer>("id"));
		
		TableColumn<StudySet, Date> studySetDateAccessedCol = new TableColumn<StudySet, Date>("Date Accessed");
		studySetDateAccessedCol.setCellValueFactory(new PropertyValueFactory<StudySet, Date>("dateAccessed"));
		
		TableColumn<StudySet, String> studySetTitleCol = new TableColumn<StudySet, String>("Title");
		studySetTitleCol.setCellValueFactory(new PropertyValueFactory<StudySet, String>("title"));
		
		TableColumn<StudySet, String> studySetDescriptionCol = new TableColumn<StudySet, String>("Description");
		studySetDescriptionCol.setCellValueFactory(new PropertyValueFactory<StudySet, String>("description"));
		
		// Initial sizes of columns
		studySetIDCol.setPrefWidth(50);
		studySetDateAccessedCol.setPrefWidth(200);
		studySetTitleCol.setPrefWidth(250);
		studySetDescriptionCol.setPrefWidth(500);

		// Add columns to table
		studySetTable.getColumns().addAll(
				studySetIDCol, 
				studySetDateAccessedCol, 
				studySetTitleCol, 
				studySetDescriptionCol);

		loadSets(studySetTable);
		
		// Creating a set
		Button createStudySetButton = new Button("Create Set");
		createStudySetButton.setOnAction(e -> { setStudyCreate(); });
		
		// Studying a set
		Button studySetButton = new Button("Study");
		studySetButton.setOnAction(e -> {
			StudySet set = studySetTable.getSelectionModel().getSelectedItem();
			if (set != null) { 
				try {
					updateSetDateAccess(set);
					setStudy(set); 
				} 
				catch (FileNotFoundException e1) { System.out.println("Update Set Access Time Failed"); }
			}
			else { getErrorAlert("Select Set First"); }
		});
		
		// Editing a set
		Button editStudySetButton = new Button("Edit");
		editStudySetButton.setOnAction(e -> {
			StudySet set = studySetTable.getSelectionModel().getSelectedItem();
			if (set != null) { 
				try {
					updateSetDateAccess(set);
					setEditStudy(set); 
				} 
				catch (FileNotFoundException e1) { System.out.println("Update Set Access Time Failed"); }
			} 
			else { getErrorAlert("Select Set First"); }
		});
		
		// Deleting a set
		Button deleteStudySetButton = new Button("Delete");
		deleteStudySetButton.setOnAction(e -> {
			StudySet set = studySetTable.getSelectionModel().getSelectedItem();
			if (set != null) {
				Optional<ButtonType> result = getConfirmationAlert("Are you sure you want to delete this set?");
				if (result.get() == ButtonType.OK) {
					try { Database.deleteSet(set); } 
					catch (FileNotFoundException e1) { System.out.println("Set Delete Failed"); }
					loadSets(studySetTable);
				}
			} else { getErrorAlert("Select Set First"); }
		});
		
		HBox buttonsHBox = new HBox(20, createStudySetButton, studySetButton, editStudySetButton, deleteStudySetButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		
		studySetVBox.getChildren().addAll(studySetTable, buttonsHBox);
	}
	
	
	/**
	 * Creates a button to return to the StudySet home
	 * @return the StudySet home button
	 */
	private Button createStudyHomeButton() {
		Button homeButton = new Button("Home");
		homeButton.setOnAction(e -> { setStudyHome(); } );
		return homeButton;
	}
	
	
	/**
	 * Loads flashcards of a set into a selected set
	 * @param table the Flashcard table
	 * @param flashcards the set of Flashcards of the set
	 */
	private void loadFlashcards(TableView<Flashcard> table, ArrayList<Flashcard> flashcards) {
		table.getItems().clear();
		for (Flashcard flashcard : flashcards) {
			table.getItems().add(flashcard);
		}
	}
	
	
	/**
	 * Sets the StudySet tab content to creating a new flashcard
	 * @param createPage the StudySet creation page
	 * @param table the Flashcard table
	 * @param flashcards the set of Flashcards of the set
	 */
	private void setFlashcardCreate(VBox createPage, TableView<Flashcard> table, ArrayList<Flashcard> flashcards) {
		studySetVBox = new VBox(20);
		studySetVBox.setAlignment(Pos.CENTER);
		studySetTab.setContent(studySetVBox);
		
		Button homeButton = createStudyHomeButton();
		
		// Title of page
		Label title = new Label("New Flashcard");
		
		Button backButton = new Button("Cancel");
		backButton.setOnAction(e -> { 
			studySetVBox = createPage;
			studySetTab.setContent(studySetVBox);
			});
		HBox topHBox = new HBox(10, title, backButton);
		
		// Prompt for priority
		Label priorityLabel = new Label("Priority");
		ComboBox<Integer> priorityComboBox = new ComboBox<Integer>();
		priorityComboBox.getItems().addAll(1, 2, 3);
		priorityComboBox.setValue(2);
		HBox priorityHBox = new HBox(10, priorityLabel, priorityComboBox);
		
		// Prompt for term
		Label termLabel = new Label("Term");
		TextField termInput = new TextField();
		termInput.setPromptText("Required");
		VBox termVBox = new VBox(termLabel, termInput);
		
		// Prompt for definition
		Label definitionLabel = new Label("Definition");
		TextField definitionInput = new TextField();
		definitionInput.setPromptText("Required");
		VBox definitionVBox = new VBox(definitionLabel, definitionInput);
		
		Button createFlashcardButton = new Button("Create");
		createFlashcardButton.setOnAction(e -> {
			// Get flashcard values
			String term = termInput.getText();
			String definition = definitionInput.getText();
			int priority = priorityComboBox.getValue();
			
			// Check that required values are there ie. term, definition
			String isValid = Database.validFlashcard(term, definition);
			if (isValid == "Valid") {
				flashcards.add(new Flashcard(term, definition, priority));
				// Same code as Back button
				studySetVBox = createPage;
				loadFlashcards(table, flashcards);
				studySetTab.setContent(studySetVBox);
			}
			else { getErrorAlert(isValid); }
		});
		
		studySetVBox.getChildren().addAll(homeButton, topHBox, priorityHBox, termVBox, definitionVBox, createFlashcardButton);
	}
	
	
	/**
	 * Editing a flashcard
	 * @param setPage the page of the set that the flashcard belongs to
	 * @param table the table of flashcards
	 * @param flashcards the set of flashcards 
	 * @param flashcard the flashcard we are editing
	 */
	private void setFlashcardEdit(VBox setPage, TableView<Flashcard> table, ArrayList<Flashcard> flashcards, Flashcard flashcard) {
		studySetVBox = new VBox(20);
		studySetVBox.setAlignment(Pos.CENTER);
		studySetTab.setContent(studySetVBox);
		
		Button homeButton = createStudyHomeButton();
		
		// Title of page
		Label title = new Label("Editing Flashcard");
		
		Button backButton = new Button("Cancel");
		backButton.setOnAction(e -> { 
			studySetVBox = setPage;
			studySetTab.setContent(studySetVBox);
			});
		HBox topHBox = new HBox(10, title, backButton);
		
		// Prompt for priority
		Label priorityLabel = new Label("Priority");
		ComboBox<Integer> priorityComboBox = new ComboBox<Integer>();
		priorityComboBox.getItems().addAll(1, 2, 3);
		priorityComboBox.setValue(flashcard.getPriority());
		HBox priorityHBox = new HBox(10, priorityLabel, priorityComboBox);
		
		// Prompt for term
		Label termLabel = new Label("Term");
		TextField termInput = new TextField(flashcard.getTerm());
		termInput.setPromptText("Required");
		VBox termVBox = new VBox(termLabel, termInput);
		
		// Prompt for definition
		Label definitionLabel = new Label("Definition");
		TextField definitionInput = new TextField(flashcard.getDefinition());
		definitionInput.setPromptText("Required");
		VBox definitionVBox = new VBox(definitionLabel, definitionInput);
		
		Button updateButton = new Button("Update Flashcard");
		updateButton.setOnAction (e -> {
			// Get flashcard values
			String term = termInput.getText();
			String definition = definitionInput.getText();
			int priority = priorityComboBox.getValue();
			
			// Check that required values are there ie. term, definition
			String isValid = Database.validFlashcard(term, definition);
			if (isValid == "Valid") {
				Database.editFlashcard(flashcard, term, definition, priority);
				// Same code as Back button
				studySetVBox = setPage;
				loadFlashcards(table, flashcards);
				studySetTab.setContent(studySetVBox);
			} else { getErrorAlert(isValid); }
		});
		
		studySetVBox.getChildren().addAll(homeButton, topHBox, priorityHBox, termVBox, definitionVBox, updateButton);
	}
	
	
	/**
	 * Sets the StudySet tab content to creating a new set
	 */
	@SuppressWarnings("unchecked")
	private void setStudyCreate() {
		studySetVBox = new VBox();
		studySetVBox.setAlignment(Pos.CENTER);
		studySetTab.setContent(studySetVBox);
		
		Button homeButton = createStudyHomeButton();
		
		// Title of page
		Label title = new Label("New Study Set");
		HBox topHBox = new HBox(title);
		
		// Prompt for title
		Label titleLabel = new Label("Title");
		TextField titleInput = new TextField();
		titleInput.setPromptText("Required");
		HBox titleHBox = new HBox(titleLabel, titleInput);
		
		// Prompt for description
		Label descriptionLabel = new Label("Description");
		TextField descriptionInput = new TextField();
		descriptionInput.setPromptText("Optional");
		HBox descriptionHBox = new HBox(descriptionLabel, descriptionInput);
		
		// Create flashcards table
		Label flashcardTableLabel = new Label("Flashcards");
		
		TableView<Flashcard> flashcardTable = new TableView<Flashcard>();
		
		TableColumn<Flashcard, Integer> flashcardPriorityCol = new TableColumn<Flashcard, Integer>("Priority");
		flashcardPriorityCol.setCellValueFactory(new PropertyValueFactory<Flashcard, Integer>("priority"));
		
		TableColumn<Flashcard, String> flashcardTermCol = new TableColumn<Flashcard, String>("Term");
		flashcardTermCol.setCellValueFactory(new PropertyValueFactory<Flashcard, String>("term"));
		
		TableColumn<Flashcard, String> flashcardDefinitionCol = new TableColumn<Flashcard, String>("Definition");
		flashcardDefinitionCol.setCellValueFactory(new PropertyValueFactory<Flashcard, String>("definition"));
		
		// Initialize sizes of columns
		flashcardPriorityCol.setPrefWidth(100);
		flashcardTermCol.setPrefWidth(400);
		flashcardDefinitionCol.setPrefWidth(500);
		
		// Add columns to table
		flashcardTable.getColumns().addAll(flashcardPriorityCol, flashcardTermCol, flashcardDefinitionCol);
			
		ArrayList<Flashcard> flashcards = new ArrayList<>();
		
		// Prompt for new flashcard
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> { 
			setFlashcardCreate(studySetVBox, flashcardTable, flashcards); 
		});
		
		// Edit a flashcard
		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> {
			Flashcard flashcard = flashcardTable.getSelectionModel().getSelectedItem();
			if (flashcard != null) { setFlashcardEdit(studySetVBox, flashcardTable, flashcards, flashcard); }
			else { getErrorAlert("Select Flashcard First"); }
		});
		
		// Delete a flashcard
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			Flashcard flashcard = flashcardTable.getSelectionModel().getSelectedItem();
			if (flashcard != null) {
				Optional<ButtonType> result = getConfirmationAlert("Are you sure you want to delete this flashcard?");
				if (result.get() == ButtonType.OK) {
					flashcards.remove(flashcard);
					loadFlashcards(flashcardTable, flashcards);
				}
			} else { getErrorAlert("Select Flashcard First"); }
		});
		
		HBox flashcardHBox = new HBox(flashcardTableLabel, addButton, editButton, deleteButton);
		VBox flashcardVBox = new VBox(flashcardHBox, flashcardTable);
		
		Button createButton = new Button("Create");
		createButton.setOnAction (e -> {
			String result;
			try {
				result = Database.addSet(titleInput.getText(), descriptionInput.getText(), flashcards);
				// Ensure required fields are there, ie. title and > 1 flashcard
				if (result == "Successful") { setStudyHome(); }
				else { getErrorAlert(result); }
			} catch (FileNotFoundException e1) { System.out.println("Set Create Failed"); }
		});
		
		studySetVBox.getChildren().addAll(homeButton, topHBox, titleHBox, descriptionHBox, flashcardVBox, createButton);
	}
	
	
	// Needs to be defined here because cannot change values from within button action scope
	private Iterator<Flashcard> it;
	private Flashcard flashcard;
	private int count;
	
	
	/**
	 * Sets the StudySet tab content to studying a selected set
	 * @param set the set to study
	 */
	private void setStudy(StudySet set) {
		studySetVBox = new VBox(20);
		studySetVBox.setAlignment(Pos.CENTER);
		studySetTab.setContent(studySetVBox);

		Button homeButton = createStudyHomeButton();
		
		// Title of page
		Label title = new Label(set.getTitle());
		HBox topHBox = new HBox(title);
		
		// Setup flashcard view
		Label flashcardDetail = new Label();
		Label flashcardPriority = new Label();
		TextArea flashcardText = new TextArea();
			flashcardText.setEditable(false);
			flashcardText.setPrefWidth(500);
			flashcardText.setPrefHeight(300);
			flashcardText.setWrapText(true);
		Label flashcardNumber = new Label();
		
		VBox flashcardInfoVBox = new VBox(flashcardDetail, flashcardPriority);
		
		// Get flashcards
		ArrayList<Flashcard> flashcards = set.shuffleFlashcards();
		int total = set.getSize();
		count = 1;
		it = flashcards.iterator();
		
		// Get first flashcard
		flashcard = it.next();
		
		// Show first flashcard
		flashcardDetail.setText("Term");
		flashcardPriority.setText("Priority: " + Integer.toString(flashcard.getPriority()));
		flashcardText.setText(flashcard.getTerm());
		flashcardNumber.setText(Integer.toString(count) + " / " + Integer.toString(total));
		
		// Flipping the flashcard
		Button flipButton = new Button("Flip");
		flipButton.setOnAction(e -> { 
			// Flip to definition
			if (flashcardDetail.getText().equals("Term")) {
				flashcardDetail.setText("Definition");
				flashcardText.setText(flashcard.getDefinition());
			}
			// Flip to term
			else {
				flashcardDetail.setText("Term");
				flashcardText.setText(flashcard.getTerm());
			}			
		});
		
		// Next flashcard
		Button nextButton = new Button("Next");
		
		HBox buttonsHBox = new HBox(50, flipButton, nextButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		
		// Styling to put the buttons directly underneath the flashcard
		VBox centerVBox = new VBox(flashcardText, buttonsHBox);
		
		HBox flashcardHBox = new HBox(flashcardInfoVBox, centerVBox, flashcardNumber);
		flashcardHBox.setAlignment(Pos.CENTER);
		
		nextButton.setOnAction(e -> {
			// Haven't reached end of set
			if (it.hasNext()) {
				// Get next flashcard
				flashcard = it.next();
				flashcardDetail.setText("Term");
				flashcardPriority.setText("Priority: " + Integer.toString(flashcard.getPriority()));
				flashcardText.setText(flashcard.getTerm());
				// Update flashcard count
				count++;
				flashcardNumber.setText(Integer.toString(count) + " / " + Integer.toString(total));
			}
			// Reached end of set
			else {
				if (nextButton.getText().equals("Again")) {
					setStudy(set);
				}
				else {
					flashcardText.setText("Reached end of set");
					nextButton.setText("Again");
					flashcardDetail.setText("");
					flashcardPriority.setText("");
					buttonsHBox.getChildren().remove(flipButton);
				}
			}
		});
		
		studySetVBox.getChildren().addAll(homeButton, topHBox, flashcardHBox);
	}
	

	/**
	 * Sets the StudySet tab content to editing a selected set
	 * @param set the set to edit
	 */
	@SuppressWarnings("unchecked")
	private void setEditStudy(StudySet set) {
		studySetVBox = new VBox();
		studySetVBox.setAlignment(Pos.CENTER);
		studySetTab.setContent(studySetVBox);
		
		Button homeButton = createStudyHomeButton();
		
		// Title of page
		Label title = new Label("Edit Set");
		HBox topHBox = new HBox(title);
		
		// Prompt for title
		Label titleLabel = new Label("Title");
		TextField titleInput = new TextField(set.getTitle());
		titleInput.setPromptText("Required");
		HBox titleHBox = new HBox(titleLabel, titleInput);
		
		// Prompt for description
		Label descriptionLabel = new Label("Description");
		TextField descriptionInput = new TextField(set.getDescription());
		descriptionInput.setPromptText("Optional");
		HBox descriptionHBox = new HBox(descriptionLabel, descriptionInput);
		
		// Create flashcards table
		Label flashcardTableLabel = new Label("Flashcards");
		
		TableView<Flashcard> flashcardTable = new TableView<Flashcard>();
		
		TableColumn<Flashcard, Integer> flashcardPriorityCol = new TableColumn<Flashcard, Integer>("Priority");
		flashcardPriorityCol.setCellValueFactory(new PropertyValueFactory<Flashcard, Integer>("priority"));
		
		TableColumn<Flashcard, String> flashcardTermCol = new TableColumn<Flashcard, String>("Term");
		flashcardTermCol.setCellValueFactory(new PropertyValueFactory<Flashcard, String>("term"));
		
		TableColumn<Flashcard, String> flashcardDefinitionCol = new TableColumn<Flashcard, String>("Definition");
		flashcardDefinitionCol.setCellValueFactory(new PropertyValueFactory<Flashcard, String>("definition"));
		
		// Initialize sizes of columns
		flashcardPriorityCol.setPrefWidth(100);
		flashcardTermCol.setPrefWidth(400);
		flashcardDefinitionCol.setPrefWidth(500);
		
		// Add columns to table
		flashcardTable.getColumns().addAll(flashcardPriorityCol, flashcardTermCol, flashcardDefinitionCol);
		
		// Get set flashcards
		ArrayList<Flashcard> flashcards = set.getFlashcards();
		loadFlashcards(flashcardTable, flashcards);
		
		// Prompt for new flashcard
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> { 
			setFlashcardCreate(studySetVBox, flashcardTable, flashcards); 
		});
		
		// Edit a flashcard
		Button editButton = new Button("Edit");
		editButton.setOnAction (e -> {
			Flashcard flashcard = flashcardTable.getSelectionModel().getSelectedItem();
			if (flashcard != null) { setFlashcardEdit(studySetVBox, flashcardTable, flashcards, flashcard); }
			else { getErrorAlert("Select Flashcard First"); }
		});
		
		// Delete a flashcard
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			Flashcard flashcard = flashcardTable.getSelectionModel().getSelectedItem();
			if (flashcard != null) {
				// Get confirmation message
				Optional<ButtonType> result = getConfirmationAlert("Are you sure you want to delete this flashcard?");
				if (result.get() == ButtonType.OK) {
					flashcards.remove(flashcard);
					// Reload table with changes
					loadFlashcards(flashcardTable, flashcards);
				}
			} else { getErrorAlert("Select Flashcard First"); }
		});
		
		HBox flashcardHBox = new HBox(flashcardTableLabel, addButton, editButton, deleteButton);
		VBox flashcardVBox = new VBox(flashcardHBox, flashcardTable);
		
		Button editSetButton = new Button("Update");
		editSetButton.setOnAction (e -> {
			String inputTitle = titleInput.getText();
			String inputDescription = descriptionInput.getText();
			String result;
			try {
				// Ensure all required fields are there ie. title, description
				result = Database.editSet(set, inputTitle, inputDescription, flashcards);
				if (result == "Successful") { setStudyHome(); }
				else { getErrorAlert(result); }
			} catch (FileNotFoundException e1) { System.out.println("Set Edit Failed"); }
		});
		
		studySetVBox.getChildren().addAll(homeButton, topHBox, titleHBox, descriptionHBox, flashcardVBox, editSetButton);
	}
		
	
	/**
	 * Loads data from the database and inserts it into the Note table
	 * @param table the Note table
	 */
	private void loadNotes(TableView<Note> table) {
		table.getItems().clear();
		for (Note note : Database.getNotes()) {
			table.getItems().add(note);
		}
	}
	
	
	/**
	 * Update the access date of a note
	 * @param note the note
	 * @throws FileNotFoundException
	 */
	private void updateNoteDateAccess(Note note) throws FileNotFoundException {
		note.updateDateAccessed();
		Database.updateNoteDatabase();
	}
	
	
	/**
	 * Sets the Note tab content to home
	 */
	@SuppressWarnings("unchecked")
	private void setNoteHome() {
		noteVBox = new VBox(20);
		noteVBox.setAlignment(Pos.CENTER);
		noteTab.setContent(noteVBox);
		
		// Create table
		TableView<Note> noteTable = new TableView<Note>();
		
		TableColumn<Note, Integer> noteIDCol = new TableColumn<Note, Integer>("ID");
		noteIDCol.setCellValueFactory(new PropertyValueFactory<Note, Integer>("id"));
		
		TableColumn<Note, Date> noteDateAccessedCol = new TableColumn<Note, Date>("Date Accessed");
		noteDateAccessedCol.setCellValueFactory(new PropertyValueFactory<Note, Date>("dateAccessed"));
		
		TableColumn<Note, String> noteTitleCol = new TableColumn<Note, String>("Title");
		noteTitleCol.setCellValueFactory(new PropertyValueFactory<Note, String>("title"));
		
		TableColumn<Note, String> noteDescriptionCol = new TableColumn<Note, String>("Description");
		noteDescriptionCol.setCellValueFactory(new PropertyValueFactory<Note, String>("description"));
		
		// Initial sizes of columns
		noteIDCol.setPrefWidth(50);
		noteDateAccessedCol.setPrefWidth(200);
		noteTitleCol.setPrefWidth(250);
		noteDescriptionCol.setPrefWidth(500);
		
		// Add columns to table
		noteTable.getColumns().addAll(
				noteIDCol, 
				noteDateAccessedCol, 
				noteTitleCol, 
				noteDescriptionCol);
		
		loadNotes(noteTable);
		
		// Creating a note
		Button createNoteButton = new Button("Create Note");
		createNoteButton.setOnAction(e -> { setNoteCreate(); });
		
		// Viewing a note
		Button viewNoteButton = new Button("View");
		viewNoteButton.setOnAction(e -> {
			Note note = noteTable.getSelectionModel().getSelectedItem();
			if (note != null) { 
				try {
					updateNoteDateAccess(note);
					viewNote(note); 
				} 
				catch (FileNotFoundException e1) { System.out.println("Update Note Access Time Failed"); }
			}
			else { getErrorAlert("Select Note First"); }
		});
		
		// Editing a note
		Button editNoteButton = new Button("Edit");
		editNoteButton.setOnAction(e -> {
			Note note = noteTable.getSelectionModel().getSelectedItem();
			if (note != null) { 
				try {
					updateNoteDateAccess(note);
					setEditNote(note);
				} 
				catch (FileNotFoundException e1) { System.out.println("Update Note Access Time Failed"); }
			}
			else { getErrorAlert("Select Note First"); }
		});
		
		// Deleting a note
		Button deleteNoteButton = new Button("Delete");
		deleteNoteButton.setOnAction(e -> {
			Note note = noteTable.getSelectionModel().getSelectedItem();
			if (note != null) {
				// Get confirmation message
				Optional<ButtonType> result = getConfirmationAlert("Are you sure you want to delete this note?");
				if (result.get() == ButtonType.OK) {
					try { Database.deleteNote(note); } 
					catch (FileNotFoundException e1) { System.out.println("Note Delete Failed"); }
					loadNotes(noteTable);
				}
			}
			else { getErrorAlert("Select Note First"); }
		});
		
		HBox buttonsHBox = new HBox(20, createNoteButton, viewNoteButton, editNoteButton, deleteNoteButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		
		noteVBox.getChildren().addAll(noteTable, buttonsHBox);
	}
	
	
	/**
	 * Creates a button to return to the Note home
	 * @return
	 */
	private Button createNoteHomeButton() {
		Button homeButton = new Button("Home");
		homeButton.setOnAction(e -> { setNoteHome(); } );
		return homeButton;
	}
	
	
	/**
	 * Sets the Note tab content to creating a new note
	 */
	private void setNoteCreate() {
		noteVBox = new VBox();
		noteVBox.setAlignment(Pos.CENTER);
		noteTab.setContent(noteVBox);
		
		Button homeButton = createNoteHomeButton();
		
		// Title of page
		Label title = new Label("New Note");
		HBox topHBox = new HBox(title);
		
		// Prompt for title
		Label titleLabel = new Label("Title");
		TextField titleInput = new TextField();
		titleInput.setPromptText("Required");
		HBox titleHBox = new HBox(titleLabel, titleInput);
		
		// Prompt for description
		Label descriptionLabel = new Label("Description");
		TextField descriptionInput = new TextField();
		descriptionInput.setPromptText("Optional");
		HBox descriptionHBox = new HBox(descriptionLabel, descriptionInput);
		
		// Prompt for text
		Label textLabel = new Label("Text");
		TextArea textInput = new TextArea();
		textInput.setPromptText("Optional");
		textInput.setWrapText(true);
		VBox textVBox = new VBox(textLabel, textInput);
		
		// Creating a note
		Button createButton = new Button("Create");
		createButton.setOnAction(e -> {
			try {
				String result = Database.addNote(titleInput.getText(), descriptionInput.getText(), textInput.getText());
				// Ensure required fields are there, ie. title
				if (result == "Successful") { setNoteHome(); }
				else { getErrorAlert(result); }
			} 
			catch (FileNotFoundException e1) { System.out.println("Note Create Failed"); }
		});
		
		noteVBox.getChildren().addAll(homeButton, topHBox, titleHBox, descriptionHBox, textVBox, createButton);
	}
	
	
	/**
	 * Sets the Note tab content to viewing a selected note
	 * @param note the note to view
	 */
	private void viewNote(Note note) {
		noteVBox = new VBox(20);
		noteVBox.setAlignment(Pos.CENTER);
		noteTab.setContent(noteVBox);
		
		Button homeButton = createNoteHomeButton();
		
		// Title of page
		Label title = new Label(note.getTitle());
		HBox topHBox = new HBox(title);
		
		// Get details
		Label description = new Label(note.getDescription());
		Label wordCount = new Label("Word Count: " + note.getWordCount());
		Label text = new Label("\n" + note.getText());
		text.setPrefWidth(1000);
		text.setPrefHeight(400);
		text.setAlignment(Pos.TOP_LEFT);

		// Scroll bar
		ScrollPane scrollBar = new ScrollPane(text);
		
		HBox noteHBox = new HBox(scrollBar, text);
		
		noteVBox.getChildren().addAll(homeButton, topHBox, description, wordCount, noteHBox);
	}
	
	
	/**
	 * Sets the Note tab to editing a selected note
	 * @param note the note to edit
	 */
	private void setEditNote(Note note) {
		noteVBox = new VBox();
		noteVBox.setAlignment(Pos.CENTER);
		noteTab.setContent(noteVBox);
		
		Button homeButton = createNoteHomeButton();
		
		// Title of page
		Label title = new Label("Edit Note");
		HBox topHBox = new HBox(title);

		// Prompt for title
		Label titleLabel = new Label("Title");
		TextField titleInput = new TextField(note.getTitle());
		titleInput.setPromptText("Required");
		HBox titleHBox = new HBox(titleLabel, titleInput);

		// Prompt for description
		Label descriptionLabel = new Label("Description");
		TextField descriptionInput = new TextField(note.getDescription());
		descriptionInput.setPromptText("Optional");
		HBox descriptionHBox = new HBox(descriptionLabel, descriptionInput);

		// Prompt for text
		Label textLabel = new Label("Text");
		TextArea textInput = new TextArea(note.getText());
		textInput.setPromptText("Optional");
		textInput.setWrapText(true);
		VBox textVBox = new VBox(textLabel, textInput);
		
		// Editing a note
		Button editButton = new Button("Update");
		editButton.setOnAction(e -> {
			try {
				String result = Database.editNote(note, titleInput.getText(), descriptionInput.getText(), textInput.getText());
				if (result == "Successful") { setNoteHome(); }
				else { getErrorAlert(result); }
			} 
			catch (FileNotFoundException e1) { System.out.println("Note Edit Failed"); }
		});
		
		noteVBox.getChildren().addAll(homeButton, topHBox, titleHBox, descriptionHBox, textVBox, editButton);
	}
}
