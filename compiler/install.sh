make
mydir=$(pwd)

exec 3>compiler.sh
echo " #!/bin/bush

if [ \$# -ne 2 ]
then
	if [ \$# -ne 1 ]
	then
		# Неверное количество параметров
		echo \"Error. Expected 'gcs [options] <file.cs>' or 'gcs <file.cs>'\"
	else
		# Один параметр		
		if [ \$1 == --help ]
		then
			echo \"Запуск: gcs <file.cs> или gcs [options] <file.cs>\"
			echo \"Options:\"
			echo \"'--dump-tokens' — Вывести результат работы лексического анализатора\"
			echo \"'--dump-ast' — Вывести AST дерево\"
			echo \"'--dump-asm' — Вывести сгенерированный код ассемблера\"
		else
			rm -f output output.s
			touch output.s
			
			exec 3>output.s
			echo \".text\" >&3
			echo \".globl main\" >&3
			echo \".type main, @function\" >&3
			echo \"main:\" >&3
			echo \"pushq %rbp\" >&3
			echo \"movq %rsp, %rbp\" >&3
			echo \"leave\" >&3
			echo \"ret\" >&3

			java -classpath $mydir/target/classes Compiler \$1
			gcc -no-pie -o output output.s
			./output
		fi
	fi
else
	# Два параметра
	java -classpath $mydir/target/classes Compiler \$1 \$2
fi" >&3

chmod +x ./compiler.sh

echo ""
echo "Выполните следующую команду:"
echo "alias gcs='$mydir/compiler.sh'"

