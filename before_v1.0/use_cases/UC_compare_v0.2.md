Περίπτωση Χρήσης 6: Σύγκριση Προϊόντων

Βήματα Happy Path:
1.  Ο ενοικιαστής επιλέγει την σύγκριση προϊόντων από την αρχική οθόνη
2.  Το σύστημα ανακτά από τη βάση δεδομένων τις αγγελίες που είναι στα αγαπημένα και τις βάζει σε μία λίστα
3.  Το σύστημα εμφανίζει στον ενοικιαστή την σελίδα σύγκρισης με τη δημιουργηθείσα λίστα
4.  Ο ενοικιαστής επιλέγει αγγελίες από τις αγαπημένες αγγελίες 
5.  Το σύστημα προσθέτει τις αγγελίες που επέλεξε ο ενοικιαστής στην λίστα των προς σύγκριση αγγελιών
6.  Ο ενοικιαστής επιλέγει να γίνει η σύγκριση των προϊόντων
7.  Το σύστημα ελέγχει ότι οι αγγελίες στην λίστα σύγκρισης ειναι τουλάχιστον δύο
8.  Το σύστημα αναζητά στη βάση δεδομένων τα τεχνικά στοιχεία των αγγελιών της λίστας σύγκρισης
9.  Το σύστημα χρησιμοποιεί AI assistance για να συγκρίνει τα εργαλεία με βάση τα τεχνικά στοιχεία τους
10. Το σύστημα, με τη βοήθεια του AI, διαμορφώνει την απάντηση και την εμφανίζει στον ενοικιαστή
11. Ο ενοικιαστής επιλέγει την αγγελία του εργαλείου που θέλει να νοικιάσει
12. Το σύστημα μεταβιβάζει τον ενοικιαστή στην πλήρη περιγραφή της αγγελίας

Εναλλακτικές Ροές:
1. Δεν έχει λίστα αγαπημένων (από βήμα 3 της βασικής)
6.1.1 Το σύστημα του εμφανίζει ως προτεινόμενες τις τελευταίες καταχωρήσεις που έχουν γίνει
6.1.2 Επιστροφή στο βήμα 3 της βασικής ροής 

2. Ελλειπείς εισαγωγές (από βήμα 7 της βασικής) 
6.2.1 Το σύστημα ενημερώνει τον ενοικιαστή ότι έχει εισάγει μία μόνο αγγελία προς σύγκριση και του ζητάει να εισάγει περισσότερες αγγελίες
6.2.2 Επιστροφή στο βήμα 2 της βασικής ροής

3. Διαγραφή Αγγελίας (από βήμα 6 και 11 της βασικής) 
6.3.1 Ο ενοικιαστής επιλέγει την διαγραφή μίας αγγελίας
6.3.2 Το σύστημα διαγράφει αυτή την αγγελία από τη λίστα των προς σύγκριση αγγελιών
6.3.1 Επιστροφή στο βήμα 6 της βασικής ρουτίνας

4. Προσθήκη Αγγελίας (από το βήμα 11 της βασικής)
6.4.1 Ο ενοικιαστής επιλέγει την προσθήκη μίας αγγελίας
6.4.2 Επιστροφή στο βήμα 2 της βασικής ρουτίνας
