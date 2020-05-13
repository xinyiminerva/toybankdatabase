import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Advlist {
    Random rand = new Random();
    int debit_num = rand.nextInt(10000);
    int credit_num = rand.nextInt(200000);
    int checking_id = rand.nextInt(1000);
    int saving_id = rand.nextInt(1000);

    public static void main(String[] args) {
        Connection conn = null;
        Scanner scan = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            try {
                System.out.println(
                        "Welcome to Bank Management Product, In order to use this product you need to enter user name and password first.");
                System.out.println("Welcome User! Please enter your username.");
                String username = scan.nextLine();
                System.out.println("Thank you for entered your username , please enter password for your username.");
                String password = scan.nextLine();
                conn = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", username,
                        password);
                flag = false;
                System.out.println("You have successfully signed in to the database!!");
                welcomeScreen(scan, conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void welcomeScreen(Scanner scan, Connection conn) {
        System.out.println("Welcome to the main menu of Nickel Scavings and Loan.");
        System.out.println("Please choose your pysical location and choose from customer,banker,manager and vendor.");
        System.out.println("Please enter 1 if you are a customer and using ATM service.");
        System.out.println("Please enter 2 if you are bank teller (customer using bank teller service.");
        System.out.println("Please enter 3 if you are a bank manager.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 1) {
                    ATMInitial(scan, conn);
                    return;
                } else if (num == 2) {
                    BankTellerInitial(scan, conn);
                    return;
                } else if (num == 3) {
                    ManagerInitial(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void Unsecure_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Unsecure Loan management page.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 to acquire a new unsecure loan.");
        System.out.println("Enter 2 to check your unsecure loan information.");
        System.out.println("Enter 3 to pay for your unsecure loan.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Unsecure_new_unsecure(scan, conn);
                    return;
                } else if (num == 2) {
                    Unsecure_info_unsecure(scan, conn);
                    return;
                } else if (num == 3) {
                    Unsecure_pay_unsecure(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    // Let user take out a new unsecure loan, we assume that one customer could take
    // out
    // one loan.
    public static void Unsecure_new_unsecure(Scanner scan, Connection conn) {
        Random rand = new Random();
        int id = rand.nextInt(10000);
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double amount = 0.0;
        double monthly_payment = 0.0;

        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();

        System.out.println("Please enter the amount of loan you want to have");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        amount = scan.nextDouble();
        System.out.println("The amount you entered " + amount);
        try {

            PreparedStatement smt0;
            ResultSet result0;
            String query0 = "select firstname, lastname from Unsecure where firstname = " + "'" + firstname + "'"
                    + " and lastname = " + "'" + lastname + "'";
            smt0 = conn.prepareStatement(query0);
            result0 = smt0.executeQuery();
            if (result0.next()) {
                System.out.println("you already acqurie loan in the database, please go back.");
                Unsecure_service(scan, conn);
                return;
            } else {
                PreparedStatement smt1;
                ResultSet result1;
                PreparedStatement smt2;
                ResultSet result2;
                String query1 = "select cust_id from Customer where cust_firstname = " + "'" + firstname + "'"
                        + " and cust_lastname = " + "'" + lastname + "'";
                String query2 = "select Uinterest_rate, Umonthly_payment from Manager where manager_id = " + 1001;
                smt1 = conn.prepareStatement(query1);
                result1 = smt1.executeQuery();
                if (!result1.next()) {
                    System.out
                            .println("There is no information for you as a customer in the database, please go back.");
                    Unsecure_service(scan, conn);
                    return;
                } else {
                    smt2 = conn.prepareStatement(query2);
                    result2 = smt2.executeQuery();
                    result2.next();
                    interest_rate = result2.getDouble(1);
                    monthly_payment = result2.getDouble(2);
                    System.out.println(" interest_rate " + interest_rate);
                    System.out.println("  monthly_payment " + monthly_payment);
                    String sql_insert = "insert into Unsecure(id,firstname,lastname,interest_rate, amount, monthly_payment) values(?,?,?,?,?,?)";
                    PreparedStatement smt3 = conn.prepareStatement(sql_insert);
                    smt3.setInt(1, id);
                    smt3.setString(2, firstname);
                    smt3.setString(3, lastname);
                    smt3.setDouble(4, interest_rate);
                    smt3.setDouble(5, amount);
                    smt3.setDouble(6, monthly_payment);
                    smt3.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Unsecure_service(scan, conn);
    }

    public static void Unsecure_info_unsecure(Scanner scan, Connection conn) {
        int id = 0;
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double amount = 0.0;
        double monthly_payment = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String sql = "select id, firstname, lastname, interest_rate, amount, monthly_payment from Unsecure where firstname = "
                + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'";
        try {
            PreparedStatement smt = conn.prepareStatement(sql);
            ResultSet result = smt.executeQuery();
            if (!result.next()) {
                System.out.println("There is no information for you in the database");
                Unsecure_service(scan, conn);
                return;
            } else {
                id = result.getInt("id");
                firstname = result.getString("firstname");
                lastname = result.getString("lastname");
                interest_rate = result.getDouble("interest_rate");
                amount = result.getDouble("amount");
                monthly_payment = result.getDouble("monthly_payment");
                System.out.println("Your customer id is " + id);
                System.out.println("Your customer name is " + firstname + " " + lastname);
                System.out.println("Current interest rate " + interest_rate);
                System.out.println("unsercure loan amount " + amount);
                System.out.println("Current monthly payment " + monthly_payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Unsecure_service(scan, conn);
    }

    public static void Unsecure_pay_unsecure(Scanner scan, Connection conn) {
        String firstname = "";
        String lastname = "";
        int id = 0;
        double amount = 0.0;
        double payment = 0.0;
        double new_amount = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println("Please enter your payment in double");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        payment = scan.nextDouble();
        System.out.println("The payment you entered " + payment);
        String query1 = "select id, amount from Unsecure where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println(
                        "There is no information for your unsecure loan  in the database, please go back and create a loan.");
                Unsecure_service(scan, conn);
                return;
            } else {
                id = result1.getInt("id");
                amount = result1.getDouble("amount");
                new_amount = amount - payment;
                PreparedStatement sm = conn.prepareStatement("update Unsecure set amount = ? where id = " + id);
                sm.setDouble(1, new_amount);
                sm.executeUpdate();
            }
        } catch (Exception e) {
        }
        Unsecure_service(scan, conn);
    }

    public static void Checking_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Checking Account Management page.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 to open a new checking account");
        System.out.println("Enter 2 to check your checking account information.");
        System.out.println("Enter 3 to deposit.");
        System.out.println("Enter 4 to withdraw.");
        System.out.println("Enter 5 to create a debit card.");
        System.out.println("Enter 6 to replace a debit card.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Checking_open(scan, conn);
                    return;
                } else if (num == 2) {
                    Checking_info(scan, conn);
                    return;
                } else if (num == 3) {
                    Checking_deposit(scan, conn);
                    return;
                } else if (num == 4) {
                    Checking_withdraw(scan, conn);
                    return;
                } else if (num == 5) {
                    Checking_createDebit(scan, conn);
                    return;
                } else if (num == 6) {
                    Checking_replaceDebit(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void Checking_open(Scanner scan, Connection conn) {
        System.out.println(
                "Please note that if you already have a checking account, we will generate a new checking account id for you for replacement");
        System.out.println("start the process of creating a new checking account");
        Random rand = new Random();
        int checking_id = rand.nextInt(10000);
        int cust_id = 0;
        String firstname = "";
        String lastname = "";
        String address = "";
        double interest_rate = 0.0;
        double balance = 0.0;
        int debit_num = 0;
        try {
            System.out.println("Please enter your first name");
            firstname = scan.nextLine();
            System.out.println("Please enter your last name");
            lastname = scan.nextLine();
            String query1 = "select cust_id, address from Customer where cust_firstname = " + "'" + firstname + "'"
                    + " and cust_lastname = " + "'" + lastname + "'";
            PreparedStatement smt1;
            ResultSet result1;
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("You are not a valid customer");
                Checking_service(scan, conn);
                return;
            }
            cust_id = result1.getInt("cust_id");
            address = result1.getString("address");
            System.out.println("Your Customer id is " + cust_id);
            String query2 = "select Cinterest_rate from Manager where manager_id = " + 1001;
            PreparedStatement smt2;
            ResultSet result2;
            smt2 = conn.prepareStatement(query2);
            result2 = smt2.executeQuery();
            result2.next();
            interest_rate = result2.getDouble("Cinterest_rate");
            System.out.println("Current saving interest is " + interest_rate);
            PreparedStatement smt4;
            String sql_insert = "insert into Checking(checking_id, firstname,lastname,address, interest_rate, balance, debit_num) values(?,?,?,?,?,?,?)";
            smt4 = conn.prepareStatement(sql_insert);
            smt4.setInt(1, checking_id);
            smt4.setString(2, firstname);
            smt4.setString(3, lastname);
            smt4.setString(4, address);
            smt4.setDouble(5, interest_rate);
            smt4.setDouble(6, balance);
            smt4.setInt(7, debit_num);
            smt4.executeUpdate();
            String sql_update = "UPDATE Customer SET checking_id = " + checking_id + " where cust_id = " + cust_id;
            smt1 = conn.prepareStatement(sql_update);
            smt1.executeUpdate();
            System.out.println("Your checking id is " + checking_id);
        } catch (final Exception e) {
        }
        Checking_service(scan, conn);
    }

    public static void Checking_info(Scanner scan, Connection conn) {
        int debit_num = 0;
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double balance = 0.0;
        String address = "";
        int checking_id = 0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String sql = "select checking_id,firstname,lastname, address, interest_rate, balance, debit_num from Checking where firstname = "
                + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'";
        try {
            PreparedStatement smt = conn.prepareStatement(sql);
            ResultSet result = smt.executeQuery();
            if (!result.next()) {
                System.out.println(
                        "There is no information for your checking account in the database, please create a new checking account.");
                Checking_service(scan, conn);
                return;
            } else {
                checking_id = result.getInt("checking_id");
                firstname = result.getString("firstname");
                lastname = result.getString("lastname");
                address = result.getString("address");
                interest_rate = result.getDouble("interest_rate");
                balance = result.getDouble("balance");
                debit_num = result.getInt("debit_num");
                System.out.println("Your checking account id is " + checking_id);
                System.out.println("Your customer name is " + firstname + " " + lastname);
                System.out.println("Current checking account interest rate " + interest_rate);
                System.out.println(" current checking account balance " + balance);
                System.out.println("Current address for your checking account " + address);
                System.out.println(
                        "your current debit card number will show 0 if you don't have debit card " + debit_num);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Checking_service(scan, conn);
    }

    public static void Checking_deposit(Scanner scan, Connection conn) {
        int checking_id = 0;
        String firstname = "";
        String lastname = "";
        double balance = 0.0;
        double payment = 0.0;
        double new_balance = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println(
                "Please enter your deposit amount using double,if your number is incorrect please try util the system accept your input.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        payment = scan.nextDouble();
        System.out.println("The value you entered" + payment);
        String query1 = "select checking_id, balance from Checking where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no account information in the database.");
                Checking_service(scan, conn);
                return;
            } else {
                checking_id = result1.getInt("checking_id");
                balance = result1.getDouble("balance");
            }
            System.out.println("Your checking account id is " + checking_id);
            System.out.println("Your current balance is " + balance);
            new_balance = balance + payment;
            System.out.println("Your new balance is " + new_balance);
            PreparedStatement sm = conn
                    .prepareStatement("update Checking set balance = ? where checking_id = " + checking_id);
            sm.setDouble(1, new_balance);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        Checking_service(scan, conn);
    }

    public static void Checking_withdraw(Scanner scan, Connection conn) {
        int checking_id = 0;
        String firstname = "";
        String lastname = "";
        double balance = 0.0;
        double withdraw = 0.0;
        double new_balance = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println(
                "Please enter your withdraw amount using double,if your number is incorrect please try util the system accept your input.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        withdraw = scan.nextDouble();
        System.out.println("The value you entered " + withdraw);
        String query1 = "select checking_id, balance from Checking where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no account information in the database.");
                Checking_service(scan, conn);
                return;
            } else {
                checking_id = result1.getInt("checking_id");
                balance = result1.getDouble("balance");
                System.out.println("Your checking id is " + checking_id);
                System.out.println("Your balance is " + balance);

                if (balance - withdraw > 0) {
                    new_balance = balance - withdraw;
                } else {
                    System.out.println("You can't overdrawn.");
                    Checking_service(scan, conn);
                }
                System.out.println("Your new balance is " + new_balance);
                PreparedStatement sm = conn
                        .prepareStatement("update Checking set balance = ? where checking_id = " + checking_id);
                sm.setDouble(1, new_balance);
                sm.executeUpdate();
            }
        } catch (Exception e) {
        }
        Checking_service(scan, conn);
    }

    public static void Checking_createDebit(Scanner scan, Connection conn) {
        Random rand = new Random();
        int debit_num = rand.nextInt(1000000);
        String firstname = "";
        String lastname = "";
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String query = "select debit_num from Checking where firstname = " + "'" + firstname + "'" + " and lastname = "
                + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query);
            result1 = smt1.executeQuery();
            result1.next();
            if (result1.getInt("debit_num") != 0) {
                System.out.println("You already have debit card");
                Checking_service(scan, conn);
                return;
            } else {
                PreparedStatement sm = conn.prepareStatement("update Checking set debit_num = ? where firstname = "
                        + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'");
                sm.setInt(1, debit_num);
                sm.executeUpdate();
            }
        } catch (Exception e) {
        }
        System.out.println("your debit card number is " + debit_num);
        Checking_service(scan, conn);
    }

    public static void Checking_replaceDebit(Scanner scan, Connection conn) {
        Random rand = new Random();
        int debit_num = rand.nextInt(1000000);
        String firstname = "";
        String lastname = "";
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String query = "select debit_num from Checking where firstname = " + "'" + firstname + "'" + " and lastname = "
                + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query);
            result1 = smt1.executeQuery();
            result1.next();
            if (result1.getInt("debit_num") != 0) {
                System.out.println("You already have debit card!");
                System.out.println("Your previous debit card number is " + result1.getInt("debit_num"));
                System.out.println("Your new debit card number is " + debit_num);
                PreparedStatement sm = conn.prepareStatement("update Checking set debit_num = ? where firstname = "
                        + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'");
                sm.setInt(1, debit_num);
                sm.executeUpdate();
            } else {
                System.out.println("you didn't have debit card, go create one first");
                Checking_service(scan, conn);
            }
        } catch (Exception e) {
        }
        Checking_service(scan, conn);
    }

    public static void ManagerInitial(Scanner scan, Connection conn) {
        // note that the manager id is 1001
        System.out.println("Welcome to Manager Service Page");
        System.out.println("Please choose the type of service you want from below options");
        System.out.println("please enter 1 if you want to change checking account interest rate.");
        System.out.println("Please enter 2 if you want to change saving account interest rate.");
        System.out.println("Please enter 3 if you want to change saving account minimum balance.");
        System.out.println(
                "Please enter 4 if you want to change saving account penalty whenn running balance below minimum.");
        System.out.println("Please enter 5 if you want to change mortgage interest rate.");
        System.out.println("Please enter 6 if you want to change mortgage monthly payment.");
        System.out.println("Please enter 7 if you want to change unsecure loan interest rate.");
        System.out.println("Please enter 8 if you want to change unsecure loan monthly payment.");
        System.out.println("Please enter 9 if you want to chenge credit card limit.");
        System.out.println("Please enter 10 if you want to go back to main menu.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 1) {
                    CheckingAccountInterest(scan, conn);
                    return;
                } else if (num == 2) {
                    SavingAccountInterest(scan, conn);
                    return;
                } else if (num == 3) {
                    SavingAccountMin(scan, conn);
                    return;
                } else if (num == 4) {
                    SavingAccountPenalty(scan, conn);
                    return;
                } else if (num == 5) {
                    MortgageMonthlyInterest(scan, conn);
                    return;
                } else if (num == 6) {
                    MortgageMonthlyPayment(scan, conn);
                    return;
                } else if (num == 7) {
                    UnsecureInterest(scan, conn);
                    return;
                } else if (num == 8) {
                    UnsecureMonthlyPayment(scan, conn);
                    return;
                } else if (num == 9) {
                    CreditCardLimit(scan, conn);
                    return;
                } else if (num == 10) {
                    welcomeScreen(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void CreditCardLimit(Scanner scan, Connection conn) {
        double CreditCardLimit = 0.0;
        System.out.println("Please enter your new credit card limit.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        CreditCardLimit = scan.nextDouble();
        System.out.println("The value you entered " + CreditCardLimit);
        try {
            PreparedStatement sm = conn.prepareStatement("update Manager set credit_limit = " + CreditCardLimit);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void CheckingAccountInterest(Scanner scan, Connection conn) {
        double CheckingAccountInterest = 0.0;
        System.out.println("Please enter your new checking account interest.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        CheckingAccountInterest = scan.nextDouble();
        System.out.println("The value you entered " + CheckingAccountInterest);
        try {
            PreparedStatement sm = conn
                    .prepareStatement("update Manager set Cinterest_rate = " + CheckingAccountInterest);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void SavingAccountInterest(Scanner scan, Connection conn) {

        double SavingAccountInterest = 0.0;
        System.out.println("Please enter your new saving account interest.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        SavingAccountInterest = scan.nextDouble();
        System.out.println("The value you entered " + SavingAccountInterest);
        try {
            PreparedStatement sm = conn
                    .prepareStatement("update Manager set Sinterest_rate = " + SavingAccountInterest);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void SavingAccountMin(Scanner scan, Connection conn) {
        double SavingAccountMin = 0.0;
        System.out.println("Please enter your new saving account minimum.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        SavingAccountMin = scan.nextDouble();
        System.out.println("The value you entered " + SavingAccountMin);
        try {
            PreparedStatement sm = conn.prepareStatement("update Manager set Sminimum = " + SavingAccountMin);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void SavingAccountPenalty(Scanner scan, Connection conn) {
        double SavingAccountPenalty = 0.0;
        System.out.println("Please enter your new saving account penalty when the balance is below minimum.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        SavingAccountPenalty = scan.nextDouble();
        System.out.println("The value you entered " + SavingAccountPenalty);
        try {
            PreparedStatement sm = conn.prepareStatement("update Manager set Spenalty = " + SavingAccountPenalty);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void MortgageMonthlyInterest(Scanner scan, Connection conn) {
        double MortgageMonthlyInterest = 0.0;
        System.out.println("Please enter your new mortgage monthly interest rate.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        MortgageMonthlyInterest = scan.nextDouble();
        System.out.println("The value you entered " + MortgageMonthlyInterest);
        try {
            PreparedStatement sm = conn
                    .prepareStatement("update Manager set Minterest_rate = " + MortgageMonthlyInterest);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void MortgageMonthlyPayment(Scanner scan, Connection conn) {
        double MortgageMonthlyPayment = 0.0;
        System.out.println("Please enter your new mortgage monthly payment.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        MortgageMonthlyPayment = scan.nextDouble();
        System.out.println("The value you entered " + MortgageMonthlyPayment);
        try {
            PreparedStatement sm = conn
                    .prepareStatement("update Manager set Mmonthly_payment = " + MortgageMonthlyPayment);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void UnsecureInterest(Scanner scan, Connection conn) {
        double UnsecureInterest = 0.0;
        System.out.println("Please enter your new unsecure loan interest rate.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        UnsecureInterest = scan.nextDouble();
        System.out.println("The value you entered " + UnsecureInterest);
        try {
            PreparedStatement sm = conn.prepareStatement("update Manager set Uinterest_rate = " + UnsecureInterest);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void UnsecureMonthlyPayment(Scanner scan, Connection conn) {
        double UnsecureMonthlyPayment = 0.0;
        System.out.println("Please enter your new unsecure loan monthly payment.");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        UnsecureMonthlyPayment = scan.nextDouble();
        System.out.println("The value you entered " + UnsecureMonthlyPayment);
        try {
            PreparedStatement sm = conn
                    .prepareStatement("update Manager set Umonthly_payment = " + UnsecureMonthlyPayment);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        ManagerInitial(scan, conn);
    }

    public static void BankTellerInitial(Scanner scan, Connection conn) {
        System.out.println("Welcome to Customer Service Page");
        System.out.println("Please choose the type of service you want from below options");
        System.out.println(
                "please enter 1 if you want to manage customer personal information, including create new customer and check balance");
        System.out.println("Please enter 2 if you want to manage Mortgage information.");
        System.out.println("Please enter 3 if you want to manage Unsecure Loan information.");
        System.out.println("Please enter 4 if you want to manage Saving Account information");
        System.out.println("Please enter 5 if you want to manage Checking Account and debit card information");
        System.out.println("Please enter 6 if you want to manage fund transfer information");
        System.out.println("Please enter 7 if you want to manage Credit Card information");
        System.out.println("Please enter 8 if you want to purchace item from vendor");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 1) {
                    welcomeScreen(scan, conn);
                    return;
                } else if (num == 2) {
                    Mortgage_service(scan, conn);
                    return;
                } else if (num == 3) {
                    Unsecure_service(scan, conn);
                    return;
                } else if (num == 4) {
                    Saving_service(scan, conn);
                    return;
                } else if (num == 5) {
                    Checking_service(scan, conn);
                    return;
                } else if (num == 6) {
                    Transfer_service(scan, conn);
                    return;
                } else if (num == 7) {
                    Credit_service(scan, conn);
                    return;
                } else if (num == 8) {
                    Purchase_service(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void Saving_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Saving management page.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 to open a new saving account");
        System.out.println("Enter 2 to check your saving account information.");
        System.out.println("Enter 3 to deposit.");
        System.out.println("Enter 4 to withdraw.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Saving_open(scan, conn);
                    return;
                } else if (num == 2) {
                    Saving_info(scan, conn);
                    return;
                } else if (num == 3) {
                    Saving_deposit(scan, conn);
                    return;
                } else if (num == 4) {
                    Saving_withdraw(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void Saving_open(Scanner scan, Connection conn) {
        System.out.println(
                "Please note that if you already have a saving account, we will generate a new saving account id for you for replacement");
        System.out.println("start the process of creating a new saving account");
        Random rand = new Random();
        int saving_id = rand.nextInt(10000);
        int cust_id = 0;
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double balance = 0.0;
        double saving_minimum = 0.0;
        double penalty = 0.0;
        try {
            System.out.println("Please enter your first name");
            firstname = scan.nextLine();
            System.out.println("Please enter your last name");
            lastname = scan.nextLine();
            String query1 = "select cust_id from Customer where cust_firstname = " + "'" + firstname + "'"
                    + " and cust_lastname = " + "'" + lastname + "'";
            PreparedStatement smt1;
            ResultSet result1;
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("You are not a valid customer");
                Saving_service(scan, conn);
                return;
            }
            cust_id = result1.getInt("cust_id");
            System.out.println("Your Customer id is " + cust_id);
            String query2 = "select Sinterest_rate, Sminimum, Spenalty from Manager where manager_id = " + 1001;
            PreparedStatement smt2;
            ResultSet result2;
            smt2 = conn.prepareStatement(query2);
            result2 = smt2.executeQuery();
            result2.next();
            interest_rate = result2.getDouble("Sinterest_rate");
            saving_minimum = result2.getDouble("Sminimum");
            penalty = result2.getDouble("Spenalty");
            System.out.println("Current  saving interest is " + interest_rate);
            System.out.println("Current saving account miniminum without penalty " + saving_minimum);
            System.out.println("Current saving account penalty  is " + penalty);
            PreparedStatement smt4;
            String sql_insert = "insert into Saving(saving_id, firstname,lastname,interest_rate, balance, saving_minimum,penalty) values(?,?,?,?,?,?,?)";
            smt4 = conn.prepareStatement(sql_insert);
            smt4.setInt(1, saving_id);
            smt4.setString(2, firstname);
            smt4.setString(3, lastname);
            smt4.setDouble(4, interest_rate);
            smt4.setDouble(5, balance);
            smt4.setDouble(6, saving_minimum);
            smt4.setDouble(7, penalty);
            smt4.executeUpdate();
            String sql_update = "UPDATE Customer SET saving_id = " + saving_id + " where cust_id = " + cust_id;
            smt1 = conn.prepareStatement(sql_update);
            smt1.executeUpdate();
            System.out.println("Your saving id is " + saving_id);
        } catch (final Exception e) {
        }
        Saving_service(scan, conn);
    }

    public static void Saving_info(Scanner scan, Connection conn) {
        try {
            String firstname = "";
            String lastname = "";
            double interest_rate = 0.0;
            double balance = 0.0;
            double saving_minimum = 0.0;
            double penalty = 0.0;
            int saving_id;
            System.out.println("Please enter your first name");
            firstname = scan.nextLine();
            System.out.println("Please enter your last name");
            lastname = scan.nextLine();
            String sql = "select saving_id,firstname,lastname, interest_rate, balance, saving_minimum, penalty from Saving where firstname = "
                    + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'";
            PreparedStatement smt = conn.prepareStatement(sql);
            ResultSet result = smt.executeQuery();
            if (!result.next()) {
                System.out.println(
                        "There is no information for your saving account in the database, please create a new saving account.");
                Saving_service(scan, conn);
            } else {
                saving_id = result.getInt("saving_id");
                firstname = result.getString("firstname");
                lastname = result.getString("lastname");
                interest_rate = result.getDouble("interest_rate");
                balance = result.getDouble("balance");
                saving_minimum = result.getDouble("saving_minimum");
                penalty = result.getDouble("penalty");
                System.out.println("Your saving account id is " + saving_id);
                System.out.println("Your customer name is " + firstname + " " + lastname);
                System.out.println("Current interest rate " + interest_rate);
                System.out.println(" current saving account balance " + balance);
                System.out.println("Current minimum balance " + saving_minimum);
                System.out.println("If you go below minimum balance, you will have a penalty of  " + penalty);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Saving_service(scan, conn);
    }

    public static void Saving_deposit(Scanner scan, Connection conn) {
        int saving_id = 0;
        String firstname = "";
        String lastname = "";
        double balance = 0.0;
        double payment = 0.0;
        double new_balance = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println("Please enter payment (a double)");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        payment = scan.nextDouble();
        System.out.println("The value you entered " + payment);

        String query1 = "select saving_id, balance from Saving where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println(
                        "There is no saving account information in the database you will be direacted to saving management page.");
                Saving_service(scan, conn);
            } else {
                saving_id = result1.getInt("saving_id");
                balance = result1.getDouble("balance");
                System.out.println("Your saving id is  " + saving_id);
                System.out.println("Your current balance  is  " + balance);
                new_balance = balance + payment;
                System.out.println("Your new balance  is  " + new_balance);
                PreparedStatement sm = conn
                        .prepareStatement("update Saving set balance = ? where saving_id = " + saving_id);
                sm.setDouble(1, new_balance);
                sm.executeUpdate();
            }
        } catch (Exception e) {
        }
        Saving_service(scan, conn);
    }

    public static void Saving_withdraw(Scanner scan, Connection conn) {
        int saving_id = 0;
        String firstname = "";
        String lastname = "";
        double balance = 0.0;
        double minimum = 0.0;
        double penalty = 0.0;
        double withdraw = 0.0;
        double new_balance = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println("Please enter withdraw amount (a double)");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        withdraw = scan.nextDouble();
        System.out.println("The value you entered " + withdraw);
        String query1 = "select saving_id, balance, saving_minimum, penalty from Saving where firstname = " + "'"
                + firstname + "'" + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no account information in the database.");
                Saving_service(scan, conn);
                return;
            } else {
                saving_id = result1.getInt("saving_id");
                balance = result1.getDouble("balance");
                minimum = result1.getDouble("saving_minimum");
                penalty = result1.getDouble("penalty");
                System.out.println("Your saving id is " + saving_id);
                System.out.println("Your balance is " + balance);
                System.out.println("Your saving minimum without penalty is " + minimum);
                System.out.println("Your penalty is " + penalty);

                if (balance - withdraw > minimum) {
                    new_balance = balance - withdraw;
                } else {
                    new_balance = balance - withdraw - penalty;
                    System.out.println("There will be a penalty for this transcation.");
                }
                System.out.println("Your new balance  is " + new_balance);
                PreparedStatement sm = conn
                        .prepareStatement("update Saving set balance = ? where saving_id = " + saving_id);
                sm.setDouble(1, new_balance);
                sm.executeUpdate();
            }
        } catch (Exception e) {
        }
        Saving_service(scan, conn);
    }

    public static void ATMInitial(Scanner scan, Connection conn) {
        System.out.println("Welcome to ATM service!");
        System.out.println("Please note that you could only withdraw using debit card in this interface");
        String firstname = "";
        String lastname = "";
        double balance = 0.0;
        double withdraw = 0.0;
        int debit_num = 1;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println("Please enter the amout you want to withdraw (using double");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        withdraw = scan.nextDouble();
        System.out.println("The withdraw amount you entered " + withdraw);
        try {
            String sql1;
            ResultSet result1;
            sql1 = "select debit_num, balance from Checking where firstname = " + "'" + firstname + "'"
                    + " and lastname = " + "'" + lastname + "'";
            PreparedStatement smt1 = conn.prepareStatement(sql1);
            result1 = smt1.executeQuery(sql1);
            if (!result1.next()) {
                System.out.println("You didn't have account");
                welcomeScreen(scan, conn);
            }
            debit_num = result1.getInt("debit_num");
            balance = result1.getDouble("balance");
            if (debit_num == 0) {
                System.out.println("You didn't have a debit card");
                welcomeScreen(scan, conn);
                return;
            } else {
                System.out.println(" Your current Debit Card Balance is " + result1.getDouble("balance"));
            }
            if (balance - withdraw < 0) {
                System.out.println("You can't overdrawn");
                welcomeScreen(scan, conn);
                return;
            } else {
                balance = balance - withdraw;
                PreparedStatement sm = conn.prepareStatement("update Checking set balance = " + balance
                        + " where firstname = " + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'");
                sm.executeUpdate();
            }
            System.out.println(" Your new Debit Card Balance is " + balance);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        welcomeScreen(scan, conn);
    }

    public static void Transfer_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Transfer Payment  management page.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 to transfer from saving to checking account.");
        System.out.println("Enter 2 to transfer from checking to saving account.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Transfer_transfer1(scan, conn);
                    return;
                } else if (num == 2) {
                    Transfer_transfer2(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void Transfer_transfer1(Scanner scan, Connection conn) {
        System.out.println("Please enter your first name");
        String firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        String lastname = scan.nextLine();
        System.out.println("Please enter tranfer ammount (in double)");
        double value1 = 0.0;
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        value1 = scan.nextDouble();
        System.out.println("The value you entered " + value1);
        Transfer_Saving_withdraw(scan, conn, value1, firstname, lastname);
    }

    public static void Transfer_Saving_withdraw(Scanner scan, Connection conn, double withdraw, String firstname,
            String lastname) {
        int saving_id = 0;
        double balance = 0.0;
        double minimum = 0.0;
        double penalty = 0.0;
        double new_balance = 0.0;
        String query1 = "select saving_id, balance, saving_minimum, penalty from Saving where firstname = " + "'"
                + firstname + "'" + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no saving account information in the database.");
                Transfer_service(scan, conn);
            } else {
                saving_id = result1.getInt("saving_id");
                balance = result1.getDouble("balance");
                minimum = result1.getDouble("saving_minimum");
                penalty = result1.getDouble("penalty");
                System.out.println("Your saving id is " + saving_id);
                System.out.println("Your balance is " + balance);
                System.out.println("Your saving minimum without penalty is " + minimum);
                System.out.println("Your penalty is " + penalty);

                if (balance - withdraw > minimum) {
                    new_balance = balance - withdraw;
                } else {
                    new_balance = balance - withdraw - penalty;
                }
                System.out.println("Your new balance is " + new_balance);
                PreparedStatement sm = conn
                        .prepareStatement("update Saving set balance = ? where saving_id = " + saving_id);
                sm.setDouble(1, new_balance);
                sm.executeUpdate();
                Transfer_Checking_deposit(scan, conn, withdraw, firstname, lastname);
            }
        } catch (Exception e) {
        }
    }

    public static void Transfer_Checking_deposit(Scanner scan, Connection conn, double payment, String firstname,
            String lastname) {
        int checking_id = 0;
        double balance = 0.0;
        double new_balance = 0.0;
        String query1 = "select checking_id, balance from Checking where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no account information in the database.");
                Transfer_service(scan, conn);
            } else {
                checking_id = result1.getInt("checking_id");
                balance = result1.getDouble("balance");
            }
            System.out.println("Your checking account id is " + checking_id);
            System.out.println("Your current balance is " + balance);
            new_balance = balance + payment;
            System.out.println("Your new balance is " + new_balance);
            PreparedStatement sm = conn
                    .prepareStatement("update Checking set balance = ? where checking_id = " + checking_id);
            sm.setDouble(1, new_balance);
            sm.executeUpdate();
        } catch (Exception e) {
        }
        Transfer_service(scan, conn);
    }

    public static void Transfer_transfer2(Scanner scan, Connection conn) {
        System.out.println("Please enter your first name");
        String firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        String lastname = scan.nextLine();
        System.out.println("Please enter tranfer ammount (in double)");
        double value1 = 0.0;
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        value1 = scan.nextDouble();
        System.out.println("The value you entered " + value1);
        Transfer_Checking_withdraw(scan, conn, value1, firstname, lastname);
    }

    public static void Transfer_Checking_withdraw(Scanner scan, Connection conn, double withdraw, String firstname,
            String lastname) {
        int checking_id = 0;
        double balance = 0.0;
        double new_balance = 0.0;
        String query1 = "select checking_id, balance from Checking where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no checking account information in the database.");
                Transfer_service(scan, conn);
                return;
            } else {
                checking_id = result1.getInt("checking_id");
                balance = result1.getDouble("balance");
                System.out.println("Your checking id is " + checking_id);
                System.out.println("Your balance is " + balance);
                new_balance = balance - withdraw;
            }
            System.out.println("Your new checking balance is " + new_balance);
            PreparedStatement sm = conn
                    .prepareStatement("update Checking set balance = ? where checking_id = " + checking_id);
            sm.setDouble(1, new_balance);
            sm.executeUpdate();
            Transfer_Saving_deposit(scan, conn, withdraw, firstname, lastname);
        } catch (Exception e) {
        }
    }

    public static void Transfer_Saving_deposit(Scanner scan, Connection conn, double payment, String firstname,
            String lastname) {
        int saving_id = 0;
        double balance = 0.0;
        double new_balance = 0.0;
        String query1 = "select saving_id, balance from Saving where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no account information in the database.");
                Transfer_service(scan, conn);
                return;
            } else {
                saving_id = result1.getInt("saving_id");
                balance = result1.getDouble("balance");
                System.out.println("Your saving id is " + saving_id);
                System.out.println("Your current balance is " + balance);
                new_balance = balance + payment;
                System.out.println("Your new balance is " + new_balance);
                PreparedStatement sm = conn
                        .prepareStatement("update Saving set balance = ? where saving_id = " + saving_id);
                sm.setDouble(1, new_balance);
                sm.executeUpdate();
            }
        } catch (Exception e) {
        }
        Transfer_service(scan, conn);
    }

    public static void Purchase_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Purchase Interface.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 if you want to buy item using credit card.");
        System.out.println("Enter 2 if you want to buy item using debit card.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Purchase_ItemList(scan, conn);
                    Purchase_creditbuy(scan, conn);
                    return;
                } else if (num == 2) {
                    Purchase_ItemList(scan, conn);
                    Purchase_debitbuy(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void Purchase_ItemList(Scanner scan, Connection conn) {

        try {
            System.out.println("The list of item id and price will be listed below.");
            ResultSet result;
            String sql;
            sql = "select id, item_price from Vendor";
            PreparedStatement smt = conn.prepareStatement(sql);
            result = smt.executeQuery(sql);
            while (result.next()) {
                System.out.println("ID is " + result.getInt("id"));
                System.out.println("Price for item " + result.getInt("id") + "is " + result.getDouble("item_price"));
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void Purchase_creditbuy(Scanner scan, Connection conn) {
        int id = 0;
        double price = 0.0;
        String firstname = "";
        String lastname = "";
        double credit_limit = 0.0;
        double balance = 0.0;
        int credit_id = 0;
        System.out.println("Please enter your first name :");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name :");
        lastname = scan.nextLine();
        System.out.println("Please enter the id of the item you want to purchase: ");
        while (!scan.hasNextInt()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        id = scan.nextInt();
        System.out.println("The id you entered " + id);
        try {
            ResultSet result;
            String sql;
            sql = "select item_price from Vendor where vendor_id = " + id;
            PreparedStatement smt = conn.prepareStatement(sql);
            result = smt.executeQuery();
            if (result.next()) {
                price = result.getDouble("item_price");
                System.out.println("you choose an item has price of " + price);
            } else {
                System.out.println("You chosen invalid item");
                Purchase_service(scan, conn);
            }

            String sql1;
            ResultSet result1;
            sql1 = "select credit_id, balance, credit_limit from Credit where firstname = " + "'" + firstname + "'"
                    + " and lastname = " + "'" + lastname + "'";
            PreparedStatement smt1 = conn.prepareStatement(sql);
            result1 = smt1.executeQuery(sql1);
            if (!result1.next()) {
                System.out.println("you didn't have credit card");
                Purchase_service(scan, conn);
            } else {
                credit_id = result1.getInt("credit_id");
                balance = result1.getDouble("balance");
                credit_limit = result1.getDouble("credit_limit");
                System.out.println(" Your current Credit Card ID is " + credit_id);
                System.out.println(" Your current Credit Card Balance is " + balance);
                System.out.println("Credit Card limit is  " + credit_limit);
                if (balance - price + credit_limit < 0) {
                    System.out.println("Exceed your credit card limit");
                    Purchase_service(scan, conn);
                } else {
                    balance = balance - price;
                    PreparedStatement sm = conn
                            .prepareStatement("update Credit set balance = " + balance + " where firstname = " + "'"
                                    + firstname + "'" + " and lastname = " + "'" + lastname + "'");
                    sm.executeUpdate();
                }
                System.out.println(" Your new Credit Card Balance is " + balance);
                Purchase_service(scan, conn);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Purchase_debitbuy(Scanner scan, Connection conn) {
        int id = 0;
        double price = 0.0;
        String firstname = "";
        String lastname = "";
        double balance = 0.0;
        int debit_num = 0;
        System.out.println("Please enter your first name :");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name :");
        lastname = scan.nextLine();
        System.out.println("Please enter the id of the item you want to purchase: ");
        while (!scan.hasNextInt()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        id = scan.nextInt();
        System.out.println("The id you entered " + id);
        try {
            ResultSet result;
            String sql;
            sql = "select item_price from Vendor where vendor_id = " + id;
            PreparedStatement smt = conn.prepareStatement(sql);
            result = smt.executeQuery();
            if (result.next()) {
                price = result.getDouble("item_price");
                System.out.println("you choose an item has price of " + price);
            } else {
                System.out.println("You chosen invalid item");
                Purchase_service(scan, conn);
            }
            String sql1;
            ResultSet result1;
            sql1 = "select debit_num, balance from Checking where firstname = " + "'" + firstname + "'"
                    + " and lastname = " + "'" + lastname + "'";
            PreparedStatement smt1 = conn.prepareStatement(sql1);
            result1 = smt1.executeQuery(sql1);
            if (!result1.next()) {
                System.out.println("You didn't create debit card;");
                Purchase_service(scan, conn);
            } else {
                debit_num = result1.getInt("debit_num");
                balance = result1.getDouble("balance");
                System.out.println(" Your current Debit Card Balance is " + balance);
                System.out.println(" Your current Debit Card Number is " + debit_num);
                if (balance - price < 0) {
                    System.out.println("You can't overdrawn");
                    Purchase_service(scan, conn);
                } else {
                    balance = balance - price;
                    PreparedStatement sm = conn
                            .prepareStatement("update Checking set balance = " + balance + " where firstname = " + "'"
                                    + firstname + "'" + " and lastname = " + "'" + lastname + "'");
                    sm.executeUpdate();
                }
                System.out.println(" Your new Debit Card Balance is " + balance);
                Purchase_service(scan, conn);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Mortgage_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Mortgage management page.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 to acquire a new mortgage.");
        System.out.println("Enter 2 to check your mortgage information.");
        System.out.println("Enter 3 to pay for your mortgage.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Mortgage_new_mortgage(scan, conn);
                    return;
                } else if (num == 2) {
                    Mortgage_info_mortgage(scan, conn);
                    return;
                } else if (num == 3) {
                    Mortgage_pay_mortgage(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    // Let user take out a new unsecure loan, we assume that one customer could take
    // out
    // one loan.
    public static void Mortgage_new_mortgage(Scanner scan, Connection conn) {
        int cust_id = 0;
        String firstname = "";
        String lastname = "";
        String address = "";
        double interest_rate = 0.0;
        double amount = 0.0;
        double monthly_payment = 0.0;

        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();

        System.out.println("Please enter the amount of money");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        amount = scan.nextDouble();
        System.out.println("The amount you entered " + amount);
        // need to find cust_id and address from customer, find monthly_payment ,
        // interest_rate from manager
        try {
            PreparedStatement smt1;
            ResultSet result1;
            PreparedStatement smt2;
            ResultSet result2;
            String query1 = "select cust_id, address from Customer where cust_firstname = " + "'" + firstname + "'"
                    + " and cust_lastname = " + "'" + lastname + "'";
            String query2 = "select Minterest_rate, Mmonthly_payment from Manager where manager_id = " + 1001;
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println("There is no information for you as a customer in the database, please go back.");
                Mortgage_service(scan, conn);
            } else {
                cust_id = result1.getInt("cust_id");
                address = result1.getString("address");
                System.out.println("your cust_id " + cust_id);
                System.out.println("your address " + address);
                smt2 = conn.prepareStatement(query2);
                result2 = smt2.executeQuery();
                result2.next();
                interest_rate = result2.getDouble(1);
                monthly_payment = result2.getDouble(2);
                String sql_insert = "insert into Mortgage(id,firstname,lastname,interest_rate, amount, monthly_payment,address) values(?,?,?,?,?,?,?)";
                PreparedStatement smt3 = conn.prepareStatement(sql_insert);
                smt3.setInt(1, cust_id);
                smt3.setString(2, firstname);
                smt3.setString(3, lastname);
                smt3.setDouble(4, interest_rate);
                smt3.setDouble(5, amount);
                smt3.setDouble(6, monthly_payment);
                smt3.setString(7, address);
                smt3.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Mortgage_service(scan, conn);
    }

    public static void Mortgage_info_mortgage(Scanner scan, Connection conn) {
        int id = 0;
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double amount = 0.0;
        double monthly_payment = 0.0;
        String address = "";
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String sql = "select id, firstname, lastname, interest_rate, amount, monthly_payment,address from Mortgage where firstname = "
                + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'";
        try {
            PreparedStatement smt = conn.prepareStatement(sql);
            ResultSet result = smt.executeQuery();
            if (!result.next()) {
                System.out.println("There is no  Mortgage information for you in the database");
                Mortgage_service(scan, conn);
            } else {
                id = result.getInt("id");
                firstname = result.getString("firstname");
                lastname = result.getString("lastname");
                interest_rate = result.getDouble("interest_rate");
                amount = result.getDouble("amount");
                monthly_payment = result.getDouble("monthly_payment");
                address = result.getString("address");
                System.out.println("Your customer id is " + id);
                System.out.println("Your customer name is " + firstname + " " + lastname);
                System.out.println("Current interest rate " + interest_rate);
                System.out.println("unsercure loan amount " + amount);
                System.out.println("Current monthly payment " + monthly_payment);
                System.out.println("Current Mortgage address " + address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Mortgage_service(scan, conn);
    }

    public static void Mortgage_pay_mortgage(Scanner scan, Connection conn) {
        String firstname = "";
        String lastname = "";
        int id = 0;
        double amount = 0.0;
        double payment = 0.0;
        double new_amount = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        System.out.println("Please enter your payment in double");
        while (!scan.hasNextDouble()) {
            scan.nextLine();
            System.out.println("Please enter again");
        }
        payment = scan.nextDouble();
        System.out.println("The payment you entered " + payment);
        String query1 = "select id, amount from Mortgage where firstname = " + "'" + firstname + "'"
                + " and lastname = " + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            if (!result1.next()) {
                System.out.println(
                        "There is no information for your Mortgage  in the database, please go back and create a Mortgage.");
                BankTellerInitial(scan, conn);
                return;
            } else {
                id = result1.getInt("id");
                amount = result1.getDouble("amount");
                new_amount = amount - payment;
                PreparedStatement sm = conn.prepareStatement("update Mortgage  set amount = ? where id = " + id);
                sm.setDouble(1, new_amount);
                sm.executeUpdate();
                System.out.println("your current mortgage amount has been update to" + new_amount);
            }
        } catch (Exception e) {
        }
        Mortgage_service(scan, conn);
    }

    public static void Credit_service(Scanner scan, Connection conn) {
        System.out.println("Welcome to Credit Card management page.");
        System.out.println("Please make sure you have registered as a customer.");
        System.out.println("Enter 0 if you haven't registerd as a customer,it will take you to the welcome menu.");
        System.out.println("Enter 1 to have a new credit card.");
        System.out.println("Enter 2 to change your credit card number.(in case you lost your credit card).");
        System.out.println("Enter 3 to pay for your credit card.");
        System.out.println("Enter 4 to check your credit card information.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 0) {
                    BankTellerInitial(scan, conn);
                    return;
                }
                if (num == 1) {
                    Credit_new_credit(scan, conn);
                    return;
                } else if (num == 2) {
                    Credit_replace_credit(scan, conn);
                    return;
                } else if (num == 3) {
                    Credit_pay(scan, conn);
                    return;
                } else if (num == 4) {
                    Credit_info(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }
    // remember to implement date latter

    public static void service(Scanner scan, Connection conn) {
        System.out.println("currently not offer");
        System.out.println("Welcome to Customer management page.");
        System.out.println("Enter 1 to create a new customer.");
        System.out.println("Enter 2 to change customer information.");
        System.out.println("Enter 3 to delete a customer.");
        System.out.println("Enter 4 to check your customer information.");
        int num;
        while (true) {
            try {
                num = scan.nextInt();
                scan.nextLine();
                if (num == 1) {
                    BankTellerInitial(scan, conn);
                    create_customer(scan, conn);
                    return;
                }
                if (num == 2) {
                    BankTellerInitial(scan, conn);
                    update_information(scan, conn);
                    return;
                } else if (num == 3) {
                    BankTellerInitial(scan, conn);
                    delete_customer(scan, conn);
                    return;
                } else if (num == 4) {
                    BankTellerInitial(scan, conn);
                    check_info(scan, conn);
                    return;
                }
            } catch (Exception e) {
                System.out.println("The number you entered is not valid please try again.");
            }
        }
    }

    public static void create_customer(Scanner scan, Connection conn) {
    }

    public static void update_information(Scanner scan, Connection conn) {
    }

    public static void delete_customer(Scanner scan, Connection conn) {
    }

    public static void check_info(Scanner scan, Connection conn) {
    }

    public static void Credit_new_credit(Scanner scan, Connection conn) {
        Random rand = new Random();
        int credit_num = rand.nextInt(200000);
        int credit_id = rand.nextInt(10000);
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double balance = 0.0;
        double credit_limit = 0.0;
        int cust_id = 0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        try {
            String query0 = "select firstname, lastname from Credit where firstname = " + "'" + firstname + "'"
                    + " and lastname = " + "'" + lastname + "'";
            String query1 = "select cust_id from Customer where cust_firstname = " + "'" + firstname + "'"
                    + " and cust_lastname = " + "'" + lastname + "'";
            String query2 = "select credit_interest_rate, credit_limit from Manager where manager_id = " + 1001;
            PreparedStatement smt0, smt1, smt2, smt3;
            ResultSet result0, result1, result2;
            smt0 = conn.prepareStatement(query0);
            result0 = smt0.executeQuery();
            if (result0.next()) {
                System.out.println("You already have credit card");
                Credit_service(scan, conn);
                return;
            }
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            // need to test if later
            if (result1.next()) {
                cust_id = result1.getInt("cust_id");
                System.out.println("Your Customer id is " + cust_id);
            } else {
                System.out.println("You are not a valid customer");
                Credit_service(scan, conn);
                return;
            }
            smt2 = conn.prepareStatement(query2);
            result2 = smt2.executeQuery();
            result2.next();
            interest_rate = result2.getDouble("credit_interest_rate");
            credit_limit = result2.getDouble("credit_limit");
            System.out.println("Current credit card  interest rate is " + interest_rate);
            System.out.println("Current credit card limit is " + credit_limit);
            String sql_insert = "insert into Credit(credit_id, firstname, lastname, interest_rate, balance, credit_limit, credit_num) values(?,?,?,?,?,?,?)";
            smt3 = conn.prepareStatement(sql_insert);
            smt3.setInt(1, credit_id);
            smt3.setString(2, firstname);
            smt3.setString(3, lastname);
            smt3.setDouble(4,interest_rate);
            smt3.setDouble(5, balance);
            smt3.setDouble(6, credit_limit);
            smt3.setInt(7, credit_num);
            smt3.executeUpdate();
            System.out.println("credit card created !");
        } catch (final Exception e) {
        }
        Credit_service(scan, conn);
    }
    public static void Credit_replace_credit(Scanner scan, Connection conn) {
        Random rand = new Random();
        int credit_num = rand.nextInt(200000);
        System.out.println("You could enter your information to create a new credit card");
        String firstname = "";
        String lastname = "";
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String query = "select credit_num from Credit where firstname = " + "'" + firstname + "'" + " and lastname = "
                + "'" + lastname + "'";
        PreparedStatement smt1;
        ResultSet result1;
        try {
            smt1 = conn.prepareStatement(query);
            result1 = smt1.executeQuery();

            if (result1.next()) {
                System.out.println("Your current credit card number is " + result1.getInt("credit_num"));
            } else {
                System.out.println("You need to create credit card first");
                Credit_service(scan, conn);
                return;
            }
            PreparedStatement sm = conn.prepareStatement("update Credit set credit_num = ? where firstname = " + "'"
                    + firstname + "'" + " and lastname = " + "'" + lastname + "'");
            sm.setInt(1, credit_num);
            sm.executeUpdate();
            System.out.println("Your new debit card number is " + credit_num);
            credit_num++;
        } catch (Exception e) {
        }
        System.out.println("You have a new credit card number. success!");
        Credit_service(scan, conn);
    }

    public static void Credit_pay(Scanner scan, Connection conn) {
        String firstname = "";
        String lastname = "";
        double payment = 0.0;
        double balance = 0.0;
        int credit_id = 0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        try {
            String query1 = "select credit_id from Credit where firstname = " + "'" + firstname + "'"
                    + " and lastname = " + "'" + lastname + "'";
            PreparedStatement smt1;
            ResultSet result1;
            smt1 = conn.prepareStatement(query1);
            result1 = smt1.executeQuery();
            // need to test if later
            if (result1.next()) {
                credit_id = result1.getInt("credit_id");
                System.out.println("you have a credit card !");
            } else {
                System.out.println("You are not a valid credit card user");
                Credit_service(scan, conn);
                return;
            }
            System.out.println("The amount you want to pay for your credit card");
            while (!scan.hasNextDouble()) {
                scan.nextLine();
                System.out.println("Please enter again");
            }
            payment = scan.nextDouble();
            System.out.println("The value you entered " + payment);

            String query = "select  balance from Credit  where  credit_id = " + credit_id;
            PreparedStatement smt;
            ResultSet result;
            smt = conn.prepareStatement(query);
            result = smt.executeQuery();
            if (!result.next()) {
                System.out.println("There is no credit card information for you in the database.");
                Credit_service(scan, conn);
                return;
            } else {
                balance = result.getDouble("balance");
            }
            System.out.println("Your current balance  is  " + balance);
            double new_balance = balance + payment;
            System.out.println("Your new balance  is  " + new_balance);
            PreparedStatement sm = conn.prepareStatement("update Credit set balance = ? where credit_id = " + credit_id);
            sm.setDouble(1, new_balance);
            sm.executeUpdate();
        } catch (Exception e) {
        }

        Credit_service(scan, conn);
    }

    public static void Credit_info(Scanner scan, Connection conn) {
        int credit_num = 0;
        int credit_id = 0;
        String firstname = "";
        String lastname = "";
        double interest_rate = 0.0;
        double balance = 0.0;
        double credit_limit = 0.0;
        System.out.println("Please enter your first name");
        firstname = scan.nextLine();
        System.out.println("Please enter your last name");
        lastname = scan.nextLine();
        String sql = "select credit_id, firstname, lastname, interest_rate, balance, credit_limit, credit_num from Credit where firstname = "
                + "'" + firstname + "'" + " and lastname = " + "'" + lastname + "'";
        try {
            PreparedStatement smt = conn.prepareStatement(sql);
            ResultSet result = smt.executeQuery();
            if (!result.next()) {
                System.out.println("There is no  Credit information for you in the database");
               Credit_service(scan, conn);
               return;
            } else {
                credit_id = result.getInt("credit_id");
                firstname = result.getString("firstname");
                lastname = result.getString("lastname");
                interest_rate = result.getDouble("interest_rate");
                balance = result.getDouble("balance");
               credit_limit = result.getDouble("credit_limit");
                credit_num = result.getInt("credit_num");
                System.out.println("Your Credit Card  id is " + credit_id);
                System.out.println("Your customer name is " + firstname + " " + lastname);
                System.out.println("Current interest rate " + interest_rate);
                System.out.println("current balance " + balance);
                System.out.println("Card limit " + credit_limit);
                System.out.println("your credit card number  " + credit_num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       Credit_service(scan, conn);

    }
}
