import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * Patient Information System
 * Uses only plain arrays (no ArrayList).
 * Each patient is stored at the same index across the parallel arrays.
 */
public class PatientRecordSystem {

    static final int MAX = 100;              // maximum number of patients

    // Parallel arrays: index i in every array belongs to the same patient
    static int[] ids = new int[MAX];
    static String[] names = new String[MAX];
    static int[] ages = new int[MAX];
    static String[] genders = new String[MAX];
    static String[] dates = new String[MAX];       // admission date, format yyyy-mm-dd
    static String[] diagnoses = new String[MAX];

    static int count = 0;                    // how many patients are stored
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            showMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addPatient();
                    break;
                case 2:
                    deletePatient();
                    break;
                case 3:
                    displayAll();
                    break;
                case 4:
                    updatePatient();
                    break;
                case 5:
                    searchByName();
                    break;
                case 6:
                    viewDetails();
                    break;
                case 7:
                    searchById();
                    break;
                case 8:
                    sortMenu();
                    break;
                case 9:
                    saveToExcel();
                    break;
                case 10:
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please pick 1-10.");
            }
        } while (choice != 10);
    }

    // ---------------------------------------------------------------
    // MENU
    // ---------------------------------------------------------------
    static void showMenu() {
        System.out.println("\n==============================");
        System.out.println("   PATIENT INFORMATION SYSTEM");
        System.out.println("==============================");
        System.out.println("1. Add Patient Record");
        System.out.println("2. Delete Patient Record");
        System.out.println("3. Display All Records");
        System.out.println("4. Update Patient Record");
        System.out.println("5. Search Patient (by name)");
        System.out.println("6. View Patient Details");
        System.out.println("7. Search Patient ID");
        System.out.println("8. Sort Patient Records");
        System.out.println("9. Save Records to Excel");
        System.out.println("10. Exit Program");
        System.out.println("==============================");
    }

    // ---------------------------------------------------------------
    // 1. ADD
    // ---------------------------------------------------------------
    static void addPatient() {
        if (count == MAX) {
            System.out.println("Records are full. Cannot add more patients.");
            return;
        }

        int id = readInt("Enter Patient ID: ");
        if (findIndexById(id) != -1) {
            System.out.println("That ID already exists. Patient not added.");
            return;
        }

        ids[count] = id;

        // each detail is asked on its own, one step at a time
        names[count] = readNonEmpty("Enter Name: ");
        System.out.println("Name saved.\n");

        ages[count] = readInt("Enter Age: ");
        System.out.println("Age saved.\n");

        System.out.print("Enter Gender: ");
        genders[count] = sc.nextLine();
        System.out.print("Enter Admission Date (yyyy-mm-dd): ");
        dates[count] = sc.nextLine();
        System.out.print("Enter Diagnosis: ");
        diagnoses[count] = sc.nextLine();

        count++;
        System.out.println("Patient record added successfully!");
    }

    // ---------------------------------------------------------------
    // 2. DELETE
    // ---------------------------------------------------------------
    static void deletePatient() {
        if (count == 0) {
            System.out.println("No records to delete.");
            return;
        }

        int id = readInt("Enter Patient ID to delete: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient not found.");
            return;
        }

        // shift every record after the deleted one to the left
        for (int i = index; i < count - 1; i++) {
            ids[i] = ids[i + 1];
            names[i] = names[i + 1];
            ages[i] = ages[i + 1];
            genders[i] = genders[i + 1];
            dates[i] = dates[i + 1];
            diagnoses[i] = diagnoses[i + 1];
        }
        count--;
        System.out.println("Patient record deleted.");
    }

    // ---------------------------------------------------------------
    // 3. DISPLAY ALL
    // ---------------------------------------------------------------
    static void displayAll() {
        if (count == 0) {
            System.out.println("No patient records yet.");
            return;
        }

        System.out.println("\n--- ALL PATIENT RECORDS ---");
        System.out.printf("%-6s %-20s %-5s %-8s %-12s %-20s%n",
                "ID", "Name", "Age", "Gender", "Date", "Diagnosis");
        System.out.println("---------------------------------------------------------------------------");
        for (int i = 0; i < count; i++) {
            System.out.printf("%-6d %-20s %-5d %-8s %-12s %-20s%n",
                    ids[i], names[i], ages[i], genders[i], dates[i], diagnoses[i]);
        }
        System.out.println("Total patients: " + count);
    }

    // ---------------------------------------------------------------
    // 4. UPDATE
    // ---------------------------------------------------------------
    static void updatePatient() {
        int id = readInt("Enter Patient ID to update: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.println("Leave a field blank to keep the current value.");

        System.out.print("New Name [" + names[index] + "]: ");
        String input = sc.nextLine();
        if (!input.equals("")) names[index] = input;

        System.out.print("New Age [" + ages[index] + "]: ");
        input = sc.nextLine();
        if (!input.equals("")) {
            try {
                ages[index] = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid age. Keeping old value.");
            }
        }

        System.out.print("New Gender [" + genders[index] + "]: ");
        input = sc.nextLine();
        if (!input.equals("")) genders[index] = input;

        System.out.print("New Admission Date [" + dates[index] + "]: ");
        input = sc.nextLine();
        if (!input.equals("")) dates[index] = input;

        System.out.print("New Diagnosis [" + diagnoses[index] + "]: ");
        input = sc.nextLine();
        if (!input.equals("")) diagnoses[index] = input;

        System.out.println("Patient record updated.");
    }

    // ---------------------------------------------------------------
    // 5. SEARCH BY NAME (partial match, ignores upper/lower case)
    // ---------------------------------------------------------------
    static void searchByName() {
        System.out.print("Enter name to search: ");
        String keyword = sc.nextLine().toLowerCase();
        boolean found = false;

        for (int i = 0; i < count; i++) {
            if (names[i].toLowerCase().contains(keyword)) {
                System.out.println("ID: " + ids[i] + " | Name: " + names[i]
                        + " | Diagnosis: " + diagnoses[i]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No patient found with that name.");
        }
    }

    // ---------------------------------------------------------------
    // 6. VIEW PATIENT DETAILS
    // ---------------------------------------------------------------
    static void viewDetails() {
        int id = readInt("Enter Patient ID to view: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.println("\n--- PATIENT DETAILS ---");
        System.out.println("Patient ID     : " + ids[index]);
        System.out.println("Name           : " + names[index]);
        System.out.println("Age            : " + ages[index]);
        System.out.println("Gender         : " + genders[index]);
        System.out.println("Admission Date : " + dates[index]);
        System.out.println("Diagnosis      : " + diagnoses[index]);
    }

    // ---------------------------------------------------------------
    // 7. SEARCH PATIENT ID (checks if the ID exists and where it is)
    // ---------------------------------------------------------------
    static void searchById() {
        int id = readInt("Enter Patient ID to search: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Patient ID " + id + " does not exist.");
        } else {
            System.out.println("Patient ID " + id + " found! Name: " + names[index]
                    + " (record #" + (index + 1) + ")");
        }
    }

    // ---------------------------------------------------------------
    // 8. SORTING
    // ---------------------------------------------------------------
    static void sortMenu() {
        if (count < 2) {
            System.out.println("Need at least 2 records to sort.");
            return;
        }

        System.out.println("\nSort by:");
        System.out.println("1. Name (alphabetically)");
        System.out.println("2. Admission Date");
        System.out.println("3. Record (Patient ID)");
        int option = readInt("Enter option: ");

        if (option < 1 || option > 3) {
            System.out.println("Invalid option.");
            return;
        }

        // Bubble sort: compare neighbors and swap them if out of order
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                boolean swap = false;

                if (option == 1) {
                    swap = names[j].compareToIgnoreCase(names[j + 1]) > 0;
                } else if (option == 2) {
                    // yyyy-mm-dd dates sort correctly as plain text
                    swap = dates[j].compareTo(dates[j + 1]) > 0;
                } else {
                    swap = ids[j] > ids[j + 1];
                }

                if (swap) {
                    swapRecords(j, j + 1);
                }
            }
        }

        System.out.println("Records sorted successfully!");
        displayAll();
    }

    // swaps ALL fields of two patients so the record stays together
    static void swapRecords(int a, int b) {
        int tempInt = ids[a];
        ids[a] = ids[b];
        ids[b] = tempInt;

        String tempStr = names[a];
        names[a] = names[b];
        names[b] = tempStr;

        tempInt = ages[a];
        ages[a] = ages[b];
        ages[b] = tempInt;

        tempStr = genders[a];
        genders[a] = genders[b];
        genders[b] = tempStr;

        tempStr = dates[a];
        dates[a] = dates[b];
        dates[b] = tempStr;

        tempStr = diagnoses[a];
        diagnoses[a] = diagnoses[b];
        diagnoses[b] = tempStr;
    }

    // ---------------------------------------------------------------
    // 9. SAVE TO EXCEL
    // Writes every record to a .csv file. Excel opens .csv files
    // directly as a spreadsheet, and no extra library is needed.
    // ---------------------------------------------------------------
    static void saveToExcel() {
        if (count == 0) {
            System.out.println("No records to save.");
            return;
        }

        System.out.print("Enter file name (press Enter for patients.csv): ");
        String fileName = sc.nextLine().trim();
        if (fileName.equals("")) {
            fileName = "patients.csv";
        }
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName = fileName + ".csv";
        }

        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileName));

            // first row = column headings
            out.println("Patient ID,Name,Age,Gender,Admission Date,Diagnosis");

            // one row per patient
            for (int i = 0; i < count; i++) {
                out.println(ids[i] + "," + csv(names[i]) + "," + ages[i] + ","
                        + csv(genders[i]) + "," + csv(dates[i]) + "," + csv(diagnoses[i]));
            }

            out.close();
            System.out.println(count + " record(s) saved!");
            // show the full location so the user knows where to find it
            System.out.println("File location: " + new File(fileName).getAbsolutePath());
            System.out.println("You can open this file with Excel or import it to Google Sheets.");
        } catch (IOException e) {
            System.out.println("Could not save the file: " + e.getMessage());
        }
    }

    // wraps text in quotes so commas inside it do not break the columns
    static String csv(String text) {
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    // ---------------------------------------------------------------
    // HELPER METHODS
    // ---------------------------------------------------------------

    // returns the array index of a patient ID, or -1 if not found
    static int findIndexById(int id) {
        for (int i = 0; i < count; i++) {
            if (ids[i] == id) {
                return i;
            }
        }
        return -1;
    }

    // keeps asking until the user types something (not blank)
    static String readNonEmpty(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            if (!input.equals("")) {
                return input;
            }
            System.out.println("This cannot be empty. Please try again.");
        }
    }

    // keeps asking until the user types a valid whole number
    static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
