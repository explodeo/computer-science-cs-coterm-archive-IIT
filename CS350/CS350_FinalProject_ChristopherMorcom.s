######	CS350 Final Christopher Morcom	######
.data
menu: .asciiz "\n\nMIPS Calculator\n============\n1. Addition\n2. Subtraction\n3. Multiplication\n4. Division\n5. Extra Operations\n\n0. Exit\n\nInput: "
extramenu: .asciiz "\n\nMIPS Extra Features\n================\n1. Decimal to Binary\t2. Decimal to Hexa\n3. Hexa to Binary\t4. Binary to Decimal\n5. To Main Menu\n0. Exit\n\nInput: "
operatorPrompt:	.asciiz "Operator (32-bit signed integer): "
InvalidSelectionError: .asciiz "\nInvalid menu selection. Try again.\n"
InvalidInput: .asciiz "INVALID INPUT. Try again.\n"
Output: .asciiz "\nOutput: "
newline: .asciiz "\n"
OutputBin: .asciiz "\nOutput (32-bit signed binary): "
OutputHex: .asciiz "\nOutput (32-bit signed Hex): "
DecPrompt: .asciiz "\nEnter an integer (signed 32-bit): "
HexPrompt: .asciiz "\nEnter a 32-bit Signed Hex number (ALL CAPS): "
BinPrompt: .asciiz "\nEnter a (32-bit signed) binary value: "
returnprompt: .asciiz "\nPress any key to return to main menu..."
NoInput: .asciiz "\nError. No input. Try again.\n"
AddInst: .asciiz "\n\nInteger Addition: Output = input_1 + input_2.\n"
SubInst: .asciiz "\n\nInteger Subtraction: Output = input_1 - input_2.\n"
MultInst: .asciiz "\n\nInteger Multiplication: Output = input_1 * input_2.\n"
DivInst: .asciiz "\n\nInteger Division: Output = input_1 / input_2.\n"
divbyzero: .asciiz "\nOutput: Error. Cannot divide by zero.\n"
divRemainder: .asciiz " Remainder: "
String: .space 30
.globl main
.text
main: # project is a series of subroutines only to maximize piplining
MainMenu:
	li $v0, 4			
	la $a0, menu	
	syscall			# prompt mainmenu
	li $v0, 12		# Read char from user input
	syscall
	addi $a3, $v0, -48	# Preserve syscall for error jumping
	beq $a3, 0, Exit
	beq $a3, 1, Addition
	beq $a3, 2, Subtraction
	beq $a3, 3, Multiplication
	beq $a3, 4, Division
	beq $a3, 5, ExtraOperations
	j MenuError
ExtraOperations:
	li $v0, 4			
	la $a0, extramenu	
	syscall			# prompt mainmenu
	li $v0, 12		# Read char from user input
	syscall
	addi $a3, $v0, -48	# ascii character minus 48 = digit (0-9)
	beq $a3, 0, Exit
	beq $a3, 1, DecToBin
	beq $a3, 2, DecToHex
	beq $a3, 3, HexToBin
	beq $a3, 4, BinToDec
	beq $a3, 5, MainMenu
	j MenuError
MenuError:
	li $v0, 4			
	la $a0, InvalidSelectionError
	syscall	
	j Return
Exit:
	li $v0, 10
	syscall
Return:
	li $v0, 4
	la $a0, returnprompt
	syscall	
	li $v0, 12
	syscall	
	li $v0, 4
	la $a0, newline
	syscall
	j ClearRegs
ClearRegs:					# error prevention by clearing all registers
	add $t0, $zero, $zero
	add $t1, $zero, $zero
	add $t2, $zero, $zero
	add $t3, $zero, $zero
	add $t4, $zero, $zero
	add $t5, $zero, $zero
	add $t6, $zero, $zero
	add $t7, $zero, $zero
	add $t8, $zero, $zero
	add $t9, $zero, $zero
	add $a0, $zero, $zero
	add $a1, $zero, $zero
	j MainMenu
