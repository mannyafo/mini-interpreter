package com.emmanuelafoakwah;

public class MiniInterpreter {

    /**
     * The capacity of the program
     * Determines the amount of space in the symbol table
     */
    static int capacity = 5;

    /**
     * These arrays store the symbols and values recorded from each instruction
     * The symbols and values are matched by the index they are found
     * at in their respective arrays
     */
    static String[] symbols = new String[capacity];
    static int[] values = new int[capacity];

    /**
     * These are the example inputs to the program
     */
    static String input1 = 	  "A = 2\n" + "B = 8\n" + "C = A + B\n" + "C";              // C = 10
    static String input2 = 	  "A = 2\n" + "B = 22\n" + "Z = 91\n"                       // Z = 26
            + "K = A + B\n" + "Z = K + A\n" + "Z";
    static String input3 = 	  "A = 2 + 1\n" + "B = A + 9\n" + "C = A + B\n" + "A";      // A = 3
    static String input4 = 	  "A = 2 + 1\n" + "B = A + 9\n" + "C = A + B\n" + "A + B";  // 'A + B' -> syntax error

    public static void main(String[] args) {

        // Calling the interpreter for each example input
        runInterpreter(input1);
        runInterpreter(input2);
        runInterpreter(input3);
        runInterpreter(input4);

    }

    /**
     * This function parses the instruction and calls the execution function
     * A message is printed if execution fails
     * @param input to parse and execute
     */
    public static void runInterpreter(String input ) {
        System.out.println("|*****New Input*****|");
        String[] instructions = parseInput(input);
        printStrings(instructions);

        if( executeInterpreter(instructions) == false) {
            System.out.println("Error, please check input.");
        }
    }

    /**
     * This function parses a multiline input string
     * @param input is the string to parse
     * @return an array containing the instructions to execute
     */
    public static String[] parseInput( String input ) {
        return input.split("\n");
    }

    /**
     * This function loops through the array of instructions
     * executing each instruction before moving onto the next
     * @param instructions is the array of instructions to interpret
     * @return a boolean indicating whether the instructions have
     * been successfully interpreted and executed
     */
    public static boolean executeInterpreter( String[] instructions ) {

        for(int i = 0; i < instructions.length; i++) {

            if( interpretInstruction(instructions[i]) == false ) {
                return false;
            }
        }
        return true;
    }


    /**
     * This function interprets a given instruction
     * then calls the necessary processing function
     * to execute the instruction
     * @param instruction is the instruction to interpret
     * @return a boolean indicating whether the interpreting and execution
     * of the instruction was successful
     */
    public static boolean interpretInstruction(String instruction) {

        String[] terms = instruction.split(" ");
        //printStrings(terms);

        int nterms = terms.length;

        if( nterms == 3 && terms[1].equals("=") ) {
            return processAssignment(terms[0], terms[2]);
        }else if( nterms == 5 && terms[1].equals("=") && terms[3].equals("+") ) {
            return processAddition(terms[0], terms[2], terms[4]);
        }else if( nterms == 1 ) {
            return processReturn(terms[0]);
        }
        return false;
    }

    /**
     * This function executes assignment instructions
     * @param symbolTerm is the symbol to assign
     * @param valueTerm is the value to assign to the symbol
     * @return a boolean indicating success or failure
     */
    public static boolean processAssignment( String symbolTerm, String valueTerm ) {

        int symbolIndex = getSymbolIndex(symbols, symbolTerm);
        int intValueTerm = Integer.parseInt(valueTerm);

        if( symbolIndex >= 0 )  {
            values[symbolIndex] = intValueTerm;
        }else if( symbolIndex == -1 ){

            int freeSymbolIndex = getFreeSymbolIndex(symbols);

            if( freeSymbolIndex == -1 ) {
                return false;
            }
            symbols[freeSymbolIndex] = symbolTerm;
            values[freeSymbolIndex] = intValueTerm;
        }else {
            return false;
        }
        return true;
    }

    /**
     * This function executes addition instructions
     * @param symbolTerm is the symbol to apply the addition to
     * @param valueTerm1 is the first value or symbol for the addition
     * @param valueTerm2 is the second value or symbol for the addition
     * @return a boolean indicating success or failure
     */
    public static boolean processAddition( String symbolTerm, String valueTerm1, String valueTerm2) {

        int intValue1 = termToInt(valueTerm1);
        int intValue2 = termToInt(valueTerm2);
        int sum = intValue1 + intValue2;

        int symbolIndex = getSymbolIndex(symbols, symbolTerm);

        if( symbolIndex >= 0 ) {

            values[symbolIndex] = sum;

        }else if( symbolIndex == -1 ) {

            int freeSymbolIndex = getFreeSymbolIndex(symbols);

            if( freeSymbolIndex == -1 ) {
                return false;
            }
            symbols[freeSymbolIndex] = symbolTerm;
            values[freeSymbolIndex] = sum;
        }else {
            return false;
        }
        return true;
    }

    /**
     * This function executes return instructions
     * @param symbolTerm is the symbol to return
     * @return a boolean indicating success or failure
     */
    public static boolean processReturn( String symbolTerm ) {

        int symbolIndex = getSymbolIndex(symbols, symbolTerm);

        if( symbolIndex >= 0 ) {
            System.out.println("Return: " + values[symbolIndex]);
            return true;
        }else {
            return false;
        }
    }

    /**
     * Utility function - converts string term to corresponding integer term
     * If the term is a number, the corresponding integer is returned
     * If the term is a symbol, the corresponding value in
     * the values array is returned
     * @param term is the term to convert
     * @return the converted integer term
     */
    public static int termToInt( String term ) {

        if( isNumeric(term) ) {
            return Integer.parseInt(term);
        }else {
            return values[ getSymbolIndex(symbols, term) ];
        }
    }

    /**
     * Utility function - checks if a string is numeric
     * @param string to check
     * @return boolean indicating whether string is numeric
     */
    public static boolean isNumeric(String string) {
        try {
            double term = Double.parseDouble(string);
        }catch( NumberFormatException nfe ){
            return false;
        }
        return true;
    }


    /**
     * Utility function - gets first free index in the array
     * returns -1 if there are no free indexes
     * @param symbols is the array to check
     * @return the first free index in the array
     */
    public static int getFreeSymbolIndex(String[] symbols) {

        for(int i = 0; i<symbols.length; i++) {

            if( symbols[i] == (null) ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Utility function - gets the index of a symbol
     * returns -1 if the symbol is not in the array
     * @param symbols is the array to check
     * @param symbolTerm is the symbol to check for
     * @return the index of the symbol in the array
     */
    public static int getSymbolIndex(String[] symbols, String symbolTerm) {

        for(int i = 0; i < symbols.length; i++) {
            if( symbols[i] != null) {
                if( symbols[i].equals(symbolTerm) ) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Utility function - print the symbols table
     * @param symbols is the symbol array to print
     * @param values is the values array to print
     */
	public static void printSymbolValues(String[] symbols, int[] values) {
		printStrings(symbols);
		printInts(values);
	}

	/**
     * Utility function - print elements in a string array
     * @param input is the string array to print
     */
	public static void printStrings(String[] input) {
		for(int i = 0; i < input.length; i++) {
			System.out.println(input[i]);
		}
	}

	/**
     * Utility function - print elements in an int array
     * @param input is the int array to print
     */
	public static void printInts(int[] input) {
		for(int i = 0; i < input.length; i++) {
			System.out.println(input[i]);
		}
	}

}
