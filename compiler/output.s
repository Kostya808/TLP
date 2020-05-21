.data

name:
		.string "a"

name.Length:
		.int 1
str0:
		.string "b"
str1:
		.string "Наибольший общий делитель: "
str2:
		.string "Введите значение переменной "
str3:
		.string "%s"
str4:
		.string ": "
str5:
		.string "%d"
str6:
		.string "%d\n"


.bss

a:
		.space 4

name_variable_initialization_variable:
		.space 100

b:
		.space 4

name_variable_initialization_variable_initialization_variable:
		.space 100

i:
		.space 4

message_print_message_with_numb:
		.space 100

numb_print_message_with_numb:
		.space 4

return_value_initialization_variable:
		.space 4


.text

.globl  main
.type  main, @function

main:
		pushq %rbp
		movq %rsp, %rbp

		# name -> name_variable_initialization_variable
		movl 	$name, %eax
		movl 	%eax, name_variable_initialization_variable
		call 	initialization_variable

		movl 	%edx, a

		movl 	str0, %eax
		movl 	%eax, name
		movl 	$1, name.Length
		# name -> name_variable_initialization_variable_initialization_variable
		movl 	$name, %eax
		movl 	%eax, name_variable_initialization_variable_initialization_variable
		call 	initialization_variable

		movl 	%edx, b

		movl 	a, %eax
		movl 	%eax, i

		jmp 	condition_jump_1
condition_jump_2:
		# if 	# Arithmetic expression == 0
		movl	a, %eax	# a % i
		movl	i, %ecx
		xor 	%edx, %edx
		divl	%ecx
		movl	%edx, %eax
		movl	$0, %ebx
		cmpl	%ebx, %eax
		jne 	condition_jump_3

		# if 	# Arithmetic expression == 0
		movl	b, %eax	# b % i
		movl	i, %ecx
		xor 	%edx, %edx
		divl	%ecx
		movl	%edx, %eax
		movl	$0, %ebx
		cmpl	%ebx, %eax
		jne 	condition_jump_4

		# "Наибольший общий делитель: " -> message_print_message_with_numb
		movl 	$str1, %eax
		movl 	%eax, message_print_message_with_numb
		# i -> numb_print_message_with_numb
		movl 	i, %eax
		movl 	%eax, numb_print_message_with_numb
		call 	print_message_with_numb

		movl 	$0, i

condition_jump_4:

condition_jump_3:

		decl	i
condition_jump_1:
		movl	i, %eax
		movl	$0, %ebx
		cmpl	%ebx, %eax
		jg 	condition_jump_2

		leave
		ret

initialization_variable:
		pushq %rbp
		movq %rsp, %rbp

		mov 	$str2, %rdi	# Console.Write "Введите значение переменной "
		call	printf 

		mov 	$str3, %rdi	# Console.Write name_variable_initialization_variable
		mov 	name_variable_initialization_variable, %rsi
		call	printf

		mov 	$str4, %rdi	# Console.Write ": "
		call	printf 

		mov $str5, %rdi	# scanf
		leaq return_value_initialization_variable, %rsi
		call scanf

		movl 	return_value_initialization_variable, %edx
		leave
		ret

print_message_with_numb:
		pushq %rbp
		movq %rsp, %rbp

		mov 	$str3, %rdi	# Console.Write message_print_message_with_numb
		mov 	message_print_message_with_numb, %rsi
		call	printf

		mov 	$str6, %rdi	# Console.WriteLine numb_print_message_with_numb
		mov 	numb_print_message_with_numb, %rsi
		call	printf

		leave
		ret



