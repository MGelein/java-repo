package trb1914.alexml.data;

import trb1914.alexml.Debug;
import trb1914.helper.SystemHelper;
/**
 * Provides an easy way to switch languages and guarantees the use
 * of the same String without typos. 
 * @author Mees Gelein
 */
public class LanguageRegistry {
	
	/**
	 * Changes the language to the specified one
	 * @param lang
	 */
	public static void changeLanguage(String lang){
		Debug.print("[LanguageRegistry]: Tried to change Language: " + lang + "\n");
		switch(lang){
		case "NL":
			SystemHelper.loadSystemNL();
			
			MENU_SETTINGS = "Instellingen";
			MENU_PROGRAM_SETTINGS = "Programma Instellingen";
			MENU_CODE_HIGHLIGHT_SETTINGS = "XML Kleuren";
			MENU_FILE = "Bestand";
			MENU_OPEN = "Openen...";
			MENU_SAVE = "Opslaan";
			MENU_SAVE_AS = "Opslaan als...";
			MENU_NEW = "Nieuw Document";
			MENU_QUIT = "Afsluiten";
			MENU_RECENT = "Recente Bestanden";
			
			NEW_SETTINGS_WARNING = "Let op: Alle wijzigingen worden pas toegepast nadat het programma opnieuw is opgestart.";
			PARSING_ERROR = "Fout bij het XML-parsen.\nHet bestand gebruikt de verkeerde syntaxis.";
			UNSAVED_WARNING = "Weet u zeker dat u wilt afsluiten?\nOnopgeslagen wijzigingen "
					+ "gaan verloren";
			DELETE_SEGMENT_WARNING = "Weet u zeker dat u dit segment wilt verwijderen?\nDeze handeling is onomkeerbaar!";
			NEW_DOCUMENT_WARNING = "Weet u zeker dat u het huidige document wilt sluiten en een nieuw document wilt creeÃ«ren?\nOnopgeslagen wijzigingen gaan verloren";
			UNDEFINED_NOTE = "Lege Noot";
			OPENING_UNSAVED_WARNING = "Het huidige document bevat onopgeslagen wijzigingen! \nWeet u zeker dat u wilt doorgaan en een nieuw document wilt openen?";
			BACKUP_FOUND_WARNING = "De vorige sessie is onverwacht beeïndigd en heeft een backup"
					+ " bestand achtergelaten.\nWilt u deze backup herstellen?\nWanneer u de backup niet herstelt wordt deze verwijderd";
			SETTINGS_WINDOW_TITLE = "Instellingen";
			DOC_SETTING_WINDOW_TITLE = "Document Informatie";
			LICENSE_TAB_TITLE = "Licentie";
			GENERAL_TAB_TITLE = "Algemeen";
			KEYWORDS_TAB_TITLE = "Sleutelbegrippen";
			TRANSLATION_TAB_TITLE = "Vertaling";
			TRANSLATION_DESC_TITLE = "Beschrijving Vertaling";
			SAVE_AND_CLOSE = "Opslaan en Afsluiten";
			DISCARD_CHANGES = "Wijzigingen verwerpen";
			SEGMENT_ID_STRING = "Segment ID";
			MAIN_TEXT_TITLE = "Hoofdtekst";
			NOTE_EDITOR_TITLE = "Noot Editor";
			
			KEYWORDS_TEXTAREA_TOOLTIP = "Sleutelbegrippen gescheiden door komma's";
			
			AUTHOR_LABEL = "Auteur";
			PLACE_LABEL = "Plaats";
			LICENSE_LABEL = "Licentie";
			TEXT_ID_LABEL = "Tekst ID";
			KEYWORDS_LABEL = "Sleutelbegrippen";
			LOCUS_FROM_LABEL = "Locus van";
			LOCUS_TO_LABEL = "Locus tot";
			TITLE_LABEL = "Titel";
			LANGUAGE_LABEL = "Taal";
			CHECK_FOR_UPDATES_LABEL = "Automatische update check bij het opstarten";
			NOTE_ID_LABEL = "Noot ID";
			NOTE_CONTENT_LABEL = "Noot";
			INSERT_NOTE_LABEL = "Noot Invoegen";
			WORD_EDITOR_LABEL = "Woorden taggen";
			ATTRIBUTE_LABEL = "Attribuut label";
			NEW_ENTRY_LABEL = "Nieuw Woord";
			DELETE_ENTRY_LABEL = "Geselecteerd Woord Invoegen";
			ADD_WORDS_FROM_MAIN_LABEL = "Voeg Alle Woorden Uit De Hoofdtekst Toe";
			DATE_LABEL = "Datum (Tekst)";
			DATE_NOT_BEFORE_LABEL = "Vroegste Datum";
			DATE_NOT_AFTER_LABEL = "Laatste Datum";
			SET_LF_LABEL = "Uiterlijk Instellen";
			EMPHASIZE_LABEL = "Benadrukken";
			REFRESH_LABEL = "Verversen";
			TRANSLATIONS_LABEL = "Vertalingen";
			ADD_NEW_TRANSLATION_LABEL = "Nieuwe Vertaling Toevoegen";
			DELETE_SELECTED_TRANSLATION_LABEL = "Geselecteerde Vertaling Verwijderen";
			MOVE_TRANSLATION_UP_LABEL = "Geselecteerde Vertaling Omhoog Bewegen";
			MOVE_TRANSLATION_DOWN_LABEL = "Geselecteerde Vertaling Omlaag Bewegen";
			
			NEW_SEGMENT_MESSAGE = "Creëer Nieuw Segment";
			OPEN_SEGMENT_MESSAGE = "Open Geselecteerd Segment";
			DELETE_SEGMENT_MESSAGE = "Verwijder Geselecteerd Segment";
			MOVE_SEGMENT_UP_MESSAGE = "Verplaats Geselecteerd Segment Omhoog";
			MOVE_SEGMENT_DOWN_MESSAGE = "Verplaats Geselecteerd Segment Omlaag";
			
			SEGMENT_VIEW_NAME = "Segmenten";
			XML_VIEW_NAME = "XML";
			PREVIEW_VIEW_NAME = "Preview";
			
			MOVE_ENTRY_UP_LABEL = "Geselecteerd Label Omhoog Bewegen";
			MOVE_ENTRY_DOWN_LABEL = "Geselecteerd Label Omlaag Bewegen";
			NEW_ATTRIBUTE_LABEL = "Creëer Nieuw Attribuut";
			NEW_WORD_LABEL = "Creëer Nieuw Woord";
			
			SEGMENT_TOOLBAR_LABEL = "Segment Beheer";
			
			NO_DESCRIPTION = "geen beschrijving beschikbaar";
			
			OPEN_FILE_BUTTON = "Bestand Openen...";
			SAVE_FILE_BUTTON = "Bestand Opslaan...";
			NEW_FILE_BUTTON = "Nieuw Bestand...";
			DOC_SETTING_BUTTON = "Document Informatie";
			
			NOTE_TYPE_ANNO = "Annotatie";
			NOTE_TYPE_LABEL = "Noot Type";
			
			MAIN_COLOR_LABEL = "Normale Tekstkleur";
			TAG_COLOR_LABEL = "Tag Kleur";
			ATTRIBUTE_KEY_COLOR_LABEL = "Attribuut Naam Kleur";
			ATTRIBUTE_VALUE_COLOR_LABEL = "Attribuut Waarde Kleur";
			
			
			UPDATE_REQUIRED_MESSAGE = "Een nieuwe versie van dit programma is verkrijgbaar.\n"
					+ "U kunt doorgaan met het gebruiken van deze versie, maar updaten is ten zeerste aanbevolen!\nDe nieuwste versie kunt u hier downloaden: alexml.heliohost.org/download.html";
			break;
		case "EN":
		default:
			break;
		}
	}
	
