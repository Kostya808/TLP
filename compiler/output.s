.data
str0:
		.string "Введите а: "
str1:
		.string "%d"
str2:
		.string "Введите b: "
str3:
		.string "Наибольший общий делитель: "
str4:
		.string "%d\n"


.bss

a:
		.space 4

b:
		.space 4

i:
		.space 4


.text

.globl  main
.type  main, @function

main:
		pushq %rbp
		movq %rsp, %rbp

		mov 	$str0, %rdi	# Console.WriteLine "Введите а: "
		call	printf 

		mov $str1, %rdi	# scanf
		leaq a, %rsi
		call scanf

		mov 	$str2, %rdi	# Console.WriteLine "Введите b: "
		call	printf 

		mov $str1, %rdi	# scanf
		leaq b, %rsi
		call scanf

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

		mov 	$str3, %rdi	# Console.WriteLine "Наибольший общий делитель: "
		call	printf 

		mov 	$str4, %rdi	# Console.WriteLine i
		mov 	i, %rsi
		call	printf

		movl 	$0, i

condition_jump_4:

condition_jump_3:

		decl	i
condition_jump_1:
		movl	i, %eax
		movl	$0, %ebx
		cmpl	%ebx, %eax
		jg 	condition_jump_2

		movl	$0, %eax
		leave
		ret


