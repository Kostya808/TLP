 #!/bin/bush

TwoArg=2
OneArg=1

if [ $# -ne 2 ]
then
	if [ $# -ne "$OneArg" ]
	then
		# Неверное количество параметров
		echo "Error. Expected './compiler.sh [options] <file.cs>' or './compiler.sh <file.cs>'"
	else
		# Один параметр
		rm -f output output.s
		touch output.s
		
		exec 3>output.s
		echo ".text" >&3
		echo ".globl main" >&3
		echo ".type main, @function" >&3
		echo "main:" >&3
		echo "pushq %rbp" >&3
		echo "movq %rsp, %rbp" >&3
		echo "movl \$0, %eax" >&3
		echo "leave" >&3
		echo "ret" >&3

		java -classpath ./target/classes Compiler $1
		gcc -no-pie -o output output.s
		./output
	fi
else
	# Два параметра
	java -classpath ./target/classes Compiler $1 $2
fi
