Περίπτωση Χρήσης 9: Αναφορά

Βήματα Happy Path:
1.  Ο χρήστης (ενοικιαστής/ιδιοκτήτης) επιλέγει αναφορά σε αγγελία
2.  Το σύστημα εμφανίζει φόρμα με πεδία για τον λόγο της αναφοράς, την προσθήκη σχολίων και επισύναψη αρχείων ή/και πολυμέσων
3.  Ο χρήστης (ενοικιαστής/ιδιοκτήτης) γράφει/επισυνάπτει σχόλια/πολυμέσα
4.  Το σύστημα αποθηκεύει προσωρινά την αναφορά
5.  Το σύστημα εμφανίζει την προεπισκόπησή της     
6.  Ο χρήστης (ενοικιαστής/ιδιοκτήτης) επιλέγει την επιβεβαίωση της υποβολής της αναφοράς
7.  Το σύστημα ελέγχει την υποβολή για μη αποδεκτές πρακτικές (απρεπή γλώσσα και συνημμένα)
8.  Το σύστημα αποθηκεύει την αναφορά στη βάση δεδομένων
9.  Το σύστημα εμφανίζει στον χρήστη (ενοικιαστής/ιδιοκτήτης) μήνυμα επιβεβαίωσης της υποβολής αναφοράς 
10. Το σύστημα μεταβιβάζει τον χρήστη (ενοικιαστής/ιδιοκτήτης) στην οθόνη αναζήτησης 

Εναλλακτικές Ροές
1. Απρεπή Γλώσσα και Συνημμένα (από βήμα 7 της βασικής)
9.1.1 Το σύστημα εντοπίζει απρεπή γλώσσα ή/και μη αποδεκτά συνημμένα στην αναφορά και εμφανίζει μήνυμα σφάλματος στον χρήστη (ενοικιαστής/ιδιοκτήτης) για την απρεπή γλώσσα ή/και τα μη αποδεκτά συνημμένα 
9.1.2 Το σύστημα μεταβιβάζει το χρήστη στην φόρμα συμπλήρωσης αναφοράς με τα στοιχεία που έχει συμπληρώσει
9.1.3 Επιστροφή στο βήμα 3 της βασικής ροής 

2. Ακύρωση αναφοράς (από βήμα 3 και 6 της βασικής)
9.2.1 Ο χρήστης (ενοικιαστής/ιδιοκτήτης) επιλέγει την έξοδο
9.2.2 Το σύστημα επιστρέφει τον χρήστη (ενοικιαστής/ιδιοκτήτης) στην αρχική οθόνη
