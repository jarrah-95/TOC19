package TOC19;
//    TOC19 is a simple program to run TOC payments within a small group. 
//    Copyright (C) 2014  Jarrah Gosbell
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
/*
* Author: Jarrah Gosbell
* Student Number: z5012558
* Class: Person
* Description: This program will allow for the input and retreval of persons in the person database.
*/

public class Person
{

	// create the variables that are needed in order of use
	private String name;
	private String output;
	private double totalCostRunning, totalCostWeek; // Running can be yearly, or can be perminant. Up to TOC. Weekly will be reset each stocktake
	private long barCode; // PMKeys number off ID

	public Person(String extName, long extBarCode, double running, double week) // construtor which will give the person its values
	{
		name = extName;
		barCode = extBarCode;
		totalCostRunning = running;
		totalCostWeek = week;
	}
	// Begin methods
	public void setData(String name, int barCode) // redundant method which has been left in case persons are edited in some way which would require this to be used.
	{
		/**
		Class Song: Method setData
		Precondition: Augments String name, String artist, double totalCostRunning, double time are input
		Postcondition: the person that this method was invoked with now has been stored.
		*/
		this.name = name;
		this.barCode = barCode;
	}

	public String getData() // output the person data as a string
	{
		/**
		Class Song: Method getData
		Procondition: setData has been run for invoking person or the person constructor outlined above has been run
		Postcondition: The data that has been entered for the invoking person will be returned. 
		*/
		
		output = "";
		output += "\n	Name: ";
		output += name;
		output += "\n	Running Cost: $";
		output += totalCostRunning;
		output += "\n	Weekly Cost: $";
		output += totalCostWeek;

		return output; //return a string with all of the person's data in it
	}
		public String getDataUser() // output the person data as a string
	{
		/**
		Class Song: Method getData
		Procondition: setData has been run for invoking person or the person constructor outlined above has been run
		Postcondition: The data that has been entered for the invoking person will be returned. 
		*/
		
		output = "";
		output += name;
		output += "\n	Running Cost: $";
		output += totalCostRunning;
		output += "\n	Current Bill Total: $";
		output += totalCostWeek;

		return output; //return a string with all of the person's data in it
	}
	public String getName() // return the name of the person
	{
		/**
		Class Song: Method getData
		Precondition: setData has been run for invoking person
		Post condition: the method will return a string contianing the name.
		*/

		return name;
	}
	public long getBarCode() // return the barcode assoiated with the person
	{
		 /**
		 Class Song: Method getBarCode
		 Precondition: SetData has been run for the invoking person
		 Psotcondition: The method will return an int containing the barcode of the item. 
		 */
		return barCode;
	}
	public double totalCostRunning() // retung the size of the person. 
	{
		/**
		Class Song: Method totalCostRunning
		Precondition: setData has been run for the invoking person
		Postcondition: this method will return the price of the invoking person.
		*/
		
		return totalCostRunning;
	}
	public double totalCostWeek() // retung the size of the person. 
	{
		/**
		Class Song: Method totalCostRunning
		Precondition: setData has been run for the invoking person
		Postcondition: this method will return the price of the invoking person.
		*/
		
		return totalCostWeek;
	}
	public void addPrice(double cost)
	{
		totalCostRunning += cost;
		totalCostWeek += cost;
	}
	public void resetWeekCost()
	{
		totalCostWeek = 0;
	}
}