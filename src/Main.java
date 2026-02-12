import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.time.LocalDate;

// ------------------------ Book Class ------------------------
class Book {
    private String id;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public void borrow() { isAvailable = false; }
    public void returned() { isAvailable = true; }

    @Override
    public String toString() {
        return id + " | " + title + " by " + author + (isAvailable ? " (Available)" : " (Borrowed)");
    }
}

// ------------------------ Member Class ------------------------
class Member {
    private String memberId;
    private String name;
    private String password;
    private ArrayList<Book> borrowedBooks;

    public Member(String memberId, String name, String password) {
        this.memberId = memberId;
        this.name = name;
        this.password = password;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public boolean authenticate(String password) { return this.password.equals(password); }
    public ArrayList<Book> getBorrowedBooks() { return borrowedBooks; }
    public void borrowBook(Book book) { borrowedBooks.add(book); }
    public void returnBook(Book book) { borrowedBooks.remove(book); }
}

// ------------------------ LibrarySystem Class ------------------------
class LibrarySystem {
    private ArrayList<Book> books;
    private ArrayList<Member> members;

    public LibrarySystem() {
        books = new ArrayList<>();
        members = new ArrayList<>();
    }

    public void addBook(Book book) { books.add(book); }
    public ArrayList<Book> getBooks() { return books; }
    public void addMember(Member member) { members.add(member); }
    public Member findMember(String id) {
        for (Member m : members) if (m.getMemberId().equals(id)) return m;
        return null;
    }
    public Book findBook(String id) {
        for (Book b : books) if (b.getId().equals(id)) return b;
        return null;
    }
}

// ------------------------ LibraryGUI Class ------------------------
class LibraryGUI {
    private LibrarySystem system;
    private Member currentMember;
    private JFrame frame;
    private JPanel sidebar;
    private JPanel mainPanel; // Main content panel
    private JButton registerBtn, loginBtn, viewBtn, borrowBtn, returnBtn, myBooksBtn, exitBtn;

    public LibraryGUI(LibrarySystem system) {
        this.system = system;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Library Management System");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Sidebar
        sidebar = new JPanel(new GridLayout(7, 1, 5, 5));
        sidebar.setBackground(new Color(230, 230, 250)); // light lavender

        registerBtn = createSidebarButton("Register Member");
        loginBtn = createSidebarButton("Member Login");
        viewBtn = createSidebarButton("View Books");
        borrowBtn = createSidebarButton("Borrow Book");
        returnBtn = createSidebarButton("Return Book");
        myBooksBtn = createSidebarButton("My Borrowed Books");
        exitBtn = createSidebarButton("Exit");

        sidebar.add(registerBtn);
        sidebar.add(loginBtn);
        sidebar.add(viewBtn);
        sidebar.add(borrowBtn);
        sidebar.add(returnBtn);
        sidebar.add(myBooksBtn);
        sidebar.add(exitBtn);

        frame.add(sidebar, BorderLayout.WEST);

        // Main panel for content
        mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        // Button actions
        registerBtn.addActionListener(e -> { highlightButton(registerBtn); registerMember(); });
        loginBtn.addActionListener(e -> { highlightButton(loginBtn); memberLogin(); });
        viewBtn.addActionListener(e -> { highlightButton(viewBtn); viewBooks(); });
        borrowBtn.addActionListener(e -> { highlightButton(borrowBtn); borrowBook(); });
        returnBtn.addActionListener(e -> { highlightButton(returnBtn); returnBook(); });
        myBooksBtn.addActionListener(e -> { highlightButton(myBooksBtn); myBorrowedBooks(); });
        exitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(245, 245, 245));
        btn.setFocusPainted(false);
        btn.setForeground(Color.BLACK);
        return btn;
    }

    private void highlightButton(JButton btn) {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) c.setBackground(new Color(245, 245, 245));
        }
        btn.setBackground(new Color(100, 149, 237)); // Cornflower blue
    }

    // -------------------- GUI Functions --------------------
    private void registerMember() {
        JPanel panel = new JPanel(new GridLayout(0,1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JPasswordField passField = new JPasswordField();

        panel.add(new JLabel("Member ID:")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Password:")); panel.add(passField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Register Member", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Member m = new Member(idField.getText(), nameField.getText(), new String(passField.getPassword()));
            system.addMember(m);
            JOptionPane.showMessageDialog(frame, "Member registered successfully!");
        }
    }

    private void memberLogin() {
        JPanel panel = new JPanel(new GridLayout(0,1));
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();

        panel.add(new JLabel("Member ID:")); panel.add(idField);
        panel.add(new JLabel("Password:")); panel.add(passField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Member Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Member m = system.findMember(idField.getText());
            if (m != null && m.authenticate(new String(passField.getPassword()))) {
                currentMember = m;
                JOptionPane.showMessageDialog(frame, "Login successful! Welcome " + m.getName());
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials!");
            }
        }
    }

    private void viewBooks() {
        mainPanel.removeAll(); // Clear previous content
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-25s %-20s %-12s\n", "ID", "Title", "Author", "Status"));
        sb.append("-----------------------------------------------------------\n");
        for (Book b : system.getBooks()) {
            sb.append(String.format("%-6s %-25s %-20s %-12s\n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.isAvailable() ? "Available" : "Borrowed"));
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(area);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void borrowBook() {
        if (currentMember == null) { JOptionPane.showMessageDialog(frame, "Login first!"); return; }

        String bookId = JOptionPane.showInputDialog(frame, "Enter Book ID to borrow:");
        if (bookId != null) {
            Book b = system.findBook(bookId);
            if (b != null && b.isAvailable()) {
                b.borrow();
                currentMember.borrowBook(b);
                JOptionPane.showMessageDialog(frame, "Book borrowed successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Book unavailable or invalid ID.");
            }
        }
    }

    private void returnBook() {
        if (currentMember == null) { JOptionPane.showMessageDialog(frame, "Login first!"); return; }

        String bookId = JOptionPane.showInputDialog(frame, "Enter Book ID to return:");
        if (bookId != null) {
            Book b = system.findBook(bookId);
            if (b != null && currentMember.getBorrowedBooks().contains(b)) {
                b.returned();
                currentMember.returnBook(b);
                JOptionPane.showMessageDialog(frame, "Book returned successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid book ID or you did not borrow this book.");
            }
        }
    }

    private void myBorrowedBooks() {
        if (currentMember == null) { JOptionPane.showMessageDialog(frame, "Login first!"); return; }

        mainPanel.removeAll();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-25s %-20s %-12s\n", "ID", "Title", "Author", "Status"));
        sb.append("-----------------------------------------------------------\n");
        for (Book b : currentMember.getBorrowedBooks()) {
            sb.append(String.format("%-6s %-25s %-20s %-12s\n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.isAvailable() ? "Available" : "Borrowed"));
        }
        if (currentMember.getBorrowedBooks().isEmpty()) sb.append("No borrowed books.\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(area);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}

