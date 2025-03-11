package com.example.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.model.Document;

public class DocumentDAO {

    private List<Document> allDocuments;

    public DocumentDAO() {
        allDocuments = loadDocumentsFromResources();
    }

    // Load documents from the resources folder
    private List<Document> loadDocumentsFromResources() {
        List<Document> documents = new ArrayList<>();
        try {
            // Directory where documents are stored
            String documentsDirectory = "classes/META-INF/resources/documents/"; // Published Version
            //String documentsDirectory = "src\\main\\resources\\META-INF\\resources\\documents"; // Dev Version

            // Check if the directory exists
            Path directoryPath = Path.of(documentsDirectory);
            if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
                System.err.println("Documents directory not found: " + documentsDirectory);
                return documents;
            }

            // List all text files in the documents directory
            Files.list(directoryPath).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        String content = Files.readString(filePath);
                        String filename = filePath.getFileName().toString();

                        // Assign author and century based on the filename format
                        String[] parts = filename.split(" - ");
                        String title = parts[0];
                        String author = parts.length > 1 ? parts[1] : "Unknown";
                        String century = parts.length > 2 ? determineCenturyFromFilename(parts[2]) : "Unknown Century";

                        // Create a Document object with content, filename, author, and century
                        Document document = new Document(documents.size() + 1, title, content, author, century);
                        documents.add(document);
                    } catch (IOException e) {
                        e.printStackTrace(); // Handle the exception as needed
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
        return documents;
    }

    // Determine the century based on the filename, assuming the format is "document title - AUTHOR - CENTURY"
    private String determineCenturyFromFilename(String centuryStr) {
        // Match century format: e.g., "1st Century", "2nd Century", etc.
        Pattern centuryPattern = Pattern.compile("(\\d+)(st|nd|rd|th)? Century");
        Matcher matcher = centuryPattern.matcher(centuryStr);

        if (matcher.find()) {
            String centuryNumber = matcher.group(1); // The numeric part of the century
            String suffix = matcher.group(2); // The suffix (st, nd, rd, th) if present
            
            // If no suffix is provided in the filename, default to 'th'
            if (suffix == null) {
                suffix = getCenturySuffix(Integer.parseInt(centuryNumber));
            }

            return centuryNumber + suffix + " Century"; // Returns "1st Century", "2nd Century", etc.
        } else {
            return "Unknown Century"; // If century is not found
        }
    }

    // Helper method to determine the correct suffix for the century
    private String getCenturySuffix(int century) {
        if (century % 10 == 1 && century % 100 != 11) {
            return "st";
        } else if (century % 10 == 2 && century % 100 != 12) {
            return "nd";
        } else if (century % 10 == 3 && century % 100 != 13) {
            return "rd";
        } else {
            return "th";
        }
    }

    // Search documents based on the query, selected authors, and selected Scriptures
    public List<Document> searchDocuments(String query, List<String> selectedAuthors, 
                                      List<String> selectedScriptures, String selectedCentury) {
        List<Document> result = new ArrayList<>();

        // If no query is provided, start with all documents
        if (query == null || query.isEmpty()) {
            result = new ArrayList<>(allDocuments);
        } else {
            String lowerQuery = query.toLowerCase();

            for (Document doc : allDocuments) {
                String content = doc.getContent();
                
                // Check if the document content contains the query string
                if (content.toLowerCase().contains(lowerQuery)) {
                    result.add(doc);
                }
            }
        }

        // Filter by author and scripture
        result = filterByAuthor(result, selectedAuthors);
        result = filterByScriptures(result, selectedScriptures, "Any"); // Change filterMode as needed

        // Now filter by century if a selected century is provided
        if (selectedCentury != null && !selectedCentury.isEmpty()) {
            result = filterByCentury(result, selectedCentury);
        }

        return result;
    }

    // Filter documents by the selected century
    private List<Document> filterByCentury(List<Document> documents, String selectedCentury) {
        if (selectedCentury == null || selectedCentury.isEmpty() || "Any".equalsIgnoreCase(selectedCentury)) {
            return documents; // If no century is selected or "Any", return the original list
        }

        List<Document> filteredDocuments = new ArrayList<>();
        for (Document document : documents) {
            String documentCentury = document.getCentury();

            if (documentCentury != null && documentCentury.equalsIgnoreCase(selectedCentury)) {
                filteredDocuments.add(document);
            }
        }

        return filteredDocuments;
    }

