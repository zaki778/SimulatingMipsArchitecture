package dataPath;
/*

 * -------------------
 * FINAL :
 * each instruction working fine alone
 * need to check the branch and jump instructions
 * 
 * 
 */


//Program Flow
//a) You must write your program in assembly language in a text file. DONE
//b) Your must read the instructions from the text file, and parse them according to their types/formats
//(opcode and other relevant fields). DONE
//c) You must store the parsed version of the instructions in the memory (instruction segment of main
//memory or instruction memory according to your package). DONE 

                                            //************\\
//ALL THE ABOVE STEPS ARE DONE BY THE METHOD PROGRAM LOADER AND INSTRUCTION SAVED IN MEMORY AS CHARS AS EACH CHAR IS 16 BITS SO THIS
//GURANTEES THE BINARY EQUIVALENT NEVER CHANGING
											//************\\

//d) You should start the execution of your pipelined implementation by fetching the first instruction from the memory 
//(instruction segment of main memory or instruction memory) at Cloc Cycle 1.
//e) You should continue the execution based on the example provided in the Datapath section of
//each package reflecting the different stages working in parallel.
//f) The Clock Cycles can be simulated as a variable that is incremented after finishing the required
//stages at a given time.
//• Example:
//f e t c h ( ) ;
//decode ( ) ;
//e x e c u t e ( ) ;
//// memory ( ) ;
//// w r i t e b a c k ( ) ;
//c y c l e s ++;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;



public class allSubRoutines 

{
	
	static int virtualClk=0;
	static boolean parentLoop=true;
	static String instructionName;
	static int neededClk;
	static boolean isBranchReg;
	static String decodedButNot;
	
	static String instructionToBeDecoded;
	static boolean isThereExec;
	static boolean isThereFetch;
	static boolean isThereDecode;
	
	static int progInstruction=0;
	static int addrMemInst=0;
	//****ControlSignals***
	static	Signal	regData, memToReg, alu1,regWrite, branch, memRead, memWrite, jump, alu2;
	static String aluOp;
	//***INSTRUCTION MEMORY
	
    //static ArrayList<Character> instructionMemory=new ArrayList<Character>(Collections.nCopies(1024, 0)); 
    //words of 8 bits
    static char[]instructionMemory=new char[1024];
    
    static byte[]dataMemory=new byte[2048]; 
    static //***REGISTERS
	short pc=0;//data type is short bec pc is 16 bits
	static byte[] registerFile=new byte[64];//data type is byte because each register is 8 bits
	static char[] statusRegister=new char[8]; // data type is byte bec its 8 bits
	static int clock=1;
	
//	***Converting from Byte to its sign equivalent 8 bits binary e.g -128 -->10000000 and 127-->01111111
	//helper Method GENERAL
	public static void init() 
	{
		neededClk=0;
		instructionToBeDecoded="";
		isThereFetch=true;
		isThereDecode=false;
		isThereExec=false;
		isBranchReg=false;
		dataMemory[13]=100;
//		dataMemory[2]=2;
//		dataMemory[3]=3;
//		
		
//		registerFile[0]=3;
//		
//		registerFile[1]=0;
//		registerFile[2]=2;
//		
//		registerFile[3]=3;
//		registerFile[4]=40;
//		registerFile[5]=50;
//		registerFile[6]=0;
//		registerFile[7]=70;
//		registerFile[8]=80;
//		registerFile[9]=90;
//		registerFile[10]=100;
//		registerFile[11]=110;
//		
//
//		registerFile[12]=120;
//		registerFile[13]=127;
//		registerFile[14]=126;
//		
		
		
		
		statusRegister[0]='0';
		statusRegister[1]='0';
		statusRegister[2]='0';
		
	}
	
	//This method removes the first char of a string a return it
	 public static String
	    removeFirst(String str)
	    {
	  
	        // Creating a StringBuilder object
	        StringBuilder sb = new StringBuilder(str);
	  
	  
	        // Removing the first character
	        // of a string
	        sb.deleteCharAt(0);
	  
	        // Converting StringBuilder into a string
	        // and return the modified string
	        return sb.toString();
	    }
	 
