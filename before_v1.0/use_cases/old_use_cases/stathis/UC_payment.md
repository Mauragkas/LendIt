Περίπτωση Χρήσης 3: Πληρωμή

Βήματα Happy Path:
1.  Το σύστημα καταγράφει την επιλογή του εργαλείου που επέλεξε ο ενοικιαστής 
2.  Το σύστημα ενημερώνει τον ιδιοκτήτη για το αίτημα ενοικίασης, το οποίο περιλαμβάνει το προφιλ του ενοικιαστή και το εργαλείο του οποίο θέλει να νοικιάσει - μετάβαση στην περίπτωση χρήσης 9: Ειδοποιήσεις 
3.  Ο ιδιοκτήτης αποδέχεται την ενοικίαση
4.  Το σύστημα καταγράφει την αποδοχή του αιτήματος από τον ιδιοκτήτη
5.  Το σύστημα στέλνει ειδοποίηση αποδοχής αιτήματος ενοικίασης στον ενοικιαστή - Μετάβαση στην περίπτωση χρήσης 9: Ειδοποιήσεις 
6.  Ο ενοικιαστής επιλέγει ημερομηνίες ενοικίασης
7.  Το σύστημα δημιουργεί την εγγραφή του αιτήματος της κράτησης
8.  Το σύστημα στέλνει τις ειδοποιήσεις επιβεβαίωσης της κράτησης - Μετάβαση στην περίπτωση χρήσης 9: Ειδοποιήσεις 
9.  Ο ενοικιαστής πατάει το κουμπί ολοκλήρωσης της ενοικίασης
10. Το σύστημα υπολογίζει το συνολικό ποσό πληρωμής, προσθέτοντας τα μεταφορικά
11. Το σύστημα δημιουργεί την επιβεβαίωση διεύθυνσης παράδοσης
12. Το σύστημα τον μεταβιβάζει στο περιβαλλόν ολοκλήρωσης της πληρωμής 
13. Το σύστημα εμφανίζει την φόρμα εισαγωγής στοιχείων κάρτας
14. Ο ενοικιαστής εισάγει τα στοιχεία της κάρτας του
15. Το σύστημα επικυρώνει τα στοιχεία της κάρτας του
16. Το σύστημα δεσμεύει το ποσό στην κάρτα
17. Το σύστημα αποθηκεύει την επιβεβαίωση της συναλλαγής
18. Το σύστημα στέλνει την απόδειξη πληρωμής στον ενοικιαστή
19. Το σύστημα καταγράφει την ολοκλήρωση της πληρωμής 
20. Μετάβαση στην περίπτωση χρήσης 4: Παράδοση

Εναλλακτικές Ροές:
1. Ο ιδιοκτήτης δεν θέλει να νοικίασει το εργαλείο του στον ενοικιαστή (από βήμα 3 της βασικής)
3.1.1 Ο ιδιοκτήτης αρνείται να νοικιάσει το εργαλείο του στον συγκεκριμένο ενοικιαστή και επιλέγει Απόρριψη Ενοικίασης
3.1.2 Το σύστημα καταγράφει άρνηση κράτησης
3.1.2 Το σύστημα στέλνει ειδοποίηση άρνησης στον ενοικιαστή
3.1.3 Το σύστημα μεταφέρει τον ενοικιαστή στην αρχική σελίδα 

2. Ο ενοικιαστής αλλάζει γνώμη (από βήμα 6, 9 και 14 της βασικής)
3.2.1 Ο ενοικιαστής απορρίπτει την κράτηση 
3.2.2 Το σύστημα καταγράφει την ακύρωση κράτησης
3.2.3 Το σύστημα στέλνει ειδοποίηση ακύρωσης στον ενοικιαστή
3.2.4 Το σύστημα μεταφέρει τον ενοικιαστή στην αρχική σελίδα 

3. Αποτυχία πληρωμής (από βήμα 15 και 16 της βασικής)
3.3.1 Η τράπεζα δεν επικυρώνει το αίτημα πληρωμής 
3.3.2 Το σύστημα καταγράφει αποτυχημένη προσπάθεια πληρωμής
3.3.3 Το σύστημα στέλνει ειδοποίηση αποτυχίας πληρωμής στον ενοικιαστή - μετάβαση στην περίπτωση χρήσης 9: Ειδοποιήσεις 
3.3.4 Επιστροφή στο βήμα 9 της βασικής ροής

4. Επιλογή pick up (από βήμα 10 της βασικής)
3.4.1 Ο ενοικιαστής επικοινωνεί μέσω μηνυμάτων για σημείο συνάντηση pick up - μετάβαση στην περίπτωση χρήσης  8: Ανταλλαγή Μηνυμάτων 
3.4.2 Ο ενοικιαστής επιλέγει την επιλογή Παράδοση με προσωπική παραλαβή
3.4.3 Το σύστημα καταγράφει την επιλογή του ενοικιαστή για παράδοση με προσωπική παραλαβή
3.4.4 Επιστροφή στο βήμα 12 της βασικής ροής