    // Filter documents by selected authors
    private List<Document> filterByAuthor(List<Document> documents, List<String> selectedAuthors) {
        if (selectedAuthors == null || selectedAuthors.isEmpty()) {
            return documents;
        }

        List<Document> filteredDocuments = new ArrayList<>();
        List<String> lowerCaseSelectedAuthors = new ArrayList<>();
        for (String author : selectedAuthors) {
            lowerCaseSelectedAuthors.add(author.toLowerCase());
        }

        for (Document document : documents) {
            String documentAuthor = document.getAuthor().toLowerCase();

            if (lowerCaseSelectedAuthors.contains(documentAuthor)) {
                filteredDocuments.add(document);
            }
        }
        return filteredDocuments;
    }

    // Filter documents by selected Scriptures using whole-word matching (case-insensitive)
    private List<Document> filterByScriptures(List<Document> documents, List<String> selectedScriptures, String filterMode) {
        // List of valid scripture abbreviations
        List<String> scriptureAbbreviations = List.of(
            "Gen", "Ex", "Lev", "Num", "Deut", "Josh", "Judg", "Ruth", "1 Sam", "2 Sam", "1 Kings", "2 Kings",
            "1 Chron", "2 Chron", "Ezra", "Neh", "Est", "Job", "Ps", "Prov", "Eccl", "Song", "Isa", "Jer",
            "Lam", "Ezek", "Dan", "Hos", "Joel", "Amos", "Obad", "Jon", "Mic", "Nah", "Hab", "Zeph",
            "Hag", "Zech", "Mal", "Matt", "Mark", "Luke", "John", "Acts", "Rom", "1 Cor", "2 Cor", "Gal", "Eph",
            "Phil", "Col", "1 Thess", "2 Thess", "1 Tim", "2 Tim", "Tit", "Phlm", "Heb", "Jas", "1 Pet",
            "2 Pet", "1 Jn", "2 Jn", "3 Jn", "Jude", "Rev"
        );
    
        if (selectedScriptures == null || selectedScriptures.isEmpty()) {
            return documents;
        }
    
        List<Document> filteredDocuments = new ArrayList<>();
    
        for (Document document : documents) {
            String content = document.getContent();
    
            boolean matchesScripture;
    
            if ("Any".equalsIgnoreCase(filterMode)) {
                // "Any" mode: Check if any of the selected scriptures is present
                matchesScripture = selectedScriptures.stream().anyMatch(scripture -> {
                    if (!scriptureAbbreviations.contains(scripture)) {
                        return false; // Skip invalid scripture abbreviations
                    }
                    String regex = "\\b" + Pattern.quote(scripture) + "\\b"; // Whole-word match
                    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(content);
                    return matcher.find();
                });
            } else if ("All".equalsIgnoreCase(filterMode)) {
                // "All" mode: Check if all selected scriptures are present
                matchesScripture = selectedScriptures.stream().allMatch(scripture -> {
                    if (!scriptureAbbreviations.contains(scripture)) {
                        return false; // Skip invalid scripture abbreviations
                    }
                    String regex = "\\b" + Pattern.quote(scripture) + "\\b"; // Whole-word match
                    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(content);
                    return matcher.find();
                });
            } else {
                // Invalid filter mode, default to "Any"
                System.err.println("Invalid filter mode: " + filterMode + ". Defaulting to 'Any' mode.");
                matchesScripture = selectedScriptures.stream().anyMatch(scripture -> {
                    if (!scriptureAbbreviations.contains(scripture)) {
                        return false; // Skip invalid scripture abbreviations
                    }
                    String regex = "\\b" + Pattern.quote(scripture) + "\\b"; // Whole-word match
                    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(content);
                    return matcher.find();
                });
            }
    
            // If the document matches the scripture filter, add it to the filtered list
            if (matchesScripture) {
                filteredDocuments.add(document);
            }
        }
        return filteredDocuments;
    }
}