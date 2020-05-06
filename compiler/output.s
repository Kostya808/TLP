.data

str_1:
		.string "%d\n"


.bss


.text

.globl  main
.type  main, @function

main:
		pushl	a_1
		pushl	$str_1
		call	printf
		addl	$8, %esp



