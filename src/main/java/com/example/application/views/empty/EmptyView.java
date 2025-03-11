package com.example.application.views.empty;

import com.example.dao.DocumentDAO;
import com.example.model.Document;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Home")
@Route(value = "")
public class EmptyView extends AppLayout {

    private VerticalLayout mainContent; // Content area to display search results
    private DocumentDAO documentDAO;    // DAO for accessing document data
    private CheckboxGroup<String> authorGroup; // Direct reference to the author CheckboxGroup
    private CheckboxGroup<String> scriptureGroup; // Direct reference to the scripture CheckboxGroup
    private TextField searchBox; // Search box field
    private ComboBox<String> centuryFilterComboBox; // Century filter ComboBox
    private RadioButtonGroup<String> sortRadioGroup; // RadioButtonGroup for sorting

    public EmptyView() {
        getElement().setAttribute("theme", Lumo.DARK);
        documentDAO = new DocumentDAO(); // Initialize DocumentDAO

        DrawerToggle toggle = new DrawerToggle();

        // Title
        H1 title = new H1("Fathers of the Faith");
        title.getStyle().set("font-size", "var(--lumo-font-size-xl)").set("margin", "0");

        // Make the Theme Toggle Button
        Icon themeIcon = new Icon(VaadinIcon.SUN_O); // Default to moon icon for dark mode
        Button themeToggleButton = new Button(themeIcon);
        themeToggleButton.addClickListener(event -> {
            if (getElement().getAttribute("theme").equals(Lumo.DARK)) {
                // Switch to light mode
                getElement().setAttribute("theme", Lumo.LIGHT);
                themeToggleButton.setIcon(new Icon(VaadinIcon.MOON_O)); // Update to sun icon
            } else {
                // Switch to dark mode
                getElement().setAttribute("theme", Lumo.DARK);
                themeToggleButton.setIcon(new Icon(VaadinIcon.SUN_O)); // Update to moon icon
            }
        });

        // Search Box & Buttons
        searchBox = new TextField();
        searchBox.setPlaceholder("Search...");
        searchBox.setClearButtonVisible(true);
        searchBox.getElement().getStyle().set("padding", "var(--lumo-space-xl)");

        Button searchButton = new Button("Search");
        Button clearButton = new Button("Clear");

        // Set up event listener for search functionality
        searchButton.addClickListener(e -> executeSearch());
        searchBox.addKeyPressListener(Key.ENTER, e -> executeSearch());

        // Add buttons with some spacing
        HorizontalLayout buttonLayout = new HorizontalLayout(searchButton, clearButton, themeToggleButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);

        Scroller scroller = new Scroller();

        // Sort Radio Buttons
        sortRadioGroup = new RadioButtonGroup<>();
        Accordion sortAccordion = new Accordion();
        sortAccordion.getElement().getStyle().set("padding", "var(--lumo-space-m)");
        sortRadioGroup.setItems("Document Name", "Author Name");
        sortRadioGroup.setValue("Document Name"); // Default to sorting by Document Name
        VerticalLayout sortByLayout = new VerticalLayout(sortRadioGroup);
        sortAccordion.add("Sort By", sortByLayout);
        sortAccordion.close();

        // Filter Check Boxes
        Accordion filterAccordion = new Accordion();
        AccordionPanel outerPanel = new AccordionPanel("Filter");
        filterAccordion.close();

        // Author and Scripture Filter Panels
        Accordion authorAccordion = new Accordion();
        authorAccordion.add("Author", createAuthorCheckboxGroup());
        authorAccordion.getElement().getStyle().set("padding", "var(--lumo-space-m)");
        authorAccordion.close();

        Accordion scriptureAccordion = new Accordion();
        scriptureAccordion.add("Scripture", createScriptureCheckboxGroup());
        scriptureAccordion.getElement().getStyle().set("padding", "var(--lumo-space-m)");
        scriptureAccordion.close();

        // Century Filter ComboBox
        centuryFilterComboBox = new ComboBox<>("Century");
        centuryFilterComboBox.setItems("Any", "1st Century", "2nd Century", "3rd Century", "4th Century");
        centuryFilterComboBox.setValue("Any"); // Default to Any
        
        outerPanel.add(centuryFilterComboBox);
        outerPanel.add(scriptureAccordion);
        outerPanel.add(authorAccordion);
        filterAccordion.add(outerPanel);
        filterAccordion.getElement().getStyle().set("padding", "var(--lumo-space-m)");

        // Main Content Layout
        mainContent = new VerticalLayout();
        mainContent.setSpacing(true);
        mainContent.setPadding(true);

        // Adjusting layout of the drawer so that the items start at the top
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setAlignItems(FlexComponent.Alignment.START); // Align the items at the top of the drawer
        drawerContent.add(scroller, sortAccordion, filterAccordion);  // Add components in this layout

        // Set up event listeners for each event (Search, sort, filters)
        clearButton.addClickListener(e -> {
            // Reset all filters and input fields
            resetFiltersAndSearch();
            sortRadioGroup.clear(); // Clears the sort radio button group
            sortRadioGroup.setValue("Document Name"); // Reset the sort mode to default ("Document Name")
            centuryFilterComboBox.setValue("Any");

            // Repopulate the documents on the main page by displaying all documents
            displaySearchResults("", new ArrayList<>(), new ArrayList<>(), "Any", "Document Name");
        });
        sortRadioGroup.addValueChangeListener(e -> {
            String selectedSortBy = e.getValue();  // Get the selected value from the radio group
            List<String> selectedAuthors = authorGroup.getValue().stream().collect(Collectors.toList());
            List<String> selectedScriptures = scriptureGroup.getValue().stream().collect(Collectors.toList());
            String selectedCentury = centuryFilterComboBox.getValue();
            displaySearchResults(searchBox.getValue(), selectedAuthors, selectedScriptures, selectedCentury, selectedSortBy);
        });
        centuryFilterComboBox.addValueChangeListener(e -> updateSearchResults());
        authorGroup.addValueChangeListener(e -> updateSearchResults());
        scriptureGroup.addValueChangeListener(e -> updateSearchResults());

        // Add components to layout
        addToDrawer(drawerContent); // Replace the direct scroller and filterAccordion with drawerContent
        addToNavbar(toggle, title, searchBox, buttonLayout); // Add the button layout here
        setContent(mainContent);

        // Initial display of all documents
        displaySearchResults("", new ArrayList<>(), new ArrayList<>(), "", "Document Name");
    }