	//returns 8 bits representation of signed byte number 
	public static  String fromByteToBinary(byte x)
	{
		 
		String op1Binary=(x<0)?Integer.toBinaryString(x).substring(24, 32):'0'+Integer.toBinaryString(x);
		return extender(op1Binary, 8, true);
	}
	
	//*** used to exend binary number to any length and it can be signed or not signed
	//helper Method GENERAL
	public static String signExtender(String toBeExtended, int amount)
	{
		//String extended="";
		String MSB=toBeExtended.charAt(0)+"";
		while(toBeExtended.length()!=amount)
		{
		toBeExtended=MSB+toBeExtended;	
		}
		return toBeExtended;
	}
	public static String extender(String toBeExtended, int newLength, Boolean isSigned)
	{
		String MSB=toBeExtended.charAt(0)+"";
		
			while(toBeExtended.length()!=newLength)
				toBeExtended=(isSigned)?MSB+toBeExtended:'0'+toBeExtended;
		
		return toBeExtended;
		
	}
	
	// Takes as parameter e.g R0 and execute the binary equivalent of the value 0 
	// another e.g R63 --> binary equivalent of 63
	// used when parsing the instruction 
	//helper method used in PROGRAM LOADER
	
	public static String registerParser(String R)
	{
		String R1S=(R.length()==2)?R.charAt(1)+"":R.substring(1, 3);
		int R1=Integer.parseInt(R1S);
		R1S=Integer.toBinaryString(R1);
		
		//3shan lw el rakam postive toBinaryString htrg3
		//el bits bt3to bzbt fa e.g 8 htb2a 1000
		//fa lw 3mlt extend bel 1 htb2a moshkla msh 3ayz kda 
		//3shan kda extender btakhod parameter false
		return extender(R1S, 6, false);
	}
	//**Parse the whole text instruction to its binary equivalent
	//**e.g ADD-->"0000" and so on ..
	//helper method used in PROGRAM LOADER
	public static String parser(String[] instructionText)
	{
		String s="";
		switch(instructionText[0])
		{
		case "ADD":s+="0000";break;
		case "SUB":s+="0001";break;
		case "MUL":s+="0010";break;
		case "MOVI":s+="0011";break;
		case "BEQZ":s+="0100";break;
		case "ANDI":s+="0101";break;
		case "EOR":s+="0110";break;
		case "BR":s+="0111";break;
		case "SAL":s+="1000";break;
		case "SAR":s+="1001";break;
		case "LDR":s+="1010";break;
		default:s+="1011";break;
		}
		s+=registerParser(instructionText[1]);
		//check if its working with all instructions 
		if(instructionText[2].charAt(0)=='R')
		{
			s+=registerParser(instructionText[2]);
		}
		else
		{
		Boolean isSigned=(instructionText[2].charAt(0)=='-')?true: false;
		String newInst=(isSigned)?removeFirst(instructionText[2]):instructionText[2];
		byte x=(isSigned)?(byte)Integer.parseInt(newInst):Byte.parseByte(instructionText[2]);
		x=(isSigned)?(byte)(-1*x):x;
	
	String immStr=fromByteToBinary(x);
		
		s+=immStr.substring(2,8);
		}
		
		return s;
	}
	// Add the binary instruction to the memory loading the instruction
	//helper method used in PROGRAM LOADER
	public static void addInstructionToMemory(String s, char[] instructionMemory)
	{
		
	    char c1=(char)Integer.parseInt(s,2);
	    
		instructionMemory[addrMemInst]=c1;
		addrMemInst++;
	}
	
	//--------------------------------------------------------------------------------------------------------\\
	 //helper method in fetch()
	public static String readFromInstructionMemory(int pc)
	{
		char theInstuctionC=instructionMemory[pc];// the instruction as char
		//converting this char to its binary equivalent
		String theInstructionS=Integer.toBinaryString(theInstuctionC);
		
	return	extender(theInstructionS, 16, false);
	}
	//------------------------------------------------------------------------------------------------------\\
	
