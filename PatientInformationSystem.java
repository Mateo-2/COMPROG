import java.util.Scanner;

/*
 * Patient Information System
 * --------------------------
 * A simple menu-driven Java console application that stores patient records.
 *
 * Concepts used:
 *  - Variables and data types
 *  - Input and output (Scanner / System.out)
 *  - Operators
 *  - Conditional statements (if, if-else, else-if, switch)
 *  - Loops (for, while, do-while)
 *  - Arrays (plain arrays only, NO ArrayList)
 *  - Methods
 *  - String manipulation (trim, toLowerCase, contains, compareTo, etc.)
 *  - Basic validation and restriction
 *  - Menu-driven programming
 *
 * How the data is stored:
 *  Each patient is stored at the same index in several "parallel" arrays.
 *  Example: patientIds[0], names[0], ages[0] ... all belong to patient #1.
 */
public class PatientInformationSystem {

    // ---------- Constants ----------
    static final int MAX_PATIENTS = 100;   // maximum number of records

    // ---------- Parallel arrays (the "database") ----------
    static int[] patientIds = new int[MAX_PATIENTS];
    static String[] names = new String[MAX_PATIENTS];
    static int[] ages = new int[MAX_PATIENTS];
    static String[] genders = new String[MAX_PATIENTS];
    static String[] diagnoses = new String[MAX_PATIENTS];
    static String[] admissionDates = new String[MAX_PATIENTS]; // format: YYYY-MM-DD

    // How many records are currently stored
    static int count = 0;

    static Scanner input = new Scanner(System.in);

