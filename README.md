🔑 Preloaded Sample Accounts
Account   Number	    Name	  PIN	Balance
1001	  Amit Kumar	  1111	  ₹5000.00
1002	  Bhavana Singh	2222	  ₹12000.00
1003	  Charan Patel	3333	  ₹750.50

📘 User Manual (Step-by-Step)
1. Launching the ATM

You will see:

====== Welcome to Shastra ATM Simulator ======
1. Login
2. Exit

2. Logging In

Enter account number

Enter PIN

On 3 wrong attempts → login fails

3. After Login (Menu)
1. Check Balance
2. Deposit
3. Withdraw
4. Transfer
5. Mini Statement
6. Change PIN
7. Logout
8. Exit

4. Description of Each Option
✔ Check Balance

Shows your current account balance.

✔ Deposit

Prompts for amount → updates balance → logs transaction.

✔ Withdraw

Checks balance → dispenses cash → updates balance.

✔ Transfer

Send money to another account by entering their account number.

✔ Mini Statement

Shows last 10 transactions with timestamp.

✔ Change PIN

Verify old PIN → set new 4-digit PIN.

✔ Logout

Returns to main screen.

✔ Exit

Closes the program.

🧪 Testing Instructions
1. Successful Login

Input:

1001  
1111  


Expected: Welcome message.

2. Deposit Test

Deposit 2000
Expected: Balance increases by 2000.

3. Withdrawal Test

Withdraw 500
Expected: Deduction from balance if sufficient funds.

4. Transfer Test

Transfer 1000 to 1002
Expected:

Sender balance decreases

Receiver balance increases

5. Invalid Inputs

Try:

Letters instead of numbers

Wrong PIN

Negative amount
Expected: Validation errors.