	//helper Method for decode()
	public static byte readFromRegisterFiles(String regAddr)
	{
		//String addrR1=instruction.substring(min, max);//get the address of the register R1 in binary
		int addR1I=Integer.parseInt(regAddr,2);//converting the addres to its equivalent int value
		return registerFile[addR1I];// the data in this register as byte because it's 8 bits
	}
	
	//----------------------------------------------------------------------------------------\\
	//ALL THE COMING METHODS ARE HELPER METHODS FOR Execute()
	//They simulate the ALU
	private static  byte and(byte op1, byte op2) 
	{
		// ***THE BINARY EQUIVALENT OF THE 2 OPERANDS 8 BITS
		String op1B= fromByteToBinary(op1);
		String op2B= fromByteToBinary(op2);
		
		String res="";
		for (int i=0; i<op1B.length(); i++)
		{
			String c1=op1B.charAt(i)+"";
			String c2=op2B.charAt(i)+"";
			res+=(c1.equals("1") && c2.equals("1"))?"1":"0";
		}
	byte resB=(byte)Integer.parseInt(res,2);
	//making the negative flag 1 when the result is negative
		statusRegister[5]=(resB<0)?'1':'0';
		statusRegister[7]=(resB==0)?'1':'0';
		
		return resB ;
		
	}
	private static byte eor(byte op1, byte op2) 
	{
		// ***THE BINARY EQUIVALENT OF THE 2 OPERANDS 8 BITS
		String op1B= fromByteToBinary(op1);
		String op2B= fromByteToBinary(op2);
		
		String res="";
		
		for (int i=0; i<op1B.length(); i++)
		{
			String c1=op1B.charAt(i)+"";
			String c2=op2B.charAt(i)+"";
			res+=(c1.equals(c2))?"0":"1";
		}
		byte resB=(byte)Integer.parseInt(res,2);
		
		//making the negative flag 1 when the result is negative
		statusRegister[5]=(resB<0)?'1':'0';
		
		statusRegister[7]=(resB==0)?'1':'0';
		
		
		return resB;
		
	}
	
	private static byte conc(byte op1, byte op2) 
	{
		String op1B= fromByteToBinary(op1);
		String op2B= fromByteToBinary(op2);
		
		String res=op1B+op2B;
		
		return (byte)Integer.parseInt(res,2);
		
	}
	
	
	public static  byte addSub(byte op1, byte op2)
	{
		//the calculated result in byte
		byte res=(byte)(op1+op2);
		
		
		//the actual real result
		//int resInt=op1+op2; 
		
		int op1P= ((op1<0)?128*2+op1:op1);
		int op2P= ((op2<0)?128*2+op2:op2);
		 int resUnsigned=op1P+op2P;
		 
		 if(!(instructionName.equals("MOVI")) && !(instructionName.equals("BEQZ")) && !(instructionName.equals("STR")) && !(instructionName.equals("lDR")) )
		 {
	//	String s=extender(Integer.toBinaryString(resUnsigned),32,false);
		//3shan lw el addition tele3 akbr mn 8 bits e.g 1+127=128 needs 9 bits (for signed no.s)
	//	statusRegister[3]=s.charAt(23);
		statusRegister[3]=(resUnsigned>255)?'1':'0';
		//updating the Overflow flag whenever the result sign is opposite to the sign of both operands.
		//note this happens only when the 2 operands have the same sign
	    statusRegister[4]=((res>=0 && op1<0 &&op2<0)||(res<=0 &&op1>0 &op2>0) )?'1':'0';
		//Making the negative flag 1 when the result is negative 0 otherwise.
		statusRegister[5]=(res<0)?'1':'0';
		//XORING between the V flag and the N flag and storing the valie in S flag
		statusRegister[6]=(statusRegister[4]==statusRegister[5])?'0':'1';
		statusRegister[7]=(res==0)?'1':'0';
		 }
		
		return res;
	}
	//its always op1-op2
	//so it changes the sign of op2 and call add
	