    // =====================================================
    //                        MAIN
    // =====================================================
    public static void main(String[] args) {
        int choice;

        System.out.println("=======================================");
        System.out.println("   WELCOME TO PATIENT INFORMATION SYSTEM");
        System.out.println("=======================================");

        do {
            showMenu();
            choice = readInt("Enter your choice (1-9): ", 1, 9);

            switch (choice) {
                case 1:
                    addPatient();
                    break;
                case 2:
                    deletePatient();
                    break;
                case 3:
                    displayAllPatients();
                    break;
                case 4:
                    updatePatient();
                    break;
                case 5:
                    searchPatientByName();
                    break;
                case 6:
                    viewPatientDetails();
                    break;
                case 7:
                    searchPatientById();
                    break;
                case 8:
                    sortMenu();
                    break;
                case 9:
                    System.out.println("\nThank you for using the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 9);

        input.close();
    }

    // =====================================================
    //                      MENU METHODS
    // =====================================================
    static void showMenu() {
        System.out.println("\n============== MAIN MENU ==============");
        System.out.println("1. Add Patient Record");
        System.out.println("2. Delete Patient Record");
        System.out.println("3. Display All Records");
        System.out.println("4. Update Patient Record");
        System.out.println("5. Search Patient (by name)");
        System.out.println("6. View Patient Details");
        System.out.println("7. Search Patient ID");
        System.out.println("8. Sort Patient Records");
        System.out.println("9. Exit Program");
        System.out.println("=======================================");
    }

    static void sortMenu() {
        if (count == 0) {
            System.out.println("\nNo records to sort.");
            return;
        }

        System.out.println("\n--- SORT PATIENT RECORDS ---");
        System.out.println("1. Alphabetically (by name)");
        System.out.println("2. By admission date (oldest first)");
        System.out.println("3. By record (patient ID)");
        System.out.println("4. Back to main menu");
        int choice = readInt("Choose a sort option (1-4): ", 1, 4);

        if (choice == 4) {
            return;
        }

        sortRecords(choice);

        if (choice == 1) {
            System.out.println("\nRecords sorted alphabetically by name.");
        } else if (choice == 2) {
            System.out.println("\nRecords sorted by admission date.");
        } else {
            System.out.println("\nRecords sorted by patient ID.");
        }
        displayAllPatients();
    }

    // =====================================================
    //                    CORE FEATURES
    // =====================================================

    // ---------- 1. ADD ----------
    static void addPatient() {
        System.out.println("\n--- ADD PATIENT RECORD ---");

        if (count >= MAX_PATIENTS) {
            System.out.println("Storage is full. Cannot add more patients.");
            return;
        }

        // Patient ID (must be positive and unique)
        int id;
        while (true) {
            id = readInt("Enter Patient ID (positive number): ", 1, 999999);
            if (findIndexById(id) == -1) {
                break; // ID is not yet used
            }
            System.out.println("That ID already exists. Please use a different ID.");
        }

        String name = readName("Enter Full Name: ");
        int age = readInt("Enter Age: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        String gender = readGender();
        String diagnosis = readNonEmpty("Enter Diagnosis: ");
        String date = readDate("Enter Admission Date (YYYY-MM-DD): ");

        // Store in the next free position
        patientIds[count] = id;
        names[count] = name;
        ages[count] = age;
        genders[count] = gender;
        diagnoses[count] = diagnosis;
        admissionDates[count] = date;
        count++;

        System.out.println("\nPatient record added successfully!");
    }

    // ---------- 2. DELETE ----------
    static void deletePatient() {
        System.out.println("\n--- DELETE PATIENT RECORD ---");

        if (count == 0) {
            System.out.println("No records to delete.");
            return;
        }

        int id = readInt("Enter Patient ID to delete: ", 1, 999999);
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient ID not found.");
            return;
        }

        printPatientDetails(index);
        String confirm = readNonEmpty("Are you sure you want to delete this record? (y/n): ");

        if (confirm.equalsIgnoreCase("y")) {
            // Shift every record after 'index' one step to the left
            for (int i = index; i < count - 1; i++) {
                patientIds[i] = patientIds[i + 1];
                names[i] = names[i + 1];
                ages[i] = ages[i + 1];
                genders[i] = genders[i + 1];
                diagnoses[i] = diagnoses[i + 1];
                admissionDates[i] = admissionDates[i + 1];
            }
            // Clear the last (now duplicate) slot
            count--;
            patientIds[count] = 0;
            names[count] = null;
            ages[count] = 0;
            genders[count] = null;
            diagnoses[count] = null;
            admissionDates[count] = null;

            System.out.println("Record deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ---------- 3. DISPLAY ALL ----------
    static void displayAllPatients() {
        System.out.println("\n--- ALL PATIENT RECORDS ---");

        if (count == 0) {
            System.out.println("No patient records found.");
            return;
        }

        System.out.printf("%-8s %-25s %-5s %-8s %-20s %-12s%n",
                "ID", "Name", "Age", "Gender", "Diagnosis", "Admitted");
        System.out.println("---------------------------------------------------------------------------------");

        for (int i = 0; i < count; i++) {
            System.out.printf("%-8d %-25s %-5d %-8s %-20s %-12s%n",
                    patientIds[i], names[i], ages[i], genders[i],
                    diagnoses[i], admissionDates[i]);
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Total patients: " + count);
    }

    // ---------- 4. UPDATE ----------
    static void updatePatient() {
        System.out.println("\n--- UPDATE PATIENT RECORD ---");

        if (count == 0) {
            System.out.println("No records to update.");
            return;
        }

        int id = readInt("Enter Patient ID to update: ", 1, 999999);
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient ID not found.");
            return;
        }

        printPatientDetails(index);

        int choice;
        do {
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Name");
            System.out.println("2. Age");
            System.out.println("3. Gender");
            System.out.println("4. Diagnosis");
            System.out.println("5. Admission Date");
            System.out.println("6. Done");
            choice = readInt("Enter choice (1-6): ", 1, 6);

            if (choice == 1) {
                names[index] = readName("Enter new Name: ");
                System.out.println("Name updated.");
            } else if (choice == 2) {
                ages[index] = readInt("Enter new Age: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
                System.out.println("Age updated.");
            } else if (choice == 3) {
                genders[index] = readGender();
                System.out.println("Gender updated.");
            } else if (choice == 4) {
                diagnoses[index] = readNonEmpty("Enter new Diagnosis: ");
                System.out.println("Diagnosis updated.");
            } else if (choice == 5) {
                admissionDates[index] = readDate("Enter new Admission Date (YYYY-MM-DD): ");
                System.out.println("Admission date updated.");
            }
        } while (choice != 6);

        System.out.println("\nUpdated record:");
        printPatientDetails(index);
    }

    // ---------- 5. SEARCH BY NAME ----------
    static void searchPatientByName() {
        System.out.println("\n--- SEARCH PATIENT BY NAME ---");

        if (count == 0) {
            System.out.println("No records to search.");
            return;
        }

        String keyword = readNonEmpty("Enter name (or part of a name): ").toLowerCase();
        boolean found = false;

        for (int i = 0; i < count; i++) {
            // Case-insensitive partial match using String manipulation
            if (names[i].toLowerCase().contains(keyword)) {
                if (!found) {
                    System.out.println("\nMatching records:");
                    System.out.printf("%-8s %-25s %-5s %-8s %-20s %-12s%n",
                            "ID", "Name", "Age", "Gender", "Diagnosis", "Admitted");
                    System.out.println("---------------------------------------------------------------------------------");
                }
                System.out.printf("%-8d %-25s %-5d %-8s %-20s %-12s%n",
                        patientIds[i], names[i], ages[i], genders[i],
                        diagnoses[i], admissionDates[i]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No patient found with that name.");
        }
    }

    // ---------- 6. VIEW DETAILS ----------
    static void viewPatientDetails() {
        System.out.println("\n--- VIEW PATIENT DETAILS ---");

        if (count == 0) {
            System.out.println("No records available.");
            return;
        }

        int id = readInt("Enter Patient ID: ", 1, 999999);
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient ID not found.");
        } else {
            printPatientDetails(index);
        }
    }

    // ---------- 7. SEARCH PATIENT ID ----------
    // Looks up an ID and reports whether it exists (and whose it is).
    static void searchPatientById() {
        System.out.println("\n--- SEARCH PATIENT ID ---");

        if (count == 0) {
            System.out.println("No records to search.");
            return;
        }

        int id = readInt("Enter Patient ID to search: ", 1, 999999);
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient ID " + id + " does NOT exist in the system.");
        } else {
            System.out.println("Patient ID " + id + " EXISTS. Patient name: " + names[index]
                    + " (record position #" + (index + 1) + ")");
        }
    }

    // ---------- 8. SORTING (Bubble Sort) ----------
    // sortType: 1 = name, 2 = date, 3 = ID
    static void sortRecords(int sortType) {
        for (int pass = 0; pass < count - 1; pass++) {
            for (int j = 0; j < count - 1 - pass; j++) {
                if (compareRecords(j, j + 1, sortType) > 0) {
                    swapRecords(j, j + 1);
                }
            }
        }
    }

    // Returns > 0 if record a should come AFTER record b
    static int compareRecords(int a, int b, int sortType) {
        if (sortType == 1) {
            return names[a].compareToIgnoreCase(names[b]);
        } else if (sortType == 2) {
            // YYYY-MM-DD strings sort correctly as plain text
            return admissionDates[a].compareTo(admissionDates[b]);
        } else {
            return patientIds[a] - patientIds[b];
        }
    }

    // Swaps ALL the data of two records so they stay together
    static void swapRecords(int a, int b) {
        int tempId = patientIds[a];
        patientIds[a] = patientIds[b];
        patientIds[b] = tempId;

        String tempName = names[a];
        names[a] = names[b];
        names[b] = tempName;

        int tempAge = ages[a];
        ages[a] = ages[b];
        ages[b] = tempAge;

        String tempGender = genders[a];
        genders[a] = genders[b];
        genders[b] = tempGender;

        String tempDiagnosis = diagnoses[a];
        diagnoses[a] = diagnoses[b];
        diagnoses[b] = tempDiagnosis;

        String tempDate = admissionDates[a];
        admissionDates[a] = admissionDates[b];
        admissionDates[b] = tempDate;
    }

    // =====================================================
    //                   HELPER METHODS
    // =====================================================

    // Returns the array index of a patient ID, or -1 if not found
    static int findIndexById(int id) {
        for (int i = 0; i < count; i++) {
            if (patientIds[i] == id) {
                return i;
            }
        }
        return -1;
    }

    static void printPatientDetails(int index) {
        System.out.println("\n-----------------------------");
        System.out.println("Patient ID     : " + patientIds[index]);
        System.out.println("Name           : " + names[index]);
        System.out.println("Age            : " + ages[index]);
        System.out.println("Gender         : " + genders[index]);
        System.out.println("Diagnosis      : " + diagnoses[index]);
        System.out.println("Admission Date : " + admissionDates[index]);
        System.out.println("-----------------------------");
    }

    // =====================================================
    //              INPUT + VALIDATION METHODS
    // =====================================================

    // Keeps asking until the user types a whole number within min..max
    static int readInt(String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();

            try {
                value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Keeps asking until the user types something that isn't blank
    static String readNonEmpty(String prompt) {
        String text;
        while (true) {
            System.out.print(prompt);
            text = input.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    // Name must contain only letters, spaces, dots, or hyphens
    static String readName(String prompt) {
        while (true) {
            String text = readNonEmpty(prompt);
            boolean valid = true;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (!(Character.isLetter(c) || c == ' ' || c == '.' || c == '-')) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                return text;
            }
            System.out.println("Name may only contain letters, spaces, dots, and hyphens.");
        }
    }

    // Gender: accepts M or F only
    static String readGender() {
        while (true) {
            String text = readNonEmpty("Enter Gender (M/F): ").toUpperCase();
            if (text.equals("M")) {
                return "Male";
            } else if (text.equals("F")) {
                return "Female";
            }
            System.out.println("Please type M for Male or F for Female.");
        }
    }

    // Date must look like YYYY-MM-DD and be a real calendar date
    static String readDate(String prompt) {
        while (true) {
            String text = readNonEmpty(prompt);
            if (isValidDate(text)) {
                return text;
            }
            System.out.println("Invalid date. Use the format YYYY-MM-DD (example: 2026-11-22).");
        }
    }

    static boolean isValidDate(String date) {
        // Must be exactly 10 characters with dashes in the right places
        if (date.length() != 10 || date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

        // All other characters must be digits
        for (int i = 0; i < date.length(); i++) {
            if (i == 4 || i == 7) {
                continue;
            }
            if (!Character.isDigit(date.charAt(i))) {
                return false;
            }
        }

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int day = Integer.parseInt(date.substring(8, 10));

        if (year < 1900 || year > 2100) {
            return false;
        }
        if (month < 1 || month > 12) {
            return false;
        }

        // Days in each month (index 0 = January)
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // Leap year check
        boolean leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        if (leap) {
            daysInMonth[1] = 29;
        }

        return day >= 1 && day <= daysInMonth[month - 1];
    }
}
