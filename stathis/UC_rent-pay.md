# Renting & Payment Processing System Events

## Renter Flow
### Happy Path
1. System displays search results based on renter's filters
2. System serves tool details page
3. System logs tool selection
4. System creates reservation request record
5. System sends confirmation notifications and enables communication channel
6. System processes payment transaction and logs result
7. System logs agreed meeting/delivery details
8. System logs tool pickup confirmation
9. System tracks rental period
10. System logs tool return confirmation
11. System records renter's return verification
12. System stores renter's rating of owner

### Alternative Path 1
4.1 System logs reservation denial
4.2 System sends denial notification to renter

### Alternative Path 2
5.1 System logs reservation cancellation
5.2 System sends cancellation notification to renter

### Alternative Path 3
6.1 System logs failed payment attempt
6.2 System sends payment failure notification
6.3 System redirects to payment retry page

### Alternative Path 4
7.1 System logs failed meeting/delivery agreement
7.2 System processes cancellation and sends notification

### Alternative Path 5
8.1 System logs condition dispute
8.2 System enables dispute communication channel
8.3 System processes return
8.4 System processes refund transaction
8.5 System stores rating

### Alternative Path 6
10.1 System logs tool non-return/damage
10.2 System processes damage charge
10.3 System sends charge notification

### Alternative Path 7
12.1 System logs missing rating

## Owner Flow
### Happy Path
1. System creates tool listing record
2. System notifies owner of reservation request
3. System logs reservation acceptance
4. System records meeting/delivery agreement
5. System processes payment to owner
6. System logs tool handoff
7. System logs tool return
8. System records owner's return verification
9. System stores owner's rating of renter

### Alternative Path 1
3.1 System logs reservation denial
3.2 System sends denial notification

### Alternative Path 2
5.1 System logs payment failure
5.2 System sends payment failure notification
5.3 System enables payment retry

### Alternative Path 3
7.1 System logs missing tool return
7.2 System enables dispute communication
7.3 System logs tool return
7.4 System stores rating

### Alternative Path 4
8.1 System logs condition dispute
8.2 System enables dispute communication
8.3 System processes compensation
8.4 System stores rating

### Alternative Path 5
9.1 System logs missing rating

## System Monitoring
- System tracks all transaction statuses
- System maintains audit logs of all events
- System monitors for suspicious activity
- System tracks dispute resolution metrics
- System generates analytics on rental patterns
