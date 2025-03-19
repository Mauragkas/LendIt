# Renting & Payment processing
## Renter
### Happy Path
1. Renter searches for a tool to rent w/ filters.
2. Renter inspects tool details.
3. Renter selects tool to rent.
4. Renter makes reservation request.
5. Renter receives confirmation and communication regarding the reservation.
6. Renter pays for reservation upfront w/ card.
7. Renter and Owner agree on a meeting place or delivery.
8. Renter receives tool.
9. Renter uses tool.
10. Renter returns tool (meeting place or delivery).
11. Renter confirms return.
12. Renter rates Owner.

### Alternative Path 1
4.1 Renter's reservation request is denied.
4.2 Renter receives notification of denial.

### Alternative Path 2
5.1 Renter's reservation request is cancelled.
5.2 Renter receives notification of cancellation.

### Alternative Path 3
6.1 Renter's payment is declined.
6.2 Renter receives notification of payment failure.
6.3 Renter prompted to retry payment from the reservation details page.

### Alternative Path 4
7.1 Renter and Owner cannot agree on a meeting place or delivery.
7.2 Renter receives notification of reservation cancellation.

### Alternative Path 5
8.1 Renter receives tool in poor condition.
8.2 Renter contacts Owner to resolve issue.
8.3 Renter returns tool.
8.4 Renter receives refund.
8.5 Renter rates Owner.

### Alternative Path 6
10.1 Renter does not return tool or breaks tool.
10.2 Renter is charged for tool.
10.3 Renter receives notification of charge.

### Alternative Path 7
12.1 Renter does not rate Owner.

## Owner
### Happy Path
1. Owner lists tool for rent.
2. Owner receives reservation request.
3. Owner accepts reservation request.
4. Owner and Renter agree on a meeting place or delivery.
5. Owner receives payment.
6. Owner meets Renter or delivery and hands off tool.
7. Owner receives tool back.
8. Owner confirms return.
9. Owner rates Renter.

### Alternative Path 1
3.1 Owner denies reservation request.
3.2 Renter receives notification of denial.

### Alternative Path 2
5.1 Owner does not receive payment.
5.2 Owner receives notification of payment failure.
5.3 Owner prompted to retry payment from the reservation details page.

### Alternative Path 3
7.1 Owner does not receive tool back.
7.2 Owner contacts Renter to resolve issue.
7.3 Owner receives tool back.
7.4 Owner rates Renter.

### Alternative Path 4
8.1 Owner receives tool back in poor condition.
8.2 Owner contacts Renter to resolve issue.
8.3 Owner receives refund.
8.4 Owner rates Renter.

### Alternative Path 5
9.1 Owner does not rate Renter.
