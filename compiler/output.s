.data

b_1:
		.int 5

str_1:
		.string "%d\n"


# .bss

# a_1:
# 		.space 4


.text

.globl  main
.type  main, @function

main:
		movl	$1, %eax	# 1 + 2
		addl	$2, %eax
		# movl	%eax, a_1

		pushl	%eax
		pushl	$str_1
		call	printf
		addl	$8, %esp



