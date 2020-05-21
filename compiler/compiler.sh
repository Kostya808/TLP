 #!/bin/bush

TwoArg=2
OneArg=1

if [ $# -ne 2 ]
then
	if [ $# -ne "$OneArg" ]
	then
		# Неверное количество параметров
		echo "Error. Expected 'compiler.sh [options] <file.cs>' or 'compiler.sh <file.cs>'"
	else
		# Один параметр
		java -classpath ./target/classes Compiler $1
		gcc -no-pie -o output output.s
		./output
	fi
else
	# Два параметра
	java -classpath ./target/classes Compiler $1 $2
fi