    private void updateSearchResults() {
        String selectedSortBy = centuryFilterComboBox.getValue(); // or get value from another sorting option
        List<String> selectedAuthors = authorGroup.getValue().stream().collect(Collectors.toList());
        List<String> selectedScriptures = scriptureGroup.getValue().stream().collect(Collectors.toList());
        String selectedCentury = centuryFilterComboBox.getValue();
        displaySearchResults(searchBox.getValue(), selectedAuthors, selectedScriptures, selectedCentury, selectedSortBy);
    }
    
    private void executeSearch() {
        List<String> selectedAuthors = authorGroup.getValue().stream().collect(Collectors.toList());
        List<String> selectedScriptures = scriptureGroup.getValue().stream().collect(Collectors.toList());
        String selectedCentury = centuryFilterComboBox.getValue();
        String selectedSortBy = sortRadioGroup.getValue(); // Get the selected sort option
        displaySearchResults(searchBox.getValue(), selectedAuthors, selectedScriptures, selectedCentury, selectedSortBy);
    }
    

    private Component createScriptureCheckboxGroup() {
        scriptureGroup = new CheckboxGroup<>(); // Initialize scriptureGroup
        scriptureGroup.setItems("Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", 
                "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel", "1 Kings", "2 Kings", 
                "1 Chronicles", "2 Chronicles", "Ezra", "Nehemiah", "Esther", "Job", 
                "Psalms", "Proverbs", "Ecclesiastes", "Song of Songs", "Isaiah", "Jeremiah", 
                "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", 
                "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", 
                "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", 
                "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians", "Philippians", 
                "Colossians", "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", 
                "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", 
                "2 John", "3 John", "Jude", "Revelation");
        scriptureGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL); // Make items stack vertically
        return scriptureGroup;
    }
    
    private Component createAuthorCheckboxGroup() {
        authorGroup = new CheckboxGroup<>(); // Initialize authorGroup
        authorGroup.setItems("Anatolius","Arnobius","Asterius","Athenagoras","Barnabas",
                "Caius","Clement of Alexandria","Clement of Rome","Commodian","Cyprian",
                "Dinoysius the Great","Dionysius","Gregory Thaumaturgus","Hermas","Hippolytus",
                "Ignatius","Irenaeus","Julius Africanus","Justin Martyr","Lactantius","Mathetes",
                "Methodius","Minucius Felix","Novatian","Origen","Papias","Polycarp","Tatian","Tertullian",
                "Theophilus","Venantius","Victorinus");
        authorGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL); 
        return authorGroup;
    }

    private void resetFiltersAndSearch() {
        searchBox.clear();
        scriptureGroup.clear();
        authorGroup.clear();
    }

    private void displaySearchResults(String query, List<String> authors, List<String> scriptures, String century, String sortBy) {
        // Map full scripture names to abbreviations
        List<String> abbreviatedScriptures = scriptures.stream()
                .map(scriptureMap::get)
                .filter(abbreviation -> abbreviation != null)
                .collect(Collectors.toList());
    
        // Fetch documents from DAO
        List<Document> documents = documentDAO.searchDocuments(query, authors, abbreviatedScriptures, century);
    
        // Sort results
        if ("Author Name".equals(sortBy)) {
            documents.sort(Comparator.comparing(Document::getAuthor, Comparator.nullsLast(Comparator.naturalOrder())));
        } else {
            documents.sort(Comparator.comparing(Document::getTitle, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    
        // Clear previous results
        mainContent.removeAll();
    
        // Highlight logic
        for (Document document : documents) {
            String highlightedContent = highlightQuery(document.getContent(), query);
            String summary = document.getTitle() + " by " + document.getAuthor() + " written in the " + document.getCentury();
    
            Details details = new Details(summary, new Html("<div>" + highlightedContent + "</div>"));
            details.setOpened(false);
            mainContent.add(details);
        }
    }
    
    private String highlightQuery(String content, String query) {
        if (query == null || query.isEmpty()) {
            return content; // Return original content if query is empty
        }
    
        // Escape special characters in the query for regex
        String escapedQuery = query.replaceAll("([.*+?^=!:${}()|\\[\\]\\/\\\\])", "\\\\$1");
    
        // Replace query occurrences with a highlighted version
        return content.replaceAll("(?i)(" + escapedQuery + ")", "<span class='custom-highlight'>$1</span>");
    }
    

    private static final Map<String, String> scriptureMap = Map.ofEntries(
        Map.entry("Genesis", "Gen"),
        Map.entry("Exodus", "Ex"),
        Map.entry("Leviticus", "Lev"),
        Map.entry("Numbers", "Num"),
        Map.entry("Deuteronomy", "Deut"),
        Map.entry("Joshua", "Josh"),
        Map.entry("Judges", "Judg"),
        Map.entry("Ruth", "Ruth"),
        Map.entry("1 Samuel", "1 Sam"),
        Map.entry("2 Samuel", "2 Sam"),
        Map.entry("1 Kings", "1 Kgs"),
        Map.entry("2 Kings", "2 Kgs"),
        Map.entry("1 Chronicles", "1 Chron"),
        Map.entry("2 Chronicles", "2 Chron"),
        Map.entry("Ezra", "Ezra"),
        Map.entry("Nehemiah", "Neh"),
        Map.entry("Esther", "Esth"),
        Map.entry("Job", "Job"),
        Map.entry("Psalms", "Ps"),
        Map.entry("Proverbs", "Prov"),
        Map.entry("Ecclesiastes", "Eccl"),
        Map.entry("Song of Songs", "Song"),
        Map.entry("Isaiah", "Isa"),
        Map.entry("Jeremiah", "Jer"),
        Map.entry("Lamentations", "Lam"),
        Map.entry("Ezekiel", "Ezek"),
        Map.entry("Daniel", "Dan"),
        Map.entry("Hosea", "Hos"),
        Map.entry("Joel", "Joel"),
        Map.entry("Amos", "Amos"),
        Map.entry("Obadiah", "Obad"),
        Map.entry("Jonah", "Jon"),
        Map.entry("Micah", "Mic"),
        Map.entry("Nahum", "Nah"),
        Map.entry("Habakkuk", "Hab"),
        Map.entry("Zephaniah", "Zeph"),
        Map.entry("Haggai", "Hag"),
        Map.entry("Zechariah", "Zech"),
        Map.entry("Malachi", "Mal"),
        Map.entry("Matthew", "Matt"),
        Map.entry("Mark", "Mark"),
        Map.entry("Luke", "Luke"),
        Map.entry("John", "John"),
        Map.entry("Acts", "Acts"),
        Map.entry("Romans", "Rom"),
        Map.entry("1 Corinthians", "1 Cor"),
        Map.entry("2 Corinthians", "2 Cor"),
        Map.entry("Galatians", "Gal"),
        Map.entry("Ephesians", "Eph"),
        Map.entry("Philippians", "Phil"),
        Map.entry("Colossians", "Col"),
        Map.entry("1 Thessalonians", "1 Thess"),
        Map.entry("2 Thessalonians", "2 Thess"),
        Map.entry("1 Timothy", "1 Tim"),
        Map.entry("2 Timothy", "2 Tim"),
        Map.entry("Titus", "Tit"),
        Map.entry("Philemon", "Phlm"),
        Map.entry("Hebrews", "Heb"),
        Map.entry("James", "Jas"),
        Map.entry("1 Peter", "1 Pet"),
        Map.entry("2 Peter", "2 Pet"),
        Map.entry("1 John", "1 John"),
        Map.entry("2 John", "2 John"),
        Map.entry("3 John", "3 John"),
        Map.entry("Jude", "Jude"),
        Map.entry("Revelation", "Rev")
    );

}