	public static byte mul(byte b1, byte b2)
	{
		byte res=(byte)(b1*b2);
		int resInt=b1*b2;
		int op1P= ((b1<0)?128*2+b1:b1);
		int op2P= ((b2<0)?128*2+b1:b2);
		 int resUnsigned=op1P+op2P;
		 
	//	String s=extender(Integer.toBinaryString(resUnsigned),32,false);
		//3shan lw el addition tele3 akbr mn 8 bits e.g 1+127=128 needs 9 bits (for signed no.s)
	//	statusRegister[3]=s.charAt(23);
		statusRegister[3]=(resUnsigned>255)?'1':'0';
		
		//Making the negative flag 1 when the result is negative 0 otherwise.
		statusRegister[5]=(res<0)?'1':'0';
		statusRegister[7]=(res==0)?'1':'0';
		
		return res;
	}
	private static byte shiftL(byte op1, byte op2) 
	{
		String zeros="";
		//In this case op2 is the immediate value
		for(int i=0; i<op2; i++)
		{
			zeros+="0";
		}
		String op1B=fromByteToBinary(op1);
		op1B+=zeros;
		int upper=op1B.length();
		int lower=upper-8;
		String s=op1B.substring(lower, upper);
		byte res=(byte)Integer.parseInt(s, 2);
		//Making the negative flag 1 when the result is negative 0 otherwise.
				statusRegister[5]=(res<0)?'1':'0';
				
				statusRegister[7]=(res==0)?'1':'0';
				
		return res;
	}
	private static byte shiftR(byte op1, byte op2) 
	{
		String zeros="";
		String op1B=fromByteToBinary(op1);
		String MSB=op1B.charAt(0)+"";
		//In this case op2 is the immediate value
		for(int i=0; i<op2; i++)
		{
			zeros+=MSB;
		}
		
		op1B=zeros+op1B;
		
		String s=op1B.substring(0, 8);
		
		byte res=(byte)Integer.parseInt(s, 2);
		//Making the negative flag 1 when the result is negative 0 otherwise.
				statusRegister[5]=(res<0)?'1':'0';
				
				statusRegister[7]=(res==0)?'1':'0';
				
		return res;
	}
	// the control unit
	//helper method used in execute()
	public static  void controlUnit(int opCode)
	{
		 	switch (opCode) 
		 	{
			case 0:
				{
					
				instructionName="ADD";
					aluOp="000" ;
					isBranchReg=false;
					regWrite=Signal.One;
					regData=Signal.Zero;
					alu1=Signal.Zero;
					alu2=Signal.Zero;
					memWrite=Signal.Zero;
					memRead=Signal.Zero;
					memToReg=Signal.Zero;
					branch=Signal.Zero;
					
				};break;
			case 1:
			{
				instructionName="SUB";
				isBranchReg=false;
				aluOp="001" ;
				regWrite=Signal.One;
				regData=Signal.Zero;
				alu1=Signal.Zero;
				alu2=Signal.Zero;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 2:
			{
				instructionName="MUL";
				isBranchReg=false;
				aluOp="010" ;
				regWrite=Signal.One;
				regData=Signal.Zero;
				alu1=Signal.Zero;
				alu2=Signal.Zero;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
						
			case 3:
			{
				//MOVI
				instructionName="MOVI";
				isBranchReg=false;
				aluOp="000" ;//hts5dem add
				regWrite=Signal.One;
				regData=Signal.X;
				alu1=Signal.One;
				alu2=Signal.One;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 4:
			{
				//BEQZ
				instructionName="BEQZ";
				isBranchReg=false;
				aluOp="000" ;
				regWrite=Signal.Zero;
				regData=Signal.One;
				alu1=Signal.One;
				alu2=Signal.Zero;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.X;
				branch=Signal.One;
			};break;
			case 5:
			{
				//ANDI
				instructionName="ANDI";
				isBranchReg=false;
				aluOp="011" ;
				regWrite=Signal.One;
				regData=Signal.X;
				alu1=Signal.Zero;
				alu2=Signal.One;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 6:
			{
				//EOR
				instructionName="EOR";
				isBranchReg=false;
				aluOp="100" ;
				regWrite=Signal.One;
				regData=Signal.Zero;
				alu1=Signal.Zero;
				alu2=Signal.Zero;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 7:
			{
				//BR
				instructionName="BR";
				isBranchReg=true;
				aluOp="101" ;
				regWrite=Signal.One;
				regData=Signal.Zero;
				alu1=Signal.Zero;
				alu2=Signal.Zero;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 8:
			{
				//SAL
				instructionName="SAL";
				isBranchReg=false;
				aluOp="110" ;
				regWrite=Signal.One;
				regData=Signal.X;
				alu1=Signal.Zero;
				alu2=Signal.One;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 9:
			{
				//SAR
				instructionName="SAR";
				isBranchReg=false;
				aluOp="111" ;
				regWrite=Signal.One;
				regData=Signal.X;
				alu1=Signal.Zero;
				alu2=Signal.X;
				memWrite=Signal.Zero;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			case 10:
			{
				//LDR
				instructionName="LDR";
				isBranchReg=false;
				aluOp="X" ;
				regWrite=Signal.One;
				regData=Signal.X;
				alu1=Signal.One;
				alu2=Signal.X;
				memWrite=Signal.Zero;
				memRead=Signal.One;
				memToReg=Signal.One;
				branch=Signal.Zero;
			};break;
			default:
			{
				//STR
				instructionName="STR";
				isBranchReg=false;
				aluOp="X" ;
				regWrite=Signal.Zero;
				regData=Signal.X;
				alu1=Signal.One;
				alu2=Signal.X;
				memWrite=Signal.One;
				memRead=Signal.Zero;
				memToReg=Signal.Zero;
				branch=Signal.Zero;
			};break;
			
			
		 	}
		
	}
	

//------------------------------\\
	//adding certain data to memory
	//helper to withMemory()
		public void addDataToMemory(String s, ArrayList<Byte> memoryData)
		{
			byte b=(byte)Integer.parseInt(s,2);
			memoryData.add(b);
		}
		
	//------------------------------------------------------------------\\
	//First step in the Program Flow to read the instructions as assembly from txt file and
	//then load to instructionMemory
	public static void programLoader(String progName)
	{
		
		
		//Get the instruction to be next executed
		
        // try-catch block to handle exceptions
        try {
  
            // Create a file object
            File f = new File(progName);
  
            // Get the absolute path of file f
            String absolute = f.getAbsolutePath();
           // String filePath=absolute+"src/AssemblyFiles";
            BufferedReader br = new BufferedReader(new FileReader(absolute));
    				
    
            String line;
            while ((line = br.readLine()) != null) 
            {
            	String[]instructionAsText=line.split(" ");
            	//parser return the binary equivalent of the instruction
            	String instructionAsBinary=parser(instructionAsText);
            	//then add instruction to the instruction memory which is one of this class' attributes
            	addInstructionToMemory(instructionAsBinary, instructionMemory);
            	progInstruction++;
            }
            br.close();
            
        }
        catch (Exception e) {
        	System.out.println("exception at progLoader");
            System.err.println(e.getMessage());
        }
	}
	
	//Second step in the Program flow the pc(attribute) already updateded from method programLoader
	public static String fetch()
	{
		//get the instruction from memory as binaryString
		//NOT SURE THE VALUE OF THE PC IS RIGHT OR NOT
		String instruction="";
		
		if(isThereFetch)
		{
			System.out.println("Fetch stage:"+"\n");
			
		 instruction=readFromInstructionMemory(pc);//I1
		 System.out.println("Parameters--> pc: "+pc+"\n");
		
		System.out.println("Instruction Number("+(pc+1)+")"+" Being Fetched"+"\n");
		System.out.println("The Output Of This Stage:"+"\n"+ "The Fetched Instruction: "+instruction+
				"\n"+"Pc For Later Use If Branch Happens: "+(pc)+"\n"+
				"\n"+"****************FETCH DONE****************");
		
		}
//		else
//		{
//			System.out.println("No More Instructions to fetch"+"\n");
//		}
		pc+=1;
		
		//is there anything to decode?
		//if(isThereDecode)
		 if(!instructionToBeDecoded.equals("")||isThereExec==true)
		//Yes:
			//Decode it:
		{
			String s=instructionToBeDecoded;
			instructionToBeDecoded=instruction;
			return s;
		   // decode(instructionToBeDecoded);
		    
		}
		// else if(isThereExec) return "1";
		
		
		//No:
			//add the currently fetched one to be decoded next cycle
		 
		   instructionToBeDecoded=instruction;
		   clock++;
		   return "end";
		   
		
		    
		
		//pc++;
		//incrementPC();//to be implemented need to handle some pipelining limitation
	}
//	
	public static String decode(String instruction)
	{
		String x="";
		if(!instruction.equals(""))
		{
			System.out.println("Decode stage: "+"\n");
			System.out.println("Parameters-->"+"\n" 
			+"The Instruction To Be Decoded: "+instruction+
					"\n"+"Pc Passed From Last Cycle In Case Of Branch:"+(pc-2));
			System.out.println("innstruction number("+(pc-1)+")"+" being decoded"+"\n"+"--------------------");
			
		String opCode=instruction.substring(0,4);//the opcode as binaryString
		int opCodeI=Integer.parseInt(opCode, 2);
		
		String r1Addr=instruction.substring(4, 10);
		String r2Addr=instruction.substring(10, 16);
		
		byte R1=readFromRegisterFiles(r1Addr);//the data of the first register as byte
		byte R2=readFromRegisterFiles(r2Addr);//the data of the second regsiter as byte
		
		String imm=instruction.substring(10,16);//the immediate/address value as binaryString
		String immExt=signExtender(imm, 8);// the immediate value as Extended Signed String
		
		
	//Writing in ID/EX pipeline Register
	 x=r1Addr+r2Addr+immExt+opCode;
	 System.out.println("The Output Of This Stage:"+
	 "\n"+"Data of First Register: "+R1+
	 "\n"+"Data Of Second Register: "+R2+
	 "\n"+"The immediate value Sign Extended: "+immExt+
	 "\n"+"The Opcode: "+ opCodeI+
	 "\n"+"The Pc In Case Of Branching: "+(pc-2));
		}
	 if(isThereExec)
	 {
		 String s=decodedButNot;
			decodedButNot=x;
			return s;
	  
	 }
		
	 
	// 	
	 isThereExec=true;
	 decodedButNot=x;
	   clock++;
	   return "end";
	    
	
	
		
		
	}
	
	//s= 0->6 r1 address, 6-->12 r2 addr, 12-->20 imm ext, opcode 20-->24
	private static void Execute(String s) 
	{
		
		int opCodeI=Integer.parseInt(s.substring(20, 24),2);
		System.out.println("Execute stage:"+"\n");
		
		controlUnit(opCodeI);
		
		String r1Addr=s.substring(0, 6);
		String r2Addr=s.substring(6, 12);		
		String immExt=s.substring(12, 20);
		
		
		// if alu2 is X means its either STR OR LDR
		//So I need to get the address as byte datatype
		byte imm=(byte)Integer.parseInt(immExt,2);
		
		
		byte r1=readFromRegisterFiles(r1Addr);
		//will be used with the BEQZ only need to assign r1 to r2 in order to make the data of r1 minus it self later
		byte r2=(regData.equals(Signal.One))?readFromRegisterFiles(r1Addr):readFromRegisterFiles(r2Addr);
		System.out.println("Parameters:"+"\n"+
		"Pc Passing It In Case Of Branching:"+(pc-3)+"\n"+
		"Data of R1: "+r1+"\n"+
		"Data of R2: "+r2+"\n"+
		"The immediate/address: "+imm+"\n");
		System.out.println("innstruction number("+(pc-2)+")"+" being executed");
		System.out.println("The opcode of the instruction being executed: "+opCodeI+"\n"+"Instruction: "+instructionName);
		
		
		byte op1=(alu1.equals(Signal.Zero))?r1:0;//wil be used with the MOVI only to add 0 to the imm.
		byte op2=(alu2.equals(Signal.Zero))?r2:imm;
		System.out.println("Operand 1 of the alu: "+ op1+"["+fromByteToBinary(op1)+"]");
		System.out.println("Operand 2 of the alu: "+ op2+"["+fromByteToBinary(op2)+"]");
		
		byte ALUres;
		
		if(aluOp.equals("000")) ALUres=addSub(op1, op2);
		else if(aluOp.equals("001")) ALUres=addSub(op1, (byte) (-1*op2));
		else if(aluOp.equals("010")) ALUres=mul(op1, op2);
		else if(aluOp.equals("011")) ALUres=and(op1, op2);
		else if(aluOp.equals("100")) ALUres=eor(op1, op2);
		else if(aluOp.equals("101")) ALUres=conc(op1, op2);
		else if(aluOp.equals("110")) ALUres=shiftL(op1, op2);
		else if(aluOp.equals("111")) ALUres=shiftR(op1, op2);
		else ALUres=addSub(op1, op2); //with memory enter to get the address.
		String[]flags= {"0","0","0","C", "V", "N", "S", "Z"};
		System.out.println("Content of the StatusRegister"+"\n"
		+Arrays.toString(flags)+"\n"
				+Arrays.toString(statusRegister)+"\n");
	
		System.out.println("The Output Of This Stage"+"\n"+
		"The Result Of The ALU: "+ALUres+"["+fromByteToBinary(ALUres)+"]"+
		"\n"+"The Address Of The Write Register: "+r1Addr+
		"\n"+"The Value Of Pc In Case Of Branching: "+((pc-2)+imm)+"\n"+"***********");
		
		
		withMemory(r1Addr, ALUres);

	//	pc=(isBranchReg)?ALUres:pc;
	////	pc+=(branch.equals(Signal.One))?imm-2:0;
		if(isBranchReg==true ||(branch.equals(Signal.One) && ALUres==0))
		{
			byte inToReset=(branch.equals(Signal.One))?imm:ALUres;
			reset(inToReset);
			parentLoop=false;
		}
		
				
//			instructionToBeDecoded="";
//			isThereExec=false;
//			neededClk-=2;
			
		
		
		
		
	}
	
	private static void withMemory(String r1Address, byte aluR)
	{
		byte toReg;
		System.out.println("Memory subStage:"+"\n");
		if(aluOp.equals("X")) {
		//Store from register
		byte r1Data=readFromRegisterFiles(r1Address);
		System.out.println(
				"Parameters: "+"\n"+
				"The Address Of Memory: "+aluR+"\n"+
				"The Address Of R1: To Get What Will Be Wrtitten In Memory In Case Of STR |OR| To Be Passed To WB Stage In Case Of LDR Or R-Instuction: "+r1Address+"\n");
		//int addressInt=Integer.parseInt(addressMem, 2);
		if(memWrite.equals(Signal.One))
		{
			System.out.println("The Old value of the dataMemory at location ("+aluR+") is: "+dataMemory[aluR]);
			dataMemory[aluR]= r1Data;
			System.out.println("The New value of the dataMemory at location ("+aluR+") is: "+dataMemory[aluR]);
		}
		System.out.println("Instruction Number("+(pc-2)+") In This SubStage"+"\n");
		//Load to register
		 toReg=dataMemory[aluR];
		System.out.println("Output Of This subStage:"+"\n"+
		"The ALU Result: "+aluR+"\n"+
		"Data Read From Memory In Case Of LDR: "+dataMemory[aluR]+"\n"+
		"The Address Of WB Register: "+r1Address+"\n"+"********");
		}
		else
		{
			System.out.println("Do Nothing!"+"\n"+"****************");
			toReg=aluR;
		}
		
		
		writeBackToReg(toReg, r1Address);
	}
	private static void writeBackToReg(byte memDataOrAlu, String r1Addr) 
	{
		if(!instructionName.equals("STR") && !instructionName.equals("BEQZ") && !instructionName.equals("BR"))
		{
		System.out.println("Write Back SubStage:"+"\n"+
	"Parameters:"+"\n"+
	"The Data To Be Written In Register: "+memDataOrAlu+"\n"+
	"The Address Of The Register: "+r1Addr+"\n");
		int r1AddrInt=Integer.parseInt(r1Addr, 2);
		//
		System.out.println("Instruction Number("+(pc-2)+") In This SubStage"+"\n");
		if(regWrite.equals(Signal.One))
		{
			System.out.println("The old value of the register("+r1AddrInt+") is: "+registerFile[r1AddrInt]);
			
			
			registerFile[r1AddrInt]=memDataOrAlu;
			System.out.println("The new value of the register("+r1AddrInt+") is: "+registerFile[r1AddrInt]);
			
		}
		}
		else
			System.out.println("Do Nothing In WB");
		
	}
	public static void reset(byte offsetOrAlu)
	{
		virtualClk+=clock;
		clock=1;
		instructionToBeDecoded="";
		isThereFetch=true;
		isThereDecode=false;
		isThereExec=false;
		pc=(branch.equals(Signal.One))?(short) (pc-2+offsetOrAlu):offsetOrAlu;
		int newProgInst=progInstruction-pc;
		programRun(newProgInst);
	}
	public static void programRun(int progInstructions)
	{
		
		
		
		 neededClk=(progInstructions-1)+3;  
		
		while(clock<= neededClk)
		{
			for(int i=3; i<8; i++)
				statusRegister[i]='X';
			
			System.out.println("-------------------------------------------------------------"+"\n"+"Clock cycle: "+(clock+virtualClk)+"\n");
			if (clock==progInstructions+1)
				isThereFetch=false;
			else 
				if(clock==progInstructions+2)
					isThereDecode=false;
			String fetched=fetch();
			if(fetched.equals("end"))
				continue;
			else
			{
		String decoded=decode(fetched);
		if(decoded.equals("end"))
			continue;
		else
			Execute(decoded);
		if(!parentLoop) break;
			}
			clock+=1;
		}
	}

//	
//	
//	//used to assign values for control Signals
	
	
	
	
	
	
	

	



	
	
	



	
	public static void main (String[] args) throws FileNotFoundException, IOException
	{
		//**************To Run The Project**************\\
		init();
		programLoader("src/AssemblyFiles/Program 1.txt");
		programRun(progInstruction);
		
		
//----------------------------------------------\\
		
//		//System.out.println(clock);
//		System.out.println("-----------------------------Finished------------------------------");
//		System.out.println("Content of the instruction Memory"+"\n"+Arrays.toString(instructionMemory));
//		System.out.println("Content of the data Memory"+"\n"+Arrays.toString(dataMemory));
		System.out.println("Content of the Registers"+"\n"+Arrays.toString(registerFile));
		
//	
		
//	---------------------------------------------------\\
		//**TESTING PARSER AND SIGNEXTENDER**\\
//	String instruction[]= {"ADD","R1","-5"};
//	String parsed=parser(instruction);
//	String imm=parsed.substring(10, 16);
//	String parsedExt=signExtender(imm, 8);
//	System.out.println(imm);
//	System.out.println(parsed);
//	System.out.println(parsedExt);
//
		
//		
//		
		
	}
	

}
