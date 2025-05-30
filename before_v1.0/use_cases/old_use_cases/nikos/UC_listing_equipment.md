Περίπτωση Χρήσης 1: Δημιουργία Αγγελίας του Εξοπλισμού

Βήματα Happy Path:
1.  Ο ιδιοκτήτης πατάει το κουμπί "Δημιουργία καταχώρησης"
2.  Το σύστημα μεταβιβάζει τον ιδιοκτήτη στη σελίδα δημιουργίας νέας καταχώρησης
3.  Ο ιδιοκτήτης συμπληρώνει τις πληροφορίες του εργαλείου, συγκεκριμένα: Όνομα εργαλείου, Περιγραφή, Κατηγορία εργαλείου (ηλεκτρικό, χειροκίνητο κλπ), Τοποθεσία, Διαθεσιμότητα, Τιμή ενοικίασης (ανά ημέρα, εβδομάδα, μήνα), καθώς και προσθήκη φωτογραφιών και περιγραφή της χρήσης του εργαλείου
4.  Το σύστημα αποθηκεύει προσωρινά τα δεδομένα και εμφανίζει προεπισκόπηση της καταχώρησης
5.  Ο ιδιοκτήτης πατάει "Δημοσίευση"
6.  Το σύστημα εμφανίζει ερώτηση για καταχώρηση έκπτωσης για μακροχρόνιες ενοικιάσεις
7.  Ο ιδιοκτήτης επιλέγει το ποσοστό της έκπτωσης που θα εφαρμόζεται στις μακροχρόνιες ενοικιάσεις
8.  Το σύστημα αποθηκεύει την έκπτωση
9.  Το σύστημα αποθηκεύει τη νέα καταχώρηση και την εμφανίζει στις διαθέσιμες αγγελίες
10. Το σύστημα μεταβιβάζει τον ιδιοκτήτη στην πλήρη περιγραφή της αγγελίας που μόλις καταχώρησε

Εναλλακτικές Ροές
1. Λάθος επιλογή (από τα βήματα 3, 5 και 7 της βασικής)
1.1.1 Ο ιδιοκτήτης αποφασίζει ότι δεν θέλει να δημιουργήσει νέα καταχώρηση και πατάει "Άκυρο"
1.1.2 Το σύστημα μεταβιβάζει τον ιδιοκτήτη στην αρχική σελίδα

2. Ελλιπή στοιχεία καταχώρησης (από το βήμα 3 της βασικής)
1.2.1 Ο ιδιοκτήτης αφήνει κενά υποχρεωτικά πεδία στη φόρμα καταχώρησης
1.2.2 Το σύστημα εμφανίζει μήνυμα λάθους και υποδεικνύει ποια πεδία πρέπει να συμπληρωθούν
1.2.3 Ο ιδιοκτήτης συμπληρώνει τα απαιτούμενα πεδία 
1.2.4 Μετάβαση στο βήμα 4 της βασικής ροής

3. Τιμή εκτός ορίων (από το βήμα 3 της βασικής)
1.3.1 Ο ιδιοκτήτης προσπαθεί να ορίσει τιμή κάτω από το ελάχιστο αποδεκτό ποσό (π.χ. 1€)
1.3.2 Το σύστημα εμφανίζει μήνυμα ότι η τιμή πρέπει να είναι εντός αποδεκτών ορίων
1.3.3 Ο ιδιοκτήτης διορθώνει την τιμή και επιστρέφει στο βήμα 4 της βασικής ροής

4. Καταχώρηση ήδη υπάρχει //Π.χ. Διπλοτυπες Καταχωρήσεις απο τον ίδιο ιδιοκτήτη (από το βήμα 3 της βασικής)
1.4.1 Ο ιδιοκτήτης προσπαθεί να προσθέσει εργαλείο που έχει ήδη ενεργή καταχώρηση
1.4.2 Το σύστημα εμφανίζει μήνυμα προειδοποίησης και δίνει την επιλογή επεξεργαστεί την υπάρχουσα καταχώρηση
1.4.3 Ο ιδιοκτήτης επιλέγει ότι θα προχωρήσει με την επεξεργασία της υπάρχουσας αγγελίας
1.4.4 Επιστρέφει στο βήμα 3 της βασικής ροής

5. Ο ιδιοκτήτης δεν θέλει να επεξεργαστεί την υπάρχουσα αίτηση (από το βήμα 3 της εναλλακτικής ροής 3)
1.5.1 Ο ιδιοκτήτης επιλέγει ότι δεν θα αλλάξει την υπάρχουσα αίτηση και πατάει Απόρριψη
1.5.2 Μετάβαση στο βήμα 2 της εναλλακτικής ροής 1

6. Διαγραφή καταχώρησης (από το βήμα 9 της βασικής)
1.6.1 Ο ιδιοκτήτης επιλέγει να διαγράψει την καταχώρηση
1.6.2 Το σύστημα εμφανίζει μήνυμα προειδοποίησης και ζητά επιβεβαίωση
1.6.3 Ο ιδιοκτήτης επιβεβαιώνει ότι θέλει να διαγράψει την αγγελία
1.6.4 Το σύστημα διαγράφει την καταχώρησή του
1.6.5 Μετάβαση στο βήμα 2 της εναλλακτικής ροής 1

7. Ο ιδιοκτήτης δεν διαγράφει την αγγελία (από το βήμα 3 της εναλλακτικής ροής 6)
1.7.1 Ο ιδιοκτήτης επιλέγει Άκυρο
1.7.2 Μετάβαση στο βήμα 2 της εναλλακτικής ροής 1

8. Τροποποίηση Αγγελίας (από το βήμα 5 της βασικής)
1.8.1 Ο ιδιοκτήτης επιλέγει την Επεξεργασία Αγγελίας
1.8.2 Ο ιδιοκτήτη εισάγει τις αλλαγές που επιθυμεί
1.8.3 Επιστροφή στο βήμα 4 της βασικής ροής

<!-- 7. Απόδοση της καταχώρησής
7.1) Ο ιδιοκτήτης πατάει πάνω στην αγγελία.
7.2) Το σύστημα εμφανίζει την απόδοση της καταχώρησής του (προβολές, αποθηκεύσεις, αιτήματα).
7.3) Ο ιδιοκτήτης πατάει το κουμπί εξόδου.
7.4) Το σύστημα μεταβιβάζει τον ιδιοκτήτη στην αρχική σελίδα. -->

<!-- 8. Διαγραφή ανενεργών αγγελιών
8.1) Το σύστημα εντοπίζει αγγελίες που δεν έχουν ανανεωθεί ή δεν χρησιμοποιούνται για ένα προκαθορισμένο χρονικό διάστημα.
8.2) Το σύστημα ενημερώνει τον ιδιοκτήτη ή τον διαχειριστή της αγγελίας με ειδοποίηση για την ανενεργή κατάσταση.
8.3) Ο ιδιοκτήτης/διαχειριστής επιλέγει να διαγράψει την ανενεργή αγγελία.
8.4) Το σύστημα ζητά επιβεβαίωση για τη διαγραφή.
8.5) Αν επιβεβαιωθεί, το σύστημα διαγράφει την αγγελία και καταγράφει το γεγονός. -->