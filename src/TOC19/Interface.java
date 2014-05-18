//package TOC19;
/***
*    TOC19 is a simple program to run TOC payments within a small group. 
*    Copyright (C) 2014  Jarrah Gosbell
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/* 
* Author: Jarrah Gosbell 
* Student Number: z5012558
* Class: Interface
* Description: This program will allow the user to interact with the program, creating, deleting and modifying products and checkOuts.
*/
// GUI Inports
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
//import javax.swing.*;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.Timer;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//Security imports
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// Other imports
import java.util.Calendar;
import java.util.Enumeration;


public class Interface
{
	// Create the necessary instance variables.
	private ProductDatabase productDatabase;
	private PersonDatabase personDatabase;
	private CheckOut checkOuts;
	private int logicalSize;
//	Timer timeOut = new Timer(60000000, new actionListener());
		
	public Interface() 
	{
		//initalize the variables created above
		productDatabase = new ProductDatabase();
		personDatabase = new PersonDatabase();
		checkOuts = new CheckOut();
		logicalSize = 0;
	}	
	
	
	private void run() 
	{
		/**
		Class Interface: Method run
		Preconditions: Interface constructor has been run
		Postconditions: the program will have run and all user interactions will have been acted upon. 
						If ended correctly, the program will write the productDatabase to productDatabase.txt. The program will end.
		*/
		setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.PLAIN,25));
		// create the variables that will be used throughout the program
		String tempName; // names of things which we will be entering and reusing	
		long tempProductPrice; // the above but as a price
		String tempInput; // what will be storing any input for testing and conversion
		long tempBarCode; 
		int quantity; // the bar code and number of each item that we have. 
		int personNumber = -1; // the position of the person in the database
		int productNumber; // as above but for products
		int q1, q2; // test question integer for menus.
		int i, error, added; //minor variables which will be used in multiple parts of the program. 
		error = 0; // setting up error
		boolean admin = false; // how the program knows that it is in admin mode
		boolean sameUser = true; // How the program knows that it is serving the same user. 
                boolean first = true;
		int another = 0; // whether another item will be added 
		final JPasswordField passwordField = new JPasswordField(10); // box to take passwords from the user
		
		// Import the productDatabase
		q2 = 0;
		error = productDatabase.readDatabase("productDatabase.txt"); // read in the product database
		if(error == -1) { // tell the user there was an error reading the above
			System.out.println("There was an error reading the productDatabase");
		}
		else { // tell the user the above went swimingly. 
			System.out.printf("I have imported %d products\n", error);
		}
		error = personDatabase.readDatabase("personDatabase.txt"); // as above for the person database
		if(error == -1) {
			System.out.println("There was an error reading the personDatabase");
		}
		else {
			System.out.printf("I have imported %d people\n", error);
		}
		while(true) // perminantly loop through this code. 
		{

				tempBarCode = getPMKeyS();
				personNumber = personDatabase.findPerson(tempBarCode); // convert this integer to the person number in the databate.
				sameUser = true; // tells the program that a user is logged in. 
                first = true;
				if(-2 == personNumber) { // checks whether that user is an admin
					String passWd = "";
					passWd = getPassWd(true);
					if (passWd != null) {
						passWd = getSecurePassword(passWd);
					}
					if(passWd != null && !"".equals(passWd) && passWd.equals(personDatabase.getPersonName(-2))) { 
						admin = true; // the above conversion will return -2 for all admins. This will enact that. 
						sameUser = false; // skip the normal user interface for non admin personnel. 
						passWd = null;
					}
					else if(passWd != null) {
						JOptionPane.showMessageDialog(null, "Password incorrect", "Error", JOptionPane.ERROR_MESSAGE);
						admin = false; 
						sameUser = false;
						passWd = null;
					}
					else {
						admin = false; 
						sameUser = false;
					}
				}
			while(sameUser) { // avoids people having to re-enter their PMKeyS to get to the shopping cart if they stuff something up. 
				while(!admin) {
				//	tempInput = JOptionPane.showInputDialog("Hello " + personDatabase.getPersonUser(personNumber) + "\nEnter the bar code of the product you would like");
					tempInput = showInputDialog("<html>Hello " + personDatabase.getPersonUser(personNumber, true) + "<br>Enter the bar code of the product you would like</html>", "what would you like",
												JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(tempInput != null && !tempInput.equals("") && isLong(tempInput)) {
						tempBarCode = Long.parseLong(tempInput); // disallows the user from entering nothing or clicking cancel. 
					}
					else if((tempInput == null && !first) || ("".equals(tempInput) && !first)) {
						break; // if canceled and not on the first run of adding items. 
					}
					else { // what to do if the user does the above on the first run.
						sameUser = false;
						another = 1;
						//checkOuts = new CheckOut();
						break;
					}
					if (tempBarCode == personDatabase.getBarCode(personNumber)) {
						JOptionPane.showMessageDialog(null, "Little early in your career to start selling yourself isn't it?\n"
														+ "This incident has been reported", "Attempted prostitution", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					productNumber = productDatabase.findProduct(tempBarCode); // Now that we have done the error checking, convert the barcode to a position in the database
					if(productNumber == -1) { // -1 is output by the above on error
						JOptionPane.showMessageDialog(null, "That product does not exist, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}

					int checkProduct; // create this for use below
					tempInput = productDatabase.getProduct(productNumber);
					checkProduct = checkOuts.productEqualTo(tempInput); // check that the product was not entered into the database before. If it was, just add the quantity to the one in the database
					if(checkProduct != -1) {
						checkOuts.addQuantity(checkProduct, 1);
					}
					else checkOuts.addProduct(checkOuts.emptyProduct(), productDatabase.getProductRef(productNumber), 1); //otherwise, add the product as normal. 
					//another = JOptionPane.showConfirmDialog(null, "Would you like to add another item?", "Continue", JOptionPane.YES_NO_OPTION);
					another = JOptionPane.showConfirmDialog(null, "You are purchasing " + checkOuts.getCheckOut(1) + "\nWould you like to add more items?" + "\nHitting no will buy this cart", "Cart", JOptionPane.YES_NO_CANCEL_OPTION);
					if(another == JOptionPane.NO_OPTION) break; // let the user buy already. 
					else if(another == JOptionPane.CANCEL_OPTION) {
						checkOuts = new CheckOut();
						sameUser = false;
						break;
					}
					else first = false; // make the add another product. 
				}
				if(!admin && sameUser) {
				//	another = JOptionPane.showConfirmDialog(null, "You are purchasing " + checkOuts.getCheckOut(1) + "\nAre you happy with this?", "Cart", JOptionPane.YES_NO_OPTION);
//					another = 0;
//					if(another == 0) { // purchise and clean up the system.
                        buyProducts(personNumber, checkOuts.getPrice());
						sameUser = false; // reset to enter PMKeyS
						JOptionPane.showMessageDialog(null, "Thank You for coming to TOC", "Thanks", JOptionPane.INFORMATION_MESSAGE);
//					}
//					if(another == 1) { // reset the cart for the user. 
//						JOptionPane.showMessageDialog(null, "Your cart has been reset", "Reset", JOptionPane.INFORMATION_MESSAGE);
//						checkOuts = new CheckOut();
//					}
				}
			}
			while(admin) {
				tempInput = showAdminMenu();
				if(tempInput == null || tempInput.length() < 1) {
					admin = false;
					break;
				}
				else if(tempInput.equals("add products")) {
					int done = 0;
						addToDatabase("product");
				}
				else if(tempInput.equals("remove products")) {	
					error = 1;
					tempInput = showInputDialog("Enter the bar code of the item you would like to delete", "Barcode", JOptionPane.QUESTION_MESSAGE,
							JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(!isLong(tempInput)) continue; // check the input
					tempBarCode = Long.parseLong(tempInput);
					q2 = productDatabase.findProduct(tempBarCode);	
					if(q2 != -1) {
						error = productDatabase.delProduct(q2); // recieve a one value on an error
						productDatabase.writeOutDatabase("productDatabase.txt");
					}
					if(error != 1 && q2 != 0) {
						JOptionPane.showMessageDialog(null, "The product that you asked for has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
						}
					if(error ==  1) {
						JOptionPane.showMessageDialog(null, "The product that you asked for could not be found, please try again", "Error", JOptionPane.ERROR_MESSAGE);
						q2 = 0;
					}
				}
				else if(tempInput.equals("add people")) {
					addToDatabase("person");
				}
				else if(tempInput.equals("remove people")) {
					error = 1;
					tempInput = showInputDialog("Enter the PMKeyS of the person you would like to delete", "PMKeyS", JOptionPane.QUESTION_MESSAGE,
							JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(!isLong(tempInput)) continue;
					tempBarCode = Long.parseLong(tempInput);
						q2 = personDatabase.findPerson(tempBarCode);	
					if(q2 != -1 && tempBarCode != 7000000) {
						error = personDatabase.delPerson(q2); // recieve a one value on an error
						q2++; // confirmation output error
						personDatabase.writeOutDatabase("personDatabase.txt");
					}
					if(error != 1 && q2 != 0) {
						JOptionPane.showMessageDialog(null, "The person that you asked for has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
					}
						if(error ==  1) {
						JOptionPane.showMessageDialog(null, "The person that you asked for could not be found, please try again", "Error", JOptionPane.ERROR_MESSAGE);
						q2 = 0;
						}
				}	
					else if(tempInput.equals("save product database")) {
						error = 0;
						error = productDatabase.adminWriteOutDatabase("adminProductDatabase.txt");
						if(error == 0) { // Inform the user of the write out.
						JOptionPane.showMessageDialog(null, "Your Database has been written to adminProductDatabase.txt", "Success", JOptionPane.INFORMATION_MESSAGE);
						}
						else { //inform the user of an error in writing the productDatabase to disk.
							JOptionPane.showMessageDialog(null, "Your productDatabase has not been written to disk as there was an error finding productDatabase.txt",
																"Error", JOptionPane.ERROR_MESSAGE);
						}
					}	
					else if(tempInput.equals("save person database")) {
						error = 0;
						error = personDatabase.adminWriteOutDatabase("adminPersonDatabase.txt");
						if(error == 0) { // Inform the user of the write out.
						JOptionPane.showMessageDialog(null, "Your Database has been written to adminPersonDatabase.txt", "Success", JOptionPane.INFORMATION_MESSAGE);
						}
						else { //inform the user of an error in writing the productDatabase to disk.
							JOptionPane.showMessageDialog(null, "Your productDatabase has not been written to disk as there was an error finding personDatabase.txt",
															"Error", JOptionPane.ERROR_MESSAGE);
						}
					}		
				else if(tempInput.equals("change product")) {
					int tempNumber = 0; // the number of the product in the database
					double tempPriceDouble = 0;
					tempInput = showInputDialog("Enter the bar code of the product you would like to edit", "Barcode", JOptionPane.INFORMATION_MESSAGE,
							JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(!isLong(tempInput)) {
						continue;
					}
					productNumber = productDatabase.findProduct(Long.parseLong(tempInput));  // find the position of the product in the database
					if(productNumber == -1) {
						JOptionPane.showMessageDialog(null, "The product that you asked for does not exist", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
				     // get the new detials of the product.
					tempInput = showInputDialog("Enter the new name for this product", "Name", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(tempInput == null || tempInput.length() < 1) continue;
					tempName = tempInput;
					tempInput = showInputDialog("Enter the new item Price witout the dollar sign", "Price", JOptionPane.QUESTION_MESSAGE,  JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(!isDouble(tempInput)) continue;
					tempPriceDouble = Double.parseDouble(tempInput);
					tempPriceDouble *= 100;
					tempProductPrice = (long)tempPriceDouble;
					tempInput = showInputDialog("Enter the new barCode of the product", "Barcode", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(!isLong(tempInput)) continue;
					tempBarCode = Long.parseLong(tempInput);
					tempNumber = productDatabase.getNumber(productNumber); //returns the number of this product in stock. 
					productDatabase.delProduct(productNumber);
					added = productDatabase.setDatabaseProduct(productDatabase.emptyProduct(), tempName, tempProductPrice, tempBarCode);
					if(added == 0) {
						JOptionPane.showMessageDialog(null, "Your product has been changed in the database", "Success", JOptionPane.INFORMATION_MESSAGE);
						productDatabase.setNumber(productDatabase.findProduct(tempBarCode), tempNumber);
					}
					else { // output when the user tries to edit the product to one that already exists.
						JOptionPane.showMessageDialog(null, "The new data that you tried to use for this product is already in use in the productDatabase", "Error", JOptionPane.ERROR_MESSAGE);
					}
					productDatabase.writeOutDatabase("productDatabase.txt");
				}
				else if(tempInput.equals("reset bills")) {
					another = 1;
					another = JOptionPane.showConfirmDialog(null, "Are you sure that you would like to reset the TOC bills?", "Are you sure?", JOptionPane.YES_NO_OPTION);
					if(another == 0) {
						personDatabase.resetBills(); // reset the bills
						personDatabase.writeOutDatabase("personDatabase.txt"); // save the new database to file
						JOptionPane.showMessageDialog(null, "Bills reset", "Success", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null, "Bills have not been reset.", "Error", JOptionPane.ERROR_MESSAGE);

					}
				}
				else if(tempInput.equals("print the person database to the screen")) {
					printDatabase("Person");
//					JTextArea textArea = new JTextArea(personDatabase.getDatabase(1)); // create the text to be displayed
//					textArea.setEditable(false); // stop the user being able to edit this and thinking it will save. 
//					JScrollPane scrollPane = new JScrollPane(textArea); // create the scrolling window for the text
//					textArea.setLineWrap(true); // force line wrap, this should not be needed, but is enabled anyway. 
//					textArea.setWrapStyleWord(true); // make wrap work on a perword basis rather than percharacter
//					scrollPane.setPreferredSize(new Dimension(500,500)); //set the size of the scrollpane
//					JOptionPane.showMessageDialog(null, scrollPane, "Person Database", JOptionPane.INFORMATION_MESSAGE); // output the scrollpane. 
				}
				else if(tempInput.equals("print the product database to the screen")) { // see above
					printDatabase("Product");
//					JTextArea textArea = new JTextArea(productDatabase.getDatabase(1));
//					textArea.setEditable(false); // stop the user being able to edit this and thinking it will save. 
//					JScrollPane scrollPane = new JScrollPane(textArea);
//					textArea.setLineWrap(true);
//					textArea.setWrapStyleWord(true);
//					scrollPane.setPreferredSize(new Dimension(500,500));
//					JOptionPane.showMessageDialog(null, scrollPane, "Product Database", JOptionPane.INFORMATION_MESSAGE);
				}
				else if(tempInput.equals("save databases to USB")) { 
					error = 0;
					error = personDatabase.adminWriteOutDatabase("adminPersonDatabase.txt");
					error = productDatabase.adminWriteOutDatabase("adminProductDatabase.txt");
					if(error != 0) JOptionPane.showMessageDialog(null, "Could not copy the database", "Error", JOptionPane.ERROR_MESSAGE);
					try {
						ProcessBuilder pb = new ProcessBuilder("copy.sh");
						Process p = pb.start();
						if (error == 0) JOptionPane.showMessageDialog(null, "Database copied", "Success", JOptionPane.INFORMATION_MESSAGE);
					}
					catch (java.io.IOException e) {
						JOptionPane.showMessageDialog(null, "Could not copy the database", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
				else if(tempInput.equals("Enter stock counts (bulk)")) {
					int tempNumber = 0;
					for(i = 0; productDatabase.productExists(i); i++) { // for each product ask for the new number of items you have. 
						tempInput = showInputDialog("You have " + productDatabase.getNumber(i) + " " + productDatabase.getProductName(i) + 
								" left from last stocktake\n Including these, how many do you have now?", "Stock Count", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, new String[0], false);
						if(!isInteger(tempInput)) continue;
						tempNumber = Integer.parseInt(tempInput);
						productDatabase.setNumber(i, tempNumber);
					}
					productDatabase.writeOutDatabase("productDatabase.txt");
				}
				else if(tempInput.equals("Enter stock count (individual)")) {
					int tempNumber = 0;
					tempInput = showInputDialog("Enter the bar code of the product you would like to set", "Barcode", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, new String[0], false);
					if(!isLong(tempInput)) continue;
					tempBarCode = Long.parseLong(tempInput);
					productNumber = productDatabase.findProduct(tempBarCode); // corrilate the product number with that in the database
					if(productNumber == -1) {
						JOptionPane.showMessageDialog(null, "The product that you asked for does not exist", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					tempInput = showInputDialog("You had " + productDatabase.getNumber(productNumber) + " " + productDatabase.getProductName(productNumber) + 
							" left from last stocktake\n Inclunding these, how many do you have now?", "Stock Counts", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, new String[0], false); // ask the user how many they had, and how many they now have
					if(!isInteger(tempInput)) continue;
					tempNumber = Integer.parseInt(tempInput);
					productDatabase.setNumber(productNumber, tempNumber); // enter all this into the database and write it out.
					productDatabase.writeOutDatabase("productDatabase.txt");
				}
				else if(tempInput.equals("change password")) {
					String passWd = "";
					passWd = getPassWd(true);
					String newPassWd = "";
					if (passWd != null) {
						passWd = getSecurePassword(passWd);
					}
					if(passWd != null && !"".equals(passWd) && passWd.equals(personDatabase.getPersonName(-2))) { 
						passWd = getPassWd(true);
						newPassWd = getPassWd(false);
						if(passWd != null && newPassWd != null && !passWd.equals("") && !newPassWd.equals("") && passWd.equals(newPassWd)) {
							passWd = getSecurePassword(passWd);
							personDatabase.setAdminPassword(passWd);
							personDatabase.writeOutDatabase("personDatabase.txt");
							JOptionPane.showMessageDialog(null, "Success, Password changed", "Success", JOptionPane.INFORMATION_MESSAGE);
						passwordField.setText("");
						}
						else {
							JOptionPane.showMessageDialog(null, "Password incorrect", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					
				}
				else if(tempInput.equals("close the program")) {
					personDatabase.writeOutDatabase("personDatabase.txt"); // write any somehow missed changes out and then exit
					productDatabase.writeOutDatabase("productDatabase.txt");
					System.exit(0);
				}
				else {
					admin = false; // somehow picked an option which doesn't exist or simpily pressed cancel. 
					break;
				}
			}
		}
	}
	public static void main(String[] args)
	{

		Interface intFace = new Interface(); // initalise task
	
		intFace.run(); // Engage

	}
	public CheckOut[] resizeCheckOut(Boolean action, CheckOut[] resizing)
	{
		if(action) {
			return (Arrays.copyOf(resizing, resizing.length + 1));
		}
		else {
			return (Arrays.copyOf(resizing, resizing.length - 1));
		}
		
	}
	public boolean isInteger(String s) 
	{
		if(s == null) return false;
		try { 
			Integer.parseInt(s); // try to parse the string, catching a failure
		} 
		catch(NumberFormatException e) { 
			return false; 
		}
		// only got here if we didn't return false
		return true;
	}
	public boolean isDouble(String s) 
	{
		if(s == null) return false;
		try { 
			Double.parseDouble(s);  // try to parse the string, catching a failure
		} 
		catch(NumberFormatException e) { 
		 return false; 
		}
		// only got here if we didn't return false
		return true;
	}
	public boolean isLong(String s)
	{
		if(s == null) return false;
		try {
			Long.parseLong(s); // try to parse the string, catching a failure. 
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	private static String getSecurePassword(String passwordToHash)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    private long getPMKeyS() // take this recursion and make it iteration. 
    {
        boolean correct = false;
        String tempInput = "error";
        while (!correct) {   
            tempInput = JOptionPane.showInputDialog(null, "Enter your PMKeyS", "PMKeyS", JOptionPane.QUESTION_MESSAGE);
            if(tempInput == null) { // First check that the PMKeyS was properly entered. This is for the cancel button
                JOptionPane.showMessageDialog(null, "I cannot allow you to close the program Dave. Sorry", "Error", JOptionPane.ERROR_MESSAGE); // 2001 esq error message for a bad PMKeyS
                continue;
            }
            else if(tempInput.equals("") || !isLong(tempInput) || tempInput.length() != 7 || !personDatabase.personExists(Integer.parseInt(tempInput))) { // checks for valid numbers in the PMKeyS
                JOptionPane.showMessageDialog(null, "Please enter your valid PMKeyS number", "Errror", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            correct = true;
        }
        return Long.parseLong(tempInput);
    }
	@SuppressWarnings("empty-statement")
	private String getPassWd(boolean first)
	{
		String passWd = "";
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		if (first) panel.add(new JLabel("Enter Password\n\n"));
		else panel.add(new JLabel("Re-enter Password\n\n"));
		final JPasswordField passwordField = new JPasswordField(10); // box to take passwords from the user
		panel.add(passwordField);
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectInitialValue() {
				passwordField.requestFocusInWindow();
			}
		};
		pane.createDialog(null, "Enter Password").setVisible(true);
		passWd = passwordField.getPassword().length == 0 ? null : new String(passwordField.getPassword());
		passwordField.setText("");
		return passWd;
	}
	private void printDatabase(String name)
	{
		JTextArea textArea;
		switch(name) {
			case("Product"):textArea = new JTextArea(productDatabase.getDatabase(1));
							break;
			case("Person"):textArea = new JTextArea(personDatabase.getDatabase(1));
							break;
			default:textArea = new JTextArea(personDatabase.getDatabase(1));
							break;
		}
		textArea.setEditable(false); // stop the user being able to edit this and thinking it will save. 
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setPreferredSize(new Dimension(800,600));
		JOptionPane.showMessageDialog(null, scrollPane, name + "Database", JOptionPane.INFORMATION_MESSAGE);
		
	}
        private void buyProducts(int personNumber, long price)
        {
            personDatabase.addCost(personNumber, price);// add the bill to the persons account
            checkOuts.productBought(); // clear the quantities and checkout
            productDatabase.writeOutDatabase("productDatabase.txt"); // write out the databases. 
            personDatabase.writeOutDatabase("personDatabase.txt");
            checkOuts = new CheckOut(); // ensure checkout clear
        }
        private String showAdminMenu()
        {
            String[] options = new String[] {"add products", "change product", "remove products", "add people", "remove people", "save person database", "save product database", 
					"print the person database to the screen", "print the product database to the screen", "reset bills", "Enter stock counts (bulk)", "Enter stock count (individual)", 
					"change password", "save databases to USB", "close the program"}; // admin options
			return showInputDialog("Select Admin Option", "Admin Menu", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, options, true);
           // return (String)JOptionPane.showInputDialog(null, "Select Admin Option", "Admin Menu", JOptionPane.PLAIN_MESSAGE, null, options, "ham"); // Don't ask me what ham does. 
        }
		private void addToDatabase(String type)
		{
			int done = 0;
			String tempInput;
			String tempName = "error";
			int added = 1;
			long tempBarCode;
			int q2 = 0;
			long tempProductPrice = 0;
			double tempPriceDouble = 0;
			
			boolean person = !(type.equals("product"));
			while(done != 1) { // Not sure that this while loop has a reason for existance. 
				if(!person) {
					q2 = productDatabase.emptyProduct(); // find the next available product.
				}
				else {
					q2 = personDatabase.emptyPerson();
				}
				if(q2 != -1) {
					if(!person) {
						tempInput = JOptionPane.showInputDialog(null, "Please enter the name of the product that you would like to create: ", "Product Name", JOptionPane.QUESTION_MESSAGE);
					}
					else {
						tempInput = JOptionPane.showInputDialog(null, "Please enter the name of the person that you would like to add: ", "Person Name", JOptionPane.QUESTION_MESSAGE);
					}
					if(tempInput == null) break; // testing that a string was entered
					else if(tempInput.length() < 1) {
						JOptionPane.showMessageDialog(null, "Please enter a valid name", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					tempName = tempInput;
					if(!person) {
						tempInput = JOptionPane.showInputDialog(null, "Please enter the price of the new product: (no dollar Sign, decimals are fine) ", "Price", JOptionPane.QUESTION_MESSAGE);
						if (tempInput == null) {
							done = 1; // exit the loop
							continue;
						}
						else if(!isDouble(tempInput)) {
							JOptionPane.showMessageDialog(null, "You did not enter a valid price.\n Maybe you added the dollar sign, don't next time.", "Error", JOptionPane.ERROR_MESSAGE);
							continue; // Ensure that the string is a double
						}
						tempPriceDouble = Double.parseDouble(tempInput);
						tempPriceDouble *= 100;
						tempProductPrice = (long)tempPriceDouble;
					}
					if(!person) {
						tempInput = JOptionPane.showInputDialog(null, "Please enter the bar code of " + tempName, "Barcode", JOptionPane.QUESTION_MESSAGE);
					}
					else {
						tempInput = JOptionPane.showInputDialog(null, "Please enter the PMKeyS of " + tempName, "PMKeyS", JOptionPane.QUESTION_MESSAGE);
					}
					if(tempInput == null) {
						done = 1; // exit the loop
						continue;
					}
					else if(!isLong(tempInput)) {
						JOptionPane.showMessageDialog(null, "You did not enter a valid barcode", "Error", JOptionPane.ERROR_MESSAGE);
						continue; // ensure that the string is an integer. 
					}
					tempBarCode = Long.parseLong(tempInput);
					if(!person) {
						if(productDatabase.findProduct(tempBarCode) != -1) {
						JOptionPane.showMessageDialog(null, "The barcode you entered has already been taken", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
						}
						added = productDatabase.setDatabaseProduct(q2, tempName, tempProductPrice, tempBarCode);
					}
					else {
						if(productDatabase.findProduct(tempBarCode) != -1) {
						JOptionPane.showMessageDialog(null, "The PMKeyS you entered has already been taken", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
						}
						added = personDatabase.setDatabasePerson(q2, tempName,0 ,0, tempBarCode);
					}
					// send the values to productDatabase where they will be sent to the product/erson constructor. 
					if(!person) {
						productDatabase.writeOutDatabase("productDatabase.txt"); // ensure that the database has been saved to file
					}
					else {
						personDatabase.writeOutDatabase("personDatabase.txt");
					}
					if(added == 0) { // output on success
						if(!person) {
							JOptionPane.showMessageDialog(null, tempName + " is now a product in your product database", "Success", JOptionPane.INFORMATION_MESSAGE); 
						}
						else {
							JOptionPane.showMessageDialog(null, tempName + " is now a person in your person database", "Success", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else { // output on error
						if(!person) {
							JOptionPane.showMessageDialog(null, tempName + " is already a product in your product database", "Error", JOptionPane.ERROR_MESSAGE);
						}
						else {
							JOptionPane.showMessageDialog(null, tempName + " is already a product in your product database", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					done = 1; // close the loop
				}
			}
		}
		public static void setUIFont (javax.swing.plaf.FontUIResource f)
		{
			java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get (key);
				if (value != null && value instanceof javax.swing.plaf.FontUIResource)
					UIManager.put (key, f);
			}
		}
		public String showInputDialog(String message, String title, int messageType, int optionType, final String[] text, boolean combo)
		{
			String input = "";
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.add(new JLabel(message));
			final JTextField textField = new JTextField(15);
			final JComboBox<String> comboBox = new JComboBox<>(text);
			
			if(combo) {
		
				panel.add(comboBox);
			}
			else {
				
				panel.add(textField);
			}
			final JOptionPane pane;
			pane = new JOptionPane(panel, messageType, optionType) {
				private static final long serialVersionUID = 1L;
			@Override
			public void selectInitialValue() {
				if (!text.equals(new String [0])) {
					comboBox.requestFocusInWindow();
				}
				else {
					textField.requestFocusInWindow();
				}
			}
		};
			final JDialog dialog = pane.createDialog(null, title); // .setVisible(true);
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					long start = Calendar.getInstance().getTimeInMillis();
					long now = Calendar.getInstance().getTimeInMillis();
					while (now - start < 90000)
					{
						now = Calendar.getInstance().getTimeInMillis();
					}
					dialog.dispose();
					pane.setValue(JOptionPane.CANCEL_OPTION);
				}
			}).start();
			dialog.setVisible(true);
			Object cancel = pane.getValue();
			if(cancel == null) return null;
			if(cancel instanceof Integer && (int)cancel == JOptionPane.CANCEL_OPTION) {
				return null;
			}
			
			if (combo) {
				input = (String)comboBox.getSelectedItem();
			}
			else input = textField.getText().length() == 0 ? null : textField.getText();
			return input;
		}
} // and that's a wrap. Computer, disable all command functions and shut down for the night. I'll see you again in the morning.      