	public static String MENU_SETTINGS = "Settings";
	public static String MENU_PROGRAM_SETTINGS = "Program Settings";
	public static String MENU_CODE_HIGHLIGHT_SETTINGS = "Code Highlighting";
	public static String MENU_FILE = "File";
	public static String MENU_OPEN = "Open...";
	public static String MENU_SAVE = "Save";
	public static String MENU_SAVE_AS = "Save as...";
	public static String MENU_NEW = "New Document";
	public static String MENU_QUIT = "Quit";
	public static String MENU_INFO = "Info";
	public static String MENU_RECENT = "Recent Files";
	
	public static String SAVING_ERROR = "Error while saving";
	public static String UNDEFINED_NOTE = "Undefined Note";
	public static String NEW_SETTINGS_WARNING = "Note: Any changes will only take effect after the program has been restarted.";
	public static String PARSING_ERROR = "Error with XML-parsing.\nMake sure the file uses correct syntax.";
	public static String UNSAVED_WARNING="Are you sure you want to close?\nAny unsaved changes will be lost";
	public static String DELETE_SEGMENT_WARNING="Are you sure you want to delete this segment?\nThis can't be undone!";
	public static String NEW_DOCUMENT_WARNING="Are you sure you want to close the current document and create a new one?\nAny unsaved changes will be lost";
	public static String OPENING_UNSAVED_WARNING = "The current document contains unsaved changes! \nAre you sure you want to continue and open another document?";
	public static String UNSAVED_CLOSE_WARNING="The current document contains unsaved changes! \nAre you sure you want to close?\nAny unsaved changes will be lost";
	public static String BACKUP_FOUND_WARNING = "A previous session was closed unexpectedly and has left a backup file.\nWould you like to restore the backup?\n"
			+ "If you dont restore the backup the file will be erased.";
	
