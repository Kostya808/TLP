.data

stroka:
		.string "source string"

stroka.Length:
		.int 13

podstroka:
		.string "str"

podstroka.Length:
		.int 3
str0:
		.string "Строка: "
str1:
		.string "\n"
str2:
		.string "Подстрока: "

count:
		.int 0

j:
		.int 0
str3:
		.string "Является подстрокой!"


.bss

i:
		.space 4


.text

.globl  main
.type  main, @function

main:
		pushq %rbp
		movq %rsp, %rbp

		mov 	$str0, %rdi	# Console.Write "Строка: "
		call	printf 

		mov 	$stroka, %rdi
		call	printf

		mov 	$str1, %rdi
		call	printf

		mov 	$str2, %rdi	# Console.Write "Подстрока: "
		call	printf 

		mov 	$podstroka, %rdi
		call	printf

		mov 	$str1, %rdi
		call	printf

		movl 	$0, i

		jmp 	condition_jump_1
condition_jump_2:
		# if 	# Array element == Array element
		movl 	j, %ecx
		movb	podstroka(,%ecx,1), %ah
		movl 	i, %ecx
		movb	stroka(,%ecx,1), %bh
		cmpb	%bh, %ah
		jne 	condition_jump_3

		incl	j
		incl	count
		jmp 	condition_jump_4
condition_jump_3:

		movl 	$0, j

		movl 	$0, count

condition_jump_4:

		# if 	# count == podstroka.Length
		movl	count, %eax
		movl	podstroka.Length, %ebx
		cmpl	%ebx, %eax
		jne 	condition_jump_5

		mov 	$str3, %rdi	# Console.WriteLine "Является подстрокой!"
		call	printf 

		mov 	$str1, %rdi
		call	printf

		movl 	stroka.Length, %eax
		movl 	%eax, i

condition_jump_5:

		incl	i
condition_jump_1:
		movl	i, %eax
		movl	stroka.Length, %ebx
		cmpl	%ebx, %eax
		jl 	condition_jump_2

		leave
		ret