IntInput:		# ## IF INTEGER INPUT, CHECK IT HERE
	move $t0, $zero	# reset $t0 register to prevent error
	li $v0, 8		# Read input from user
	la $a0, String
	li $a1, 11
	syscall 		# only read up to 10 integers
	addi $t8, $zero, -1	# set charcounter
	addi $t6, $zero, 1	# set default sign bit
	intloop:
		addi $t8, $t8, 1		# set t8 to nth count
		lb $t7, String($t8)		# store nth char into t7
		beq $t7, 45, negInt		# if int is negative
		beq $t7, 10, intCHK		# if newline, string is exhausted
		intcont:
		beqz $t7, Resume
		bge $t7, 48, num
		j ERROR
		num: ble $t7, 57, ProcNum	# check if char is ascii num
		negInt:
			bgt $t8, 0, ERROR 	# error flagged if - sign was input later
			setneg: addi $t6, $zero, -1 # set sign bit to negative
			j intloop
		intCHK:
			beqz $t8, ERROR 	# error flagged if a newline was entered only
			beq $t7, 10, Resume	# if last char was a newline & input valid, resume
			j intcont
		ProcNum:
			addi $t7, $t7, -48	# convert ascii to int
			mulo $t0, $t0, 10 	# shift placevalue
			add $t0, $t0, $t7	# add next number
			# mfhi $t5 
			# bltz $t0, OVERFLOW_ERROR	# check if overflow to sign bit 
			j intloop
	Resume: 
		mulo $t0, $t0, $t6		# set int to either + or - based on $t6 value
		mfhi $t5 
		bgtz $t5, OVERFLOW_ERROR
		jr $ra
ERROR:
	li $v0, 4			
	la $a0, InvalidInput
	syscall	
	jr $t9	# jump back to last operation
OVERFLOW_ERROR:
	li $v0, 4			
	la $a0, Overflow
	syscall	
	j Return # jump back to main if overflow
# ##########################-----GENERAL OPERATIONS BELOW-----########################## #
GetInstrANDInput:
	li $v0, 4			
	move $a0, $t2	
	syscall				# read instructions
		la $ra, Input1Done
		li $v0, 4			
		la $a0, DecPrompt	
		syscall
		j IntInput		# read and check First Input
	Input1Done:
		move $t1, $t0	# move one operator into another register
		la $ra, Input2Done
		li $v0, 4			
		la $a0, DecPrompt
		syscall 
		j IntInput		# read and check Second input
	Input2Done:
		jr $a3
Addition:
	la $t9, Addition	# in case of overflow, jump back here
	la $t2, AddInst
	la $a3, Add			# load user instructions
	j GetInstrANDInput
	Add: 				# output caller
		add $s0, $t0, $t1
		li $v0, 4		
		la $a0, Output	
		syscall
		li $v0, 1
		move $a0, $s0
		syscall
		j Return		
Subtraction:
	la $t9, Subtraction	# in case of overflow, jump back here
	la $t2, SubInst		# load user instructions
	la $a3, Subtract
	j GetInstrANDInput
	Subtract: 			# output caller
		sub $s0, $t1, $t0
		li $v0, 4		
		la $a0, Output	
		syscall
		li $v0, 1
		move $a0, $s0
		syscall
		j Return	
Multiplication:
	la $t9, Multiplication	# in case of overflow, jump back here
	la $t2, MultInst	# load user instructions
	la $a3, Multiply
	j GetInstrANDInput
	Multiply: 			# output caller
		mulo $s0, $t0, $t1
		li $v0, 4		
		la $a0, Output	
		syscall
		li $v0, 1
		move $a0, $s0
		syscall	
		j Return
Division:
	la $t9, Division	# in case of overflow, jump back here
	la $t2, DivInst 	# load user instructions
	la $a3, Divide
	j GetInstrANDInput
	Divide: 			# output caller
		beqz $t0, DivZeroError
		div $t1, $t0
		li $v0, 4		
		la $a0, Output	
		syscall
		li $v0, 1
		mflo $a0
		syscall
		li $v0, 4		
		la $a0, divRemainder	# print result
		syscall
		li $v0, 1
		mfhi $a0
		syscall	
		j Return
DivZeroError:
	li $v0, 4
	la $a0, divbyzero
	syscall	
	jr $t9		# catch divide by zero error here
j Return
# ##########################-----EXTRA OPERATIONS BELOW-----########################## #
DecToBin:
	la $t9, DecToBin
	la $ra DTBCont
	addi $t1, $zero, 31	# set a constant representing 32 bits
	addi $t0, $zero, 0	# clear output var
	li $v0, 4			
	la $a0, DecPrompt	
	syscall			# prompt input
	j IntInput
	DTBCont:
		li $v0, 4		
		la $a0, OutputBin	
		syscall
		beqz $t0, DTBZeroIn
		j TBResult
	DTBZeroIn:		# if input was 0, print 0 and return
		li $v0, 4		
		la $a0, OutputBin
		syscall	
		li $v0, 1		
		li $a0, 0
		syscall	
		j Return
