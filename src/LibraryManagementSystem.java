import javax.swing.*;

// ------------------------ Main Class ------------------------
public class LibraryManagementSystem {
    public static void main(String[] args) {
        LibrarySystem system = new LibrarySystem();

        // Add 5 sample books
        system.addBook(new Book("B001", "Java Programming", "Author A"));
        system.addBook(new Book("B002", "Data Structures", "Author B"));
        system.addBook(new Book("B003", "Algorithms", "Author C"));
        system.addBook(new Book("B004", "Database Systems", "Author D"));
        system.addBook(new Book("B005", "Operating Systems", "Author E"));

        SwingUtilities.invokeLater(() -> new LibraryGUI(system));
    }
}
