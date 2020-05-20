.data

stroka:
		.string "abcdef"

stroka.Length:
		.int 6

podstroka:
		.string "bcd"

podstroka.Length:
		.int 3

count:
		.int 0

j:
		.int 0
str0:
		.string "Является подстрокой!\n"


.bss

i:
		.space 4


.text

.globl  main
.type  main, @function

main:
		pushq %rbp
		movq %rsp, %rbp

		movl 	$0, i

		jmp 	condition_jump_1
condition_jump_2:
		# if 	# Array element == Array element
		movl 	j, %edx
		movb	podstroka(,%edx,1), %ah
		movl 	i, %edx
		movb	stroka(,%edx,1), %bh
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

		mov 	$str0, %rdi	# Console.Write "Является подстрокой!\n"
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

		movl	$0, %eax
		leave
		ret