DecToHex:
	la $t9, DecToHex	# store return addresses 
	la $ra, DTHCont
	li $v0, 4			
	la $a0, DecPrompt	
	syscall			# prompt for input
	j IntInput		# read and check input
	DTHCont:		# continue here after checking output
		li $v0, 4			
		la $a0, OutputHex
		syscall			# display "Output:_"
		addi $t4, $zero, 28	# create bit shifter	
	DTHloop:
		blt $t4, $zero, Return	# end printing if bit counter > 32
		add $t3, $t0, $zero	# store input into $t3
		srlv $t3, $t3, $t4	# srl to get next 4 bits of data
		andi $t2, $t3, 15	# remove all but next 4 bits
		bgt $t2, 9, HexChar	# if value is above 9, it is A-F
		j HexNum
	HexChar:
		addi $t2, $t2, 55	# set to ascii alphabet equivalent
		j PrintHex
	HexNum:
		addi $t2, $t2, 48	# set to ascii number equivalent
		j PrintHex
	PrintHex:
		li $v0, 11
		addi $a0, $t2, 0
		syscall
		addi $t4, $t4, -4	# increment bit shifter
		j DTHloop
HexToBin:
	la $t9 HexToBin
	addi $t0, $zero, 0	# clear output var
	li $v0, 4			
	la $a0, HexPrompt	
	syscall			# prompt input
	la $a0, String	# load address of array
	la $a1, 9		# load char count+1 into a1
	li $v0, 8		# Read String from user
	syscall
	li $v0, 4
	la $a0, OutputBin
	syscall			
	addi $t8, $zero, -1	# load charcounter into $t8
	CheckString:
		addi $t8, $t8, 1	# set t8 to nth count
		lb $t7, String($t8)	# store nth char into t7
		beqz $t8, CheckNoHexInput	# called only once to check if no input
		HasHexInput:
		beqz $t7, TBResult
		beq $t7, 10, TBResult	# if null or newline, string is exhausted
		sll $t0, $t0, 4		# shift bits in result to make space for next bits
		bge $t7, 65, ischar
		ble $t7, 57, isnum	# check if possibly ascii num or char
		j ERROR
		CheckNoHexInput:
			beqz $t7, NoHexInput
			beq $t7, 10, NoHexInput	
			j HasHexInput
	isnum:	bge $t7, 48, NumConvert	# check if number
		j ERROR
	ischar:	ble $t7, 70, ABConvert	# check if character
		j ERROR
	NumConvert:
		addi $t7, $t7, -48	# set register to binary of hex value
		j HTBConvert
	ABConvert:
		addi $t7, $t7, -55	# set register to binary of hex value
		j HTBConvert
	HTBConvert:
		add $t0, $t0, $t7	# load bits into register
		j CheckString
	NoHexInput:
		li $v0, 4			
		la $a0, NoInput	
		syscall	
		j HexToBin
	TBResult:
		addi $t1, $zero, 31	# set a bitshifter
		TruncLeadZeros:
			bltz $t1, Return	
			srlv $a0, $t0, $t1		# set arg to 1 or 0
			addi $t1, $t1, -1		# increment bitcounter
			beqz $a0, TruncLeadZeros	# will truncate leading zeros
			andi $a0, $a0, 1		# force upper 31 bits to 0
			li $v0, 1
			syscall
			j TruncLeadZeros
BinToDec:
	la $t9, BinToDec	# set return if error
	addi $t1, $zero, 32	# set a bitshifter
	addi $t0, $zero, 0	# clear output var
	li $v0, 4			
	la $a0, BinPrompt	
	syscall			# prompt input
	la $a0, String	# load address of array
	la $a1, 33		# load char count+1 into a1
	li $v0, 8		# Read String from user
	syscall
	li $v0, 4
	la $a0, Output
	syscall			
	addi $t8, $zero, -1	# load charcounter into $t8
	CheckBinary:
		addi $t8, $t8, 1	# increment charcounter
		lb $t7, String($t8)	# store nth char into t7
		beqz $t7, BTDResult
		beq $t7, 10, BTDResult	# if null or newline, string is exhausted
		sll $t0, $t0, 1		# shift bits in result to make space for next bits
		addi $t7, $t7, -48
		bgt $t7, 1, ERROR
		bltz $t7, ERROR 	# Will output error if 
		add $t0, $t0, $t7	# load bits into register
		j CheckBinary
	BTDResult:
		addi $a0, $t0, 0
		li $v0, 1
		syscall
		j Return
# ####################################################################################### #
							#	EXCEPTION HANDLER BELOW		#
# ####################################################################################### #
.kdata	# kernel data
Overflow: .asciiz "\nOutput: OVERFLOW\n"
.ktext 0x80000180	# Errors or traps are caught at this address
OverflowHandler:
	la $a0, Overflow
	li $v0, 4
	syscall
	la $k0, Return
	# move $14, $k0
	jr $t9 		# go back to last operation