package com.transaction_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.util.Scanner;

public class BankApp {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		String url = "jdbc:mysql://localhost:3306/account";
		String un = "root";
		String pwd = "root";

		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, un, pwd);

//			Login Module
			System.out.println("Welcome To NGV Bank");
			System.out.println("Enter Account Number:");
			int acc_num = sc.nextInt();
			System.out.println("Enter Pin:");
			int pin = sc.nextInt();

			PreparedStatement pstmt1 = con.prepareStatement("select * from acc where acc_num = ? and pin = ? ");
			pstmt1.setInt(1, acc_num);
			pstmt1.setInt(2, pin);
			ResultSet res = pstmt1.executeQuery();

			res.next();
			String name = res.getString(2);
			int bal = res.getInt(4);
			System.out.println("Welcome " + name);
			System.out.println("Available balance is: " + bal);

//			Transfer Module
			System.out.println("Transfer Details");
			System.out.println("Enter the beneficiary Account Number: ");
			int bacc_num = sc.nextInt();
			System.out.println("Enter Transfer Amount: ");
			int t_amount = sc.nextInt();

			con.setAutoCommit(false);
			Savepoint s = con.setSavepoint();

			PreparedStatement pstmt2 = con.prepareStatement("update acc set balance = balance - ? where acc_num = ? ");
			pstmt2.setInt(1, t_amount);
			pstmt2.setInt(2, acc_num);
			pstmt2.executeUpdate();

			System.out.println("Incoming credit Request");
			System.out.println(name + " account no " + acc_num + " wants to transfer " + t_amount);
			System.out.println("Press Y to receive");
			System.out.println("Press N to reject");

			String choice = sc.next();
			if (choice.equals("Y")) {
				PreparedStatement pstmt3 = con
						.prepareStatement("update acc set balance = balance + ? where acc_num = ? ");
				pstmt3.setInt(1, t_amount);
				pstmt3.setInt(2, bacc_num);
				pstmt3.executeUpdate();

				PreparedStatement pstmt4 = con.prepareStatement("select * from acc where acc_num = ? ");
				pstmt4.setInt(1, bacc_num);
				ResultSet res2 = pstmt4.executeQuery();
				res2.next();

				System.out.println("Updated balance is: " + res2.getInt(4));
			} else {
				con.rollback(s);
				PreparedStatement pstmt5 = con.prepareStatement("select * from acc where acc_num = ? ");
				ResultSet res2 = pstmt5.executeQuery();
				res2.next();

				System.out.println("Existing balance is: " + res2.getInt(4));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

	}

}
