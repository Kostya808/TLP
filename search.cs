// using System;
 
// namespace ConsoleApplication1
// {
public class Program
{
    public static void Main(string[] args)
    {
        string stroka = "abcdef";
        string podstroka = "bcd";
        int count = 0;
        int i;
        int j = 0;

        for(i = 0; i < stroka.Length && j < podstroka.Length; i++) {
            if(podstroka[j] == stroka[i]) {
                j++;
                count++;
            }
            else {
                j = 0;
                count = 0;
            }
        }
        if(count == podstroka.Length) {
            Console.Write("Является подстрокой!");
        }

    }
}
// }