	public static String SETTINGS_WINDOW_TITLE = "Settings";
	public static String DOC_SETTING_WINDOW_TITLE = "Document Information";
	public static String LICENSE_TAB_TITLE = "License";
	public static String GENERAL_TAB_TITLE = "General";
	public static String KEYWORDS_TAB_TITLE = "Keywords";
	public static String TRANSLATION_TAB_TITLE = "Translation";
	public static String TRANSLATION_DESC_TITLE = "Translation Description";
	public static String SEGMENT_ID_STRING = "Segment ID";
	public static String MAIN_TEXT_TITLE = "Main text";
	public static String NOTE_EDITOR_TITLE = "Note Editor";
	
	public static String AUTHOR_LABEL = "Author";
	public static String PLACE_LABEL = "Place";
	public static String LICENSE_LABEL = "License";
	public static String TEXT_ID_LABEL = "Text ID";
	public static String KEYWORDS_LABEL = "Keywords";
	public static String LOCUS_FROM_LABEL = "Locus from";
	public static String LOCUS_TO_LABEL = "Locus to";
	public static String TITLE_LABEL = "Title";
	public static String LANGUAGE_LABEL = "Language";
	public static String CHECK_FOR_UPDATES_LABEL = "Automatically check for updates on startup";
	public static String NOTE_ID_LABEL = "Note ID";
	public static String NOTE_CONTENT_LABEL = "Note";
	public static String INSERT_NOTE_LABEL = "Insert Note";
	public static String NOTE_TYPE_LABEL = "Note Type";
	public static String WORD_EDITOR_LABEL = "Word Tagging";
	public static String ATTRIBUTE_LABEL = "Attribute Name";
	public static String NEW_ENTRY_LABEL = "New Entry";
	public static String DELETE_ENTRY_LABEL = "Delete Selected Entry";
	public static String ADD_WORDS_FROM_MAIN_LABEL = "Add All Words From Main Text";
	public static String DATE_LABEL = "Date (Text)";
	public static String DATE_NOT_BEFORE_LABEL = "Not Before Date";
	public static String DATE_NOT_AFTER_LABEL = "Not After Date";
	public static String SET_LF_LABEL = "Set Look and Feel";
	public static String EMPHASIZE_LABEL = "Emphasize";
	public static String REMOVE_ALL_EMPHASIS_LABEL = "Remove All Emphasis";
	public static String TRANSLATIONS_LABEL = "Translations";
	public static String ADD_NEW_TRANSLATION_LABEL = "Add New Translation";
	public static String DELETE_SELECTED_TRANSLATION_LABEL = "Delete Selected Translation";
	public static String MOVE_TRANSLATION_UP_LABEL = "Move Selected Translation Up";
	public static String MOVE_TRANSLATION_DOWN_LABEL = "Move Selected Translation Down";
	
