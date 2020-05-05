.data

str_1:
		.string "Hello, world!\n"

.bss

.text

.globl  main
.type  main, @function

main:
		pushl	$str_1
		call	printf
		addl	$4, %esp


