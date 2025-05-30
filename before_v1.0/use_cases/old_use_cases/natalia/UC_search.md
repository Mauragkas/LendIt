Περίπτωση Χρήσης 2: Αναζήτηση Εργαλείου

Βήματα Happy Path:
1.  Ο ενοικιαστής πατάει το κουμπί "Αναζήτηση"
2.  Το σύστημα μεταβιβάζει τον ενοικιαστή στην σελίδα Αναζήτησης 
3.  Ο ενοικιαστής πληκτρολογεί το εργαλείο που θέλει να αναζητήσει
4.  Το σύστημα συλλέγει τα δεδομένα αναζήτησης του ενοικιαστή και αναζητά τις καταχωρήσεις που περιέχουν στην περιγραφή τους μία ή περισσότερες από τις λέξεις κλειδιά που έχει δώσει
5.  Το σύστημα κατατάσσει σε μία λίστα τις σχετικές καταχωρήσεις 
6.  Ο ενοικιαστής επιλέγει ανάμεσα σε έξι φίλτρα αναζήτησης: διαθεσιμότητα, εύρος τιμών, αύξουσα-φθίνουσα τιμή, κατηγορία εργαλείων (ηλεκτρικό, χειροκίνητο κτλ) , τοποθεσία (για εύρεση των πιο κοντινών διαθέσιμων εργαλείων) και κανένα
7.  Το σύστημα συλλέγει τα νέα δεδομένα από τα φίλτρα
8.  Το σύστημα εκτελεί μία εκ νέου αναζήτηση στην ήδη δημιουργηθείσα λίστα και την αναδιατάσσει με βάση τα φίλτρα που επέλεξε ο ενοικιαστής, εμφανίζοντας πρώτα τις επιλογές που ικανοποιούν τα περισσότερα φίλτρα
9.  Το σύστημα εμφανίζει τις πενήντα πιο σχετικές καταχωρήσεις 
10. Ο ενοικιαστής επιλέγει να δει την πλήρη περιγραφή ενός εργαλείου από τη λίστα
11. Το σύστημα μεταβιβάζει το χρήστη στην πλήρη περιγραφή του εργαλείου που διάλεξε 
12. Το συστημα συλλέγει τις λέξεις κλειδιά από την πλήρη περιγραφή του επιλεγμένου εργαλείου και επαναλαμβάνει το βήμα 5 
13. Το σύστημα εμφανίζει τη νέα λίστα που περιέχει σχετικά προϊόντα με αυτό που τώρα βλέπει ο ενοικιαστής κάτω από την πλήρη περιγραφή του
14. Ο ενοικιαστής επιλέγει εργαλείο προς ενοικίαση
15. Μεταβιβαζόμαστε στην περίπτωση χρήσης Πληρωμή

Εναλλακτικές Ροές:
1. "Λάθος επιλογή" (από βήμα 3, 6, 10 και 14 της βασικής)
2.1.1 Ο ενοικιαστής αποφασίζει ότι τελικά δεν θέλει να συνεχίσει την αναζήτηση και πατάει το κουμπί εξόδου  "Άκυρο"
2.1.2 To σύστημα μεταβιβάζει το χρήστη στην αρχική σελίδα 

2. "Λάθος είσοδος" (από το βήμα 5 της βασικής)
2.2.1 Το σύστημα δεν βρίσκει σχετικές καταχωρήσεις με την αναζήτηση του χρήστη 
2.2.2 Το σύστημα ενημερώνει το χρήστη για την αδυναμία εύρεσης αποτελεσμάτων 
2.2.3 Επιστροφή στο βήμα 2 της βασικής ροής

3. "Λάθος φίλτρα" (από το βήμα 8 της βασικής)
2.3.1 Το σύστημα αδυνατεί να κάνει αναδιάταξη των σχετικών εγγραφών, καθώς καμία δεν πληρή τα φίλτρα 
2.3.2 Το σύστημα ενημερώνει το χρήστη για την αδυναμία εύρεσης αποτελεσμάτων με βάση τα επιλεγμένα φίλτρα
2.3.3 Επιστροφή στο βήμα 2 της βασικής ροής

4. "Αλλαγή σελίδας" (από το βήμα 10 της βασικής)
2.4.1 Ο ενοικιαστής επιλέγει να δει κάτω από τις πρώτες πενήντα σχετικές αγγελίες και τις επόμενες πενήντα σχετικές καταχωρήσεις
2.4.2 Το σύστημα εμφανίζει πλέον τις πρώτες 100 σχετικές αγγελίες
2.4.3 Επιστροφή στο βήμα 10 της βασικής ροής

5. "Αλλαγή σελίδας" (από το βήμα 2 της εναλλακτικής ροής 4)
2.5.1 Το σύστημα ενημερώνει τον ενοικιαστή ότι δεν υπάρχουν άλλες 50 σχετικές αγγελίες
2.5.2 Το σύστημα εμφανίζει όλες τις σχετικές αγγελίες και στο κάτω μέρος εμφανίζεται "Δεν υπάρχουν άλλες διαθέσιμες σχετικές αγγελίες" 
2.4.3 Επιστροφή στο βήμα 10 της βασικής ροής

6. "Νέα επιλογή από τα προτεινόμενα" (από το βήμα 14 της βασικής)
2.5.1 Ο ενοικιαστής επιλέγει να δει την αναλυτική περιγραφή εργαλείου από την προτεινόμενη λίστα 
2.5.2 Επιστροφή στο βήμα 12 του happy path

7. "Καμία Αγορά" (από το βήμα 14 της βασικής)
2.7.1 Ο ενοικιαστής δεν κάνει αγορά και πατάει το κουμπί επιστροφής στην σελίδα αναζήτησης 
2.7.2 Το σύστημα μεταβιβάζει το χρήστη στην σελίδα αναζήτησης
2.7.3 Μετάβαση στο βήμα 9 του βασικής ροής

8. "Φίλτρο Τοποθεσίας" (από το βήμα 8 της βασικής)
2.8.1 Ο ενοικιαστής έχει επιλέξει το φίλτρο της τοποθεσίας και άρα μετάβαση στην περίπτωση χρήσης 5: Χάρτης
2.8.2 Επιστροφή στο βήμα 8 της βασικής ροής