	public static String NEW_SEGMENT_MESSAGE = "Create New Segment...";
	public static String OPEN_SEGMENT_MESSAGE = "Open Selected Segment";
	public static String DELETE_SEGMENT_MESSAGE = "Delete Selected Segment";
	public static String MOVE_SEGMENT_UP_MESSAGE = "Move Selected Segment Up";
	public static String MOVE_SEGMENT_DOWN_MESSAGE = "Move Selected Segment Down";
	
	public static String SEGMENT_VIEW_NAME = "Segment-view";
	public static String XML_VIEW_NAME = "XML-view";
	public static String PREVIEW_VIEW_NAME = "Preview";
	
	public static String MOVE_ENTRY_UP_LABEL = "Move Selected Entry Up";
	public static String MOVE_ENTRY_DOWN_LABEL = "Move Selected Entry Down";
	public static String NEW_ATTRIBUTE_LABEL = "Create New Attribute";
	public static String NEW_WORD_LABEL = "Create New Word";
	public static String REFRESH_LABEL = "Refresh";
	
	public static String SEGMENT_TOOLBAR_LABEL = "Segment Control";
	public static String KEYWORDS_TEXTAREA_TOOLTIP = "Keywords separated by commas";
	
	public static String NO_DESCRIPTION = "no description available";
	
	public static String OPEN_FILE_BUTTON = "Open File...";
	public static String SAVE_FILE_BUTTON = "Save File...";
	public static String NEW_FILE_BUTTON = "New Empty File...";
	public static String DOC_SETTING_BUTTON = "Document Settings";
	public static String SAVE_AND_CLOSE = "Save and Close";
	public static String DISCARD_CHANGES = "Discard Changes";
	
	public static String MAIN_COLOR_LABEL = "Normal Text Color";
	public static String TAG_COLOR_LABEL = "Tag Color";
	public static String ATTRIBUTE_KEY_COLOR_LABEL = "Attribute Name Color";
	public static String ATTRIBUTE_VALUE_COLOR_LABEL = "Attribute Value Color";
	
	public static String NOTE_TYPE_CRIT = "Apparatus Criticus";
	public static String NOTE_TYPE_FONT = "Apparatus Fontium";
	public static String NOTE_TYPE_ANNO = "Annotation";
	
	public static String UPDATE_REQUIRED_MESSAGE = "A new version of the program has been published.\nYou can continue using this version"
			+ " of the program but updating is highly recommended!\nThe newest version can be downloaded at: alexml.heliohost.org/download.html";
	
	public static char NOTE_CHAR = '◙';
	public static String NOTE_STRING = "◙";
	
	public static String APP_CRIT = "appCrit";
	public static String APP_FONT = "appFont";
	public static String ANNOTATION = "annotation";
	
	public static String APPLICATION_VERSION = "v1.1.15";	//major release, minor release, build
	public static String APPLICATION_NAME = "AleXander XML";
	public static String APPLICATION_TITLE = APPLICATION_NAME + " " + APPLICATION_VERSION + " ";
	public static String CREATED_DOCUMENT_COMMENT="<!--this document was created using " + APPLICATION_NAME + " editor " + APPLICATION_VERSION + "-->\n";
	public static String CREDITS = "AleXander XML ©2014 - 2015. Design and Programming by Mees Gelein";
	public static String APPLICATION_INFO = APPLICATION_NAME + " " + APPLICATION_VERSION + "\nSoftware Design and Programming by Mees Gelein\n\n"
			+ "XSL Scripting by Jan Jouke Tjalsma\nAdditional XSL Scripting by Mees Gelein\n\nIdeas, suggestions and beta testing:\nTazuko van Berkel\nMarjolein van Raalte\nJan Jouke Tjalsma\n";
	
}
